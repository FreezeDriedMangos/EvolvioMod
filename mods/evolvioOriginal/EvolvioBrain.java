package evolvioOriginal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import core.Board;
import core.Creature;
import core.EvolvioMod;
import core.modAPI.Brain;
import processing.core.PFont;

public class EvolvioBrain implements Brain {
	final int BRAIN_WIDTH = 3;
//	final int BRAIN_HEIGHT = 13;
	final double AXON_START_MUTABILITY = 0.0005;
	final int MIN_NAME_LENGTH = 3;
	final int MAX_NAME_LENGTH = 10;
	final float BRIGHTNESS_THRESHOLD = 0.7f;
	final double STARTING_AXON_VARIABILITY = 1.0;
	final int MEMORY_COUNT = 5;
	
	private int brainHeight = 0;// = 13;
	private int brainWidth = BRAIN_WIDTH;
	
	float preferredRank = 8;
	double[] visionAngles = { 0, -0.4, 0.4 };
	double[] visionDistances = { 0, 0.7, 0.7 };
	// double visionAngle;
	// double visionDistance;
	double[] visionOccludedX = new double[visionAngles.length];
	double[] visionOccludedY = new double[visionAngles.length];
	double visionResults[] = new double[9];
	double[] memories;

	
	Axon[][][] axons;
	double[][] neurons;
	
	HashMap<String, Integer> outputIndecies = new HashMap<>();
	List<String> outputs = null;
	List<String> inputs = null;
	
	StackTraceElement[] whereDidIComeFrom; // TODO: remove this debug field
	
	public EvolvioBrain() {
		whereDidIComeFrom = Thread.currentThread().getStackTrace();
	}
	
	@Override
	public void init(Creature c, Board b, List<String> inputsRequired, List<String> outputsRequired) {
		//this.BRAIN_HEIGHT = Math.max(/*inputsRequired.size()*/BRAIN_HEIGHT+inputsRequired.size(), outputsRequired.size());
		if(brainHeight != 0) {
			System.err.println("Attempted initialization on an offspring brain");
			return;
		}
		
		for(int i = 0; i < outputsRequired.size(); i++) {
			outputIndecies.put(outputsRequired.get(i), i);
		}
		
		inputs = inputsRequired;
		outputs = outputsRequired;
		brainHeight = Math.max(inputs.size(), outputs.size()) + MEMORY_COUNT+1; // 1 is for the constant and 1 is for the size
		
		axons = new Axon[brainWidth - 1][brainHeight][brainHeight - 1];
		neurons = new double[brainWidth][brainHeight];
		for (int x = 0; x < brainWidth - 1; x++) {
			for (int y = 0; y < brainHeight; y++) {
				for (int z = 0; z < brainHeight - 1; z++) {
					double startingWeight = 0;
					if (true || y == brainHeight - 1) {
						startingWeight = (Math.random() * 2 - 1) * STARTING_AXON_VARIABILITY;
					}
					axons[x][y][z] = new Axon(startingWeight, AXON_START_MUTABILITY);
				}
			}
		}
		neurons = new double[brainWidth][brainHeight];
		for (int x = 0; x < brainWidth; x++) {
			for (int y = 0; y < brainHeight; y++) {
				if (y == brainHeight - 1) {
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
			neurons[0][(brainHeight - MEMORY_COUNT) + i] = memories[i];
		}

		//neurons[0][neurons.length-1] = 1;
		
		for (int layer = 1; layer < brainWidth; layer++) {
			for (int i = 0; i < brainHeight - 1; i++) {
				double total = 0;
				for (int input = 0; input < brainHeight; input++) {
					total += neurons[layer - 1][input] * axons[layer - 1][input][i].weight;
				}
				if (layer == brainWidth - 1) {
					neurons[layer][i] = total;
				} else {
					neurons[layer][i] = sigmoid(total);
				}
			}
		}

		int end = brainWidth - 1;
		for (int i = 0; i < MEMORY_COUNT; i++) {
			memories[i] = neurons[end][(brainHeight-MEMORY_COUNT) + i];
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

		int end = brainWidth - 1;
		Integer index = outputIndecies.get(name);
		if(index == null) { 
			System.err.println("Requested brain output that does not exist: \"" + name + "\""); 
			for(StackTraceElement e : Thread.currentThread().getStackTrace()) {
				System.err.println("\t" + e);
			}
			
			return -100;
		}
		
		return neurons[end][index];
	}

	@Override
	public Brain getOffspring(List<Brain> parents, List<String> inputsRequired, List<String> outputsRequired) {
		System.out.println("inputs: " + inputsRequired);
		System.out.println("outputs: " + outputsRequired);
		
		brainHeight = Math.max(inputsRequired.size(), outputsRequired.size()) + MEMORY_COUNT+1; // 1 is for the constant and 1 is for the size
		System.out.println("brainHeight: " + brainHeight);
		System.out.println("brainWidth: " + brainWidth);
		
		System.out.println("memorycount: " + MEMORY_COUNT);
		
		
		int parentsTotal = parents.size();
		Axon[][][] newAxons = new Axon[brainWidth - 1][brainHeight][brainHeight - 1];
		double[][] newNeurons = new double[brainWidth][brainHeight];
		float randomParentRotation = EvolvioMod.main.random(0, 1);
		for (int x = 0; x < brainWidth - 1; x++) {
			for (int y = 0; y < brainHeight; y++) {
				for (int z = 0; z < brainHeight - 1; z++) {
					float axonAngle = (float) (Math.atan2((y + z) / 2.0 - brainHeight / 2.0,
							x - brainWidth / 2) / (2 * Math.PI) + Math.PI);
					Brain parentForAxon = parents.get((int) (((axonAngle + randomParentRotation) % 1.0) * parentsTotal));
					EvolvioBrain parentBrain = (EvolvioBrain) parentForAxon;
					newAxons[x][y][z] = parentBrain.axons[x][y][z].mutateAxon();
				}
			}
		}
		for (int x = 0; x < brainWidth; x++) {
			for (int y = 0; y < brainHeight; y++) {
				float axonAngle = (float) (Math.atan2(y - brainHeight / 2.0, x - brainWidth / 2)
						/ (2 * Math.PI) + Math.PI);
				Brain parentForAxon = parents.get((int) (((axonAngle + randomParentRotation) % 1.0) * parentsTotal));
				EvolvioBrain parentBrain = (EvolvioBrain) parentForAxon;
				newNeurons[x][y] = parentBrain.neurons[x][y];
			}
		}
		
		EvolvioBrain b = new EvolvioBrain();
		b.brainHeight = brainHeight;
		b.axons = newAxons;
		b.neurons = newNeurons;
		b.memories = new double[MEMORY_COUNT];
		
		for(int i = 0; i < outputsRequired.size(); i++) {
			b.outputIndecies.put(outputsRequired.get(i), i);
		}
		
		b.inputs = new ArrayList<>();
		b.inputs.addAll(inputsRequired);
		
		b.outputs = new ArrayList<>();
		b.outputs.addAll(outputsRequired);
		
		return b;
	}

	@Override
	public void draw(PFont font, float scaleUp, int mX, int mY) {
		EvolvioBrain brain = this;//(EvolvioBrain)b;
		
		final float neuronSize = 0.4f;
		final float backgroundX = (-1.7f - neuronSize) * scaleUp;
		final float backgroundY = -neuronSize * scaleUp;
		final float backgroundHeight = (brainHeight + neuronSize * 2) * scaleUp;
		final float backgroundWidth = (2.4f + brainWidth + neuronSize * 2) * scaleUp;
		EvolvioMod.main.noStroke();
		EvolvioMod.main.fill(0, 0, 0.4f);
		EvolvioMod.main.rect(backgroundX, backgroundY,backgroundWidth, backgroundHeight);

		
		EvolvioMod.main.ellipseMode(EvolvioMod.main.RADIUS);
		EvolvioMod.main.strokeWeight(2);
		EvolvioMod.main.textFont(font, 0.58f * scaleUp);
		EvolvioMod.main.fill(0, 0, 1);
		//String[] inputLabels = { "0Hue", "0Sat", "0Bri", "1Hue", "1Sat", "1Bri", "2Hue", "2Sat", "2Bri", "Size", "MHue",
		//		"Mem", "Const." };
		
		// input labels
		String[] inputLabels = new String[brain.brainHeight]; 
		int placeholderCount = Math.max(0, brain.outputs.size() - brain.inputs.size());
		
		for(int i = 0; i < brain.inputs.size(); i++) {
			inputLabels[i] = brain.inputs.get(i);
		}
		for(int i = 0; i < placeholderCount; i++) {
			inputLabels[brain.inputs.size() + i] = getPlaceholderLabel(i);
		}
		for (int i = 0; i < MEMORY_COUNT; i++) {
			inputLabels[/* brain.inputs.size() + placeholderCount + i*/ brainHeight-MEMORY_COUNT-1 + i] = "Mem";
		}
		inputLabels[inputLabels.length-1] = "Const.";
		
		// output labels
		
		String[] outputLabels = new String[brain.brainHeight]; 
		placeholderCount = Math.max(0, brain.inputs.size() - brain.outputs.size());
		
		for(int i = 0; i < brain.outputs.size(); i++) {
			outputLabels[i] = brain.outputs.get(i);
		}
		for(int i = 0; i < placeholderCount; i++) {
			outputLabels[brain.outputs.size() + i] = getPlaceholderLabel(i);
		}
		for (int i = 0; i < MEMORY_COUNT; i++) {
			outputLabels[/*brain.outputs.size() + placeholderCount + i*/  brainHeight-MEMORY_COUNT-1 + i] = "Mem";
		}
		outputLabels[outputLabels.length-1] = "Const.";
		
//		String[] outputLabels = { "BHue", "Accel.", "Turn", "Eat", "Fight", "Birth", "MHue", "How funny?", "How popular?",
//				"How generous?", "How smart?", "Mem", "Const." };
		
		// draw the labels
		
		int specialNodeTextColor = EvolvioMod.main.color(0.8f, 0.5f, 1);
		int unusedNodeTextColor = EvolvioMod.main.color(1f, 0.5f, 1f);
		int nodeTextColor = EvolvioMod.main.color(0.5f, 0.5f, 1);
		
		final float textHeight = EvolvioMod.main.textAscent() + EvolvioMod.main.textDescent();
		EvolvioMod.main.fill(1);
		EvolvioMod.main.text("Key:",backgroundX, backgroundY + backgroundHeight + textHeight);
		EvolvioMod.main.fill(nodeTextColor);
		EvolvioMod.main.text("Given input / output",backgroundX, backgroundY + backgroundHeight + 2*textHeight);
		EvolvioMod.main.fill(unusedNodeTextColor);
		EvolvioMod.main.text("Unused placeholder node",backgroundX, backgroundY + backgroundHeight + 3*textHeight);
		EvolvioMod.main.fill(specialNodeTextColor);
		EvolvioMod.main.text("Special locally-defined node",backgroundX, backgroundY + backgroundHeight + 4*textHeight);
		
		//EvolvioMod.main.text("Given input / output", (-neuronSize - 0.1f) * scaleUp, (y + (neuronSize * 0.6f)) * scaleUp);
		
		
		for (int y = 0; y < brain.brainHeight; y++) {
			if(y >= brain.inputs.size() && y < inputLabels.length-MEMORY_COUNT-1) {EvolvioMod.main.fill(unusedNodeTextColor);}
			else if(y >= brain.inputs.size()) {EvolvioMod.main.fill(specialNodeTextColor);}
			else 	         			                                           {EvolvioMod.main.fill(nodeTextColor);}
			
			EvolvioMod.main.textAlign(EvolvioMod.main.RIGHT);
			EvolvioMod.main.text(inputLabels[y], (-neuronSize - 0.1f) * scaleUp, (y + (neuronSize * 0.6f)) * scaleUp);
			
			if(y >= brain.outputs.size() && y < outputLabels.length-MEMORY_COUNT-1) {EvolvioMod.main.fill(unusedNodeTextColor);}
			else if(y >= brain.inputs.size()) {EvolvioMod.main.fill(specialNodeTextColor);}
			else 				          {EvolvioMod.main.fill(nodeTextColor);}
			
			EvolvioMod.main.textAlign(EvolvioMod.main.LEFT);
			EvolvioMod.main.text(outputLabels[y], (brainWidth - 1 + neuronSize + 0.1f) * scaleUp,
					(y + (neuronSize * 0.6f)) * scaleUp);
		}
		EvolvioMod.main.textAlign(EvolvioMod.main.CENTER);
		for (int x = 0; x < brain.brainWidth; x++) {
			for (int y = 0; y < brain.brainHeight; y++) {
				EvolvioMod.main.noStroke();
				double val = brain.neurons[x][y];
				EvolvioMod.main.fill(neuronFillColor(val));
				EvolvioMod.main.ellipse(x * scaleUp, y * scaleUp, neuronSize * scaleUp, neuronSize * scaleUp);
				EvolvioMod.main.fill(brain.neuronTextColor(val));
				EvolvioMod.main.text(EvolvioMod.main.nf((float) val, 0, 1), x * scaleUp,
						(y + (neuronSize * 0.6f)) * scaleUp);
			}
		}
		if (mX >= 0 && mX < brain.brainWidth && mY >= 0 && mY < brain.brainHeight) {
			for (int y = 0; y < brain.brainHeight; y++) {
				if (mX >= 1 && mY < brain.brainHeight - 1) {
					brain.drawAxon(mX - 1, y, mX, mY, scaleUp);
				}
				if (mX < brain.brainWidth - 1 && y < brain.brainHeight - 1) {
					brain.drawAxon(mX, mY, mX + 1, y, scaleUp);
				}
			}
		}
	}

	private String getPlaceholderLabel(int i) {
		switch(i) {
			case 0: return "How funny?";
			case 1: return "How creative?";
			case 2: return "How considerate?";
			case 3: return "How honest?";
			default: return "How unoriginal?";
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
	
	@Override
	public String makeString() {
		StringBuilder s = new StringBuilder();
		
		if(brainHeight == 0) {
			System.err.println("Failed to initialize brain that was constructed at:");
			for(StackTraceElement e : this.whereDidIComeFrom) {
				System.err.println("\t" + e);
			}
		}
		
		s.append("Axons:"); /* System.err.println("brain height " + brainHeight); */
		for (int x = 0; x < brainWidth - 1; x++) {
			s.append("\nLayer " + x + "\n");
			for (int y = 0; y < brainHeight; y++) {
				for (int z = 0; z < brainHeight - 1; z++) {
					s.append(axons[x][y][z] + "\t");
				}
				s.append("\n");
			}
		}
		
		s.append("\n");
		s.append("Memories:\n");
		
		for (int i = 0; i < MEMORY_COUNT; i++) {
			s.append(i + ": " + memories[i] + "\n");
		}
		
		return s.toString();
	}

	@Override
	public Brain fromString(String s) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean canMate(List<Brain> parents) {
		return true; // there's no speciation in this lawless land
	}
}
