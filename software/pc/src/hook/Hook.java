package hook;

import strategie.GameState;
import utils.Config;
import utils.Log;

import java.util.ArrayList;

/**
 * Classe-mère abstraite des hooks, utilisés pour la programmation évènementielle
 * @author pf
 *
 */

abstract public class Hook
{

	protected ArrayList<Callback> callbacks = new ArrayList<Callback>();

	/** Système de log sur lequel écrire */
	protected Log log;
	
	/** endroit ou lire la configuration du robot */
	@SuppressWarnings("unused")
	private Config config;
	
	/** Etat du jeu sur lequel on vérifie si le hook se déclenche ou non */
	protected GameState mState;

	/**
	 *  ce constructeur ne sera appellé que par les constructeurs des classes filles (des hooks bien précis)  
	 * @param config endroit où lire la configuration du robot 
	 * @param log Système de log sur lequel écrire
	 * @param gameState Etat du jeu sur lequel on vérifie si le hook se déclenche ou non
	 */
	public Hook(Config config, Log log, GameState gameState)
	{
		this.config = config;
		this.log = log;
		this.mState = gameState;
	}
	
	/**
	 * On peut ajouter un callback à un hook.
	 * Il n'y a pas de méthode pour en retirer, car il n'y en a a priori pas besoin
	 * @param callback
	 */
	public void addCallback(Callback callback)
	{
		callbacks.add(callback);
	}
	
	/**
	 * Déclenche le hook.
	 * Tous ses callbacks sont exécutés
	 * @return true si ce hook modifie les déplacements du robot
	 */
	protected boolean trigger()
	{
		boolean retour = false;
		
		for(Callback callback : callbacks)
			retour |= callback.call();
		return retour;
	}

	/**
	 * Méthode qui sera surchargée par les classes filles.
	 * Elle contient la condition d'appel du hook
	 * @return true si ce hook modifie les déplacements du robot, false sinon
	 */
	public abstract boolean evaluate();
	
	/**
	 * On peut supprimer le hook s'il n'y a plus aucun callback déclenchable.
	 * @return vrai si le hook est supprimable
	 */
	public boolean canBeDeleted()
	{
	    for(Callback c: callbacks)
	        if(!c.shouldBeDeleted())
	            return false;
	    return true;
	}

}

