package entities;

import org.newdawn.slick.opengl.Texture;

public class Box extends Shape
{
	public int goldCount = 0;
	public boolean jumping, dying= false, win, winning, onIce = false, grounded = false, bounce = false, thwomping = false, hitWall = false, crashing = false;
	public double startX, startY;
	public int gravityMod = 1;						// gravityMod = 1 for normal, -1 for reverse
	public int lastDIR = 1;
	public int k = 0;
	public long dyingTime = 0;
	public double savex, savey;
	int increment = 10;
	double offset = 0;

	public Shape groundPiece;

	public Box ( double x, double y, double width, double height, Texture[] textureString )
	{
		super( x, y, width, height, textureString );
		code = 4;
		name = "Box";
		defaultWidth = 24;
		defaultHeight = 24;
		alive = true;
		groundPiece = null;
		win = false;
	}

	public boolean intersects(Shape other)
	{
		
		boolean intersect = false;
		double p1y;
		double p1x;
		double p2x;
		double p2y;
		double p3y = other.getY();
		double p3x = other.getX();
		double p4x = other.getX() + other.getWidth();
		double p4y = other.getY() + other.getHeight();
		
		if( other.name.equals("Gem") )
		{
			
			p1y = this.getY();
			p1x = this.getX();
			p2x = this.getX() + this.getWidth();
			p2y = this.getY() + this.getHeight();
		}
		else
		{
		
			p1y = this.getY() + 7;
			p1x = this.getX() + 4;
			p2x = this.getX() + this.getWidth() - 4;
			p2y = this.getY() + this.getHeight() - 10;
		}
		

		if ( p2y < p3y || p1y > p4y || p2x < p3x || p1x > p4x )
		{
			//System.out.println("not intersected");
		} 
		else
		{
			//System.out.println("i intersected");
			intersect = true;
			
		}
		if (intersect && other.visible)
		{
			if (other.partner != null)
			{
				if ( other.name.equals("Grav") && other.width < 15 && other.height < 15 ) //TODO: remove this?
				{
					other.partner.action();		// this is for blips
					other.visible = false;		// make them disappear on touch
				}
				
			}
			if (other.name.equals("Grav") && other.width > 15)
				other.interact(this);
			if (other.name.equals("Coin"))
				other.interact(this);
			if (other.name.equals("Dead") || other.name.equals("Doorjam") && !this.dying && !this.winning)
			{
				other.interact(this);
			}
			if (other.name.equals("Sky"))
				intersect = false;
			if (onIce && other.name.equals("Ice") && other.vert)
				lastDIR *= -1;				// bouncing off of the side of ice
			
			if ( other.name.equals("Bat") )
			{
				if ( this.x > other.x && ( this.x + this.width < other.x + other.width ) && this.y <= other.y + other.height )
				{
					/*this.crashing = true;
					this.y = other.y +other.height;
					GameOn.gravSpeed = 0;
					
					*/
					//System.out.println("Gravspeed: " + GameOn.gravSpeed);
					//GameOn.gravSpeed = .01;
					//System.out.println("fix me");
					//this.y = other.y + other.height;
					//intersect = false;
				}
			}
			
		}

		return intersect;
	}

	public boolean inMiddle (Shape other)
	{
		
		if ( this.x > other.x && ( this.x + this.width < other.x + other.width ) && ( ( (this.gravityMod == 1) && (this.y <= other.y + other.height) ) || ( (this.gravityMod == -1) && (this.y + this.height  >= other.y) ) ))
		{
			return true;
		}
		else
		{
			
			return false;
		}
		
	}
	
	/* getDirection
	 * 		Allows us a nice way to set the current texture's direction, less if's
	 */
	public String getDirection()
	{
		if ( this.lastDIR == 1 )
			return "Right";
		else
			return "Left";
	}
	
	/* getGravity
	 * 		Allows us a nice way to set the current texture's gravity, less if's
	 */
	public String getGravity()
	{
		if ( this.gravityMod == 1 )
			return "Down";
		else
			return "Up";
				
	}
	
	/* deathAnimation
	 * 		Allows for a nice way for the player to die in a nice way
	 */
	public void deathAnimation()
	{
		//	TODO: tweak this 
		double distance = -( .02 * this.timer * this.timer ) + ( .7 * this.timer) - 2.25;
		this.setY( this.getY() - distance );
		
	}
	
	public boolean under (Shape other)
	{
		
		if ( this.x + this.width > other.x && ( this.x < other.x + other.width ) && ( ( (this.gravityMod == -1) && (this.y > other.y) && (this.y + this.height < other.y) ) || ( (this.gravityMod == 1) && ( this.y + this.height  >= other.y + other.height ) && this.y < other.y + other.height ) ))
		{
			return true;
		}
		else
		{
			return false;
		}
		
	}
	@Override
	public void interact(Box player)
	{
		// nothing
	}

}
