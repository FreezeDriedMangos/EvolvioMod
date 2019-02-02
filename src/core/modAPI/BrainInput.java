package core.modAPI;

public interface BrainInput {
	public String getDisplayName(); // the shortened version of getName() that gets drawn to screen
	public String getName(); // the full name of the input (ie: eyeballHue0)
	public double getValue();
}
