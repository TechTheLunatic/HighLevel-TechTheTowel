package scripts;


import enums.ActuatorOrder;
import enums.DirectionStrategy;
import enums.Speed;
import enums.TurningStrategy;
import exceptions.ExecuteException;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.BadVersionException;
import exceptions.serial.SerialConnexionException;
import exceptions.serial.SerialFinallyException;
import hook.Hook;
import hook.types.HookFactory;
import robot.Robot;
import smartMath.Circle;
import smartMath.Vec2;
import strategie.GameState;
import table.Shell;
import table.Table;
import table.obstacles.Obstacle;
import table.obstacles.ObstacleCircular;
import table.obstacles.ObstacleRectangular;
import utils.Config;
import utils.Log;

import java.util.ArrayList;

/**
 * Script de récupération unitaire des coquillages
 * La première version est spéciale, elle permet d'en récupérer deux d'un coup
 * @author discord
 */
public class ShellGetter extends AbstractScript
{
    public ShellGetter(HookFactory hookFactory, Config config, Log log)
    {
        super (hookFactory,config,log);
        /**
         * Versions du script
         */
        versions = new Integer[]{0,1,2,3,4};
    }

    @Override
    public void execute(int versionToExecute, GameState<Robot> stateToConsider, ArrayList<Hook> hooksToConsider) throws SerialFinallyException, ExecuteException {

        if(versionToExecute == 0) //Récupération des deux proches du tapis (jamais ennemis)
        {
            Speed speedBeforeScriptWasCalled = stateToConsider.robot.getLocomotionSpeed();
            stateToConsider.robot.setLocomotionSpeed(Speed.SLOW_ALL); // TODO A changer quand asserv OK

            try {
                stateToConsider.robot.moveLengthwise(100);

                stateToConsider.robot.turn(Math.PI/2);

                stateToConsider.robot.shellsOnBoard=true;

                stateToConsider.robot.moveLengthwise(1000);

                stateToConsider.table.shellsObtained+=2;

                stateToConsider.obtainedPoints += 4;

                stateToConsider.robot.moveLengthwise(-200);

                stateToConsider.robot.shellsOnBoard=false;

                stateToConsider.robot.turn(Math.PI);

                stateToConsider.robot.moveLengthwise(200);

                stateToConsider.robot.setLocomotionSpeed(speedBeforeScriptWasCalled);

                ArrayList<ObstacleCircular> cir = stateToConsider.table.getObstacleManager().getFixedObstacles();

                //On supprime les obstacles de la table
                for(ObstacleCircular i : cir)
                {
                    if(i.isInObstacle(new Vec2(1300,750)) || i.isInObstacle(new Vec2(1300,450)))
                    {
                        stateToConsider.table.getObstacleManager().removeObstacle(i);
                    }
                }

            } catch (UnableToMoveException e) {
                e.printStackTrace();
            }
        }
        if(versionToExecute >= 1 && versionToExecute < 5)
        {
            Shell selected = getTheShell(versionToExecute);
            try {
                //Orientation vers le coquillage
                stateToConsider.robot.turn(Math.atan((selected.getY() - stateToConsider.robot.getPosition().y) /
                        (selected.getX() - stateToConsider.robot.getPosition().x)));

                //TODO ouvrir la porte droite
                stateToConsider.robot.useActuator(ActuatorOrder.OPEN_DOOR, true);

                stateToConsider.robot.doorIsOpen = true;

                stateToConsider.robot.setRobotRadius(TechTheSand.expandedRobotRadius);

                stateToConsider.robot.setTurningStrategy(TurningStrategy.LEFT_ONLY);

                stateToConsider.robot.setDirectionStrategy(DirectionStrategy.FORCE_FORWARD_MOTION);

                stateToConsider.robot.turnRelative(Math.PI);

                stateToConsider.robot.shellsOnBoard = true;

                ArrayList<ObstacleCircular> cir = stateToConsider.table.getObstacleManager().getFixedObstacles();

                //On supprime l'obstacle de la table
                for(ObstacleCircular i : cir)
                {
                    if(i.isInObstacle(selected.getPosition()))
                    {
                        stateToConsider.table.getObstacleManager().removeObstacle(i);
                        break;
                    }
                }

            } catch (UnableToMoveException e) {
                e.printStackTrace();
            } catch (SerialConnexionException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public int remainingScoreOfVersion(int version, GameState<?> state) {
        return 0;
    }

    @Override
    public Circle entryPosition(int version, int ray, Vec2 robotPosition) throws BadVersionException {
        if (version == 0 )
        {
            return new Circle(new Vec2(1150,300));
        }
        else if(version >= 1 && version < 5 )
        {
            Shell selected;
            if((selected = getTheShell(version)) == null)
                throw new BadVersionException(true);

            return selected.entryPosition;

        }
        else
        {
            //TODO jetter une exception
            log.debug("erreur : mauvaise version de script");
            throw new BadVersionException();
        }
    }

    @Override
    public void finalize(GameState<?> state) throws UnableToMoveException, SerialFinallyException {

    }

    @Override
    public Integer[] getVersion(GameState<?> stateToConsider) {
        return new Integer[0];
    }

    private Shell getTheShell(int version)
    {
        ArrayList<Shell> list = new ArrayList<Shell>();
        list.addAll(Table.ourShells);
        list.addAll(Table.neutralShells);

        for(Shell i : list)
        {
            if(i.getX() >= 0) //Ceux de notre côté
            {
                if(version == 1)
                    return i;
                else
                    version--;
            }
        }
        return null;
    }

}