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
			String tparents, boolean mutateName, Brain tbrain, List<CreaturePeripheral> per, int tgen, double tmouthHue) {

		super(tpx, tpy, tvx, tvy, tenergy, tdensity, thue, tsaturation, tbrightness, tb, bt);
		
		if(tbrain == null) {
			peripherals = ModLoader.createPeripherals(this, board);
			brain = ModLoader.createBrain(this, tb);
			ModLoader.initializeAttributes(this, tb);
		} else {
			brain = tbrain;
			peripherals = per;
		}
		
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
		
		gen = tgen;
		secondaryHue = tmouthHue;
	}

	public CreatureAttribute getAttribute(String name) {
		return attributes.get(name);
	}

	public void drawBrain(PFont font, float scaleUp, int mX, int mY) {
		brain.draw(font, scaleUp, mX, mY);
	}
	
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
			
			for(CreatureAction a : actions) {
				a.doAction(brain, this, board, timeStep);
			}
		}
	}

	public void drawSoftBody(float scaleUp, float camZoom, boolean overworldDraw) {
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

		if (amount < 0) {
			ModLoader.creatureEatBehavior.creatureFailToEatFromTile(this, coveredTile, amount, attemptedAmount, timeStep);
		} else {
			ModLoader.creatureEatBehavior.creatureEatFromTile(this, coveredTile, amount, attemptedAmount, timeStep);
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
			
			ModLoader.creatureEatBehavior.creatureDepositFoodToTile(this, getRandomCoveredTile(), energyLost);
		}
	}

	public int getColorAt(double x, double y) {
		if (x >= 0 && x < board.boardWidth && y >= 0 && y < board.boardHeight) {
			return ModLoader.tileDrawer.getColor(board.tiles[(int) (x)][(int) (y)]);
		} else {
			return ModLoader.tileDrawer.getBackgroundColor();
		}
	}

	public void returnToEarth() {
		int pieces = 20;
		for (int i = 0; i < pieces; i++) {
			ModLoader.creatureEatBehavior.creatureDepositFoodToTile(this, getRandomCoveredTile(), energy / pieces);
		}
		
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

			if(!ModLoader.canReproduce(parents)) {
				return;
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

	public double getEnergyLevel() {
		return energy;
	}

	public Brain getBrain() {
		return brain;
	}
	
	public boolean isAvatar() {
		return board.avatar == this;
	}
	
	public List<CreaturePeripheral> getPeripherals() {
		return peripherals;
	}
	
	public String makeString() {
		StringBuilder s = new StringBuilder();
		
		s.append("Record of creature " + name + "\n");
		s.append("\n");
		s.append("+|- Name:\n" + name + "\n");
		s.append("+|- Parents:\n" + parents + "\n");
		
		s.append("+|- Gen:\n" + gen + "\n");
		s.append("+|- ID:\n" + id + "\n");

		s.append("\n");
		
		s.append("+|- SoftBody Data:\n");
		s.append(super.makeString());
		s.append("\n");
		
		s.append("\n");
		
		s.append("+|- Brain:\n");
		s.append(brain.makeString());
		s.append("\n");
		
		s.append("\n");
		
		s.append("+|- Attributes:\n");
		for(Entry<String, CreatureAttribute> ent : attributes.entrySet()) {
			s.append("-|+" + ent.getValue().getClass().getCanonicalName() + ":" + ent.getKey() + "\n");
			s.append(ent.getValue().makeString());
			s.append("\n");
		}
		s.append("\n");
		
		s.append("+|- Actions:\n");
		for(CreatureAction a : actions) {
			s.append("-|+" + a.getClass().getCanonicalName() + "\n");
//			s.append(a.makeString());
			s.append("\n");
		}
		s.append("\n");
		
		s.append("+|- Peripherals:\n");
		for(CreaturePeripheral a : peripherals) {
			s.append("-|+" + a.getClass().getCanonicalName() + "\n");
//			s.append(a.makeString());
			s.append("\n");
		}
		s.append("\n");
		
		s.append("+|- FeatureDrawers:\n");
		for(CreatureFeatureDrawer a : featureDrawers) {
			s.append("-|+" + a.getClass().getCanonicalName()+"\n");
//			s.append(a.makeString());
			s.append("\n");
		}
		s.append("\n");
		
		return s.toString();
	}
	
	public static Creature fromString(String creatureString) throws Exception {
		String[] segments = creatureString.split("\n\\+\\|- \\w+:\n");
		
		Creature revived = (Creature) SoftBody.fromString(segments[5]);
		
		revived.name = segments[1];
		revived.parents = segments[2];
		revived.gen = Integer.parseInt(segments[3]);
		revived.id = Integer.parseInt(segments[4]);
		
		revived.brain = ModLoader.brainModel.newInstance().fromString(segments[6]);
		
		String[] attributeStrings = segments[7].split("-\\|\\+");
		for(String as : attributeStrings) {
			int endOfHeading = as.indexOf("\n");
			String heading = as.substring(0, endOfHeading);
			String body = as.substring(endOfHeading+1);
			
			int headingSplit = heading.indexOf(":");
			String cannonicalName = heading.substring(0, headingSplit);
			
			Class<CreatureAttribute> attributeClass = (Class<CreatureAttribute>) ModLoader.loadClass(cannonicalName);
			CreatureAttribute revivedAttr = attributeClass.newInstance().fromString(body);
			
			revived.attributes.put(revivedAttr.getName(), revivedAttr);
		}
		
		String[] actionStrings = segments[8].split("-\\|\\+");
		for(String as : actionStrings) {
			int endOfHeading = as.indexOf("\n");
			String heading = as.substring(0, endOfHeading);
			String body = as.substring(endOfHeading+1);
			
			Class<CreatureAction> actionClass = (Class<CreatureAction>) ModLoader.loadClass(heading);
			CreatureAction revivedAct = actionClass.newInstance();
			
			revived.actions.add(revivedAct);
		}
		
		String[] peripheralStrings = segments[9].split("-\\|\\+");
		for(String ps : peripheralStrings) {
			int endOfHeading = ps.indexOf("\n");
			String heading = ps.substring(0, endOfHeading);
			String body = ps.substring(endOfHeading+1);
			
			Class<CreaturePeripheral> peripheralClass = (Class<CreaturePeripheral>) ModLoader.loadClass(heading);
			CreaturePeripheral revivedPer = peripheralClass.newInstance();
			
			revived.peripherals.add(revivedPer);
		}
		
		String[] drawerStrings = segments[10].split("-\\|\\+");
		for(String ds : drawerStrings) {
			int endOfHeading = ds.indexOf("\n");
			String heading = ds.substring(0, endOfHeading);
			String body = ds.substring(endOfHeading+1);
			
			Class<CreatureFeatureDrawer> drawerClass = (Class<CreatureFeatureDrawer>) ModLoader.loadClass(heading);
			CreatureFeatureDrawer revivedDraw = drawerClass.newInstance();
			
			revived.featureDrawers.add(revivedDraw);
		}
		
		return revived;
	}
}
