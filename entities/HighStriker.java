package entities;


import org.newdawn.slick.opengl.Texture;

public class HighStriker extends Shape 
{
	private int coinAmt;
	public int currImage = 0;
	private int currTimer;
	boolean goingUp= true, goingDown = false;
	public boolean win, done = false, dynamic = false;
	
	public HighStriker ( double x, double y, double width, double height, Texture[] textureString )
	{
		super( x, y, width, height, textureString );
		code = 32;
		name = "HighStriker";
		defaultWidth = 128;
		defaultHeight = 512;
		this.type = 0;
		solid = true;
		visible = true;
	}

	@Override
	public void interact(Box player) 
	{
		// TODO Auto-generated method stub

	}
	@Override
	public int draw()
	{
		if ( !this.dynamic )
			return super.draw();
		else
		{
			textureStart();
			
			// If we are beyond the bounds of the animation array, loop TODO: to keep the last image we have to keep adding and subtracting 1, this blows
			
			
			
			
			if ( currTimer < textureDuration )
				currTimer++;
			else
			{
				currImage++;
				currTimer = 0;
			}
			
			if ( currImage >= myTextureArray.length  )
			{
				//stopAnimation = true;
				currImage = myTextureArray.length - 1;
			}
			
			
			setPic ( myTextureArray[currImage] );
					
			
			textureVertices();
			
			return ( timer / textureDuration );
		}
		
	}

	public void setCoin(int amt)
	{
		coinAmt = amt;
	}
	
	public int getCoin()
	{
		return coinAmt;
	}
	
	public int getCurrTimer()
	{
		return currTimer;
	}

	@Override
	public boolean intersects(Shape other) 
	{
		// TODO Auto-generated method stub
		return false;
	}

}
