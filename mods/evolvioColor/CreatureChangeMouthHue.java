package evolvioColor;

import java.util.ArrayList;
import java.util.List;

import core.Board;
import core.Creature;
import core.modAPI.Brain;
import core.modAPI.CreatureAction;

public class CreatureChangeMouthHue implements CreatureAction {
	@Override
	public void doAction(Brain brain, Creature creature, Board board, double timeStep) {
		double val = Math.min(Math.max(brain.getOutput("mouthHue"), 0), 1);
		((MouthHue)creature.getAttribute("mouthHue")).setValue(val);
	}

	@Override
	public List<String> getRequiredOutputs() {
		List<String> list = new ArrayList<>();
		
		list.add("mouthHue");
		
		return list;
	}
}
