package entities;

import org.newdawn.slick.opengl.Texture;

public class Text extends Shape
{
	public Text ( double x, double y, double width, double height, Texture[] textureString )
	{
		super( x, y, width, height, textureString );
		code = 21;
		name = "Text";
		defaultWidth = 256;
		defaultHeight = 48;
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
