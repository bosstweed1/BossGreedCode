package entities;

import org.newdawn.slick.opengl.Texture;

public class Coin extends Shape
{
	public Coin ( double x, double y, double width, double height, Texture[] textureString )
	{
		super( x, y, width, height, textureString );
		code = 7;
		name = "Coin";
		defaultWidth = 16;
		defaultHeight = 32;
		partner = this;
		action = 2;	// disappear
		visible = true;
	}

	@Override
	public boolean intersects(Shape other)
	{
		return false;
	}

	@Override
	public void interact(Box player)
	{
		if (!this.removeMe)
		{
			player.goldCount++;
			this.removeMe = true;
		}
	}
}
