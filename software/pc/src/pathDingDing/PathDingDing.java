package pathDingDing;

import smartMath.Vec2;
import table.Table;
import utils.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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
 * @author julian
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
	
	/**
	 * Calcule le chemin à parcourir à l'aide de l'algorithme A*
	 * @param startNode noeud de départ
	 * @param endNode noeud d'arrivée
	 * @return Liste de noeuds à parcourir ; null si échec
	 */
	public ArrayList<Node> computePath(Node startNode, Node endNode)
	{
		//TODO pathfinding
		this.initialise();
		// Verifie si le graphe n'a pas ete initialise vide (ALERTE AU GOGOLE!!)
		if(graph.isEmpty())
		{
			log.critical("GRAPHE DE PathDingDing VIDE !!");
			return new ArrayList<Node>();
		}
		
		if(endNode == startNode)
		{
			log.critical("Appel pathDingDing avec arrivée=départ !");
			return new ArrayList<Node>();
		}
		
		//===========================================
		// DEBUT DE L'ALGORITHME A* - INITIALISATION
		//===========================================
		
		//On ajoute le noeud de départ à la liste fermée
		this.closedNodes.add(startNode);
		
		// D'abord, on ajoute les noeuds adjacents au depart dans la liste ouverte
		ArrayList<Node> related = this.graph.getRelatedNodes(startNode);
		for(int i=0 ; i < related.size() ; i++)
		{
			openNodes.add(related.get(i));
			
			//Cette ligne calcule le coût de déplacement et le set ; l'offset est à 0 car on débute le chemin
			openNodes.get(i).setMovementCost(openNodes.get(i).computeMovementCost(startNode, (double)0));
			
			openNodes.get(i).setParent(startNode);
		}
		
		//On vérifie que l'on est pas dans un cas de bloquage
		if(openNodes.isEmpty())
			return null;
		
		//On classe ces noeuds par coût croissant grâce au service Collections et la méthode compareTo() dont hérite Node
		Collections.sort(openNodes);
		
		//On ajoute le meilleur noeud (en premier dans openNodes) dans la liste fermée
		closedNodes.add(openNodes.get(0));
		
		//====================================================================
		// Boucle principale - Recherche de l'arrivée en parcourant le graphe
		//====================================================================
		
		while(!this.closedNodes.contains(endNode)) //Tant que le noeud de fin n'est pas dans la liste fermée, on continue
		{
			//On enregistre le dernier noeud fermé dans une variable (ça rends le code plus lisible)
			Node lastClosedNode = closedNodes.get(closedNodes.size()-1);
			
			//On prend les noeuds proches du dernier noeud fermé
			related = this.graph.getRelatedNodes(lastClosedNode);
			
			
			//On vérifie si un de ces noeuds n'existe pas déjà dans la liste des noeuds ouverts (pas de doublons)
			// ou s'il est dans la liste des noeuds fermés
			for(int i=0 ; i < related.size() ; i++)
			{
				if(openNodes.contains(related.get(i)))
				{
					Node replicate = openNodes.get(openNodes.indexOf(related.get(i)));
					related.remove(replicate);
					Node newParent = lastClosedNode;
					//Si il existe, on recalcule le coût de déplacement (l'heuristique ne changeant pas
					//s'il est inférieur on change le noeud avec le nouveau coût, sinon on l'ignore
					double newCost = replicate.computeMovementCost(newParent, newParent.getMovementCost());
					if(newCost < replicate.getMovementCost())
					{
						replicate.setMovementCost(newCost);
						
						//Un fois modifié, on le reclasse dans la liste afin de la garder ordonnée
						//Ceci est fait en le supprimant et en l'ajoutant avant le premier noeud
						//ayant un coût plus grand que lui-même (la liste est triée)
						openNodes.remove(replicate);
						int compteur = 0;
						while(replicate.getCost() >  openNodes.get(compteur).getCost())
						{
							compteur++;
						}
						replicate.setParent(newParent);
						openNodes.add(compteur, replicate);
					}
				}
				else if(closedNodes.contains(related.get(i)))
					related.remove(i);
			}
			
			
			//On place les noeuds restants dans la liste des noeuds ouverts, de manière à la garder triée
			for(int i=0 ; i < related.size() ; i++)
			{
				int compteur = 0;
				while(related.get(i).getCost() >  openNodes.get(compteur).getCost())
				{
					compteur++;
				}
				openNodes.add(compteur, related.get(i));
				openNodes.get(compteur).setParent(lastClosedNode);
				openNodes.get(i).setMovementCost(openNodes.get(i).computeMovementCost(lastClosedNode, lastClosedNode.getMovementCost()));
			}
			
			//On ajoute le meilleur noeud dans la liste fermée en le supprimant de openNodes
			closedNodes.add(openNodes.get(0));
			openNodes.remove(0);
			
			//On vérifie que la liste des noeuds ouverts n'est pas vide
			//Si c'est le cas, il n'y a pas de chemin existant, ce noeud est inaccessible
			if(openNodes.isEmpty())
			{
				log.critical("pathDingDing : Le noeud demandé ("+endNode.getPosition().toString()+") est inacessible.");
			}
			
			//ET ON RECOMMENCE !!!
		} 
		
		//==============================================
		// Recomposition du chemin - Arrivée --> Départ
		//==============================================
		
		//result est le chemin final à renvoyer ; on y met l'arrivée
		ArrayList<Node> result = new ArrayList<Node>();
		
		//On remonte le chemin en ajoutant le parent du dernier noeud ajouté à result
		//Il s'arrête quand il rencontre le noeud de départ
		Node currentNode = endNode;
		while(currentNode != startNode)
		{
			result.add(0, currentNode);
			currentNode = currentNode.getParent();
		}
		
		//Petite vérification
		if(result.isEmpty())
		{
			log.critical("erreur : pathDingDing sans résultat");
		}
		
		
		// ET C'EST FUCKING TERMINE !!!!
		return result;
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




