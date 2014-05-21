package entities;

import org.lwjgl.Sys;
import org.newdawn.slick.opengl.Texture;

public class Dead extends Shape
{
	public Dead ( double x, double y, double width, double height, Texture[] textureString )
	{
		super( x, y, width, height, textureString );
		code = 8;
		name = "Dead";
		defaultWidth = 128;
		defaultHeight = 32;
		solid = true;
	}

	@Override
	public boolean intersects( Shape other )
	{
		return false;
	}

	@Override
	public void interact( Box player )
	{
		player.dying = true;
		player.timer = 0;
		player.savex = this.x;
		player.savey = this.y;
		player.dyingTime = Sys.getTime() * 1000 / Sys.getTimerResolution();
	}

}
