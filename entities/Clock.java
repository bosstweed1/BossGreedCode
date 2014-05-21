package entities;

import org.newdawn.slick.opengl.Texture;

public class Clock extends Shape
{
	public Clock ( double x, double y, double width, double height, Texture[] textureString )
	{
		super( x, y, width, height, textureString );
		code = 31;
		name = "Clock";
		on = true;
		defaultWidth = 32;
		defaultHeight = 32;
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
