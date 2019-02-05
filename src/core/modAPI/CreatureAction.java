package core.modAPI;

import core.Board;
import core.Creature;

public interface CreatureAction {
	public void doAction(Brain brain, Creature creature, Board board, double timeStep);
}
