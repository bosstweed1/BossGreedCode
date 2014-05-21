package editor;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import entities.*;

public class Level 
{
	public String name, inputValue;
	public int id, totalCoins;
	
	
	public List<Shape> shapes = new ArrayList<Shape>(20);
	
	public Level( String name, List<Shape> shapes )
	{
		this.shapes = shapes;
		this.name = name;
		loadLevel( name );
	}
	
	public void loadLevel( String filename )
	{
		inputValue = JOptionPane.showInputDialog("Enter the filename to load please: ");
		if ( inputValue != null && !inputValue.equals("") )
		{
			try
			{
				shapes.clear();
				ObjectInputStream IS = new ObjectInputStream( new FileInputStream(inputValue) );
				int size = IS.readInt();
				for ( int i = 0; i < size; i++ )
				{
					int code = IS.readInt();
					Shape temp = Shape.load( IS, code, null );
					LevelEditor.assignPic( temp );
					shapes.add( temp );
				}
				System.out.println("Loaded!");
			} 
			catch ( FileNotFoundException e )
			{
				e.printStackTrace();
			} 
			catch ( IOException e )
			{
				e.printStackTrace();
			}
		}
		
	}
	
}
