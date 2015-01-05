package graphics;

import java.awt.Graphics;

import javax.swing.JPanel;

import java.awt.Color; 
import java.util.ArrayList;

import table.Table;
import smartMath.Vec2;

import table.Table;
import table.obstacles.*;

/**
 * panneau sur lequel est dessine la table
 * @author Etienne
 *
 */
public class Panel extends JPanel
{	
	private ArrayList<Vec2> mPath = new ArrayList<Vec2>();
	private Table mTable;
	
	public Panel(Table table)
	{
		mTable = table;
	}
	
	public void paintComponent(Graphics g)
	{
		g.setColor(Color.black);
	    g.fillRect(0, 0, this.getWidth(), this.getHeight());
	    
	    g.setColor(Color.darkGray);
	    
	    ArrayList<ObstacleLinear> lines = mTable.getObstacleManager().getLines();
	    for(int i = 0; i < lines.size(); i++)
	    {
	    	g.drawLine((int)((lines.get(i).getA().x + 1500) * this.getWidth() / 3000), (int)((-lines.get(i).getA().y) * this.getHeight() / 2000 + this.getHeight()), (int)((lines.get(i).getB().x + 1500) * this.getWidth() / 3000), (int)((-lines.get(i).getB().y) * this.getHeight() / 2000 + this.getHeight()));
	    }
	    
	    g.setColor(Color.white);
	    
	    ArrayList<ObstacleRectangular> rects = mTable.getObstacleManager().getRects();
	    for(int i = 0; i < rects.size(); i++)
	    {
	    	g.fillRect((rects.get(i).getPosition().x - (rects.get(i).getSizeX() / 2) + 1500) * this.getWidth() / 3000, -(rects.get(i).getPosition().y + rects.get(i).getSizeY()) * this.getHeight() / 2000 + this.getHeight(), rects.get(i).getSizeX() * this.getWidth() / 3000, rects.get(i).getSizeY() * this.getHeight() / 2000);
	    }
	    
	    g.setColor(Color.GREEN);
	    
	    ArrayList<ObstacleCircular> greenPlots = mTable.getObstacleManager().getGreenPlots();
	    for(int i = 0; i < greenPlots.size(); i++)
	    {
	    	g.drawOval((greenPlots.get(i).getPosition().x - greenPlots.get(i).getRadius() + 1500) * this.getWidth() / 3000, -(greenPlots.get(i).getPosition().y - greenPlots.get(i).getRadius()) * this.getHeight() / 2000 + this.getHeight()/2, (2 * greenPlots.get(i).getRadius()) * this.getWidth() / 3000, (2 * greenPlots.get(i).getRadius()) * this.getHeight() / 2000);
	    }
	    
	    g.setColor(Color.YELLOW);
	    
	    ArrayList<ObstacleCircular> yellowPlots = mTable.getObstacleManager().getYellowPlots();
	    for(int i = 0; i < yellowPlots.size(); i++)
	    {
	    	g.drawOval((yellowPlots.get(i).getPosition().x - yellowPlots.get(i).getRadius() + 1500) * this.getWidth() / 3000, -(yellowPlots.get(i).getPosition().y - yellowPlots.get(i).getRadius()) * this.getHeight() / 2000 + this.getHeight()/2, (2 * yellowPlots.get(i).getRadius()) * this.getWidth() / 3000, (2 * yellowPlots.get(i).getRadius()) * this.getHeight() / 2000);
	    }
	    
	    g.setColor(Color.blue);
	    for(int i = 0; i+1 < mPath.size(); i++)
	    {
	    	g.drawLine((mPath.get(i).x + 1500) * this.getWidth() / 3000, -mPath.get(i).y * this.getHeight() / 2000 + this.getHeight(), (mPath.get(i+1).x + 1500) * this.getWidth() / 3000, -mPath.get(i+1).y * this.getHeight() / 2000 + this.getHeight());
	    }
	    g.setColor(Color.red);
	    for(int i = 0; i < mPath.size(); i++)
	    {
	    	g.drawString(mPath.get(i).x + ", " + mPath.get(i).y, (mPath.get(i).x + 1500) * this.getWidth() / 3000, -mPath.get(i).y * this.getHeight() / 2000 + this.getHeight());
	    }
	    
	    
	}
	
	public void drawArrayList(ArrayList<Vec2> path)
	{
		
		mPath = path;
		repaint();
	}
	
	public Table getTable()
	{
		return mTable;
	}
}
