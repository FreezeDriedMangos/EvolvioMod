package claysMisc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import core.Board;
import core.Creature;
import core.EvolvioMod;
import core.modAPI.CreaturePeripheral;

public class BrainRandomness implements CreaturePeripheral {

	@Override
	public void init() {}

	@Override
	public List<String> getInputNames() {
		List<String> l = new ArrayList<>();
		
		l.add("rand");
		
		return l;
	}

	@Override
	public Map<String, Double> getInputValues(Creature c, Board b, double timeStep) {
		Map<String, Double> vals = new HashMap<>();
		
		vals.put("rand", Double.valueOf(EvolvioMod.main.random(-1,1)));
		
		return vals;
	}

}
