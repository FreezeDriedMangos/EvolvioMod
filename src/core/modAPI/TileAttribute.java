package core.modAPI;

import core.Board;

import core.Tile;
/**
 * Implementing this interface serves the same purpose as adding a field to the Tile class. I created this
 * interface so that nobody would have to modify any core source code. This way, installing mods will never
 * cause horrible conflicts.
 * 
 * @author clay
 *
 * @param <T> the type of the new "field" for the Tile class
 */
public interface TileAttribute<T> {
	
	/**
	 * 
	 * @param x
	 * @param y
	 * @param stepSize
	 * @param b
	 * @param t
	 */
    public void init(int x, int y, float stepSize, Board b, Tile t);
    
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
     * Called once per tic. This is the function where you want to update the value of this attribute (if you actually want to do that) 
     * 
     * @param lastUpdateTime the last time the simulation updated
     * @param updateTime the time of this current update
     * @param t the tile that this attribute belongs to
     * @param b the board that the tile t is a part of
     */
    public void update(double lastUpdateTime, double updateTime, Tile t, Board b);
}
