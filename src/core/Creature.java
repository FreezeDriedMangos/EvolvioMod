package core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import core.modAPI.Brain;
import core.modAPI.CreatureAction;
import core.modAPI.CreatureAttribute;
import core.modAPI.CreatureFeatureDrawer;
import core.modAPI.CreaturePeripheral;
import processing.core.PFont;

public class Creature extends SoftBody {
	public static final double REPRODUCE_WILLINGNESS_THRESHOLD = -1;
	
	final static double ACCELERATION_ENERGY = 2*0.18;
	final static double ACCELERATION_BACK_ENERGY = 2*0.24;
	final static double SWIM_ENERGY = 0.008;
	final static double TURN_ENERGY = 0.05;
//  double EAT_ENERGY = 0.05;
	// double EAT_SPEED = 0.5; // 1 is instant, 0 is nonexistent, 0.001 is verrry
	// slow.
	// double EAT_WHILE_MOVING_INEFFICIENCY_MULTIPLIER = 2.0; // The bigger this
	// number is, the less effiently creatures eat when they're moving.
	final static double FIGHT_ENERGY = 0.06;
	final static double INJURED_ENERGY = 0.25;
	final static double METABOLISM_ENERGY = 0.004;

	public final int ENERGY_HISTORY_LENGTH = 6;
	public final double SAFE_SIZE = 1.25;
	public final double MATURE_AGE = 0.01;

	public final int MIN_NAME_LENGTH = 3;
	public final int MAX_NAME_LENGTH = 10;
	
	String name;
	String parents;
	int gen;
	int id;
//	public double MAX_VISION_DISTANCE = 10;
	public double currentEnergy;
	
//	public final double STARTING_AXON_VARIABILITY = 1.0;
	
	public double[] previousEnergy = new double[ENERGY_HISTORY_LENGTH];
//  final double FOOD_SENSITIVITY = 0.3;

	public double vr = 0;
	public double rotation = 0;
//	public final int BRAIN_WIDTH = 3;
//	public final int BRAIN_HEIGHT = 13;
//	public final double AXON_START_MUTABILITY = 0.0005;
//	public final float BRIGHTNESS_THRESHOLD = 0.7f;
	//public Axon[][][] axons;
	//public double[][] neurons;

	float preferredRank = 8;
//	double[] visionAngles = { 0, -0.4, 0.4 };
//	double[] visionDistances = { 0, 0.7, 0.7 };
	// double visionAngle;
	// double visionDistance;
//	double[] visionOccludedX = new double[visionAngles.length];
//	double[] visionOccludedY = new double[visionAngles.length];
//	double visionResults[] = new double[9];
	//int MEMORY_COUNT = 1;
	//double[] memories;

//	float CROSS_SIZE = 0.022f;

	public double secondaryHue;
	CreatureThread thread;

	HashMap<String, CreatureAttribute> attributes = new HashMap<>();
	List<CreatureAction> actions = new ArrayList<>();
	List<CreaturePeripheral> peripherals = new ArrayList<>();
	List<CreatureFeatureDrawer> featureDrawers = new ArrayList<>();
	Brain brain;
	
	public Creature(double tpx, double tpy, double tvx, double tvy, double tenergy, double tdensity, double thue,
			double tsaturation, double tbrightness, Board tb, double bt, double rot, double tvr, String tname,
			String tparents, boolean mutateName, /*Axon[][][] tbrain, double[][] tneurons,*/ Brain tbrain, List<CreaturePeripheral> per, int tgen, double tmouthHue) {

		super(tpx, tpy, tvx, tvy, tenergy, tdensity, thue, tsaturation, tbrightness, tb, bt);
		
		if(tbrain == null) {
			peripherals = ModLoader.createPeripherals(this, board);
			brain = ModLoader.createBrain(this, tb);
			ModLoader.initializeAttributes(this, tb);
		} else {
			brain = tbrain;
			peripherals = per;
		}
		
//		boolean firstPrint = true;
//		System.out.println("\n" + (tbrain == null));
//		for(StackTraceElement e : Thread.currentThread().getStackTrace()) {
//			if(firstPrint) {firstPrint = false; System.out.println(e);}
//			else { System.out.println("\t" +e); }
//		}
		
//		if (tbrain == null) {
//			axons = new Axon[BRAIN_WIDTH - 1][BRAIN_HEIGHT][BRAIN_HEIGHT - 1];
//			neurons = new double[BRAIN_WIDTH][BRAIN_HEIGHT];
//			for (int x = 0; x < BRAIN_WIDTH - 1; x++) {
//				for (int y = 0; y < BRAIN_HEIGHT; y++) {
//					for (int z = 0; z < BRAIN_HEIGHT - 1; z++) {
//						double startingWeight = 0;
//						if (y == BRAIN_HEIGHT - 1) {
//							startingWeight = (Math.random() * 2 - 1) * STARTING_AXON_VARIABILITY;
//						}
//						axons[x][y][z] = new Axon(startingWeight, AXON_START_MUTABILITY);
//					}
//				}
//			}
//			neurons = new double[BRAIN_WIDTH][BRAIN_HEIGHT];
//			for (int x = 0; x < BRAIN_WIDTH; x++) {
//				for (int y = 0; y < BRAIN_HEIGHT; y++) {
//					if (y == BRAIN_HEIGHT - 1) {
//						neurons[x][y] = 1;
//					} else {
//						neurons[x][y] = 0;
//					}
//				}
//			}
//		} else {
//			axons = tbrain;
//			neurons = tneurons;
//		}
		rotation = rot;
		vr = tvr;
		isCreature = true;
		
		if (tname.length() >= 1) {
			if (mutateName) {
				name = mutateName(tname);
			} else {
				name = tname;
			}
			name = sanitizeName(name);
		} else {
			name = createNewName();
		}
		parents = tparents;
		board.softBodyIDUpTo++;
		// visionAngle = 0;
		// visionDistance = 0;
		// visionEndX = getVisionStartX();
		// visionEndY = getVisionStartY();
//		for (int i = 0; i < 9; i++) {
//			visionResults[i] = 0;
//		}
//		memories = new double[MEMORY_COUNT];
//		for (int i = 0; i < MEMORY_COUNT; i++) {
//			memories[i] = 0;
//		}
		gen = tgen;
		secondaryHue = tmouthHue;
	}

	public CreatureAttribute getAttribute(String name) {
		return attributes.get(name);
	}

	public void drawBrain(PFont font, float scaleUp, int mX, int mY) {
//		ModLoader.brainDrawer.drawBrain(brain, font, scaleUp, mX, mY);
		brain.draw(font, scaleUp, mX, mY);
		
//		final float neuronSize = 0.4f;
//		EvolvioMod.main.noStroke();
//		EvolvioMod.main.fill(0, 0, 0.4f);
//		EvolvioMod.main.rect((-1.7f - neuronSize) * scaleUp, -neuronSize * scaleUp,
//				(2.4f + BRAIN_WIDTH + neuronSize * 2) * scaleUp, (BRAIN_HEIGHT + neuronSize * 2) * scaleUp);
//
//		EvolvioMod.main.ellipseMode(EvolvioMod.main.RADIUS);
//		EvolvioMod.main.strokeWeight(2);
//		EvolvioMod.main.textFont(font, 0.58f * scaleUp);
//		EvolvioMod.main.fill(0, 0, 1);
//		String[] inputLabels = { "0Hue", "0Sat", "0Bri", "1Hue", "1Sat", "1Bri", "2Hue", "2Sat", "2Bri", "Size", "MHue",
//				"Mem", "Const." };
//		String[] outputLabels = { "BHue", "Accel.", "Turn", "Eat", "Fight", "Birth", "How funny?", "How popular?",
//				"How generous?", "How smart?", "MHue", "Mem", "Const." };
//		for (int y = 0; y < BRAIN_HEIGHT; y++) {
//			EvolvioMod.main.textAlign(EvolvioMod.main.RIGHT);
//			EvolvioMod.main.text(inputLabels[y], (-neuronSize - 0.1f) * scaleUp, (y + (neuronSize * 0.6f)) * scaleUp);
//			EvolvioMod.main.textAlign(EvolvioMod.main.LEFT);
//			EvolvioMod.main.text(outputLabels[y], (BRAIN_WIDTH - 1 + neuronSize + 0.1f) * scaleUp,
//					(y + (neuronSize * 0.6f)) * scaleUp);
//		}
//		EvolvioMod.main.textAlign(EvolvioMod.main.CENTER);
//		for (int x = 0; x < BRAIN_WIDTH; x++) {
//			for (int y = 0; y < BRAIN_HEIGHT; y++) {
//				EvolvioMod.main.noStroke();
//				double val = neurons[x][y];
//				EvolvioMod.main.fill(neuronFillColor(val));
//				EvolvioMod.main.ellipse(x * scaleUp, y * scaleUp, neuronSize * scaleUp, neuronSize * scaleUp);
//				EvolvioMod.main.fill(neuronTextColor(val));
//				EvolvioMod.main.text(EvolvioMod.main.nf((float) val, 0, 1), x * scaleUp,
//						(y + (neuronSize * 0.6f)) * scaleUp);
//			}
//		}
//		if (mX >= 0 && mX < BRAIN_WIDTH && mY >= 0 && mY < BRAIN_HEIGHT) {
//			for (int y = 0; y < BRAIN_HEIGHT; y++) {
//				if (mX >= 1 && mY < BRAIN_HEIGHT - 1) {
//					drawAxon(mX - 1, y, mX, mY, scaleUp);
//				}
//				if (mX < BRAIN_WIDTH - 1 && y < BRAIN_HEIGHT - 1) {
//					drawAxon(mX, mY, mX + 1, y, scaleUp);
//				}
//			}
//		}
	}

//	public void drawAxon(int x1, int y1, int x2, int y2, float scaleUp) {
//		EvolvioMod.main.stroke(neuronFillColor(axons[x1][y1][y2].weight * neurons[x1][y1]));
//
//		EvolvioMod.main.line(x1 * scaleUp, y1 * scaleUp, x2 * scaleUp, y2 * scaleUp);
//	}

	public void useBrain(double timeStep, boolean useOutput) {
		HashMap<String, Double> peripheralInputs = new HashMap<>();
		for(CreaturePeripheral p : peripherals) {
			peripheralInputs.putAll(p.getInputValues(this, board, timeStep));
		}
		
		brain.think(this, peripheralInputs, board, timeStep);
		
		if (useOutput) {
			hue = Math.min(Math.max(brain.getOutput("hue"), 0), 1);
			accelerate(brain.getOutput("accelerate"), timeStep);
			turn(brain.getOutput("turn"), timeStep);
			eat(brain.getOutput("eat"), timeStep);
			fight(brain.getOutput("fight"), timeStep);
			if (brain.getOutput("reproduce") > 0 && board.year - birthTime >= MATURE_AGE && energy > SAFE_SIZE) {
				reproduce(SAFE_SIZE, timeStep);
			}
			//secondaryHue = Math.min(Math.max(brain.getOutput("secondaryHue"), 0), 1);
			
			//brain.useOutput(this, board, timeStep);
			
			for(CreatureAction a : actions) {
				a.doAction(brain, this, board, timeStep);
			}
		}
		
//		for (int i = 0; i < 9; i++) {
//			neurons[0][i] = visionResults[i];
//		}
//		neurons[0][9] = energy;
//		neurons[0][10] = secondaryHue;
//		for (int i = 0; i < MEMORY_COUNT; i++) {
//			neurons[0][11 + i] = memories[i];
//		}
//		for (int x = 1; x < BRAIN_WIDTH; x++) {
//			for (int y = 0; y < BRAIN_HEIGHT - 1; y++) {
//				double total = 0;
//				for (int input = 0; input < BRAIN_HEIGHT; input++) {
//					total += neurons[x - 1][input] * axons[x - 1][input][y].weight;
//				}
//				if (x == BRAIN_WIDTH - 1) {
//					neurons[x][y] = total;
//				} else {
//					neurons[x][y] = sigmoid(total);
//				}
//			}
//		}
//		if (useOutput) {
//			int end = BRAIN_WIDTH - 1;
//			hue = Math.min(Math.max(neurons[end][0], 0), 1);
//			accelerate(neurons[end][1], timeStep);
//			turn(neurons[end][2], timeStep);
//			eat(neurons[end][3], timeStep);
//			fight(neurons[end][4], timeStep);
//			if (neurons[end][5] > 0 && board.year - birthTime >= MATURE_AGE && energy > SAFE_SIZE) {
//				reproduce(SAFE_SIZE, timeStep);
//			}
//			secondaryHue = Math.min(Math.max(neurons[end][10], 0), 1);
//			for (int i = 0; i < MEMORY_COUNT; i++) {
//				memories[i] = neurons[end][11 + i];
//			}
//		}
	}

//	public double sigmoid(double input) {
//		return 1.0 / (1.0 + Math.pow(2.71828182846, -input));
//	}

//	public int neuronFillColor(double d) {
//		if (d >= 0) {
//			return EvolvioMod.main.color(0, 0, 1, (float) (d));
//		} else {
//			return EvolvioMod.main.color(0, 0, 0, (float) (-d));
//		}
//	}
//
//	public int neuronTextColor(double d) {
//		if (d >= 0) {
//			return EvolvioMod.main.color(0, 0, 0);
//		} else {
//			return EvolvioMod.main.color(0, 0, 1);
//		}
//	}

	public void drawSoftBody(float scaleUp, float camZoom, boolean overworldDraw) {
//		for(CreaturePeripheral peripheral : peripherals) {
//			peripheral.preCreatureDraw(this, board, scaleUp, camZoom, overworldDraw);
//		}
//		for(CreatureAction action : actions) {
//			action.preCreatureDraw(this, board, scaleUp, camZoom, overworldDraw);
//		}
		for(CreatureFeatureDrawer drawer : featureDrawers) {
			drawer.preCreatureDraw(this, board, scaleUp, overworldDraw);
		}
		
		EvolvioMod.main.ellipseMode(EvolvioMod.main.RADIUS);
		double radius = getRadius();
		
		EvolvioMod.main.noStroke();
		if (fightLevel > 0) {
			EvolvioMod.main.fill(0, 1, 1, (float) (fightLevel * 0.8));
			EvolvioMod.main.ellipse((float) (px * scaleUp), (float) (py * scaleUp),
					(float) (FIGHT_RANGE * radius * scaleUp), (float) (FIGHT_RANGE * radius * scaleUp));
		}
		EvolvioMod.main.strokeWeight(board.CREATURE_STROKE_WEIGHT);
		EvolvioMod.main.stroke(0, 0, 1);
		EvolvioMod.main.fill(0, 0, 1);
		if (this == board.selectedCreature) {
			EvolvioMod.main.ellipse((float) (px * scaleUp), (float) (py * scaleUp),
					(float) (radius * scaleUp + 1 + 75.0 / camZoom), (float) (radius * scaleUp + 1 + 75.0 / camZoom));
		}
		super.drawSoftBody(scaleUp);
		EvolvioMod.main.noFill();
		EvolvioMod.main.strokeWeight(board.CREATURE_STROKE_WEIGHT);
		EvolvioMod.main.stroke(0, 0, 1);
		EvolvioMod.main.ellipseMode(EvolvioMod.main.RADIUS);
		EvolvioMod.main.ellipse((float) (px * scaleUp), (float) (py * scaleUp),
				(float) (board.MINIMUM_SURVIVABLE_SIZE * scaleUp), (float) (board.MINIMUM_SURVIVABLE_SIZE * scaleUp));
		EvolvioMod.main.pushMatrix();
		EvolvioMod.main.translate((float) (px * scaleUp), (float) (py * scaleUp));
		EvolvioMod.main.scale((float) radius);
		EvolvioMod.main.rotate((float) rotation);
		EvolvioMod.main.strokeWeight((float) (board.CREATURE_STROKE_WEIGHT / radius));
		EvolvioMod.main.stroke(0, 0, 0);
		EvolvioMod.main.fill((float) secondaryHue, 1.0f, 1.0f);
		EvolvioMod.main.ellipse(0.6f * scaleUp, 0, 0.37f * scaleUp, 0.37f * scaleUp);
		/*
		 * rect(-0.7*scaleUp,-0.2*scaleUp,1.1*scaleUp,0.4*scaleUp); beginShape();
		 * vertex(0.3*scaleUp,-0.5*scaleUp); vertex(0.3*scaleUp,0.5*scaleUp);
		 * vertex(0.8*scaleUp,0.0*scaleUp); endShape(CLOSE);
		 */
		EvolvioMod.main.popMatrix();
		if (overworldDraw) {
			EvolvioMod.main.fill(0, 0, 1);
			EvolvioMod.main.textFont(EvolvioMod.main.font, 0.2f * scaleUp);
			EvolvioMod.main.textAlign(EvolvioMod.main.CENTER);
			EvolvioMod.main.text(getCreatureName(), (float) (px * scaleUp),
					(float) ((py - getRadius() * 1.4 - 0.07) * scaleUp));
		}
		

//		for(CreaturePeripheral peripheral : peripherals) {
//			peripheral.postCreatureDraw(this, board, scaleUp, camZoom, overworldDraw);
//		}
//		for(CreatureAction action : actions) {
//			action.postCreatureDraw(this, board, scaleUp, camZoom, overworldDraw);
//		}
		for(CreatureFeatureDrawer drawer : featureDrawers) {
			drawer.postCreatureDraw(this, board, scaleUp, overworldDraw);
		}
	}

	public void doThread(double timeStep, Boolean userControl) { // just kidding, multithreading doesn't really help
																	// here.
		// collide(timeStep);
		// metabolize(timeStep);
		// useBrain(timeStep, !userControl);
		thread = new CreatureThread("Thread " + id, this, timeStep, userControl);
		thread.start();
	}

	public void metabolize(double timeStep) {
		loseEnergy(energy * METABOLISM_ENERGY * timeStep);
	}

	public void accelerate(double amount, double timeStep) {
		double multiplied = amount * timeStep / getMass();
		super.setVx(getVX() + Math.cos(rotation) * multiplied);
		super.setVy(getVY() + Math.sin(rotation) * multiplied);
		if (amount >= 0) {
			loseEnergy(amount * ACCELERATION_ENERGY * timeStep);
		} else {
			loseEnergy(Math.abs(amount * ACCELERATION_BACK_ENERGY * timeStep));
		}
	}

	public void turn(double amount, double timeStep) {
		vr += 0.04 * amount * timeStep / getMass();
		loseEnergy(Math.abs(amount * TURN_ENERGY * energy * timeStep));
	}

	public Tile getRandomCoveredTile() {
		double radius = (float) getRadius();
		double choiceX = 0;
		double choiceY = 0;
		while (EvolvioMod.main.dist((float) px, (float) py, (float) choiceX, (float) choiceY) > radius) {
			choiceX = (Math.random() * 2 * radius - radius) + px;
			choiceY = (Math.random() * 2 * radius - radius) + py;
		}
		int x = xBound((int) choiceX);
		int y = yBound((int) choiceY);
		return board.tiles[x][y];
	}

	public void eat(double attemptedAmount, double timeStep) {
		Tile coveredTile = getRandomCoveredTile();

		double amount = ModLoader.creatureEatBehavior.getCreatureEatAmount(this, coveredTile, attemptedAmount,
				timeStep);

		
		// the below original code is now part of a default mod
		/*
		 * double amount = attemptedAmount/(1.0+distance(0,0,vx,vy)*
		 * EAT_WHILE_MOVING_INEFFICIENCY_MULTIPLIER); // The faster you're moving, the
		 * less efficiently you can eat.
		 */

		if (amount < 0) {
			ModLoader.creatureEatBehavior.creatureFailToEatFromTile(this, coveredTile, amount, attemptedAmount,
					timeStep);

			// the below original code is now part of a default mod
			/*
			 * dropEnergy(-amount*timeStep);
			 * loseEnergy(-attemptedAmount*EAT_ENERGY*timeStep);
			 */
		} else {

			// this should go through the mod loader
			ModLoader.creatureEatBehavior.creatureEatFromTile(this, coveredTile, amount, attemptedAmount, timeStep);

			// the below original code is now part of a default mod
			/*
			 * double foodToEat =
			 * coveredTile.foodLevel*(1-Math.pow((1-EAT_SPEED),amount*timeStep));
			 * if(foodToEat > coveredTile.foodLevel){ foodToEat = coveredTile.foodLevel; }
			 * coveredTile.removeFood(foodToEat, true); double foodDistance =
			 * Math.abs(coveredTile.foodType-mouthHue); double multiplier =
			 * 1.0-foodDistance/FOOD_SENSITIVITY; if(multiplier >= 0){
			 * addEnergy(foodToEat*multiplier); }else{ loseEnergy(-foodToEat*multiplier); }
			 * loseEnergy(attemptedAmount*EAT_ENERGY*timeStep);
			 */
		}
	}

	public void fight(double amount, double timeStep) {
		if(amount != brain.getOutput("fight"))
			System.out.println(amount + " from brain: " + brain.getOutput("fight"));
		
		if (amount > 0 && board.year - birthTime >= MATURE_AGE) {
			fightLevel = amount;
			loseEnergy(fightLevel * FIGHT_ENERGY * energy * timeStep);
			for (int i = 0; i < colliders.size(); i++) {
				SoftBody collider = colliders.get(i);
				if (collider.isCreature) {
					float distance = EvolvioMod.main.dist((float) px, (float) py, (float) collider.px,
							(float) collider.py);
					double combinedRadius = getRadius() * FIGHT_RANGE + collider.getRadius();
					if (distance < combinedRadius) {
						((Creature) collider).dropEnergy(fightLevel * INJURED_ENERGY * timeStep);
					}
				}
			}
		} else {
			fightLevel = 0;
		}
	}

	public void loseEnergy(double energyLost) {
		if (energyLost > 0) {
			energy -= energyLost;
		}
	}

	public void dropEnergy(double energyLost) {
		if (energyLost > 0) {
			energyLost = Math.min(energyLost, energy);
			energy -= energyLost;
			// getRandomCoveredTile().addFood(energyLost, hue, true);
			ModLoader.creatureEatBehavior.creatureDepositFoodToTile(this, getRandomCoveredTile(), energyLost);

		}
	}

//	public void see(double timeStep) {
//		for (int k = 0; k < visionAngles.length; k++) {
//			double visionStartX = px;
//			double visionStartY = py;
//			double visionTotalAngle = rotation + visionAngles[k];
//
//			double endX = getVisionEndX(k);
//			double endY = getVisionEndY(k);
//
//			visionOccludedX[k] = endX;
//			visionOccludedY[k] = endY;
//			int c = getColorAt(endX, endY);
//			visionResults[k * 3] = EvolvioMod.main.hue(c);
//			visionResults[k * 3 + 1] = EvolvioMod.main.saturation(c);
//			visionResults[k * 3 + 2] = EvolvioMod.main.brightness(c);
//
//			int tileX = 0;
//			int tileY = 0;
//			int prevTileX = -1;
//			int prevTileY = -1;
//			ArrayList<SoftBody> potentialVisionOccluders = new ArrayList<SoftBody>();
//			for (int DAvision = 0; DAvision < visionDistances[k] + 1; DAvision++) {
//				tileX = (int) (visionStartX + Math.cos(visionTotalAngle) * DAvision);
//				tileY = (int) (visionStartY + Math.sin(visionTotalAngle) * DAvision);
//				if (tileX != prevTileX || tileY != prevTileY) {
//					addPVOs(tileX, tileY, potentialVisionOccluders);
//					if (prevTileX >= 0 && tileX != prevTileX && tileY != prevTileY) {
//						addPVOs(prevTileX, tileY, potentialVisionOccluders);
//						addPVOs(tileX, prevTileY, potentialVisionOccluders);
//					}
//				}
//				prevTileX = tileX;
//				prevTileY = tileY;
//			}
//			double[][] rotationMatrix = new double[2][2];
//			rotationMatrix[1][1] = rotationMatrix[0][0] = Math.cos(-visionTotalAngle);
//			rotationMatrix[0][1] = Math.sin(-visionTotalAngle);
//			rotationMatrix[1][0] = -rotationMatrix[0][1];
//			double visionLineLength = visionDistances[k];
//			for (int i = 0; i < potentialVisionOccluders.size(); i++) {
//				SoftBody body = potentialVisionOccluders.get(i);
//				double x = body.px - px;
//				double y = body.py - py;
//				double r = body.getRadius();
//				double translatedX = rotationMatrix[0][0] * x + rotationMatrix[1][0] * y;
//				double translatedY = rotationMatrix[0][1] * x + rotationMatrix[1][1] * y;
//				if (Math.abs(translatedY) <= r) {
//					if ((translatedX >= 0 && translatedX < visionLineLength && translatedY < visionLineLength)
//							|| distance(0, 0, translatedX, translatedY) < r
//							|| distance(visionLineLength, 0, translatedX, translatedY) < r) { // YES! There is an
//																								// occlussion.
//						visionLineLength = translatedX - Math.sqrt(r * r - translatedY * translatedY);
//						visionOccludedX[k] = visionStartX + visionLineLength * Math.cos(visionTotalAngle);
//						visionOccludedY[k] = visionStartY + visionLineLength * Math.sin(visionTotalAngle);
//						visionResults[k * 3] = body.hue;
//						visionResults[k * 3 + 1] = body.saturation;
//						visionResults[k * 3 + 2] = body.brightness;
//					}
//				}
//			}
//		}
//	}
//
	public int getColorAt(double x, double y) {
		if (x >= 0 && x < board.boardWidth && y >= 0 && y < board.boardHeight) {
			// return board.tiles[(int) (x)][(int) (y)].getColor();
			return ModLoader.tileDrawer.getColor(board.tiles[(int) (x)][(int) (y)]);
		} else {
			return board.BACKGROUND_COLOR;
		}
	}
//
//	public double distance(double x1, double y1, double x2, double y2) {
//		return (Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1)));
//	}
//
//	public void addPVOs(int x, int y, ArrayList<SoftBody> PVOs) {
//		if (x >= 0 && x < board.boardWidth && y >= 0 && y < board.boardHeight) {
//			for (int i = 0; i < board.softBodiesInPositions[x][y].size(); i++) {
//				SoftBody newCollider = (SoftBody) board.softBodiesInPositions[x][y].get(i);
//				if (!PVOs.contains(newCollider) && newCollider != this) {
//					PVOs.add(newCollider);
//				}
//			}
//		}
//	}

	public void returnToEarth() {
		int pieces = 20;
		double radius = (float) getRadius();
		for (int i = 0; i < pieces; i++) {
			// getRandomCoveredTile().addFood(energy / pieces, hue, true);
			ModLoader.creatureEatBehavior.creatureDepositFoodToTile(this, getRandomCoveredTile(), energy / pieces);
			// TODO: this function should go in CreatureEatBehavior
		}
//		for (int x = SBIPMinX; x <= SBIPMaxX; x++) {
//			for (int y = SBIPMinY; y <= SBIPMaxY; y++) {
//				board.softBodiesInPositions[x][y].remove(this);
//			}
//		}
		
		board.creatureQuadTree.remove(collisionBox);
		if (board.selectedCreature == this) {
			board.unselect();
		}
		
		if(board.avatar == this) {
			board.avatar = null;
		}
	}

	public void reproduce(double babySize, double timeStep) {
		if (colliders == null) {
			collide(timeStep);
		}
		int highestGen = 0;
		if (babySize >= 0) {
			ArrayList<Creature> parents = new ArrayList<Creature>(0);
			parents.add(this);
			double availableEnergy = getBabyEnergy();
			for (int i = 0; i < colliders.size(); i++) {
				SoftBody possibleParent = colliders.get(i);
				if (possibleParent.isCreature && ((Creature) possibleParent).getBrain().getOutput("reproduce") > REPRODUCE_WILLINGNESS_THRESHOLD) {
					// Must be a WILLING creature to also give birth.
					float distance = EvolvioMod.main.dist((float) px, (float) py, (float) possibleParent.px,
							(float) possibleParent.py);
					double combinedRadius = getRadius() * FIGHT_RANGE + possibleParent.getRadius();
					if (distance < combinedRadius) {
						parents.add((Creature) possibleParent);
						availableEnergy += ((Creature) possibleParent).getBabyEnergy();
					}
				}
			}

			ArrayList<Creature> parentMasterList = new ArrayList<>();
			parentMasterList.addAll(parents);
			
			if (availableEnergy > babySize) {
				double newPX = EvolvioMod.main.random(-0.01f, 0.01f);
				double newPY = EvolvioMod.main.random(-0.01f, 0.01f); // To avoid landing directly on parents, resulting
																		// in division by 0)
				double newHue = 0;
				double newSaturation = 0;
				double newBrightness = 0;
				double newMouthHue = 0;
				int parentsTotal = parents.size();
				String[] parentNames = new String[parentsTotal];
//				List<List<CreaturePeripheral>> parentPeripherals = new ArrayList<>();
//				for(Creature p : parents) {
//					parentPeripherals.add(p.peripherals);
//				}
				
				List<CreaturePeripheral> newPeripherals = ModLoader.createPeripherals(null, board);//ModLoader.getOffspringPeripherals(parentPeripherals);
				
				Brain newBrain = ModLoader.getOffspringBrain(newPeripherals, parents);
				System.out.println("Offspring brain:\n" + newBrain.makeString());
				
				for (int i = 0; i < parentsTotal; i++) {
					int chosenIndex = (int) EvolvioMod.main.random(0, parents.size());
					Creature parent = parents.get(chosenIndex);
					parents.remove(chosenIndex);
					parent.energy -= babySize * (parent.getBabyEnergy() / availableEnergy);
					newPX += parent.px / parentsTotal;
					newPY += parent.py / parentsTotal;
					newHue += parent.hue / parentsTotal;
					newSaturation += parent.saturation / parentsTotal;
					newBrightness += parent.brightness / parentsTotal;
					newMouthHue += parent.secondaryHue / parentsTotal;
					parentNames[i] = parent.name;
					if (parent.gen > highestGen) {
						highestGen = parent.gen;
					}
				}
				newSaturation = 1;
				newBrightness = 1;
				
				Creature baby = new Creature(newPX, newPY, 0, 0, babySize, density, newHue, newSaturation,
						newBrightness, board, board.year, EvolvioMod.main.random(0, (float) (2 * Math.PI)), 0,
						stitchName(parentNames), andifyParents(parentNames), true, newBrain, newPeripherals, highestGen + 1,
						newMouthHue);
				
				ModLoader.setOffspringAttributes(baby, parentMasterList, board);
				
				board.addCreature(baby);
			}
		}
	}

	public String stitchName(String[] s) {
		String result = "";
		for (int i = 0; i < s.length; i++) {
			float portion = ((float) s[i].length()) / s.length;
			int start = (int) Math.min(Math.max(Math.round(portion * i), 0), s[i].length());
			int end = (int) Math.min(Math.max(Math.round(portion * (i + 1)), 0), s[i].length());
			result = result + s[i].substring(start, end);
		}
		return result;
	}

	public String andifyParents(String[] s) {
		String result = "";
		for (int i = 0; i < s.length; i++) {
			if (i >= 1) {
				result = result + " & ";
			}
			result = result + capitalize(s[i]);
		}
		return result;
	}

	public String createNewName() {
		String nameSoFar = "";
		int chosenLength = (int) (EvolvioMod.main.random(MIN_NAME_LENGTH, MAX_NAME_LENGTH));
		for (int i = 0; i < chosenLength; i++) {
			nameSoFar += getRandomChar();
		}
		return sanitizeName(nameSoFar);
	}

	public char getRandomChar() {
		float letterFactor = EvolvioMod.main.random(0, 100);
		int letterChoice = 0;
		while (letterFactor > 0) {
			letterFactor -= board.letterFrequencies[letterChoice];
			letterChoice++;
		}
		return (char) (letterChoice + 96);
	}

	public String sanitizeName(String input) {
		String output = "";
		int vowelsSoFar = 0;
		int consonantsSoFar = 0;
		for (int i = 0; i < input.length(); i++) {
			char ch = input.charAt(i);
			if (isVowel(ch)) {
				consonantsSoFar = 0;
				vowelsSoFar++;
			} else {
				vowelsSoFar = 0;
				consonantsSoFar++;
			}
			if (vowelsSoFar <= 2 && consonantsSoFar <= 2) {
				output = output + ch;
			} else {
				double chanceOfAddingChar = 0.5;
				if (input.length() <= MIN_NAME_LENGTH) {
					chanceOfAddingChar = 1.0;
				} else if (input.length() >= MAX_NAME_LENGTH) {
					chanceOfAddingChar = 0.0;
				}
				if (EvolvioMod.main.random(0, 1) < chanceOfAddingChar) {
					char extraChar = ' ';
					while (extraChar == ' ' || (isVowel(ch) == isVowel(extraChar))) {
						extraChar = getRandomChar();
					}
					output = output + extraChar + ch;
					if (isVowel(ch)) {
						consonantsSoFar = 0;
						vowelsSoFar = 1;
					} else {
						consonantsSoFar = 1;
						vowelsSoFar = 0;
					}
				} else { // do nothing
				}
			}
		}
		return output;
	}

	public String getCreatureName() {
		return capitalize(name);
	}

	public String capitalize(String n) {
		return n.substring(0, 1).toUpperCase() + n.substring(1, n.length());
	}

	public boolean isVowel(char a) {
		return (a == 'a' || a == 'e' || a == 'i' || a == 'o' || a == 'u' || a == 'y');
	}

	public String mutateName(String input) {
		if (input.length() >= 3) {
			if (EvolvioMod.main.random(0, 1) < 0.2) {
				int removeIndex = (int) EvolvioMod.main.random(0, input.length());
				input = input.substring(0, removeIndex) + input.substring(removeIndex + 1, input.length());
			}
		}
		if (input.length() <= 9) {
			if (EvolvioMod.main.random(0, 1) < 0.2) {
				int insertIndex = (int) EvolvioMod.main.random(0, input.length() + 1);
				input = input.substring(0, insertIndex) + getRandomChar()
						+ input.substring(insertIndex, input.length());
			}
		}
		int changeIndex = (int) EvolvioMod.main.random(0, input.length());
		input = input.substring(0, changeIndex) + getRandomChar() + input.substring(changeIndex + 1, input.length());
		return input;
	}

	public void applyMotions(double timeStep) {
//		if (getRandomCoveredTile().fertility > 1) {
		if (getRandomCoveredTile().isWater()) {
			loseEnergy(SWIM_ENERGY * energy);
		}
		super.applyMotions(timeStep);
		rotation += vr;
		vr *= Math.max(0, 1 - FRICTION / getMass());
	}

	public double getEnergyUsage(double timeStep) {
		return (energy - previousEnergy[ENERGY_HISTORY_LENGTH - 1]) / ENERGY_HISTORY_LENGTH / timeStep;
	}

	public double getBabyEnergy() {
		return energy - SAFE_SIZE;
	}

	public void addEnergy(double amount) {
		energy += amount;
	}

	public void setPreviousEnergy() {
		for (int i = ENERGY_HISTORY_LENGTH - 1; i >= 1; i--) {
			previousEnergy[i] = previousEnergy[i - 1];
		}
		previousEnergy[0] = energy;
	}

	public double measure(int choice) {
		int sign = 1 - 2 * (choice % 2);
		if (choice < 2) {
			return sign * energy;
		} else if (choice < 4) {
			return sign * birthTime;
		} else if (choice == 6 || choice == 7) {
			return sign * gen;
		}
		return 0;
	}

	public void setHue(double set) {
		hue = Math.min(Math.max(set, 0), 1);
	}

	public void setMouthHue(double set) {
		secondaryHue = Math.min(Math.max(set, 0), 1);
	}

	public void setSaturarion(double set) {
		saturation = Math.min(Math.max(set, 0), 1);
	}

	public void setBrightness(double set) {
		brightness = Math.min(Math.max(set, 0), 1);
	}

	/*
	 * public void setVisionAngle(double set){ visionAngle =
	 * set;//Math.min(Math.max(set,-Math.PI/2),Math.PI/2); while(visionAngle <
	 * -Math.PI){ visionAngle += Math.PI*2; } while(visionAngle > Math.PI){
	 * visionAngle -= Math.PI*2; } } public void setVisionDistance(double set){
	 * visionDistance = Math.min(Math.max(set,0),MAX_VISION_DISTANCE); }
	 */
	/*
	 * public double getVisionStartX(){ return px;//+getRadius()*Math.cos(rotation);
	 * } public double getVisionStartY(){ return
	 * py;//+getRadius()*Math.sin(rotation); }
	 */
//	public double getVisionEndX(int i) {
//		double visionTotalAngle = rotation + visionAngles[i];
//		return px + visionDistances[i] * Math.cos(visionTotalAngle);
//	}
//
//	public double getVisionEndY(int i) {
//		double visionTotalAngle = rotation + visionAngles[i];
//		return py + visionDistances[i] * Math.sin(visionTotalAngle);
//	}

	public double getEnergyLevel() {
		return energy;
	}

	public Brain getBrain() {
		return brain;
	}
	
	public boolean isAvatar() {
		return board.avatar == this;
	}
	
	public String toString() {
		StringBuilder s = new StringBuilder();
		
		s.append("+|- Name: " + name + "\n");
		s.append("+|- Parents: " + parents + "\n");
		
		s.append("+|- Gen: " + gen + "\n");
		s.append("+|- ID: " + id + "\n");

		s.append("\n");
		
		s.append("+|- SoftBody Data\n");
		s.append(super.makeString());
		s.append("+|- \\SoftBody Data\n");
		
		s.append("\n");
		
		s.append("+|- Brain\n");
		s.append(brain.makeString());
		s.append("+|- \\Brain\n");
		
		s.append("\n");
		
		s.append("+|- Attributes\n");
		for(Entry<String, CreatureAttribute> ent : attributes.entrySet()) {
			s.append("-|+" + ent.getKey() + ":" + ent.getValue().getClass().getCanonicalName() + "\n");
			s.append(ent.getValue().makeString());
			s.append("\n");
		}
		s.append("+|- \\Attributes\n");
		
		s.append("+|- Actions\n");
		for(CreatureAction a : actions) {
			s.append("-|+" + a.getClass().getCanonicalName() + "\n");
			s.append(a.toString());
			s.append("\n");
		}
		s.append("+|- \\Actions\n");
		
		s.append("+|- Peripherals\n");
		for(CreaturePeripheral a : peripherals) {
			s.append("-|+" + a.getClass().getCanonicalName() + "\n");
			s.append(a.toString());
			s.append("\n");
		}
		s.append("+|- \\Peripherals\n");
		
		s.append("+|- FeatureDrawers\n");
		for(CreatureFeatureDrawer a : featureDrawers) {
			s.append("-|+" + a.getClass().getCanonicalName() + "\n");
			s.append(a.toString());
			s.append("\n");
		}
		s.append("+|- \\FeatureDrawers\n");
		
		return s.toString();
	}
	
	public Creature fromString(String s) {
		//TODO: this
		return null;
	}
}
