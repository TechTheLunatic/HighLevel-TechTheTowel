package tests;


import hook.Hook;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import enums.ServiceNames;
import exceptions.Locomotion.UnableToMoveException;
import robot.DirectionStrategy;
import robot.Locomotion;
import smartMath.Vec2;

/**
 * Test unitaire pour flooder la serie.
 *
 * @author Théo
 */
public class JUnit_Flood extends JUnit_Test
{
	/** The deplacements. */
	private Locomotion mLocomotion;

	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		log.debug("JUnit_DeplacementsTest.setUp()", this);
		mLocomotion = (Locomotion)container.getService(ServiceNames.LOCOMOTION);
		config.set("couleur", "vert");
		mLocomotion.updateConfig();
		mLocomotion.setPosition(new Vec2(-123, 456));
		mLocomotion.setOrientation(Math.PI);
		mLocomotion.setTranslationnalSpeed(170);
		mLocomotion.setRotationnalSpeed(160);
	}

	@Test
	public void testFlood() throws Exception
	{
		int compt=0;
		while(true)
		{
			log.debug(compt++, this);
			mLocomotion.getPosition();
		}
	}	
	
}

