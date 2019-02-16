package evolvioColor;

import java.util.ArrayList;
import java.util.List;

import core.Board;
import core.Creature;
import core.modAPI.Brain;
import core.modAPI.CreatureAction;

public class CreatureChangeMouthHue implements CreatureAction {
	private static final double USER_CHANGE_SPEED = 0.01;

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

	@Override
	public void userDoAction(char keyPressed, Creature creature, Board board, double timeStep) {
		MouthHue hue = (MouthHue)creature.getAttribute("mouthHue");
		double userHue = hue.getValue();
		
		if(keyPressed == 'i') {
			userHue += USER_CHANGE_SPEED;
		} else if (keyPressed == 'k') {
			userHue -= USER_CHANGE_SPEED;
		}
		hue.setValue(Math.min(Math.max(userHue, 0), 1));
	}

	@Override
	public String getUserInstructions() {
		return "I,K: Change mouth color";
	}
}
