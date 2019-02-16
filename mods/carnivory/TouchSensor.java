package carnivory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import core.Board;
import core.Creature;
import core.EvolvioMod;
import core.SoftBody;
import core.modAPI.CreaturePeripheral;
import core.modAPI.CreatureFeatureDrawer;

public class TouchSensor implements CreaturePeripheral, CreatureFeatureDrawer {

	private static final String INPUT_NAME = "touch";
	private static final double MAX_TOUCH_DISTANCE = 0.5;
	private static final double TOUCH_PERIMETER_STROKE_WEIGHT = 0.2;
	private static final float TOUCH_PERIMETER_ALPHA = 0.2f;
	
	private int resolution = 10;
	double[] visionOccludedX = new double[resolution];
	double[] visionOccludedY = new double[resolution];
	double distanceDetected = MAX_TOUCH_DISTANCE;
	
	@Override
	public void init() {}

	@Override
	public List<String> getInputNames() {
		List<String> list = new ArrayList<>();

		list.add("touch");
		
		return list;
	}


	@Override
	public Map<String, Double> getInputValues(Creature creature, Board board, double timeStep) {
		distanceDetected = MAX_TOUCH_DISTANCE; // reinitialize
		
		double maxAngle = 2*Math.PI;
		double deltaAngle = maxAngle / (double)resolution;
		
		double touchX = creature.px - MAX_TOUCH_DISTANCE;
		double touchY = creature.py - MAX_TOUCH_DISTANCE;
		double touchS = 2.0*MAX_TOUCH_DISTANCE;
		
		List<SoftBody> potentialTouchers = board.getSoftBodiesInArea(touchX, touchY, touchS, touchS);
		for(SoftBody s : potentialTouchers) {
			double dist = this.distance(s.px, s.px, creature.px, creature.py);
			distanceDetected = Math.min(distanceDetected, dist);
		}
		
//		for (int k = 0; k < resolution; k++) {
//			double visionStartX = creature.px;
//			double visionStartY = creature.py;
//			double visionTotalAngle = creature.rotation + k*deltaAngle;
//
//			double endX = getVisionEndX(k*deltaAngle, creature, MAX_TOUCH_DISTANCE);
//			double endY = getVisionEndY(k*deltaAngle, creature, MAX_TOUCH_DISTANCE);
//
//			visionOccludedX[k] = endX;
//			visionOccludedY[k] = endY;
//
//			int tileX = 0;
//			int tileY = 0;
//			int prevTileX = -1;
//			int prevTileY = -1;
//			ArrayList<SoftBody> potentialVisionOccluders = new ArrayList<SoftBody>();
//			for (int DAvision = 0; DAvision < MAX_TOUCH_DISTANCE + 1; DAvision++) {
//				tileX = (int) (visionStartX + Math.cos(visionTotalAngle) * DAvision);
//				tileY = (int) (visionStartY + Math.sin(visionTotalAngle) * DAvision);
//				if (tileX != prevTileX || tileY != prevTileY) {
//					addPVOs(tileX, tileY, creature, board, potentialVisionOccluders);
//					if (prevTileX >= 0 && tileX != prevTileX && tileY != prevTileY) {
//						addPVOs(prevTileX, tileY, creature, board, potentialVisionOccluders);
//						addPVOs(tileX, prevTileY, creature, board, potentialVisionOccluders);
//					}
//				}
//				prevTileX = tileX;
//				prevTileY = tileY;
//			}
//			double[][] rotationMatrix = new double[2][2];
//			rotationMatrix[1][1] = rotationMatrix[0][0] = Math.cos(-visionTotalAngle);
//			rotationMatrix[0][1] = Math.sin(-visionTotalAngle);
//			rotationMatrix[1][0] = -rotationMatrix[0][1];
//			double visionLineLength = MAX_TOUCH_DISTANCE;
//			for (int i = 0; i < potentialVisionOccluders.size(); i++) {
//				SoftBody body = potentialVisionOccluders.get(i);
//				double x = body.px - creature.px;
//				double y = body.py - creature.py;
//				double r = body.getRadius();
//				double translatedX = rotationMatrix[0][0] * x + rotationMatrix[1][0] * y;
//				double translatedY = rotationMatrix[0][1] * x + rotationMatrix[1][1] * y;
//				if (Math.abs(translatedY) <= r) {
//					if ((translatedX >= 0 && translatedX < visionLineLength && translatedY < visionLineLength)
//							|| distance(0, 0, translatedX, translatedY) < r
//							|| distance(visionLineLength, 0, translatedX, translatedY) < r) { // YES! There is an
//																								// occlussion.
//						visionLineLength = translatedX - Math.sqrt(r * r - translatedY * translatedY);
//						
//						distanceDetected = Math.min(distanceDetected, visionLineLength);
//						visionOccludedX[k] = visionStartX + visionLineLength * Math.cos(visionTotalAngle);
//						visionOccludedY[k] = visionStartY + visionLineLength * Math.sin(visionTotalAngle);
//					}
//				}
//			}
//		}
		
		double percentDist = 1 - distanceDetected/MAX_TOUCH_DISTANCE;
		
		HashMap<String, Double> vals = new HashMap<>();
		vals.put(INPUT_NAME, percentDist);
		
		return vals;
	}
	
//	public void addPVOs(int x, int y, Creature creature, Board board, ArrayList<SoftBody> PVOs) {
//		if (x >= 0 && x < board.boardWidth && y >= 0 && y < board.boardHeight) {
//			for (int i = 0; i < board.softBodiesInPositions[x][y].size(); i++) {
//				SoftBody newCollider = (SoftBody) board.softBodiesInPositions[x][y].get(i);
//				if (!PVOs.contains(newCollider) && newCollider != creature) {
//					PVOs.add(newCollider);
//				}
//			}
//		}
//	}

	public double distance(double x1, double y1, double x2, double y2) {
		return (Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1)));
	}
	
//	public double getVisionEndX(double angle, Creature c, double dist) {
//		double visionTotalAngle = c.rotation + angle;
//		return c.px + dist * Math.cos(visionTotalAngle);
//	}
//
//	public double getVisionEndY(double angle, Creature c, double dist) {
//		double visionTotalAngle = c.rotation + angle;
//		return c.py + dist * Math.sin(visionTotalAngle);
//	}

	@Override
	public void preCreatureDraw(Creature c, Board b, float scaleUp, boolean overworldDraw) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postCreatureDraw(Creature creature, Board board, float scaleUp, boolean overworldDraw) {
		if(!overworldDraw) return;
		
		float radius = (float) creature.getRadius();
		float tRadius = scaleUp * ((float)distanceDetected);
		
		EvolvioMod.main.ellipseMode(EvolvioMod.main.RADIUS);
		EvolvioMod.main.pushMatrix();
		EvolvioMod.main.translate((float) (creature.px * scaleUp), (float) (creature.py * scaleUp));
		//EvolvioMod.main.rotate((float) creature.rotation);
		EvolvioMod.main.strokeWeight((float) (TOUCH_PERIMETER_STROKE_WEIGHT / radius));
		EvolvioMod.main.stroke(0, 0, 1, TOUCH_PERIMETER_ALPHA);
		EvolvioMod.main.fill(0,0,0,0);
		EvolvioMod.main.ellipse(0, 0, tRadius, tRadius); // draw the touch perimeter
		
//		// draw the actual touch sensors
//		double maxAngle = 2*Math.PI;
//		double deltaAngle = maxAngle / (double)resolution;
//		for (int k = 0; k < resolution; k++) {
//			float endX = (float) getVisionEndX(k*deltaAngle, creature, distanceDetected) - (float)creature.px;
//			float endY = (float) getVisionEndY(k*deltaAngle, creature, distanceDetected) - (float)creature.py;
//			
//			EvolvioMod.main.fill(0, 0, 1, TOUCH_PERIMETER_ALPHA);
//			EvolvioMod.main.ellipse(scaleUp*endX, scaleUp*endY, (float)TOUCH_PERIMETER_STROKE_WEIGHT/radius, (float)TOUCH_PERIMETER_STROKE_WEIGHT/radius);
//		}
		
		EvolvioMod.main.popMatrix();
	}

}
