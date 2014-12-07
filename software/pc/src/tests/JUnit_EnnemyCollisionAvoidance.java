package tests;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import enums.ServiceNames;
import robot.*;
import smartMath.Vec2;
import table.Table;

// TODO: Auto-generated Javadoc
/**
 * Tests unitaires pour la détection d'ennemi.
 *
 * @author pf
 */
public class JUnit_EnnemyCollisionAvoidance extends JUnit_Test
{

	/** The robotvrai. */
	private RobotReal robotvrai;
	
	/** The table. */
	private Table table;
	
	/* (non-Javadoc)
	 * @see tests.JUnit_Test#setUp()
	 */
	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		log.debug("JUnit_DetectionEnnemiTest.setUp()", this);
		config.set("couleur", "jaune");
		robotvrai = (RobotReal) container.getService(ServiceNames.ROBOT_REAL);
		table = (Table) container.getService(ServiceNames.TABLE);
		container.getService(ServiceNames.THREAD_SENSOR);
	}

	/**
	 * Test_ajout obstacle.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void test_ajoutObstacle() throws Exception
	{
		log.debug("JUnit_DetectionEnnemiTest.test_ajoutObstacle()", this);
		robotvrai.setPosition(new Vec2(-600, 1410));
		robotvrai.setOrientation((float)-Math.PI/2);
		Thread.sleep(1000);
		container.startInstanciedThreads();
		Thread.sleep(1000);
		Assert.assertEquals(table.mObstacleManager.getMobileObstaclesCount(), 0);
		log.warning("!!!!!!!!!!!!!!!!!!!!!!!!!!!!", this);
		log.warning("Vous avez 5 secondes pour placer un obstacle devant le robot", this);
		Thread.sleep(6000);
		Assert.assertTrue(table.mObstacleManager.getMobileObstaclesCount() >= 1);
	}

	//TODO: écrire un test pour chaque élément de la table, et vérifier s'il est détecté ou non (selon ce qui devrait se passer).
	
}
