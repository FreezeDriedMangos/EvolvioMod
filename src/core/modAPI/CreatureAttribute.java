package core.modAPI;

import java.util.ArrayList;

import core.Board;
import core.Creature;

/**
 * Implementing this interface serves the same purpose as adding a field to the Creature class. I created this
 * interface so that nobody would have to modify any core source code. This way, installing mods will never
 * cause horrible conflicts.
 * 
 * @author clay
 *
 * @param <T> the type of the new "field" for the Creature class
 */
public interface CreatureAttribute<T> {
    
	/**
	 * Called when the creature c is created
	 * @param b the board that the creature c lives on
	 * @param c the creature whose attribute you're initializing
	 */
    public void init(Board b, Creature c);
    
    /**
     * What is this attribute called?
     * @return the name of this attribute (not neccessarily this class' name)
     */
    public String getName();
    
    /**
     * What is the current value of this attribute?
     * @return the value of this attribute
     */
    public T getValue();
    
    /**
     * Set the value of this attribute
     * @param v
     */
    public void setValue(T v);
    
    /**
     * Called once per tic. This is the function where you want to update this 
     * @param lastUpdateTime the last time the simulation updated
     * @param updateTime the time of this current update
     * @param c the creature that this attribute belongs to
     * @param b the board that the creature c lives on
     */
    public void update(double lastUpdateTime, double updateTime, Creature c, Board b);

    /**
     * Called once per tic. This is the function where you want to update the value of this attribute (if you actually want to do that) 
     * 
     * @param parentAttributes a list of the attributes (of this implementation) that belong to the parents. To be clear,
     * every single element of this list is an instance of this same implementation
     * @param board the board that the parents (and soon to be child) live on
     */
	public void initFromParents(ArrayList<CreatureAttribute> parentAttributes, Board board);
	
	/**
	 * This function is used to write the creature that this attribute object belongs to to file to save it from permanent deletion.
	 * @return A string containing all of the information needed to reconstruct this attribute for this creature.
	 */
	public String makeString();
	/**
	 * This method is used to revive creatures essentially from cryosleep
	 * @param s a string previously returned from toString()
	 * @return the attribute revived from the string representation s
	 */
	public CreatureAttribute<T> fromString(String s);
	//TODO: future update: speciation
//	public float getSpeciesDelta(Creature other);
//	/**
//	 * Two creatures will be allowed to mate if all attributes and brains in both creatures return true here
//	 * @param totalSpeciesDelta = brain.getSpeciesDelta(other.brain) * 0.5 + creatureAttributesSpeciesDelta_Sum * 0.5 
//	 * @return
//	 */
//	public boolean canMate(float totalSpeciesDelta);
}
