package entities;

import org.newdawn.slick.opengl.Texture;

public class PauseFade extends Shape 
{
	public PauseFade ( double x, double y, double width, double height, Texture[] textureString )
	{
		super( x, y, width, height, textureString );
		code = 32;
		name = "PauseFade";
		defaultWidth = 32;
		defaultHeight = 32;
	}

	@Override
	public void interact(Box player) 
	{
		// TODO Auto-generated method stub

	}

	@Override
	public boolean intersects(Shape other) 
	{
		// TODO Auto-generated method stub
		return false;
	}

}
