package game;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glTranslatef;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;

import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.AudioLoader;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.util.ResourceLoader;

import entities.Action;
import entities.Arrow;
import entities.Box;
import entities.Clock;
import entities.Coin;
import entities.PauseFade;
import entities.Shape;
import entities.Text;

public class GameOn 
{
	// Macros
	private static final int WIDTH = 1024; // game window resolution
	private static final int HEIGHT = 728;
	private static final int WINNING_ANIMATION_TIME = 3000;
	private static final int DYING_ANIMATION_TIME = 1500;
	private static final int WALL_JUMP_TIME = 400;
	private static final int MAX_GRAV = 15;
	
	
	// Bookkeeping
	public int coinCount = 0;
	public boolean bPassed = false;
	private boolean bFinished = false, bPaused = false;
	private float translateX = WIDTH / 2, translateY = HEIGHT * 2 / 5;
	private float startX = translateX, startY = translateY;
	private long startPause, endPause,  timeDifference = 0L;
	public long startTime, winTime = -1;
	private List<Shape> shapes = new ArrayList<Shape>(20);
	private String fileName;
	private static Map<String,Texture[]> textureMap;
	private static Map<Integer,String> shapeMap = createShapeMap();
	private Coin theCoin;
	private Text theX, theColon, coinCount1, coinCount2, time1, time2, time3, time4;
	private Clock theClock;
	
	// Movement
	private Box thePlayer;
	private long thwompTime = 0;
	private long jumpTime = 0;
	private boolean noLeft = false, noRight = false, rightWall = false, leftWall = false;
	private Action ground = null;
	private Action wall = null;
	
	// RUNNING, JUMPING, SLIDING - ADJUST THESE ANDY
	private double runSpeed;
	private boolean running, moving, pressingJump;
	private int failCount = 0; 
	private double MAX_FAILS = 5; 				// this is to determine when the player has let go of the jump key
	private double MOVEMENT_AMOUNT = 5; 		// max walk speed
	private double CHANGE_DIR_SPEED = 1; 		// deceleration amount when changing directions
	private double RUN_ACCEL_SPEED = .15; 		// how fast your speed goes up when running
	private double INIT_ACCEL_SPEED = .5; 		// how fast you raise up to the normal MOVEMENT_AMOUNT
	private double DEACCEL_SPEED = .30; 		// how fast you slow down if you're above MOVEMENT_AMOUNT and let go of run, but keep moving
	private double RUN_SLIDE_AMOUNT = .25; 		// deceleration if you're running and stop moving
	private double WALK_SLIDE_AMOUNT = .25; 	// deceleration if you're walking and stop moving
	
	// Jumping and gravity
	// this is the order of jumping: Press Jump to START_SLOWDOWN1_TIME
	// START_SLOWDOWN1_TIME to START_SLOWDOWN2_TIME
	// START_SLOWDOWN2_TIME to HANGTIME
	// HANGTIME to HANGTIME_RUN (if the player is sprinting)
	//
	// The corresponding gravSpeeds are INIT_JUMP_SPEED for the first period,
	// SLOWDOWN1_JUMP_SPEED for the second period, SLOWDOWN2_JUMP_SPEED for the
	// third and fourth.
	private double INIT_JUMP_SPEED = 5.1; 		// the initial speed that you start rising with
	private double initGravSpeed = INIT_JUMP_SPEED;
	private static double gravSpeed = 5.1;
	private double BONUS_RUN_AMOUNT = .3;
	
	// Paused
	private int P_row = 1;
	private PauseFade theFade;
	private Text PausedText,ResumeText, RestartText, ExitText;
	private Arrow theArrow;

	// Sounds!
	private Audio jumpSound, coinSound;
	public static Audio gravSound, landingSound;

	// Show hitboxes
	private boolean displayHitbox = false;

	/* GameOn
	 * 		Main function, loads the level, sounds, kicks everything off then runs
	 * 		the main game loop
	 */
	public GameOn( String level, Map<String, Texture[]> textMap ) 
	{
		
		// Initialization process
		textureMap = textMap;
		fileName = level; // make the filename the level name so restart() functions properly
		initSound();
		load( level ); 		// load the level passed in
		initTextObjects();
		
		startTime = getTime();

		// Main game loop
		while ( !Display.isCloseRequested() && !bFinished ) 
		{
			glClear( GL_COLOR_BUFFER_BIT );
			glPushMatrix();

				glTranslatef( translateX, 0, 0 );
				glTranslatef( 0, translateY, 0 );
				String playerTextureString = null;
				if ( !thePlayer.dying )
					playerTextureString = input();										// get the users input
				
				playerTextureString = update( playerTextureString );					// update any/all movement happening in the game
				thePlayer.setTextureArray ( textureMap.get( playerTextureString ) );
				render();																// draw the game
				
			glPopMatrix();
			
			gamePaused( bPaused );														// decide what is statically shown if the game is paused
			
			staticRender();																// static draw
			
			Display.update();
			Display.sync(60);
		}

	}
	
	/* createShapeMap()
	 * 		Create and initialize our mappings from code -> shape to 
	*/
	private static Map<Integer,String> createShapeMap()
	{
		
		HashMap<Integer,String> tempMap = new HashMap<Integer,String>();
		
		tempMap.put( 3, "cliffGrassDown" );	//TODO: Switch this one
		tempMap.put( 4, "bagDownRight" );	//TODO: Switch this one
		tempMap.put( 7, "coin" );
		tempMap.put( 8, "deadHorizontal" );	//TODO: Switch this one
		tempMap.put( 10, "doorBasic" );
		tempMap.put( 11, "gravflip" );
		tempMap.put( 13, "ice" );			//TODO: Switch on this one
		
		return ( Collections.unmodifiableMap(tempMap) );
		
	}
	
	/* initSound
	 * 		Loads the sound effects and music
	 * 		TODO: add more sounds
	 */
	private void initSound() 
	{
		try 
		{
			jumpSound = AudioLoader.getAudio("WAV",
					ResourceLoader.getResourceAsStream("res/sound/jump.wav"));		// jumping
			coinSound = AudioLoader.getAudio("WAV",
					ResourceLoader.getResourceAsStream("res/sound/coin.wav"));		// grabbing a coin
			gravSound = AudioLoader.getAudio("WAV",
					ResourceLoader.getResourceAsStream("res/sound/GravFip.wav"));	// gravity flipper
			landingSound = AudioLoader.getAudio("WAV",
					ResourceLoader.getResourceAsStream("res/sound/landing.wav"));	// TODO: this isn't used
			
			
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}

	}
	
	/* load
	 * 		reads a level file and creates all the level objects from it
	 * 		TODO: clean this along with the level editor, only put necessities in the level file
	 */
	public void load(String filename) 
	{
		try 
		{
			shapes.clear(); // clear the array of the old shapes (if any)
			ObjectInputStream IS = new ObjectInputStream( new FileInputStream("levels/" + filename) );
			int size = IS.readInt(); // first thing is an int with the amount of
										// shapes
			double r = IS.readDouble(); // then the sky's rgb						TODO: REMOVE SKY COLOR
			double g = IS.readDouble();
			double b = IS.readDouble();

			// Here is where we read in, shape by shape, from the level file
			for ( int i = 0; i < size; i++ ) 
			{
				int code = IS.readInt(); // first there's a shapeCode to
											// determine what to load
				Shape temp = Shape.load( IS, code, textureMap.get( shapeMap.get( code ) ) ); // this static method in
				
				// Here we have to adjust the texture array in some way
				
				if ( code == 3 && temp.weak )
				{
					if ( temp.weak )
						temp.setTextureArray( textureMap.get( "cliffGrassDownBreak" ) );
					else if ( temp.vert )
						temp.setTextureArray( textureMap.get( "cliffGrassUp") );
				}
					
				shapes.add( temp ); 		// add it to the shape array
			}

			startX = IS.readFloat(); // lastly we have the start position			TODO: does this need to be configurable?
			startY = IS.readFloat();
			IS.close(); // gotta close the file stream

			// make the player
			thePlayer = new Box( startX, startY - 30, 32, 32, textureMap.get("bagDownRight") );						//	TODO: macros for sizes
			//shapes.add(player);												//  TODO: is there a benefit to add the player as a shape?

			// this loop goes through and links all the partners, since they
			// weren't strictly saved											//	TODO: look into partners and its benefits, make this better
			for (Shape shape : shapes) 
			{
				if (shape.partnerX != 0 || shape.partnerY != 0) // note that if the partner's x,y is 0,0 for some reason, this won't work
				{
					for (Shape shaper : shapes) 
					{
						if (shape.partnerX == shaper.x
								&& shape.partnerY == shaper.y) 
						{
							shape.partner = shaper;
							if (shape == shaper && shape.action == 3) // if the shape is partners with itself and the action is moving, then start moving 
								shape.action();
						}
					}
				}
			}
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	/* initTextObjects
	 * 		Initializes all the menu and text objects shown during the game
	 * 		// TODO: macros for sizes
	 */
	private void initTextObjects()
	{
		theCoin = new Coin ( 2 , -10, 36, 72, textureMap.get("coin") );
		theX = new Text (38 , 25, 36 , 36, textureMap.get("textX") );
		coinCount1 = new Text( 57, 3, 48, 48, textureMap.get("text0") );
		coinCount2 = new Text( 92, 3, 48, 48, textureMap.get("text0") );
		theClock = new Clock( 300, 3, 48, 48, textureMap.get("clock") );
		time1 = new Text( 350, 3, 48, 48, textureMap.get("text0") );
		time2 = new Text( 395, 3, 48, 48, textureMap.get("text0") );
		time3 = new Text( 440, 3, 48, 48, textureMap.get("text0") );
		time4 = new Text( 485, 3, 48, 48, textureMap.get("text0") );
		theColon = new Text( 418, -3, 48, 48, textureMap.get("text0") );
		
		theFade = new PauseFade( 0, 0, 1024, 728, textureMap.get("pause") );
		PausedText = new Text( 320, 150, 256*1.5, 96*1.5, textureMap.get("textPaused") );
		ResumeText = new Text( 396, 280, 256, 96, textureMap.get("textResume") );
		RestartText = new Text( 278, 380, 512, 48, textureMap.get("textRestart") );
		ExitText = new Text( 399, 470, 128, 48, textureMap.get("textExit") );
		theArrow = new Arrow( 275, 300, 100, 48, textureMap.get("arrowRight") );
		
	}
	
	/* getTime
	 * 		Returns the current system time
	 * 
	 */
	private long getTime() 
	{
		return ( Sys.getTime() * 1000 / Sys.getTimerResolution() );
	}
	
	/* input
	 * 		Main input loop, handles all actions made by the user
	 * 
	 */
	public String input() 
	{
		String playerTextureString = null;
		// If the game is paused, navigate the menu
		if ( bPaused )
		{
			if ( Keyboard.isKeyDown( Keyboard.KEY_UP ) && P_row > 1 )
			{
				P_row--;
				theArrow.setY( theArrow.getY() + 85 );
				fixKeyboard();
				
			} 
			else if ( Keyboard.isKeyDown( Keyboard.KEY_DOWN ) && P_row < 3 )
			{
				P_row++;
				theArrow.setY( theArrow.getY() - 85 );
				fixKeyboard();
			}
			else if ( Keyboard.isKeyDown( Keyboard.KEY_RETURN ) )
			{
				if( P_row == 1)
				{
					// The game has been unpaused, so correct the time
					bPaused = false;
					endPause = getTime();
					timeDifference = endPause - startPause;
					startTime += timeDifference;
					fixKeyboard();
				}
				else if ( P_row == 2)
				{
					restart();
					fixKeyboard();
				}
				else if ( P_row == 3)
				{
					restart(); // TODO: this is quit to main menu...
				}
			}
		}
		else
		{
			// Game is not paused let the user play, as long as they are not dead or winning
			if ( !thePlayer.winning && !thePlayer.dying )
			{
				if ( failCount++ > MAX_FAILS ) // if you've let go of jump for MAX_FAILS straight times,
					pressingJump = false; // then you're not pressing jump
		
				// Can't move while crashing TODO: move this up an if?
				if ( !thePlayer.crashing )
				{
					moving = false;
					
					// Press spacebar to jump, if you're on the ground, rising, or ready to wall jump and not thwomping
					if ( Keyboard.isKeyDown( Keyboard.KEY_SPACE )
							&& ( ( thePlayer.grounded || pressingJump || thePlayer.hitWall ) )
							&& !thePlayer.thwomping ) 
					{
							playerJump();
							
							if ( rightWall )
								noRight = false;
							
							if ( leftWall )
								noLeft = false;
							
							// TODO: necessary?
							rightWall = false;
							leftWall = false;
					}
					
					// Press down to thwomp, if you are in the air and not already thwomping
					if ( Keyboard.isKeyDown( Keyboard.KEY_DOWN ) && !thePlayer.grounded && !thePlayer.thwomping ) 
							playerTextureString = playerThwomp();
					
					// Press left to move left, as long as you are allowed, this will allow the player to move right if they 
					// could not do so before
					if ( Keyboard.isKeyDown( Keyboard.KEY_A )
							|| Keyboard.isKeyDown( Keyboard.KEY_LEFT )
							&& !noLeft ) 
					{
						move( -1 );
						noRight = false;
					}
					
					// Press right to move right, as long as you are allowed, this will allow the player to move left if they 
					// could not do so before
					if ( Keyboard.isKeyDown( Keyboard.KEY_D )
							|| Keyboard.isKeyDown( Keyboard.KEY_RIGHT )
							&& !noRight ) 
					{
						move( 1 );
						noLeft = false;
					}
				}
				
				// Press P or Esc to pause the game
				if ( Keyboard.isKeyDown( Keyboard.KEY_P ) || ( Keyboard.isKeyDown( Keyboard.KEY_ESCAPE ) ) )
				{
					bPaused = true;
					startPause = getTime();
					fixKeyboard();
				}
				
				// TODO: debugging remove this eventually
				if ( Keyboard.isKeyDown( Keyboard.KEY_R ) ) 
					restart();
				
				// TODO: debugging remove this eventually
				if ( Keyboard.isKeyDown( Keyboard.KEY_G ) ) 
				{
					thePlayer.gravityMod *= -1;
					coinSound.playAsSoundEffect(1, 1, false);
					fixKeyboard();
				}
		
				// TODO: debugging remove this eventually
				if ( Keyboard.isKeyDown( Keyboard.KEY_H ) ) 
				{
					displayHitbox = !displayHitbox;
					fixKeyboard();
				}
			}
		}
		return playerTextureString;
	}
	
	// TODO : can this be done in a cleaner way?
	/* fixKeyboard
	 * 		Destroys and creates a new keyboard to disallow for holding down keys
	 * 		when we don't want to let the user do that
	 */
	private void fixKeyboard() 
	{
		Keyboard.destroy();
		try 
		{
			Keyboard.create();
		} 
		catch ( LWJGLException e ) 
		{
			e.printStackTrace();
		}
	}
	
	/* restart
	 * 		If the player dies or restarts from the pause menu, restart the level from its 
	 * 		initial state
	 */
	private void restart() 
	{
		thePlayer.alive = true;
		thePlayer.setX( startX );
		thePlayer.setY( startY );
		translateX = WIDTH / 2;
		translateY = HEIGHT * 2 / 5;
		thePlayer.gravityMod = 1;
		thePlayer.onIce = false;
		startTime = getTime();
		load( fileName );
		fixKeyboard();
		runSpeed = 0;
		bFinished = false;
		bPaused = false;
	}
	
	/* playerJump
	 * 		Wraps the jump function, handles spawning textures when wall jumping and
	 * 		some variable handling to allow for smooth jumping
	 */
	private void playerJump()
	{
		pressingJump = true;
		failCount = 0; // TODO: rename this
		
		// If you have a ground piece that you are on, you may jump
		if ( thePlayer.groundPiece != null ) 
		{
			if ( !thePlayer.groundPiece.name.equals("Grav") && thePlayer.grounded
					&& !thePlayer.jumping ) 
			{
				jump();
			}
			
			if (runSpeed > MOVEMENT_AMOUNT) 
				initGravSpeed = INIT_JUMP_SPEED + BONUS_RUN_AMOUNT;
			else
				initGravSpeed = INIT_JUMP_SPEED;
		}
		// If you are hitting a wall, but not for too long, you may jump
		else if ( ( getTime() - jumpTime) < WALL_JUMP_TIME && thePlayer.hitWall ) 
		{
			
			jump();
			thePlayer.hitWall = false;
			if ( thePlayer.lastDIR == 1 )
				wall = new Action( thePlayer.getX() - thePlayer.getWidth(), thePlayer.getY() - thePlayer.getHeight() / 2, 64, 6, textureMap.get("wallJumpRight") );
			else
				wall = new Action( thePlayer.getX(), thePlayer.getY() - thePlayer.getHeight() / 2, 64, 64, textureMap.get("wallJumpLeft") );
			
			thePlayer.setX( thePlayer.getX() + 5 * thePlayer.lastDIR );
			runSpeed = 2;
		}
	}
	
	/* jump
	 * 		Sets the player to be jumping, plays the jumping sound
	 * 
	 */
	private void jump() 
	{
		thePlayer.jumping = true; // you're jumping
		jumpTime = getTime();
		thePlayer.grounded = false; // you're in the air
		gravSpeed = initGravSpeed; // bring on the gravity
		thePlayer.groundPiece = null; // you have no groundPiece
		jumpSound.playAsSoundEffect(1, 1, false); // play the jump sound!
	}
	
	/* playerThwomp
	 * 		Makes the player thwomp
	 * 
	 */
	private String playerThwomp()
	{
		thwompTime = getTime();
		thePlayer.thwomping = true;	// set thwomping to true
		thePlayer.timer = 0;
		gravSpeed = 15;				// max grav
		moving = false;
		thePlayer.jumping = false;
		fixKeyboard();
		thePlayer.stopAnimation = true;
		return "bag" + thePlayer.getGravity() + "Thwomp" + thePlayer.getDirection();
		
	}
	
	/* move
	 * 		This is called when the user moves left or right and moves boss greed accordingly
	 * 
	 */
	private void move( int dir ) 
	{
		
		// If you changed directions
		if ( dir != thePlayer.lastDIR ) 
		{
			runSpeed -= CHANGE_DIR_SPEED; // decelerate by CHANGE_DIR_SPEED
			// play slide sound
			if ( runSpeed < 0 ) // at this point, you've completely turned around
			{
				thePlayer.lastDIR = dir; // so change the player's direction and
				runSpeed = .25; // start with a little push in the right direction
			}
		}
		// Moving in the same direction
		else
		{
			moving = true; 

			// You're sprinting and not yet at top run speed // TODO: something about sprinting/boosting/ etc... unused currently
			if ( runSpeed < .6 * MOVEMENT_AMOUNT && running ) 
				runSpeed += RUN_ACCEL_SPEED; // acceleration amount

			// If you're walking and not yet at top walk speed
			if ( runSpeed < MOVEMENT_AMOUNT ) 
				runSpeed += INIT_ACCEL_SPEED; // initial acceleration amount

			// If you were running at beyond top walk speed, then decelerate	// TODO: more sprinting handling.. unused
			// to top walk speed
			if ( !running && runSpeed > MOVEMENT_AMOUNT ) 
			{
				runSpeed -= DEACCEL_SPEED; // when you let go of running

				if ( runSpeed < MOVEMENT_AMOUNT ) // to make sure you don't go below top walk speed
					runSpeed = MOVEMENT_AMOUNT;
			}
		}
	}
	
	
	/* update
	 * 		Main loop while in the game, first recognize if boss greed is on the ground
	 * 		next if he has bumped into a wall or ceiling, then handle horizontal and vertical movement
	 * 		Lastly see if the player has won or lost
	 */
	private String update( String playerTextureString ) 
	{
		
		if ( !thePlayer.dying )
		{
			// Check if the player is on the ground
			thePlayer.grounded = onGround();
			
			// Check if the player has intersected with anything, this is seperate from onGround because 
			// objects affect bg differently whether he is on the ground or not (i.e. walljumping)
			playerTextureString = collide( thePlayer.grounded, playerTextureString );
			
			// Handle anything dealing with horizontal movement of the player
			playerHorizontalMovement();
			
			// Handle anything dealing with vertical movement of the player
			playerTextureString = playerVerticalMovement( playerTextureString );
		}
		else
		{
			playerTextureString = "bagDead";
			thePlayer.stopAnimation = true;
		}
		
		// Handle winning and losing
		playerEndGame();
		
		// Camera logic
		if ( !thePlayer.winning ) 
		{
			if ( !thePlayer.dying )
			{
				translateY = HEIGHT / 2 - (int) thePlayer.getY();
				translateX = WIDTH / 2 - (int) thePlayer.getX();
			}
		}
		
		return playerTextureString;
	}
	
	/* onGround
	 * 		This method returns if the player is on the ground.
	 * 
	 */
	public boolean onGround() 
	{

		// mostly for grav, possibly could be used for a trampoline		TODO: can this be removed?
 		if ( thePlayer.bounce ) 
 		{
			thePlayer.bounce = false;
			thePlayer.groundPiece = null;
		}

		// If we're already on the ground, then just check that we're still on
		// top of the groundPiece
		if ( thePlayer.groundPiece != null && thePlayer.grounded ) 
		{
			// Just thwomped through a ground piece ( i hope )
			if ( !thePlayer.groundPiece.solid )
			{
				thePlayer.groundPiece = null;
				return false;
			}
			
			// Still walking on top of the previous groundpiece
			if ( ( ( thePlayer.groundPiece.getX() - thePlayer.getWidth() + 5 ) <= thePlayer.getX() 
					&& ( ( thePlayer.groundPiece.getX() + thePlayer.groundPiece.getWidth() ) >= thePlayer.getX() + 5 ) ) )
			{
				thePlayer.groundPiece.interact( thePlayer ); 
				
				// Make sure we are always directly on top of our groundpiece
				if ( thePlayer.gravityMod == 1 )
				{
					if ( thePlayer.getY() != thePlayer.groundPiece.getY() - thePlayer.getHeight() )
						thePlayer.setY( thePlayer.groundPiece.getY() - thePlayer.getHeight() );
				}
				else
				{
					if ( thePlayer.getY() != thePlayer.groundPiece.getY() + thePlayer.groundPiece.getHeight() )
						thePlayer.setY( thePlayer.groundPiece.getY() + thePlayer.groundPiece.getHeight() );
				}
				
				// We should always be free to move here
				noRight = false;
				noLeft = false;
				rightWall = false;
				leftWall = false;
				thePlayer.hitWall = false;
				thePlayer.stopAnimation = false;
				return true;
			}
		}

		// If we're not on the ground, time to see if we're on the ground
		// somewhere else
		if ( !thePlayer.dying )
		{
			for ( Shape shape : shapes ) 
			{
				// Check to make sure the shape is of a name we want to land on 		TODO: checking shape.names is ugly, there's a better way find it
				if ( thePlayer.intersects( shape ) )
				{
					shape.interact( thePlayer );
					
					if ( shape.visible && shape.solid && ( shape.name.equals("Bat") || shape.name.equals("Ice") ) )
					{
						// Set onice if we are onice. brilliant
						if ( shape.name.equals("Ice") )
						{
							thePlayer.onIce = true;
							
							/*	TODO: implement this change
							DEACCEL_SPEED = 3;
							WALK_SLIDE_AMOUNT = .02;
							RUN_SLIDE_AMOUNT = .02;
							INIT_ACCEL_SPEED = .4;
							*/
						}
						else
							thePlayer.onIce = false;
						
						if ( thePlayer.getY() < shape.y && thePlayer.gravityMod == 1 ) //normal case, normal grav
						{
							thePlayer.setY( shape.getY() - thePlayer.getHeight() );
							thePlayer.groundPiece = shape;
							shape.interact( thePlayer );
							ground = new Action( thePlayer.getX() - thePlayer.getWidth() / 2, thePlayer.getY() + thePlayer.getHeight() - 64, 64, 64, textureMap.get("dustUp") );		// TODO: macro these
							thePlayer.hitWall = false;
							thePlayer.stopAnimation = false;
							return true; 
						}
						else if ( thePlayer.getY() + thePlayer.getHeight() > shape.y + shape.height && thePlayer.gravityMod == -1 ) // normal case, flipped grav
						{
							thePlayer.setY( shape.getY() + shape.getHeight() ); 	// set height
							thePlayer.groundPiece = shape; 		// set ground piece
							shape.interact( thePlayer );				// interact with our groundpiece
							ground = new Action( thePlayer.getX() - thePlayer.getWidth() / 2, thePlayer.getY(), 64, 64, textureMap.get("dustDown") );	// TODO: macro these
							thePlayer.hitWall = false;				// we're on the ground, so we aren't wallsliding
							thePlayer.stopAnimation = false;
							return true;
						}
						
						thePlayer.thwomping = false;
					}
				}	
			}
		}
		thePlayer.groundPiece = null;
		
		return false; // you weren't grounded before, you're not on any of the
						// other shapes, you're not grounded
	}
	
	/* collide
	 * 		Collide will decide what to do when the player intersects with a shape, depending if the player
	 * 		is on the ground or not
	 */
	public String collide( boolean onGround, String returnString )
	{
		
		if ( !thePlayer.thwomping )
			returnString = "bag" + thePlayer.getGravity() + thePlayer.getDirection();
		else
			returnString = "bag" + thePlayer.getGravity() + "Thwomp" + thePlayer.getDirection();
		
		for ( Shape shape : shapes ) 
		{
			// Gem is the door, so we win!
			// Begin the winning animation
			if ( thePlayer.winning || shape.finished )
			{
				// skip this loop
			}
			else if ( thePlayer.intersects( shape ) ) 
			{
				
				if ( shape.name.equals("Gem") )
				{
					// thePlayer.setHeight( 0 );
					// thePlayer.setWidth( 0 );
					thePlayer.winning = true;
					shape.setTextureArray( textureMap.get("doorWin") );
					shape.stopAnimation = true;
					shape.interact( thePlayer );
					coinCount = thePlayer.goldCount;
					winTime = getTime();
					System.out.println("Coin count = : " + coinCount + "\n");
					
				}
				else if ( shape.name.equals("Coin") && shape.visible ) // We just collected a coin, stop drawing it and play the sound

				{
					coinSound.playAsSoundEffect(1, 1, false);
					shape.visible = false;
				}
				else if ( shape.visible && shape.solid ) // Here is the fun part, we want to handle hitting the sides and bottoms of normal platforms

				{
					// Here we hit the side of the platform, we intersected but not in the middle and not under
					// We will begin sliding down the wall, giving us time to wall jump
					// TODO: is gravity checked here?
					if ( shape.name.equals("Bat") && !thePlayer.inMiddle( shape ) && !thePlayer.under( shape ) ) 
					{
						if ( thePlayer.lastDIR == 1 )
							leftWall = true;
						else
							rightWall = true;
						
						thePlayer.lastDIR *= -1;
						thePlayer.setX( shape.getX() + thePlayer.getWidth() * thePlayer.lastDIR );
						returnString = "bag" + thePlayer.getGravity() + "Wall" + thePlayer.getDirection();
						jumpTime = getTime();
						noRight = true;
						noLeft = true;
						thePlayer.hitWall = true;
						runSpeed = 0;
						gravSpeed = .1;
						
					}
					else if ( thePlayer.under( shape ) )	//if we are under a platform, we'd like to get knocked back down
					{
						if ( thePlayer.gravityMod == 1 )
							thePlayer.setY( shape.getY() + shape.getHeight() );
						else
							thePlayer.setY( shape.getY() - thePlayer.getHeight() );
						
						thePlayer.jumping = false;
					}
					
					// So you dont immediately walljump by holding jump
					if ( !onGround )
						fixKeyboard();
				}
				
				// Move the shape if it moves	TODO: decide the value in this
				if ( shape.moving ) 
					handleMoving( shape );
			}
		}
		return returnString;
		
	}
		
	/* handleMoving
	 * 		in game shapes have the ability to move, the functionality is here
	 * 		TODO: may not even be used... but definitely optimization candidate
	 */
	private void handleMoving( Shape shape ) 
	{
		if ( shape.name.equals("Cloud") ) 
			shape.x += shape.moveSpeed;
		else 
		{
			// Pretty simple, solid logic 	TODO: downright -> leftright?
			if ( shape.upDown && shape.downRight ) 
			{
				shape.setY( shape.getY() + shape.moveSpeed );
				if ( thePlayer.groundPiece == shape ) // if the player is on it
					thePlayer.setY( thePlayer.getY() + shape.moveSpeed ); // then move him too
				if ( shape.getY() > shape.endPos ) // check if you hit the end
					shape.downRight = false; // and if so toggle the direction
			} 
			else if ( shape.upDown && !shape.downRight ) 
			{
				shape.setY( shape.getY() - shape.moveSpeed );
				if ( thePlayer.groundPiece == shape ) 
					thePlayer.setY( thePlayer.getY() - shape.moveSpeed ); 
				if ( shape.getY() < shape.startPos ) 
					shape.downRight = true;
			} 
			else if ( !shape.upDown && shape.downRight ) 
			{
				shape.setX( shape.getX() + shape.moveSpeed );
				if ( thePlayer.groundPiece == shape ) 
					thePlayer.setX( thePlayer.getX() + shape.moveSpeed );
				if ( shape.x > shape.endPos ) 
					shape.downRight = false;

			} 
			else if ( !shape.upDown && !shape.downRight ) 
			{
				shape.setX( shape.getX() - shape.moveSpeed );
				if ( thePlayer.groundPiece == shape )
					thePlayer.setX( thePlayer.getX() - shape.moveSpeed );
				if ( shape.x < shape.startPos ) 
					shape.downRight = true;
			}
		}
	}
	
	/* playerHorizontalMovement
	 * 		The user still moves from left to right outside of the move function, and that happens here
	 * 
	 */
	private void playerHorizontalMovement()
	{
		// Check our status of moving and adjust parameters accordingly
		if ( thePlayer.onIce ) 
		{
			if ( thePlayer.jumping || !thePlayer.grounded )
				CHANGE_DIR_SPEED = 0.001;
			else
				CHANGE_DIR_SPEED = .15; // decelerate by CHANGE_DIR_SPEED
			
			DEACCEL_SPEED = 3;
			WALK_SLIDE_AMOUNT = .02;
			RUN_SLIDE_AMOUNT = .02;
			INIT_ACCEL_SPEED = .4;
		} 
		else if ( thePlayer.jumping || !thePlayer.grounded )
			CHANGE_DIR_SPEED = 0.3;
		else
		{
			// Normal settings
			CHANGE_DIR_SPEED = 1;
			DEACCEL_SPEED = .20;
			WALK_SLIDE_AMOUNT = .3;
			RUN_SLIDE_AMOUNT = .3;
			INIT_ACCEL_SPEED = .5;
		}
		
		// If you pushed left or right, then move in that direction
		if ( moving && !thePlayer.dying ) 
			thePlayer.setX( thePlayer.getX() + runSpeed * thePlayer.lastDIR );
		else if ( !thePlayer.dying )
		{
			// Start sliding
			if ( runSpeed > 0 ) 
			{
				if ( running ) 
					runSpeed -= RUN_SLIDE_AMOUNT; // decelerate at this rate if running
				else 
					runSpeed -= WALK_SLIDE_AMOUNT; // and at this rate if not running
				
				thePlayer.setX( thePlayer.getX() + runSpeed * thePlayer.lastDIR ); // this is the actual slide
			}
		}
		return;
	}
	
	/* playerVerticalMovement
	 * 		All of the vertical physics after jumping are handled here
	 * 
	 */
	private String playerVerticalMovement( String playerTextureString )
	{
		// If you've been on the wall too long, crash
		if ( ( getTime() - jumpTime ) >= WALL_JUMP_TIME && !thePlayer.grounded && thePlayer.hitWall )
		{
			thePlayer.crashing = true;
			runSpeed = 0;
			playerTextureString = "bag" + thePlayer.getGravity() + "Crash" + thePlayer.getDirection();
		}

		// Slide down the wall
		if ( thePlayer.hitWall && !thePlayer.crashing )
		{
			gravSpeed += .1;
			thePlayer.setY( thePlayer.getY() + ( gravSpeed * thePlayer.gravityMod ) );
			playerTextureString = "bag" + thePlayer.getGravity() + "Wall" + thePlayer.getDirection();
			
		}
		else if ( thePlayer.jumping ) // Continue rising from the jump
		{
			if ( gravSpeed > - MAX_GRAV )
				gravSpeed -= .15;
			
			if ( gravSpeed <= 1 || !pressingJump )
				thePlayer.jumping = false;

			thePlayer.setY( thePlayer.getY() - ( gravSpeed * thePlayer.gravityMod ) );
		}
		else if ( !thePlayer.jumping && !thePlayer.grounded && ( getTime() - thwompTime) > 250 ) // Normal falling 
		{
			if ( gravSpeed < MAX_GRAV )
				gravSpeed += .4;		// TODO: macro this
			
			thePlayer.setY( thePlayer.getY() + ( gravSpeed * thePlayer.gravityMod ) );
			
		}
		else if ( thePlayer.grounded )
		{
			// On the ground, make sure our player's attributes are the way we want them
			thePlayer.thwomping = false;
			thePlayer.crashing = false;
			thePlayer.hitWall = false;
			
		}
		return playerTextureString;
	}
	
	/* playerEndGame
	 * 		If the player has won or lost, handle it
	 * 
	 */
	private void playerEndGame()
	{
		// The player died, restart
		if ( !thePlayer.alive && !thePlayer.winning ) 
			restart();
		
		if ( ( getTime() - thePlayer.dyingTime ) > DYING_ANIMATION_TIME  && thePlayer.dying )
			thePlayer.alive = false;
		
		if ( thePlayer.dying )
			thePlayer.deathAnimation();
		
		// The winning animation has finished, player wins
		if ( ( getTime() - winTime > WINNING_ANIMATION_TIME ) && thePlayer.winning )
			won();
	}
	
	/* won
	 * 		If the player wins, end the game and reset some variables
	 * 
	 */
	private void won() 
	{
		// End the round, reset some vars TODO: can we just set done and be 'done'?
		thePlayer.alive = true;
		thePlayer.gravityMod = 1;
		thePlayer.onIce = false;
		fixKeyboard();
		runSpeed = 0;
		bFinished = true;
		bPassed = true;
		
	}
	
	/* render
	 * 		Main drawing function, handles drawing everything in the game except text and values 
	 * 		for example the coin count is not drawn here
	 */
	private void render() 
	{
		// Draw everything in order
		for ( int i = 1; i <= 4; i++ ) 
		{
			for ( Shape shape : shapes ) 
			{
				if ( shape.visible && shape.displayOrder == i && shape != null ) 
				{
					shape.draw();
					if ( displayHitbox ) 
						shape.drawHitbox();
				}
			}
		}
		/*
		// Draw puff of landing on the ground on top
		if ( ground != null )
		{
			ground.draw( player );
		}
		
		// Draw the wall jump ani on top
		if ( wall != null )
		{
			wall.draw( player );
		}
		*/
		// Draw the player on top TODO: look into changing this display order to 3-d it up
		if( !thePlayer.winning )
			thePlayer.draw();
	}
	
	/* gamePaused
	 * 		If the user pauses the game, stop the time, show menu options
	 * 
	 */
	private void gamePaused( boolean paused )
	{
		
		// The game is paused, draw menu items appropriately
		if ( paused )
		{
			theFade.draw();
			PausedText.draw();
			ResumeText.draw();
			RestartText.draw();
			ExitText.draw();
			
			if ( P_row == 1)
				theArrow.setY(300);
			else if ( P_row == 2)
				theArrow.setY(386);
			else if ( P_row == 3)
				theArrow.setY(470);
			
			theArrow.draw();
			
			time1.setTextureArray( textureMap.get( "text" + (int) ( ( ( startPause - startTime ) / 600000 ) % 6 ) ) );
			time2.setTextureArray( textureMap.get( "text" + (int) ( ( ( startPause - startTime ) / 60000 ) % 10 ) ) );
			time3.setTextureArray( textureMap.get( "text" + (int) ( ( ( startPause - startTime ) / 10000 ) % 6 ) ) );
			time4.setTextureArray( textureMap.get( "text" + (int) ( ( ( startPause - startTime ) / 1000 ) % 10 ) ) );
		}
		else
		{
			// Not paused, calculate the correct digit types for the time
			if ( !thePlayer.winning )
			{
				coinCount2.setTextureArray( textureMap.get( "text" + thePlayer.goldCount % 10 ) );
				coinCount1.setTextureArray( textureMap.get( "text" + thePlayer.goldCount / 10 ) );
				
				time1.setTextureArray( textureMap.get( "text" + (int) ( ( ( getTime()  - startTime ) / 600000 ) % 6 ) ) );
				time2.setTextureArray( textureMap.get( "text" + (int) ( ( ( getTime()  - startTime ) / 60000 ) % 10 ) ) );
				time3.setTextureArray( textureMap.get( "text" + (int) ( ( ( getTime()  - startTime ) / 10000 ) % 6 ) ) );
				time4.setTextureArray( textureMap.get( "text" + (int) ( ( ( getTime()  - startTime ) / 1000 ) % 10 ) ) );
				
			}
			else
			{
				coinCount2.setTextureArray( textureMap.get( "text" + coinCount % 10 ) );
				coinCount1.setTextureArray( textureMap.get( "text" + coinCount / 10 ) );
				
				time1.setTextureArray( textureMap.get( "text" + (int) ( ( ( winTime - startTime ) / 600000 ) % 6 ) ) );
				time2.setTextureArray( textureMap.get( "text" + (int) ( ( ( winTime - startTime ) / 60000 ) % 10 ) ) );
				time3.setTextureArray( textureMap.get( "text" + (int) ( ( ( winTime - startTime ) / 10000 ) % 6 ) ) );
				time4.setTextureArray( textureMap.get( "text" + (int) ( ( ( winTime - startTime ) / 1000 ) % 10 ) ) );

				
			}
		}
	}
	
	/* staticRender
	 * 		The secondary drawing function, handles static objects on the screen such as the coin count
	 * 		Objects must be drawn in separate places because of glMatrix push/pop, we want these objects to 
	 * 		always appear at the same x,y relative to where the player has moved, the others are static
	 */
	private void staticRender()
	{
		// Draw elements
		theCoin.draw();
		theX.draw();
		theClock.draw();
		coinCount1.draw();
		coinCount2.draw();
		time1.draw();
		time2.draw();
		time3.draw();
		time4.draw();
		theColon.draw();
	}

	// GameOn
	public static void main( String[] args ) 
	{
		new GameOn( null, null );
	}

}
