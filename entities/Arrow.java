package entities;

import org.newdawn.slick.opengl.Texture;

public class Arrow extends Shape
{
	public Arrow ( double x, double y, double width, double height, Texture[] textureString )
	{
		super( x, y, width, height, textureString );
		code = 1;
		name = "Arrow";
	}
	
	@Override
	public boolean intersects(Shape other)
	{
		return false;
	}

	@Override
	public void interact(Box player) {
		//do nothing
		
	}

	

}
