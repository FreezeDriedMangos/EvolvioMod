package evolvioColor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import core.Board;
import core.Creature;
import core.modAPI.CreaturePeripheral;

public class SizeSensor implements CreaturePeripheral {

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<String> getInputNames() {
		List<String> input = new ArrayList<>();
		
		input.add("size");
		
		return input;
	}

	@Override
	public Map<String, Double> getInputValues(Creature c, Board b, double timeStep) {
		Map<String, Double> vals = new HashMap<>();
		
		vals.put("size", c.getEnergyLevel());
		
		return vals;
	}

	@Override
	public void preCreatureDraw(Creature c, Board b, float scaleUp, float camZoom, boolean overworldDraw) {}

	@Override
	public void postCreatureDraw(Creature c, Board b, float scaleUp, float camZoom, boolean overworldDraw) {}
}
