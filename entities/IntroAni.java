package entities;

import org.newdawn.slick.opengl.Texture;

public class IntroAni extends Shape
{
	public IntroAni ( double x, double y, double width, double height, Texture[] textureString )
	{
		super( x, y, width, height, textureString );
		code = 37;
		name = "IntroAni";
		defaultWidth = 40;
		defaultHeight = 40;
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

	public int getType() {
		// TODO Auto-generated method stub
		return this.type;
	}
	
	public void setType(int newType) {
		// TODO Auto-generated method stub
		this.type = newType;
	}
}