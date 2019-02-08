package evolvioColor;

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
}
