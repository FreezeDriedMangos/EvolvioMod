package manyBrains;

import java.util.List;
import java.util.Map;

import core.Board;
import core.Creature;
import core.EvolvioMod;
import core.modAPI.Brain;
import processing.core.PFont;

public class NewPseudoanimalsBrain implements Brain {

	@Override
	public void init(Creature c, Board b, List<String> inputsRequired, List<String> outputsRequired) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void think(Creature c, Map<String, Double> peripheralInputs, Board b, double timeStep) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public double getOutput(String name) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Brain getOffspring(List<Creature> parents, List<String> inputsRequired, List<String> outputsRequired) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void draw(PFont font, float scaleUp, int mX, int mY) {
		EvolvioMod.main.fill(0);
		EvolvioMod.main.text("Not implemented", 0, 0);
	}

	@Override
	public Brain fromString(String s) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean canMate(List<Brain> parents) {
		// TODO Auto-generated method stub
		return false;
	}
	// axon weights have a genetically defined value, but can drift from that value (to a certain point) according to usage
	// ^ neural plasticity

	@Override
	public String makeString() {
		// TODO Auto-generated method stub
		return null;
	}
}
