package scripts;


import enums.ActuatorOrder;
import enums.ContactSensors;
import enums.DirectionStrategy;
import enums.TurningStrategy;
import exceptions.BlockedActuatorException;
import exceptions.ExecuteException;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.serial.SerialConnexionException;
import exceptions.serial.SerialFinallyException;
import hook.Hook;
import hook.types.HookFactory;
import robot.Robot;
import smartMath.Circle;
import smartMath.Vec2;
import strategie.GameState;
import utils.Config;
import utils.Log;

import java.util.ArrayList;

/**
 * Script de rapatriement du sable ; utilise le PDD
 * Version 0 et 1: se déplace de la montagne à la zone de construction, choisit une stratégie d'orientation lors du chemin déterminé par le PDD pour ne pas perdre le sable
 * @author CF, discord
 */
public class DropTheSand extends AbstractScript
{
    public DropTheSand(HookFactory hf, Config config, Log log)
    {
        super(hf, config, log);
        versions = new Integer[]{0};

    }

    /**
     * Execute le script de rapatriement du sable; nécessite un chemin calculé par le PDD
     * @param versionToExecute version à executer
     * @param actualState le GameState
     * @param hooksToConsider les hooks
     * @throws SerialConnexionException
     */
    @Override
    public void execute(int versionToExecute, GameState<Robot> actualState, ArrayList<Hook> hooksToConsider) throws SerialFinallyException, ExecuteException
    {

        if(versionToExecute == 0)
        {
        	try
        	{
        		// On autorise la marche arrière au robot
        		actualState.robot.setDirectionStrategy(DirectionStrategy.FASTEST);
        		
        		// On recule pour 'déposer' le sable
        		// TODO la distance est arbitraire, à modifier avec les phases de test
        		actualState.robot.moveLengthwise(-200, hooksToConsider, false);
        		
                actualState.robot.useActuator(ActuatorOrder.CLOSE_DOOR, true);
                
                // on vérifie si la porte n'est pas bloquée lors de sa fermeture
                if(!actualState.robot.getContactSensorValue(ContactSensors.DOOR_CLOSED))
                {
                    actualState.robot.useActuator(ActuatorOrder.STOP_DOOR, false);
                    throw new BlockedActuatorException("Porte bloquée !");
                }

                actualState.robot.setRobotRadius(TechTheSand.retractedRobotRadius);

                //On indique au robot qu'il ne transporte plus de sable
        		actualState.robot.setIsSandInside(false);
        		
        		// On reprend une stratégie de mouvement optimale
        		actualState.robot.setTurningStrategy(TurningStrategy.FASTEST);
        	}
        	
        	catch(Exception e)
            {
				finalize(actualState);
				throw new ExecuteException(e);
			}
        }

    }

    @Override
    public int remainingScoreOfVersion(int version, GameState<?> state) 
    {
        return 0;
    }

   /* Renvoie la position d'entree du script suivant la version en argument*/
    @Override
    public Circle entryPosition(int version, int ray, Vec2 robotPosition)
    {
        if(version == 0)
        {
            return new Circle(1000, 400, 0);
        }
        else
        {
			//TODO jetter une exception
			log.debug("erreur : mauvaise version de script");
			return new Circle(0,0,0);
		}
    }

    @Override
    public void finalize(GameState<?> state) throws SerialFinallyException 
    {
    	// on tente de fermer la vitre avec changement de rayon
    	try
    	{
    		state.robot.useActuator(ActuatorOrder.CLOSE_DOOR, true);
    		if (state.robot.getIsSandInside() == true)
    		{
    			state.robot.setRobotRadius(TechTheSand.middleRobotRadius);
    		}
    		else
    		{
    			state.robot.setRobotRadius(TechTheSand.retractedRobotRadius);
    		}
    	}
    	catch (Exception e)
    	{
    		log.debug("DropTheSand : Impossible de ranger la vitre !");
    		throw new SerialFinallyException();
    	}
    }

    @Override
    public Integer[] getVersion(GameState<?> stateToConsider) {
        return versions;
    }
}