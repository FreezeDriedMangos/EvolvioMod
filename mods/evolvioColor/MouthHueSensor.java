package evolvioColor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import core.Board;
import core.Creature;
import core.EvolvioMod;
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

	@Override
	public void preCreatureDraw(Creature c, Board b, float scaleUp, float camZoom, boolean overworldDraw) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postCreatureDraw(Creature creature, Board board, float scaleUp, float camZoom, boolean overworldDraw) {
		double radius = creature.getRadius();
		
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
	// remove literal references in EvolvioBrain
}
