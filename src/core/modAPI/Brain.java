package core.modAPI;

import java.util.ArrayList;

import core.Board;
import core.Creature;

public interface Brain {
	// should always return the same thing, ideally should return a literal array of literal strings
	public String[] getCustomOutputNames();
	
	// output names will forcefully include standard outputs like accelerate, turn
	// fight, have a baby, eat, etc.
	public void init(Creature c, Board b, String[] outputNames);
	public void think(Creature c, Board b, double timeStep); // updates the brain
	public double getOutput(String name);
	public double[] getAllOutputs();
	public String[] getOutputNames();
	public void draw();
	
	public Brain getOffspring(ArrayList<Creature> parents);

	public void useOutput(Creature creature, Board board, double timeStep);
}
