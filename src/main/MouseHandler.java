package main;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class MouseHandler implements MouseListener{

	GamePanel gp;
	
	public boolean leftPressed, rightPressed, shotKeyPressed;
	
	public MouseHandler(GamePanel gp) {
		this.gp = gp;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		int code = e.getButton();
		if(gp.gameState == gp.playState) {	
			if(code == MouseEvent.BUTTON1) {
				leftPressed = true;
			}
			if(code == MouseEvent.BUTTON2) {
				shotKeyPressed = true;
			}
			if(code == MouseEvent.BUTTON3) {
				rightPressed = true;
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		int code = e.getButton();
		if(code == MouseEvent.BUTTON1) {
			leftPressed = false;
		}
		if(code == MouseEvent.BUTTON2) {
			shotKeyPressed = false;
		}
		if(code == MouseEvent.BUTTON3) {
			rightPressed = false;
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

}
