package pathDingDing;

import smartMath.Vec2;
import table.Table;
import utils.Log;

import java.util.ArrayList;
import container.Service;

/**
 * Classe de calcul de chemins utilisant l'algorithme A*
 * 
 * Le but de l'algorithme A* est de trouver le chemin le plus court en demarrant d'un
 * node et en parcourant les nodes alentours reliés. Le node suivant est choisi selon 2 critères :
 *  1- Le coût direct, c'est-a-dire le temps que va prendre le robot pour s'y rendre
 *  2- L'heuristique, cela peut prendre plusieures formes, ici c'est la distance à vol d'oiseau entre
 *     le noeud et l'arrivée, donc un noeud qui fait s'eloigner le robot de la destination finale aura
 *     une heuristique plus elevee qu'un noeud plus proche
 *  On choisit simplement le node avec la somme des deux la moins elevee. Ce node sera considere comme FERME
 *  
 *  Avant de choisir le node suivant, on parcourt les nodes adjacents et :
 *   - S'il est pratiquable et non ferme, on l'ajoute a la liste des nodes ouverts
 *   - Si il est deja dans la liste des ouverts, on recalcule son coût direct (l'heuristique ne changeant pas)
 *   - Sinon on l'ignore
 *  Le choix du node suivant (avec les critères enonces precedemment) se fait donc sur la liste ouverte
 *  
 *  La liste ouverte est triee par la somme des critères afin d'eviter un parcours complet de la liste
 *  a chaque etape de l'algo (optimisation du temps de calcul)
 *  
 *  A chaque node ferme, on specifie son parent afin de pouvoir remonter le chemin une fois l'arrivee trouvee
 *  
 *  Le code est commente a chaque etape, mais il est preferable de lire cet article pour une meilleure comprehension :
 *  http://www.gamedev.net/page/resources/_/technical/artificial-intelligence/a-pathfinding-for-beginners-r2003
 *  
 * @author Etienne, julian
 *
 */
public class PathDingDing implements Service
{
	//La table de jeu
	private Table table;
	
	//Le graphe a parcourir
	private Graph graph;
	
	//Noeuds ouverts (a vider a chaque calcul de chemin) 
	private ArrayList<Node> openNodes;
	
	//Noeuds fermés (a vider a chaque calcul de chemin)
	private ArrayList<Node> closedNodes;
	
	//Le log
	private Log log;
	
	public PathDingDing(Table table, Log log)
	{
		//TODO constructeur pathfinding
		this.table = table;
		this.log = log;
		
		this.graph = new Graph();
		
		this.openNodes = new ArrayList<Node>();
		this.closedNodes = new ArrayList<Node>();
	}
	
	
	public ArrayList<Vec2> computePath(Node startNode, Node endNode)
	{
		//TODO pathfinding
		this.initialise();
		// Verifie si le graphe n'a pas ete initialise vide (ALERTE AU GOGOLE!!)
		if(graph.isEmpty())
		{
			log.critical("GRAPHE DE PathDingDing VIDE !!");
		}
		
		//=============================
		// DEBUT DE L'ALGORITHME A*
		//=============================
		
		// D'abord, on ajoute les noeuds adjacents au depart dans la liste ouverte
		ArrayList<Node> related = this.graph.getRelatedNodes(startNode);
		for(int i=0 ; i < related.size() ; i++)
			openNodes.add(related.get(i));
		
		
		
		
		return null;
	}
	
	/**
	 * Vide les listes ouverte et fermee pour lancer un nouveau calcul
	 */
	public void initialise()
	{
		if(!openNodes.isEmpty() || !closedNodes.isEmpty())
		{
			this.openNodes = new ArrayList<Node>();
			this.closedNodes = new ArrayList<Node>();
		}
	}


	@Override
	public void updateConfig() 
	{
		// TODO update the config
	}
}




