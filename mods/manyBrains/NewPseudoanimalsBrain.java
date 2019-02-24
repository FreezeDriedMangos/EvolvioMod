package manyBrains;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import core.Board;
import core.Creature;
import core.EvolvioMod;
import core.modAPI.Brain;
import processing.core.PFont;

public class NewPseudoanimalsBrain implements Brain {

	// axon weights have a genetically defined value, but can drift from that value (to a certain point) according to usage
	// ^ neural plasticity

	
	private static final double EXCITEMENT_TRANSFER_RATIO = 0.5;
	// valid on the range (0,0.5]
	private static final double PLASTICITY = 0.2; // the max amount axon weights are allowed to drift from their genetic value
	private static final int NUM_HIDDEN_NODES = 10;

	private static final double CURVE_POWER = 1.2;

	/**
	 * the array is structured like this:
	 * [input0, input1, input2 . . . inputN, hidden0, hidden1 . . . hiddenQ, outputK, . . . output2, output1, output0]
	 */
	double[] neurons;
	double[][] axonWeights;
	double[][] genetic_axonWeights;

	double plasticity = PLASTICITY; 
	double excitementTransferRatio = EXCITEMENT_TRANSFER_RATIO;
	double curvePower = CURVE_POWER;
	
	//I/O
	HashMap<String, Integer> outputIndecies = new HashMap<>();
	List<String> outputs = new ArrayList<>();
	List<String> inputs = new ArrayList<>();
	
	// drawing
	double[][] axonFlashes;
	
	@Override
	public void init(Creature c, Board b, List<String> inputsRequired, List<String> outputsRequired) {
		int numNeurons = inputsRequired.size() + outputsRequired.size() + NUM_HIDDEN_NODES;
		neurons = new double[numNeurons];
		
		axonWeights = new double[numNeurons][numNeurons]; 
		genetic_axonWeights = new double[numNeurons][numNeurons]; 
		
		for(int to = 0; to < axonWeights.length; to++) {
			for(int from = 0; from < axonWeights[to].length; from++) {
				axonWeights[to][from] = genetic_axonWeights[to][from] = (Math.random()* (1.0-2.0*plasticity)) + plasticity;
				System.out.println(axonWeights[to][from]);
			}
		}
		
		
		inputs.addAll(inputsRequired);
		outputs.addAll(outputsRequired);
		
		for(int i = 0; i < outputs.size(); i++) {
			outputIndecies.put(outputs.get(i), numNeurons-i-1);
		}
		
		axonFlashes = new double[numNeurons][numNeurons];
	}

	@Override
	public void think(Creature c, Map<String, Double> peripheralInputs, Board b, double timeStep) {
		// update input neurons
		for(int i = 0; i < inputs.size(); i++) {
			// inputs are on the range [-1, 1], but node values should be on the range [0,1]
			neurons[i] += (peripheralInputs.get(inputs.get(i))+1.0) / 2.0;
		}

		// think
		double[] delta = new double[neurons.length];
		
		// don't give to input nodes or take from output nodes
		for(int to = inputs.size(); to < axonWeights.length; to++) {
			for(int from = 0; from < axonWeights[to].length - outputs.size(); from++) {
				if(Math.random() < axonWeights[to][from]) {
					double val = excitementTransferRatio*(neurons[from]+delta[from]);
					
					delta[to] += val;
					delta[from] -= val;
					
					axonFlashes[to][from] = 1;
					
					strengthenAxon(from, to);
				} else {
					//weakenAxon(from, to);
				}
			}
		}
		
		for(int i = 0; i < neurons.length; i++) {
			neurons[i] += delta[i];
		}
	}

	// see this site for a visual on the below formulas https://www.desmos.com/calculator/sbjnaqgmk1
	private void strengthenAxon(int from, int to) {
		double genetic = genetic_axonWeights[to][from];
//		double old = axonWeights[to][from];
		
//		if(genetic == old) {
//			axonWeights[to][from] = old + PLASTICITY_KICKSTART;
//			return;
//		}
//		
//		axonWeights[to][from] = PLASTICITY * sigmoid_alt(PLASTICITY_RATE*(old-genetic)) + genetic;
		
		double x = axonWeights[to][from];
		double a = genetic + plasticity;
		double b = genetic - plasticity;
		double c = Math.pow(2*plasticity, 1/curvePower) / (2*plasticity);
//		double c = Math.sqrt(a-b) / (a-b);
		axonWeights[to][from] = a-Math.pow(c* (x-a), curvePower); //PLASTICITY * sigmoid_alt(old)+genetic;
	}
	private void weakenAxon(int from, int to) {
		double genetic = genetic_axonWeights[to][from];
//		double old = axonWeights[to][from];
//		
//		axonWeights[to][from] = PLASTICITY * sigmoid_alt(PLASTICITY_RATE*(old-genetic)-PLASTICITY_KICKSTART) + genetic;

		double x = axonWeights[to][from];
		double a = genetic + plasticity;
		double b = genetic - plasticity;
		double c = Math.pow(2*plasticity, 1/curvePower) / (2*plasticity);
//		double c = Math.sqrt(a-b) / (a-b);
		axonWeights[to][from] = b-Math.pow(c* (x-b), curvePower); 
	}

//	/** Sigmoid with a range of (-1, 1) */
//	private double sigmoid_alt(double x) {
//		return (2.0 / (1.0 - Math.pow(Math.E, -x))) - 1.0;
//	}

	
	@Override
	public double getOutput(String name) { // neuron values are on the range [0,1], but this function should return on the range [-1,1]
		return 2.0*(neurons[outputIndecies.get(name)]-0.5);
	}

	@Override
	public Brain getOffspring(List<Creature> parents, List<String> inputsRequired, List<String> outputsRequired) {
		NewPseudoanimalsBrain offspring = new NewPseudoanimalsBrain();
				
		List<NewPseudoanimalsBrain> parentBrains = new ArrayList<>();
		for(Creature parent : parents) {
			parentBrains.add((NewPseudoanimalsBrain)parent.getBrain());
		}
		
		int numNeurons = ((NewPseudoanimalsBrain)parents.get(0).getBrain()).neurons.length;
		neurons = new double[numNeurons];
		
		axonWeights = new double[numNeurons][numNeurons]; 
		genetic_axonWeights = new double[numNeurons][numNeurons]; 
		
		for(int to = 0; to < axonWeights.length; to++) {
			for(int from = 0; from < axonWeights[to].length; from++) {
				double geneticval = 0;
				double epigeneticVal = 0;
				
				for(NewPseudoanimalsBrain parent : parentBrains) {
					geneticval += parent.genetic_axonWeights[to][from] / parentBrains.size();
					epigeneticVal += (parent.axonWeights[to][from] - parent.genetic_axonWeights[to][from]) / parentBrains.size();
				}
				
				offspring.axonWeights[to][from] = geneticval + epigeneticVal;
			}
		}
		
		
		offspring.inputs.addAll(inputsRequired);
		offspring.outputs.addAll(outputsRequired);
		
		
		for(int i = 0; i < outputs.size(); i++) {
			offspring.outputIndecies.put(outputs.get(i), numNeurons-i-1);
		}
		
		offspring.axonFlashes = new double[numNeurons][numNeurons];
		
		return offspring;
	}

	@Override
	public void draw(PFont font, float scaleUp, int mX, int mY) {
		//EvolvioMod.main.fill(0);
		//EvolvioMod.main.text("Not implemented", 0, 0);

		final float neuronSize = 0.4f;
		final float radius = neurons.length * 2f*neuronSize / (float)(2f*Math.PI);
		final float deltaTheta = (float)(2f*Math.PI) / (float)neurons.length;
		
		final float xOffset = 1.1f;
		final float yOffset = 5;

		EvolvioMod.main.strokeWeight(3f);
		for(int i = 0; i < neurons.length; i++) {
			float x = (float) (radius * Math.cos(deltaTheta*i)) + xOffset;
			float y = (float) (radius * Math.sin(deltaTheta*i)) + yOffset;
			float x2 = (float) ((radius+1) * Math.cos(deltaTheta*i)) + xOffset;
			float y2 = (float) ((radius+1) * Math.sin(deltaTheta*i)) + yOffset;
			
			boolean isInput = i < inputs.size();
			boolean isOutput = i > neurons.length - outputs.size();
			if(isInput)       { EvolvioMod.main.stroke(EvolvioMod.main.color(1,    1,1)); }
			else if(isOutput) { EvolvioMod.main.stroke(EvolvioMod.main.color(0.5f, 1,1)); }
			else              { EvolvioMod.main.stroke(EvolvioMod.main.color(0.25f,1,1)); }
			
			EvolvioMod.main.line(x*scaleUp, y*scaleUp, x2*scaleUp, y2*scaleUp);

			EvolvioMod.main.noStroke();
			EvolvioMod.main.fill(neuronFillColor(neurons[i]));
			EvolvioMod.main.ellipse(x * scaleUp, y * scaleUp, neuronSize * scaleUp, neuronSize * scaleUp);
		}
		
		EvolvioMod.main.noStroke();
		for(int i = 0; i < neurons.length; i++) {
			float x = (float) (radius * Math.cos(deltaTheta*i)) + xOffset;
			float y = (float) (radius * Math.sin(deltaTheta*i)) + yOffset;
			
			EvolvioMod.main.fill(neuronFillColor(neurons[i]));
			EvolvioMod.main.ellipse(x * scaleUp, y * scaleUp, neuronSize * scaleUp, neuronSize * scaleUp);
		}
		
		EvolvioMod.main.strokeWeight(3f);
		for(int to = 0; to < axonWeights.length; to++) {
			float x2 = (float) (radius * Math.cos(deltaTheta*to)) + xOffset;
			float y2 = (float) (radius * Math.sin(deltaTheta*to)) + yOffset;
			
			for(int from = 0; from < axonWeights[to].length; from++) {
				double alpha = axonFlashes[to][from];
				
				// draw axon
				EvolvioMod.main.stroke(EvolvioMod.main.color(0f,0f,(float)alpha));
//				EvolvioMod.main.stroke(1);
				float x1 = (float) (radius * Math.cos(deltaTheta*from)) + xOffset;
				float y1 = (float) (radius * Math.sin(deltaTheta*from)) + yOffset;
				
				EvolvioMod.main.line(x1*scaleUp, y1*scaleUp, x2*scaleUp, y2*scaleUp);

				axonFlashes[to][from] /= 2.0;
			}
		}
	}
	/**
	 * sigmoid with limits at 0.2 and 0.8
	 * @param x
	 * @return
	 */
	private float neuronFillColor(double x) {
		return (float) (0.2 + 0.6*(1.0 / (1.0 + Math.pow(Math.E, -13*x+6.5))));
	}

	@Override
	public boolean canMate(List<Brain> parents) {
		return true;
	}

	@Override
	public Brain fromString(String s) {
		NewPseudoanimalsBrain revived = new NewPseudoanimalsBrain();
		String[] sections = s.split("&");
		
		// Constants
		String plasticityString = sections[0].split("=")[1];
		revived.plasticity = Double.parseDouble(plasticityString);
		
		String exString = sections[1].split("=")[1];
		revived.excitementTransferRatio = Double.parseDouble(exString);
		
		String curveString = sections[2].split("=")[1];
		revived.curvePower = Double.parseDouble(curveString);
		
		// I/O
		String numNeuronsString = sections[3].split("=")[1];
		int numNeurons = Integer.parseInt(numNeuronsString);
		
		revived.inputs = new ArrayList<>();
		for(String str : sections[4].split("=\n")[1].split("\t")) {
			revived.inputs.add(str);
		}
		
		revived.outputs = new ArrayList<>();
		for(String str : sections[5].split("=\n")[1].split("\t")) {
			revived.outputs.add(str);
		}
		
		for(int i = 0; i < outputs.size(); i++) {
			revived.outputIndecies.put(revived.outputs.get(i), numNeurons-i-1);
		}
		
		// neurons
		String[] vals = sections[6].split("=\n")[1].split("\t");
		revived.neurons = new double[numNeurons];
		for(int i = 0; i < numNeurons; i++) {
			revived.neurons[i] = Double.parseDouble(vals[i]);
		}
		
		// axons

		revived.axonWeights = new double[numNeurons][numNeurons]; 
		revived.genetic_axonWeights = new double[numNeurons][numNeurons]; 

		String[] rows = sections[7].split("=\n")[1].split(";");
		for(int to = 0; to < axonWeights.length; to++) {
			String[] values = rows[to].split("\t");
			for(int from = 0; from < axonWeights[to].length; from++) {
				String[] parts = values[from].split(" ");
				double genetic = Double.parseDouble(parts[0]);
				double delta = Double.parseDouble(parts[1]);
				
				revived.genetic_axonWeights[to][from] = genetic;
				revived.axonWeights        [to][from] = genetic+delta;
				
			}
		}
		
		// drawing stuff
		revived.axonFlashes = new double[numNeurons][numNeurons];
		
		return revived;
	}
	
	@Override
	public String makeString() {
		StringBuilder s = new StringBuilder();
		
		s.append("PLASTICITY=" + plasticity + "&");
		s.append("EXCITEMENT_TRANSFER_RATIO=" + excitementTransferRatio + "&");
		s.append("CURVE_POWER=" + curvePower + "&");
		s.append("Number of Neruons=" + neurons.length + "&");
		
		s.append("Inputs=\n");
		for(String i : this.inputs) {
			s.append(i + "\t");
		}
		s.append("&\n");
		
		s.append("Outputs=\n");
		for(String o : this.outputs) {
			s.append(o + "\t");
		}
		s.append("&\n");
		
		s.append("Neurons=\n");
		for(double d : this.neurons) {
			s.append(d + "\t");
		}
		s.append("&\n");
		
		s.append("Axons=\n");
		for(int to = 0; to < axonWeights.length; to++) {
			for(int from = 0; from < axonWeights[to].length; from++) {
				double delta = axonWeights[to][from] - genetic_axonWeights[to][from];
				
				s.append(genetic_axonWeights[to][from]);
				s.append(" " + (delta < 0 ? "" : "+") + delta);
				s.append("\t");
			}
			s.append(";\n");
		}
		s.append("\n");
		
		return s.toString();
	}
}
