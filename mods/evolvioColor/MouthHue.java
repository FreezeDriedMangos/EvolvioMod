package evolvioColor;

import java.util.ArrayList;

import core.Board;
import core.Creature;
import core.EvolvioMod;
import core.modAPI.CreatureAttribute;
import core.modAPI.CreatureFeatureDrawer;

public class MouthHue implements CreatureAttribute<Double>, CreatureFeatureDrawer {

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
	public void update(double lastUpdateTime, double updateTime, Creature c, Board b) {}

	@Override
	public void initFromParents(ArrayList<CreatureAttribute> parentAttributes, Board board) {
		mouthHue = 0;
		double numParents = parentAttributes.size();
		
		for(CreatureAttribute c : parentAttributes) {
			mouthHue += (Double)c.getValue() / numParents;
		}
	}



	@Override
	public void preCreatureDraw(Creature c, Board b, float scaleUp, boolean overworldDraw) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postCreatureDraw(Creature creature, Board board, float scaleUp, boolean overworldDraw) {
		double radius = creature.getRadius();
		
		// TODO: this stopped working for some reason
		
		EvolvioMod.main.ellipseMode(EvolvioMod.main.RADIUS);
		EvolvioMod.main.pushMatrix();
		EvolvioMod.main.translate((float) (creature.px * scaleUp), (float) (creature.py * scaleUp));
		EvolvioMod.main.scale((float) radius);
		EvolvioMod.main.rotate((float) creature.rotation);
		EvolvioMod.main.strokeWeight((float) (board.CREATURE_STROKE_WEIGHT / radius));
		EvolvioMod.main.stroke(0, 0, 0);
		EvolvioMod.main.fill((float) mouthHue, 1.0f, 1.0f);
		EvolvioMod.main.ellipse(0.6f * scaleUp, 0, 0.37f * scaleUp, 0.37f * scaleUp);
		/*
		 * rect(-0.7*scaleUp,-0.2*scaleUp,1.1*scaleUp,0.4*scaleUp); beginShape();
		 * vertex(0.3*scaleUp,-0.5*scaleUp); vertex(0.3*scaleUp,0.5*scaleUp);
		 * vertex(0.8*scaleUp,0.0*scaleUp); endShape(CLOSE);
		 */
		EvolvioMod.main.popMatrix();
	}

	@Override
	public String makeString() {
		return "" + mouthHue;
	}

	@Override
	public CreatureAttribute<Double> fromString(String s) {
		MouthHue h = new MouthHue();
		h.mouthHue = Integer.parseInt(s);
		return h;
	}
}
