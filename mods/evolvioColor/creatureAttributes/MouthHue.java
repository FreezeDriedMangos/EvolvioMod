package evolvioColor.creatureAttributes;

import java.util.ArrayList;

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

	@Override
	public void initFromParents(ArrayList<CreatureAttribute> parentAttributes, Board board) {
		mouthHue = 0;
		double numParents = parentAttributes.size();
		
		for(CreatureAttribute c : parentAttributes) {
			mouthHue += (Double)c.getValue() / numParents;
		}
	}

}
