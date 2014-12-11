package tests;

import org.junit.*;

import graphics.*;
import smartMath.Vec2;

import java.util.ArrayList;

public class JUnit_Graphics extends JUnit_Test
{
	Window win;
	
    @Before
    public void setUp() throws Exception
    {
    	win = new Window();
        super.setUp();
    }
    
    //test de l'intersection de deux segments
    @Test
    public void testPanel()
    {
    	ArrayList<Vec2> path = new ArrayList<Vec2>();
    	path.add(new Vec2(0, 100));
    	path.add(new Vec2(0, 1900));
    	path.add(new Vec2(-1400, 1900));
    	path.add(new Vec2(-1400, 100));
    	win.getPanel().drawArrayList(path);
    }
}