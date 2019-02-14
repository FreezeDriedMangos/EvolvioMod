package core.modAPI;

import core.Tile;

/**
 * Only one implementation of this interface can be loaded at once. Implementations of this interface 
 * handle drawing tiles (individually). If your mod has tile attributes that you'd like to represent visually,
 * I recommend writing an implementation of this interface.
 * 
 * @author clay
 *
 */
public interface TileDrawer {
	/**
	 * Draw the given tile at the given scale. Tiles are (unscaled) one unit long. Make sure to draw this tile at the
	 * position (t.x * scaleUp, t.y * scaleUp).
	 * @param t the tile to be drawn
	 * @param scaleUp the scale at which to draw the tile
	 */
	public void draw(Tile t, float scaleUp);
	
	/**
	 * This is a fun little optional method. It's purpose is to make adjacent tiles blend together when that option
	 * is toggled from the main menu. It's reccomended to simply draw a larger, lower alpha version of what you
	 * would draw in the method draw()
	 * @param t
	 * @param scaleUp
	 */
	public void drawBlendLayer(Tile t, float scaleUp);
	
	/**
	 * Draw the mouseover information for the given tile
	 * @param t
	 * @param scaleUp
	 */
	public void drawInformation(Tile t, float scaleUp);
	
	/**
	 * Get the draw color of the given tile. Used for creatures' senses.
	 * @param t the tile to get the color of
	 * @return the color of the tile t
	 */
	public int getColor(Tile t);
}
