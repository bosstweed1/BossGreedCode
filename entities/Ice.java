package entities;

import org.newdawn.slick.opengl.Texture;

public class Ice extends Shape
{
	public Ice ( double x, double y, double width, double height, Texture[] textureString )
	{
		super( x, y, width, height, textureString );
		code = 13;
		name = "Ice";
		defaultWidth = 128;
		defaultHeight = 32;
		solid = true;
	}

	@Override
	public boolean intersects(Shape other)
	{
		return false;
	}

	@Override
	public void interact(Box player)
	{
		player.onIce = true;
	}

}
