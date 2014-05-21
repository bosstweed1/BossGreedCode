package entities;

import org.newdawn.slick.opengl.Texture;

public class Wall extends Shape
{
	public Wall ( double x, double y, double width, double height, Texture[] textureString )
	{
		super( x, y, width, height, textureString );
		code = 22;
		name = "Wall";
		defaultWidth = 40;
		defaultHeight = 40;
	}
	
	@Override
	public boolean intersects(Shape other)
	{
		return false;
	}

	@Override
	public void interact(Box player)
	{
		// nothing
	}
}
