package robot.cardsWrappers;

import robot.serial.SerialConnexion;
import utils.Log;
import utils.Config;
import container.Service;
import enums.ActuatorOrder;
import exceptions.serial.SerialConnexionException;


/**
 * Classe . Utilisée par robot pour bouger les actionneurs.
 * @author pf
 */
@SuppressWarnings("unused")
public class ActuatorCardWrapper implements Service
{

	
	// pour écrire dans le log en cas de problème
	private Log log;
	
	// pour parler aux cartes du robot
	private SerialConnexion serie;

	public ActuatorCardWrapper(Config config, Log log, SerialConnexion serie)
	{
		this.log = log;
		this.serie = serie;
		
	}

	public void updateConfig()
	{
	}
	
	/**
	 * Envoie un ordre à la série. Le protocole est défini dans l'enum ActuatorOrder
	 * @param order
	 * @throws SerialConnexionException
	 */
	public void useActuator(ActuatorOrder order) throws SerialConnexionException
	{
		serie.communiquer(order.getSerialOrder(), 0);
	}

}
