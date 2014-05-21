package entities;

import org.newdawn.slick.opengl.Texture;

public class ArrowKey extends Shape
{
	public ArrowKey ( double x, double y, double width, double height, Texture[] textureString )
	{
		super( x, y, width, height, textureString );
		code = 2;
		name = "ArrowKey";
		defaultWidth = 64;
		defaultHeight = 64;
	}

	@Override
	public void interact(Box player)
	{
		// nothing
	}

	@Override
	public boolean intersects(Shape other)
	{
		return false;
	}

}
