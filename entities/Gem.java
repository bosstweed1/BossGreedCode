package entities;

import org.newdawn.slick.opengl.Texture;

public class Gem extends Shape
{
	boolean winner = false;
	public Gem ( double x, double y, double width, double height, Texture[] textureString )
	{
		super( x, y, width, height, textureString );
		code = 10;
		name = "Gem";
		defaultWidth = 64;
		defaultHeight = 64;
	}

	@Override
	public boolean intersects( Shape other )
	{
		return false;
	}

	@Override
	public void interact( Box player )
	{
		System.out.println("WINNER\n");
		this.winner = true;
	}

}
