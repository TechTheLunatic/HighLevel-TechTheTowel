package scripts;

import java.util.ArrayList;

import robot.Robot;
import smartMath.Circle;
import smartMath.Vec2;
import strategie.GameState;
import table.Table;
import enums.Speed;
import exceptions.ExecuteException;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.serial.SerialFinallyException;
import hook.Hook;
import hook.types.HookFactory;
import utils.Config;
import utils.Log;


/**
 * @author julian
 * Script pour la fermeture des portes des cabines
 * Version 0 : Deplacement de la serviette aux portes puis fermeture en même tamps ; aucune action prevue hors du deplacement ; aucun pathdingding/evitement ; si pb -> arret complet
 */
public class CloseDoors extends AbstractScript
{
	/**
	 * Definit si la porte exterieure est fermee
	 */
	private Boolean extDoorClosed = false;
	/**
	 * Definit si la porte interieure est fermee
	 */
	private Boolean intDoorClosed = false;

	public CloseDoors(HookFactory hookFactory, Config config, Log log) {
		super(hookFactory, config, log);
		/**
		 * Versions du script
		 */
		versions = new Integer[]{0};
		
	}
	
	/**
	 * On lance le script choisi.
	 * @param versionToExecute Version a lancer
	 * @param stateToConsider Notre bon vieux robot
	 * @param hooksToConsider Les hooks necessaires pour l'execution du script
	 */
	@Override
	public void execute(int versionToExecute, GameState<Robot> stateToConsider,ArrayList<Hook> hooksToConsider) throws SerialFinallyException, ExecuteException
	{
		//Les parametres de cette version ont ete determines experimentalement, fonctionnel sur robot 2015
		if(versionToExecute == 0)
		{
			try
			{
				//On ralentit pour eviter de demonter les elements de jeu "Discord-style"
				Speed speedBeforeScriptWasCalled = stateToConsider.robot.getLocomotionSpeed();
				stateToConsider.robot.setLocomotionSpeed(Speed.SLOW);
			
				//On tourne le robot vers la position
				stateToConsider.robot.turn((Math.PI*0.5 + 0.986), hooksToConsider, false);
			
				//On deplace le robot vers les portes
				stateToConsider.robot.moveLengthwise(380, hooksToConsider, false);
				
				//On s'oriente vers les portes
				stateToConsider.robot.turn(-(Math.PI / 2), hooksToConsider, false);
				
				//On ferme les portes
				stateToConsider.robot.moveLengthwise(-600, hooksToConsider, true);
			
				//On recule
				stateToConsider.robot.moveLengthwise(200, hooksToConsider, false);
				
				//PORTES FERMEES !
				stateToConsider.robot.setLocomotionSpeed(speedBeforeScriptWasCalled);
				
				extDoorClosed = true;
				intDoorClosed = true;
			}
			catch(UnableToMoveException e)
			{
				finalize(stateToConsider);
				throw new ExecuteException(e);
			}
			//TODO else
			
		}
	}

	@Override
	public int remainingScoreOfVersion(int version, GameState<?> state) 
	{
		return 0;
	}

	@Override
	public Circle entryPosition(int version, int ray, Vec2 robotPosition)
	{
		if (version == 0)
			return new Circle(Table.entryPosition);
		else
		{
			log.debug("erreur : mauvaise version de script");
			return new Circle(Table.entryPosition);
		}
	}

	@Override
	public void finalize(GameState<?> state) throws SerialFinallyException
	{
		
		if(this.extDoorClosed)
			state.obtainedPoints += 20;
		if(this.intDoorClosed)
			state.obtainedPoints += 20;
		state.table.extDoorClosed = this.extDoorClosed;
		state.table.intDoorClosed = this.intDoorClosed;
	}

	@Override
	public Integer[] getVersion(GameState<?> stateToConsider) 
	{
		return versions;
	}
	
}