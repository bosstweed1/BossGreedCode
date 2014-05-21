package entities;

import org.newdawn.slick.opengl.Texture;

import game.GameOn;

public class Grav extends Shape
{
	public Grav ( double x, double y, double width, double height, Texture[] textureString )
	{
		super( x, y, width, height, textureString );
		code = 11;
		name = "Grav";
		defaultWidth = 80;
		defaultHeight = 12;
		solid = true;
	}

	@Override
	public boolean intersects(Shape other)
	{
		boolean intersect = false;
		if ((this.getX() + this.getWidth()) >= other.getX()
				&& (this.getX() + this.getWidth()) <= (other.getX() + other
						.getWidth())
				&& (this.getY() + this.getHeight()) >= other.getY()
				&& (this.getY() + this.getHeight()) <= (other.getY() + other
						.getHeight()))
		{
			intersect = true;

		} else if ((this.getX() + this.getWidth()) >= other.getX()
				&& (this.getX() + this.getWidth()) <= (other.getX() + other
						.getWidth()) && this.getY() >= other.getY()
				&& this.getY() <= (other.getY() + other.getHeight()))
		{
			intersect = true;

		} else if (this.getX() >= other.getX()
				&& this.getX() <= (other.getX() + other.getWidth())
				&& (this.getY() + this.getHeight()) >= other.getY()
				&& (this.getY() + this.getHeight()) <= (other.getY() + other
						.getHeight()))
		{
			intersect = true;

		} else if (this.getX() >= other.getX()
				&& this.getX() <= other.getX() + other.getWidth()
				&& this.getY() >= other.getY()
				&& this.getY() <= (other.getY() + other.getHeight()))
		{
			intersect = true;

		}
		return intersect;
	}

	@Override
	public void interact(Box player)
	{
		if (player.groundPiece != this)
		{
			player.gravityMod = -player.gravityMod;
			if( player.gravityMod == 1)
			{
				player.y += player.height;
			}
			else
			{
				player.y -= player.height;
			}
			player.grounded = false;
			player.bounce = true;
			if ( !player.winning)
			{
				GameOn.gravSound.playAsSoundEffect(1, 1, false);
			}
		}
	}

}
