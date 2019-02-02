package evolvioColor.tileAttributes;

import core.Board;
import core.EvolvioMod;
import core.Tile;
import core.modAPI.TileAttribute;
import processing.core.PApplet;

public class FoodHue implements TileAttribute<Double> {

	double hue;
	
	@Override
	public void init(int x, int y, float stepSize, Board b, Tile t) {
        float climateType = EvolvioMod.main.noise(x*stepSize*0.2f+10000,y*stepSize*0.2f+10000)*1.63f-0.4f;
        climateType = Math.min(Math.max(climateType,0),0.8f);
        
        hue = climateType;
	}

	@Override
	public String getName() {
		return "foodHue";
	}

	@Override
	public Double getValue() {
		return hue;
	}

	@Override
	public void setValue(Double v) {
		hue = v;
	}

	@Override
	public void update(double lastUpdateTime, double updateTime, Tile t, Board b) {
		// tile hue doesn't change
	}

}
