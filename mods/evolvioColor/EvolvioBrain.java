package evolvioColor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import core.Board;
import core.Creature;
import core.EvolvioMod;
import core.SoftBody;
import core.modAPI.Brain;
import core.modAPI.BrainDrawer;
import processing.core.PFont;

public class EvolvioBrain implements Brain, BrainDrawer {
	final int BRAIN_WIDTH = 3;
//	final int BRAIN_HEIGHT = 13;
	final double AXON_START_MUTABILITY = 0.0005;
	final int MIN_NAME_LENGTH = 3;
	final int MAX_NAME_LENGTH = 10;
	final float BRIGHTNESS_THRESHOLD = 0.7f;
	final double STARTING_AXON_VARIABILITY = 1.0;
	int BRAIN_HEIGHT = 13;
	
	float preferredRank = 8;
	double[] visionAngles = { 0, -0.4, 0.4 };
	double[] visionDistances = { 0, 0.7, 0.7 };
	// double visionAngle;
	// double visionDistance;
	double[] visionOccludedX = new double[visionAngles.length];
	double[] visionOccludedY = new double[visionAngles.length];
	double visionResults[] = new double[9];
	int MEMORY_COUNT = 1;
	double[] memories;

	
	Axon[][][] axons;
	double[][] neurons;
	
	HashMap<String, Integer> outputIndecies = new HashMap<>();
	List<String> inputs = null;
	
	@Override
	public void init(Creature c, Board b, List<String> inputsRequired, List<String> outputsRequired) {
		//this.BRAIN_HEIGHT = Math.max(/*inputsRequired.size()*/BRAIN_HEIGHT+inputsRequired.size(), outputsRequired.size());
		for(int i = 0; i < outputsRequired.size(); i++) {
			outputIndecies.put(outputsRequired.get(i), i);
		}
		
		inputs = inputsRequired;
		BRAIN_HEIGHT = inputs.size() + MEMORY_COUNT+1; // 1 is for the constant and 1 is for the size
		
		axons = new Axon[BRAIN_WIDTH - 1][BRAIN_HEIGHT][BRAIN_HEIGHT - 1];
		neurons = new double[BRAIN_WIDTH][BRAIN_HEIGHT];
		for (int x = 0; x < BRAIN_WIDTH - 1; x++) {
			for (int y = 0; y < BRAIN_HEIGHT; y++) {
				for (int z = 0; z < BRAIN_HEIGHT - 1; z++) {
					double startingWeight = 0;
					if (y == BRAIN_HEIGHT - 1) {
						startingWeight = (Math.random() * 2 - 1) * STARTING_AXON_VARIABILITY;
					}
					axons[x][y][z] = new Axon(startingWeight, AXON_START_MUTABILITY);
				}
			}
		}
		neurons = new double[BRAIN_WIDTH][BRAIN_HEIGHT];
		for (int x = 0; x < BRAIN_WIDTH; x++) {
			for (int y = 0; y < BRAIN_HEIGHT; y++) {
				if (y == BRAIN_HEIGHT - 1) {
					neurons[x][y] = 1;
				} else {
					neurons[x][y] = 0;
				}
			}
		}
		
		memories = new double[MEMORY_COUNT];
	}

	//pc = peripheral-related change
	
	@Override
	public void think(Creature c, Map<String, Double> peripheralInputs, Board b, double timeStep) {
		// TODO Auto-generated method stub
		//see(c, b, timeStep); // pc
		
//		for (int i = 0; i < 9; i++) {
//			neurons[0][i] = visionResults[i];
//		}
//		neurons[0][9] = c.getEnergyLevel();
//		neurons[0][10] = c.secondaryHue;
		for (int i = 0; i < inputs.size(); i++) {
			neurons[0][i] = peripheralInputs.get(inputs.get(i));
		}
		
		for (int i = 0; i < MEMORY_COUNT; i++) {
			neurons[0][inputs.size() + i] = memories[i];
		}

		//neurons[0][neurons.length-1] = 1;
		
		for (int x = 1; x < BRAIN_WIDTH; x++) {
			for (int y = 0; y < BRAIN_HEIGHT - 1; y++) {
				double total = 0;
				for (int input = 0; input < BRAIN_HEIGHT; input++) {
					total += neurons[x - 1][input] * axons[x - 1][input][y].weight;
				}
				if (x == BRAIN_WIDTH - 1) {
					neurons[x][y] = total;
				} else {
					neurons[x][y] = sigmoid(total);
				}
			}
		}

		int end = BRAIN_WIDTH - 1;
		for (int i = 0; i < MEMORY_COUNT; i++) {
			memories[i] = neurons[end][11 + i];
		}
	}

	public double sigmoid(double input) {
		return 1.0 / (1.0 + Math.pow(2.71828182846, -input));
	}
	
	// pc - all of the below functions being commented out
//	public void see(Creature creature, Board board, double timeStep) {
//		for (int k = 0; k < visionAngles.length; k++) {
//			double visionStartX = creature.px;
//			double visionStartY = creature.py;
//			double visionTotalAngle = creature.rotation + visionAngles[k];
//
//			double endX = creature.getVisionEndX(k);
//			double endY = creature.getVisionEndY(k);
//
//			visionOccludedX[k] = endX;
//			visionOccludedY[k] = endY;
//			int c = creature.getColorAt(endX, endY);
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
//					addPVOs(tileX, tileY, creature, board, potentialVisionOccluders);
//					if (prevTileX >= 0 && tileX != prevTileX && tileY != prevTileY) {
//						addPVOs(prevTileX, tileY, creature, board, potentialVisionOccluders);
//						addPVOs(tileX, prevTileY, creature, board, potentialVisionOccluders);
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
//				double x = body.px - creature.px;
//				double y = body.py - creature.py;
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
//	public void addPVOs(int x, int y, Creature creature, Board board, ArrayList<SoftBody> PVOs) {
//		if (x >= 0 && x < board.boardWidth && y >= 0 && y < board.boardHeight) {
//			for (int i = 0; i < board.softBodiesInPositions[x][y].size(); i++) {
//				SoftBody newCollider = (SoftBody) board.softBodiesInPositions[x][y].get(i);
//				if (!PVOs.contains(newCollider) && newCollider != creature) {
//					PVOs.add(newCollider);
//				}
//			}
//		}
//	}
//	
//	public double distance(double x1, double y1, double x2, double y2) {
//		return (Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1)));
//	}

	@Override
	public double getOutput(String name) {

		int end = BRAIN_WIDTH - 1;
		Integer index = outputIndecies.get(name);
		if(index == null) { 
			System.err.println("Requested brain output that does not exist: \"" + name + "\""); 
			for(StackTraceElement e : Thread.currentThread().getStackTrace()) {
				System.err.println("\t" + e);
			}
			
			return -100;
		}
		
		return neurons[end][index];
		
//		switch(name) {
//			case "hue":          return neurons[end][0];
//			case "accelerate":   return neurons[end][1];
//			case "turn":         return neurons[end][2];
//			case "eat":          return neurons[end][3];
//			case "fight":        return neurons[end][4];
//			case "reproduce":    return neurons[end][5];
//			case "secondaryHue": return neurons[end][10];
//		}
//		
//		return 0;
	}

	@Override
	public Brain getOffspring(List<Creature> parents, List<String> inputsRequired, List<String> outputsRequired) {
		int parentsTotal = parents.size();
		Axon[][][] newBrain = new Axon[BRAIN_WIDTH - 1][BRAIN_HEIGHT][BRAIN_HEIGHT - 1];
		double[][] newNeurons = new double[BRAIN_WIDTH][BRAIN_HEIGHT];
		float randomParentRotation = EvolvioMod.main.random(0, 1);
		for (int x = 0; x < BRAIN_WIDTH - 1; x++) {
			for (int y = 0; y < BRAIN_HEIGHT; y++) {
				for (int z = 0; z < BRAIN_HEIGHT - 1; z++) {
					float axonAngle = (float) (Math.atan2((y + z) / 2.0 - BRAIN_HEIGHT / 2.0,
							x - BRAIN_WIDTH / 2) / (2 * Math.PI) + Math.PI);
					Creature parentForAxon = parents.get((int) (((axonAngle + randomParentRotation) % 1.0) * parentsTotal));
					EvolvioBrain parentBrain = (EvolvioBrain) parentForAxon.getBrain();
					newBrain[x][y][z] = parentBrain.axons[x][y][z].mutateAxon();
				}
			}
		}
		for (int x = 0; x < BRAIN_WIDTH; x++) {
			for (int y = 0; y < BRAIN_HEIGHT; y++) {
				float axonAngle = (float) (Math.atan2(y - BRAIN_HEIGHT / 2.0, x - BRAIN_WIDTH / 2)
						/ (2 * Math.PI) + Math.PI);
				Creature parentForAxon = parents.get((int) (((axonAngle + randomParentRotation) % 1.0) * parentsTotal));
				EvolvioBrain parentBrain = (EvolvioBrain) parentForAxon.getBrain();
				newNeurons[x][y] = parentBrain.neurons[x][y];
			}
		}
		
		EvolvioBrain b = new EvolvioBrain();
		b.axons = newBrain;
		b.neurons = newNeurons;
		b.memories = new double[MEMORY_COUNT];
		
		for(int i = 0; i < outputsRequired.size(); i++) {
			b.outputIndecies.put(outputsRequired.get(i), i);
		}
		
		b.inputs = new ArrayList<>();
		b.inputs.addAll(inputsRequired);
		
		return b;
	}

	@Override
	public void drawBrain(Brain b, PFont font, float scaleUp, int mX, int mY) {
		EvolvioBrain brain = (EvolvioBrain)b;
		
		final float neuronSize = 0.4f;
		EvolvioMod.main.noStroke();
		EvolvioMod.main.fill(0, 0, 0.4f);
		EvolvioMod.main.rect((-1.7f - neuronSize) * scaleUp, -neuronSize * scaleUp,
				(2.4f + BRAIN_WIDTH + neuronSize * 2) * scaleUp, (BRAIN_HEIGHT + neuronSize * 2) * scaleUp);

		EvolvioMod.main.ellipseMode(EvolvioMod.main.RADIUS);
		EvolvioMod.main.strokeWeight(2);
		EvolvioMod.main.textFont(font, 0.58f * scaleUp);
		EvolvioMod.main.fill(0, 0, 1);
		//String[] inputLabels = { "0Hue", "0Sat", "0Bri", "1Hue", "1Sat", "1Bri", "2Hue", "2Sat", "2Bri", "Size", "MHue",
		//		"Mem", "Const." };
		String[] inputLabels = new String[brain.BRAIN_HEIGHT]; 
		for(int i = 0; i < brain.inputs.size(); i++) {
			inputLabels[i] = brain.inputs.get(i);
		}
		
		for (int i = 0; i < MEMORY_COUNT; i++) {
			inputLabels[brain.inputs.size() + i] = "Mem";
		}
		inputLabels[inputLabels.length-1] = "Const.";
		
		String[] outputLabels = { "BHue", "Accel.", "Turn", "Eat", "Fight", "Birth", "MHue", "How funny?", "How popular?",
				"How generous?", "How smart?", "Mem", "Const." };
		
		// TODO: make this work off of the stuff passed in /\
		
		for (int y = 0; y < brain.BRAIN_HEIGHT; y++) {
			EvolvioMod.main.textAlign(EvolvioMod.main.RIGHT);
			EvolvioMod.main.text(inputLabels[y], (-neuronSize - 0.1f) * scaleUp, (y + (neuronSize * 0.6f)) * scaleUp);
			EvolvioMod.main.textAlign(EvolvioMod.main.LEFT);
			EvolvioMod.main.text(outputLabels[y], (BRAIN_WIDTH - 1 + neuronSize + 0.1f) * scaleUp,
					(y + (neuronSize * 0.6f)) * scaleUp);
		}
		EvolvioMod.main.textAlign(EvolvioMod.main.CENTER);
		for (int x = 0; x < brain.BRAIN_WIDTH; x++) {
			for (int y = 0; y < brain.BRAIN_HEIGHT; y++) {
				EvolvioMod.main.noStroke();
				double val = brain.neurons[x][y];
				EvolvioMod.main.fill(neuronFillColor(val));
				EvolvioMod.main.ellipse(x * scaleUp, y * scaleUp, neuronSize * scaleUp, neuronSize * scaleUp);
				EvolvioMod.main.fill(brain.neuronTextColor(val));
				EvolvioMod.main.text(EvolvioMod.main.nf((float) val, 0, 1), x * scaleUp,
						(y + (neuronSize * 0.6f)) * scaleUp);
			}
		}
		if (mX >= 0 && mX < brain.BRAIN_WIDTH && mY >= 0 && mY < brain.BRAIN_HEIGHT) {
			for (int y = 0; y < brain.BRAIN_HEIGHT; y++) {
				if (mX >= 1 && mY < brain.BRAIN_HEIGHT - 1) {
					brain.drawAxon(mX - 1, y, mX, mY, scaleUp);
				}
				if (mX < brain.BRAIN_WIDTH - 1 && y < brain.BRAIN_HEIGHT - 1) {
					brain.drawAxon(mX, mY, mX + 1, y, scaleUp);
				}
			}
		}
	}

	public void drawAxon(int x1, int y1, int x2, int y2, float scaleUp) {
		EvolvioMod.main.stroke(neuronFillColor(axons[x1][y1][y2].weight * neurons[x1][y1]));

		EvolvioMod.main.line(x1 * scaleUp, y1 * scaleUp, x2 * scaleUp, y2 * scaleUp);
	}
	public int neuronFillColor(double d) {
		if (d >= 0) {
			return EvolvioMod.main.color(0, 0, 1, (float) (d));
		} else {
			return EvolvioMod.main.color(0, 0, 0, (float) (-d));
		}
	}

	public int neuronTextColor(double d) {
		if (d >= 0) {
			return EvolvioMod.main.color(0, 0, 0);
		} else {
			return EvolvioMod.main.color(0, 0, 1);
		}
	}

	//@Override
	//public void useOutput(Creature creature, Board board, double timeStep) {
	//	double val = Math.min(Math.max(getOutput("secondaryHue"), 0), 1);
	//	((MouthHue)creature.getAttribute("mouthHue")).setValue(val);
	//}
}
