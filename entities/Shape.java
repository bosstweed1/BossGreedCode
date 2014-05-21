package entities;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_LINE_LOOP;
import static org.lwjgl.opengl.GL11.GL_LINE_STIPPLE;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glColor4d;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glLineStipple;
import static org.lwjgl.opengl.GL11.glLineWidth;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glVertex2d;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;

public abstract class Shape
{
	// Public variables TODO: these should probably ALL be private..
	public boolean visible = true, touched, selected, user, removeMe = false, 
					solid = false, moving = false, upDown = true, downRight = true, 
					on, vert, alive, faded = false, fading = false, finished = false, weak, 
					transparent = false, stopAnimation = false;
	
	public double dx, dy, x, y, width, height, endPos, startPos, partnerX,
			partnerY, moveSpeed, fade = 1;
	
	public String name, textureString, tileSet = "Default";;
	
	public Texture pic;					// for drawing in the level editor
	
	public static int BORDER = 5;		// for selecting in the level editor
	
	public int type, save_x, save_y, startX, timer = 0, textureDuration = 10, editorPage = 1,
			displayOrder = 3, defaultWidth, defaultHeight, action;
	
	public Shape partner;
	
	public Texture [] myTextureArray;

	// Protected variables
	protected int code;

	public Shape ( double x, double y, double width, double height, Texture[] textureString )
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.myTextureArray = textureString;
	}

	public abstract void interact( Box player );

	public int draw()
	{
		textureStart();
		
		// If we are beyond the bounds of the animation array, loop TODO: to keep the last image we have to keep adding and subtracting 1, this blows
		if ( ( timer / textureDuration ) >= myTextureArray.length  )
		{
			if ( !stopAnimation )
				timer = 0;
			else
				timer--;
		}
		
		setPic ( myTextureArray[timer / textureDuration] );
		
		if ( ( timer / textureDuration ) < myTextureArray.length )
			this.timer++;
		
		textureVertices();
		
		return ( timer / textureDuration );
		
	}

	public abstract boolean intersects( Shape other );
	
	public void setTextureString( String textString )
	{
		this.textureString = textString;
	}

	public void action()
	{
		System.out.println("Actioning");
		if ( action == 1 )
			this.visible = true;
		if ( action == 2 )
			this.visible = false;
		if ( action == 3 )
			this.moving = true;
	}

	public void editorDraw()
	{
		if ( name.equals("Sky") )
			this.draw();
		else
		{
			textureStart();
			pic.bind();
			textureVertices();
		}
	}

	public void setPic( Texture tex )
	{
		pic = tex;
	}

	public static Shape load( ObjectInputStream IS, int shapeCode, Texture[] textMap )
	{
		Shape temp = new Bat( 0, 0, 0, 0, textMap );
		if ( shapeCode == 3 )
			temp = new Bat( 0, 0, 0, 0, textMap );
		else if ( shapeCode == 7 )
			temp = new Coin( 0, 0, 0, 0, textMap );
		else if ( shapeCode == 8 )
			temp = new Dead( 0, 0, 0, 0, textMap );
		else if ( shapeCode == 10 )
			temp = new Gem( 0, 0, 0, 0, textMap );
		else if ( shapeCode == 11 )
			temp = new Grav( 0, 0, 0, 0, textMap );
		else if ( shapeCode == 13 )
			temp = new Ice( 0, 0, 0, 0, textMap );
		
		loadInstanceVars( IS, temp );
		
		return temp;
	}

	public static void loadInstanceVars( ObjectInputStream IS, Shape temp )
	{
		try
		{
			temp.x = IS.readDouble();
			temp.y = IS.readDouble();
			temp.width = IS.readDouble();
			temp.height = IS.readDouble();

			temp.partnerX = IS.readDouble();
			temp.partnerY = IS.readDouble();
			temp.endPos = IS.readDouble();
			temp.startPos = IS.readDouble();
			temp.moveSpeed = IS.readDouble();

			temp.visible = IS.readBoolean();
			temp.upDown = IS.readBoolean();
			temp.downRight = IS.readBoolean();
			temp.moving = IS.readBoolean();

			temp.timer = IS.readInt();
			temp.type = IS.readInt();
			temp.action = IS.readInt();
	//		temp.displayOrder = IS.readInt();

			temp.weak = IS.readBoolean();
			temp.on = IS.readBoolean();
			temp.vert = IS.readBoolean();
			temp.alive = IS.readBoolean();
			temp.solid = IS.readBoolean();
			
		}
		catch ( IOException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void save( ObjectOutputStream OS, Shape temp )
	{
		try
		{
			OS.writeInt( temp.code );

			OS.writeDouble( temp.x );
			OS.writeDouble( temp.y );
			OS.writeDouble( temp.width );
			OS.writeDouble( temp.height );

			OS.writeDouble( temp.partnerX );
			OS.writeDouble( temp.partnerY );
			OS.writeDouble( temp.endPos );
			OS.writeDouble( temp.startPos );
			OS.writeDouble( temp.moveSpeed );

			OS.writeBoolean( temp.visible );
			OS.writeBoolean( temp.upDown );
			OS.writeBoolean( temp.downRight );
			OS.writeBoolean( temp.moving );

			OS.writeInt( temp.timer );
			OS.writeInt( temp.type );
			OS.writeInt( temp.action );
	//		OS.writeInt( temp.displayOrder );

			OS.writeBoolean( temp.weak );
			OS.writeBoolean( temp.on );
			OS.writeBoolean( temp.vert );
			OS.writeBoolean( temp.alive );
			OS.writeBoolean( temp.solid );
			
			System.out.println( "weak:" + temp.weak );
			System.out.println( "on:" + temp.on );
			System.out.println( "vert:" + temp.vert );
			System.out.println( "alive:" + temp.alive );
			System.out.println( "solid:" + temp.solid );
		}
		catch ( IOException e )
		{
			e.printStackTrace();
		}
	}
	

	public void setTextureArray( Texture[] newArray )
	{
		this.myTextureArray = newArray;
	}

	public void textureStart()
	{
		glEnable( GL_BLEND );
		glBlendFunc( GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA );
		GL11.glTexParameteri( GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP );//this magically stops 
		GL11.glTexParameteri( GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T,GL11.GL_CLAMP );	//pixel bleeding, gg opengl
		
		if ( !faded && !fading )
		{
			glColor4d( 1, 1, 1, 1 );
		}
		else if ( fading )
		{
			glColor4d( 1, 1, 1, this.fade );
			this.fade -= .02;
			
			if ( this.fade <= 0 )
			{
				this.visible = false;
			}
			else
			{
				this.visible = true;
			}
		}
		else
		{
			glColor4d( 1, 1, 1, .7 );
		}
		
		GL11.glEnable( GL11.GL_TEXTURE_2D );
	}

	public void textureVertices()
	{
		
		pic.bind();
		glBegin( GL_QUADS );
		
		glTexCoord2f( 0, 0 );
		glVertex2d( this.x, this.y );
		glTexCoord2f( 1, 0 );
		glVertex2d( this.x + this.width, this.y );
		glTexCoord2f( 1, 1 );
		glVertex2d( this.x + this.width, this.y + this.height );
		glTexCoord2f( 0, 1 );
		glVertex2d( this.x, this.y + this.height );
		glEnd();
		//GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
		//GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T,GL11.GL_CLAMP);
		GL11.glDisable( GL11.GL_TEXTURE_2D );

		if ( this.selected )
		{
			drawBorder( false );
			if ( partner != null )
				partner.drawBorder( true );
		}
		
		if ( faded )
			glColor4d( 1, 1, 1, 1 );	// fix it back
	}

	public void drawHitbox()
	{
		glBegin( GL_LINE_LOOP );
		glVertex2d( x, y );
		glVertex2d( x + width, y );
		glVertex2d( x + width, y + height );
		glVertex2d( x, y + height );
		glEnd();
	}

	// for selecting in the level editor
	public void drawBorder( boolean blue )
	{
		// copied most of the following from http://forums.inside3d.com/viewtopic.php?t=1326
		if ( blue )
			glColor3f( 0, 0, 1 ); // blue
		else
			glColor3f( 1, 0, 0 ); // red

		glLineWidth( 2 );  // Set line width to 2
		glLineStipple( 1, (short) 0xf0f0 );  // Repeat count, repeat pattern
		glEnable( GL_LINE_STIPPLE ); // Turn stipple on

		glBegin( GL_LINE_LOOP );
		glVertex2d( x - BORDER, y - BORDER );
		glVertex2d( x + BORDER + width, y - BORDER );
		glVertex2d( x + BORDER + width, y + BORDER + height );
		glVertex2d( x - BORDER, y + BORDER + height );
		glEnd();
		if ( action == 3 )
		{
			glBegin( GL_LINE_LOOP );
			double endPoint = endPos;
			if ( !downRight )
				endPoint = startPos;
			//System.out.println("StartPos, EndPos, endPoint: " + startPos + ", " + endPos + ", " + endPoint);
			if ( upDown )
			{
				glVertex2d( x - BORDER, endPoint - BORDER );
				glVertex2d( x + BORDER + width, endPoint - BORDER );
				glVertex2d( x + BORDER + width, endPoint + BORDER + height );
				glVertex2d( x - BORDER, endPoint + BORDER + height );
			} 
			else
			{
				glVertex2d( endPoint - BORDER, y - BORDER );
				glVertex2d( endPoint + BORDER + width, y - BORDER );
				glVertex2d( endPoint + BORDER + width, y + BORDER + height );
				glVertex2d( endPoint - BORDER, y + BORDER + height );
			}
			glEnd();
		}

		glDisable( GL_LINE_LOOP ); // Turn it back off
		glDisable( GL_LINE_STIPPLE ); // Turn it back off
		glEnd();
	}

	public boolean isVisible()
	{
		return visible;
	}

	public boolean contains( Shape other )
	{
		return ( (this.getX() < other.getX() )
				&& ( (this.getX() + this.getWidth()) > (other.getX() + other
						.getWidth())) && this.getY() < other.getY() && (this
				.getY() + this.getHeight()) > (other.getY() + other.getHeight()));
	}

	// ---- From AbstractMoveableEntity

	public void update( int delta )
	{
		this.x += delta * dx;
		this.y += delta * dy;
	}

	public double getHeight()
	{
		return height;
	}

	public double getWidth()
	{
		return width;
	}

	public double getX()
	{
		return x;
	}

	public double getY()
	{
		return y;
	}

	public double getDX()
	{
		return dx;
	}

	public double getDY()
	{
		return dy;
	}
	
	public int getShapeCode()
	{
		return code;
	}

	public void setHeight( double height )
	{
		this.height = height;
	}

	public void setWidth( double width )
	{
		this.width = width;
	}

	public void setX( double x )
	{
		this.x = x;
	}

	public void setY( double y )
	{
		this.y = y;
	}

	public void setDX( double dx )
	{
		this.dx = dx;
	}

	public void setDY( double dy )
	{
		this.dy = dy;
	}

	public void setPosition( double x, double y )
	{
		this.x = x;
		this.y = y;
	}

	
	
	//-- End AbstractMoveableEntity

}
