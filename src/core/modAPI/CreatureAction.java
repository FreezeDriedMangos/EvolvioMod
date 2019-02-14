package core.modAPI;

import java.util.List;

import core.Board;
import core.Creature;

/**
 * An action associated with a set of outputs. Implement this interface if you want your creatures to interact with
 * the environment (tiles or other creatures) in new ways.
 * @author clay
 *
 */
public interface CreatureAction {
	/**
	 * @return a list of strings representing the outputs this action uses
	 */
	public List<String> getRequiredOutputs();
	
	/**
	 * This function is called after the creature's brain's think() method is called. In this method
	 * Unless you want the creatures to constantly be performing this action, it is reccomended
	 * you check the brain's output values to make sure that the creature is trying to perform this action.
	 * This action doesn't neccessarily have to trigger.
	 * @param brain the brain of the creature that may or may not perform this action
	 * @param creature the creature mentioned above
	 * @param board the board that creature lives on
	 * @param timeStep I really don't know what this does. I included it because some Evolvio Original mods needed it for some reason
	 */
	public void doAction(Brain brain, Creature creature, Board board, double timeStep);
}
