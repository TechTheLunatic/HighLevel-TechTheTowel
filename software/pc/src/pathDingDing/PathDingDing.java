package pathDingDing;

import smartMath.Point;
import table.Table;
import smartMath.Path;
import smartMath.Vec2;

import java.util.ArrayList;

import exceptions.Locomotion.BlockedException;

/**
 * Classe encapsulant les calculs de pathfinding 
 * @author Marsya et etienne
 *
 */
public class PathDingDing
{
	private Table mTable;
	
	// TODO: documenter a quoi sert cette variable
	int compteur;
	
	public PathDingDing(Table mTable)
	{
		this.mTable = mTable;
		compteur = 0;
	}
	
	/**
	 * 
	 * @param segment1A point A du premier segment
	 * @param segment1B point B du premier segment
	 * @param segment2A point A de second segment
	 * @param segment2B point B du second segment
	 * @return vrai si intersection, faux sinon
	 */
	public static boolean intersects(Point segment1A, Point segment1B, Point segment2A, Point segment2B)
	{
		// TODO: expliciter en comentaire les formules utilisés. 
		return (segment1B.x - segment1A.x) * (segment2B.y - segment2A.y) - (segment1B.y - segment1A.y) * (segment2B.x - segment2A.x) != 0
			&& ((segment1B.x - segment1A.x) * (segment2B.y - segment1A.y) - (segment1B.y - segment1A.y) * (segment2B.x - segment1A.x)) * ((segment1B.x - segment1A.x) * (segment2A.y - segment1A.y) - (segment1B.y - segment1A.y) * (segment2A.x - segment1A.x)) <= 0
			&& ((segment2B.x - segment2A.x) * (segment1B.y - segment2A.y) - (segment2B.y - segment2A.y) * (segment1B.x - segment2A.x)) * ((segment2B.x - segment2A.x) * (segment1A.y - segment2A.y) - (segment2B.y - segment2A.y) * (segment1A.x - segment2A.x)) <= 0
			;
	}
	
	/**
	 * 
	 * @param segment1A point A du segment 1
	 * @param segment1B point B du segment 1
	 * @param segment2A point A du segment 2
	 * @param segment2B point B du segment 2
	 * @return le point d'intersection des droites port�es par les segments 1 et 2
	 */
	public static Point intersection(Point segment1A, Point segment1B, Point segment2A, Point segment2B)
	{
		// TODO: commenter pour expliquer a quoi sert ces variables, et les renommer pour refléter cette utilité
		double inter, k;
		// TODO: expliciter en comentaire les formules utilisés. 
		
		if((segment2B.y - segment2A.y) != 0)
		{
			inter = (segment2B.x - segment2A.x) / (segment2B.y - segment2A.y);
			k = (segment1A.x - segment2A.x + inter * (segment2A.y - segment1A.y)) / (segment1B.x - segment1A.x - inter * (segment1B.y - segment1A.y));
		}
		else
			k = -(segment2A.y - segment1A.y) / (segment1B.y - segment1A.y);
		
		return new Point(segment1A.x - k * (segment1B.x - segment1A.x), segment1A.y - k * (segment1B.y - segment1A.y));
	}
	
	//TODO: expliquer quand BlockedException est lancé, etce que cela veut dire pour l'utilisateur de cette classe.
	/**
	 * 
	 * @param start point de d�part
	 * @param end point d'arriv�e
	 * @return un chemin entre le point de d�part et d'arriv�e
	 * @throws BlockedException 
	 */
	public ArrayList<Vec2> computePath(Vec2 start, Vec2 end) throws BlockedException
	{
		compteur = 0;
		Point DoubleStart = new Point(start.x, start.y), DoubleEnd = new Point(end.x, end.y);
		Path path = new Path();
		path.add(DoubleStart);
		path.addAll(dodgeStatic(DoubleStart, DoubleEnd));
		path.add(DoubleEnd);
		simplify(path);
		return path.toVec2Array();
	}

	//TODO: expliquer quand BlockedException est lancé, etce que cela veut dire pour l'utilisateur de cette classe.
	// D'ailleurs, est-ce réellement une BlockedException (bloquage mécanique du robot en cours contre un obstacle qui l'empèche avancer plus)
	// ne pas hésiter a créer de nouveaux types d'exceptions
	/**
	 * 
	 * @param start point de d�part
	 * @param end point d'arriv�e
	 * @return un chemin entre le point de d�part et d'arriv�e en evitant uniquement les obstacles fixes
	 * @throws BlockedException 
	 */
	private Path dodgeStatic(Point start, Point end) throws BlockedException
	{
		if(compteur >= 1000)
			throw new BlockedException();
		
		Path path = new Path();
		
		// cherche le point d'intersection avec les obstacles le plus proche de point de d�part
		double min = 13000000;
		int indiceDistMin = 0;
		Point node = new Point();
		boolean intersects = false;
		for(int ind_ligne = 0 ; ind_ligne < mTable.getLines().size() ; ind_ligne++)
    	{
	    	if( intersects(start, end, mTable.getLines().get(ind_ligne).getA(), mTable.getLines().get(ind_ligne).getB()))
	    	{
	    		node = intersection(start, end, mTable.getLines().get(ind_ligne).getA(), mTable.getLines().get(ind_ligne).getB());
	    		intersects = true;
	    		double dist = Math.pow(node.x - start.x, 2) + Math.pow(node.y - start.y, 2);
		    	if (dist <= min)
		    	{
		    		min = dist;
		    		indiceDistMin = ind_ligne;
		    	}
		    }
    	}
		//si il y a un point d'intersection, ajoute ce point au chemin, et recommence la recherche de chemin sur les chemins d�but -> point de passage du point d'intersection
		//                                                                                                                   point de passage du point d'intersection -> fin
		if(intersects)
		{
			//s'il n'y a qu'un seul point de passage sur l'obstacle
			if( mTable.getLines().get(indiceDistMin).getNbPassagePoint() == 1 )
			{
				compteur++;
				path.addAll(dodgeStatic(start, mTable.getLines().get(indiceDistMin).getPassagePoint1()));
				path.add(mTable.getLines().get(indiceDistMin).getPassagePoint1());
				path.addAll(dodgeStatic(mTable.getLines().get(indiceDistMin).getPassagePoint1(), end));
			}
			//s'il y a deux points de passage sur l'obstacle, prend le point de passage le plus proche du point d'arriv�e.
			else
			{
				if(Math.pow(mTable.getLines().get(indiceDistMin).getPassagePoint1().x - end.x, 2) + Math.pow(mTable.getLines().get(indiceDistMin).getPassagePoint1().y - end.y, 2) <= Math.pow(mTable.getLines().get(indiceDistMin).getPassagePoint2().x - end.x, 2) + Math.pow(mTable.getLines().get(indiceDistMin).getPassagePoint2().y - end.y, 2))
				{
					compteur++;
					path.addAll(dodgeStatic(start, mTable.getLines().get(indiceDistMin).getPassagePoint1()));
					path.add(mTable.getLines().get(indiceDistMin).getPassagePoint1());
					path.addAll(dodgeStatic(mTable.getLines().get(indiceDistMin).getPassagePoint1(), end));
				}
				else
				{
					compteur++;
					path.addAll(dodgeStatic(start, mTable.getLines().get(indiceDistMin).getPassagePoint2()));
					path.add(mTable.getLines().get(indiceDistMin).getPassagePoint2());
					path.addAll(dodgeStatic(mTable.getLines().get(indiceDistMin).getPassagePoint2(), end));
				}
			}
		}
		return path;
	}
	
	/**
	 * 
	 * @param path chemin � simplifier
	 * @return un chemin simplifi�
	 */
	public Path simplify(Path path)
	{
		
		// TODO: expliciter en commentaire l'algo
		
		for(int i = 0; i < path.size() - 2; i++)
		{
			boolean removable = true;
			for(int ind_ligne = 0 ; ind_ligne < mTable.getLines().size() ; ind_ligne++)
	    	{
		    	if( intersects(path.getPosition(i), path.getPosition(i+2), mTable.getLines().get(ind_ligne).getA(), mTable.getLines().get(ind_ligne).getB()));
		    	{
		    		removable = false;
		    	}
	    	}
			if(removable)
			{
				path.remove(i+1);
				i--;
			}
		}
		return path;
	}
}