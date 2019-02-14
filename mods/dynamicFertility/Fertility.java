package dynamicFertility;

import core.Board;
import core.EvolvioMod;
import core.Tile;
import core.modAPI.TileAttribute;
import processing.core.PApplet;

public class Fertility implements TileAttribute<Double> {

	private double fluxPeriod = 1.2;
	private double periodOffset;
	
	double baseVal = 0;
	double val;
	
	
	@Override
	public void init(int x, int y, float stepSize, Board b, Tile t) {
		// taken from Carykh's evolvioOriginal
		float bigForce = PApplet.pow(((float)y)/b.getHeight(),0.5f);
        baseVal = EvolvioMod.main.noise(x*stepSize*3,y*stepSize*3)*(1-bigForce)*5.0f+EvolvioMod.main.noise(x*stepSize*0.5f,y*stepSize*0.5f)*bigForce*5.0f-1.5f;
        val = baseVal;
        
        periodOffset = bigForce*10*EvolvioMod.main.noise(-x*stepSize*3,-y*stepSize*3);//baseVal * (x-y);
        fluxPeriod = 1+EvolvioMod.main.noise(-x*stepSize,y*stepSize);
	}

	@Override
	public String getName() {
		return "fertility";
	}

	@Override
	public Double getValue() {
		return val;
	}

	@Override
	public void setValue(Double v) {
		baseVal = v;
	}

	@Override
	public void update(double lastUpdateTime, double updateTime, Tile t, Board b) {
		double periodNormalizer = 10 / (2*Math.PI);
		double input = b.getYear() + periodOffset;
		double cos = Math.cos(fluxPeriod * input * periodNormalizer);
		double flux = (cos*0.25) + 0.75;
		
		val = baseVal * flux;
	}
}
