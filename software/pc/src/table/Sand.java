package table;

import enums.Elements;
import smartMath.Vec2;

/**
 * Classe definissant les cubes/cylindre/cones de sable.
 * @author julian
 */
public class Sand extends GameElement
{
	/** Type d'element de sable, enumeres dans enums/Elements.java */
	private Elements type;
	
	public int sandSize = 58;
	
	/** Niveau de l'element (z si vous preferez)
	 *  0 = sur la table
	 *  1 = sur un element
	 *  2 = sur deux elements
	 *  n = sur n elements
	 */
	private int level;
	
	/**
	 * Construit l'element de sable
	 * @param type son type (cube, cylindre ou cone)
	 * @param position sa position (x,y)
	 * @param level niveau de l'element (voir def)
	 */
	public Sand(Elements type, Vec2 position, int level)
	{
		super(position);
		this.level = level;
		this.type = type;
	}
	
	public float getX()
	{
		return position.x;
	}
	
	public float getY()
	{
		return position.y;
	}
	
	public float getZ()
	{
		return (((level*2)+1)*this.sandSize)/2;
	}
	
	public Elements getType()
	{
		return this.type; 
	}
	
}
