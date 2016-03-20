package tests;

import enums.*;
import exceptions.BlockedActuatorException;
import exceptions.ExecuteException;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.PathNotFoundException;
import exceptions.PointInObstacleException;
import exceptions.serial.SerialConnexionException;
import exceptions.serial.SerialFinallyException;
import hook.Hook;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import robot.Robot;
import scripts.ScriptManager;
import smartMath.Vec2;
import strategie.GameState;
import table.Table;

import java.util.ArrayList;

public class JUnit_ScriptedMatch extends JUnit_Test
{
    private GameState<Robot> theRobot;
    private ScriptManager scriptManager;
    private ArrayList<Hook> emptyHook = new ArrayList<Hook>();

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() throws Exception
    {
        super.setUp();
        scriptManager = (ScriptManager)container.getService(ServiceNames.SCRIPT_MANAGER);
        theRobot = (GameState<Robot>)container.getService(ServiceNames.GAME_STATE);
        initialize();

        // Lance le thread graphique
        container.getService(ServiceNames.THREAD_INTERFACE);
        //container.getService(ServiceNames.THREAD_EYES);
        container.startInstanciedThreads();
    }

    private void initialize() throws Exception
    {
        theRobot.robot.setOrientation(Math.PI);
        theRobot.robot.setPosition(Table.entryPosition);
        theRobot.robot.setLocomotionSpeed(Speed.MEDIUM_ALL);
        theRobot.robot.moveLengthwise(200, emptyHook, false);

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
        }catch (SerialConnexionException e)
        {
            e.printStackTrace();
        }

    }


    @After
    public void aftermath() throws Exception
    {
        //on remonte les bras
        theRobot.robot.useActuator(ActuatorOrder.ARM_INIT,true);
        try
        {
            returnToEntryPosition(theRobot);
        }
        catch (UnableToMoveException | PathNotFoundException | PointInObstacleException e)
        {
            e.printStackTrace();
        }
    }

    @Test
    public void match() throws Exception
    {
        try
        {
            Vec2 sup = scriptManager.getScript(ScriptNames.TECH_THE_SAND).entryPosition(1, theRobot.robot.getRobotRadius(), theRobot.robot.getPosition()).position;
            theRobot.table.getObstacleManager().freePoint(sup);
            scriptManager.getScript(ScriptNames.TECH_THE_SAND).goToThenExec(2,theRobot, emptyHook);
        }
        catch(ExecuteException | SerialFinallyException e)
        {
            e.printStackTrace();
        }
        try
        {
            scriptManager.getScript(ScriptNames.CASTLE).execute(2, theRobot, emptyHook);
        }
        catch(ExecuteException|SerialFinallyException e)
        {
            e.printStackTrace();
        }
        try
        {
            theRobot.robot.useActuator(ActuatorOrder.CLOSE_DOOR, false);
            Vec2 sup = scriptManager.getScript(ScriptNames.CLOSE_DOORS).entryPosition(0, theRobot.robot.getRobotRadius(), theRobot.robot.getPosition()).position;
            theRobot.table.getObstacleManager().freePoint(sup);
            scriptManager.getScript(ScriptNames.CLOSE_DOORS).goToThenExec(0,theRobot, emptyHook);
        }
        catch(ExecuteException | SerialFinallyException e)
        {
            e.printStackTrace();
        }
        try
        {
            Vec2 sup = scriptManager.getScript(ScriptNames.SHELL_GETTER).entryPosition(-1, theRobot.robot.getRobotRadius(), theRobot.robot.getPosition()).position;
            theRobot.table.getObstacleManager().freePoint(sup);
            scriptManager.getScript(ScriptNames.SHELL_GETTER).goToThenExec(-1,theRobot, emptyHook);
        }
        catch(ExecuteException | SerialFinallyException e)
        {
            e.printStackTrace();
        }
        try
        {
            Vec2 sup = scriptManager.getScript(ScriptNames.FISHING).entryPosition(0, theRobot.robot.getRobotRadius(), theRobot.robot.getPosition()).position;
            theRobot.table.getObstacleManager().freePoint(sup);
            scriptManager.getScript(ScriptNames.FISHING).goToThenExec(0, theRobot, emptyHook);
        }
        catch(ExecuteException | SerialFinallyException e)
        {
            e.printStackTrace();
        }
        try
        {
            scriptManager.getScript(ScriptNames.SHELL_GETTER).goToThenExec(1,theRobot, emptyHook);
        }
        catch(ExecuteException | SerialFinallyException e)
        {
            e.printStackTrace();
        }
        try
        {
            scriptManager.getScript(ScriptNames.SHELL_GETTER).goToThenExec(2,theRobot, emptyHook);
        }
        catch(ExecuteException | SerialFinallyException e)
        {
            e.printStackTrace();
        }
        try
        {
            scriptManager.getScript(ScriptNames.SHELL_GETTER).goToThenExec(3,theRobot, emptyHook);
        }
        catch(ExecuteException | SerialFinallyException e)
        {
            e.printStackTrace();
        }
        try
        {
            scriptManager.getScript(ScriptNames.SHELL_GETTER).goToThenExec(4,theRobot, emptyHook);
        }
        catch(ExecuteException | SerialFinallyException e)
        {
            e.printStackTrace();
        }
        try
        {
            scriptManager.getScript(ScriptNames.SHELL_DEPOSIT).goToThenExec(0,theRobot, emptyHook);
        }
        catch(ExecuteException | SerialFinallyException e)
        {
            e.printStackTrace();
        }
    }
}