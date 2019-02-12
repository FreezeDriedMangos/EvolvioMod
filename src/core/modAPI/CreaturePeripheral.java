package core.modAPI;

import java.util.List;
import java.util.Map;

import core.Board;
import core.Creature;

/**
 * Note: this interface will never have a initFromParents() method. If you want your
 * peripheral to be impacted by this creature's parents', consider using a creature
 * attribute to inform your peripheral. Creature attributes <i>do</i> have inheritance
 * @author clay
 *
 */
public interface CreaturePeripheral {
	public void init();
//	public CreaturePeripheral getOffspring(List<Creature> parents);
	
	public List<String> getInputNames();
	/**
	 * Updating the peripheral should also happen here
	 * @param c
	 * @param b
	 * @param timeStep
	 * @return
	 */
	public Map<String, Double> getInputValues(Creature c, Board b, double timeStep);
}
