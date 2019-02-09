package core;

import java.awt.Component;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import processing.core.PApplet;
import processing.core.PFont;

public class EvolvioMod extends PApplet {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5156677256654057134L;

	public static EvolvioMod main;
	
	Board evoBoard;
	final int SEED = 51;
	final float NOISE_STEP_SIZE = 0.1f;
	final int BOARD_WIDTH = 100;
	final int BOARD_HEIGHT = 100;

	final int WINDOW_WIDTH = (int)(1920);  // sc
	final int WINDOW_HEIGHT = (int)(1080); // sc - scale change
	int INITIAL_WINDOW_HEIGHT = -1;
	final float ASPECT_RATIO = (float)WINDOW_WIDTH / (float)WINDOW_HEIGHT;
	//final float WINDOW_SCALE() = WINDOW_WIDTH/(width > 0 ? width : WINDOW_WIDTH);//0.5;


	final float SCALE_TO_FIX_BUG = 100 * WINDOW_SCALE(); // sc (this one made button clicking work)
	final float GROSS_OVERALL_SCALE_FACTOR = ((float)WINDOW_HEIGHT)/BOARD_HEIGHT/SCALE_TO_FIX_BUG;

	final double TIME_STEP = 0.001;
	final float MIN_TEMPERATURE = -0.5f;
	final float MAX_TEMPERATURE = 1.0f;

	final int ROCKS_TO_ADD = 0;
	final int CREATURE_MINIMUM = 60;

	float cameraX = BOARD_WIDTH*0.5f;
	float cameraY = BOARD_HEIGHT*0.5f;
	float cameraR = 0;
	float zoom = 1;
	public PFont font;
	int dragging = 0; // 0 = no drag, 1 = drag screen, 2 and 3 are dragging temp extremes.
	float prevMouseX;
	float prevMouseY;
	boolean draggedFar = false;
	final String INITIAL_FILE_NAME = "PIC";

	Component contentPane;
	
	float lastDrawTime = 0;
	private boolean finishedSetup = false;
	
	public static void main(String args[]) {

		ModLoader.init();

		PApplet.main("core.EvolvioMod");
	}
//	public static void finishStartup() {
//
//		PApplet.main("core.EvolvioMod");
//	}

	public float WINDOW_SCALE() {
		try{
		    return (float)getDrawspaceWidth() / (float)(WINDOW_WIDTH); // this magic number is the value of (float)frame.getWidth() / (float)WINDOW_WIDTH before any size changes are done. I'm not sure why this is happening
		} catch (Exception e) { return 1; }
	}

//	@Override
//	public void settings(){
//		float initScale = 0.6f;
//		size((int)(WINDOW_WIDTH*initScale), (int)(WINDOW_HEIGHT*initScale)); 
//    }

	@Override
	public void setup() {
		EvolvioMod.main = this;
		
		colorMode(HSB, 1.0f);
		font = super.loadFont("Jygquip1-48.vlw");
		
		float initScale = 0.6f;
		size((int)(WINDOW_WIDTH*initScale), (int)(WINDOW_HEIGHT*initScale)); 
		frame.setSize((int)(WINDOW_WIDTH*initScale), (int)(WINDOW_HEIGHT*initScale)); // sc 
		
		
		// allow for resizing 
		// also handle aspect ratio fixing
		frame.setResizable(true);
		
		contentPane = frame.getComponents()[0];
		frame.addComponentListener(new ComponentAdapter() {
		    int lastWidth = WINDOW_WIDTH;
		    int lastHeight = WINDOW_HEIGHT;
		    public void componentResized(ComponentEvent componentEvent) {
		        // make sure aspect ratio is correct
		        if(frame.getWidth() <= ASPECT_RATIO*frame.getHeight()) {
		        	contentPane.setSize(contentPane.getWidth(), (int)(contentPane.getWidth() / ASPECT_RATIO));
		        } else if (frame.getHeight() != lastHeight) {
		        	contentPane.setSize((int)(contentPane.getHeight() * ASPECT_RATIO), contentPane.getHeight());
		        }
		        
		        lastWidth = frame.getWidth();
		        lastHeight = frame.getHeight();
		    }
		});
		
		//
//		ModLoader.init();
//		contentPane.addMouseWheelListener(new MouseWheelListener(){
//			public void mouseWheelMoved(MouseWheelEvent event) {
//				System.out.println("scrolling is happening!");
//				mouseWheel(event);
//			}
//		});	
		
//		System.out.println("frame components");
//		for(Component c : frame.getComponents()) {
//			System.out.println(c);
//		}
		
		
	}
	
	void finishSetup() {
		evoBoard = new Board(BOARD_WIDTH, BOARD_HEIGHT, NOISE_STEP_SIZE, MIN_TEMPERATURE, MAX_TEMPERATURE, 
				ROCKS_TO_ADD, CREATURE_MINIMUM, SEED, INITIAL_FILE_NAME, TIME_STEP);
		resetZoom();
		
		finishedSetup = true;
	}
	
	public int getDrawspaceHeight() {
		return contentPane.getHeight();
	}
	public int getDrawspaceWidth() {
		return contentPane.getWidth();
	}

	@Override
	public void draw() {
		if(!finishedSetup) { return; }
		
		// Carykh's code
		for (int iteration = 0; iteration < evoBoard.playSpeed; iteration++) {
		    evoBoard.iterate(TIME_STEP);
		}
		if (dist(prevMouseX, prevMouseY, mouseX, mouseY) > 5) {
		    draggedFar = true;
		}
		
		//test
		
		//fill(200,0,0);
		//rect(0,0,200,200);
		//if(true)return;
		
		// end test
		
		// limit the framerate if the speed is too high. This gives the simulation priority and the ability to run
		if(evoBoard.playSpeed >= Board.MAX_REALTIME_PLAYSPEED && (evoBoard.year-lastDrawTime) < Board.NON_REALTIME_DRAWRATE) {
		     return;   
		}
		lastDrawTime = (float)evoBoard.year;
		
		if (dragging == 1) {
		    cameraX -= toWorldXCoordinate(mouseX, mouseY)-toWorldXCoordinate(prevMouseX, prevMouseY);
		    cameraY -= toWorldYCoordinate(mouseX, mouseY)-toWorldYCoordinate(prevMouseX, prevMouseY);
		} else if (dragging == 2) { //UGLY UGLY CODE.    Do not look at this    // hate to break it to ya buddy, but I'm looking at it
		    int adjustedMouseY = (int)(mouseY / WINDOW_SCALE());
		    
		    if (evoBoard.setMinTemperature(1.0f-(adjustedMouseY-30f)/660.0f)) {
		        dragging = 3;
		    }
		} else if (dragging == 3) {
		    int adjustedMouseY = (int)(mouseY / WINDOW_SCALE());
		    
		    if (evoBoard.setMaxTemperature(1.0f-(adjustedMouseY-30f)/660.0f)) {
		        dragging = 2;
		    }
		}
		if (evoBoard.userControl && evoBoard.selectedCreature != null) {
		    cameraX = (float)evoBoard.selectedCreature.px;
		    cameraY = (float)evoBoard.selectedCreature.py;
		    cameraR = -PI/2.0f-(float)evoBoard.selectedCreature.rotation;
		}else{
		    cameraR = 0;
		}
		pushMatrix();
		scale(GROSS_OVERALL_SCALE_FACTOR*WINDOW_SCALE()); // sc
		evoBoard.drawBlankBoard(SCALE_TO_FIX_BUG);
		translate(BOARD_WIDTH*0.5f*SCALE_TO_FIX_BUG, BOARD_HEIGHT*0.5f*SCALE_TO_FIX_BUG);
		scale(zoom); 
		if (evoBoard.userControl && evoBoard.selectedCreature != null) {
		    rotate(cameraR);
		}
		translate(-cameraX*SCALE_TO_FIX_BUG, -cameraY*SCALE_TO_FIX_BUG);
		evoBoard.drawBoard(SCALE_TO_FIX_BUG, zoom, (int)toWorldXCoordinate(mouseX/WINDOW_SCALE(), mouseY/WINDOW_SCALE()), (int)toWorldYCoordinate(mouseX/WINDOW_SCALE(), mouseY/WINDOW_SCALE())); // sc
		popMatrix();
		scale(WINDOW_SCALE()); // sc
		evoBoard.drawUI(SCALE_TO_FIX_BUG, TIME_STEP, (int)(getDrawspaceHeight()/WINDOW_SCALE()), 0, (int)(getDrawspaceWidth()/WINDOW_SCALE()), (int)(height/WINDOW_SCALE()), font); //sc

		//this function is causing problems
		//evoBoard.fileSave();
		prevMouseX = mouseX;
		prevMouseY = mouseY;
	}

	/**
	 * Zooms the overworld map
	 */
	@Override
	public void mouseWheelMoved(MouseWheelEvent event) {
		float delta = (float) event.getPreciseWheelRotation();
		if (delta >= 0.5) {
		    setZoom(zoom*0.90909f, mouseX, mouseY);
		} else if (delta <= -0.5) {
		    setZoom(zoom*1.1f, mouseX, mouseY);
		}
	}

	/**
	 * Does different things depending on where the mouse currently is
	 */
	@Override
	public void mousePressed() {
		int adjustedMouseX = (int)(mouseX/WINDOW_SCALE()); // sc
		int adjustedMouseY = (int)(mouseY/WINDOW_SCALE()); // sc
		
		
		
		//int fheight = frame.getHeight();
		//int fwidth = frame.getWidth();
		
		// the board is drawn at size (WINDOW_HEIGHT x WINDOW_HEIGHT)
		if (adjustedMouseX < WINDOW_HEIGHT) {
		    // so if mouseX is less than WINDOW_HEIGHT, the mouse is on the board (aka overworld map)
		    dragging = 1;
		} else {
		    // otherwise it's on the menu ui
		    if (abs(adjustedMouseX-(WINDOW_HEIGHT+65)) <= 60 && abs(adjustedMouseY-147) <= 60 && evoBoard.selectedCreature != null) {
		            // TODO: figure out what button this is
		            cameraX = (float)evoBoard.selectedCreature.px;
		            cameraY = (float)evoBoard.selectedCreature.py;
		            zoom = 16;
		    } else if (adjustedMouseY >= 95 && adjustedMouseY < 135 && evoBoard.selectedCreature == null) { // TODO: figure out what this condition means
		        if (adjustedMouseX >= WINDOW_HEIGHT+10 && adjustedMouseX < WINDOW_HEIGHT+230) {
		            // reset zoom button
		            resetZoom();
		        } else if (adjustedMouseX >= WINDOW_HEIGHT+240 && adjustedMouseX < WINDOW_HEIGHT+460) {
		            // "Sort By" button
		            evoBoard.creatureRankMetric = (evoBoard.creatureRankMetric+1)%8;
		        }
		    } else if (adjustedMouseY >= 570) {
		        float x = (adjustedMouseX-(WINDOW_HEIGHT-30));
		        float y = (adjustedMouseY-570);
		        boolean clickedOnLeft = (x%230 < 110);
		        if (x >= 0 && x < 2*230 && y >= 0 && y < 4*50 && x%230 < 220 && y%50 < 40) {
		            int mX = (int)(x/230);
		            int mY = (int)(y/50);
		            int buttonNum = mX+mY*2;
		            if (buttonNum == 0) {
		                evoBoard.userControl = !evoBoard.userControl;
		            } else if (buttonNum == 1) {
		                if (clickedOnLeft) {
		                    evoBoard.creatureMinimum -= evoBoard.creatureMinimumIncrement;
		                } else {
		                    evoBoard.creatureMinimum += evoBoard.creatureMinimumIncrement;
		                }
		            } else if (buttonNum == 2) {
		                evoBoard.prepareForFileSave(0);
		            } else if (buttonNum == 3) {
		                if (clickedOnLeft) {
		                    evoBoard.imageSaveInterval *= 0.5;
		                } else {
		                    evoBoard.imageSaveInterval *= 2.0;
		                }
		                if (evoBoard.imageSaveInterval >= 0.7) {
		                    evoBoard.imageSaveInterval = Math.round(evoBoard.imageSaveInterval);
		                }
		            } else if (buttonNum == 4) {
		                evoBoard.prepareForFileSave(2);
		            } else if (buttonNum == 5) {
		                if (clickedOnLeft) {
		                    evoBoard.textSaveInterval *= 0.5;
		                } else {
		                    evoBoard.textSaveInterval *= 2.0;
		                }
		                if (evoBoard.textSaveInterval >= 0.7) {
		                    evoBoard.textSaveInterval = Math.round(evoBoard.textSaveInterval);
		                }
		            }else if(buttonNum == 6){
		                if (clickedOnLeft) {
		                    if(evoBoard.playSpeed >= 2){
		                        evoBoard.playSpeed /= 2;
		                    }else{
		                        evoBoard.playSpeed = 0;
		                    }
		                } else {
		                    if(evoBoard.playSpeed == 0){
		                        evoBoard.playSpeed = 1;
		                    } else if (evoBoard.playSpeed < Board.MAX_PLAYSPEED){
		                        evoBoard.playSpeed *= 2;
		                    }
		                }
		            }
		        }
		    } else if (adjustedMouseX >= WINDOW_HEIGHT+10 && adjustedMouseX < WINDOW_WIDTH-50 && evoBoard.selectedCreature == null) {
		        int listIndex = (adjustedMouseY-150)/70;
		        if (listIndex >= 0 && listIndex < evoBoard.LIST_SLOTS) {
		            evoBoard.selectedCreature = evoBoard.list[listIndex];
		            cameraX = (float)evoBoard.selectedCreature.px;
		            cameraY = (float)evoBoard.selectedCreature.py;
		            zoom = 16;
		        }
		    }
		    if (adjustedMouseX >= WINDOW_WIDTH-50) {
		        // The mouse clicked on the temperature bar
		        
		        float toClickTemp = (adjustedMouseY-30f)/660.0f;
		        float lowTemp = 1.0f-evoBoard.getLowTempProportion();
		        float highTemp = 1.0f-evoBoard.getHighTempProportion();
		        if (abs(toClickTemp-lowTemp) < abs(toClickTemp-highTemp)) {
		            dragging = 2;
		        } else {
		            dragging = 3;
		        }
		    }
		}
		draggedFar = false;
	}

	@Override
	public void mouseReleased() {
		if (!draggedFar) {
		    if (mouseX/WINDOW_SCALE() < WINDOW_HEIGHT) { // DO NOT LOOK AT THIS CODE EITHER it is bad // It's cool, I'll fix it
		        dragging = 1;
		        float mX = toWorldXCoordinate(mouseX, mouseY);
		        float mY = toWorldYCoordinate(mouseX, mouseY);
		        int x = (int)(floor(mX));
		        int y = (int)(floor(mY));
		        evoBoard.unselect();
		        cameraR = 0;
		        if (x >= 0 && x < BOARD_WIDTH && y >= 0 && y < BOARD_HEIGHT) {
		            for (int i = 0; i < evoBoard.softBodiesInPositions[x][y].size (); i++) {
		                SoftBody body = (SoftBody)evoBoard.softBodiesInPositions[x][y].get(i);
		                if (body.isCreature) {
		                    float distance = dist(mX, mY, (float)body.px, (float)body.py);
		                    if (distance <= body.getRadius()) {
		                        evoBoard.selectedCreature = (Creature)body;
		                        zoom = 16;
		                    }
		                }
		            }
		        }
		    }
		}
		dragging = 0;
	}

	void resetZoom() {
		cameraX = BOARD_WIDTH*0.5f;
		cameraY = BOARD_HEIGHT*0.5f;
		zoom = 1;
	}

	void setZoom(float target, float x, float y) {
		float grossX = grossify(x, BOARD_WIDTH);
		cameraX -= (grossX/target-grossX/zoom);
		float grossY = grossify(y, BOARD_HEIGHT);
		cameraY -= (grossY/target-grossY/zoom);
		zoom = target;
	}

	float grossify(float input, float total) { // Very weird function
		return (input/GROSS_OVERALL_SCALE_FACTOR-total*0.5f*SCALE_TO_FIX_BUG)/SCALE_TO_FIX_BUG;
	}

	float toWorldXCoordinate(float x, float y) {
		float w = WINDOW_HEIGHT/2;
		float angle = atan2(y-w, x-w);
		float dist = dist(w, w, x, y);
		return cameraX+grossify(cos(angle-cameraR)*dist+w, BOARD_WIDTH)/zoom;
	}

	float toWorldYCoordinate(float x, float y) {
		float w = WINDOW_HEIGHT/2;
		float angle = atan2(y-w, x-w);
		float dist = dist(w, w, x, y);
		return cameraY+grossify(sin(angle-cameraR)*dist+w, BOARD_HEIGHT)/zoom;
	}
}

