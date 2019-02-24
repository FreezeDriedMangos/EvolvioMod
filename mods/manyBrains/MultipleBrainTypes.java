package manyBrains;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import core.Board;
import core.Creature;
import core.EvolvioUtils;
import core.modAPI.Brain;
import evolvioOriginal.EvolvioBrain;
//import core.modAPI.metaTools.ExternalModRequirements;
import processing.core.PFont;

public class MultipleBrainTypes implements Brain/* , ExternalModRequirements */{

	BrainType type;
	
	Brain actualBrain;
	
	@Override
	public void init(Creature c, Board b, List<String> inputsRequired, List<String> outputsRequired) {
		type = BrainType.getRandomType();
		
		if(type == BrainType.EVOLVIO) {
			actualBrain = new EvolvioBrain();
		} else if (type == BrainType.NEWPSEUDO) {
			actualBrain = new NewPseudoanimalsBrain();
		}
		
		actualBrain.init(c, b, inputsRequired, outputsRequired);
	}

	@Override
	public void think(Creature c, Map<String, Double> peripheralInputs, Board b, double timeStep) {
		actualBrain.think(c, peripheralInputs, b, timeStep);
	}

	@Override
	public double getOutput(String name) {
		return actualBrain.getOutput(name);
	}

	@Override
	public Brain getOffspring(List<Brain> parents, List<String> inputsRequired, List<String> outputsRequired) {
		BrainType type = ((MultipleBrainTypes)parents.get(0)).type;
		
		if(type == null) {
			System.err.println("Parents had no brain type.");
			EvolvioUtils.printStackTrace(System.err);
			
			Brain child = new MultipleBrainTypes();
			child.init(null, null, inputsRequired, outputsRequired);
			return child;
		}
		
		List<Brain> actualParents = new ArrayList<>();
		for(Brain parent : parents) {
			actualParents.add(((MultipleBrainTypes)parent).actualBrain);
		}
		
		if(type == BrainType.EVOLVIO) {
			return new EvolvioBrain().getOffspring(actualParents, inputsRequired, outputsRequired);
		} else if (type == BrainType.NEWPSEUDO) {
			return new NewPseudoanimalsBrain().getOffspring(actualParents, inputsRequired, outputsRequired);
		}
		
		return null;
	}

	@Override
	public void draw(PFont font, float scaleUp, int mX, int mY) {
		actualBrain.draw(font, scaleUp, mX, mY);
	}

	@Override
	public String makeString() {
		return type.toString() + "\n" + actualBrain.makeString();
	}

	@Override
	public Brain fromString(String s) {
		Scanner scan = new Scanner(s);
		String typeString = scan.nextLine();
		scan.close();
		
		s = s.substring(s.indexOf('\n')+1);
		
		if(typeString.equals(BrainType.EVOLVIO.toString())) {
			return new EvolvioBrain().fromString(s);
		} else if(typeString.equals(BrainType.NEWPSEUDO.toString())) {
			return new NewPseudoanimalsBrain().fromString(s);
		}
		
		return null;
	}

	@Override
	public boolean canMate(List<Brain> parents) {
		BrainType type = ((MultipleBrainTypes)parents.get(0)).type;
		for(Brain b : parents) {
			if(((MultipleBrainTypes)b).type != type) {
				return false;
			}
		}
		
		List<Brain> actualParents = new ArrayList<>();
		for(Brain parent : parents) {
			actualParents.add(((MultipleBrainTypes)parent).actualBrain);
		}
		
		if(type == BrainType.EVOLVIO) {
			return new EvolvioBrain().canMate(actualParents);
		} else if (type == BrainType.NEWPSEUDO) {
			return new NewPseudoanimalsBrain().canMate(actualParents);
		}
		
		return false;
	}

//	@Override
//	public List<String> getRequiredMods() {
//		List<String> list = new ArrayList<>();
//		list.add("evolvioOriginal.EvolvioBrain");
//		return list;
//	}
	
	static enum BrainType {
		EVOLVIO, NEWPSEUDO;

		public static BrainType getRandomType() {
			int t = (int) (Math.random()*2);
			
			if(t == 0) { return EVOLVIO;   }
			if(t == 1) { return NEWPSEUDO; }
			
			return null;
		}
	}
}

