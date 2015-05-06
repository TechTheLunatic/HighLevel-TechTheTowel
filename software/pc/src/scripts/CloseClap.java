package scripts;

import java.util.ArrayList;
import java.util.Arrays;

import enums.ActuatorOrder;
import enums.Speed;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.serial.SerialConnexionException;
import exceptions.serial.SerialFinallyException;
import hook.Callback;
import hook.Hook;
import hook.methods.OpenClapLeftHighExe;
import hook.methods.OpenClapLeftMiddleExe;
import hook.methods.OpenClapRightHighExe;
import hook.methods.OpenClapRightMiddleExe;
import hook.types.HookFactory;
import robot.Robot;
import smartMath.Circle;
import smartMath.Vec2;
import strategie.GameState;
import utils.Config;
import utils.Log;

/**
 * 
 * @author theo
 * Exemple sur Paul
 * Script Fermer les claps
 *
 *Table :
 *
 *    ___________________
 *   |                  |
 * 	 |					|
 *   |					|Debut du robot ici
 *   |					|
 *   |		N° claps	|
 *    /_3_/________2_/_1     
 *    
 *    1,2,3 nous appartiennent
 *    
 *   
 */

public class CloseClap extends AbstractScript 
{
	private static final int AverageTimeToPlaceGlass = 10000;

	private static final int AverageTimeToPlacePlot = 5000;

	//Distance à avancer après le clap 2 pour esquiver l'estrade rouge en (0,0)
	private int distanceToDodgeEstrade = 250;
	
	//Distance à avancer après le clap 2 pour aller au clap 3
	private int distanceBetween2and3 = 1700;
	
	
	/**
	 * Constructeur (normalement appelé uniquement par le scriptManager) du script fermant les Claps
	 * Le container se charge de renseigner la hookFactory, le système de config et de log.
	 * @param hookFactory La factory a utiliser pour générer les hooks dont pourra avoir besoin le script
	 * @param config le fichier de config a partir duquel le script pourra se configurer
	 * @param log le système de log qu'utilisera le script
	 * TODO: seul closeAllClaps fonctionne, il faut modifier les autres
	 */
	
	public CloseClap(HookFactory hookFactory, Config config, Log log)
	{
		super(hookFactory, config, log);
		versions = new Integer[]{1, 2, 3 ,12 ,123 , -1, -12}; // liste des versions
	}
	
	@Override
	public void execute(int versionToExecute, GameState<Robot> stateToConsider, ArrayList<Hook> hooksToConsider) throws UnableToMoveException, SerialConnexionException, SerialFinallyException
	{		
		try
		{
			if (versionToExecute == 123)
				closeAllOurClaps(stateToConsider, hooksToConsider);
			else if (versionToExecute == 1)
				closeFirstClap(stateToConsider, hooksToConsider);
			else if (versionToExecute == 2)
				closeSecondClap(stateToConsider, hooksToConsider);
			else if (versionToExecute == 3)
				closeThirdClap(stateToConsider, hooksToConsider);
			else if (versionToExecute == 12)
				closeFirstAndSecondClapBackwardWithHooks(stateToConsider, hooksToConsider);
			else if (versionToExecute == -1)
				closeFirstClapBackward(stateToConsider, hooksToConsider);
			else if (versionToExecute == -12)
				closeFirstAndSecondClapBackward(stateToConsider, hooksToConsider);
			else
				log.debug("Souci de version", this);	//TODO: lancer une exception de version inconnue (la créer si besoin)
		}
		catch (UnableToMoveException | SerialConnexionException e)
		{
			finalize(stateToConsider);
			throw e ;
		}
	}
	
	public void closeFirstClap (GameState<Robot> stateToConsider,  ArrayList<Hook> hooksToConsider) throws UnableToMoveException, SerialConnexionException
	{
		//pour ne pas frotter l'ascenceur
		stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_LOW, true);

		//on commence en (1290,231), on se tourne dans le bon sens
		stateToConsider.robot.turn(Math.PI, hooksToConsider, false);
				
		//on recule pour se mettre en (1360,231)
		stateToConsider.robot.moveLengthwise(-120, hooksToConsider, true);//-100
			
		//On ouvre le bras puis on avance pour se retrouver en (1010,231)
		stateToConsider.robot.useActuator(ActuatorOrder.MID_LEFT_CLAP, true);
		stateToConsider.robot.moveLengthwise(250, hooksToConsider, false);
		stateToConsider.table.clapXClosed(1);
	
		stateToConsider.robot.useActuator(ActuatorOrder.HIGH_LEFT_CLAP, true);
		//On s'echape
		stateToConsider.robot.turn(Math.PI/2, hooksToConsider, false);
		
		//On ferme tout pour finir
		stateToConsider.robot.useActuator(ActuatorOrder.LOW_RIGHT_CLAP, false);
		stateToConsider.robot.useActuator(ActuatorOrder.LOW_LEFT_CLAP, false);
	}

	public void closeSecondClap (GameState<Robot> stateToConsider,  ArrayList<Hook> hooksToConsider) throws UnableToMoveException, SerialConnexionException
	{
		//pour ne pas frotter l'ascenceur
		stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_LOW, true);
				
		//on commence en (700,231), on se tourne dans le bon sens
		stateToConsider.robot.turn(Math.PI, hooksToConsider, false);
		
		//On ouvre le bras puis on avance de 300mm pour se retrouver en (400,231)
		stateToConsider.robot.useActuator(ActuatorOrder.MID_LEFT_CLAP, true);
		
		//On s'echape
		stateToConsider.robot.turn(Math.PI/2, hooksToConsider, false);
		stateToConsider.table.clapXClosed(2);	

						
		//On ferme tout pour finir
		stateToConsider.robot.useActuator(ActuatorOrder.LOW_RIGHT_CLAP, false);
		stateToConsider.robot.useActuator(ActuatorOrder.LOW_LEFT_CLAP, false);	
	}
	
	public void closeThirdClap (GameState<Robot> stateToConsider,  ArrayList<Hook> hooksToConsider) throws UnableToMoveException, SerialConnexionException  //Ferme le claps de fin
	{
		
		//pour ne pas frotter l'ascenceur
		stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_LOW, true);
		
		
		//(-900,500)->(-1050,230), mis en place pour contrer le PathNotFound
		stateToConsider.robot.turn(Math.PI, hooksToConsider, false);
		stateToConsider.robot.moveLengthwise(150, hooksToConsider, false);
		stateToConsider.robot.turn(-Math.PI/2, hooksToConsider, false);
		stateToConsider.robot.moveLengthwise(500-230, hooksToConsider, false);

		
		stateToConsider.robot.turn(0, hooksToConsider, false);
		//on ouvre notre bras puis on avance de 200mm pour se retrouver en 
		stateToConsider.robot.useActuator(ActuatorOrder.MID_RIGHT_CLAP, true);		
		stateToConsider.robot.moveLengthwise(200, hooksToConsider, false);//(-850,231) 
		stateToConsider.table.clapXClosed(3);
		
		stateToConsider.robot.useActuator(ActuatorOrder.HIGH_RIGHT_CLAP, true);
		
		//On s'echape
		stateToConsider.robot.turn(Math.PI/2, hooksToConsider, false);
				
		//On ferme tout pour finir
		stateToConsider.robot.useActuator(ActuatorOrder.LOW_RIGHT_CLAP, false);
		stateToConsider.robot.useActuator(ActuatorOrder.LOW_LEFT_CLAP, false);
	}
	
	public void closeFirstAndSecondClapBackwardWithHooks (GameState<Robot> stateToConsider,  ArrayList<Hook> hooksToConsider) throws UnableToMoveException, SerialConnexionException
	{
		
		//on met le robot en vitesse lente
		stateToConsider.robot.setLocomotionSpeed(Speed.SLOW);
		
		//pour ne pas frotter l'ascenceur
		stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_LOW, true);
		
		//on commence en (1295,230), on se tourne dans le bon sens
		//stateToConsider.robot.moveLengthwise(80, hooksToConsider, false);
		
		stateToConsider.robot.useActuator(ActuatorOrder.MID_RIGHT_CLAP, true);
		
		stateToConsider.robot.turn(0, hooksToConsider, false);
		
		//ajout de hooks
		Hook hook1 = hookFactory.newHookXisLesser(1250, 10);
		Hook hook2 = hookFactory.newHookXisLesser(1000, 10);
		
		// ajoute un callback au hook de position qui ouvre / ferme le bras
		hook1.addCallback(	new Callback(new OpenClapRightHighExe(),true, stateToConsider)	);
		hook2.addCallback(	new Callback(new OpenClapRightMiddleExe(),true, stateToConsider)	);
		
		// ajoute le hook a la liste a passer a la locomotion
		hooksToConsider.add(hook1);
		hooksToConsider.add(hook2);
		
		//on met le robot en vitesse moyenne
		stateToConsider.robot.setLocomotionSpeed(Speed.BETWEEN_SCRIPTS_SLOW);
		
		stateToConsider.robot.moveLengthwise(-400, hooksToConsider, Speed.BETWEEN_SCRIPTS_SLOW);
		stateToConsider.table.clapXClosed(1);
		stateToConsider.table.clapXClosed(2);
		
		//on met le robot en vitesse lente
		stateToConsider.robot.setLocomotionSpeed(Speed.SLOW);
				
		stateToConsider.robot.turn(-Math.PI * 0.5, hooksToConsider, false);
		
		//On ferme tout pour finir
		stateToConsider.robot.useActuator(ActuatorOrder.LOW_RIGHT_CLAP, false);
		stateToConsider.robot.useActuator(ActuatorOrder.LOW_LEFT_CLAP, false);
		
		stateToConsider.robot.moveLengthwise(-80, hooksToConsider, false);
	}
	
	public void closeAllOurClaps(GameState<Robot> stateToConsider,  ArrayList<Hook> hooksToConsider) throws UnableToMoveException, SerialConnexionException  //Ferme tous les Claps, depuis le  debut
	{
		//pour ne pas frotter l'ascenceur
		stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_LOW, true);
				
		//on commence en (1290,231), on se tourne dans le bon sens
		stateToConsider.robot.turn(Math.PI, hooksToConsider, false);
		
		//on recule pour se mettre en (1360,231)
		stateToConsider.robot.moveLengthwise(-120, hooksToConsider, true);//-100
	
		//On ouvre le bras puis on avance de 250mm pour se retrouver en (1010,231)
		stateToConsider.robot.useActuator(ActuatorOrder.MID_LEFT_CLAP, true);

		stateToConsider.robot.moveLengthwise(250, hooksToConsider, false);
		stateToConsider.table.clapXClosed(1);
	
		//On monte notre bras pour passer au dessus du clap ennemi notre bras et on avance de 250mm pour se retrouver en (660,231)
		stateToConsider.robot.useActuator(ActuatorOrder.HIGH_LEFT_CLAP, true);

		stateToConsider.robot.moveLengthwise(250, hooksToConsider, false);

		//On ouvre le bras puis on avance de 220mm pour se retrouver en (400,231)
		stateToConsider.robot.useActuator(ActuatorOrder.MID_LEFT_CLAP, true);

		stateToConsider.robot.moveLengthwise(220, hooksToConsider, false);
		stateToConsider.table.clapXClosed(2);	

		//on baisse notre bras
		stateToConsider.robot.turn(0.5*Math.PI, hooksToConsider, false);
		
		stateToConsider.robot.useActuator(ActuatorOrder.LOW_LEFT_CLAP, true);
	
		//on vas au 3eme clap donc en (-1340,231)
		stateToConsider.robot.moveLengthwise(distanceToDodgeEstrade, hooksToConsider, false);
		stateToConsider.robot.turn(Math.PI, hooksToConsider, false);
		stateToConsider.robot.moveLengthwise(distanceBetween2and3, hooksToConsider, false);
		stateToConsider.robot.turn(-0.5*Math.PI, hooksToConsider, false);
		stateToConsider.robot.moveLengthwise(distanceToDodgeEstrade, hooksToConsider, false);
		
		//on est en (-1340,231), on se retourne dans le bon sens
		stateToConsider.robot.turn(0, hooksToConsider, false);
		
		//on ouvre notre bras puis on avance de 200mm pour se retrouver en (-1140,231) 
		stateToConsider.robot.useActuator(ActuatorOrder.MID_RIGHT_CLAP, true);
		stateToConsider.robot.moveLengthwise(200, hooksToConsider, false);
		stateToConsider.table.clapXClosed(3);
		
		stateToConsider.robot.useActuator(ActuatorOrder.HIGH_RIGHT_CLAP, true);
		//On s'echape
		stateToConsider.robot.turn(Math.PI/2, hooksToConsider, false);
		
		//On ferme tout pour finir
		stateToConsider.robot.useActuator(ActuatorOrder.LOW_RIGHT_CLAP, false);
		stateToConsider.robot.useActuator(ActuatorOrder.LOW_LEFT_CLAP, false);
	}
	
	
	void closeFirstClapBackward(GameState<Robot> stateToConsider,  ArrayList<Hook> hooksToConsider) throws UnableToMoveException, SerialConnexionException
	{
		//pour ne pas frotter l'ascenceur
		stateToConsider.robot.useActuator(ActuatorOrder.ELEVATOR_LOW, true);

		//on commence en (1290,231), on se tourne dans le sens inverse au clap
		stateToConsider.robot.turn(0, hooksToConsider, false);
				
		//on recule pour se mettre en (1360,231)
		stateToConsider.robot.moveLengthwise(120, hooksToConsider, true);//-100
			
		//On ouvre le bras puis on recule pour se retrouver en (1010,231)
		stateToConsider.robot.useActuator(ActuatorOrder.MID_RIGHT_CLAP, true);
		
		stateToConsider.robot.moveLengthwise(-250, hooksToConsider, false);
		stateToConsider.table.clapXClosed(1);
	
		stateToConsider.robot.useActuator(ActuatorOrder.HIGH_RIGHT_CLAP, true);

		//On s'echape dans le sens normal
		stateToConsider.robot.turn(Math.PI/2, hooksToConsider, false);
		
		//On ferme tout pour finir
		stateToConsider.robot.useActuator(ActuatorOrder.LOW_RIGHT_CLAP, false);
		stateToConsider.robot.useActuator(ActuatorOrder.LOW_LEFT_CLAP, false);
	}

	void closeFirstAndSecondClapBackward(GameState<Robot> stateToConsider,  ArrayList<Hook> hooksToConsider) throws UnableToMoveException, SerialConnexionException
	{
		try 
		{
			stateToConsider.robot.turn (Math.PI*0.25);
			
			stateToConsider.robot.useActuator(ActuatorOrder.MID_RIGHT_CLAP, true);
			
			stateToConsider.robot.turn (0);
			stateToConsider.robot.useActuator(ActuatorOrder.HIGH_RIGHT_CLAP, true);
			
			stateToConsider.robot.moveLengthwise(-400);
			stateToConsider.robot.useActuator(ActuatorOrder.MID_RIGHT_CLAP, true);
			
			stateToConsider.robot.turn(Math.PI*-0.5);
			stateToConsider.robot.useActuator(ActuatorOrder.LOW_RIGHT_CLAP, true);
			
			stateToConsider.robot.turn (Math.PI);
			
			//On ferme tout pour finir
			stateToConsider.robot.useActuator(ActuatorOrder.LOW_RIGHT_CLAP, false);
			stateToConsider.robot.useActuator(ActuatorOrder.LOW_LEFT_CLAP, false);
		}
		catch (UnableToMoveException e1) 
		{
			e1.printStackTrace();
		} 
	}
	
	
	@Override
	public Circle entryPosition(int version, int ray, Vec2 robotPosition)
	{		
		if (version == 1)
			return new Circle(1290,230); //point d'entrée : bord de la table, robot devant le clap 1
		else if(version == 2)
			return new Circle(760,260); //point d'entrée : devant le clap 2
		else if(version == 3)
			return new Circle(-900,500);//point d'entrée : devant le clap 3
		else if(version == 12)
			return new Circle(1280,230); //point d'entrée : devant le clap 1
		else if(version == 123)
			return new Circle(1290,230); //point d'entrée : devant le clap 1
		else if(version == -1)
			return new Circle(1240,230); //point d'entrée : devant le clap 1 //TODO point d'entrée à changer
		else if(version == -12)
			return new Circle(1220,230); //point d'entrée : devant le clap 1 //TODO point d'entrée à changer
		else
		{
			log.debug("Probleme d'entrée de position", this);
			return null;
		}
	}

	@Override
	public int remainingScoreOfVersion(int version, GameState<?> stateToConsider)
	{
		//On met à jour le nombre de points restants pour la version : à  0, on a tout fait
		int score = 15;
		if(stateToConsider.table.isClapXClosed(1) || version==2 || version==3)
			score -= 5;
		if(stateToConsider.table.isClapXClosed(2) || version == 1 || version == 3 || version == -1 )
			score -= 5;
		if(stateToConsider.table.isClapXClosed(3) || version == 1 || version == 2 || version == 12 || version == -1 || version == -12)
			score -= 5;
		if(!stateToConsider.table.isGlassXTaken(0) && (version ==1 || version == 12 || version == -1 || version == -12))
			score -=Math.min((int)(90000-stateToConsider.timeEllapsed)/AverageTimeToPlaceGlass, 4);
		if(!stateToConsider.table.isPlotXEaten(3) && (version ==1 || version == 12 || version == -1 || version == -12))
			score -= Math.min((int)(90000-stateToConsider.timeEllapsed)/AverageTimeToPlacePlot, 5);
		if(!stateToConsider.table.isPlotXEaten(4) && (version ==1 || version == 12 || version == -1 || version == -12))
			score -=Math.min((int)(90000-stateToConsider.timeEllapsed)/AverageTimeToPlacePlot,5);
		return score;
	}

	@Override
	public void finalize(GameState<?> stateToConsider) throws SerialFinallyException
	{	
		try 
		{
			//On ferme le robot à la fin, attention à ne rien cogner ! (rembarde , etc)
			stateToConsider.robot.useActuator(ActuatorOrder.LOW_RIGHT_CLAP, false);
			stateToConsider.robot.useActuator(ActuatorOrder.LOW_LEFT_CLAP, false);
			
			// TODO: voir si on ne peut pas mettre ici une protection anti-cognage en demandant au service de gestion d'obstacle s'il y a un obstacle sur les cotés du robot. (Attention, les bras doivent toujours essayer de se fermer, mais c'est juste pour voir si on ne peut pas le faire 3cm plus loin)
		} 
		catch (SerialConnexionException e) 
		{
			log.debug("Erreur termine : ne peux pas replier claps", this); // Viens me parler de cette ligne. Je dois t'expliquer ce que veut dire "remonter une exception"
			throw new SerialFinallyException ();
		}
	}


	public Integer[] getVersion(GameState<?> stateToConsider)
	{
		ArrayList<Integer> versionList = new ArrayList<Integer>(Arrays.asList(versions));
		
		if (stateToConsider.table.isClapXClosed(1))
		{
			versionList.remove((Integer)1);
			versionList.remove((Integer)12);
			versionList.remove((Integer)123);
			versionList.remove((Integer)(-12));
			versionList.remove((Integer)(-1));
		}
		if (stateToConsider.table.isClapXClosed(2))
		{
			versionList.remove((Integer)2);
			versionList.remove((Integer)12);
			versionList.remove((Integer)(-12));
			versionList.remove((Integer)123);
		}
		if (stateToConsider.table.isClapXClosed(3))
		{
			versionList.remove((Integer)3);
			versionList.remove((Integer)123);
		}
		
		
		//on convertit l'arrayList en Integer[]	
				Integer[] retour = new Integer[versionList.size()];
			    for (int i=0; i < retour.length; i++)
			    {
			    	retour[i] = versionList.get(i).intValue();
			    }
				return retour;
	}
}



