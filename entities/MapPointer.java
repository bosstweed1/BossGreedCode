package entities;

import org.newdawn.slick.opengl.Texture;

public class MapPointer extends Shape 
{
	public MapPointer ( double x, double y, double width, double height, Texture[] textureString )
	{
		super( x, y, width, height, textureString );
		code = 27;
		name = "Map";

	}

	@Override
	public void interact(Box player) 
	{
		//do nothing

	}

	@Override
	public boolean intersects(Shape other) 
	{
		
		return false;
	}

}
