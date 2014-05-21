package entities;

import org.newdawn.slick.opengl.Texture;

public class Bat extends Shape
{
	public Bat ( double x, double y, double width, double height, Texture[] textureString )
	{
		super( x, y, width, height, textureString );
		code = 3;
		name = "Bat";
		defaultWidth = 128;
		defaultHeight = 32;
		solid = true;
		visible = true;
	}

	@Override
	public void interact(Box player)
	{
		if (this.weak && player.thwomping)
		{
			this.solid = false;
			this.fading = true;
		}
	}

	@Override
	public boolean intersects(Shape other)
	{
		return false;
	}

}
