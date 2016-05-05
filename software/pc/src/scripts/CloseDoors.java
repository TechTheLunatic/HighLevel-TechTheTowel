package scripts;

import enums.ActuatorOrder;
import exceptions.BadVersionException;
import exceptions.ExecuteException;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.serial.SerialFinallyException;
import hook.Callback;
import hook.Hook;
import hook.methods.OpenDoor;
import hook.methods.SpeedDown;
import hook.methods.StopDetect;
import hook.types.HookFactory;
import robot.Robot;
import smartMath.Arc;
import smartMath.Circle;
import smartMath.Vec2;
import strategie.GameState;
import table.Table;
import utils.Config;
import utils.Log;

import java.util.ArrayList;

import enums.Speed;


/**
 * Script pour la fermeture des portes des cabines
 * Version 0 : Deplacement de la serviette aux portes puis fermeture en même temps ; aucune action prevue hors du deplacement ; aucun pathdingding/evitement ; si pb -> arret complet
 * Version 1 : Identique à la version 0, sauf qu'on ferme les portes en marche avant
 * Version 2 : Intégration des trajectoires courbes
 * Version 3 : version 0 avec appel PDD
 * @author Discord, CF
 */
public class CloseDoors extends AbstractScript
{
	public CloseDoors(HookFactory hookFactory, Config config, Log log) {
		super(hookFactory, config, log);
		/**
		 * Versions du script
		 */
		versions = new Integer[]{0,1,2,3,4};
		
	}
	
	/**
	 * On lance le script choisi.
	 * @param versionToExecute Version a lancer
	 * @param stateToConsider Notre bon vieux robot
	 * @param hooksToConsider Les hooks necessaires pour l'execution du script
	 */
	@Override
	public void execute(int versionToExecute, GameState<Robot> stateToConsider,ArrayList<Hook> hooksToConsider) throws SerialFinallyException, ExecuteException, UnableToMoveException
	{
		//Les parametres de cette version ont ete determines experimentalement, fonctionnel sur robot 2015
		
		try
		{
		
			if(versionToExecute == 0 || versionToExecute == 3)
			{

				//On ralentit pour éviter de démonter les éléments de jeu "Discord-style"
				Speed speedBeforeScriptWasCalled = stateToConsider.robot.getLocomotionSpeed();

                stateToConsider.robot.setBasicDetection(true);
				stateToConsider.robot.setForceMovement(false);
                stateToConsider.robot.setLocomotionSpeed(Speed.FAST_ALL);

                //On s'oriente vers les portes
				stateToConsider.robot.turn(-(Math.PI / 2), hooksToConsider, false);

                stateToConsider.robot.setBasicDetection(true);

                Hook hook = hookFactory.newYGreaterHook(1700);
                hook.addCallback(new Callback(new SpeedDown(), true, stateToConsider));
                hooksToConsider.add(hook);

                hook = hookFactory.newYGreaterHook(1600);
                hook.addCallback(new Callback(new StopDetect(), true, stateToConsider));
                hooksToConsider.add(hook);

                stateToConsider.robot.setForceMovement(true);

                //On ferme les portes
				stateToConsider.robot.moveLengthwise(-1000, hooksToConsider, true);

                stateToConsider.robot.setBasicDetection(true);

               // stateToConsider.robot.setForceMovement(false);


                //PORTES FERMEES !
				stateToConsider.obtainedPoints += 20;
				stateToConsider.table.extDoorClosed = true;
				stateToConsider.table.intDoorClosed = true;

				//if(Geometry.isBetween(stateToConsider.robot.getPosition().y, 1790, 1890))
					stateToConsider.robot.setPosition(new Vec2(stateToConsider.robot.getPosition().x,1840));
					stateToConsider.robot.setOrientation(-Math.PI/2);

				//else
				//	log.debug("Position trop éloignée pour se recaler en y (cylindre ?)");

                stateToConsider.robot.setLocomotionSpeed(Speed.MEDIUM_ALL);

                stateToConsider.robot.setBasicDetection(true);
				//On avance
				stateToConsider.robot.moveLengthwiseWithoutDetection(300, hooksToConsider, false);

                stateToConsider.robot.setForceMovement(false);

                stateToConsider.robot.turn(Math.PI);
                stateToConsider.robot.setForceMovement(true);


                hook = hookFactory.newXGreaterHook(1200);
                hook.addCallback(new Callback(new SpeedDown(), true, stateToConsider));
                hooksToConsider.add(hook);

				stateToConsider.robot.moveLengthwise(-500, hooksToConsider, true);

				stateToConsider.robot.setForceMovement(false);

				//  if(Geometry.isBetween(stateToConsider.robot.getPosition().x, 1300, 1400))
			//	{
				    stateToConsider.robot.setPosition(new Vec2(1350, stateToConsider.robot.getPosition().y));
					stateToConsider.robot.setOrientation(Math.PI);
			//	}
            //    else
            //        log.debug("Position trop éloignée pour se recaler en x (cylindre ?)");

				stateToConsider.robot.moveLengthwise(300, hooksToConsider, false);


				stateToConsider.robot.setBasicDetection(false);



                stateToConsider.robot.setLocomotionSpeed(speedBeforeScriptWasCalled);

			}

			else if (versionToExecute == 1)
			{


				//On ralentit pour éviter de démonter les éléments de jeu "Discord-style"
				//Speed speedBeforeScriptWasCalled = stateToConsider.robot.getLocomotionSpeed();
				//stateToConsider.robot.setLocomotionSpeed(Speed.SLOW);

				//On s'oriente vers les portes
				stateToConsider.robot.turn((Math.PI / 2), hooksToConsider, false);

				//On ferme les portes
				stateToConsider.robot.moveLengthwise(600, hooksToConsider, true);

				//PORTES FERMEES !
				stateToConsider.obtainedPoints += 20;
				stateToConsider.table.extDoorClosed = true;
				stateToConsider.table.intDoorClosed = true;

				//On recule
				stateToConsider.robot.moveLengthwise(-200, hooksToConsider, false);


				//stateToConsider.robot.setLocomotionSpeed(speedBeforeScriptWasCalled);

			}
			else if (versionToExecute == 2)
			{


				//On ralentit pour éviter de démonter les éléments de jeu "Discord-style"
				//Speed speedBeforeScriptWasCalled = stateToConsider.robot.getLocomotionSpeed();
				//stateToConsider.robot.setLocomotionSpeed(Speed.SLOW);

				// On suit une trajectoire courbe pour fermer les deux portes
				stateToConsider.robot.moveArc(new Arc(Table.entryPosition, new Vec2(1100,1900-stateToConsider.robot.getRobotRadius()),Math.PI,true), hooksToConsider);

				//PORTES FERMEES !
				stateToConsider.obtainedPoints += 20;
				stateToConsider.table.extDoorClosed = true;
				stateToConsider.table.intDoorClosed = true;

			}
            else if(versionToExecute == 4)
            {
                //On ralentit pour éviter de démonter les éléments de jeu "Discord-style"
                Speed speedBeforeScriptWasCalled = stateToConsider.robot.getLocomotionSpeed();

                stateToConsider.robot.setBasicDetection(true);
                stateToConsider.robot.setForceMovement(false);
                stateToConsider.robot.setLocomotionSpeed(Speed.FAST_ALL);

                stateToConsider.robot.useActuator(ActuatorOrder.OPEN_DOOR, false);

                //On s'oriente vers les portes
                stateToConsider.robot.turn(-(Math.PI / 2), hooksToConsider, false);

                stateToConsider.robot.setBasicDetection(true);

                Hook hook = hookFactory.newYGreaterHook(1700);
                hook.addCallback(new Callback(new SpeedDown(), true, stateToConsider));
                hooksToConsider.add(hook);

                hook = hookFactory.newYGreaterHook(1600);
                hook.addCallback(new Callback(new StopDetect(), true, stateToConsider));
                hooksToConsider.add(hook);

                hook = hookFactory.newYGreaterHook(1500);
                hook.addCallback(new Callback(new OpenDoor(), true, stateToConsider));
                hooksToConsider.add(hook);

                stateToConsider.robot.setForceMovement(true);

                //On ferme les portes
                stateToConsider.robot.moveLengthwise(-900, hooksToConsider, true);

                stateToConsider.robot.setBasicDetection(true);

                // stateToConsider.robot.setForceMovement(false);


                //PORTES FERMEES !
                stateToConsider.obtainedPoints += 20;
                stateToConsider.table.extDoorClosed = true;
                stateToConsider.table.intDoorClosed = true;

                //if(Geometry.isBetween(stateToConsider.robot.getPosition().y, 1790, 1890))
                stateToConsider.robot.setPosition(new Vec2(stateToConsider.robot.getPosition().x,1840));
                stateToConsider.robot.setOrientation(-Math.PI/2);

                //else
                //	log.debug("Position trop éloignée pour se recaler en y (cylindre ?)");

                stateToConsider.robot.setLocomotionSpeed(Speed.MEDIUM_ALL);

                stateToConsider.robot.setBasicDetection(true);
                //On avance
                stateToConsider.robot.moveLengthwiseWithoutDetection(300, hooksToConsider, false);

                stateToConsider.robot.setForceMovement(false);
            }
		}
		catch(Exception e)
		{
			finalize(stateToConsider,e);
		}
	}

	@Override
	public int remainingScoreOfVersion(int version, GameState<?> state) 
	{
		
		// Score maximal possible de 20 points
		int score = 20;
		
		// Score de 10 points fournis par porte fermée
		if (version == 0 || version == 1)
		{
			if (state.table.extDoorClosed)
			{
				score-=10;
			}
			if (state.table.intDoorClosed)
			{
				score-=10;
			}
		}
		return score;
	}

	@Override
	public Circle entryPosition(int version, int ray, Vec2 robotPosition) throws BadVersionException
	{
		if (version == 0 || version == 1 || version == 4)
		{
			// modification possible selon l'envergure du robot new Vec2(1135,1600)
			return new Circle(robotPosition);
		}
		else if (version == 2)
		{
			return new Circle(Table.entryPosition);
		}
		else if(version == 3)
		{
			return new Circle(new Vec2(1050,1750));
		}
		else
		{
			log.debug("erreur : mauvaise version de script");
			throw new BadVersionException();
		}
	}

	@Override
	public void finalize(GameState<?> state, Exception e) throws SerialFinallyException
	{
		log.debug("Exception " + e + "dans Close Doors : Lancement du Finalize !");
        state.robot.setBasicDetection(false);
    }

	@Override
	public Integer[] getVersion(GameState<?> stateToConsider) 
	{
		return versions;
	}
	
}