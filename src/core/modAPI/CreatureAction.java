package core.modAPI;

import java.util.List;

import core.Board;
import core.Creature;

public interface CreatureAction {
	public List<String> getRequiredOutputs();
	public void doAction(Brain brain, Creature creature, Board board, double timeStep);
}
