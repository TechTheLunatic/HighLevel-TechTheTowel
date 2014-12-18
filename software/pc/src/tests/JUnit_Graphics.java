package tests;

import org.junit.*;

import enums.ServiceNames;
import graphics.*;
import smartMath.Vec2;
import table.Table;

import java.util.ArrayList;

public class JUnit_Graphics extends JUnit_Test
{
	Window win;
	
    @Before
    public void setUp() throws Exception
    {
        super.setUp();
    	//TODO : appeler le container pour creer la table
    	win = new Window(new Table(log, config));
    }
    
    //test de l'intersection de deux segments
    @Test
    public void testPanel() throws InterruptedException
    {
    	ArrayList<Vec2> path = new ArrayList<Vec2>();
    	path.add(new Vec2(0, 100));
    	path.add(new Vec2(0, 1900));
    	path.add(new Vec2(-1400, 1900));
    	path.add(new Vec2(-1400, 100));
    	win.getPanel().drawArrayList(path);
    	Thread.sleep(5000);
    }
}