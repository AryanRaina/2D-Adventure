package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.JPanel;

import ai.PathFinder;
import data.SaveLoad;
import entity.Entity;
import entity.Player;
import environment.EnvironmentManager;
import tile.Map;
import tile.TileManager;
import tile_interactive.InteractiveTile;

@SuppressWarnings("serial")
public class GamePanel extends JPanel implements Runnable{

	//Screen Settings
	final int originalTileSize = 16; //16X16 tile
	final int scale = 3;
	
	public final int tileSize = originalTileSize * scale; //48X48 tile
	public final int maxScreenCol = 20;
	public final int maxScreenRow = 12;
	public final int screenWidth = tileSize * maxScreenCol; //960 pixels
	public final int screenHeight = tileSize * maxScreenRow; //576 pixels
	
	// World Settings
	public int maxWorldCol;
	public int maxWorldRow;
	public final int maxMap = 10;
	public int currentMap = 0; 
	
	//Full screen
	int screenWidth2 = screenWidth;
	int screenHeight2 = screenHeight;
	BufferedImage tempScreen;
	Graphics2D g2;
	public boolean fullScreenOn = false;
	
	int FPS = 60;
	
	//System
	public TileManager tileM = new TileManager(this);
	public KeyHandler keyH = new KeyHandler(this);
	public MouseHandler mouseH = new MouseHandler(this);
	Sound music = new Sound();
	Sound se = new Sound();
	public CollisionChecker cChecker = new CollisionChecker(this);
	public AssetSetter aSetter = new AssetSetter(this);
	public UI ui = new UI(this);
	public EventHandler eHandler = new EventHandler(this);
	Config config = new Config(this);
	public PathFinder pFinder = new PathFinder(this);
	EnvironmentManager eManager = new EnvironmentManager(this);
	Map map = new Map(this);
	SaveLoad saveLoad = new SaveLoad(this);
	public EntityGenerator eGenerator = new EntityGenerator(this);
	public CutsceneManager csManager = new CutsceneManager(this);
	Thread gameThread;
	
	//Entity and Object
	public Player player = new Player(this,keyH,mouseH);
	public Entity obj[][] = new Entity[maxMap][20];
	public Entity npc[][] = new Entity[maxMap][10];
	public Entity monster[][] = new Entity[maxMap][20];
	public InteractiveTile iTile[][] = new InteractiveTile[maxMap][50];
	public Entity projectile[][] = new Entity[maxMap][20];
//	public ArrayList<Entity> projectileList = new ArrayList<>();
	public ArrayList<Entity> particleList = new ArrayList<>();
	ArrayList<Entity> entityList = new ArrayList<>();
	
	
	//Game State
	public int gameState;
	public final int titleState = 0;
	public final int playState = 1;
	public final int pauseState = 2;
	public final int dialogueState = 3;
	public final int characterState = 4;
	public final int optionState = 5;
	public final int gameOverState = 6;
	public final int transitionState = 7;
	public final int tradeState = 8;
	public final int sleepState = 9;
	public final int mapState = 10;
	public final int cutSceneState = 11;
	
	//others

	public boolean bossBattleOn = false;
	
	//area
	public int currentArea;
	public int nextArea;
	public final int outside = 0;
	public final int indoor = 1;
	public final int dungeon = 2;
	
	
	public GamePanel() {
		this.setPreferredSize(new Dimension(screenWidth,screenHeight));
		this.setBackground(Color.black);
		this.setDoubleBuffered(true);
		this.addKeyListener(keyH);
		this.addMouseListener(mouseH);
		this.setFocusable(true);
	}

	public void setupGame() {
		aSetter.setObject();
		aSetter.setNPC();
		aSetter.setMonster();
		aSetter.setBoss();
		aSetter.setInteractiveTile();
		eManager.setup();
		//playMusic(0);
		gameState = titleState;
		currentArea = outside;
		
		tempScreen = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_ARGB);
		g2 = (Graphics2D) tempScreen.getGraphics();
		if(fullScreenOn == true) {
			setFullScreen();
		}
	}
	
	public void resetGame(boolean restart) {
		
		currentArea = outside;
		removeTempEntity();
		bossBattleOn = false;
		player.setDefaultPositions();
		player.restoreStatus();
		player.resetCounter();
		aSetter.setNPC();
		aSetter.setMonster();
		player.speed = player.defaultSpeed;

		if(restart == true) {
			player.setDefaultValues();
			aSetter.setObject();
			aSetter.setInteractiveTile();
			eManager.lighting.resetDay();
		}
	
	}
	
	public void setFullScreen() {
		
		//get screen resolution
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gd = ge.getDefaultScreenDevice();
		gd.setFullScreenWindow(Main.window);
		
		screenWidth2 = Main.window.getWidth();
		screenHeight2 = Main.window.getHeight();
	}
	
	public void startGameThread() {
		gameThread = new Thread(this);
		gameThread.start();
	}

	@Override
	public void run() {
	
//		double drawInterval = 1000000000/FPS;
//		double nextDrawTime = System.nanoTime() +drawInterval;
//		while(gameThread != null) {	
//			// 1. update character position
//			update();
//			// 2. draw the screen with updated position
//			repaint();
//			try {
//				double remainingTime = nextDrawTime - System.nanoTime();
//				remainingTime = remainingTime/1000000;
//				
//				if(remainingTime < 0) {
//					remainingTime = 0;
//				}
//				Thread.sleep((long) remainingTime);
//				nextDrawTime += drawInterval;
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}
		
		double drawInterval = 1000000000/FPS;
		double delta = 0;
		long lastTime = System.nanoTime();
		long currentTime;
		
		while(gameThread != null) {
			currentTime = System.nanoTime();
			
			delta +=(currentTime - lastTime) / drawInterval;
			
			lastTime = currentTime;
			
			if(delta >= 1) {
				update();
				//repaint();
				drawToTempScreen();
				drawToScreen();
				delta--;
			}
			
		}
		
	}
	public void update() {
		
		if(gameState == playState) {
			//Player
			player.update();	
			//NPC
			for(int i = 0; i< npc[1].length; i++) {
				if(npc[currentMap][i] != null) {
					npc[currentMap][i].update();
				}
			}
			for(int i = 0; i< monster[1].length; i++) {
				if(monster[currentMap][i] != null) {
					if(monster[currentMap][i].alive == true && monster[currentMap][i].dying == false) {
						monster[currentMap][i].update();						
					}
					if(monster[currentMap][i].alive == false) {
						monster[currentMap][i].checkDrop();
						monster[currentMap][i] = null;						
					}
				}
			}
			
			for(int i = 0; i< projectile[1].length; i++) {
				if(projectile[currentMap][i] != null) {
					if(projectile[currentMap][i].alive == true) {
						projectile[currentMap][i].update();						
					}
					if(projectile[currentMap][i].alive == false) {
						projectile[currentMap][i] = null;					
					}
				}
			}
			
			for(int i = 0; i< particleList.size(); i++) {
				if(particleList.get(i) != null) {
					if(particleList.get(i).alive == true) {
						particleList.get(i).update();						
					}
					if(particleList.get(i).alive == false) {
						particleList.remove(i);					
					}
				}
			}
			
			for(int i = 0; i < iTile[1].length; i++) {
				if(iTile[currentMap][i] !=  null) {
					iTile[currentMap][i].update();
				}
			}
			eManager.update();
		}
		if(gameState == pauseState) {
			//nothing
		}
	}
	
	public void drawToTempScreen() {
		//Title Screen
		if(gameState == titleState) {
			ui.draw(g2);
		}
		//map screen
		else if(gameState == mapState) {
			map.drawFullMapScreen(g2);
		}
		else {
			//Tile
			tileM.draw(g2);
			
			//Interactive tile
			for(int i = 0; i < iTile[1].length; i++) {
				if(iTile[currentMap][i] != null) {
					iTile[currentMap][i].draw(g2);
				}
			}
			
			//Add entities to the list
			entityList.add(player);
			
			for(int i = 0; i < npc[1].length; i++) {
				if(npc[currentMap][i] != null) {
					entityList.add(npc[currentMap][i]);
				}
			}
			for(int i = 0; i < obj[1].length; i++) {
				if(obj[currentMap][i] != null) {
					entityList.add(obj[currentMap][i]);
				}
			}
			for(int i = 0; i < monster[1].length; i++) {
				if(monster[currentMap][i] != null) {
					entityList.add(monster[currentMap][i]);
				}
			}
			
			for(int i = 0; i < projectile[1].length; i++) {
				if(projectile[currentMap][i] != null) {
					entityList.add(projectile[currentMap][i]);
				}
			}
			
			for(int i = 0; i < particleList.size(); i++) {
				if(particleList.get(i) != null) {
					entityList.add(particleList.get(i));
				}
			}
			//Sort
			Collections.sort(entityList, new Comparator<Entity>() {

				@Override
				public int compare(Entity e1, Entity e2) {
					int result = Integer.compare(e1.worldY, e2.worldY);
					return result;
				}
			});
			
			//Draw entities
			for(int i = 0; i < entityList.size(); i++) {
				entityList.get(i).draw(g2);
			}
			//Empty entityList
			entityList.clear();
			
			//environment
			eManager.draw(g2);
			
			//mini map
			map.drawMiniMap(g2);
			
			//cutscene
			csManager.draw(g2);
			
			//UI
			ui.draw(g2);
		}
		
		//Debug
		if(keyH.showDebugText == true) {
			g2.setFont(new Font("Arial",Font.PLAIN,20));
			g2.setColor(Color.white);
			int x = 10; 
			int y = 400;
			int lineHeight = 20;
			
			g2.drawString("WorldX" + player.worldX, x, y); y += lineHeight;
			g2.drawString("WorldY" + player.worldY, x, y); y += lineHeight;
			g2.drawString("Col" + (player.worldX + player.solidArea.x)/tileSize, x, y); y += lineHeight;
			g2.drawString("Row" + (player.worldY + player.solidArea.y)/tileSize, x, y);
		}

	}
	
	public void drawToScreen() {
		Graphics g = getGraphics();
		g.drawImage(tempScreen, 0, 0, screenWidth2, screenHeight2, null);
		g.dispose();
	}
	
	public void playMusic(int i) {
		music.setFile(i);
		music.play();
		music.loop();
	}
	public void stopMusic() {
		music.stop();
	}
	public void playSE(int i) {
		se.setFile(i);
		se.play();
	}
	
	public void changeArea() {
		if(nextArea != currentArea) {
			stopMusic();
			if(nextArea == outside) {
				playMusic(0);
			}
			if(nextArea == indoor) {
				playMusic(18);
			}
			if(nextArea == dungeon) {
				playMusic(19);
			}
			aSetter.setNPC();
		}
		player.speed = player.defaultSpeed;
		currentArea = nextArea;
		aSetter.setMonster();
	}
	public void removeTempEntity() {
		for(int mapNum = 0; mapNum < maxMap; mapNum++) {
			for(int i = 0; i < obj[1].length; i++) {
				if(obj[mapNum][i] != null && obj[mapNum][i].temp == true) {
					obj[mapNum][i] = null;
				}
			}
		}
	}
}
