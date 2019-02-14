package evolvioColor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import core.Board;
import core.Creature;
import core.EvolvioMod;
import core.modAPI.CreatureFeatureDrawer;
import core.modAPI.CreaturePeripheral;

public class MouthHueSensor implements CreaturePeripheral {

	double mouthHue = 0;
	
	@Override
	public void init() {}

	@Override
	public List<String> getInputNames() {
		List<String> input = new ArrayList<>();
		
		input.add("mHue");
		
		return input;
	}

	@Override
	public Map<String, Double> getInputValues(Creature c, Board b, double timeStep) {
		Map<String, Double> vals = new HashMap<>();
		
		mouthHue = (Double)c.getAttribute("mouthHue").getValue();
		vals.put("mHue", mouthHue);
		
		return vals;
	}
}
