package entities;

import org.newdawn.slick.opengl.Texture;

public class Puck extends Shape 
{
	public Puck ( double x, double y, double width, double height, Texture[] textureString )
	{
		super( x, y, width, height, textureString );
		code = 35;
		name = "Puck";
		defaultWidth = 64;
		defaultHeight = 128;
		
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