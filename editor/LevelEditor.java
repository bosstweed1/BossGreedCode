package editor;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.GL_LINE_LOOP;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glRectd;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.opengl.GL11.glVertex2f;

import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import entities.Bat;
import entities.Coin;
import entities.Dead;
import entities.Gem;
import entities.Grav;
import entities.Ice;
import entities.Shape;

public class LevelEditor
{
	// 1280x800 for high res, 1024x600 for low res
	protected static final int EDITOR_RESOLUTION_X = 1024;			// width of the level editor screen
	protected static final int EDITOR_RESOLUTION_Y = 600;			// height of the level editor screen
	protected final static int GAME_RESOLUTION_X = 640;		// game dimensions
	protected final static int GAME_RESOLUTION_Y = 480;		// of BossGreed
	public static final int MAX_GRID_SIZE = 200;			// maximum size for the grid
	public static final int MIN_GRID_SIZE = 10;				// minimum size for the grid
	public static final int MAX_TYPE_SIZE = 25;				// maximum size for the 'type' var
	public static final int MIN_TYPE_SIZE = 0;				// minimum size for the 'type' var

	// for convenience, dependent on the above constants
	int TOP = (EDITOR_RESOLUTION_Y - GAME_RESOLUTION_Y) / 2 - 1 - 70;
	int BOTTOM = (EDITOR_RESOLUTION_Y + GAME_RESOLUTION_Y) / 2 + 1 - 70;
	int LEFT = (EDITOR_RESOLUTION_X - GAME_RESOLUTION_X) / 2 - 1;
	int RIGHT = (EDITOR_RESOLUTION_X + GAME_RESOLUTION_X) / 2 - 1;

	// not final, so you can change within the editor
	int FONT_SIZE = 24;				// (this is automatically changed if lowRes)
	int GRID_SIZE = 50;				// CHANGE THIS for a different default grid size
	int CAMERA_SCROLL_SPEED = 5;	// CHANGE THIS to adjust the WASD scroll speed
	int FUDGE_X = 320;				// don't worry about this
	int FUDGE_Y = 91;				// or this
	int THICKNESS = 1;				// CHANGE THIS to adjust the IJKL thickness

	int transX = GAME_RESOLUTION_X / 2, transY = GAME_RESOLUTION_Y / 2, mouseX,
			mouseY, width = 26, height = 26;
	float startX, startY;
	int [] Keys = new int[20];		// make an array to hold the keys for controls

	int picsX = 50, picsY = BOTTOM + 50, picsW = 50, picsH = 50;		// for the bottom pics grid
	int buttonCode = 1;				// used to choose which instance int to change
	int pointerX, pointerY;			// for the ^ used to show the current piece
	int page = 1;					// used for the bottom pics
	int MAX_PAGES = 1;

	int currentOrder = 3;
	int currentType = 0;

	boolean drawGrid = true, lowRes = EDITOR_RESOLUTION_X < 1280,
			settingPartner, settingStart;
	boolean mouseLockX = false, mouseLockY = false;

	private String currShape = "Box", inputValue, skyHex = "default",
			fileName = "Unnamed";


	private List<Shape> shapes = new ArrayList<Shape>(20);
	private List<Shape> bottomShapes = new ArrayList<Shape>(20);

	UnicodeFont uniFont;
	
		
	// Dead Textures
	public static Texture[] deadHorizontal = new Texture[5];
	public static Texture[] deadVertical = new Texture[5];
	
	// Cliff Textures
	public static Texture[] cliffGrassUp = new Texture[1];
	public static Texture[] cliffGrassDown = new Texture[1];
	public static Texture[] cliffGrassDownBreak = new Texture[1];
	
	// Coin Textures
	public static Texture[] coin = new Texture[1];
	
	// Door Textures
	public static Texture[] doorBasic = new Texture[1];
	public static Texture[] doorWin = new Texture[15];
	
	// GravFlip Textures
	public static Texture[] gravflip = new Texture[19];
	
	// Ice Textures
	public static Texture[] ice = new Texture[1];
		
	public static Map<String,Texture[]> textureMap = createTextureMap();
	
	public static Map<Integer,String> shapeMap = createShapeMap();
	
	private Shape selected, current = new Bat( 0, 0, 0, 0, textureMap.get("cliffGrassDown") );

	
	public LevelEditor()
	{
		
		//  TODO: Whats the point of this ?
		File dir1 = new File(".");
        File dir2 = new File("..");
        
        try 
        {
            System.out.println("Current dir : " + dir1.getCanonicalPath());
            System.out.println("Parent  dir : " + dir2.getCanonicalPath());
        } 
        catch ( Exception e ) 
        {
            e.printStackTrace();
        }
            
		initGL();
		adjustResolution();	// TODO: this may not be necessary
		initFonts();
		loadAllTextures( textureMap );

		assignPic( current );
		drawShapes();

		while ( !Display.isCloseRequested() )
		{
			glClear(GL_COLOR_BUFFER_BIT);	// wipe the screen

			mouse();

			mouseInput();
			input();
			render();
			drawText();

			Display.update();
			Display.sync(60);
		}

		Display.destroy();
		System.exit(0);
	}
	
	/* createTextureMap()
	 * 		Create and initialize our texture map and textures
	*/
	private static Map<String,Texture[]> createTextureMap()
	{
		
		HashMap<String,Texture[]> tempMap = new HashMap<String,Texture[]>();
		
		tempMap.put( "deadHorizontal", deadHorizontal);
		tempMap.put( "deadVertical", deadVertical);
		tempMap.put( "cliffGrassUp", cliffGrassUp);
		tempMap.put( "cliffGrassDown", cliffGrassDown);
		tempMap.put( "cliffGrassDownBreak", cliffGrassDownBreak);
		tempMap.put( "coin", coin); 
		tempMap.put( "doorBasic", doorBasic);
		tempMap.put( "doorWin", doorWin);
		tempMap.put( "gravflip", gravflip);
		tempMap.put( "ice", ice);

		return ( Collections.unmodifiableMap(tempMap) );
		
	}
	
	/* createShapeMap()
	 * 		Create and initialize our mappings from code -> shape to 
	*/
	private static Map<Integer,String> createShapeMap()
	{
		
		HashMap<Integer,String> tempMap = new HashMap<Integer,String>();
		
		tempMap.put( 40, "dustUp" );		//TODO: Switch this one
		tempMap.put( 1, "arrowRight" );		//TODO: Shouldnt need this
		tempMap.put( 2, "spacebar" );		//TODO: Shouldnt need this
		tempMap.put( 3, "cliffGrassDown" );	//TODO: Switch this one
		tempMap.put( 4, "bagDownRight" );	//TODO: Switch this one
		tempMap.put( 33, "carnivalBarker" );//TODO: Shouldnt need this
		tempMap.put( 31, "clock" );			//TODO: Shouldnt need this
		tempMap.put( 7, "coin" );
		tempMap.put( 8, "deadHorizontal" );	//TODO: Switch this one
		tempMap.put( 10, "doorBasic" );
		tempMap.put( 11, "gravflip" );
		tempMap.put( 32, "highstrikerBasic" );
		tempMap.put( 13, "ice" );
		tempMap.put( 37, "intro" );			//TODO: Shouldnt need this
		tempMap.put( 27, "worldTower" );	//TODO: Switch this one
		tempMap.put( 39, "shard" );			//TODO: Shouldnt need this
		tempMap.put( 35, "sign" );			//TODO: Shouldnt need this
		tempMap.put( 21, "text0" );			//TODO: Shouldnt need this
		tempMap.put( 36, "textBox" );		//TODO: Shouldnt need this
		tempMap.put( 35, "towerDoor" );		//TODO: Shouldnt need this
		tempMap.put( 22, "wallpaper");		//TODO: Shouldnt need this
		return ( Collections.unmodifiableMap(tempMap) );
		
	}
	
	/* loadAllTextures()
	 * 		This will load the textures in an efficient way into the map
	 */
	private static void loadAllTextures( Map<String,Texture[]> textMap )
	{
		for ( Map.Entry<String, Texture[]> entry : textMap.entrySet() )
		{
			int size = entry.getValue().length;
			String currEntry = entry.getKey();
			
			for ( int i = 0; i < size; i++ )
			{
				entry.getValue()[i] = loadTexture("Animations/" + currEntry + "/" + currEntry + i);
			}
			
		}
		
	}

	/* adjustResolution()
	 * 		This will change the size of text if you are on a lower resolution
	 */
	public void adjustResolution()
	{
		// CHANGE THIS if you find problems with lowRes mode
		if ( lowRes )
		{
			FONT_SIZE = 18;
			GRID_SIZE = 32;
			TOP += 30;
			BOTTOM += 30;
			LEFT += 50;
			RIGHT += 50;
			//	picsW = 35;
			//	picsH = 35;
			//	picsY -= 15;
			FUDGE_X = 242;				// don't worry about this
			FUDGE_Y = 21;				// or this		
		}
	}

	/* getShape()
	 * 		Will either display data of an existing shape
	 * 		or change the current shape being used
	 * 		TODO: make this two separate functions?
	 */
	public Shape getShape()
	{
		Shape ans = null;
		
		// This will show the details of the shape that has already been placed
		// in the level
		if ( mouseY + transY >= TOP && mouseY + transY <= BOTTOM )
		{
			for ( Shape shape : shapes )
			{
				if ( mouseX >= shape.getX()
						&& ( mouseX <= shape.getX() + shape.getWidth() )
						&& mouseY >= shape.getY()
						&& ( mouseY <= shape.getY() + shape.getHeight() )
						&& !shape.name.equals("Sky")
						&& currentOrder == shape.displayOrder )
				{
					ans = shape;
					if ( !settingPartner && !settingStart )
						shape.selected = true;
				} 
				else
				{
					shape.selected = false;
				}
			}
		}
		
		// This will select a new shape from the menu bar
		if ( mouseY + transY >= BOTTOM )
		{
			for ( Shape shape : bottomShapes )
			{
				if ( mouseX + transX >= shape.getX()
						&& ( mouseX + transX <= shape.getX() + shape.getWidth() )
						&& mouseY + transY >= shape.getY()
						&& ( mouseY + transY <= shape.getY() + shape.getHeight() )
						&& page == shape.editorPage )
				{
					currShape = shape.name;
					current = getCurrShape();
					if ( current.name.equals("Cloud") )
						current.type = 1;
					assignPic( current );
					ans = current;
					pointerX = (int) ( shape.getX() + ( shape.getWidth() - FONT_SIZE ) / 2 ) + 5;
					pointerY = (int) ( shape.getY() + ( shape.getHeight() + FONT_SIZE ) / 2 ) + 5;

				}
			}
		}
		
		return ans;
	}

	/* render()
	 * 		Main drawing function
	 */
	private void render()
	{

		// Draw the current selected piece
		current.setPosition( mouseX, mouseY );
		current.setWidth( width );
		current.setHeight( height );
		current.displayOrder = currentOrder;
		
		assignPic( current );

		// Draw the game box
		glBegin( GL_LINE_LOOP );
		glVertex2f( LEFT, TOP );
		glVertex2f( RIGHT, TOP );
		glVertex2f( RIGHT, BOTTOM );
		glVertex2f( LEFT, BOTTOM );
		glEnd();

		translate();

		// This is where the level itself goes TODO: functions?

		for ( int i = 1; i <= 4; i++ )
		{
			for ( Shape shape : shapes )
			{
				if ( shape.displayOrder == i )
					shape.draw();
			}
		}

		current.draw();

		// End level drawing

		drawBoundary();
		
		glPopMatrix();

		// Draw the bottom menu of shapes
		for ( Shape shape : bottomShapes )
		{
			if ( page == shape.editorPage )
			{
				shape.draw();
			}
		}

		if ( drawGrid )
			drawGrid();

	}

	/* assignPic()
	 * 	Assign Texture array to current in this function
	 */
	public static void assignPic( Shape temp )
	{
		// this is where the heavy lifting is done to
		// determine which picture to show
		
		temp.setTextureArray( textureMap.get( shapeMap.get( temp.getShapeCode() ) ) );
		
		if ( temp.name.equals("Bat") )
		{
			if ( temp.vert )
				temp.setTextureArray( textureMap.get("cliffGrassUp" ) );
			else if ( temp.weak  )
				temp.setTextureArray( textureMap.get("cliffGrassDownBreak" ) );
		}
		
		if ( temp.name.equals("Dead") )
		{
			if ( temp.vert )
				temp.setTextureArray( textureMap.get("deadVertical" ) );
		}
	}

	/* getCurrShape()
	 * 		Creates the new shape that was selected to add it to the level
	 */
	public Shape getCurrShape()
	{
		Shape temp = new Bat( mouseX, mouseY, width, height, textureMap.get("cliffGrassDown") );
		if (currShape == "Coin")
			temp = new Coin( mouseX, mouseY, width, height, textureMap.get("coin") );
		if (currShape == "Dead")
			temp = new Dead( mouseX, mouseY, width, height, textureMap.get("deadHorizontal") );
		if (currShape == "Gem")
			temp = new Gem( mouseX, mouseY, width, height, textureMap.get("doorBasic") );
		if (currShape == "Grav")
			temp = new Grav( mouseX, mouseY, width, height, textureMap.get("grav") );
		if (currShape == "Ice")
			temp = new Ice( mouseX, mouseY, width, height, textureMap.get("ice") );

		if ( temp.defaultWidth > 0 && (mouseY + transY > BOTTOM) )
		{
			width = temp.defaultWidth;
			height = temp.defaultHeight;
		}

		return temp;
	}

	/* drawShapes()
	 * 		Draws the shapes at the bottom of the screen? how do we use one object here?
	 * 		TODO: look into this
	 */
	public void drawShapes()
	{

		Shape temp = new Bat( picsX, picsY, picsW, picsH, textureMap.get("cliffGrassDown")  );
		assignAndMove( temp );
		temp = new Coin( picsX, picsY, picsW, picsH, textureMap.get("coin")  );
		assignAndMove( temp );
		temp = new Dead( picsX, picsY, picsW, picsH, textureMap.get("deadHorizonal")  );
		assignAndMove( temp );
		temp = new Gem( picsX, picsY, picsW, picsH, textureMap.get("doorBasic")  );
		assignAndMove( temp );
		temp = new Grav( picsX, picsY, picsW, picsH, textureMap.get("grav")  );
		assignAndMove( temp );
		temp = new Ice( picsX, picsY, picsW, picsH, textureMap.get("ice") );
		assignAndMove( temp );

		page = 1;
	}

	/* assignAndMove()
	 * 		Assigns pic to the bottom row, why do we keep adding to bottomShapes?TODO: check this
	 */
	public void assignAndMove( Shape temp )
	{
		assignPic( temp );
		bottomShapes.add( temp );
		temp.editorPage = page;

		picsX += 2 * picsW;
		if (picsX + 2 * picsW >= EDITOR_RESOLUTION_X
				&& picsY < EDITOR_RESOLUTION_Y)
		{
			picsX = 50;
			if ( lowRes )
				picsY += picsH + 15;
			else
				picsY += picsH + 50;

			if ( picsY + picsH >= EDITOR_RESOLUTION_Y )
			{
				if ( lowRes )
					picsY = BOTTOM + 15;
				else
					picsY = BOTTOM + 50;
				page++;
				MAX_PAGES++;
			}
		}
	}

	/* mouseIn()
	 * 		Checks if the mouse is in the level area
	 */
	private boolean mouseIn( int left, int right, int top, int bottom )
	{
		return mouseX + transX >= left && mouseX + transX <= right
				&& mouseY + transY > top && mouseY + transY < bottom;
	}

	/* mouseInput()
	 * 		Checks the mouse inputs and acts accordingly
	 */
	private void mouseInput()
	{
		if ( Mouse.isButtonDown(0) && mouseIn( LEFT, RIGHT, TOP, BOTTOM ) )
		{
			if ( !settingPartner && !settingStart )
			{
				shapes.add( current );

				current = getCurrShape();
				if ( current.name.equals("Cloud") )
					current.type = 1;
				assignPic( current );
			} 
			else if ( settingPartner )
			{
				selected.partner = getShape();
				if ( selected.partner != null )
				{
					selected.partnerX = selected.partner.x;
					selected.partnerY = selected.partner.y;
				}
				settingPartner = false;
				selected.selected = true;	//lololol	
			} 
			else if ( settingStart )
			{
				startX = mouseX;
				startY = mouseY;

				settingStart = false;
			}
			fixMouse();
		}

		// clicking the buttons. this is ugly because it can easily be screwed
		// up if the interface shifts around, but at least it's very easy to change
		// ----Start left side buttons

		// CHANGE THIS if you have problems clicking the buttons. Although, you really shouldn't.

		//-- Width and height
		if ( Mouse.isButtonDown(0)
				&& mouseIn( 0, LEFT, TOP + 3 * FONT_SIZE, TOP + 4 * FONT_SIZE ) )
		{
			inputValue = JOptionPane.showInputDialog("Enter a positive integer width.");
			
			if ( inputValue != null && !inputValue.equals("") )
				width = Integer.parseInt( inputValue );
			
			fixMouse();
		}
		if ( Mouse.isButtonDown(0)
				&& mouseIn( 0, LEFT, TOP + 4 * FONT_SIZE, TOP + 5 * FONT_SIZE ) )
		{
			inputValue = JOptionPane.showInputDialog("Enter a positive integer height.");
			
			if ( inputValue != null && !inputValue.equals("") )
				height = Integer.parseInt( inputValue );
			
			fixMouse();
		}

		// --- Ints
		if ( Mouse.isButtonDown(0)
				&& mouseIn( 0, 50, TOP + 8 * FONT_SIZE, TOP + 9 * FONT_SIZE ) )
			buttonCode = 1;			// i
		if ( Mouse.isButtonDown(0)
				&& mouseIn( 75, 175, TOP + 8 * FONT_SIZE, TOP + 9 * FONT_SIZE ) )
			buttonCode = 2;			// type
		if ( Mouse.isButtonDown(0)
				&& mouseIn( 0, 200, TOP + 9 * FONT_SIZE, TOP + 10 * FONT_SIZE ) )
		{
			inputValue = JOptionPane.showInputDialog("Enter a (double) movement speed. Positive = right, Negative = left.");
			
			if ( inputValue != null && !inputValue.equals("") )
				current.moveSpeed = Double.parseDouble( inputValue );
			
			fixMouse();
		}

		// --- Booleans

		// Moving
		if ( Mouse.isButtonDown(0)
				&& mouseIn( 0, LEFT - 70, TOP + 10 * FONT_SIZE + 5, TOP + 11
						* FONT_SIZE + 5 ) )
		{
			current.moving = !current.moving;
			fixMouse();
		}

		// Vert
		if ( Mouse.isButtonDown(0)
				&& mouseIn( 0, 125, TOP + 11 * FONT_SIZE + 5, TOP + 12
						* FONT_SIZE + 5 ) )
		{
			current.vert = !current.vert;
			fixMouse();
		}
		
		// On TODO: useful?
		if ( Mouse.isButtonDown(0)
				&& mouseIn( 135, 245, TOP + 11 * FONT_SIZE + 5, TOP + 12
						* FONT_SIZE + 5 ) )
		{
			current.on = !current.on;
			fixMouse();
		}
		
		// Weak
		if ( Mouse.isButtonDown(0)
				&& mouseIn( 0, 245, TOP + 12 * FONT_SIZE + 5, TOP + 13
						* FONT_SIZE + 5 ) )
		{
			current.weak = !current.weak;
			fixMouse();
		}
		
		// Nothing? used to be sky.. TODO: remove
		if ( Mouse.isButtonDown(0)
				&& mouseIn(0, LEFT - 20, TOP + 14 * FONT_SIZE + 5, TOP + 15
						* FONT_SIZE + 5 ) )
		{
			fixMouse();
		}
		
		// Set starting position. TODO: does this work?
		if ( Mouse.isButtonDown(0)
				&& mouseIn(0, LEFT - 50, TOP + 16 * FONT_SIZE + 5, TOP + 17
						* FONT_SIZE + 5 ) )
		{
			JOptionPane.showMessageDialog( null, "Click to set start position" );
			settingStart = true;
			fixMouse();
		}
		
		// Draw order
		if ( Mouse.isButtonDown(0)
				&& mouseIn( 0, LEFT - 50, TOP + 17 * FONT_SIZE + 5, TOP + 18
						* FONT_SIZE + 5 ) )
		{
			inputValue = JOptionPane
					.showInputDialog("Draw order? 1 = Background,"
							+ "2 = Scenery, 3 = Foreground, 4 = In front of bossgreed");
			currentOrder = Integer.parseInt( inputValue );
			fixMouse();
		}

		//-----End left side buttons

		//----Right side buttons
		
		// Initiate partner
		if ( Mouse.isButtonDown(0)
				&& mouseIn( RIGHT, EDITOR_RESOLUTION_X, TOP + 3 * FONT_SIZE, TOP
						+ 4 * FONT_SIZE ) )
		{
			settingPartner = true;
		}

		// Select partner shape
		if ( Mouse.isButtonDown(0)
				&& selected != null
				&& mouseIn( RIGHT + 5, EDITOR_RESOLUTION_X, TOP + 7 * FONT_SIZE,
						TOP + 10 * FONT_SIZE ) )
		{
			if ( selected.partner != null )
			{
				if ( mouseIn(RIGHT + 5, EDITOR_RESOLUTION_X,
						TOP + 7 * FONT_SIZE, TOP + 8 * FONT_SIZE ) )
				{
					selected.partner.action = 1;
					selected.partner.visible = false;
				}
				
				if ( mouseIn( RIGHT + 5, EDITOR_RESOLUTION_X,
						TOP + 8 * FONT_SIZE, TOP + 9 * FONT_SIZE ) )
					selected.partner.action = 2;
				
				if ( mouseIn( RIGHT + 5, EDITOR_RESOLUTION_X,
						TOP + 9 * FONT_SIZE, TOP + 10 * FONT_SIZE ) )
				{
					movementStuff();
				}
			}
		}
		//-----End right side buttons

		// Select the shape in the level pane
		if ( Mouse.isButtonDown(0) && mouseY + transY >= BOTTOM )
		{
			Shape temp = getShape();
			if (temp != null)
				current = temp;
		}

		// Set the new shape from the menu
		if ( Mouse.isButtonDown(1) && mouseY + transY <= BOTTOM
				&& mouseY + transY >= TOP )
		{
			if ( settingPartner )
				settingPartner = false;
			selected = getShape();
		}
	}

	/* input()
	 * 		Handles all inputs
	 */
	private void input()
	{
		if ( ( Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)	// Save
				|| Keyboard.isKeyDown(Keyboard.KEY_RCONTROL)
				|| Keyboard.isKeyDown(Keyboard.KEY_LMETA) || Keyboard
					.isKeyDown(Keyboard.KEY_RMETA) )
				&& Keyboard.isKeyDown(Keyboard.KEY_S) )
		{
			fixKeyboard();
			save( shapes );
		} 
		else if ( ( Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) // Load
				|| Keyboard.isKeyDown(Keyboard.KEY_RCONTROL)
				|| Keyboard.isKeyDown(Keyboard.KEY_LMETA) || Keyboard
					.isKeyDown(Keyboard.KEY_RMETA) )
				&& Keyboard.isKeyDown(Keyboard.KEY_O) )
		{
			fixKeyboard();
			load( shapes );
		} 
		else
		{
			// CHANGE THIS if you want to change the button mapping,
			// or just add new functionality

			if ( Keyboard.isKeyDown(Keyboard.KEY_W) )
				transY += CAMERA_SCROLL_SPEED;
			if ( Keyboard.isKeyDown(Keyboard.KEY_S) )
				transY -= CAMERA_SCROLL_SPEED;
			if ( Keyboard.isKeyDown(Keyboard.KEY_A) )
				transX += CAMERA_SCROLL_SPEED;
			if ( Keyboard.isKeyDown(Keyboard.KEY_D) )
				transX -= CAMERA_SCROLL_SPEED;

			if ( Keyboard.isKeyDown(Keyboard.KEY_I) && (height - THICKNESS) >= 1 )
				height -= THICKNESS;
			if ( Keyboard.isKeyDown(Keyboard.KEY_K) )
				height += THICKNESS;
			if ( Keyboard.isKeyDown(Keyboard.KEY_L) )
				width += THICKNESS;
			if ( Keyboard.isKeyDown(Keyboard.KEY_J) && (width - THICKNESS) >= 1 )
				width -= THICKNESS;

			if ( Keyboard.isKeyDown(Keyboard.KEY_COMMA )
					&& GRID_SIZE > MIN_GRID_SIZE)
				GRID_SIZE--;
			if ( Keyboard.isKeyDown(Keyboard.KEY_PERIOD )
					&& GRID_SIZE < MAX_GRID_SIZE)
				GRID_SIZE++;

			if ( Keyboard.isKeyDown(Keyboard.KEY_T) )
			{
				drawGrid = !drawGrid;
				fixKeyboard();
			}
			
			// Cycles through types TODO: should weak/vert be listed as types?
			if ( Keyboard.isKeyDown(Keyboard.KEY_RBRACKET) )
			{
	//			if (buttonCode == 1)	TODO: what was the purpose of i?
		//			current.i++;
				if (buttonCode == 2 && current.type < MAX_TYPE_SIZE) {
					current.type++;
					currentType++;
				}

				fixKeyboard();
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_LBRACKET))
			{
	//			if (buttonCode == 1 && current.i > 0)
	//				current.i--;
				if (buttonCode == 2 && current.type > MIN_TYPE_SIZE) {
					current.type--;
					currentType--;
				}

				fixKeyboard();
			}

			// Deletes a shape
			if ( Keyboard.isKeyDown(Keyboard.KEY_DELETE)
					|| Keyboard.isKeyDown(Keyboard.KEY_BACK) )
				delete();

			//TODO: M does something?
			if ( Keyboard.isKeyDown(Keyboard.KEY_M) && selected != null )
			{
				current = selected;
				current.selected = false;
				currShape = current.name;
				selected = null;
				width = (int) current.getWidth();
				height = (int) current.getHeight();
			}

			mouseLockX = false;
			mouseLockY = false;

			// Lock the x or y axis to allow for more precise shape placement
			if ( Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) )
			{
				mouseLockX = true;
			}
			
			if ( Keyboard.isKeyDown(Keyboard.KEY_RSHIFT) )
			{
				mouseLockY = true;
			}

			// Cycles through pages of icons on the bottom TODO: is this necessary?
			if ( Keyboard.isKeyDown(Keyboard.KEY_MINUS) && page > 1 )
			{
				page--;
				fixKeyboard();
			}
			if ( Keyboard.isKeyDown(Keyboard.KEY_EQUALS) && page < MAX_PAGES )
			{
				page++;
				fixKeyboard();
			}
		}
	}
	
	/* fixMouse()
	 * 		destroy and create mouse because of constant clicking? TODO: gotta be a way to toggle in LWJGL lets find it already
	 */
	public void fixMouse()
	{
		Mouse.destroy();
		try
		{
			Mouse.create();
		}
		catch (LWJGLException e)
		{
			e.printStackTrace();
		}
	}

	/* fixKeyboard()
	 * 		destroy and create mouse because of constant clicking? TODO: gotta be a way to toggle in LWJGL lets find it already
	 */
	public void fixKeyboard()
	{
		Keyboard.destroy();
		try
		{
			Keyboard.create();
		}
		catch (LWJGLException e)
		{
			e.printStackTrace();
		}
	}

	/* mouse()
	 * 		Retrieve the "true" coordinates of the mouse.
	 */
	private void mouse()
	{
		if ( !mouseLockX )
			mouseX = Mouse.getX() - transX;
		if ( !mouseLockY )
			mouseY = EDITOR_RESOLUTION_Y - Mouse.getY() - 1 - transY;
	}

	/* movementStuff()
	 * 		Handles moving of partners
	 */
	private void movementStuff()
	{
		selected.partner.upDown = true;
		selected.partner.action = 3;
		selected.partner.downRight = true;

		inputValue = JOptionPane.showInputDialog("Move which way? (1 - Up/Down, else - Left/Right)");
		if ( inputValue != null && !inputValue.equals("") )
		{
			if ( !inputValue.equals("1") )
				selected.partner.upDown = false;
			
			// Moving up and down
			if ( selected.partner.upDown )
			{
				inputValue = JOptionPane.showInputDialog("Current bottom of the piece: "
								+ (selected.partner.y + selected.partner.height - FUDGE_Y)
								+ ", enter end Y.");
				if ( inputValue != null && !inputValue.equals("") )
				{
					selected.partner.endPos = Double.parseDouble(inputValue)
							+ FUDGE_Y - selected.partner.height;
					selected.partner.startPos = selected.partner.y;
				}
			}
			
			// Moving left and right
			if ( !selected.partner.upDown )
			{
				inputValue = JOptionPane.showInputDialog("Current right side of the piece: "
								+ (selected.partner.x + selected.partner.width - FUDGE_X)
								+ ", enter end X.");
				if ( inputValue != null && !inputValue.equals("") )
				{
					selected.partner.endPos = Double.parseDouble(inputValue)
							+ FUDGE_X - selected.partner.width;
					selected.partner.startPos = selected.partner.x;
				}
			}
			
			inputValue = JOptionPane.showInputDialog("Choose a (double) speed");
			
			// Set the speed at which the partner will move
			if ( inputValue != null && !inputValue.equals("") )
				selected.partner.moveSpeed = Double.parseDouble(inputValue);

			if ( selected.partner.endPos < selected.partner.startPos )
			{
				// switch them
				double temp = selected.partner.endPos;
				double temp2 = selected.partner.startPos;
				selected.partner.endPos = temp2;
				selected.partner.startPos = temp;
				System.out.println("SWITCH");
				selected.partner.downRight = false;
			}
		}
	}

	/* delete()
	 * 		Deletes the shape TODO: improve this
	 */
	public void delete()
	{
		// for some reason you have to do it like this,
		// you can't just shapes.remove(selected)
		Shape temp = new Bat( 0, 0, 0, 0, textureMap.get("cliffGrassDown") );
		for ( Shape shape : shapes )
			if ( shape.selected )
				temp = shape;

		if ( temp.selected )
			shapes.remove(temp);

		selected = null;
	}

	/* drawBoundary()
	 * 		This draws a black frame around the screen size to create the 'window-in-window' illusion
	 */
	private void drawBoundary()
	{

		glColor4f( 0f, 0f, 0f, 1f );
		glRectd(-transX, -transY - CAMERA_SCROLL_SPEED, EDITOR_RESOLUTION_X
				- transX, TOP - 1 - transY);	// TOP
		glRectd(-transX - CAMERA_SCROLL_SPEED, -transY, LEFT - 1 - transX,
				EDITOR_RESOLUTION_Y - transY);	// left
		glRectd(-transX, BOTTOM + 1 - transY, EDITOR_RESOLUTION_X - transX,
				EDITOR_RESOLUTION_Y - transY + CAMERA_SCROLL_SPEED);	// BOTTOM
		glRectd(RIGHT + 1 - transX, -transY, EDITOR_RESOLUTION_X - transX
				+ CAMERA_SCROLL_SPEED, EDITOR_RESOLUTION_Y - transY);	// RIGHT
	}

	/* drawGrid()
	 * 		Draw the grid
	 */
	private void drawGrid()
	{
		
		glBegin(GL_LINES);

		glColor4f( 1.0f, 1.0f, 1.0f, .75f );
		for ( int i = 0; TOP + i < BOTTOM; i += GRID_SIZE )
		{
			glVertex2f(LEFT, TOP + i);
			glVertex2f(RIGHT, TOP + i);
		}
		for ( int i = 0; LEFT + i < RIGHT; i += GRID_SIZE )
		{
			glVertex2f(LEFT + i, TOP);
			glVertex2f(LEFT + i, BOTTOM);
		}
		glDisable(GL_BLEND);
		glEnd();
	}

	/* drawText()
	 * 		Draws all unicode text
	 */
	private void drawText()
	{
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

		// CHANGE THIS if you want to add more text/debug stuff to the screen

		//---LEFT SIDE----

		uniFont.drawString(5, TOP, "Mouse: " + (mouseX + transX) + ","
				+ (mouseY + transY));
		uniFont.drawString(5, TOP + FONT_SIZE, "Game x,y: "
				+ (mouseX - LEFT - 1) + "," + (mouseY - TOP - 2));
		uniFont.drawString(5, TOP + 2 * FONT_SIZE, "CurrShape: " + currShape);
		uniFont.drawString(5, TOP + 3 * FONT_SIZE, "Width: " + width);
		uniFont.drawString(5, TOP + 4 * FONT_SIZE, "Height: " + height);
		uniFont.drawString(5, TOP + 5 * FONT_SIZE, "Grid size: " + GRID_SIZE);

		uniFont.drawString(5, TOP + 7 * FONT_SIZE, "---Instance variables---");

	/*	String iString = "i: ", typeString = "type: ";

		if ( buttonCode == 1 )
			iString = "I= ";
		if ( buttonCode == 2 )
			typeString = "TYPE= ";

		uniFont.drawString(5, TOP + 8 * FONT_SIZE, iString + current.i
				+ "     " + typeString + currentType);
	*/
		uniFont.drawString(5, TOP + 9 * FONT_SIZE, "moveSpeed: "
				+ current.moveSpeed);
		uniFont.drawString(5, TOP + 10 * FONT_SIZE, "moving: " + current.moving);

		uniFont.drawString(5, TOP + 11 * FONT_SIZE, "vert: " + current.vert
				+ "   on: " + current.on);
		
		uniFont.drawString(5, TOP + 12 * FONT_SIZE, "weak: " + current.weak);
		uniFont.drawString(5, TOP + 13 * FONT_SIZE, "---------------------");
		uniFont.drawString(5, TOP + 14 * FONT_SIZE, "Sky hex: 0x" + skyHex);
		uniFont.drawString(5, TOP + 15 * FONT_SIZE, "File name: " + fileName);
		uniFont.drawString(5, TOP + 16 * FONT_SIZE, "Start x,y: "
				+ (startX - FUDGE_X) + "," + (startY - FUDGE_Y));
		uniFont.drawString(5, TOP + 17 * FONT_SIZE, "Draw Order (1-4): "
				+ currentOrder);

		//---END LEFT SIDE

		//---RIGHT SIDE

		if ( selected != null )
		{
			uniFont.drawString(RIGHT + 5, TOP, "Selected:" + selected.name);
			uniFont.drawString(RIGHT + 5, TOP + FONT_SIZE, "x,y pos: "
					+ (selected.x - FUDGE_X) + "," + (selected.y - FUDGE_Y));

			if ( !settingPartner )
				uniFont.drawString(RIGHT + 5, TOP + 3 * FONT_SIZE,
						"*SET PARTNER*");
			else
				uniFont.drawString(RIGHT + 5, TOP + 3 * FONT_SIZE,
						"Click on partner");
			if ( selected.partner == null )
				uniFont.drawString(RIGHT + 5, TOP + 4 * FONT_SIZE, "No partner");
			else
			{
				uniFont.drawString(RIGHT + 5, TOP + 4 * FONT_SIZE,
						"Has partner");
				uniFont.drawString(RIGHT + 5, TOP + 6 * FONT_SIZE, "Action:");
				uniFont.drawString(RIGHT + 5, TOP + 7 * FONT_SIZE,
						"        Appear");
				uniFont.drawString(RIGHT + 5, TOP + 8 * FONT_SIZE,
						"        Disappear");
				uniFont.drawString(RIGHT + 5, TOP + 9 * FONT_SIZE,
						"        Start moving");

				if ( selected.partner.action > 0 )
					uniFont.drawString(RIGHT + 5, TOP
							+ (selected.partner.action + 6) * FONT_SIZE,
							"  -->");
			}
		}

		if ( pointerX > 0 )
			uniFont.drawString(pointerX, pointerY, "^");

		// uniFont.drawString(55, 10, "Button!");
		// more text here

		GL11.glDisable(GL11.GL_TEXTURE_2D);
		glDisable(GL_BLEND);
	}

	/* load()
	 * 		Loads a previously constructed level
	 */
	public void load( List<Shape> shapes )
	{
		fileName = JOptionPane
				.showInputDialog("Enter the filename to load please: ");
		if ( fileName != null && !fileName.equals("") )
		{
			try
			{
				shapes.clear();
				ObjectInputStream IS = new ObjectInputStream(
						new FileInputStream( "levels/" + fileName ) );

				int size = IS.readInt();

				for ( int i = 0; i < size; i++ )
				{
					int code = IS.readInt();
					Shape temp = Shape.load(IS, code, null);
					assignPic(temp);
					shapes.add(temp);
				}
				startX = IS.readFloat();
				startY = IS.readFloat();

				transX = GAME_RESOLUTION_X / 2;
				transY = GAME_RESOLUTION_Y / 2;

				IS.close();

				//TODO: learn more about partners, and if they have a use, this is silly :P
				for ( Shape shape : shapes )
					if ( shape.partnerX != 0 && shape.partnerY != 0 )
						for ( Shape shaper : shapes )
							if ( shape.partnerX == shaper.x
									&& shape.partnerY == shaper.y )
								shape.partner = shaper;

				System.out.println("Loaded!");
			}
			catch ( FileNotFoundException e )
			{
				e.printStackTrace();
			}
			catch ( IOException e )
			{
				e.printStackTrace();
			}
		}
	}

	/* save()
	 * 		Saves the current level
	 */
	public void save( List<Shape> shapes )
	{
		fileName = JOptionPane
				.showInputDialog("Enter the desired filename please: ");
		if ( fileName != null && !fileName.equals("") )
		{
			try
			{
				ObjectOutputStream OS = new ObjectOutputStream(
						new FileOutputStream("levels/" + fileName));
				OS.writeInt(shapes.size());

				for (Shape shape : shapes)
				{
					Shape.save(OS, shape);
				}

				OS.writeFloat(startX);
				OS.writeFloat(startY);

				OS.close();
				System.out.println("Saved!");
			}
			catch ( FileNotFoundException e )
			{
				e.printStackTrace();
			}
			catch ( IOException e )
			{
				e.printStackTrace();
			}
		}
	}

	/* translate()
	 * 		Moves the level window appropriately
	 */
	private void translate()
	{
		glPushMatrix();
		glTranslatef( transX, 0, 0 );
		glTranslatef( 0, transY, 0 );
	}

	/* initGL()
	 * 		Standard GL initialization 
	 */
	private void initGL()
	{
		try
		{
			Display.setDisplayMode(new DisplayMode(EDITOR_RESOLUTION_X,
					EDITOR_RESOLUTION_Y));
			Display.setTitle("Level Editor");
			Display.create();
		}
		catch ( LWJGLException e )
		{
			e.printStackTrace();
		}

		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0, EDITOR_RESOLUTION_X, EDITOR_RESOLUTION_Y, 0, -1, 1);
		glMatrixMode(GL_MODELVIEW);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	}

	/* initFonts()
	 * 	 	This takes time... adds textures for plain text used in conversations
	 *		TODO: can we get rid of suppress warnings
	 */
	@SuppressWarnings("unchecked")
	private void initFonts()
	{

		Font awtFont = new Font("", Font.PLAIN, FONT_SIZE);

		uniFont = new UnicodeFont(awtFont, FONT_SIZE, false, false);
		uniFont.addAsciiGlyphs();
		uniFont.addGlyphs( 400, 600 );           // Setting the unicode Range
		uniFont.getEffects().add(new ColorEffect(java.awt.Color.white));
		try
		{
			uniFont.loadGlyphs();
		}
		catch ( SlickException e )
		{
		}
	}

	/* loadTexture()
	 * 		Use TextureLoader to load all of the games images
	 */
	public static Texture loadTexture(String key)
	{
		try
		{
			return TextureLoader.getTexture("png", new FileInputStream(
					new File("res/img/" + key + ".png")));
		}
		catch ( FileNotFoundException e )
		{

			e.printStackTrace();
		}
		catch ( IOException e )
		{

			e.printStackTrace();
		}
		return null;

	}

	public static void main(String [] args)
	{
		new LevelEditor();
	}

}
