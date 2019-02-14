package carnivory;

import java.util.ArrayList;

import core.Board;
import core.Creature;
import core.EvolvioMod;
import core.modAPI.CreatureAttribute;

public class Carnivory implements CreatureAttribute<Double> {

	double val = 0;
	
	@Override
	public void init(Board b, Creature c) {
		val = EvolvioMod.main.random(-1, 1);
	}

	@Override
	public String getName() {
		return "carnivory";
	}

	@Override
	public Double getValue() {
		return val;
	}

	@Override
	public void setValue(Double v) {
		val = v;
	}

	@Override
	public void update(double lastUpdateTime, double updateTime, Creature c, Board b) {}

	@Override
	public void initFromParents(ArrayList<CreatureAttribute> parentAttributes, Board board) {
		for(CreatureAttribute a : parentAttributes) {
			val += (Double)a.getValue() / parentAttributes.size();
		}
		// TODO: mutation
	}

	@Override
	public CreatureAttribute<Double> fromString(String s) {
		Carnivory c = new Carnivory();
		c.val = Double.parseDouble(s);
		return c;
	}

	@Override
	public String makeString() {
		return "" + val;
	}

}
