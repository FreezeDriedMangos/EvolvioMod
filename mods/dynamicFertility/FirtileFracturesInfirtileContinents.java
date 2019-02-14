package dynamicFertility;

import core.Board;
import core.EvolvioMod;
import core.Tile;
import core.modAPI.TileAttribute;
import processing.core.PApplet;

public class FirtileFracturesInfirtileContinents implements TileAttribute<Double> {

	double fertility;
	
	@Override
	public void init(int x, int y, float stepSize, Board b, Tile t) {
		float bigForce = PApplet.pow(((float)y)/b.getHeight(),0.5f);
        fertility = EvolvioMod.main.noise(x*stepSize*3,y*stepSize*3)*(1-bigForce)*5.0f+EvolvioMod.main.noise(x*stepSize*0.5f,y*stepSize*0.5f)*bigForce*5.0f-1.5f;
        
        double distFromTop = (double)y / (double)b.boardHeight;
        distFromTop = 1f - distFromTop;
        fertility *= distFromTop;
	}

	@Override
	public String getName() {
		return "fertility";
	}

	@Override
	public Double getValue() {
		return fertility;
	}

	@Override
	public void setValue(Double v) {
		fertility = v;
	}

	@Override
	public void update(double lastUpdateTime, double updateTime, Tile t, Board b) {
		// tile fertility doesn't change
	}
}