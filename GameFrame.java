import com.sun.jnlp.JNLPRandomAccessFileImpl;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class GameFrame extends JFrame implements Runnable, KeyListener {
  	private static final int NUM_BUFFERS = 2;	// used for page flipping

	private int pWidth, pHeight;     		// dimensions of screen
	private int prevPY, prevPX, prevEX, prevEY;

	private Thread gameThread = null;            	// the thread that controls the game
	private volatile boolean running = false;    	// used to stop the animation thread

	private Animation animation = null;
	private Image bgImage;				// background image
	private Classes.Mage player;
	private Entity enemy;
	AudioClip playSound = null;			// theme sound
	private ArrayList<Image> tiles;
	private TileMap map;
	private TileMapRenderer renderer;
	private boolean fight = false;
	private boolean resetPos = false;
	private int prevX;
	private int prevY;
	private int runAttempt = 1;
	private int menu = 0;
	private int pDamage = 0;
	private int eDamage = 0;
  	// used at game termination
	private boolean finishedOff = false;

	// used by the quit 'button'
	private volatile boolean isOverQuitButton = false;
	private Rectangle quitButtonArea;

	// used by the pause 'button'
	private volatile boolean isOverPauseButton = false;
	private Rectangle pauseButtonArea;
	private volatile boolean isPaused = false;

	// used by the stop 'button'
	private volatile boolean isOverStopButton = false;
	private Rectangle stopButtonArea;
	private volatile boolean isStopped = false;

	// used by the show animation 'button'
	private volatile boolean isOverShowAnimButton = false;
	private Rectangle showAnimButtonArea;
	private volatile boolean isAnimShown = false;

	// used by the pause animation 'button'
	private volatile boolean isOverPauseAnimButton = false;
	private Rectangle pauseAnimButtonArea;
	private volatile boolean isAnimPaused = false;

	private volatile boolean isOverFightButton = false;
	private Rectangle fightButtonArea;

	private volatile boolean isOverRunButton = false;
	private Rectangle runButtonArea;

	private volatile boolean isOverItemButton = false;
	private Rectangle itemButtonArea;

	private volatile boolean isOverStatsButton = false;
	private Rectangle statsButtonArea;

	private volatile boolean isOverScanButton = false;
	private Rectangle scanButtonArea;

	// used for full-screen exclusive mode  
	private GraphicsDevice device;
	private Graphics gScr;
	private BufferStrategy bufferStrategy;

	private Long Timer;

	public GameFrame () {
		super("Bat and Ball Game: Full Screen Exclusive Mode");

		initFullScreen();

		// create game sprites
		Classes classes = new Classes(5,5);
		player = classes.new Mage(5,5);

		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				testMousePress(e.getX(), e.getY()); 
			}
		});

		addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseMoved(MouseEvent e) {
				testMouseMove(e.getX(), e.getY()); 
			}
		});

		addKeyListener(this);			// respond to key events

		// specify screen areas for the buttons
		//  leftOffset is the distance of a button from the left side of the window

		int leftOffset = (pWidth - (5 * 150) - (4 * 20)) / 2;
		pauseButtonArea = new Rectangle(leftOffset, pHeight-60, 150, 40);

		leftOffset = leftOffset + 170;
		stopButtonArea = new Rectangle(leftOffset, pHeight-60, 150, 40);

		leftOffset = leftOffset + 170;
		showAnimButtonArea = new Rectangle(leftOffset, pHeight-60, 150, 40);

		leftOffset = leftOffset + 170;
		pauseAnimButtonArea = new Rectangle(leftOffset, pHeight-60, 150, 40);

		leftOffset = leftOffset + 170;
		quitButtonArea = new Rectangle(leftOffset, pHeight-60, 150, 40);

		leftOffset = ((pWidth - (5 * 150) - (4 * 20)) / 2) + 825/2;
		fightButtonArea = new Rectangle(leftOffset + 25,pHeight-300, 25,25);

		runButtonArea = new Rectangle(leftOffset + 156,pHeight-300, 25,25);

		itemButtonArea =new Rectangle(leftOffset + 287,pHeight-300, 25,25);

		statsButtonArea =new Rectangle(leftOffset + 25,pHeight-250, 25,25);;

		scanButtonArea =new Rectangle(leftOffset + 156,pHeight-250, 25,25);

		tiles = new ArrayList<>();
		renderer = new TileMapRenderer();

		loadImages();
		try{
			map = loadMap("maps/map1.txt");
		}catch (Exception e){
			System.out.println("Errorrrrr!!!!111 " + e);
		}
		player.setAnimation(0);

		Timer = System.currentTimeMillis() + (5*60*1000);

		loadClips();
		startGame();
	}

	private void initFullScreen() {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		device = ge.getDefaultScreenDevice();

		setUndecorated(true);	// no menu bar, borders, etc.
		setIgnoreRepaint(true);	// turn off all paint events since doing active rendering
		setResizable(false);	// screen cannot be resized
		
		if (!device.isFullScreenSupported()) {
			System.out.println("Full-screen exclusive mode not supported");
			System.exit(0);
		}

		device.setFullScreenWindow(this); // switch on full-screen exclusive mode

		// we can now adjust the display modes, if we wish

		showCurrentMode();

		pWidth = getBounds().width;
		pHeight = getBounds().height;

		try {
			createBufferStrategy(NUM_BUFFERS);
		}
		catch (Exception e) {
			System.out.println("Error while creating buffer strategy " + e); 
			System.exit(0);
		}

		bufferStrategy = getBufferStrategy();
	}

	// this method creates and starts the game thread

	private void startGame() { 
		if (gameThread == null || !running) {
			gameThread = new Thread(this);
			gameThread.start();
			playSound.loop();
		}
	}
    
	/* This method handles mouse clicks on one of the buttons
	   (Pause, Stop, Show Anim, Pause Anim, and Quit).
	*/

	private void testMousePress(int x, int y) {

		if (isStopped && !isOverQuitButton) 	// don't do anything if game stopped
			return;

		if (isOverStopButton) {			// mouse click on Stop button
			isStopped = true;
			isPaused = false;
		}
		else
		if (isOverPauseButton) {		// mouse click on Pause button
			isPaused = !isPaused;     	// toggle pausing
		}
		else
		if (isOverShowAnimButton && !isPaused) {// mouse click on Show Anim button
			if (isAnimShown)		// make invisible if visible
		 		isAnimShown = false;
			else {				// make visible if invisible
				isAnimShown = true;
				isAnimPaused = false;	// always animate when making visible
			}
		}
		else
		if (isOverPauseAnimButton) {		// mouse click on Pause Anim button
			isAnimPaused = !isAnimPaused;	// toggle pausing
		}
		else if (isOverQuitButton) {		// mouse click on Quit button
			running = false;		// set running to false to terminate
		}

		if (isOverFightButton){
			if (enemy.isAlive()) {
				FIGHT(enemy);
				menu = 1;
			}
		}
		if (isOverRunButton){
			int num1 = enemy.Speed / 4;
			if (num1 < 1)
				num1 = 1;
			int chance = (((player.Speed * 32) / num1) + 30) * runAttempt / 256;
			int willRun = new Random().nextInt(255);
			if (chance < willRun)
				fight = false;
			else{
				enemy.attack(player);
				if (!player.isAlive()){
					isStopped = true;
					fight = false;
					return;
				}
			}
		}

		if (isOverItemButton){

		}
  	}


	/* This method checks to see if the mouse is currently moving over one of
	   the buttons (Pause, Stop, Show Anim, Pause Anim, and Quit). It sets a
	   boolean value which will cause the button to be displayed accordingly.
	*/

	private void testMouseMove(int x, int y) { 
		if (running) {
			isOverPauseButton = pauseButtonArea.contains(x,y) ? true : false;
			isOverStopButton = stopButtonArea.contains(x,y) ? true : false;
			isOverShowAnimButton = showAnimButtonArea.contains(x,y) ? true : false;
			isOverPauseAnimButton = pauseAnimButtonArea.contains(x,y) ? true : false;
			isOverQuitButton = quitButtonArea.contains(x,y) ? true : false;
			isOverFightButton = fightButtonArea.contains(x,y) ? true : false;
			isOverRunButton = runButtonArea.contains(x,y) ? true : false;
			isOverItemButton = itemButtonArea.contains(x,y) ? true : false;
			isOverScanButton = scanButtonArea.contains(x,y) ? true : false;
			isOverStatsButton = statsButtonArea.contains(x,y) ? true : false;
		}
	}

	// implementation of KeyListener interface

	public void keyPressed (KeyEvent e) {

		int keyCode = e.getKeyCode();
         
		if ((keyCode == KeyEvent.VK_ESCAPE) || (keyCode == KeyEvent.VK_Q) ||
             	   (keyCode == KeyEvent.VK_END)) {
           		running = false;		// user can quit anytime by pressing
			return;				//  one of these keys (ESC, Q, END)
         	}	

		if (isPaused || isStopped)
			// don't do anything if either condition is true
			return;

		if (keyCode == KeyEvent.VK_LEFT) {
			player.moveLeft();
		}else if (keyCode == KeyEvent.VK_UP) {
			player.moveUp();
		}else if (keyCode == KeyEvent.VK_RIGHT) {
			player.moveRight();
		}else if (keyCode == KeyEvent.VK_DOWN) {
			player.moveDown();
		}else if (keyCode == KeyEvent.VK_H){
			player.setCurrHP(player.getHP());
		}

		if (new Random().nextInt(100)+1 <= 20)
			fight = true;
	}

	public void keyReleased (KeyEvent e) {

	}

	public void keyTyped (KeyEvent e) {

	}


	// implmentation of MousePressedListener interface

	// implmentation of MouseMotionListener interface

	// The run() method implements the game loop.

	public void run() {

		running = true;
		try {
			while (running && (Timer > System.currentTimeMillis())) {
	  			gameUpdate();
	  			screenUpdate();
				Thread.sleep(100);
			}
			Thread.sleep(1000);
		}
		catch(InterruptedException e) {}
		finishOff();
	}


	// This method updates the game objects (animation and ball)

	private void gameUpdate() {
		if (!isPaused) {
			if (!isStopped) {
				player.update();
				if (enemy != null)
					enemy.update();
			}
		}
  	}


	// This method updates the screen using double buffering / page flipping

	private void screenUpdate() { 

		try {
			gScr = bufferStrategy.getDrawGraphics();

			if (!fight) {
				if (resetPos == true){
					player.setX(prevX);
					player.setY(prevY);
					resetPos = false;
				}
				OverWorldRender(gScr);
			}

			if (fight){
				FightRenderer(gScr);
			}

			gScr.setColor(Color.GREEN);
			gScr.setFont(new Font("SansSerif", Font.BOLD, 20));
			gScr.drawString(Long.toString(((Timer - System.currentTimeMillis()) / 1000)/60)+ ":" + Long.toString(((Timer - System.currentTimeMillis()) / 1000)%60),pWidth/2-20,20);


			if (!player.isAlive()){
				try{
					gameOverMessage(gScr);
					Thread.sleep(1000);
					running = false;
				}catch (Exception E){}
			}

			gScr.dispose();
			if (!bufferStrategy.contentsLost())
				bufferStrategy.show();
			else
				System.out.println("Contents of buffer lost.");

			// Sync the display on some systems.
			// (on Linux, this fixes event queue problems)

			Toolkit.getDefaultToolkit().sync();
		}
		catch (Exception e) { 
			e.printStackTrace();  
			running = false; 
		} 
	}

	/* This method renders all the game entities to the screen: the
	   background image, the buttons, ball, bat, and the animation.
	*/

	private void OverWorldRender(Graphics gScr){
 
		//gScr.drawImage (bgImage, 0, 0, pWidth, pHeight, null);
							// draw the background image
		player.setDX(5);
		player.setDY(5);
		renderer.draw((Graphics2D)gScr,map,pWidth,pHeight);
		enemy = null;
		drawButtons(gScr);			// draw the buttons

		gScr.setColor(Color.GRAY);
		if (isStopped){				// display game over message
			gameOverMessage(gScr);
			running = false;
		}
	}

	private void FightRenderer(Graphics gScr){
		if(enemy==null) {
			enemy = new Monster(0, 0, player.Level);
			loadAnimation(enemy);
			enemy.setAnimation(0);
		}
		gScr.drawImage (bgImage, 0, 0, pWidth, pHeight, null);
		drawButtons(gScr);
		drawBattleButtons(gScr,menu);

		prevPX = player.getX();
		prevPY = player.getY();

		player.setAnimationFromPrevious(2);
		enemy.setAnimationFromPrevious(1);

		setFightPositions(enemy);

		player.draw(gScr);
		enemy.draw(gScr);

		player.drawString(gScr,0,20);
		enemy.drawString(gScr, pWidth - 125, 20);

		gScr.setColor(Color.GRAY);
		if (isStopped){				// display game over message
			gameOverMessage(gScr);
			running = false;
		}
	}

	public void FIGHT(Entity en){
		if (player.getSpeed() > en.getSpeed()){
			pDamage = player.attack(en);
			if(!en.isAlive()){
				fight = false;
				player.increseStats(en.Level, false);
				return;
			}
			eDamage = en.attack(player);
			if (!player.isAlive()){
				isStopped = true;
				fight = false;
				return;
			}
		}else{
			eDamage = en.attack(player);
			if (!player.isAlive()){
				isStopped = true;
				fight = false;
				return;
			}
			pDamage = player.attack(en);
			if(!en.isAlive()){
				fight = false;
				player.increseStats(en.Level, false);
				return;
			}
		}
	}

	public void setFightPositions(Entity en){
		if (resetPos == false){
			prevX = player.getX();
			prevY = player.getY();
			resetPos = true;
		}

		player.setDX(0);
		player.setDY(0);

		player.setX(pWidth/2/2);
		player.setY(585);

		en.setDX(0);
		en.setDY(0);

		en.setX(pWidth/2/2 + pWidth/2);
		en.setY(585);
	}

	/* This method draws the buttons on the screen. The text on a button
	   is highlighted if the mouse is currently over that button AND if
	   the action of the button can be carried out at the current time.
	*/

	private void drawBattleButtons(Graphics g, int menu){
		Font oldFont, newFont;

		oldFont = g.getFont();		// save current font to restore when finished

		newFont = new Font ("TimesRoman", Font.ITALIC + Font.BOLD, 25);
		g.setFont(newFont);		// set this as font for text on buttons

		int leftOffset = (pWidth - (5 * 150) - (4 * 20)) / 2;

		textBox(g,leftOffset);
		if (menu == 0)
			menuButtons(g, leftOffset);
		else if (menu == 1)
			damageDealt(g, leftOffset);

	}

	private void textBox(Graphics g, int leftOffset){
		g.setColor(new Color(31,40,37));
		g.fillRect(leftOffset, pHeight-350, 825, 150);
		g.setColor(new Color(192,167,83));
		g.fillRect(leftOffset+5, pHeight-345, 815, 140);
		g.setColor(Color.WHITE);
		g.fillRect(leftOffset+15, pHeight-335, 795, 120);
		g.setColor(new Color(40,77,103));
		g.fillRect(leftOffset+18, pHeight-332, 789, 114);
	}

	private void menuButtons(Graphics g, int leftOffset){
		leftOffset = leftOffset + 825/2;
		g.setColor(new Color(128,126,166));
		g.fillRect(leftOffset, pHeight-350, 413, 150);
		g.setColor(Color.WHITE);
		g.fillRect(leftOffset + 10, pHeight-340, 393, 130);

		if (isOverFightButton && !isStopped)
			g.setColor(Color.BLUE);
		else
			g.setColor(Color.GRAY);
		g.drawString("Fight",fightButtonArea.x,fightButtonArea.y);
		if (isOverRunButton && !isStopped)
			g.setColor(Color.BLUE);
		else
			g.setColor(Color.GRAY);
		g.drawString("Run",runButtonArea.x,runButtonArea.y);
		if (isOverItemButton && !isStopped)
			g.setColor(Color.BLUE);
		else
			g.setColor(Color.GRAY);
		g.drawString("Item",itemButtonArea.x,itemButtonArea.y);
		if (isOverStatsButton && !isStopped)
			g.setColor(Color.BLUE);
		else
			g.setColor(Color.GRAY);
		g.drawString("Stats",statsButtonArea.x,statsButtonArea.y);
		if (isOverScanButton && !isStopped)
			g.setColor(Color.BLUE);
		else
			g.setColor(Color.GRAY);
		g.drawString("Scan",scanButtonArea.x,scanButtonArea.y);
	}

	private void damageDealt(Graphics g, int leftOffset){
		g.setColor(Color.WHITE);
		Font newFont = new Font ("TimesRoman", Font.ITALIC + Font.BOLD, 35);
		g.setFont(newFont);


		menu = 0;
	}

	private void drawButtons (Graphics g) {
		Font oldFont, newFont;

		oldFont = g.getFont();		// save current font to restore when finished
	
		newFont = new Font ("TimesRoman", Font.ITALIC + Font.BOLD, 18);
		g.setFont(newFont);		// set this as font for text on buttons

		g.setColor(Color.black);	// set outline colour of button

		// draw the pause 'button'

		g.setColor(Color.BLACK);
		g.drawOval(pauseButtonArea.x, pauseButtonArea.y, 
			   pauseButtonArea.width, pauseButtonArea.height);

		if (isOverPauseButton && !isStopped)
			g.setColor(Color.WHITE);
		else
			g.setColor(Color.RED);	

		if (isPaused && !isStopped)
			g.drawString("Paused", pauseButtonArea.x+45, pauseButtonArea.y+25);
		else
			g.drawString("Pause", pauseButtonArea.x+55, pauseButtonArea.y+25);

		// draw the stop 'button'

		g.setColor(Color.BLACK);
		g.drawOval(stopButtonArea.x, stopButtonArea.y, 
			   stopButtonArea.width, stopButtonArea.height);

		if (isOverStopButton && !isStopped)
			g.setColor(Color.WHITE);
		else
			g.setColor(Color.RED);

		if (isStopped)
			g.drawString("Stopped", stopButtonArea.x+40, stopButtonArea.y+25);
		else
			g.drawString("Stop", stopButtonArea.x+60, stopButtonArea.y+25);

		// draw the show animation 'button'

		g.setColor(Color.BLACK);
		g.drawOval(showAnimButtonArea.x, showAnimButtonArea.y, 
			   showAnimButtonArea.width, showAnimButtonArea.height);

		if (isOverShowAnimButton && !isPaused && !isStopped)
			g.setColor(Color.WHITE);
		else
			g.setColor(Color.RED);
      		g.drawString("Show Anim", showAnimButtonArea.x+35, showAnimButtonArea.y+25);

		// draw the pause anim 'button'

		g.setColor(Color.BLACK);
		g.drawOval(pauseAnimButtonArea.x, pauseAnimButtonArea.y, 
			   pauseAnimButtonArea.width, pauseAnimButtonArea.height);

		if (isOverPauseAnimButton && isAnimShown && !isPaused && !isStopped)
			g.setColor(Color.WHITE);
		else
			g.setColor(Color.RED);

		if (isAnimShown && isAnimPaused && !isStopped)
			g.drawString("Anim Paused", pauseAnimButtonArea.x+30, pauseAnimButtonArea.y+25);
		else
			g.drawString("Pause Anim", pauseAnimButtonArea.x+35, pauseAnimButtonArea.y+25);

		// draw the quit 'button'

		g.setColor(Color.BLACK);
		g.drawOval(quitButtonArea.x, quitButtonArea.y, 
			   quitButtonArea.width, quitButtonArea.height);
		if (isOverQuitButton)
			g.setColor(Color.WHITE);
		else
			g.setColor(Color.RED);

		g.drawString("Quit", quitButtonArea.x+60, quitButtonArea.y+25);
		g.setFont(oldFont);		// reset font

	}

	// displays a message to the screen when the user stops the game

	private void gameOverMessage(Graphics g) {
		
		Font font = new Font("SansSerif", Font.BOLD, 24);
		FontMetrics metrics = this.getFontMetrics(font);

		String msg = "Game Over. Thanks for playing!";

		int x = (pWidth - metrics.stringWidth(msg)) / 2; 
		int y = (pHeight - metrics.getHeight()) / 2;

		g.setColor(Color.BLUE);
		g.setFont(font);
		g.drawString(msg, x, y);

	}

	/* This method performs some tasks before closing the game.
	   The call to System.exit() should not be necessary; however,
	   it prevents hanging when the game terminates.
	*/

	private void finishOff() { 
    		if (!finishedOff) {
			finishedOff = true;
			restoreScreen();
			System.exit(0);
		}
	}

	/* This method switches off full screen mode. The display
	   mode is also reset if it has been changed.
	*/

	private void restoreScreen() { 
		Window w = device.getFullScreenWindow();

		if (w != null)
			w.dispose();
		
		device.setFullScreenWindow(null);
	}

	// This method provides details about the current display mode.

	private void showCurrentMode() {
		DisplayMode dm = device.getDisplayMode();
		System.out.println("Current Display Mode: (" + 
                           dm.getWidth() + "," + dm.getHeight() + "," +
                           dm.getBitDepth() + "," + dm.getRefreshRate() + ")  " );
  	}

  	public void loadAnimation(Entity x){

		Image stripImage = loadImage("images/PC.png");

		int PlayableWidth = (int) stripImage.getWidth(null) / 4;
		int PlayableHeight = (int) stripImage.getHeight(null) / 2;

		Random rand = new Random();
		int charColor = rand.nextInt(4);
		int charHat = rand.nextInt(2);

		int imageHeight = PlayableHeight / 4;
		int imageWidth = PlayableWidth / 3;

		int startHeight = 0;
		int startWidth = 0;

		if (charColor == 1) {
			startHeight = 0;
			startWidth = PlayableWidth * 2;
		} else if (charColor == 2) {
			startHeight = PlayableHeight;
			startWidth = 0;
		} else if (charColor == 3) {
			startHeight = PlayableHeight;
			startWidth = PlayableWidth * 2;
		}

		if (charHat == 1) {
			startWidth += PlayableWidth;
		}

		// create animation object and insert frames
		int X = rand.nextInt(pWidth - 75);
		int Y = rand.nextInt(pWidth - 75);

		for (int i = 0; i < 4; i++) {
			animation = new Animation(this, X, Y, 0, 0, 75, 75, "images/player1.png");
			for (int j = 0; j < 4; j++) {
				BufferedImage frameImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
				/*
				BufferedImage frameImage = GraphicsEnvironment.getLocalGraphicsEnvironment().
							getDefaultScreenDevice().getDefaultConfiguration().
							createCompatibleImage(imageWidth, imageHeight);
				*/
				Graphics2D g = (Graphics2D) frameImage.getGraphics();
				if (j <= 2) {
					g.drawImage(stripImage,
							0, 0, imageWidth, imageHeight,
							(j * imageWidth) + startWidth, (i * imageHeight) + startHeight, (j * imageWidth) + imageWidth + startWidth, (i * imageHeight) + imageHeight + startHeight,
							null);
				} else {
					g.drawImage(stripImage,
							0, 0, imageWidth, imageHeight,
							(1 * imageWidth) + startWidth, (i * imageHeight) + startHeight, (1 * imageWidth) + imageWidth + startWidth, (i * imageHeight) + imageHeight + startHeight,
							null);
				}

				animation.addFrame(frameImage, 250);
			}
			x.addAnimation(animation);
		}
	}

	public void loadImages() {
		bgImage = loadImage("images/background.jpg");

		loadAnimation(player);
		//loadAnimation(enemy);

		Image mapImage = loadImage("images/TileMap.png");

		int tileWidth = (int) mapImage.getWidth(null) / (4*8);
		int tileHeight = (int) mapImage.getHeight(null) / (6*4);

		int terrainWidth = (int) mapImage.getWidth(null) / 8;
		int terrainHeight = (int) mapImage.getHeight(null) / 4;

		for (int i=0; i<6; i++){
			for (int j=0; j<4; j++){
				BufferedImage frameImage = new BufferedImage(pWidth/(4*8), pHeight/(6*3), BufferedImage.TYPE_INT_ARGB);

				Graphics2D g = (Graphics2D) frameImage.getGraphics();

				g.drawImage(mapImage,
						0, 0, pWidth/(4*8), pHeight/(6*3),
						j * tileWidth + terrainWidth * 2, i * tileHeight, (j * tileWidth) + tileWidth + terrainWidth * 2, (i * tileHeight) + tileHeight,
						null);

				tiles.add(frameImage);
			}
		}
	}

	private boolean[][] generateMap(MapGen x){
		//Create a new map
		boolean[][] cellmap = new boolean[x.getHeight()][x.getWidth()];
		//Set up the map with random values
		cellmap = x.initialiseMap(cellmap);
		//And now run the simulation for a set number of steps
		for(int i=0; i<10; i++){
			cellmap = x.doSimulationStep(cellmap);
		}

		for (int i = 0; i < x.getHeight(); i++){
			for (int j = 0; j< x.getWidth(); j++){
				if (cellmap[i][j] == true)
					System.out.print("0");
				else
					System.out.print(new Random().nextInt(5));
			}
			System.out.println("");
		}

		return cellmap;
	}

	private TileMap loadMap(String filename) throws IOException {
		/*
		ArrayList<String> lines = new ArrayList<>();
		int width = 0;
		int height = 0;
		// read every line in the text file into the list
		BufferedReader reader = new BufferedReader(
				new FileReader(filename));
		while (true) {
			String line = reader.readLine();
			// no more lines to read
			if (line == null) {
				reader.close();
				break;
			}
			// add every line except for comments
			if (!line.startsWith("#")) {
				lines.add(line);
				width = Math.max(width, line.length());
			}
		}
		// parse the lines to create a TileEngine
		height = lines.size();
		*/
		MapGen x = new MapGen(50,50);
		boolean[][] cellmap = generateMap(x);
		TileMap newMap = new TileMap(x.getWidth(), x.getHeight());
		for (int y=0; y<x.getHeight(); y++) {
			boolean[] line = cellmap[y];
			for (int z=0; z<x.getWidth(); z++) {
				//char ch = line.charAt(x);
				// check if the char represents tile A, B, C, etc.
				//int tile = ch - 'A';
				if (line[y] == true) {
					newMap.setTile(z, y, (Image)tiles.get(new Random().nextInt(2)));
				}else{
					newMap.setTile(z, y, (Image)tiles.get(new Random().nextInt(2)+4));
				}
			}
		}
		// add the player to the map
		newMap.setPlayer(player);
		return newMap;
	}


	public Image loadImage (String fileName) {
		return new ImageIcon(fileName).getImage();
	}

	public void loadClips() {

		try {
			playSound = Applet.newAudioClip (
					getClass().getResource("sounds/background.wav"));

		}
		catch (Exception e) {
			System.out.println ("Error loading sound file: " + e);
		}

	}

	public void playClip (int index) {

		if (index == 1 && playSound != null)
			playSound.play();

	}

}

