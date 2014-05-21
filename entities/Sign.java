package entities;

import org.newdawn.slick.opengl.Texture;

public class Sign extends Shape 
{
	public Sign ( double x, double y, double width, double height, Texture[] textureString )
	{
		super( x, y, width, height, textureString );
		code = 35;
		name = "Sign";
		defaultWidth = 128;
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
