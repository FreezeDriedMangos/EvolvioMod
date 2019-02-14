package core.modAPI;

import core.Board;
import core.Tile;
import core.Creature;

/**
 * Only one implementation of this interface can be loaded at once. Implementing this interface allows you to 
 * control how creatures eat from tiles and deposit food to tiles (by vomiting or by dying)
 * @author clay
 *
 */
public interface CreatureEatBehavior {
	/**
	 * Called when a creature tries to eat food from a tile to determine how successful it is. It shouldn't
	 * modify the creature or the tile.
	 * 
	 * @param c the creature attempting to eat
	 * @param t the tile it's attempting to eat from 
	 * @param attemptedAmount the amount of food that the creature is trying to eat
	 * @param timeStep not sure what this is, but Evolvio Original and Evolvio Color needed it. 
	 * @return the amount of food that the creature actually will eat
	 */
    public double getCreatureEatAmount(Creature c, Tile t, double attemptedAmount, double timeStep);
    
    /**
     * Called when a creature tries to eat food from a tile after calling getCreatureEatAmount().  
     * This method should handle what happens when a creature actually eats from a tile. It probably 
     * should modify both the creature and the tile
     * 
     * @param c the creature eating
     * @param t the tile being eaten from
     * @param amount the amount of food being eaten
     * @param attemptedAmount the amount of food attempted to be eaten
     * @param timeStep
     */
    public void creatureEatFromTile(Creature c, Tile t, double amount, double attemptedAmount, double timeStep);
    
    /**
     * Called when a creature tries to eat food from a tile after calling getCreatureEatAmount().  
     * This method should handle what happens when a creature actually eats from a tile but ends up not being able
     * to eat anything. It probably should modify just the creature and not the tile.
     * 
     * <br>
     * 
     * this is basically creatureEatFromTile() where amount <= 0
     * 
     * @param c the creature eating
     * @param t the tile being eaten from
     * @param amount the amount of food being eaten
     * @param attemptedAmount the amount of food attempted to be eaten
     * @param timeStep
     */
    public void creatureFailToEatFromTile(Creature c, Tile t, double amount, double attemptedAmount, double timeStep);

    /**
     * Basically the reverse of creatureEatFromTile(). Called when a creature dies or "vomits". It should
     * modify both the creature and the tile.
     * 
     * @param c the creature depositing food
     * @param t the tile being deposited to
     * @param amount the amount being deposited
     */
    public void creatureDepositFoodToTile(Creature c, Tile t, double amount);
}
