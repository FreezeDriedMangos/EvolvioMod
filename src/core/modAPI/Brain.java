package core.modAPI;

import java.util.List;
import java.util.Map;

import core.Board;
import core.Creature;
import processing.core.PFont;

/**
 * This interface allows you to write your own code to model a brain for the creatures. Only one implementation of
 * this interface can be loaded at once.
 * @author clay
 *
 */
public interface Brain {
	// output names will always include standard outputs like accelerate, turn
	// fight, have a baby, eat, etc.
	/**
	 * Initialization. Note: if you do not set up this brain object to provide all outputs specified by outputsRequired,
	 * bad things will probably happen.
	 * 
	 * @param c the creature that this brain object will belong to
	 * @param b the board that the Creature c lives on
	 * @param inputsRequired a list of all the inputs this brain object will have to take into account
	 * @param outputsRequired a list of all the outputs this brain must provide
	 */
	public void init(Creature c, Board b, List<String> inputsRequired, List<String> outputsRequired);
	
	/**
	 * This is the "update" method. It is called once per tic, before outputs are retrieved from this brain object.
	 * @param c the creature this brain belongs to
	 * @param peripheralInputs the current values of the inputs specified in the init() method
	 * @param b the board that creature c lives on
	 * @param timeStep I'm going to be totally honest, I have no idea what timeStep is for. It propogates down the method calls from EvolvioMod.TIME_STEP. Check EvolvioBrain in the "Evolvio Original" mod for 
	 * an example on what to do with it
	 */
	public void think(Creature c, Map<String, Double> peripheralInputs, Board b, double timeStep); // updates the brain
	
	/**
	 * Called after think(), once per output specified in init(). Please return a value for each of these, getOutput()
	 * will never be called with an invalid value of name
	 * @param name the name of the output whose value was requested
	 * @return the value of the requested output
	 */
	public double getOutput(String name);
	
	/**
	 * Called when one or more creatures reproduce. This is your chance to have the creatures' brains evolve!
	 * @param parents a list of all the parents involved in the creation of this new life. You can assume that all elements of this List are instances of your Brain implementation
	 * @param inputsRequired all the inputs that this new brain will be required to consider (same as init)
	 * @param outputsRequired a list of all the outputs this brain must provide (same as init)
	 * @return an entirely new brain object (with no references shared with its parents) that is the result of 
	 * the parents mating
	 */
	public Brain getOffspring(List<Brain> parents, List<String> inputsRequired, List<String> outputsRequired);
	
	/**
	 * Draw the brain! Please use Processing's built-in tools for this. Don't worry about where to draw it, the drawspace has already been translated to the right spot
	 * for you.
	 * @param font the font used for drawing text
	 * @param scaleUp what scale to draw the brain at
	 * @param mX the relative x coordinate of the mouse (if this seems wrong, that's on me)
	 * @param mY the relative y coordinate of the mouse (if this seems wrong, that's on me)
	 */
	public void draw(PFont font, float scaleUp, int mX, int mY);
	
	/**
	 * This function is used to write this brain to file to save it from permanent deletion.
	 * @return A string containing all of the information needed to reconstruct this brain.
	 */
	public String makeString();
	
	/**
	 * This method is used to read a brain from file
	 * @param s a string previously returned from makeString()
	 * @return the brain revived from the string representation s
	 */
	public Brain fromString(String s);

	/**
	 * This function will essentially be called statically. I would declare this as a staic method, but interfaces
	 * can't have static abstract methods. 
	 * <br>
	 * This method is called just before getOffspring(), which is only called if this function returns true
	 * @param parents A list of prospective parents' brains. You can assume that all elements of this List are instances of your Brain implementation
	 * @return whether or not the parents in the list can all mate with each other
	 */
	public boolean canMate(List<Brain> parents);
	
}
