package core.modAPI;

import java.util.List;
import java.util.Map;

import core.Board;
import core.Creature;

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
	public void preCreatureDraw(Creature c, Board b, float scaleUp, float camZoom);
	public void postCreatureDraw(Creature c, Board b, float scaleUp, float camZoom);
}
