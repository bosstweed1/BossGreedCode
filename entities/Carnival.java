package entities;

import org.newdawn.slick.opengl.Texture;

public class Carnival extends Shape 
{
	public Carnival ( double x, double y, double width, double height, Texture[] textureString )
	{
		super( x, y, width, height, textureString );
		code = 33;
		name = "Carnival";
		defaultWidth = 64;
		defaultHeight = 128;
		
	}

	@Override
	public void interact(Box player) {
		//do nothing

	}

	@Override
	public boolean intersects(Shape other) {
		
		return false;
	}

}
