package game;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;

import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glTranslated;
import static org.lwjgl.opengl.GL11.glPopMatrix;

import java.awt.Font;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;
import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.AudioLoader;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

import entities.Action;
import entities.Arrow;
import entities.ArrowKey;
import entities.Bat;
import entities.Box;
import entities.Carnival;
import entities.Clock;
import entities.Coin;
import entities.HighStriker;
import entities.IntroAni;
import entities.MapPointer;
import entities.PauseFade;
import entities.Puck;
import entities.Shape;
import entities.Shard;
import entities.Sign;
import entities.Text;
import entities.TextBox;
import entities.TowerDoor;
import entities.Wall;

public class GameShell
{
	
	enum State
	{
		INTRO, WELCOME, MAIN_MENU, GAMESLOTS, STRIKER, STATS, WORLD, LEVEL, EXIT;
	}
	
	enum Striker
	{
		NORMAL, INTRO, JUMP, OUTRO;
	}

	private static State state = State.INTRO;
	private static Striker strikerState = Striker.INTRO;
	
	private State nextState; // State before/after stats

	private static final int WIDTH = 1024; // Width of the game window
	private static final int HEIGHT = 728; // Height of the game window
	private static final int INTRO_TIME = 10; // Time for the intro animation
	private static final int PLAYER_SIZE = 32; // Size of Boss Greed
	private static final int TEXT_HEIGHT = 48; // This is not the height of all text, but most
	private static final int MAX_GRAV = 15;
	
	private static UnicodeFont uniFont;
	
	private final int FONT_SIZE = 14;

	private String currentFile; // Current save file being used
	private String copyFile; // File being copied
	private boolean copying = false; //booleans for copying


	// All the textures used in the game
	
	// Arrow Textures
	public static Texture[] arrowLeft = new Texture[2];
	public static Texture[] arrowRight = new Texture[2];
	
	// Bag Textures
	public static Texture[] bagUpLeft = new Texture[3];
	public static Texture[] bagUpRight = new Texture[3];
	public static Texture[] bagDownLeft = new Texture[3];
	public static Texture[] bagDownRight = new Texture[3];
	public static Texture[] bagUpWallLeft = new Texture[1];
	public static Texture[] bagUpWallRight = new Texture[1];
	public static Texture[] bagDownWallLeft = new Texture[1];
	public static Texture[] bagDownWallRight = new Texture[1];
	public static Texture[] bagUpThwompLeft = new Texture[2];
	public static Texture[] bagUpThwompRight = new Texture[2];
	public static Texture[] bagDownThwompLeft = new Texture[2];
	public static Texture[] bagDownThwompRight = new Texture[2];
	public static Texture[] bagUpCrashLeft = new Texture[1];
	public static Texture[] bagUpCrashRight = new Texture[1];
	public static Texture[] bagDownCrashLeft = new Texture[1];
	public static Texture[] bagDownCrashRight = new Texture[1];
	public static Texture[] bagDead = new Texture[6];
	
	// Barker Textures
	public static Texture[] barker = new Texture[6];
	public static Texture[] barkerTalk = new Texture[5];
	public static Texture[] barkerBubble = new Texture[4];
	
	// Dead Textures
	public static Texture[] deadHorizontal = new Texture[5];
	public static Texture[] deadVertical = new Texture[5];
	
	// Cliff Textures
	public static Texture[] cliffGrassUp = new Texture[1];
	public static Texture[] cliffGrassDown = new Texture[1];
	public static Texture[] cliffGrassDownBreak = new Texture[1];
	//TODO: Add cliffGrassUpBreak, does this exist?
	
	// Clock Textures
	public static Texture[] clock = new Texture[4];
	
	// Coin Textures
	public static Texture[] coin = new Texture[1];
	
	// public static Texture[] desert = new Texture[17];  Desert Images TODO: add into game 
	
	// Door Textures
	public static Texture[] doorBasic = new Texture[1];
	public static Texture[] doorWin = new Texture[15];
	
	// Dust Textures
	public static Texture[] dustUp = new Texture[2];
	public static Texture[] dustDown = new Texture[2];
	
	// GravFlip Textures
	public static Texture[] gravflip = new Texture[19];
	
	// HighStriker Textures
	public static Texture[] highstrikerBasic = new Texture[2];
	public static Texture[] highstrikerDynamic = new Texture[50];
	public static Texture[] highstrikerWin = new Texture[2];
	
	// Puck Textures
	public static Texture[] puck = new Texture[1];
	
	// Ice Textures
	public static Texture[] ice = new Texture[1];
	
	// Intro Textures
	public static Texture[] intro = new Texture[30];
	
	// Desk Textures
	public static Texture[] desk = new Texture[1];
	
	// Shard Textures
	public static Texture[] shard = new Texture[1];
	
	// Arrow up Textures
	public static Texture[] arrowkeyUp = new Texture[1];
	
	// Arrow left Textures
	public static Texture[] arrowkeyLeft = new Texture[1];
		
	// Arrow right Textures
	public static Texture[] arrowkeyRight = new Texture[1];
	
	// Esc Textures
	public static Texture[] esc = new Texture[1];
	
	// Spacebar right Textures
	public static Texture[] spacebar = new Texture[1];
	
	// Sign Textures
	public static Texture[] sign = new Texture[1];
	
	// Pause Textures
	public static Texture[] pause = new Texture[1];
	
	// Text Textures
	public static Texture[] textLevel = new Texture[1];	
	public static Texture[] textLevels = new Texture[1];
	public static Texture[] textAbout = new Texture[1];
	public static Texture[] textExit = new Texture[1];
	public static Texture[] textControls = new Texture[1];
	public static Texture[] textMainMenu = new Texture[1];
	public static Texture[] textRestart = new Texture[1];
	public static Texture[] textWelcome = new Texture[1];
	public static Texture[] textGame = new Texture[1];
	public static Texture[] textDDD = new Texture[1];
	public static Texture[] textWF = new Texture[1];
	public static Texture[] textTTM = new Texture[1];
	public static Texture[] textSSL = new Texture[1];
	public static Texture[] textTTC = new Texture[1];
	public static Texture[] textBack = new Texture[1];
	public static Texture[] text0 = new Texture[1];
	public static Texture[] text1 = new Texture[1];
	public static Texture[] text2 = new Texture[1];
	public static Texture[] text3 = new Texture[1];
	public static Texture[] text4 = new Texture[1];
	public static Texture[] text5 = new Texture[1];
	public static Texture[] text6 = new Texture[1];
	public static Texture[] text7 = new Texture[1];
	public static Texture[] text8 = new Texture[1];
	public static Texture[] text9 = new Texture[1];
	public static Texture[] textSlot1 = new Texture[1];
	public static Texture[] textSlot2 = new Texture[1];
	public static Texture[] textSlot3 = new Texture[1];
	public static Texture[] textHyphen = new Texture[1];
	public static Texture[] textStats = new Texture[1];
	public static Texture[] textPlay = new Texture[1];
	public static Texture[] textCopy = new Texture[1];
	public static Texture[] textErase = new Texture[1];
	public static Texture[] textPaused = new Texture[1];
	public static Texture[] textResume = new Texture[1];
	public static Texture[] textMap = new Texture[1];
	public static Texture[] textPress = new Texture[5];
	public static Texture[] textPoint = new Texture[1];
	public static Texture[] textColon = new Texture[1];
	public static Texture[] textX = new Texture[1];
	
	// TextBox Textures
	public static Texture[] textbox = new Texture[1];
	
	// TowerDoor Textures
	public static Texture[] towerDoor = new Texture[1];
	
	// Walljump Textures
	public static Texture[] walljumpLeft = new Texture[1];
	public static Texture[] walljumpRight = new Texture[1];
	
	// Wallpaper Textures
	public static Texture[] wallpaper = new Texture[1];
	
	// World Textures
	public static Texture[] worldTower = new Texture[1];
	public static Texture[] worldForest = new Texture[1];
	public static Texture[] worldWater = new Texture[1];
	public static Texture[] worldCave = new Texture[1];
	public static Texture[] worldSnow = new Texture[1];
	public static Texture[] worldDesert = new Texture[1];
	public static Texture[] worldSlums = new Texture[1];
	public static Texture[] worldSewers = new Texture[1];
	public static Texture[] worldHell = new Texture[1];
	public static Texture[] worldZen = new Texture[1];
	
	
	public static Map<String,Texture[]> textureMap = createTextureMap();
	
	
	// Intro Vars
	
	private	IntroAni theIntro;
	private Shard theShard;
	private long introStartTime;
	private long introPrevTime;
	
	// Welcome Vars
	private Text pressText;									// Press Text on welcome screen
	private Wall theWallpaper;								// Wallpaper on welcome screen
	private ArrowKey theSpacebar;							// Spacebar on welcome screen
	public static boolean bWelcomeSpaceDraw;				// Boolean if it is time to draw the spacebar
	private static final int MM_STARTX = 20; 						// Initial arrow x position of row 1
	private static final int MM_STARTY = 150; 						// Initial arrow y position of row 1
	private static final int MM_VERTICALCHANGE = 50; 				// Change in vertical position for the main menu arrow
	private static final int P_VERTICALCHANGE = 80; 				// Change in vertical position for the pause menu
	
	private static final int GS_STARTX = 0; 						// Initial arrow x position of GS_row 1
	private static final int GS_STARTY = 450; 						// Initial arrow y position of GS_row 1
	private static final int GS_HORIZONTALCHANGE = 330; 			// Change in horizontal position for the pause menu
	private static final int GS_WORDSINDEX = 18; 					// This is the index of where the words of GS menu start, these move faster than 
															// the other shapes in the array so this is necessary attempt to be efficient in moving them all at the same time
	private static final int CHAR_SIZE = 16; 				// Change in vertical position for the pause menu

	// Pause Vars
	
	private PauseFade thePauseFade;									// Black tint for pause screen
	private boolean bMapPause = false;								// Boolean for if the game is paused
	private Text P_pausedText, P_resumeText, P_statsText, P_mainMenuText; 	// Text objects for pause screen								// Arrow for choosing 
	

	// Main Menu Vars
	private Text MM_gameText, MM_controlsText, MM_exitText, MM_slot1Text, MM_slot2Text, MM_slot3Text, MM_backText;	// main menu text
	private Arrow MM_arrow;																			// row arrow
	private int MM_row = 1;														// row and col used for menu/pause/gameslot enums
	
	// Game Slots Vars
	public boolean bSlotsAnimation = false, bGetReadyToPlay = false; 			// gameslot animation control
	private Coin GS_coin; 																		// coin symbol for save file
	private Text GS_coinCount1, GS_coinCount2, GS_coinCount3, GS_x, GS_colon, GS_point; 									// text symbols for savefiles
	private Text GS_timer1, GS_timer2, GS_timer3, GS_timer4, GS_timer5, GS_timer6, GS_hyphen, GS_levelDigit1, GS_levelDigit2, GS_levelName;//text symbols for game slots
	private Clock GS_clock;	 																	// clock for save file
	private Text GS_playText, GS_eraseText, GS_copyText, GS_statsText, GS_backText; 	// text for when a slot is selected
	private int GS_row = 1, GS_col = 1, GS_prevSlot = 1;
	private ArrayList<Shape> playerSlotStats = new ArrayList<Shape>();
	
	// World Vars
	private double translate_x, translate_y;
	private MapPointer TTC;
	private Box thePlayer;
	private Text worldText, W_x, W_coinCount1, W_coinCount2, W_coinCount3;
	private TowerDoor TTCLevels[];
	private Coin W_coin;
	private int levelAccess = 0, prevLevel = 0, coinTally = 0;
	private long totalTime;
	private Shape[] worldShapes;
	private Shape[] TTCShapes = new Shape[2];
	private Shape[] WFShapes;
	private Shape[] WaterShapes;
	private Shape[] CaveShapes;
	private Shape[] SnowShapes;
	private Shape[] DesertShapes;
	private Shape[] SlumsShapes;
	private Shape[] SewersShapes;
	private Shape[] HellShapes;
	private boolean bNoInput = false,  bStrikerAnimation = false, bMoveBagAwayFromBarker = false, bPassed = false, bWeighing = false, bFinished = false;
	private ArrowKey barkerSpacebar;							// Spacebar on on barker screen
	private ArrayList<Texture> strikerTexture = new ArrayList<Texture>();
	private Texture[] strikerTextureArray;
	private Puck thePuck;
	
	
	private ArrayList<Shape[]> worldShapeMap = new ArrayList<Shape[]>();
	
	// World movement TODO: add jumping? how will we move in the overworld? 
	private double runSpeed;
	private boolean running, moving, pressingJump;
	private int failCount = 0;

	private double MAX_FAILS = 5; 			// this is to determine when the player has let go of the jump key
	private double MOVEMENT_AMOUNT = 5; 	// max walk speed
	private double CHANGE_DIR_SPEED = 1;	// deceleration amount when changing directions
	private double RUN_ACCEL_SPEED = .15;	// how fast your speed goes up when running
	private double INIT_ACCEL_SPEED = .5; 	// how fast you raise up to the normal MOVEMENT_AMOUNT
	private double DEACCEL_SPEED = .30; 	// how fast you slow down if you're above MOVEMENT_AMOUNT and let go of run, but keep moving
	private double RUN_SLIDE_AMOUNT = .25; 	// deceleration if you're running and stop moving
	private double WALK_SLIDE_AMOUNT = .25; // deceleration if you're walking and stop moving
	private double INIT_JUMP_SPEED = 5.1; 		// the initial speed that you start rising with
	private double initGravSpeed = INIT_JUMP_SPEED;
	private static double gravSpeed = 5.1;
	private boolean noLeft = false, noRight = false, rightWall = false, leftWall = false;
	private long jumpTime = 0;
	private Audio jumpSound, coinSound;
	public static Audio gravSound, landingSound;
	
	// Level Vars
	private String[] levelNames;
	private Integer[] coinTotals;
	private Long[] timeTotals;
	private int currentLevel = -1;
	private int levelIndex = 0;
	
	// Stats vars	
	private int currentWorld = 1;
	private double amt = 18.5;
	private Arrow statsLeft, statsRight;
	private ArrowKey escapeKey;
	private Text[] S_levelText = new Text[4];
	private Text[] S_hyphen = new Text[4];
	private Text[] S_levelDigit1 = new Text[4];
	private Text[] S_levelDigit2 = new Text[4];
	private Coin[] S_coin = new Coin[4];
	private Text[] S_x = new Text[4];
	private Text[] S_coinCount1 = new Text[4];
	private Text[] S_coinCount2 = new Text[4];
	private Clock[] S_clock = new Clock[4];
	private Text[] S_time1 = new Text[4];
	private Text[] S_time2 = new Text[4];
	private Text[] S_time3 = new Text[4];
	private Text[] S_time4 = new Text[4];
	private Text[] S_time5 = new Text[4];
	private Text[] S_time6 = new Text[4];
	private Text[] S_colon = new Text[4];
	private Text[] S_point = new Text[4];
	
	private ArrayList<Shape[]> statsShapeMap = new ArrayList<Shape[]>();

	
	// Striker Vars 	TODO: Implement this in the over world
	private Sign goldSign;
	private HighStriker TTCStriker;
	private HighStriker high;

	private Carnival theBarker, carniTalk;
	//private Puck thePuck;
	private boolean textDone = false, initStriker = false, startJump = false, levelPass = false, readyForWeigh = false;
	private long startingTime, jumpingTime, endingTime, textStartTime = 0L;
	private TextBox theTextBox;
	private int textAmt = 0, textRow = 0;
	private String barkerText[];
	private String barkerWin[];
	private String barkerLost[];
	
	
	

	// declare different states for the game
	/*
	 * Intro - 'A Bosscoding Production' Welcome - 'Press Space' with wallpaper
	 * Main_Menu - Menu screen with the choices: Game, Settings, Quit Game - a
	 * screen with 3 game slots, if nothing there then it says 'new game', if
	 * there is a save file either there is an option to load or erase the file
	 * Settings - a screen where you could toggle the sound, change controls, or
	 * go back to the main menu Leveli - goes to the ith level World - goes to
	 * the world map and draws boss greed going to whatever level is next Exit -
	 * Exits the game Win/Gameover - have not decided if these are necessary yet
	 * but its good to keep them here if we do decide to use them
	 * 
	 * 
	 * Natural progression is Intro - welcome - main - game(new) - world -
	 * leveli - world - level(i+1)....exit
	 */

	public static void main(String [] args)
	{
		new GameShell();
	}

	public GameShell()
	{

		initGL();
		initFonts();
		initSound();
		loadAllTextures( textureMap );
		
		introStartTime = getTime();
		
		initStatsVars();
		initPauseVars();
		initWelcomeVars();
		initMainMenuVars();
		initGameSlotsVars();
		initHighStrikerVars();
		initWorldVars();
		initIntroVars();
		initLevelVars();

		// Main Game Shell Loop
		while (!Display.isCloseRequested())
		{
			glClear(GL_COLOR_BUFFER_BIT);
			render();

				input();
			Display.update();
			Display.sync(60);
		}

		Display.destroy();
		System.exit(0);

	}

	/* initIntroVars()
	 * 		Create and initialize Intro variables
	*/
	private void initIntroVars() 
	{
		// Initialize Intro Vars
		theIntro = new IntroAni( 0, 0, WIDTH, HEIGHT, textureMap.get("intro") );
		theShard = new Shard( 480, 108, 64, 64, textureMap.get("shard") );
	}

	/* initWelcomeVars()
	 * 		Create and initialize Welcome variables
	*/
	private void initWelcomeVars() 
	{
		// Initialize Welcome Vars
		pressText = new Text( 235, 650, 256, 64, textureMap.get("textPress") );//	pressText = new Text(50, 410, 256, TEXT_HEIGHT, );
		theWallpaper = new Wall( 0, 108, 1024, 512, textureMap.get("wallpaper") ); //this is the resolution
		theSpacebar = new ArrowKey( 513, 650, 256, 64, textureMap.get("spacebar") );//	spacebar = new ArrowKey(280, 410, 256, TEXT_HEIGHT, );
		bWelcomeSpaceDraw = false;
	}
	
	/* initMainMenuVars()
	 * 		Create and initialize MainMenu variables
	*/
	private void initMainMenuVars() 
	{
		// Initialize Main Menu Vars
		MM_gameText = new Text(150, 150, 256, TEXT_HEIGHT, textureMap.get("textGame") );
		MM_controlsText = new Text(150, 200, 512, TEXT_HEIGHT, textureMap.get("textControls") );
		MM_exitText = new Text(150, 250, 128, TEXT_HEIGHT, textureMap.get("textExit") );
		MM_arrow = new Arrow( MM_STARTX, MM_STARTY, 128, TEXT_HEIGHT, textureMap.get("arrowRight") );
		MM_slot1Text = new Text(150, 150, 256, TEXT_HEIGHT, textureMap.get("textSlot1") );
		MM_slot2Text = new Text(150, 200, 256, TEXT_HEIGHT, textureMap.get("textSlot2") );
		MM_slot3Text = new Text(150, 250, 256, TEXT_HEIGHT, textureMap.get("textSlot3") );
		MM_backText = new Text(150, 300, 256, TEXT_HEIGHT, textureMap.get("textBack") );
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
	
	/* initGameSlotsVars()
	 * 		Create and initialize GameSlots variables
	*/
	private void initGameSlotsVars() 
	{
		GS_coin = new Coin ( 1065, 188, 36, 72, textureMap.get("coin") );
		GS_playText = new Text( 1060, 450, 128, 96, textureMap.get("textPlay") );
		GS_copyText = new Text( 1390, 450, 256, 96, textureMap.get("textCopy") );
		GS_eraseText = new Text( 1720, 450, 256, TEXT_HEIGHT, textureMap.get("textErase") );
		GS_statsText = new Text( 1060, 500, 256, TEXT_HEIGHT, textureMap.get("textStats") );
		GS_backText = new Text( 1390, 500, 256, TEXT_HEIGHT, textureMap.get("textBack") );
		GS_x = new Text (1104, 218, TEXT_HEIGHT, TEXT_HEIGHT, textureMap.get("textX") );
		GS_coinCount1 = new Text( 1127, 201, TEXT_HEIGHT, TEXT_HEIGHT, textureMap.get("text0") );
		GS_coinCount2 = new Text( 1162, 201, TEXT_HEIGHT, TEXT_HEIGHT, textureMap.get("text0") );
		GS_coinCount3 = new Text( 1197, 201, TEXT_HEIGHT, TEXT_HEIGHT, textureMap.get("text0") );
		GS_clock = new Clock( 1260, 201, TEXT_HEIGHT, TEXT_HEIGHT, textureMap.get("clock") );
		GS_timer1 = new Text( 1310, 201, TEXT_HEIGHT, TEXT_HEIGHT, textureMap.get("text0") );
		GS_timer2 = new Text( 1355, 201, TEXT_HEIGHT, TEXT_HEIGHT, textureMap.get("text0") );
		GS_timer3 = new Text( 1400, 201, TEXT_HEIGHT, TEXT_HEIGHT, textureMap.get("text0") );
		GS_timer4 = new Text( 1445, 201, TEXT_HEIGHT, TEXT_HEIGHT, textureMap.get("text0") );
		GS_timer5 = new Text( 1490, 201, TEXT_HEIGHT, TEXT_HEIGHT, textureMap.get("text0") );
		GS_timer6 = new Text( 1535, 201, TEXT_HEIGHT, TEXT_HEIGHT, textureMap.get("text0") );
		GS_point = new Text( 1468, 199, TEXT_HEIGHT, TEXT_HEIGHT, textureMap.get("textPoint") );
		GS_colon = new Text( 1377, 195, TEXT_HEIGHT, TEXT_HEIGHT, textureMap.get("textColon") );
		GS_levelName = new Text ( 1595, 199, 196, TEXT_HEIGHT, textureMap.get("textLevel") );
		GS_hyphen = new Text ( 1795, 209, 32, TEXT_HEIGHT, textureMap.get("textHyphen") );
		GS_levelDigit1 = new Text ( 1755, 201, TEXT_HEIGHT, TEXT_HEIGHT, textureMap.get("text0") );
		GS_levelDigit2 = new Text ( 1805, 201, TEXT_HEIGHT, TEXT_HEIGHT, textureMap.get("text0") );
		GS_coin.save_x = 222;
		GS_coin.startX = 1072;
		// Populate the arraylist for GS stats shapes for easier manipulations later on
		
		playerSlotStats.add( GS_coin );
		playerSlotStats.add( GS_x );
		playerSlotStats.add( GS_coinCount1 );
		playerSlotStats.add( GS_coinCount2 );
		playerSlotStats.add( GS_coinCount3 );
		playerSlotStats.add( GS_clock );
		playerSlotStats.add( GS_timer1 );
		playerSlotStats.add( GS_timer2 );
		playerSlotStats.add( GS_timer3 );
		playerSlotStats.add( GS_timer4 );
		playerSlotStats.add( GS_timer5 );
		playerSlotStats.add( GS_timer6 );
		playerSlotStats.add( GS_point );
		playerSlotStats.add( GS_colon );
		playerSlotStats.add( GS_levelName );
		playerSlotStats.add( GS_hyphen );
		playerSlotStats.add( GS_levelDigit1 );
		playerSlotStats.add( GS_levelDigit2 );
		playerSlotStats.add( GS_playText );
		playerSlotStats.add( GS_copyText );
		playerSlotStats.add( GS_eraseText );
		playerSlotStats.add( GS_statsText );
		playerSlotStats.add( GS_backText );
	}
	
	/* initStatsVars()
	 * 		Create and initialize Stats variables
	*/
	private void initStatsVars() 
	{
		// Initialize Stats Vars
		escapeKey = new ArrowKey ( 20, 20, 64, 64, textureMap.get("esc") );
		statsLeft = new Arrow( 20, 340, 128, TEXT_HEIGHT, textureMap.get("arrowLeft") );
		statsRight = new Arrow( 876, 340, 128, TEXT_HEIGHT, textureMap.get("arrowRight") );
		worldText = new Text( 60, 50, 1024, TEXT_HEIGHT * 2, textureMap.get("textTTC") );
		
		for ( int i = 0; i < S_levelText.length; i++ )
		{
			S_levelText[i] = new Text( 120, 211 + 100 * i, 196, TEXT_HEIGHT, textureMap.get("textLevel") );
			S_hyphen[i] = new Text( 320, 224 + 100 * i, 36, TEXT_HEIGHT, textureMap.get("textHyphen") );
			S_levelDigit1[i] = new Text( 280, 213 + 100 * i, TEXT_HEIGHT, TEXT_HEIGHT, textureMap.get("text0") );
			S_levelDigit2[i] = new Text( 335, 213 + 100 * i, TEXT_HEIGHT, TEXT_HEIGHT, textureMap.get("text0") );
			S_coin[i] = new Coin( 390, 200 + 100 * i, 36, 72, textureMap.get("coin") );
			S_x[i] = new Text( 426, 235 + 100 * i, 36, 36, textureMap.get("textX") );
			S_coinCount1[i] = new Text( 445, 213 + 100 * i, TEXT_HEIGHT, TEXT_HEIGHT, textureMap.get("text0") );
			S_coinCount2[i] = new Text( 480, 213 + 100 * i, TEXT_HEIGHT, TEXT_HEIGHT, textureMap.get("text0") );
			S_clock[i] = new Clock( 543, 213 + 100 * i, TEXT_HEIGHT, TEXT_HEIGHT, textureMap.get("clock") );
			S_time1[i] = new Text( 595, 213 + 100 * i, TEXT_HEIGHT, TEXT_HEIGHT, textureMap.get("text0") );
			S_time2[i] = new Text( 640, 213 + 100 * i, TEXT_HEIGHT, TEXT_HEIGHT, textureMap.get("text0") );
			S_time3[i] = new Text( 685, 213 + 100 * i, TEXT_HEIGHT, TEXT_HEIGHT, textureMap.get("text0") );
			S_time4[i] = new Text( 730, 213 + 100 * i, TEXT_HEIGHT, TEXT_HEIGHT, textureMap.get("text0") );
			S_time5[i] = new Text( 775, 213 + 100 * i, TEXT_HEIGHT, TEXT_HEIGHT, textureMap.get("text0") );
			S_time6[i] = new Text( 820, 213 + 100 * i, TEXT_HEIGHT, TEXT_HEIGHT, textureMap.get("text0") );
			S_colon[i] = new Text( 663, 206 + 100 * i, TEXT_HEIGHT, TEXT_HEIGHT, textureMap.get("textColon") );
			S_point[i] = new Text( 753, 208 + 100 * i, TEXT_HEIGHT, TEXT_HEIGHT, textureMap.get("textPoint") );
		}
		
		statsShapeMap.add( S_levelDigit1 );
		statsShapeMap.add( S_levelDigit2 );
		statsShapeMap.add( S_coinCount1 );
		statsShapeMap.add( S_coinCount2 );
		statsShapeMap.add( S_time1 );
		statsShapeMap.add( S_time2 );
		statsShapeMap.add( S_time3 );
		statsShapeMap.add( S_time4 );
		statsShapeMap.add( S_time5 );
		statsShapeMap.add( S_time6 );
		statsShapeMap.add( S_colon );
		statsShapeMap.add( S_point );
		statsShapeMap.add( S_levelText );
		statsShapeMap.add( S_hyphen );
		statsShapeMap.add( S_coin );
		statsShapeMap.add( S_x );
		statsShapeMap.add( S_clock );
	}
	
	/* initWorldVars()
	 * 		Create and initialize World variables
	*/
	private void initWorldVars() 
	{
		// Initialize World Vars
		
		// Initialize Striker Vars
		high = new HighStriker( 800, 102, 128, 512, textureMap.get("highstrikerBasic") );
		TTCStriker = new HighStriker( 1616, 188, 128, 512, textureMap.get("highstrikerBasic" ) );
		TTCStriker.setCoin( 30 );
		TTCStriker.textureDuration = 3;
		theBarker = new Carnival( 1500, 604, 64, 128, textureMap.get("barker") );
		barkerSpacebar = new ArrowKey( 384, 288, 256, 64, textureMap.get("spacebar") );
		carniTalk = new Carnival( 256 + 16, 128 + 8, 96, 96, textureMap.get("barkerTalk") );
		goldSign = new Sign( 550, 486, 128, 128, textureMap.get("sign") );
		
		thePuck = new Puck( TTCStriker.getX() + 60, TTCStriker.getY() + TTCStriker.getHeight() - 14, 8, 8, textureMap.get("puck") );
		thePuck.save_y = (int) ( TTCStriker.getY() + TTCStriker.getHeight() - 14 );
		int coinAmount = 30, prizeMoney = 300;
		theTextBox = new TextBox( 256, 128, 512, 128, textureMap.get("textbox") );
		barkerText = new String[5];
		barkerWin = new String[5];
		barkerLost = new String[5];
		barkerText[0] = "Step right up don't be shy,";
		barkerText[1] = "Ring the bell and win the grand prize!";
		barkerText[2] = "Press SPACEBAR to jump and move the puck,";
		barkerText[3] = "Without " + coinAmount + " coins you won't get " + prizeMoney + " bucks!";
		barkerText[4] = "This is me wishing you the best of luck!";
		barkerWin[0] = "Congratulations! You did it!";
		barkerWin[1] = "..what you want $300?? About that..";
		barkerWin[2] = "You won access to the forest where you";
		barkerWin[3] = "will have another chance to win the grand prize";
		barkerWin[4] = "See you there!";
		barkerLost[0] = "Ohh so close!";
		barkerLost[1] = "What a shame!";
		barkerLost[2] = "There are more coins in the desert to collect.";
		barkerLost[3] = "Come back when you have at least " + coinAmount;
		barkerLost[4] = "Better luck next time!";
				
				
		TTC = new MapPointer( 0, -350, 1536, 1536, textureMap.get("worldTower") );
		TTCLevels = new TowerDoor[4];
		TTCLevels[0] = new TowerDoor(588, 600, 64, 97, textureMap.get("towerDoor") );
		TTCLevels[1] = new TowerDoor( 900, 600,  64, 97, textureMap.get("towerDoor") );
		TTCLevels[2] = new TowerDoor( 992, 600, 64, 97, textureMap.get("towerDoor") );
		TTCLevels[3] = new TowerDoor( 1304, 600,  64, 97, textureMap.get("towerDoor") );
		TTCShapes[0] = new Bat( 0, 700, 2000, 32, textureMap.get("cliffGrassDown") );
		TTCShapes[1] = new Bat( TTCStriker.getX(), TTCStriker.getY() + TTCStriker.getHeight() - 32, 32, 32, textureMap.get("cliffGrassDown") );
		TTCShapes[1].transparent = true;
		thePlayer = new Box( 41, 544, PLAYER_SIZE, PLAYER_SIZE, textureMap.get("bagDownRight") );
		W_coin = new Coin ( 2 , -10, 36, 72, textureMap.get("coin") );
		W_x = new Text( 38 , 25, 36 , 36, textureMap.get("textX") );
		W_coinCount3 = new Text( 57, 3, TEXT_HEIGHT, TEXT_HEIGHT, textureMap.get("text0") );
		W_coinCount2 = new Text( 92, 3, TEXT_HEIGHT, TEXT_HEIGHT, textureMap.get("text0") );
		W_coinCount1 = new Text( 128, 3, TEXT_HEIGHT, TEXT_HEIGHT, textureMap.get("text0") );
		
		worldShapeMap.add( TTCShapes );
		worldShapeMap.add( WFShapes );
	}
	
	/* initPauseVars()
	 * 		Create and initialize Pause variables
	*/
	private void initPauseVars() 
	{
		// Initialize Pause Vars
		thePauseFade = new PauseFade( 0, 0, WIDTH, HEIGHT, textureMap.get("pause") );
		P_pausedText = new Text( 320, 145, 384, 144, textureMap.get("textPaused") );
		P_resumeText = new Text( 396, 270, 256, 96, textureMap.get("textResume") );
		P_statsText = new Text( 402, 384, 256, TEXT_HEIGHT, textureMap.get("textStats") );
		P_mainMenuText = new Text( 320, 455, 512, TEXT_HEIGHT, textureMap.get("textMainMenu") );
	}

	/* initHighStrikerVars()
	 * 		Create and initialize HighStriker variables
	*/
	private void initHighStrikerVars() 
	{
		
	}

	/* initLevelVars()
	 * 		Create and initialize Level variables
	*/
	private void initLevelVars() 
	{
		// Initialize Level Vars
		levelNames = new String[24];
		
		levelNames[0] = "testlevel1";
		levelNames[1] = "testlevel1";
		levelNames[2] = "testlevel1";
		levelNames[3] = "testlevel1";
		levelNames[4] = "testlevel1";
		levelNames[5] = "initialLevel";
		levelNames[6] = "initialLevel";
		levelNames[7] = "initialLevel";
		levelNames[8] = "initialLevel";
		levelNames[9] = "initialLevel";
		levelNames[10] = "initialLevel";
		levelNames[11] = "initialLevel";
		levelNames[12] = "initialLevel";
		levelNames[13] = "initialLevel";
		levelNames[14] = "initialLevel";
		levelNames[15] = "initialLevel";
		levelNames[16] = "initialLevel";
		levelNames[17] = "initialLevel";
		levelNames[18] = "initialLevel";
		levelNames[19] = "initialLevel";
		coinTotals = new Integer[24];
		timeTotals = new Long[24];
		
		for (int i = 0; i < coinTotals.length; i++ )
		{
			coinTotals[i] = 0;
			timeTotals[i] = (long) 0;
		}
		
	}

	/* createTextureMap()
	 * 		Create and initialize our texture map and textures
	*/
	private static Map<String,Texture[]> createTextureMap()
	{
		
		HashMap<String,Texture[]> tempMap = new HashMap<String,Texture[]>();
		
		tempMap.put( "arrowLeft", arrowLeft);
		tempMap.put( "arrowRight", arrowRight);
		tempMap.put( "bagDead", bagDead);
		tempMap.put( "bagDownCrashLeft", bagDownCrashLeft);
		tempMap.put( "bagDownCrashRight", bagDownCrashRight);
		tempMap.put( "bagDownLeft", bagDownLeft);
		tempMap.put( "bagDownRight", bagDownRight);
		tempMap.put( "bagDownThwompLeft", bagDownThwompLeft);
		tempMap.put( "bagDownThwompRight", bagDownThwompRight);
		tempMap.put( "bagDownWallLeft", bagDownWallLeft);
		tempMap.put( "bagDownWallRight", bagDownWallRight);
		tempMap.put( "bagUpCrashLeft", bagUpCrashLeft);
		tempMap.put( "bagUpCrashRight", bagUpCrashRight);
		tempMap.put( "bagUpLeft", bagUpLeft);
		tempMap.put( "bagUpRight", bagUpRight);
		tempMap.put( "bagUpThwompLeft", bagUpThwompLeft);
		tempMap.put( "bagUpThwompRight", bagUpThwompRight);
		tempMap.put( "bagUpWallLeft", bagUpWallLeft);
		tempMap.put( "bagUpWallRight", bagUpWallRight);
		tempMap.put( "barker", barker);
		tempMap.put( "barkerTalk", barkerTalk);
		tempMap.put( "barkerBubble", barkerBubble);
		tempMap.put( "deadHorizontal", deadHorizontal);
		tempMap.put( "deadVertical", deadVertical);
		tempMap.put( "cliffGrassUp", cliffGrassUp);
		tempMap.put( "cliffGrassDown", cliffGrassDown);
		tempMap.put( "cliffGrassDownBreak", cliffGrassDownBreak);
		tempMap.put( "clock", clock);
		tempMap.put( "coin", coin);
		// tempMap.put " desert",  desert);  Desert Images TODO: add into game 
		tempMap.put( "doorBasic", doorBasic);
		tempMap.put( "doorWin", doorWin);
		tempMap.put( "dustUp", dustUp);
		tempMap.put( "dustDown", dustDown);
		tempMap.put( "gravflip", gravflip);
		tempMap.put( "highstrikerBasic", highstrikerBasic);
		tempMap.put( "highstrikerDynamic", highstrikerDynamic);
		tempMap.put( "highstrikerWin", highstrikerWin);
		tempMap.put( "sign", sign);
		tempMap.put( "puck", puck);
		tempMap.put( "ice", ice);
		tempMap.put( "intro", intro);
		tempMap.put( "desk", desk);
		tempMap.put( "shard", shard);
		tempMap.put( "arrowkeyUp", arrowkeyUp);
		tempMap.put( "arrowkeyLeft", arrowkeyLeft);
		tempMap.put( "arrowkeyRight", arrowkeyRight);
		tempMap.put( "esc", esc);
		tempMap.put( "spacebar", spacebar);
		tempMap.put( "pause", pause);
		tempMap.put( "textLevel", textLevel);
		tempMap.put( "textLevels", textLevels);
		tempMap.put( "textAbout", textAbout);
		tempMap.put( "textExit", textExit);
		tempMap.put( "textControls", textControls);
		tempMap.put( "textMainMenu", textMainMenu);
		tempMap.put( "textRestart", textRestart);
		tempMap.put( "textWelcome", textWelcome);
		tempMap.put( "textGame", textGame);
		tempMap.put( "textDDD", textDDD);
		tempMap.put( "textWF", textWF);
		tempMap.put( "textTTM", textTTM);
		tempMap.put( "textSSL", textSSL);
		tempMap.put( "textTTC", textTTC);
		tempMap.put( "textBack", textBack);
		tempMap.put( "text0", text0);
		tempMap.put( "text1", text1);
		tempMap.put( "text2", text2);
		tempMap.put( "text3", text3);
		tempMap.put( "text4", text4);
		tempMap.put( "text5", text5);
		tempMap.put( "text6", text6);
		tempMap.put( "text7", text7);
		tempMap.put( "text8", text8);
		tempMap.put( "text9", text9);
		tempMap.put( "textSlot1", textSlot1);
		tempMap.put( "textSlot2", textSlot2);
		tempMap.put( "textSlot3", textSlot3);
		tempMap.put( "textHyphen", textHyphen);
		tempMap.put( "textStats", textStats);
		tempMap.put( "textPlay", textPlay);
		tempMap.put( "textCopy", textCopy);
		tempMap.put( "textErase", textErase);
		tempMap.put( "textPaused", textPaused);
		tempMap.put( "textResume", textResume);
		tempMap.put( "textMap", textMap);
		tempMap.put( "textPress", textPress);
		tempMap.put( "textPoint", textPoint);
		tempMap.put( "textColon", textColon);
		tempMap.put( "textX", textX);
		tempMap.put( "textbox", textbox);
		tempMap.put( "walljumpLeft", walljumpLeft);
		tempMap.put( "walljumpRight", walljumpRight);
		tempMap.put( "wallpaper", wallpaper);
		tempMap.put( "worldTower", worldTower);
		tempMap.put( "worldForest", worldForest);
		tempMap.put( "worldWater", worldWater);
		tempMap.put( "worldCave", worldCave);
		tempMap.put( "worldSnow", worldSnow);
		tempMap.put( "worldDesert", worldDesert);
		tempMap.put( "worldSlums", worldSlums);
		tempMap.put( "worldSewers", worldSewers);
		tempMap.put( "worldHell", worldHell);
		//tempMap.put( "worldZen", worldZen); TODO: add zen world (sketch)
			
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
				entry.getValue()[i] = loadTexture("Animations/" + currEntry + "/" + currEntry + i);
		}
	}

	/* initGL
	 * 		Standard GL initialization 
	 *
	 */
	public void initGL()
	{
		try
		{
			Display.setDisplayMode( new DisplayMode( WIDTH, HEIGHT ) );
			Display.setTitle( "BossGreed" );
			Display.setVSyncEnabled( true );
			Display.create();
		}
		catch ( LWJGLException e )
		{
			e.printStackTrace();
		}

		glMatrixMode( GL_PROJECTION );
		glLoadIdentity();
		glOrtho( 0, WIDTH, HEIGHT, 0, -1, 1 );
		glMatrixMode( GL_MODELVIEW );
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

	/* initFonts
	 * 		This takes time... adds textures for plain text used in conversations
	 *		TODO: can we get rid of suppress warnings
	 */
	@SuppressWarnings( "unchecked" )
	private void initFonts()
	{

		Font awtFont = new Font( "", Font.PLAIN, FONT_SIZE );

		uniFont = new UnicodeFont( awtFont, FONT_SIZE, false, false );
		uniFont.addAsciiGlyphs();
		uniFont.addGlyphs( 400, 600 );           // Setting the unicode Range
		uniFont.getEffects().add( new ColorEffect( java.awt.Color.white ) );
		
		try
		{
			uniFont.loadGlyphs();
		}
		catch (SlickException e)
		{
			e.printStackTrace();
		}

		System.out.println( "Fonts initialized!" );
	}

	/* render
	 * 		Main drawing function, handles drawing everything in the shell except text and values 
	 * 		
	 */
	private void render()
	{
		/*
		System.out.println("x: " + thePlayer.getX());
		System.out.println("y: " + thePlayer.getY());
		*/
		switch ( state )
		{
			case INTRO:
				drawIntro();
				break;
			
			case WELCOME:
				drawWelcome();
				break;
			
			case MAIN_MENU:
				drawMain();
				break;
			
			case GAMESLOTS:
				drawGameSlots();
				break;
				
			case STATS:
				drawStats();
				break;
			
			case WORLD:
				drawWorld();
				break;
	
			case LEVEL:
				playLevel();	//Will play the current level -stored in levelIndex TODO: pass this in?
				break;
				
			case EXIT:
				Display.destroy();
				System.exit( 0 );
				break;
		}
	}

	//TODO: even though this wont be used, we'll have to draw the striker in an efficient w
	/* input
	 * 		Main input loop, handles all actions made by the user
	 * 
	 */
	private void input()
	{
		switch ( state )
		{
		
		case STRIKER:
			
			if ( readyForWeigh && !high.done )
			{
				if ( Keyboard.isKeyDown( Keyboard.KEY_SPACE ) )
				{
					thePuck.dy = coinTally/ (1.00 * high.getCoin());
					if (thePuck.dy > 1 )
						thePuck.dy = 1;
					thePuck.dy *= 5.5;
					//startingTime = getTime();
					jumpingTime = getTime();
					startJump = true;
					fixKeyboard();
					readyForWeigh = false;
				}
			}
			else if ( high.done && textDone)
			{
				if ( Keyboard.isKeyDown( Keyboard.KEY_SPACE ) )
				{
					state = State.WORLD;
					endingTime = 0;
				}
			}
			break;
		case INTRO:
			//no input here, just animation
		case LEVEL:
			//this is handled by GameOn
			break;
		
		case WELCOME:
			
			if ( Keyboard.isKeyDown( Keyboard.KEY_SPACE ) )
			{
				state = State.MAIN_MENU;
				theWallpaper.faded = true;
			}
			break;
		
		case MAIN_MENU:
			
			if ( Keyboard.isKeyDown(Keyboard.KEY_UP) && MM_row > 1)
			{
				MM_row--;
				MM_arrow.setY( MM_arrow.getY() - MM_VERTICALCHANGE );
				fixKeyboard();
			} 
			else if ( Keyboard.isKeyDown( Keyboard.KEY_DOWN ) && MM_row < 3 )
			{
				MM_row++;
				MM_arrow.setY( MM_arrow.getY() + MM_VERTICALCHANGE );
				fixKeyboard();
			} 
			else if ( Keyboard.isKeyDown( Keyboard.KEY_RETURN ) )
			{
				if ( MM_row == 1 )
				{
					state = State.GAMESLOTS;
					theWallpaper.faded = true;
				}
				//else if ( row == 2 )
					//settings
				else
					state = State.EXIT;
				
				fixKeyboard();
			}
			break;
			
		case STATS:
			
			if ( Keyboard.isKeyDown(Keyboard.KEY_RIGHT) && currentWorld < 6 )
			{
				currentWorld++;
				setUpStats();
				fixKeyboard();
			}
			else if ( Keyboard.isKeyDown(Keyboard.KEY_LEFT) && currentWorld > 1 )
			{
				currentWorld--;
				setUpStats();
				fixKeyboard();
			}
			else if ( Keyboard.isKeyDown(Keyboard.KEY_ESCAPE) )
			{
				state = nextState;
				fixKeyboard();
			}
			
			break;
			
		case GAMESLOTS:
			
			if ( !bGetReadyToPlay )
			{
				if ( !bSlotsAnimation )
				{
					if ( Keyboard.isKeyDown(Keyboard.KEY_UP) && MM_row > 1 )
					{
						MM_row--;
						MM_arrow.setY( MM_arrow.getY() - MM_VERTICALCHANGE );
						
						fixKeyboard();
					} 
					else if ( Keyboard.isKeyDown(Keyboard.KEY_DOWN) && MM_row < 4 )
					{
						MM_row++;
						MM_arrow.setY( MM_arrow.getY() + MM_VERTICALCHANGE );
						
						fixKeyboard();
					} 
					else if ( Keyboard.isKeyDown(Keyboard.KEY_RETURN) )
					{
						if ( copying )
						{
							copyFile = "file" + MM_row + ".txt";
							saveGame( copyFile );
							copying = false;
						}
						else
						{
							// This is going back to the main menu "game/controls/exit"
							if ( MM_row == 4 )
							{
								state = State.MAIN_MENU;
								MM_row = 1;
								MM_arrow.setX( MM_STARTX );
								MM_arrow.setY( MM_STARTY );
								bSlotsAnimation = false;
								theWallpaper.faded = true;
							}
							else
							{
								// This is selecting a game slot, either 1 2 or 3
								bSlotsAnimation = true;
								setUpGameSlots();
								loadGame( currentFile );
								prevLevel = 0;
								translate_x = 0;
								translate_y = 0;
							}
							fixKeyboard();
						}
					}
				}
				else
				{	
					if ( Keyboard.isKeyDown(Keyboard.KEY_UP) && GS_row > 1 )
					{
						GS_row--;
						MM_arrow.setY( MM_arrow.getY() - MM_VERTICALCHANGE );
						
						fixKeyboard();
					} 
					else if ( Keyboard.isKeyDown(Keyboard.KEY_DOWN) && GS_row < 2 && GS_col < 3 )
					{
						GS_row++;
						MM_arrow.setY( MM_arrow.getY() + MM_VERTICALCHANGE );
						
						fixKeyboard();
					} 
					else if ( Keyboard.isKeyDown(Keyboard.KEY_LEFT) && GS_col > 1 )
					{
						GS_col--;
						MM_arrow.setX( MM_arrow.getX() - GS_HORIZONTALCHANGE );
						
						fixKeyboard();
					} 
					else if ( Keyboard.isKeyDown(Keyboard.KEY_RIGHT) )
					{
						
						if ( ( GS_row == 2 && GS_col < 2 ) || ( GS_row == 1 && GS_col < 3 ) )
						{
							GS_col++;
							MM_arrow.setX( MM_arrow.getX() + GS_HORIZONTALCHANGE );
						}
						
						fixKeyboard();
					} 
					else if ( Keyboard.isKeyDown(Keyboard.KEY_RETURN) )
					{
						
						if ( GS_row == 1 && GS_col == 1) //play
						{
							bSlotsAnimation = false;
							bGetReadyToPlay = true;
						}
						else if ( GS_row == 1 && GS_col == 2) //copy	TODO: this doesnt work?
						{
							copying = true;
							bSlotsAnimation = false;
							MM_arrow.setX( MM_STARTX );
							MM_arrow.setY( MM_STARTY );
							
						}
						else if ( GS_row == 1 && GS_col == 3)//erase
						{
							loadGame( "file4.txt" );
							saveGame( currentFile );
						}
						else if ( GS_row == 2 && GS_col == 1)//stats
						{
							state = State.STATS;
							nextState = State.GAMESLOTS;
						}
						else if ( GS_row == 2 && GS_col == 2)//back
						{
							bSlotsAnimation = false;
							MM_arrow.setX( MM_STARTX );
							MM_arrow.setY( MM_STARTY );
							MM_row = 1;
						}
						
						fixKeyboard();
					}
				}
			}
			break;
			
		case WORLD:
			
			if ( bMapPause )
			{
				if ( Keyboard.isKeyDown(Keyboard.KEY_UP) && MM_row > 1 )
				{
					MM_row--;
					MM_arrow.setY( MM_arrow.getY() - P_VERTICALCHANGE );
					
					fixKeyboard();
				} 
				else if ( Keyboard.isKeyDown(Keyboard.KEY_DOWN) && MM_row < 3 )
				{
					MM_row++;
					MM_arrow.setY( MM_arrow.getY() + P_VERTICALCHANGE );
					
					fixKeyboard();
				}
				else if ( Keyboard.isKeyDown(Keyboard.KEY_RETURN) )
				{
					if ( MM_row == 1 )
						bMapPause = false;
					else if ( MM_row == 2 )
					{
						state = State.STATS;
						nextState = State.WORLD;
					}
					else if ( MM_row == 3 )
					{
						state = State.MAIN_MENU;
						theWallpaper.faded = true;
						bMapPause = false;
						bSlotsAnimation = false;
						MM_row = 1;
						MM_arrow.setX( MM_STARTX );
						MM_arrow.setY( MM_STARTY );
					}
					fixKeyboard();
				}
			}
			else
			{
				if ( Keyboard.isKeyDown(Keyboard.KEY_RETURN) )
				{
					if ( currentLevel >= 0 )
						state = State.LEVEL;
				}
				
				if ( Keyboard.isKeyDown(Keyboard.KEY_LEFT) )
				{
					move( -1 );
					thePlayer.setTextureArray( textureMap.get( "bagDownLeft" ) );
				} 
				else if ( Keyboard.isKeyDown(Keyboard.KEY_RIGHT) )
				{
					move( 1 );
					thePlayer.setTextureArray( textureMap.get( "bagDownRight" ) );
				}
				else
					moving = false;
				
				if ( Keyboard.isKeyDown(Keyboard.KEY_UP) )
					thePlayer.setY( thePlayer.getY() - 2 );
				else if ( Keyboard.isKeyDown(Keyboard.KEY_DOWN) )
					thePlayer.setY( thePlayer.getY() + 2 );
				else if ( Keyboard.isKeyDown(Keyboard.KEY_B) )
					translate_x -= 3;
				else if ( Keyboard.isKeyDown(Keyboard.KEY_P) || Keyboard.isKeyDown(Keyboard.KEY_ESCAPE) )
				{
					bMapPause = true;
					MM_arrow.setX( 275 );
					MM_arrow.setY( 300 );
					MM_row = 1;
				}
			}
			
			if ( Keyboard.isKeyDown( Keyboard.KEY_SPACE ) && bStrikerAnimation )
			{
				if ( strikerState == Striker.INTRO )
					strikerState = Striker.JUMP;
				else if ( strikerState == Striker.OUTRO )
				{
					strikerState = Striker.INTRO;
					if ( !bPassed )
						bMoveBagAwayFromBarker = true;
					
					bWeighing = false;
					bFinished = false;
				}
				
					
					
				fixKeyboard();
			}
			break;

		case EXIT:
			//no input
			break;
		}
	}
	
	/* setUpGameSlots
	 * 		Update the current gameslots info when the user changes worlds to display
	 * 
	 */
	private void setUpGameSlots() 
	{
		
		for ( int i = 0; i < GS_WORDSINDEX; i++ )
			playerSlotStats.get( i ).setY( playerSlotStats.get( i ).getY() + ( MM_row - GS_prevSlot ) * MM_VERTICALCHANGE );

		GS_coinCount3.setTextureArray( textureMap.get( "text" + coinTally % 10 ) );
		GS_coinCount2.setTextureArray( textureMap.get( "text" + ( (coinTally % 100 )/ 10 ) ) );
		GS_coinCount1.setTextureArray( textureMap.get( "text" + ( coinTally / 100 ) ) );
		GS_timer1.setTextureArray( textureMap.get( "text" + (int) ( ( totalTime / 600000 ) % 6) ) ); //10s of min
		GS_timer2.setTextureArray( textureMap.get( "text" + (int) ( ( totalTime / 60000 ) % 10) ) ); //min
		GS_timer3.setTextureArray( textureMap.get( "text" + (int) ( ( totalTime / 10000 ) % 6 ) ) ); //10s of seconds
		GS_timer4.setTextureArray( textureMap.get( "text" + (int) ( ( totalTime / 1000 ) % 10 ) ) ); //seconds
		GS_timer5.setTextureArray( textureMap.get( "text" + (int) ( ( totalTime / 100 ) % 10) ) );//tenths
		GS_timer6.setTextureArray( textureMap.get( "text" + (int) ( ( totalTime / 10 ) % 10) ) ); //hundredths
		GS_levelDigit1.setTextureArray( textureMap.get( "text" + ( ( levelAccess / 4 ) + 1 ) ) ); // The world
		GS_levelDigit2.setTextureArray( textureMap.get( "text" + ( ( levelAccess % 4 ) + 1 ) ) ); // The level
		GS_prevSlot = MM_row;
		currentFile = "file" + MM_row + ".txt";
		GS_row = 1;
		GS_col = 1;
		MM_arrow.setX( GS_STARTX );
		MM_arrow.setY( GS_STARTY );
		
	}

	/* setUpStats
	 * 		Update the current stats info when the user changes worlds to display
	 * 
	 */
	private void setUpStats() 
	{
		// Set the World Text
		if ( currentWorld == 1 )
		{
			worldText.setTextureArray( textureMap.get("textTTC") );
			worldText.setHeight( TEXT_HEIGHT  * 2);
		}
		else if ( currentWorld == 2)
		{
			worldText.setTextureArray( textureMap.get("textWF") );
			worldText.setHeight( TEXT_HEIGHT );
		}
		
		// Set the stats Text
		for ( int i = 0; i < statsShapeMap.get( 0 ).length; i++ )
		{
			statsShapeMap.get( 0 )[i].setTextureArray( textureMap.get("text" + currentWorld ) ); // levelDigit1
			statsShapeMap.get( 1 )[i].setTextureArray( textureMap.get("text" + i) ); // levelDigit2
			statsShapeMap.get( 2 )[i].setTextureArray( textureMap.get("text" + coinTotals[i + 4 * ( currentWorld - 1 )] / 10 ) ); // coinCount1
			statsShapeMap.get( 3 )[i].setTextureArray( textureMap.get("text" + coinTotals[i + 4 * ( currentWorld - 1 )] % 10 ) ); // coinCount2
			statsShapeMap.get( 4 )[i].setTextureArray( textureMap.get("text" + (int) ( ( timeTotals[i + 4 * ( currentWorld - 1 )] / 600000 ) % 6 ) ) ); // time1
			statsShapeMap.get( 5 )[i].setTextureArray( textureMap.get("text" + (int) ( ( timeTotals[i + 4 * ( currentWorld - 1 )] / 60000 ) % 10) ) ); // time2
			statsShapeMap.get( 6 )[i].setTextureArray( textureMap.get("text" + (int) ( ( timeTotals[i + 4 * ( currentWorld - 1 )] / 10000 ) % 6 ) ) ); // time3
			statsShapeMap.get( 7 )[i].setTextureArray( textureMap.get("text" + (int) ( ( timeTotals[i + 4 * ( currentWorld - 1 )] / 1000 ) % 10 ) ) ); // time4
			statsShapeMap.get( 8 )[i].setTextureArray( textureMap.get("text" + (int) ( ( timeTotals[i + 4 * ( currentWorld - 1 )] / 100 ) % 10) ) ); // time5
			statsShapeMap.get( 9 )[i].setTextureArray( textureMap.get("text" + (int) ( ( timeTotals[i + 4 * ( currentWorld - 1 )] / 10 ) % 10) ) ); // time6
		}
	}

	/* playerHorizontalMovement
	 * 		The user still moves from left to right outside of the move function, and that happens here
	 * 
	 */
	private void playerHorizontalMovement()
	{
		// Normal settings
		CHANGE_DIR_SPEED = 1;
		DEACCEL_SPEED = .20;
		WALK_SLIDE_AMOUNT = .3;
		RUN_SLIDE_AMOUNT = .3;
		INIT_ACCEL_SPEED = .5;
			
		// If you pushed left or right, then move in that direction
		if ( moving ) 
			thePlayer.setX( thePlayer.getX() + runSpeed * thePlayer.lastDIR );
		else
		{
			// Start sliding
			if ( runSpeed > 0) 
			{
				if ( running ) 
					runSpeed -= RUN_SLIDE_AMOUNT; // decelerate at this rate if running
				else 
					runSpeed -= WALK_SLIDE_AMOUNT; // and at this rate if not running
				
				thePlayer.setX( thePlayer.getX() + runSpeed * thePlayer.lastDIR );
			}
		}	
	}
	
	/* onGround
	 * 		This method returns if the player is on the ground.
	 * 
	 */
	public boolean onGround( Shape[] shapes ) 
	{

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
						
						if ( ( thePlayer.getY() < shape.y && thePlayer.gravityMod == 1 && ( !bStrikerAnimation || bFinished ) ) || ( bStrikerAnimation && thePlayer.getY() < shape.y && thePlayer.gravityMod == 1 && gravSpeed < 0 )) //normal case, normal grav
						{
							thePlayer.setY( shape.getY() - thePlayer.getHeight() );
							thePlayer.groundPiece = shape;
							shape.interact( thePlayer );
							//ground = new Action( thePlayer.getX() - thePlayer.getWidth() / 2, thePlayer.getY() + thePlayer.getHeight() - 64, 64, 64, textureMap.get("dustUp") );		// TODO: macro these
							thePlayer.hitWall = false;
							thePlayer.stopAnimation = false;
							return true; 
						}
						else if ( thePlayer.getY() + thePlayer.getHeight() > shape.y + shape.height && thePlayer.gravityMod == -1 ) // normal case, flipped grav
						{
							thePlayer.setY( shape.getY() + shape.getHeight() ); 	// set height
							thePlayer.groundPiece = shape; 		// set ground piece
							shape.interact( thePlayer );				// interact with our groundpiece
							//ground = new Action( thePlayer.getX() - thePlayer.getWidth() / 2, thePlayer.getY(), 64, 64, textureMap.get("dustDown") );	// TODO: macro these
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
	public String collide( boolean onGround, String returnString, Shape[] shapes )
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
				
				if ( shape.visible && shape.solid && !shape.transparent ) // Here is the fun part, we want to handle hitting the sides and bottoms of normal platforms

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
				
			}
		}
		return returnString;
		
	}
	
	/* playerVerticalMovement
	 * 		All of the vertical physics after jumping are handled here
	 * 
	 */
	private String playerVerticalMovement( String playerTextureString )
	{
		
		if ( thePlayer.jumping ) // Continue rising from the jump
		{
			if ( gravSpeed > - MAX_GRAV )
				gravSpeed -= .15;
			
			if ( ( gravSpeed <= 1 || !pressingJump ) && !bStrikerAnimation )
				thePlayer.jumping = false;

			thePlayer.setY( thePlayer.getY() - ( gravSpeed * thePlayer.gravityMod ) );
		}
		else if ( !thePlayer.jumping && !thePlayer.grounded ) // Normal falling 
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
		jumpSound.playAsSoundEffect( 1, 1, false ); // play the jump sound!
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
			// TODO: play slide sound
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

			// If you were running at beyond top walk speed, then decelerate to top walk speed	// TODO: more sprinting handling.. unused
			if ( !running && runSpeed > MOVEMENT_AMOUNT ) 
			{
				runSpeed -= DEACCEL_SPEED; // when you let go of running

				if ( runSpeed < MOVEMENT_AMOUNT ) // to make sure you don't go below top walk speed
					runSpeed = MOVEMENT_AMOUNT;
			}
		}
	}
	
	/* drawWorld
	 * 		Specific rendering of the overworld	
	 * 		TODO: change this function..entirely...
	 */
	private void drawWorld()
	{
		glPushMatrix();
		
			glTranslated( translate_x, translate_y, 0 );
			
			if ( ( thePlayer.x + translate_x ) > ( 2 * WIDTH / 3 ) )
				translate_x -= 4;
			
			if ( ( thePlayer.x + translate_x ) <  ( WIDTH / 3 ) && translate_x < 0)
				translate_x += 4;
			
			for ( int i = 0; i < TTCLevels.length; i++ )	// TODO: there should not be any world specific variables, they will change upon changing a world
			{
				if ( thePlayer.intersects( TTCLevels[i] ) )
				{
					currentLevel = i;
					//TODO: fade in level text here
				}
				
			}
			
			// Check if the player is on the ground
			thePlayer.grounded = onGround( worldShapes );
			
			// Check if the player has intersected with anything, this is seperate from onGround because 
			// objects affect bg differently whether he is on the ground or not (i.e. walljumping)
			collide( thePlayer.grounded, null, worldShapes );
			// Handle anything dealing with horizontal movement of the player
			playerHorizontalMovement();
			
			playerVerticalMovement( null );
			
			for ( int i = 0; i < worldShapes.length; i++ )
				worldShapes[i].draw();
			
			TTC.draw();
			TTCStriker.draw();
			theBarker.draw();
			thePuck.draw();
			
			if ( prevLevel < 6 )
			{
				worldText = new Text( 60, 50, 1024, TEXT_HEIGHT * 2, textureMap.get("textTTC") );	// TODO: move this to a setup world, dont create a new one everytime
			}
			else if ( prevLevel < 11)
			{
				worldText = new Text( 30, 50, 1024 , TEXT_HEIGHT, textureMap.get("textWF") );
			}
			
			thePlayer.draw();
			
		glPopMatrix();
		
		if ( ( prevLevel == 19 && thePlayer.lastDIR == 1) )
		{
			
			
		}
		else
		{
			worldText.draw();
		}
		
		W_coin.draw(); 
		W_x.draw();
		W_coinCount1.setTextureArray( textureMap.get( "text" + coinTally % 10 ) );
		W_coinCount2.setTextureArray( textureMap.get( "text" + ( coinTally % 100 ) / 10 ) );
		W_coinCount3.setTextureArray( textureMap.get( "text" + (coinTally / 100 ) ) );
		W_coinCount1.draw();
		W_coinCount2.draw();
		W_coinCount3.draw();
		
		if ( bMoveBagAwayFromBarker )
		{
			bNoInput = true;
			if ( theBarker.getX() - ( thePlayer.getX() + thePlayer.getWidth()) > 50 )
			{
				bMoveBagAwayFromBarker = false;
				strikerState = Striker.INTRO;
				bNoInput = false;
				textRow = 0;
				textAmt = 0;
				bStrikerAnimation = false;
				
			}
			else
			{
				strikerState = Striker.NORMAL;
				thePlayer.setX( thePlayer.getX() - 1 );
				thePlayer.lastDIR = -1;
			}
				
			
		}
		else if ( thePlayer.intersects( theBarker ) && !bPassed )
		{
			bNoInput = true;
			runSpeed = 0;
			
			bStrikerAnimation = true;
			if ( textStartTime < 1 )
				textStartTime = getTime();
		}
		
		if ( bStrikerAnimation )
			strikerAnimation();
		
		if ( bMapPause )
		{
			thePauseFade.draw();
			P_pausedText.draw();
			P_resumeText.draw();
			P_statsText.draw();
			P_mainMenuText.draw();
			MM_arrow.draw();
			
		}

	}
	
	/* strikerAnimation
	 * 		Handles animation for the player jumping on the striker, and the striker physics
	 * 
	 */
	private void strikerAnimation()
	{
		// show text
		switch( strikerState )
		{
		case NORMAL:
			break;
		
		case INTRO:
			strikerText( barkerText );
			break;
		
		case JUMP:
			strikerJump();
			break;
		
		case OUTRO:
			if ( bPassed )
			{
				strikerText( barkerWin );
				// spacebar moves us along to the right, dont get stopped by barker anymore
				// load new world when walk to the right/ immediately?
				
			}
			else
			{
				strikerText( barkerLost );
				// spacebar moves us along to the left, reset situation to previous
			}
			break;
			
		}
		
	}
	
	/* strikerJump
	 * 		Animation to move the bag and perform the high striker test
	 * 
	 */
	private void strikerJump()
	{
		
		if ( thePlayer.groundPiece == worldShapes[1] && thePlayer.jumping ) // Add some delay so we can get above the plank at all
		{
			
			//TTCStriker.setTextureArray( textureMap.get( "highstrikerDynamic" ) );
			
			bWeighing = true;
			thePlayer.jumping = false;
			//int loopCount = (int) ( (double) ( coinTally / TTCStriker.getCoin() ) ) * textureMap.get( "highstrikerDynamic" ).length;
			int coinz = 10;
			int loopCount = (int) Math.floor( ( coinz / (TTCStriker.getCoin() * 1.0) ) * textureMap.get( "highstrikerDynamic" ).length );
			
			strikerTextureArray = new Texture[loopCount * 2];
			
			for ( int i = 0; i < loopCount; i++ )
				strikerTextureArray[i] = textureMap.get( "highstrikerDynamic" )[i];
			
			for ( int i = 0; i < loopCount; i++ )
				strikerTextureArray[loopCount+i] = textureMap.get( "highstrikerDynamic" )[loopCount-1-i];
			
			TTCStriker.setTextureArray( strikerTextureArray );
			startingTime = getTime();
			thePuck.dy = coinz / (1.00 * TTCStriker.getCoin() );
			
			TTCStriker.textureDuration = 1;
			if (thePuck.dy > 1 )
				thePuck.dy = 1;
			thePuck.dy = thePuck.dy * ( 4.94 + ( ( 1 - ( coinz / TTCStriker.getCoin() ) ) + ( 1 - ( coinz / TTCStriker.getCoin() ) ) ) / 2 ) ;
			TTCStriker.currImage = 0;
			TTCStriker.dynamic = true;
		}
		
		if ( bFinished && thePlayer.getX() > theBarker.getX() )
		{
			thePlayer.setX( thePlayer.getX() - 1 );
			thePlayer.setTextureArray( textureMap.get( "bagDownLeft" ) );
		}
		else if ( bFinished )
		{
			thePlayer.setTextureArray( textureMap.get( "bagDownRight" ) );
			strikerState = Striker.OUTRO;
			textRow = 0;
			textAmt = 0;
			if ( coinTally >= TTCStriker.getCoin() )
				bPassed = true;
			bStrikerAnimation = false;
		} 
		else if ( thePlayer.getX() < TTCStriker.getX() )	// Get the bag in position to jump
		{
			thePlayer.setX( thePlayer.getX() + 1 );
			thePlayer.setTextureArray( textureMap.get( "bagDownRight" ) );
		}
		else if ( thePlayer.grounded && !bWeighing )	// jump
			jump();
		else if ( bWeighing )
		{
			thePuck.dy = thePuck.dy - .000035 * ( getTime() - startingTime );
			
			if ( TTCStriker.getCurrTimer() == 0)
			{
				int testDuration =  4 /  (int) Math.ceil( Math.abs( thePuck.dy ) );
				testDuration++;
				if ( testDuration > TTCStriker.textureDuration )
					TTCStriker.textureDuration++;
				else if ( testDuration < TTCStriker.textureDuration )
					TTCStriker.textureDuration--;
				 
			}
			
			//System.out.println("duration: " + TTCStriker.textureDuration );
			//TTCStriker.textureDuration = 1;
			thePuck.setY( thePuck.getY() - thePuck.dy );
			if ( thePuck.getY() - thePuck.save_y > 1)
			{
				thePuck.setY( thePuck.save_y );
				bFinished = true;
				thePuck.dy = 0;
			}
		}
			
		
		if ( ( thePlayer.x + translate_x ) > (  WIDTH / 2 ) )
			translate_x -= 2;
		
	
	}
	
	/* strikerText
	 * 		Draws text for the striker animation
	 * 
	 */
	private void strikerText( String[] text )
	{
		
		theTextBox.draw(); 
		carniTalk.draw();
		if ( textAmt != -1 )
		{
			if ( ( getTime() - textStartTime ) > textAmt * 40 )
				textAmt++;
			
			if ( textAmt < text[textRow].length() )
				uniFont.drawString( 256 + 16 * 8, 144 + textRow * CHAR_SIZE, text[textRow].substring( 0, textAmt ) );
			else
			{
				textRow++;
				textAmt = 0;
				textStartTime = getTime();
			}
		}
		
		for ( int i = 0; i < textRow; i++ )
			uniFont.drawString( 384, 144 + i * CHAR_SIZE, text[i] );
		
		if ( textRow == 5 )
		{
			textAmt = -1;
			bNoInput = false;
			barkerSpacebar.draw();
		}
		
	}
	
	/* saveGame
	 * 		Handles saving the game
	 * 
	 */
	private void saveGame( String fileName )
	{
		try 
		{
			FileOutputStream fos = new FileOutputStream( fileName );
		    DataOutputStream dos = new DataOutputStream( fos );
		    
		    // Write main information
		    dos.writeBytes( coinTally + "\n" );
		    dos.writeBytes( levelAccess + "\n" );
		    dos.writeBytes( totalTime + "\n" );
		    
		    // Write level information
		    for ( int i = 0; i < 20 ; i++)
		    {
		    	dos.writeBytes( coinTotals[i]+"\n" );
		    	dos.writeBytes( timeTotals[i]+"\n" );
		    }
		    
		    dos.close();
		    fos.close();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	/* loadGame
	 * 		Handles loading the game
	 * 
	 */
	private void loadGame( String fileName )
	{
		try
		{
			Scanner s = new Scanner( new File( fileName ) );
		    
		    coinTally = Integer.parseInt( s.nextLine() );
		    levelAccess = Integer.parseInt( s.nextLine() );
		    totalTime = Long.parseLong( s.nextLine() );
		    
		    for ( int i = 0; i < 20 ; i++ )
		    {
		    	coinTotals[i] = Integer.parseInt( s.nextLine() );
		    	timeTotals[i] = Long.parseLong( s.nextLine() );
		    }
		    
		    s.close();
		}
		catch ( IOException e ) 
		{
			e.printStackTrace();
		}
	}
	
	/* playLevel
	 * 		This kicks off the current level we will play, starting GameOn
	 * 
	 */
	private void playLevel()
	{
		GameOn level1 = new GameOn( levelNames[levelIndex], textureMap );
		
		if ( level1.bPassed )
		{			
			if (levelAccess < levelIndex && levelIndex < 24 )
				levelAccess = levelIndex ;
			
			if ( level1.coinCount > coinTotals[levelIndex] )
				coinTotals[levelIndex] = level1.coinCount;
			
			if ( level1.winTime - level1.startTime < timeTotals[levelIndex] || timeTotals[levelIndex] == (long) 0 )
				timeTotals[levelIndex] = level1.winTime - level1.startTime;
			
			coinTally = 0;
			totalTime = 0;
			
			for ( int i = 0 ; i < coinTotals.length ; i++)
			{
				coinTally += coinTotals[i];
				totalTime += timeTotals[i];
				System.out.println("cointotal: " + coinTally + "Level " + i + ": " + coinTotals[i] + "\n");
				saveGame( currentFile );
				
			}
		}
		state = State.WORLD;
		pressText.timer = 1; //TODO: necessary?
	}
	
	/* drawStats
	 * 		This is the main rendering function for drawing statistics
	 * 
	 */
	private void drawStats()
	{
		theWallpaper.draw();
		thePauseFade.draw();
		escapeKey.draw();
		
		for ( int i = 0; i < statsShapeMap.size(); i++ )
			for ( int j = 0; j < statsShapeMap.get( i ).length; j++ )
				statsShapeMap.get(i)[j].draw();
		
		if ( currentWorld != 1 )
			statsLeft.draw();
		
		if ( currentWorld != 6 )
			statsRight.draw();

		worldText.draw();
	}
	
	/* drawGameSlots
	 * 		Handles rendering of menu screen of selecting and modifying game slots
	 * 
	 */
	private void drawGameSlots()
	{
		int signChange;
		boolean bAnimationCondition;
		
		theWallpaper.draw();
		
		// Set up how the SlotStats will be manipulated
		if ( bSlotsAnimation )
		{
			bAnimationCondition = playerSlotStats.get( 0 ).getX() > playerSlotStats.get( 0 ).save_x;
			signChange = -1;
			
			if ( MM_slot2Text.getY() < 250 && MM_row == 1 )
					MM_slot2Text.setY( MM_slot2Text.getY() + 10 );
			
			if ( MM_slot3Text.getY() < 300 && MM_row != 3 )
				MM_slot3Text.setY( MM_slot3Text.getY() + 10 );
		}
		else
		{
			bAnimationCondition = playerSlotStats.get( 0 ).getX() < playerSlotStats.get( 0 ).startX;
			signChange = 1;
			
			if ( MM_slot2Text.getY() > 200 && MM_row == 1 )
				MM_slot2Text.setY( MM_slot2Text.getY() - 10 );
			
			if ( MM_slot3Text.getY() > 250 && MM_row != 3 )
				MM_slot3Text.setY( MM_slot3Text.getY() - 10 );
			
			MM_backText.draw();
		}
		
		// Animate the players stats for the current save slot
		for ( int i = 0; i < playerSlotStats.size(); i++ )
		{
			if ( bAnimationCondition )
				playerSlotStats.get( i ).setX( playerSlotStats.get( i ).getX() + signChange * ( amt + ( i / GS_WORDSINDEX ) ) );
			
			playerSlotStats.get( i ).draw();
		}	
		
		// Animate all the info off the screen, then cut to the World
		if ( bGetReadyToPlay && !bAnimationCondition )
		{
			state = State.WORLD;
			worldShapes = worldShapeMap.get( currentWorld - 1 );
			thePlayer.setX(41);
			thePlayer.setY( worldShapes[0].getY() - thePlayer.getHeight() );
			bGetReadyToPlay = false;
		}
			
		// We only want to change this as we are moving
		if ( bAnimationCondition )
			amt = amt + signChange * .2;

		// Draw the slot information
		MM_slot1Text.draw();
		MM_slot2Text.draw();
		MM_slot3Text.draw();
		MM_arrow.draw();
	}

	/* drawMain
	 * 		Handles rendering of the main menu screen
	 * 
	 */
	private void drawMain()
	{
		theWallpaper.draw();
		MM_gameText.draw();
		MM_controlsText.draw();
		MM_exitText.draw();
		MM_arrow.draw();
	}

	/* drawWelcome
	 * 		Handles rendering of the welcome screen
	 *
	 */
	private void drawWelcome()
	{
		theWallpaper.draw();
		int pressStage = pressText.draw();
		if ( pressStage == 4 )
			theSpacebar.draw();
	}

	/* drawIntro
	 * 		Handles rendering of the intro cutscene
	 * 		TODO: add made by... finish actual intro
	 */
	private void drawIntro()
	{	
		// Get the previous time we entered
		long introEndTime = getTime();
		
		if ( introPrevTime == 0 )
			introPrevTime = getTime();
		
		if ( ( introEndTime - introStartTime ) > 3000 )
		{
			if ( theIntro.getHeight() < 780 )
			{
				theIntro.setHeight( theIntro.getHeight() + 4 );
				theIntro.setWidth( theIntro.getWidth() + 6.8 );
				theIntro.setX( theIntro.getX() - 2.68 );
				theIntro.setY( theIntro.getY() - 2.65 );
			}
			
			theIntro.draw();
			
			if ( ( introEndTime - introStartTime ) > 5500 )
			{
				
				if ( ( introEndTime - introPrevTime ) >= 130 && theIntro.getType() < 42 )
				{
					theIntro.setType( theIntro.getType() + 1 );
					introPrevTime = 0;
				}
				
				if ( theIntro.getType() > 25 )
				{
					theShard.draw();
					if ( theShard.getY() < 400 )
						theShard.setY( theShard.getY() + 1 );
				}
			}
		}
		
		if ( ( introEndTime - introStartTime ) > INTRO_TIME )
		{
			state = State.WELCOME;
			theWallpaper.faded = false;
		}
	}

	/* getTime
	 * 		Returns the current system time
	 * 
	 */
	private long getTime()
	{
		return ( Sys.getTime() * 1000 / Sys.getTimerResolution() );
	}
	
	

	/* loadTexture
	 * 		Use TextureLoader to load all of the games images
	 * 
	 */
	public static Texture loadTexture( String key )
	{
		try
		{
			return TextureLoader.getTexture( "png", new FileInputStream( new File( "res/img/" + key + ".png" ) ) );
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
}