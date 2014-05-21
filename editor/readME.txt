ReadME for the Editor:

// Aug 7, 2012

LevelEditor.java:
	Controls:
	- to go to the previous page
	+ to go to the next page
	
Cactus.java:
	New entity
	
instructions.txt:
	Detailed instructions to add new entities/images
	

//Tue July 31, 2012

LevelEditor.java:

	You can now click (left-click) on width and height to enter a number manually.
	The current width and height are no longer reset to default after each time a piece is placed.
		- To reset to the default size, just click on the piece in the bottom pic grid.
	You can no longer have a negative value for i.
	
	Not sure if I mentioned this before, but setting a piece to partner itself and 'start moving' as the action will cause an automatically moving piece.
	
	Labeled a few items with 'CHANGE THIS' (for easy searching) that will allow you to adjust the editor's GUI and functionality.

	Okay. That's all for now. As usual, please ask me for any bug fixes/feature additions.

//Thu July 26, 2012

So a lot of changes. Big changes. I'm calling this a Beta release because it's the first working product that works from Editor-Game and back again.

LevelEditor.java:

	Full List of Controls:
	Control-S to save. Always prompts for a name, just overwrite with same name if wanted.
	Control-O to open.
	
	WASD to move the game camera.
	IJKL to adjust the current piece's width and height.
	, to decrease the grid size.
	. to increase the grid size.
	T to toggle the grid on or off.
	[ to decrease the current instance int's value.
	] to increase the current instance int's value.
	DELETE to remove the selected piece. 
	M to pick up and move the selected piece (slightly buggy at times).
	LSHIFT to lock the mouse's x value (and adjust only y).
	RSHIFT to lock the mouse's y value (and adjust only x).
	
	Left click on instance variables to select them.
	Left click on booleans to toggle.
	Left click on Sky to set the hex value.
	Left click on Start x,y and then click again to set the starting position.
	Left click on the lower icons to change the current piece.
	
	Right click on a placed piece to select it.
	
	Once selected,
	Right click on a non-piece to deselect.
	Left click '*SET PARTNER*', and then click again on a piece to set the partner.
	Once selected piece has a partner,
	Left click on an action to select it.
	
	
	Notes:
	I gave my mom the game and she found a bug in 30 seconds. If you held jump while landing on a grav,
	you would jump through the other side and screw things up. I fixed it after like 20 minutes of hunting.

	I got really frustrated trying to add right left collision detection and didn't touch the game for a while.

// Wed July 25, 2012

GameOn:
	This is where I'm working now. 


// Fri July 20, 2012

Game2.java:
	The version I'll be working on as I re-write the engine. I've barely touched it, so don't even bother checking it out.

Level.java:
	Mostly a shell, will contain a lot of stuff. The loading for the game will be done from these.

LevelEditor.java:

	Controls:
	Right click a piece to select it (more below).
	M to move a selected piece (it picks it up and makes it your current piece).
	Delete to delete the selected piece.
	
	Notes:
	Added selecting pieces. Right click on an already-placed piece to select it. It is highlighted in red.
		- Also note the additional text that appears on the right side, associated with it.
		- Also also, right click on a non-piece to deselect.
	Added a partner system. Select a piece (right click), then click '*SET PARTNER*' and left click another piece to set the partner.
		- Once set, you have the option to select an action (by clicking on it). An arrow shows your choice.
	Added moving selected pieces.
	Added default behavior for coins to partner themselves and disappear.

// Thur July 19, 2012

Fixed the grav flip so that you can increase i (or decrease init) and it will cycle through the images.
	- I guess this way you can create trippy patterns where they all start at different times?
Added all of the dimensions for the shapes (as defaultWidth and defaultHeight in Shape).
	- If unspecified, I have it set the height/width to 40x40.
	- Also note that you can still change the width height as much as you want. I can disable this if you like, but I don't think it hurts since you can just re-click to reset it.
Added a MAX and MIN_TYPE_SIZE (currently allows 0-25).
Added a lowRes mode for 1024x600. To enable, just set the first two constants (EDITOR_RESOLUTION_X and EDITOR_RESOLUTION_Y).


// Wed July 18, 2012
More changes.

Saving and loading works! No errors with it so far in my testing.

Controls: (that were added/changed since last time)
Left click on the instance ints to select them (they capitalize and change to =), then
[ to decrease currently selected int value.
] to increase currently selected int value.
Left click on any of the booleans to toggle them.
Left click on the bottom icons (no longer right click).
	- Additionally, the should be 'highlighted; with a small ^ beneath them.

Notes:
I need you to tell me the correct sizes to use for the 'locked' width/height shapes, then I'll add that. Should be very easy to do.
Fixed a bug right before pushing that would screw buttons up after translation. This build should be totally solid.

// Wed July 18, 2012
Well that changed quickly. Now it's feature rich, prettier, and pretty badass.

Layout:
Top bar has buttons (not currently active, but easy to implement).
Center is the game screen, set to correct resolution.
Over top the game screen there is a grid of adjustable size (constrained between MIN and MAX_GRID_SIZE).
To the left of the game screen there's debug info, mostly on the current piece (including all instance ints and booleans).
Below the game screen there are two rows of click-able icons (one for each of the classes) that will change the current shape to that kind.

Controls:
WASD - to translate the camera.
IJKL - to adjust the thickness of the current shape.
, - to decrease the grid thickness.
. - to increase the grid thickness.
T - to toggle the grid on or off.
CTRL-S - Save the level.
CTRL-O - Open a level.
Left click within the game screen to place the current shape.
	- For now, clicking outside does nothing to avoid accidentally placing shapes in an untranslated-to area.
Right click one of the bottom icons to select that type of shape.

Notes:
Things should be very stable, except I haven't fully implemented saving and loading. So right now if you try to load any level with more than just boxes, it'll fail.
I think my next plan is to be able to change the instance variables so you can get different types of clouds, words, etc.

And that's it for now. Pretty goddamn awesome. I'm happy. Taking a lunch break and then some more work.

// Tues July 17, 2012
Right now it's not feature-rich at all. What you can do is use the mouse to click and place objects
(by default the left-facing bag), translate the internal screen without messing up the outside frame (WASD),
save (control-S), load (control-O), and switch to the arrow (hit X).

Basically just a prototype, but I had a nice flash of insight about how to move on and I'm very excited to
get home tonight and implement it. I'm going to really flesh out the assignPic method, which together with the
abstract pic methods in Shape (only one file- not in any of the rest), can very very powerfully make things happen.

I'm excited. I think progress is going to happen quickly. 