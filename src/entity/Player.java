package entity;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import main.GamePanel;
import main.KeyHandler;
import main.MouseHandler;
import object.OBJ_Fireball;
import object.OBJ_Key;
import object.OBJ_Shield_Wood;
import object.OBJ_Sword_Normal;

public class Player extends Entity {

	KeyHandler keyH;
	MouseHandler mouseH;
	
	public final int screenX;
	public final int screenY;
	int standCounter = 0;
	public boolean lightUpdated = false;
	
	public Player(GamePanel gp, KeyHandler keyH, MouseHandler mouseH) {
		
		super(gp);
		
		this.keyH = keyH;
		this.mouseH = mouseH;
		
		screenX = gp.screenWidth/2 - (gp.tileSize/2);
		screenY = gp.screenHeight/2 - (gp.tileSize/2);
		
		solidArea = new Rectangle();
		solidArea.x = 8;
		solidArea.y = 16;
		solidAreaDefaultX = solidArea.x;
		solidAreaDefaultY = solidArea.y;
		solidArea.width = 32;
		solidArea.height = 32;
		
//		attackArea.width = 36;
//		attackArea.height = 36;
		
		setDefaultValues();
		
	}
	public void setDefaultValues() {
		worldX = gp.tileSize * 23;
		worldY = gp.tileSize * 21;
		defaultSpeed = 4;
		speed = defaultSpeed;
		direction = "down";
		
		//Player status
		level = 1;
		maxLife = 6;
		life = maxLife;
		maxMana = 4;
		mana = maxMana;
		ammo = 10;
		strength = 1;
		dexterity = 1;
		exp = 0;
		nextLevelExp = 5;
		coin = 0;
		currentWeapon = new OBJ_Sword_Normal(gp);
		currentShield = new OBJ_Shield_Wood(gp);
		currentLight = null;
		projectile = new OBJ_Fireball(gp);
		attack = getAttack();
		defence = getDefence();
		
		getPlayerImage();
		getPlayerAttackImage();
		getGuardImage();
		setItems();
		setDialogue();
	}
	
	public void setDefaultPositions() {
		gp.currentMap = 0;
		worldX = gp.tileSize * 23;
		worldY = gp.tileSize * 21;
		direction = "down";
	}
	
	public void setDialogue() {
		dialogues[0][0] = "You are level " + level +" now!\n";
	}
	
	public void restoreStatus() {
		life = maxLife;
		mana = maxMana;
		invincible = false;
		transparent = false;
		attacking = false;
		guarding = false;
		knockBack = false;
		lightUpdated = true;
		speed = defaultSpeed;
	}
	
	public void setItems() {
		inventory.clear();
		inventory.add(currentWeapon);
		inventory.add(currentShield);
		inventory.add(new OBJ_Key(gp));
		
		
	} 
	
	public int getAttack() {
		attackArea = currentWeapon.attackArea;
		motion1_duration = currentWeapon.motion1_duration;
		motion2_duration = currentWeapon.motion2_duration;
		return attack = strength * currentWeapon.attackValue;
	}
	public int getDefence() {
		return defence = dexterity * currentShield.defenceValue;
	}
	
	public int getCurrentWeaponSlot() {
		int currentWeaponSlot = 0;
		for(int i = 0; i < inventory.size(); i++) {
			if(inventory.get(i) == currentWeapon) {
				currentWeaponSlot = i;
			}
		}
		return currentWeaponSlot;
	}
	
	public int getCurrentShieldSlot() {
		int currentShieldSlot = 0;
		for(int i = 0; i < inventory.size(); i++) {
			if(inventory.get(i) == currentShield) {
				currentShieldSlot = i;
			}
		}
		return currentShieldSlot;
	}
	
	public void getPlayerImage() {
			up1 = setup("/player/boy_up_1", gp.tileSize, gp.tileSize);
			up2 = setup("/player/boy_up_2", gp.tileSize, gp.tileSize);
			down1 = setup("/player/boy_down_1", gp.tileSize, gp.tileSize);
			down2 = setup("/player/boy_down_2", gp.tileSize, gp.tileSize);
			left1 = setup("/player/boy_left_1", gp.tileSize, gp.tileSize);
			left2 = setup("/player/boy_left_2", gp.tileSize, gp.tileSize);
			right1 = setup("/player/boy_right_1", gp.tileSize, gp.tileSize);
			right2 = setup("/player/boy_right_2", gp.tileSize, gp.tileSize);
	}
	
	public void getSleepingImage(BufferedImage image) {
		up1 = image;
		up2 = image;
		down1 = image;
		down2 = image;
		left1 = image;
		left2 = image;
		right1 = image;
		right2 = image;
	}
	
	public void getPlayerAttackImage() {
		
		if(currentWeapon.type == type_sword) {
			attackUp1 = setup("/player/boy_attack_up_1", gp.tileSize, gp.tileSize*2);
			attackUp2 = setup("/player/boy_attack_up_2", gp.tileSize, gp.tileSize*2);
			attackDown1 = setup("/player/boy_attack_down_1", gp.tileSize, gp.tileSize*2);
			attackDown2 = setup("/player/boy_attack_down_2", gp.tileSize, gp.tileSize*2);
			attackLeft1 = setup("/player/boy_attack_left_1", gp.tileSize*2, gp.tileSize);
			attackLeft2 = setup("/player/boy_attack_left_2", gp.tileSize*2, gp.tileSize);
			attackRight1 = setup("/player/boy_attack_right_1", gp.tileSize*2, gp.tileSize);
			attackRight2 = setup("/player/boy_attack_right_2", gp.tileSize*2, gp.tileSize);			
		}
		if(currentWeapon.type == type_axe) {
			attackUp1 = setup("/player/boy_axe_up_1", gp.tileSize, gp.tileSize*2);
			attackUp2 = setup("/player/boy_axe_up_2", gp.tileSize, gp.tileSize*2);
			attackDown1 = setup("/player/boy_axe_down_1", gp.tileSize, gp.tileSize*2);
			attackDown2 = setup("/player/boy_axe_down_2", gp.tileSize, gp.tileSize*2);
			attackLeft1 = setup("/player/boy_axe_left_1", gp.tileSize*2, gp.tileSize);
			attackLeft2 = setup("/player/boy_axe_left_2", gp.tileSize*2, gp.tileSize);
			attackRight1 = setup("/player/boy_axe_right_1", gp.tileSize*2, gp.tileSize);
			attackRight2 = setup("/player/boy_axe_right_2", gp.tileSize*2, gp.tileSize);			
		}
		if(currentWeapon.type == type_pickaxe) {
			attackUp1 = setup("/player/boy_pick_up_1", gp.tileSize, gp.tileSize*2);
			attackUp2 = setup("/player/boy_pick_up_2", gp.tileSize, gp.tileSize*2);
			attackDown1 = setup("/player/boy_pick_down_1", gp.tileSize, gp.tileSize*2);
			attackDown2 = setup("/player/boy_pick_down_2", gp.tileSize, gp.tileSize*2);
			attackLeft1 = setup("/player/boy_pick_left_1", gp.tileSize*2, gp.tileSize);
			attackLeft2 = setup("/player/boy_pick_left_2", gp.tileSize*2, gp.tileSize);
			attackRight1 = setup("/player/boy_pick_right_1", gp.tileSize*2, gp.tileSize);
			attackRight2 = setup("/player/boy_pick_right_2", gp.tileSize*2, gp.tileSize);			
		}
	}
	        
	public void getGuardImage() {
		guardUp = setup("/player/boy_guard_up", gp.tileSize, gp.tileSize);
		guardDown = setup("/player/boy_guard_down", gp.tileSize, gp.tileSize);
		guardLeft = setup("/player/boy_guard_left", gp.tileSize, gp.tileSize);
		guardRight = setup("/player/boy_guard_right", gp.tileSize, gp.tileSize);
	}
	
	public void update() {
		
		if(knockBack == true) {
		
			collisionOn = false;
			gp.cChecker.checkTile(this);
			gp.cChecker.checkObject(this, true);
			gp.cChecker.checkEntity(this, gp.npc);
			gp.cChecker.checkEntity(this, gp.monster);
			gp.cChecker.checkEntity(this, gp.iTile);
			
			if(collisionOn == true) {
				knockBackCounter = 0;
				knockBack = false;
				speed = defaultSpeed;
			}
			else if(collisionOn == false) {
				switch(knockBackDirection) {
				case "up":
					worldY -= speed; 
					break;
				case "down":
					worldY += speed;
					break;
				case "left":
					worldX -= speed;
					break;
				case "right":
					worldX += speed;
					break;
				}
			}
			knockBackCounter++;
			if(knockBackCounter == 10) {
				knockBackCounter = 0;
				knockBack = false;
				speed = defaultSpeed;
			}
		}
		
		else if(attacking == true) {
			attacking();
		}
		
		else if(mouseH.rightPressed == true) {
			guarding = true;
			guardCounter++;
		}
		
		else if(keyH.upPressed == true || keyH.downPressed == true ||keyH.leftPressed == true ||keyH.rightPressed == true || keyH.spacePressed == true || mouseH.leftPressed == true) {
			if(keyH.upPressed == true) {
				direction = "up";
			}else if(keyH.downPressed == true) {
				direction = "down";
			}else if(keyH.leftPressed == true) {
				direction = "left";
			}else if(keyH.rightPressed == true) {
				direction = "right";
			}
			
			// CHECK TILE COLLISION
			collisionOn = false;
			gp.cChecker.checkTile(this);
			
			//Check Object Collision
			int objIndex = gp.cChecker.checkObject(this, true);
			pickUpObject(objIndex);
			
			//Check NPC collision
			int npcIndex = gp.cChecker.checkEntity(this, gp.npc);
			interactNPC(npcIndex);
			
			//Check Monster Collision
			int monsterIndex = gp.cChecker.checkEntity(this, gp.monster);
			contactMonster(monsterIndex);
			
			//Check Interactive tile collision
			gp.cChecker.checkEntity(this, gp.iTile);
			
			//Check Event
			gp.eHandler.checkEvent();
			
			
			//If Collision is false, player can move
			if(collisionOn == false && keyH.spacePressed == false) {
				switch(direction) {
				case "up":
					worldY -= speed; 
					break;
				case "down":
					worldY += speed;
					break;
				case "left":
					worldX -= speed;
					break;
				case "right":
					worldX += speed;
					break;
				}
			}
			gp.keyH.spacePressed = false;
			guarding = false;
			guardCounter = 0;
			
			spriteCounter++;
			if(spriteCounter > 12) {
				if(spriteNum == 1) {
					spriteNum = 2;
				}else if(spriteNum == 2) {
					spriteNum = 1;
				}
				spriteCounter = 0;
			}
		}
		else {
			standCounter++;
			if(standCounter == 20) {
				spriteNum = 1;
				standCounter = 0;
			}
			guarding = false;
			guardCounter = 0;
		}
		
		if((gp.mouseH.shotKeyPressed == true || gp.keyH.shotKeyPressed == true) && projectile.alive == false && shotAvailableCounter == 30 && projectile.haveResource(this) == true) {
			projectile.set(worldX, worldY, direction, true, this);
			
			projectile.subtractResource(this);
			
			for(int i = 0; i < gp.projectile[1].length; i++) {
				if(gp.projectile[gp.currentMap][i] == null) {
					gp.projectile[gp.currentMap][i] = projectile;
					break;
				}
			}
			
			shotAvailableCounter = 0;
			
			gp.playSE(10);
			
		}
		
		if(invincible == true) {
			invincibleCounter++;
			if(invincibleCounter > 60) {
				invincible = false;
				transparent = false;
				invincibleCounter = 0;
			}
		}
		if(shotAvailableCounter < 30) {
			shotAvailableCounter++;
		}
		if(life > maxLife) {
			life = maxLife;
		}
		if(mana > maxMana) {
			mana = maxMana;
		}
		if(life <= 0) {
			gp.gameState = gp.gameOverState;
			gp.stopMusic();
			gp.playSE(12);
		}
	}
	
	public void pickUpObject(int i) {
		if(i != 999) {
			
			//Pickup only items
			if(gp.obj[gp.currentMap][i].type == type_pickupOnly) {
				gp.obj[gp.currentMap][i].use(this);
				gp.obj[gp.currentMap][i] = null;
			}
			else if(gp.obj[gp.currentMap][i].type == type_obstacle) {
				//Obstacle
				if(keyH.spacePressed == true) {
					gp.obj[gp.currentMap][i].interact();
				}
			}
			else {
				//Inventory items
				String text;
				if(canObtainItem(gp.obj[gp.currentMap][i]) == true) {
					gp.playSE(1);
					text = "Got a "+ gp.obj[gp.currentMap][i].name + "!";
				}
				else {
					text = "You cannot carry any more!";
				}
				gp.ui.addMessage(text);
				gp.obj[gp.currentMap][i] = null;
				
			}
		}
	}
	
	public void interactNPC(int i) {
		if(i != 999) {
			if(gp.keyH.spacePressed == true) {
				gp.npc[gp.currentMap][i].speak();
			}
			gp.npc[gp.currentMap][i].move(direction);
		}
		else {
			if(gp.mouseH.leftPressed == true) {
				gp.playSE(7);
				attacking =true;
			}
		}
	}
	
	public void contactMonster(int i) {
		if(i != 999) {
			if(invincible == false && gp.monster[gp.currentMap][i].dying == false) {
				gp.playSE(6);
				
				int damage = gp.monster[gp.currentMap][i].attack - defence;
				if(damage < 1) {
					damage = 1;
				}
				
				life -= damage;				
				invincible = true;
				transparent = true;
			}
			
		}
	}
	
	public void damageMonster(int i, Entity attacker, int attack, int knockBackPower) {
		if(i != 999) {
			if(gp.monster[gp.currentMap][i].invincible == false) {
				
				gp.playSE(5);
				
				if(knockBackPower > 0) {
					setKnockBack(gp.monster[gp.currentMap][i], attacker, knockBackPower);
				}
				
				if(gp.monster[gp.currentMap][i].offBalance == true) {
					attack *= 2;
				}
				
				int damage = attack - gp.monster[gp.currentMap][i].defence;
				if(damage < 0) {
					damage = 0;
				}
				
				gp.monster[gp.currentMap][i].life -= damage;
				gp.ui.addMessage(damage + "damage!");
				gp.monster[gp.currentMap][i].invincible = true;
				gp.monster[gp.currentMap][i].damageReaction();
				
				if(gp.monster[gp.currentMap][i].life <=0) {
					gp.monster[gp.currentMap][i].dying = true;
					gp.ui.addMessage("Killed the "+gp.monster[gp.currentMap][i].name + "!");
					gp.ui.addMessage("Exp + "+gp.monster[gp.currentMap][i].exp + "XP");
					exp += gp.monster[gp.currentMap][i].exp;
					checkLevelUp();
				}
			}
		}
	}
	
	
	
 	public void damageInteractiveTile(int i) {
		if(i != 999 && gp.iTile[gp.currentMap][i].destructible == true && gp.iTile[gp.currentMap][i].isCorrectItem(this) == true && gp.iTile[gp.currentMap][i].invincible == false) {
			gp.iTile[gp.currentMap][i].playSE();
			gp.iTile[gp.currentMap][i].life--;
			gp.iTile[gp.currentMap][i].invincible = true;
			
			generateParticle(gp.iTile[gp.currentMap][i], gp.iTile[gp.currentMap][i]);
			
			if(gp.iTile[gp.currentMap][i].life == 0) {
//				gp.iTile[gp.currentMap][i].checkDrop();
				gp.iTile[gp.currentMap][i] = gp.iTile[gp.currentMap][i].getDestroyedForm();				
			}
		}
	}
	
	public void damageProjectile(int i) {
		
		if(i != 999) {
			Entity projectile = gp.projectile[gp.currentMap][i];
			projectile.alive = false;
			generateParticle(projectile,projectile);
		}
	}
	
	public void checkLevelUp() {
		if(exp >= nextLevelExp) {
			level++;
			nextLevelExp = nextLevelExp*2;
			maxLife += 2;
			strength++;
			dexterity++;
			maxMana += 1;
			attack = getAttack();
			defence = getDefence();
			
			gp.playSE(8);
			gp.gameState = gp.dialogueState;
			setDialogue();
			startDialogue(this,0);
		}
	}
	
	public void selectItem() {
		int itemIndex = gp.ui.getItemIndexOnSlot(gp.ui.playerSlotCol, gp.ui.playerSlotRow);
	
		if(itemIndex < inventory.size()) {
			Entity selectedItem = inventory.get(itemIndex);
			if(selectedItem.type == type_sword || selectedItem.type == type_axe || selectedItem.type == type_pickaxe) {
				currentWeapon = selectedItem;
				attack = getAttack();
				getPlayerAttackImage();
			}
			if(selectedItem.type == type_shield) {
				currentShield = selectedItem;
				defence = getDefence();
			}
			if(selectedItem.type == type_light) {
				if(currentLight == selectedItem) {
					currentLight = null;
				}
				else {
					currentLight = selectedItem;
				}
				lightUpdated = true;
			}
			if(selectedItem.type == type_consumable) {
				if(selectedItem.use(this) == true) {
					if(selectedItem.amount > 1) {
						selectedItem.amount--;
					}
					else {
						inventory.remove(itemIndex);											
					}
				}
			}
		}
	}
	
	public int searchItemInInventory(String itemName) {
		
		int itemIndex = 999;
		
		for(int i = 0; i < inventory.size(); i++) {
			if(inventory.get(i).name.equals(itemName)) {
				itemIndex = i;
				break;
			}
		}
		return itemIndex;
	}
	
	public boolean canObtainItem(Entity item) {
		boolean canObtain = false;
		
		Entity newItem = gp.eGenerator.getObject(item.name);
		
		//check if item is stackable
		if(newItem.stackable == true) {
			int index = searchItemInInventory(newItem.name);
			
			if(index != 999) {
				inventory.get(index).amount++;
				canObtain = true;
			}
			else {
				if(inventory.size() != inventorySize) {
					inventory.add(newItem);
					canObtain = true;
				}
			}
		}
		else {
			if(inventory.size() != inventorySize) {
				inventory.add(newItem);
				canObtain = true;
			}
		}
		return canObtain;
	}
	
 	public void draw(Graphics2D g2) {
		
		BufferedImage image = null;
		int tempScreenX = screenX;
		int tempScreenY = screenY;
		
		switch(direction) {
		case "up":
			if(attacking == false) {
				if(spriteNum == 1) { image = up1;}
				if(spriteNum == 2) { image = up2;}				
			}
			if(attacking == true) {
				tempScreenY = screenY - gp.tileSize;
				if(spriteNum == 1) { image = attackUp1;}
				if(spriteNum == 2) { image = attackUp2;}
			}
			if(guarding == true) {
				image = guardUp;
			}
			break;
		case "down":
			if(attacking == false) {
				if(spriteNum == 1) { image = down1;}
				if(spriteNum == 2) { image = down2;}
			}
			if(attacking == true) {
				if(spriteNum == 1) { image = attackDown1;}
				if(spriteNum == 2) { image = attackDown2;}
			}
			if(guarding == true) {
				image = guardDown;
			}
			break;
		case "left":
			if(attacking == false) {
				if(spriteNum == 1) { image = left1;}
				if(spriteNum == 2) { image = left2;}
			}
			if(attacking == true) {
				tempScreenX = screenX - gp.tileSize;
				if(spriteNum == 1) { image = attackLeft1;}
				if(spriteNum == 2) { image = attackLeft2;}
			}
			if(guarding == true) {
				image = guardLeft;
			}
			break;
		case "right":
			if(attacking == false) {
				if(spriteNum == 1) { image = right1;}
				if(spriteNum == 2) { image = right2;}
			}
			if(attacking == true) {
				if(spriteNum == 1) { image = attackRight1;}
				if(spriteNum == 2) { image = attackRight2;}
			}
			if(guarding == true) {
				image = guardRight;
			}
			break;
		}
		
		if(transparent == true) {
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
		}
		if(drawing == true) {
			g2.drawImage(image, tempScreenX, tempScreenY, null);
		}
		
		//Reset alpha
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
		
	}
}
