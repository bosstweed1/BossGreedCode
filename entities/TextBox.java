package entities;

import org.newdawn.slick.opengl.Texture;

public class TextBox extends Shape 
{
	public TextBox ( double x, double y, double width, double height, Texture[] textureString )
	{
		super( x, y, width, height, textureString );
		code = 36;
		name = "TextBox";
		defaultWidth = 512;
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
