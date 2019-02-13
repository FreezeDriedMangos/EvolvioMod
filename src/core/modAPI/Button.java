package core.modAPI;

import core.Board;

/**
 * A button for the main menu, just to the left of the temperature bar and underneath the creature rankings
 * @author clay
 *
 */
public interface Button {
	public static final int STANDARD_BUTTON_WIDTH = 220;
	public static final int STANDARD_BUTTON_HEIGHT = 40;
	public static final int NUM_COLUMNS = 3;//2;
	public static final int PADDING = 10;
	
	/**
	 * Called when this button is clicked
	 * @param relX the location of the click relative to the top left corner of the button
	 * @param relY the location of the click relative to the top left corner of the button
	 */
    public void click(int relX, int relY);

    /**
     * Called by Board.drawUI() 
     * @return the string that will be drawn on the top half of the button
     */
	public String getText();
	/**
     * Called by Board.drawUI() 
     * @return the string that will be drawn on the bottom half of the button
     */
	public String getSecondLineText();
	
	/**
	 * if your button flashes, this will control that. You are responsible for dimming the flash over time
	 * @return the current intensity of the flash
	 */
	public float getFlashAlpha();
	public void init();
}
