package tests;

import enums.ServiceNames;
import exceptions.PathNotFoundException;
import exceptions.PointInObstacleException;
import graphics.Window;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import pathDingDing.PathDingDing;
import robot.Robot;
import robot.RobotReal;
import scripts.TechTheSand;
import smartMath.Vec2;
import strategie.GameState;
import table.Table;
import table.obstacles.Obstacle;
import table.obstacles.ObstacleRectangular;
import utils.Log;

import java.util.ArrayList;


/**
 * Teste le PDD gràce à une inteface graphique ; peut aussi ordonner le robot à se déplacer à un point
 * Commandes :
 *        Clic gauche : pt de départ
 *        Clic droit : pt d'arrivée
 *        Touche O : le prochain clic sera un appel au robot, il se déplacera au dernier point spécifié par le clic droit
 *        Touche S : Le robot peut tourner à gauche et à droite
 *        Touche Q : Le robot ne peut tourner qu'à gauche
 *        Touche D : Le robot ne peut tourner qu'à droite
 */
public class JUnit_Pathfinding extends JUnit_Test
{
    Window win;
    Table table;
    PathDingDing pf;
    Log log;
    GameState<RobotReal> game;


    public static void main(String[] args) throws Exception
    {
        JUnitCore.main("tests.JUnit_Pathfinding");
    }

    @Before
    public void setUp() throws Exception
    {
        super.setUp();
        table = (Table)container.getService(ServiceNames.TABLE);
        
        // Pour dégager tous les coquillages de la table
        /*table.deleteAllTheShells();
        Vec2 sup1 = new Vec2(1255,725);
		Vec2 sup2 = new Vec2(1325,510);
		Vec2 sup3 = new Vec2(1380,136);
		table.getObstacleManager().freePoint(sup1);
		table.getObstacleManager().freePoint(sup2);
		table.getObstacleManager().freePoint(sup3);*/
        //game = (GameState<RobotReal>) container.getService(ServiceNames.GAME_STATE);
        log = (Log)container.getService(ServiceNames.LOG);
        win = new Window(table);
        

        //game.robot.setPosition(Table.entryPosition);
        //game.robot.setOrientation(Math.PI);
        //game.changeRobotRadius(TechTheSand.retractedRobotRadius);

       /* ArrayList<ObstacleRectangular> mRectangles = game.table.getObstacleManager().getRectangles();

        for (int i=0;i< mRectangles.size();i++)
        {
            if(mRectangles.get(i).isInObstacle(new Vec2(700,1100)))
            {
                game.table.getObstacleManager().removeObstacle(mRectangles.get(i));
            }
        }*/


        pf = (PathDingDing)container.getService(ServiceNames.PATHDINGDING);
        table.getObstacleManager().updateObstacles(TechTheSand.expandedRobotRadius);
       // game.robot.moveLengthwise(250);
    }



    @Test
    public void testClickedPF() throws Exception
    {
        win.getPanel().drawGraph(pf.getGraph());
        while(true)
        {
           /* if(win.getKeyboard().isModeActual() && win.getMouse().hasClickedRight())
            {

               // game.robot.setTurningStrategy(win.getKeyboard().getTurningStrategy());

                try
                {
                    //table.getObstacleManager().setEnnemyRobot1Position(win.getMouse().getMiddleClickPosition());
                    win.getPanel().drawArrayList(pf.computePathVec2(game.robot.getPosition(), win.getMouse().getRightClickPosition(), new ArrayList<Obstacle>()));

                }
                catch(PathNotFoundException e)
                {
                    log.debug("pas de chemin trouve entre "+game.robot.getPosition()+"et"+ win.getMouse().getRightClickPosition());
                }
                catch(PointInObstacleException e)
                {
                    log.debug("point d'arrivée dans un obstacle");
                }
                win.getPanel().repaint();
                //win.getKeyboard().resetModeActual();
                win.getMouse().resetHasClicked();
                //game.robot.moveToLocation(win.getMouse().getRightClickPosition(), new ArrayList<hook.Hook>(), table);
            }
            else */if(win.getMouse().hasClicked())
            {
                //game.robot.setTurningStrategy(win.getKeyboard().getTurningStrategy());
                try
                {
                    //table.getObstacleManager().setEnnemyRobot1Position(win.getMouse().getMiddleClickPosition());
                    //long start = System.currentTimeMillis();
                    win.getPanel().drawArrayList(pf.computePathVec2(win.getMouse().getLeftClickPosition(), win.getMouse().getRightClickPosition(), new ArrayList<Obstacle>()));
                    //long end = System.currentTimeMillis();
                    //System.out.println("time elapsed : " + (end - start));
                }
                catch(PathNotFoundException e)
                {
                    log.debug("pas de chemin trouve entre "+win.getMouse().getLeftClickPosition()+"et"+ win.getMouse().getRightClickPosition());
                }
                catch(PointInObstacleException e)
                {
                    log.debug("point d'arrivée dans un obstacle");
                }
                win.getPanel().repaint();
            }
            else
                Thread.sleep(200);
        }
    }

    }

