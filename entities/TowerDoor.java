package entities;

import org.newdawn.slick.opengl.Texture;

public class TowerDoor extends Shape 
{
	public TowerDoor ( double x, double y, double width, double height, Texture[] textureString )
	{
		super( x, y, width, height, textureString );
		code = 38;
		name = "TowerDoor";
		defaultWidth = 83;
		defaultHeight = 97;
		
	}

	@Override
	public void interact(Box player) 
	{
		//do nothing
	}

	@Override
	public boolean intersects(Shape other) 
	{
		return false;
	}

}