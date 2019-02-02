package evolvioColor.creatureAttributes;

import core.Board;
import core.Creature;
import core.modAPI.CreatureAttribute;

public class MouthHue implements CreatureAttribute<Double> {

	double mouthHue;
	
	@Override
	public void init(Board b, Creature c) {
		mouthHue = c.secondaryHue;
	}

	@Override
	public String getName() {
		return "mouthHue";
	}

	@Override
	public Double getValue() {
		return mouthHue;
	}

	@Override
	public void setValue(Double v) {
		mouthHue = v;
	}

	@Override
	public void update(double lastUpdateTime, double updateTime, Creature c, Board b) {
		// TODO Auto-generated method stub

	}

}
