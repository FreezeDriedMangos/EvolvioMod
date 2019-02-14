package core.modAPI;

import core.Board;
import core.Creature;

/**
 * Implementing this interface allows you to draw extra dodads on the creatures. These functions were
 * originally in the CreaturePeripheral interface, but I moved them here for optomization and to allow
 * CreatureAttributes to be drawn, if desired.
 * 
 * @author clay
 *
 */
public interface CreatureFeatureDrawer {
	/**
	 * Called before the main body of the creature is drawn
	 * 
	 * @param c the creature being drawn
	 * @param b the board the creature lives on
	 * @param scaleUp the scale that whatever you're drawing should be drawn at
	 * @param overworldDraw true if drawing in the overworld. False if drawing in a menu or something
	 */
	public void preCreatureDraw(Creature c, Board b, float scaleUp, boolean overworldDraw);
	/**
	 * Called after the main body of the creature is drawn
	 * 
	 * @param c the creature being drawn
	 * @param b the board the creature lives on
	 * @param scaleUp the scale that whatever you're drawing should be drawn at
	 * @param overworldDraw true if drawing in the overworld. False if drawing in a menu or something
	 */
	public void postCreatureDraw(Creature c, Board b, float scaleUp, boolean overworldDraw);
}
