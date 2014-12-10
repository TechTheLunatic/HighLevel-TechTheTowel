package robot.cardsWrappers;

import robot.serial.SerialConnexion;
import utils.Log;
import utils.Config;
import container.Service;
import enums.ActuatorOrder;
import exceptions.serial.SerialConnexionException;


/**
 * Surcouche user-friendly pour parler a la carte actionneurs.
 * Utilisée par RobotReal pour bouger les actionneurs.
 * @author pf, marsu
 */
public class ActuatorCardWrapper implements Service
{
	/** service de log a utiliser en cas de soucis */
	private Log log;
	
	/** connexion série avec la carte actionneurs */
	private SerialConnexion actuatorCardSerial;

	/**
	 * Construit la surchouche de la carte actionneurs
	 * @param config le fichoer ou lire la configuration du robot
	 * @param log le système de log ou écrire  
	 * @param serial la connexion série avec la carte actionneurs
	 */
	public ActuatorCardWrapper(Config config, Log log, SerialConnexion serial)
	{
		this.log = log;
		this.actuatorCardSerial = serial;
		
	}

	public void updateConfig()
	{
	}
	
	/**
	 * Envoie un ordre à la série. Le protocole est défini dans l'enum ActuatorOrder
	 * @param order l'ordre a envoyer
	 * @throws SerialConnexionException en cas de problème de communication avec la carte actionneurs
	 */
	public void useActuator(ActuatorOrder order) throws SerialConnexionException
	{
		log.debug("Envoi consigne a la carte actionneur : " + order.toString(), this);
		actuatorCardSerial.communiquer(order.getSerialOrder(), 0);
	}

	

	
}