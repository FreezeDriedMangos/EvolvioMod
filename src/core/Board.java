package core;

import java.util.ArrayList;
import java.util.List;

import com.github.ryanp102694.QuadTree;
import com.github.ryanp102694.geometry.AbstractRectangleObject;
import com.github.ryanp102694.geometry.RectangleObject;

import core.modAPI.Button;
import core.modAPI.CreatureAction;
import core.modAPI.CreatureAttribute;
import processing.core.PFont;

public class Board {
	public static boolean BLEND_TILES = false;
	
	static final int MAX_PLAYSPEED = 4056;
	static final int MAX_REALTIME_PLAYSPEED = 2048;
	static final float NON_REALTIME_DRAWRATE = 10; // draw once every NON_REALTIME_DRAWRATE years if playspeed is >=
													// MAX_REALTIME_PLAYSPEED

	public int boardWidth;
	public int boardHeight;
	int creatureMinimum;
	Tile[][] tiles;
	double year = 0;
	float MIN_TEMPERATURE;
	float MAX_TEMPERATURE;
	final float THERMOMETER_MIN = -2;
	final float THERMOMETER_MAX = 2;
	int ROCKS_TO_ADD;
	final float MIN_ROCK_ENERGY_BASE = 0.8f;
	final float MAX_ROCK_ENERGY_BASE = 1.6f;
	final float MIN_CREATURE_ENERGY = 1.2f;
	final float MAX_CREATURE_ENERGY = 2.0f;
	final float ROCK_DENSITY = 5;
	final float OBJECT_TIMESTEPS_PER_YEAR = 100;
	final int ROCK_COLOR = EvolvioMod.main.color(0, 0, 0.5f);
	final int BACKGROUND_COLOR = EvolvioMod.main.color(0, 0, 0.1f);
	final float MINIMUM_SURVIVABLE_SIZE = 0.06f;
	public final float CREATURE_STROKE_WEIGHT = 0.6f;
	//public ArrayList[][] softBodiesInPositions;
	QuadTree creatureQuadTree;
	Creature avatar = null;
	
	
	ArrayList<SoftBody> rocks;
	ArrayList<Creature> creatures;
	Creature selectedCreature = null;
	int softBodyIDUpTo = 0;
	float[] letterFrequencies = { 8.167f, 1.492f, 2.782f, 4.253f, 12.702f, 2.228f, 2.015f, 6.094f, 6.966f, 0.153f,
			0.772f, 4.025f, 2.406f, 6.749f, 7.507f, 1.929f, 0.095f, 5.987f, 6.327f, 9.056f, 2.758f, 0.978f, 2.361f,
			0.150f, 1.974f, 10000.0f };// 0.074};
	final int LIST_SLOTS = 6;
	int creatureRankMetric = 0;
	int buttonColor = EvolvioMod.main.color(0.82f, 0.8f, 0.7f);
	Creature[] list = new Creature[LIST_SLOTS];
	final int creatureMinimumIncrement = 5;
	String folder = "TEST";
	int[] fileSaveCounts;
	double[] fileSaveTimes;
	double imageSaveInterval = 1;
	double textSaveInterval = 1;
	final double FLASH_SPEED = 80;
	boolean userControl;
	double temperature;
	double MANUAL_BIRTH_SIZE = 1.2;
	boolean wasPressingB = false;
	double timeStep;
	int POPULATION_HISTORY_LENGTH = 200;
	int[] populationHistory;
	double recordPopulationEvery = 0.02;
	int playSpeed = 1;
	public int threadsToFinish = 0;

	protected Board() {
		creatureQuadTree = new QuadTree();
	}
	
	public Board(int w, int h, float stepSize, float min, float max, int rta, int cm, int SEED,
			String INITIAL_FILE_NAME, double ts) {
		this();
		
		EvolvioMod.main.noiseSeed(SEED);
		EvolvioMod.main.randomSeed(SEED);
		boardWidth = w;
		boardHeight = h;
		tiles = new Tile[w][h];
		for (int x = 0; x < boardWidth; x++) {
			for (int y = 0; y < boardHeight; y++) {
				// bigForce controls how big the land masses are
				float bigForce = EvolvioMod.main.pow(((float) y) / boardHeight, 0.5f);
				float fertility = EvolvioMod.main.noise(x * stepSize * 3, y * stepSize * 3) * (1 - bigForce) * 5.0f
						+ EvolvioMod.main.noise(x * stepSize * 0.5f, y * stepSize * 0.5f) * bigForce * 5.0f - 1.5f;
				float climateType = EvolvioMod.main.noise(x * stepSize * 0.2f + 10000, y * stepSize * 0.2f + 10000)
						* 1.63f - 0.4f;
				climateType = Math.min(Math.max(climateType, 0), 0.8f);
				tiles[x][y] = new Tile(x, y, fertility, 0, climateType, this, stepSize);
			}
		}
		MIN_TEMPERATURE = min;
		MAX_TEMPERATURE = max;

//		softBodiesInPositions = new ArrayList[boardWidth][boardHeight];
//		for (int x = 0; x < boardWidth; x++) {
//			for (int y = 0; y < boardHeight; y++) {
//				softBodiesInPositions[x][y] = new ArrayList<SoftBody>(0);
//			}
//		}
		//creatureQuadTree = new QuadTree();

		ROCKS_TO_ADD = rta;
		rocks = new ArrayList<SoftBody>(0);
		for (int i = 0; i < ROCKS_TO_ADD; i++) {
			rocks.add(new SoftBody(EvolvioMod.main.random(0, boardWidth), EvolvioMod.main.random(0, boardHeight), 0, 0,
					getRandomSize(), ROCK_DENSITY, EvolvioMod.main.hue(ROCK_COLOR),
					EvolvioMod.main.saturation(ROCK_COLOR), EvolvioMod.main.brightness(ROCK_COLOR), this, year));
		}

		creatureMinimum = cm;
		creatures = new ArrayList<Creature>(0);
		maintainCreatureMinimum(false);
		for (int i = 0; i < LIST_SLOTS; i++) {
			list[i] = null;
		}
		folder = INITIAL_FILE_NAME;
		fileSaveCounts = new int[4];
		fileSaveTimes = new double[4];
		for (int i = 0; i < 4; i++) {
			fileSaveCounts[i] = 0;
			fileSaveTimes[i] = -999;
		}
		userControl = true;
		timeStep = ts;
		populationHistory = new int[POPULATION_HISTORY_LENGTH];
		for (int i = 0; i < POPULATION_HISTORY_LENGTH; i++) {
			populationHistory[i] = 0;
		}
	}
	
	public Creature spawnCreature() {
		return this.spawnCreature(EvolvioMod.main.random(0, boardWidth), EvolvioMod.main.random(0, boardHeight));
	}
	public Creature spawnCreature(double x, double y) {
		Creature c =
				(new Creature(x, y,
						0, 0, EvolvioMod.main.random(MIN_CREATURE_ENERGY, MAX_CREATURE_ENERGY), 1,
						EvolvioMod.main.random(0, 1), 1, 1, this, year,
						EvolvioMod.main.random(0, 2 * EvolvioMod.main.PI), 0, "", "[PRIMORDIAL]", true, null,
						null, 1, EvolvioMod.main.random(0, 1)));
		addCreature(c);
		return c;
	}

	public void drawBoard(float scaleUp, float camZoom, int mX, int mY) {
		for (int x = 0; x < boardWidth; x++) {
			for (int y = 0; y < boardHeight; y++) {
				tiles[x][y].update(); //TODO: move this somewhere else
				ModLoader.tileDrawer.draw(tiles[x][y], scaleUp);
			}
		}
		if(BLEND_TILES) {
			for (int x = 0; x < boardWidth; x++) {
				for (int y = 0; y < boardHeight; y++) {
					ModLoader.tileDrawer.drawBlendLayer(tiles[x][y], scaleUp);
				}
			}
		}
		if(0<= mX && mX < boardWidth && 0<= mY && mY < boardHeight) {
			ModLoader.tileDrawer.drawInformation(tiles[mX][mY], scaleUp);
		}
		
		for (int i = 0; i < rocks.size(); i++) {
			rocks.get(i).drawSoftBody(scaleUp);
		}
		for (int i = 0; i < creatures.size(); i++) {
			creatures.get(i).drawSoftBody(scaleUp, camZoom, true);
		}
	}

	public void drawBlankBoard(float scaleUp) {
		EvolvioMod.main.fill(BACKGROUND_COLOR);
		EvolvioMod.main.rect(0, 0, scaleUp * boardWidth, scaleUp * boardHeight);
	}

	public void drawUI(float scaleUp, double timeStep, int x1, int y1, int x2, int y2, PFont font) {
		EvolvioMod.main.fill(0, 0, 0);
		EvolvioMod.main.noStroke();
		EvolvioMod.main.rect(x1, y1, x2 - x1, y2 - y1);

		EvolvioMod.main.pushMatrix();
		EvolvioMod.main.translate(x1, y1);

		EvolvioMod.main.fill(0, 0, 1);
		EvolvioMod.main.textAlign(EvolvioMod.main.LEFT);
		EvolvioMod.main.textFont(font, 48);
		String yearText = "Year " + EvolvioMod.main.nf((float) year, 0, 2);
		EvolvioMod.main.text(yearText, 10, 48);
		float seasonTextXCoor = EvolvioMod.main.textWidth(yearText) + 50;
		EvolvioMod.main.textFont(font, 24);
		EvolvioMod.main.text("Population: " + creatures.size(), 10, 80);
		String[] seasons = { "Winter", "Spring", "Summer", "Autumn" };
		EvolvioMod.main.text(seasons[(int) (getSeason() * 4)], seasonTextXCoor, 30);

		if (selectedCreature == null) {
			for (int i = 0; i < LIST_SLOTS; i++) {
				list[i] = null;
			}
			for (int i = 0; i < creatures.size(); i++) {
				int lookingAt = 0;
				if (creatureRankMetric == 4) {
					while (lookingAt < LIST_SLOTS && list[lookingAt] != null
							&& list[lookingAt].name.compareTo(creatures.get(i).name) < 0) {
						lookingAt++;
					}
				} else if (creatureRankMetric == 5) {
					while (lookingAt < LIST_SLOTS && list[lookingAt] != null
							&& list[lookingAt].name.compareTo(creatures.get(i).name) >= 0) {
						lookingAt++;
					}
				} else {
					while (lookingAt < LIST_SLOTS && list[lookingAt] != null && list[lookingAt]
							.measure(creatureRankMetric) > creatures.get(i).measure(creatureRankMetric)) {
						lookingAt++;
					}
				}
				if (lookingAt < LIST_SLOTS) {
					for (int j = LIST_SLOTS - 1; j >= lookingAt + 1; j--) {
						list[j] = list[j - 1];
					}
					list[lookingAt] = creatures.get(i);
				}
			}
			double maxEnergy = 0;
			for (int i = 0; i < LIST_SLOTS; i++) {
				if (list[i] != null && list[i].energy > maxEnergy) {
					maxEnergy = list[i].energy;
				}
			}
			for (int i = 0; i < LIST_SLOTS; i++) {
				if (list[i] != null) {
					list[i].preferredRank += (i - list[i].preferredRank) * 0.4;
					float y = y1 + 175 + 70 * list[i].preferredRank;
					drawCreature(list[i], 45, y + 5, 2.3f, scaleUp);
					EvolvioMod.main.textFont(font, 24);
					EvolvioMod.main.textAlign(EvolvioMod.main.LEFT);
					EvolvioMod.main.noStroke();
					EvolvioMod.main.fill(0.333f, 1, 0.4f);
					float multi = (x2 - x1 - 200);
					if (list[i].energy > 0) {
						EvolvioMod.main.rect(85, y + 5, (float) (multi * list[i].energy / maxEnergy), 25);
					}
					if (list[i].energy > 1) {
						EvolvioMod.main.fill(0.333f, 1, 0.8f);
						EvolvioMod.main.rect(85 + (float) (multi / maxEnergy), y + 5,
								(float) (multi * (list[i].energy - 1) / maxEnergy), 25);
					}
					EvolvioMod.main.fill(0, 0, 1);
					EvolvioMod.main.text(
							list[i].getCreatureName() + " [" + list[i].id + "] (" + toAge(list[i].birthTime) + ")", 90,
							y);
					EvolvioMod.main.text("Energy: " + EvolvioMod.main.nf(100 * (float) (list[i].energy), 0, 2), 90,
							y + 25);
				}
			}
			EvolvioMod.main.noStroke();
			EvolvioMod.main.textAlign(EvolvioMod.main.CENTER);
			EvolvioMod.main.textFont(font, 19);
			
			int padding = 10;
			int numCols = 2;
			for(int i = 0; i < ModLoader.buttons.size(); i++) { // col 2
				Button button = ModLoader.buttons.get(i);
				int x = getButtonX(i);
				int y = getButtonY(i);
				
				EvolvioMod.main.fill(buttonColor);
				EvolvioMod.main.rect(x, y, Button.STANDARD_BUTTON_WIDTH, Button.STANDARD_BUTTON_HEIGHT);

				EvolvioMod.main.fill(0, 0, 1, button.getFlashAlpha());
				EvolvioMod.main.rect(x, y, Button.STANDARD_BUTTON_WIDTH, Button.STANDARD_BUTTON_HEIGHT);

				String line1 = button.getText();
				String line2 = button.getSecondLineText();
				
				EvolvioMod.main.fill(0, 0, 1, 1);
				if (line1 != null) EvolvioMod.main.text(line1, x + 110, y + 17);
				if (line2 != null) EvolvioMod.main.text(line2, x + 110, y + 37);
			}
			
//			EvolvioMod.main.rect(10, 95, 220, 40); // buttons are (220 x 40), col 1 starts at (10, 90)
//			EvolvioMod.main.rect(240, 95, 220, 40); // col 2 starts at (240, 95)
//			EvolvioMod.main.fill(0, 0, 1);
//			EvolvioMod.main.textAlign(EvolvioMod.main.CENTER);
//			EvolvioMod.main.text("Reset zoom", 120, 123);
//			String[] sorts = { "Biggest", "Smallest", "Youngest", "Oldest", "A to Z", "Z to A", "Highest Gen",
//					"Lowest Gen" };
//			EvolvioMod.main.text("Sort by: " + sorts[creatureRankMetric], 350, 123);
//
//			EvolvioMod.main.textFont(font, 19);
//			String[] buttonTexts = { "Brain Control", "Maintain pop. at " + creatureMinimum, "Screenshot now",
//					"-   Image every " + EvolvioMod.main.nf((float) imageSaveInterval, 0, 2) + " years   +",
//					"Text file now",
//					"-    Text every " + EvolvioMod.main.nf((float) textSaveInterval, 0, 2) + " years    +",
//					"-    Play Speed (" + playSpeed + "x)    +", "This button does nothing" };
//			if (userControl) {
//				buttonTexts[0] = "Keyboard Control";
//			}
//			for (int i = 0; i < 8; i++) {
//				float x = (i % 2) * 230 + 10;
//				float y = EvolvioMod.main.floor(i / 2) * 50 + 570;
//				EvolvioMod.main.fill(buttonColor);
//				EvolvioMod.main.rect(x, y, 220, 40);
//				if (i >= 2 && i < 6) {
//					double flashAlpha = 1.0 * Math.pow(0.5, (year - fileSaveTimes[i - 2]) * FLASH_SPEED);
//					EvolvioMod.main.fill(0, 0, 1, (float) flashAlpha);
//					EvolvioMod.main.rect(x, y, 220, 40);
//				}
//				EvolvioMod.main.fill(0, 0, 1, 1);
//				EvolvioMod.main.text(buttonTexts[i], x + 110, y + 17);
//				if (i == 0) {
//				} else if (i == 1) {
//					EvolvioMod.main.text(
//							"-" + creatureMinimumIncrement + "                    +" + creatureMinimumIncrement,
//							x + 110, y + 37);
//				} else if (i <= 5) {
//					EvolvioMod.main.text(getNextFileName(i - 2), x + 110, y + 37);
//				}
//			}
		} else {
			float energyUsage = (float) selectedCreature.getEnergyUsage(timeStep);
			EvolvioMod.main.noStroke();
			if (energyUsage <= 0) {
				EvolvioMod.main.fill(0, 1, 0.5f);
			} else {
				EvolvioMod.main.fill(0.33f, 1, 0.4f);
			}
			float EUbar = 20 * energyUsage;
			EvolvioMod.main.rect(110, 280, Math.min(Math.max(EUbar, -110), 110), 25);
			if (EUbar < -110) {
				EvolvioMod.main.rect(0, 280, 25, (-110 - EUbar) * 20 + 25);
			} else if (EUbar > 110) {
				float h = (EUbar - 110) * 20 + 25;
				EvolvioMod.main.rect(185, 280 - h, 25, h);
			}
			EvolvioMod.main.fill(0, 0, 1);
			EvolvioMod.main.text("Name: " + selectedCreature.getCreatureName(), 10, 225);
			EvolvioMod.main.text("Energy: " + EvolvioMod.main.nf(100 * (float) selectedCreature.energy, 0, 2) + " yums",
					10, 250);
			EvolvioMod.main.text("E Change: " + EvolvioMod.main.nf(100 * energyUsage, 0, 2) + " yums/year", 10, 275);

			// draw the creature's information
			EvolvioMod.main.text("ID: " + selectedCreature.id, 10, 325);
			EvolvioMod.main.text("X: " + EvolvioMod.main.nf((float) selectedCreature.px, 0, 2), 10, 350);
			EvolvioMod.main.text("Y: " + EvolvioMod.main.nf((float) selectedCreature.py, 0, 2), 10, 375);
			EvolvioMod.main.text("Rotation: " + EvolvioMod.main.nf((float) selectedCreature.rotation, 0, 2), 10, 400);
			EvolvioMod.main.text("B-day: " + toDate(selectedCreature.birthTime), 10, 425);
			EvolvioMod.main.text("(" + toAge(selectedCreature.birthTime) + ")", 10, 450);
			EvolvioMod.main.text("Generation: " + selectedCreature.gen, 10, 475);
			EvolvioMod.main.text("Parents: " + selectedCreature.parents, 10, 500, 210, 255);
			EvolvioMod.main.text("Hue: " + EvolvioMod.main.nf((float) (selectedCreature.hue), 0, 2), 10, 550, 210, 255);
			//EvolvioMod.main.text("Mouth hue: " + EvolvioMod.main.nf((float) (selectedCreature.secondaryHue), 0, 2), 10,
			//		575, 210, 255);
			
			final float textHeight = EvolvioMod.main.textAscent() + EvolvioMod.main.textDescent();
			int attNum = 1;
			for(CreatureAttribute<?> att : selectedCreature.attributes.values()) {
				EvolvioMod.main.text(att.getName() + ": " + att.getValue(), 10, 570 + (attNum++)*textHeight);
			}
			
			if (userControl || selectedCreature == avatar) {
				attNum++;
				
//				EvolvioMod.main.text(
//						"Controls:\nUp/Down: Move\nLeft/Right: Rotate\nSpace: Eat\nF: Fight\nV: Vomit\nU,J: Change color"
//								+ "\nB: Give birth (Not possible if under "
//								+ Math.round((MANUAL_BIRTH_SIZE + 1) * 100) + " yums)",
//						10, /*625*/570 + (attNum++)*textHeight, 250, 400);
//				
//				float startY = attNum+textHeight*7;
//				for(CreatureAction a : selectedCreature.actions) {
//					EvolvioMod.main.text(a.getUserInstructions(), 10, startY, 250, 400);
//					startY += textHeight;
//				}
				
				StringBuilder controlString = new StringBuilder();
				controlString.append("Controls:\nUp/Down: Move\nLeft/Right: Rotate\nSpace: Eat\nF: Fight\nV: Vomit\nU,J: Change color"
						+ "\nB: Give birth (Not possible if under "
						+ Math.round((MANUAL_BIRTH_SIZE + 1) * 100) + " yums)");
				for(CreatureAction a : selectedCreature.actions) {
					controlString.append("\n" + a.getUserInstructions());
				}
				
				EvolvioMod.main.text(controlString.toString(), 10, 570 + (attNum++)*textHeight, 250, 400);
				
			}
			EvolvioMod.main.pushMatrix();
			EvolvioMod.main.translate(400, 80);
			float apX = EvolvioMod.main.round((EvolvioMod.main.mouseX * EvolvioMod.main.WINDOW_SCALE() - 400 - x1) / 46.0f);
			float apY = EvolvioMod.main.round((EvolvioMod.main.mouseY * EvolvioMod.main.WINDOW_SCALE() - 80 - y1) / 46.0f);
			selectedCreature.drawBrain(font, 46, (int) apX, (int) apY);
			EvolvioMod.main.popMatrix();
		}

		if (selectedCreature == null) {
			drawPopulationGraph(x1, x2, y1, y2);
		}

		EvolvioMod.main.fill(0, 0, 0);
		EvolvioMod.main.textAlign(EvolvioMod.main.RIGHT);
		EvolvioMod.main.textFont(font, 24);
		EvolvioMod.main.text("Population: " + creatures.size(), x2 - x1 - 10, y2 - y1 - 10);
		EvolvioMod.main.popMatrix();

		EvolvioMod.main.pushMatrix();
		EvolvioMod.main.translate(x2, y1);
		EvolvioMod.main.textAlign(EvolvioMod.main.RIGHT);
		EvolvioMod.main.textFont(font, 24);
		EvolvioMod.main.text("Temperature", -10, 24);
		drawThermometer(-45, 30, 20, 660, temperature, THERMOMETER_MIN, THERMOMETER_MAX,
				EvolvioMod.main.color(0, 1, 1));
		EvolvioMod.main.popMatrix();

		if (selectedCreature != null) {
			drawCreature(selectedCreature, x1 + 65, y1 + 147, 2.3f, scaleUp);
		}
	}

	static int getButtonX(int i) {
		int padding = Button.PADDING;
		int col = i % Button.NUM_COLUMNS;
		
		int x = padding + col*(padding+Button.STANDARD_BUTTON_WIDTH);
		return x;
	}
	static int getButtonY(int i) {
		int padding = Button.PADDING;
		int row = i / Button.NUM_COLUMNS;
		
		int y = /*95*/ 570 + row*(padding+Button.STANDARD_BUTTON_HEIGHT);
		return y;
	}
	
	void drawPopulationGraph(float x1, float x2, float y1, float y2) {
		float barWidth = (x2 - x1) / ((float) (POPULATION_HISTORY_LENGTH));
		EvolvioMod.main.noStroke();
		EvolvioMod.main.fill(0.33333f, 1, 0.6f);
		int maxPopulation = 0;
		for (int i = 0; i < POPULATION_HISTORY_LENGTH; i++) {
			if (populationHistory[i] > maxPopulation) {
				maxPopulation = populationHistory[i];
			}
		}
		for (int i = 0; i < POPULATION_HISTORY_LENGTH; i++) {
			float h = (((float) populationHistory[i]) / maxPopulation) * (y2 - 770);
			EvolvioMod.main.rect((POPULATION_HISTORY_LENGTH - 1 - i) * barWidth, y2 - h, barWidth, h);
		}
	}

	String getNextFileName(int type) {
		String[] modes = { "manualImgs", "autoImgs", "manualTexts", "autoTexts" };
		String ending = ".png";
		if (type >= 2) {
			ending = ".txt";
		}
		return folder + "/" + modes[type] + "/" + EvolvioMod.main.nf(fileSaveCounts[type], 5) + ending;
	}

	public void iterate(double timeStep) {
		double prevYear = year;
		year += timeStep;
		if (Math.floor(year / recordPopulationEvery) != Math.floor(prevYear / recordPopulationEvery)) {
			for (int i = POPULATION_HISTORY_LENGTH - 1; i >= 1; i--) {
				populationHistory[i] = populationHistory[i - 1];
			}
			populationHistory[0] = creatures.size();
		}
		temperature = getGrowthRate(getSeason());
		double tempChangeIntoThisFrame = temperature - getGrowthRate(getSeason() - timeStep);
		double tempChangeOutOfThisFrame = getGrowthRate(getSeason() + timeStep) - temperature;
		if (tempChangeIntoThisFrame * tempChangeOutOfThisFrame <= 0) { // Temperature change flipped direction.
			for (int x = 0; x < boardWidth; x++) {
				for (int y = 0; y < boardHeight; y++) {
					tiles[x][y].update();
				}
			}
		}
		/*
		 * for(int x = 0; x < boardWidth; x++){ for(int y = 0; y < boardHeight; y++){
		 * tiles[x][y].iterate(this, year); } }
		 */
		for (int i = 0; i < creatures.size(); i++) {
			creatures.get(i).setPreviousEnergy();
		}
		/*
		 * for(int i = 0; i < rocks.size(); i++){
		 * rocks.get(i).collide(timeStep*OBJECT_TIMESTEPS_PER_YEAR); }
		 */
		maintainCreatureMinimum(false);
		threadsToFinish = creatures.size();
		for (int i = 0; i < creatures.size(); i++) {
			Creature me = creatures.get(i);
			// me.doThread(timeStep, userControl);
			me.collide(timeStep);
			me.metabolize(timeStep);
			
			if ((userControl && me == selectedCreature) || me == avatar) {
				if (EvolvioMod.main.keyPressed) {
					if (EvolvioMod.main.key == EvolvioMod.main.CODED) {
						if (EvolvioMod.main.keyCode == EvolvioMod.main.UP)
							me.accelerate(0.04, timeStep * OBJECT_TIMESTEPS_PER_YEAR);
						if (EvolvioMod.main.keyCode == EvolvioMod.main.DOWN)
							me.accelerate(-0.04, timeStep * OBJECT_TIMESTEPS_PER_YEAR);
						if (EvolvioMod.main.keyCode == EvolvioMod.main.LEFT)
							me.turn(-0.1, timeStep * OBJECT_TIMESTEPS_PER_YEAR);
						if (EvolvioMod.main.keyCode == EvolvioMod.main.RIGHT)
							me.turn(0.1, timeStep * OBJECT_TIMESTEPS_PER_YEAR);
					} else {
						if (EvolvioMod.main.key == ' ')
							me.eat(0.1, timeStep * OBJECT_TIMESTEPS_PER_YEAR);
						if (EvolvioMod.main.key == 'v')
							me.eat(-0.1, timeStep * OBJECT_TIMESTEPS_PER_YEAR);
						if (EvolvioMod.main.key == 'f')
							me.fight(0.5, timeStep * OBJECT_TIMESTEPS_PER_YEAR);
						if (EvolvioMod.main.key == 'u')
							me.setHue(me.hue + 0.02);
						if (EvolvioMod.main.key == 'j')
							me.setHue(me.hue - 0.02);

//						if (EvolvioMod.main.key == 'i')
//							me.setMouthHue(me.secondaryHue + 0.02);
//						if (EvolvioMod.main.key == 'k')
//							me.setMouthHue(me.secondaryHue - 0.02);
						if (EvolvioMod.main.key == 'b') {
							if (!wasPressingB) {
								me.reproduce(MANUAL_BIRTH_SIZE, timeStep);
							}
							wasPressingB = true;
						} else {
							wasPressingB = false;
						}
						
						for(CreatureAction a : me.actions) {
							a.userDoAction(EvolvioMod.main.key, me, this, timeStep);
						}
					}
				}
			} else {
				me.useBrain(timeStep, !userControl);
			}
			
			if (me.getRadius() < MINIMUM_SURVIVABLE_SIZE) {
				me.returnToEarth();
				creatures.remove(me);
				i--;
			}
		}
		finishIterate(timeStep);
	}

	public void finishIterate(double timeStep) {
		for (int i = 0; i < rocks.size(); i++) {
			rocks.get(i).applyMotions(timeStep * OBJECT_TIMESTEPS_PER_YEAR);
		}
		for (int i = 0; i < creatures.size(); i++) {
			creatures.get(i).applyMotions(timeStep * OBJECT_TIMESTEPS_PER_YEAR);
			//creatures.get(i).see(timeStep * OBJECT_TIMESTEPS_PER_YEAR);
		}
		if (Math.floor(fileSaveTimes[1] / imageSaveInterval) != Math.floor(year / imageSaveInterval)) {
			prepareForFileSave(1);
		}
		if (Math.floor(fileSaveTimes[3] / textSaveInterval) != Math.floor(year / textSaveInterval)) {
			prepareForFileSave(3);
		}
	}

	double getGrowthRate(double theTime) {
		double temperatureRange = MAX_TEMPERATURE - MIN_TEMPERATURE;
		return MIN_TEMPERATURE + temperatureRange * 0.5 - temperatureRange * 0.5 * Math.cos(theTime * 2 * Math.PI);
	}

	public double getGrowthOverTimeRange(double startTime, double endTime) {
		double temperatureRange = MAX_TEMPERATURE - MIN_TEMPERATURE;
		double m = MIN_TEMPERATURE + temperatureRange * 0.5;
		return (endTime - startTime) * m + (temperatureRange / Math.PI / 4.0)
				* (Math.sin(2 * Math.PI * startTime) - Math.sin(2 * Math.PI * endTime));
	}

	double getSeason() {
		return (year % 1.0);
	}

	void drawThermometer(float x1, float y1, float w, float h, double prog, double min, double max, int fillColor) {
		EvolvioMod.main.noStroke();
		EvolvioMod.main.fill(0, 0, 0.2f);
		EvolvioMod.main.rect(x1, y1, w, h);
		EvolvioMod.main.fill(fillColor);
		double proportionFilled = (prog - min) / (max - min);
		EvolvioMod.main.rect(x1, (float) (y1 + h * (1 - proportionFilled)), w, (float) (proportionFilled * h));

		double zeroHeight = (0 - min) / (max - min);
		double zeroLineY = y1 + h * (1 - zeroHeight);
		EvolvioMod.main.textAlign(EvolvioMod.main.RIGHT);
		EvolvioMod.main.stroke(0, 0, 1);
		EvolvioMod.main.strokeWeight(3);
		EvolvioMod.main.line(x1, (float) (zeroLineY), x1 + w, (float) (zeroLineY));
		double minY = y1 + h * (1 - (MIN_TEMPERATURE - min) / (max - min));
		double maxY = y1 + h * (1 - (MAX_TEMPERATURE - min) / (max - min));
		EvolvioMod.main.fill(0, 0, 0.8f);
		EvolvioMod.main.line(x1, (float) (minY), x1 + w * 1.8f, (float) (minY));
		EvolvioMod.main.line(x1, (float) (maxY), x1 + w * 1.8f, (float) (maxY));
		EvolvioMod.main.line(x1 + w * 1.8f, (float) (minY), x1 + w * 1.8f, (float) (maxY));

		EvolvioMod.main.fill(0, 0, 1);
		EvolvioMod.main.text("Zero", x1 - 5, (float) (zeroLineY + 8));
		EvolvioMod.main.text(EvolvioMod.main.nf(MIN_TEMPERATURE, 0, 2), x1 - 5, (float) (minY + 8));
		EvolvioMod.main.text(EvolvioMod.main.nf(MAX_TEMPERATURE, 0, 2), x1 - 5, (float) (maxY + 8));
	}

	void drawVerticalSlider(float x1, float y1, float w, float h, double prog, int fillColor, int antiColor) {
		EvolvioMod.main.noStroke();
		EvolvioMod.main.fill(0, 0, 0.2f);
		EvolvioMod.main.rect(x1, y1, w, h);
		if (prog >= 0) {
			EvolvioMod.main.fill(fillColor);
		} else {
			EvolvioMod.main.fill(antiColor);
		}
		EvolvioMod.main.rect(x1, (float) (y1 + h * (1 - prog)), w, (float) (prog * h));
	}

	boolean setMinTemperature(float temp) {
		MIN_TEMPERATURE = tempBounds(THERMOMETER_MIN + temp * (THERMOMETER_MAX - THERMOMETER_MIN));
		if (MIN_TEMPERATURE > MAX_TEMPERATURE) {
			float placeHolder = MAX_TEMPERATURE;
			MAX_TEMPERATURE = MIN_TEMPERATURE;
			MIN_TEMPERATURE = placeHolder;
			return true;
		}
		return false;
	}

	boolean setMaxTemperature(float temp) {
		MAX_TEMPERATURE = tempBounds(THERMOMETER_MIN + temp * (THERMOMETER_MAX - THERMOMETER_MIN));
		if (MIN_TEMPERATURE > MAX_TEMPERATURE) {
			float placeHolder = MAX_TEMPERATURE;
			MAX_TEMPERATURE = MIN_TEMPERATURE;
			MIN_TEMPERATURE = placeHolder;
			return true;
		}
		return false;
	}

	float tempBounds(float temp) {
		return Math.min(Math.max(temp, THERMOMETER_MIN), THERMOMETER_MAX);
	}

	float getHighTempProportion() {
		return (MAX_TEMPERATURE - THERMOMETER_MIN) / (THERMOMETER_MAX - THERMOMETER_MIN);
	}

	float getLowTempProportion() {
		return (MIN_TEMPERATURE - THERMOMETER_MIN) / (THERMOMETER_MAX - THERMOMETER_MIN);
	}

	String toDate(double d) {
		return "Year " + EvolvioMod.main.nf((float) (d), 0, 2);
	}

	String toAge(double d) {
		return EvolvioMod.main.nf((float) (year - d), 0, 2) + " yrs old";
	}

	void maintainCreatureMinimum(boolean choosePreexisting) {
		while (creatures.size() < creatureMinimum) {
			if (choosePreexisting) {
				Creature c = getRandomCreature();
				c.addEnergy(c.SAFE_SIZE);
				c.reproduce(c.SAFE_SIZE, timeStep);
			} else {
				Creature c =
						(new Creature(EvolvioMod.main.random(0, boardWidth), EvolvioMod.main.random(0, boardHeight),
								0, 0, EvolvioMod.main.random(MIN_CREATURE_ENERGY, MAX_CREATURE_ENERGY), 1,
								EvolvioMod.main.random(0, 1), 1, 1, this, year,
								EvolvioMod.main.random(0, 2 * EvolvioMod.main.PI), 0, "", "[PRIMORDIAL]", true, null,
								null, 1, EvolvioMod.main.random(0, 1)));
				addCreature(c);
			}
		}
	}

	Creature getRandomCreature() {
		int index = (int) (EvolvioMod.main.random(0, creatures.size()));
		return creatures.get(index);
	}

	double getRandomSize() {
		return Math.pow(EvolvioMod.main.random(MIN_ROCK_ENERGY_BASE, MAX_ROCK_ENERGY_BASE), 4);
	}

	void drawCreature(Creature c, float x, float y, float scale, float scaleUp) {
		EvolvioMod.main.pushMatrix();
		float scaleIconUp = scaleUp * scale;
		EvolvioMod.main.translate((float) (-c.px * scaleIconUp), (float) (-c.py * scaleIconUp));
		EvolvioMod.main.translate(x, y);
		c.drawSoftBody(scaleIconUp, 40.0f / scale, false);
		EvolvioMod.main.popMatrix();
	}

	void prepareForFileSave(int type) {
		fileSaveTimes[type] = -999999;
	}

	void fileSave() {
		for (int i = 0; i < 4; i++) {
			if (fileSaveTimes[i] < -99999) {
				fileSaveTimes[i] = year;
				if (i < 2) {
					EvolvioMod.main.saveFrame(getNextFileName(i));
				} else {
					String[] data = this.toBigString();
					EvolvioMod.main.saveStrings(getNextFileName(i), data);
				}
				fileSaveCounts[i]++;
			}
		}
	}

	public String[] toBigString() { // Convert current evolvio board into string. Does not work
		String[] placeholder = { "This feature", "Not yet implemented",
				"see line ~544 in Board's toBigString() function" };
		return placeholder;
	}

	public void unselect() {
		selectedCreature = null;
	}
	
	public float getHeight() {
		return boardHeight;
	}

	public void addCreature(Creature c) {
		creatures.add(c);
		//creatureQuadTree.addRectangleObject(new CreatureAreaBox(c));
	}
	
	public double getYear() { return year; }
	public double getTime() { return getYear(); }

	public List<SoftBody> getSoftBodiesInArea(double x, double y, double w, double h) {
		List<RectangleObject> queryResults = this.creatureQuadTree.search(new AbstractRectangleObject(x, y, w, h) {});
		
		List<SoftBody> results = new ArrayList<>();
		for(RectangleObject r : queryResults) {
			results.add(((SoftBodyRectangleObject)r).reference);
		}
		
		return results;
	} 
}
