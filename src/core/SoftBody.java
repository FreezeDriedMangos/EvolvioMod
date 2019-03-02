package core;

import java.util.ArrayList;
import java.util.List;

import com.github.ryanp102694.geometry.RectangleObject;

public class SoftBody {
	private static final double STANDING_COLLISION_FORCE = 0.5;

	public int id;
	
	public double px;
	public double py;
	private double vx;
	private double vy;
	public double energy;
	public float ENERGY_DENSITY; // set so when a creature is of minimum size, it equals one.
	public double density;
	public double hue;
	public double saturation;
	public double brightness;
	public double birthTime;
	public boolean isCreature = false;
	public final float FRICTION = 0.004f;
	public final float COLLISION_FORCE = 0.01f;
	public final float FIGHT_RANGE = 2.0f;
	public double fightLevel = 0;
	
	SoftBodyRectangleObject collisionBox;
	List<SoftBody> colliders = new ArrayList<>();
	Board board;

	private SoftBody() {}
	
	public SoftBody(double tpx, double tpy, double tvx, double tvy, double tenergy, double tdensity, double thue,
			double tsaturation, double tbrightness, Board tb, double bt) {
		px = tpx;
		py = tpy;
		vx = tvx;
		vy = tvy;
		energy = tenergy;
		density = tdensity;
		hue = thue;
		saturation = tsaturation;
		brightness = tbrightness;
		board = tb;
		//setSBIP(false);
		//setSBIP(false); // just to set previous SBIPs as well.
		
		birthTime = bt;
		ENERGY_DENSITY = 1.0f / (tb.MINIMUM_SURVIVABLE_SIZE * tb.MINIMUM_SURVIVABLE_SIZE * (float) Math.PI);

		id = board.softBodyIDUpTo + 1;
		

		collisionBox = new SoftBodyRectangleObject(this);
		tb.creatureQuadTree.insert(collisionBox);
	}

	public int xBound(int x) {
		return Math.min(Math.max(x, 0), board.boardWidth - 1);
	}

	public int yBound(int y) {
		return Math.min(Math.max(y, 0), board.boardHeight - 1);
	}

	public double xBodyBound(double x) {
		double radius = getRadius();
		return Math.min(Math.max(x, radius), board.boardWidth - radius);
	}

	public double yBodyBound(double y) {
		double radius = getRadius();
		return Math.min(Math.max(y, radius), board.boardHeight - radius);
	}

	public void collide(double timeStep) {
		List<RectangleObject> c = board.creatureQuadTree.search(collisionBox);
		colliders.clear();
		for(RectangleObject o : c) {
			if(o.getId().equals(collisionBox.getId())) continue;
			
			colliders.add(((SoftBodyRectangleObject)o).reference);
		}
		
		for (int i = 0; i < colliders.size(); i++) {
			SoftBody collider = colliders.get(i);
			float distance = EvolvioMod.main.dist((float) px, (float) py, (float) collider.px, (float) collider.py);
			double combinedRadius = getRadius() + collider.getRadius();
			if (distance < combinedRadius) {
				double force = combinedRadius * COLLISION_FORCE * (STANDING_COLLISION_FORCE+magnitude(vx - collider.vx, vy - collider.vy));
				collision(this,     force, distance, collider.px, collider.py);
				collision(collider, force, distance, this.px,     this.py);
				
				this.applyMotions(timeStep);
				collider.applyMotions(timeStep);
			}
		}
		fightLevel = 0;
	}
	
	private static void collision(SoftBody subject, double force, double distance, double otherX, double otherY) {
		subject.setVx(subject.vx + ((subject.px - otherX) / distance) * force / subject.getMass());
		subject.setVy(subject.vy + ((subject.py - otherY) / distance) * force / subject.getMass());
	}

	private static double magnitude(double x, double y) {
		return Math.sqrt(x*x + y*y);
	}

	public void applyMotions(double timeStep) {
		px = xBodyBound(px + vx * timeStep);
		py = yBodyBound(py + vy * timeStep);
		vx *= Math.max(0, 1 - FRICTION / getMass());
		vy *= Math.max(0, 1 - FRICTION / getMass());
		try {
			board.creatureQuadTree.update(collisionBox, new SoftBodyRectangleObject(this));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void drawSoftBody(float scaleUp) {
		double radius = getRadius();
		EvolvioMod.main.stroke(0);
		EvolvioMod.main.strokeWeight(board.CREATURE_STROKE_WEIGHT);
		EvolvioMod.main.fill((float) hue, (float) saturation, (float) brightness);
		EvolvioMod.main.ellipseMode(EvolvioMod.main.RADIUS);
		EvolvioMod.main.ellipse((float) (px * scaleUp), (float) (py * scaleUp), (float) (radius * scaleUp),
				(float) (radius * scaleUp));
	}

	public double getRadius() {
		if (energy <= 0) {
			return 0;
		} else {
			return Math.sqrt(energy / ENERGY_DENSITY / Math.PI);
		}
	}

	public double getMass() {
		return energy / ENERGY_DENSITY * density;
	}

	/**
	 * @return the vx
	 */
	public double getVX() {
		return vx;
	}

	/**
	 * @return the vy
	 */
	public double getVY() {
		return vy;
	}

	public double getHue() {
		return hue;
	}

	public String makeString() {
		StringBuilder s = new StringBuilder();
		
		s.append("Loc: " + "(" + px + ", " + py + ")\n");
		s.append("Vel: " + "(" + vx + ", " + vy + ")\n");
		s.append("HSB: " + "(" + hue + ", " + saturation + ", " + brightness + ")\n");
		s.append("Energy: " + energy + "\n");
		s.append("Density: " + density + "\n");
		s.append("Birth time: " + birthTime + "\n");
		
		return s.toString();
	}
	
	public static SoftBody fromString(String softbodyString) throws Exception {
		SoftBody revived = new SoftBody();
		softbodyString = softbodyString.replaceAll("\n", "");
		String[] features = softbodyString.split("\\w+: ");
		
		String[] locs = features[1].split(",");
		revived.px = Double.parseDouble(locs[0].substring(1));
		revived.py = Double.parseDouble(locs[1].substring(1,locs[1].length()-1));
		
		String[] vels = features[2].split(",");
		revived.vx = Double.parseDouble(vels[0].substring(1));
		revived.vy = Double.parseDouble(vels[1].substring(1,locs[1].length()-1));
		
		String[] cols = features[3].split(",");
		revived.hue        = Double.parseDouble(cols[0].substring(1));
		revived.saturation = Double.parseDouble(cols[1].substring(1));
		revived.brightness = Double.parseDouble(cols[2].substring(1,cols[2].length()-1));
		
		revived.energy    = Double.parseDouble(features[4]);
		revived.density   = Double.parseDouble(features[5]);
		revived.birthTime = Double.parseDouble(features[6]);
		
		return revived;
	}
	
	public void setVx(double newVx) {
		if(Double.isNaN(newVx)) {
			System.err.println("Attempted to set a NaN value for vx");
			for (StackTraceElement e : Thread.currentThread().getStackTrace() ) {
				System.err.println(e);
			}
		} else {
			vx = newVx;
		}
	}
	public void setVy(double newVy) {
		if(Double.isNaN(newVy)) {
			System.err.println("Attempted to set a NaN value for vy");
			for (StackTraceElement e : Thread.currentThread().getStackTrace() ) {
				System.err.println(e);
			}
		} else {
			vy = newVy;
		}
	}
	
}
