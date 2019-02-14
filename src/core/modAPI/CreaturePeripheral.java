package core.modAPI;

import java.util.List;
import java.util.Map;

import core.Board;
import core.Creature;

/**
 * Implementing this interface is a way to add sensory organs to the creatures 
 * <br>
 * Note: this interface will never have a initFromParents() method. If you want your
 * peripheral to be impacted by this creature's parents', consider using a creature
 * attribute to inform your peripheral. Creature attributes <i>do</i> have inheritance
 * @author clay
 *
 */
public interface CreaturePeripheral {
	/**
	 * This should do everything your constructor would normally do
	 */
	public void init();
//	public CreaturePeripheral getOffspring(List<Creature> parents);
	
	/**
	 * This method should always return the same values. I would've declared it static if interfaces supported
	 * static abstract methods.
	 * 
	 * @return a list of all brain inputs that this peripheral will supply the values of (you are encouraged to 
	 * make up your own input names, rather than use inputs that already exist)
	 */
	public List<String> getInputNames();
	
	/**
	 * Updating the peripheral should also happen here.
	 * 
	 * @param c the creature that this peripheral object belongs to
	 * @param b the board that c lives on
	 * @param timeStep again, not sure what this is, but Eyestalks in the Evolvio Original mod needed it
	 * @return every string returned by getInputNames() should have an entry in this map. There should be no
	 * other entries in this map
	 */
	public Map<String, Double> getInputValues(Creature c, Board b, double timeStep);
}
