package claysMisc;

import java.util.ArrayList;
import java.util.List;

import core.Board;
import core.Creature;
import core.EvolvioMod;
import core.SoftBody;
import core.modAPI.Brain;
import core.modAPI.CreatureAction;
import core.modAPI.CreatureFeatureDrawer;
import core.modAPI.CreaturePeripheral;

public class Speech implements CreatureAction, CreatureFeatureDrawer {

	private static final float DRAW_ALPHA = 0.5f;
	private static final float DEFAULT_RANGE = 100;
	
	float voiceRange = DEFAULT_RANGE;
	
	double hue;
	double intensity;
	
	@Override
	public void preCreatureDraw(Creature creature, Board b, float scaleUp, boolean overworldDraw) {
		EvolvioMod.main.ellipseMode(EvolvioMod.main.RADIUS);
		EvolvioMod.main.pushMatrix();
		EvolvioMod.main.translate((float) (creature.px * scaleUp), (float) (creature.py * scaleUp));
		
		EvolvioMod.main.noStroke();
		EvolvioMod.main.fill(EvolvioMod.main.color((float)hue, 1, 1, DRAW_ALPHA));
		
		float radius = (float) (voiceRange * intensity * scaleUp);
		EvolvioMod.main.ellipse(0,0, radius, radius);
		
		EvolvioMod.main.popMatrix();
	}

	@Override
	public void postCreatureDraw(Creature creature, Board b, float scaleUp, boolean overworldDraw) {}

	@Override
	public List<String> getRequiredOutputs() {
		List<String> outputs = new ArrayList<>();
		outputs.add("speakHue");
		outputs.add("speak");
		
		return outputs;
	}

	@Override
	public void doAction(Brain brain, Creature creature, Board board, double timeStep) {
		intensity = Math.max(0, brain.getOutput("speak"));
		hue = (brain.getOutput("speak")+1.0)/2.0;
		
		if(intensity > 0) {
			updateOthers(creature, board);
		}
	}
	
	public void updateOthers(Creature creature, Board board) {
		System.out.println("\tupdating others");
		
		double sidelength = voiceRange*intensity;
		List<SoftBody> inRange = board.getSoftBodiesInArea(creature.px-sidelength, creature.py-sidelength, sidelength*2, sidelength*2);
	
		for(SoftBody s : inRange) {
			Creature c = (Creature)s;
			
			for(CreaturePeripheral p : c.getPeripherals()) {
				if(p instanceof Ears) {
					((Ears)p).hear(hue, creature.px - c.px, creature.py - c.py);
				}
			}
		}
	}

	@Override
	public void userDoAction(char keyPressed, Creature creature, Board board, double timeStep) {
		intensity = 0;
		
		if(keyPressed == 'z') {
			hue -= 0.1;
		} else if(keyPressed == 'c') {
			hue += 0.1;
		} else if(keyPressed == 'x') {
			intensity = 1;
			updateOthers(creature, board);
		}
	}

	@Override
	public String getUserInstructions() {
		return "z/c: change tone\nx: speak";
	}
}
