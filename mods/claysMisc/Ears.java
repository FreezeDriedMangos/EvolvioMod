package claysMisc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import core.Board;
import core.Creature;
import core.modAPI.CreaturePeripheral;

public class Ears implements CreaturePeripheral {

	public static final float HEARING_DISTANCE = 10;

	private double nearestHue = -1;
	private double distToNearest = Integer.MAX_VALUE;
	private double directionToNearest = -1;
	
	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<String> getInputNames() {
		List<String> inputs = new ArrayList<>();
		inputs.add("heardHue");
		inputs.add("distToHeard");
		inputs.add("dirToHeard");
		
		return inputs;
	}

	@Override
	public Map<String, Double> getInputValues(Creature c, Board b, double timeStep) {
		Map<String, Double> retval = new HashMap<>();
		
		retval.put("heardHue", nearestHue);
		retval.put("distToHeard", sigmoid(distToNearest));
		retval.put("dirToHeard", directionToNearest);
		
		
		distToNearest = Integer.MAX_VALUE;
		nearestHue = -1;
		directionToNearest = -1;
		
		return retval;
	}
	
	public static double sigmoid(double x) {
		return (2.0/(1+Math.pow(Math.E, x)))-1;
	}

	public void hear(double hue, double dx, double dy) {
		double dist = Math.sqrt(dx*dx + dy*dy);
		
		if(dist <= distToNearest) {
			return;
		}
		
		distToNearest = dist;
		nearestHue = hue;
		directionToNearest = Math.atan2(dy, dx);
	}
}
