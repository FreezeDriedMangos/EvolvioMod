package behaviorDeterrants;

import java.util.ArrayList;

import core.Board;
import core.Creature;
import core.modAPI.CreatureAttribute;

public class Dizziness implements CreatureAttribute<Double> {
	
	public static final double ENERGY_SCALAR = 1000;
	public static final double ROTATION_THRESHOLD = 0.5;
	public static final double ROTATION_ZERO_THRESHOLD = 0.1;
	
	
	double rotAt;
	double timeStartedRotating;
	double timeSpentRotating;
	
	@Override
	public void init(Board b, Creature c) {}

	@Override
	public String getName() {
		return "Dizziness";
	}

	@Override
	public Double getValue() {
		return timeSpentRotating;
	}

	@Override
	public void setValue(Double v) {}

	@Override
	public void update(double lastUpdateTime, double updateTime, Creature c, Board b) {
		//TODO: the longer that a creature turns for, the more turning costs
		
		// if switch from posRot to neg rot, reset rotationStartTime
		
		double rot = c.getBrain().getOutput("turn");
		
		if(Math.abs(rot) < ROTATION_ZERO_THRESHOLD) { // not rotating
			rotAt = rot;
			timeStartedRotating = b.getYear();
		}
		
		if(Math.signum(rot) != Math.signum(rotAt)) { // switched rotation direction
			rotAt = rot;
			timeStartedRotating = b.getYear();
			}
		
		if(Math.abs(rot-rotAt) > ROTATION_THRESHOLD) { // changed rotation significantly
			rotAt = rot;
			timeStartedRotating = b.getYear();
		}
		
		timeSpentRotating = timeStartedRotating-b.getYear();
		
		c.loseEnergy(timeSpentRotating*ENERGY_SCALAR);
	}
	
	@Override
	public String makeString() {
		return "" + timeSpentRotating + " " + rotAt + " " + timeStartedRotating;
	}
	
	@Override
	public CreatureAttribute<Double> fromString(String s) {
		Dizziness d = new Dizziness();
		
		s.split(" ");
		
		return d;
	}

	@Override
	public void initFromParents(ArrayList<CreatureAttribute> parentAttributes, Board board) {}
	
	
}
