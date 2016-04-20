package tests;

import enums.*;
import exceptions.BlockedActuatorException;
import exceptions.serial.SerialConnexionException;
import hook.Hook;
import org.junit.Before;
import org.junit.Test;
import robot.Robot;
import robot.cardsWrappers.SensorsCardWrapper;
import scripts.ScriptManager;
import smartMath.Vec2;
import strategie.GameState;
import table.Table;
import threads.ThreadTimer;
import utils.Sleep;

import java.util.ArrayList;

/**
 * Code d'homologation
 * @author discord
 */
public class JUnit_Homologation extends JUnit_Test
{
    private GameState<Robot> theRobot;
    private ScriptManager scriptManager;
    private SensorsCardWrapper mSensorsCardWrapper;
    private ArrayList<Hook> emptyHook = new ArrayList<Hook>();

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() throws Exception
    {
        super.setUp();
        scriptManager = (ScriptManager)container.getService(ServiceNames.SCRIPT_MANAGER);
        theRobot = (GameState<Robot>)container.getService(ServiceNames.GAME_STATE);
        mSensorsCardWrapper = (SensorsCardWrapper) container.getService(ServiceNames.SENSORS_CARD_WRAPPER);
        initialize();

        // Lance le thread graphique
        container.getService(ServiceNames.THREAD_TIMER);
        container.getService(ServiceNames.THREAD_SENSOR);
        //container.getService(ServiceNames.THREAD_INTERFACE);
        container.startInstanciedThreads();

        waitMatchBegin();
    }

    private void initialize() throws Exception
    {
        theRobot.robot.setOrientation(Math.PI);
        theRobot.robot.setPosition(Table.entryPosition);
        theRobot.robot.setLocomotionSpeed(Speed.SLOW_ALL);
        //theRobot.robot.moveLengthwise(200, emptyHook, false);

        theRobot.robot.useActuator(ActuatorOrder.ARM_INIT, true);

        try
        {
            if(!theRobot.robot.getContactSensorValue(ContactSensors.DOOR_CLOSED))
            {
                theRobot.robot.useActuator(ActuatorOrder.CLOSE_DOOR, true);
            }
            if(!theRobot.robot.getContactSensorValue(ContactSensors.DOOR_CLOSED))
            {
                theRobot.robot.useActuator(ActuatorOrder.STOP_DOOR, true);
                throw new BlockedActuatorException("Porte droite bloquée !");
            }

            // petit temps d'attente pour éviter de faire planter les portes #LeHautNiveauDemandeDeLaMerde
            theRobot.robot.sleep(100);

        }
        catch (SerialConnexionException e)
        {
            e.printStackTrace();
        }

    }

    @Test
    public void launch()
    {
        try
        {
            theRobot.robot.moveLengthwise(200);
            //scriptManager.getScript(ScriptNames.CASTLE).goToThenExec(0, theRobot, emptyHook);
        }
        catch(Exception e)
        {
            log.debug("Problème d'exécution dans Castle");
            e.printStackTrace();
        }
        try {
            Vec2 sup = scriptManager.getScript(ScriptNames.CLOSE_DOORS).entryPosition(3, theRobot.robot.getRobotRadius(), theRobot.robot.getPosition()).position;
            theRobot.table.getObstacleManager().freePoint(sup);
            scriptManager.getScript(ScriptNames.CLOSE_DOORS).goToThenExec(3, theRobot, emptyHook);
        } catch (Exception e) {
            log.debug("Problème d'exécution dans Close Doors");
            e.printStackTrace();
        }
        try {
            theRobot.robot.setTurningStrategy(TurningStrategy.FASTEST);
            theRobot.robot.useActuator(ActuatorOrder.CLOSE_DOOR, false);
            Vec2 sup = scriptManager.getScript(ScriptNames.FISHING).entryPosition(3, theRobot.robot.getRobotRadius(), theRobot.robot.getPosition()).position;
            theRobot.table.getObstacleManager().freePoint(sup);
            scriptManager.getScript(ScriptNames.FISHING).goToThenExec(3, theRobot, emptyHook);
        } catch (Exception e) {
            log.debug("Problème d'exécution dans Fishing");

        }
    }

    /**
     * Attends que le match soit lancé
     * cette fonction prend fin quand le match a démarré
     */
    private void waitMatchBegin()
    {

        System.out.println("Robot pret pour le match, attente du retrait du jumper");

        // attends que le jumper soit retiré du robot
        boolean jumperWasAbsent = mSensorsCardWrapper.isJumperAbsent();
        while(jumperWasAbsent || !mSensorsCardWrapper.isJumperAbsent())
            Sleep.sleep(100);

        // maintenant que le jumper est retiré, le match a commencé
        ThreadTimer.matchStarted = true;
    }
}
