package core.modAPI;

import java.util.List;
import java.util.Map;

import core.Board;
import core.Creature;
import processing.core.PFont;

public interface Brain {
	// output names will always include standard outputs like accelerate, turn
	// fight, have a baby, eat, etc.
	public void init(Creature c, Board b, List<String> inputsRequired, List<String> outputsRequired);
	public void think(Creature c, Map<String, Double> peripheralInputs, Board b, double timeStep); // updates the brain
	public double getOutput(String name);
	
	public Brain getOffspring(List<Creature> parents, /*List<BrainInput> inputsRequired,*/ List<String> inputsRequired, List<String> outputsRequired);
	
	public void draw(PFont font, float scaleUp, int mX, int mY);
	//TODO: future update: speciation
//	public double getSpeciesDelta(Brain other);
//	public boolean canMate(double speciesDelta);
}
