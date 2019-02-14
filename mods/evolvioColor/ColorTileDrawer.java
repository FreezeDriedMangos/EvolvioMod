package evolvioColor;

import core.EvolvioMod;
import core.Tile;
import core.modAPI.Button;
import core.modAPI.TileDrawer;
import evolvioOriginal.FoodLevel;

public class ColorTileDrawer implements TileDrawer, Button {
    public final int barrenColor = EvolvioMod.main.color(0,0,1);
    public final int fertileColor = EvolvioMod.main.color(0,0,0.2f);
    public final int blackColor = EvolvioMod.main.color(0,1,0);
    public final int waterColor = barrenColor;//EvolvioMod.main.color(0,0,0);
    // the original water color may have been confusing the creatures, as super firtile was
    // the same as water - the epitome of infertile. So I changed it up and made water color the
    // same as barren - aka infirtile
    
    public static boolean drawTileBorders = false;
    public boolean blendAdjacent = true;
    public float blendRadius = 0.1f;
    
	@Override
	public void draw(Tile t, float scaleUp) {
		EvolvioMod.main.stroke(0, 0, 0, 1);
		EvolvioMod.main.strokeWeight(2);
		if(!drawTileBorders) { EvolvioMod.main.noStroke(); }
		
		float tileSize = drawTileBorders? 1 : 1.1f;
		int landColor = getColor(t);
		if(t.isWater()) landColor = EvolvioMod.main.color(0); // draw water as black for user clairity, even though creatures see it as white
		
		EvolvioMod.main.fill(landColor);
		EvolvioMod.main.rect(t.getPosX() * scaleUp, t.getPosY() * scaleUp, tileSize * scaleUp, tileSize * scaleUp);
	}

	@Override
	public void drawBlendLayer(Tile t, float scaleUp) {
		if(t.isWater()) {return;}
		
		// Purely visual thing for the user of the program. The creatures won't see this blending
		float tileSize = drawTileBorders? 1 : 1;//1.1f;
		EvolvioMod.main.fill(getColor(t, 0.25f));
		EvolvioMod.main.rect((t.getPosX() - blendRadius) * scaleUp, (t.getPosY() - blendRadius) * scaleUp, (tileSize + 2*blendRadius) * scaleUp, (tileSize + 2*blendRadius)* scaleUp);
	}
	
	@Override
	public void drawInformation(Tile t, float scaleUp) {
		int landColor = getColor(t);
		if (EvolvioMod.main.brightness(landColor) >= 0.7) {
			EvolvioMod.main.fill(0, 0, 0, 1);
		} else {
			EvolvioMod.main.fill(0, 0, 1, 1);
		}

		double foodLevel = (Double) t.getAttribute("foodLevel").getValue();
		double foodType = (Double) t.getAttribute("foodHue").getValue();
		double climateType = foodType;//(Double) t.getAttribute("climateType").getValue();
		
		EvolvioMod.main.textAlign(EvolvioMod.main.CENTER);
		EvolvioMod.main.textFont(EvolvioMod.main.font, /* 21 */35 * EvolvioMod.main.WINDOW_SCALE());
		EvolvioMod.main.text(EvolvioMod.main.nf((float) (100 * foodLevel), 0, 2) + " yums",
				(t.getPosX() + 0.5f) * scaleUp, (t.getPosY() + 0.3f) * scaleUp);
		EvolvioMod.main.text("Clim: " + EvolvioMod.main.nf((float) (climateType), 0, 2),
				(t.getPosX() + 0.5f) * scaleUp, (t.getPosY() + 0.6f) * scaleUp);
		EvolvioMod.main.text("Food: " + EvolvioMod.main.nf((float) (foodType), 0, 2),
				(t.getPosX() + 0.5f) * scaleUp, (t.getPosY() + 0.9f) * scaleUp);
	}

	@Override
	public int getColor(Tile t) {
		return getColor(t, 1.0f);
	}
	
	public int getColor(Tile t, float alpha) {
		double fertility = (Double)t.getAttribute("fertility").getValue();
		double foodLevel = (Double)t.getAttribute("foodLevel").getValue();
		double foodHue   = (Double)t.getAttribute("foodHue").  getValue();
		
		
		int foodColor = EvolvioMod.main.color((float) (foodHue), 1, 1);
		if (t.isWater()) {
			return waterColor;
		} else if (foodLevel < FoodLevel.MAX_GROWTH_LEVEL) {
			return interColorFixedHue(interColor(barrenColor, fertileColor, fertility, alpha), foodColor,
					foodLevel / FoodLevel.MAX_GROWTH_LEVEL, EvolvioMod.main.hue(foodColor), alpha);
		} else {
			return interColorFixedHue(foodColor, blackColor, 1.0 - FoodLevel.MAX_GROWTH_LEVEL / foodLevel,
					EvolvioMod.main.hue(foodColor), alpha);
		}
	}
	
	public int interColor(int a, int b, double x, float alpha){
        double hue = inter(EvolvioMod.main.hue(a),EvolvioMod.main.hue(b),x);
        double sat = inter(EvolvioMod.main.saturation(a),EvolvioMod.main.saturation(b),x);
        double bri = inter(EvolvioMod.main.brightness(a),EvolvioMod.main.brightness(b),x); // I know it's dumb to do interpolation with HSL but oh well
        return EvolvioMod.main.color((float)(hue),(float)(sat),(float)(bri), alpha);
    }
    public int interColorFixedHue(int a, int b, double x, double hue, float alpha){
        double satB = EvolvioMod.main.saturation(b);
        if(EvolvioMod.main.brightness(b) == 0){ // I want black to be calculated as 100% saturation
            satB = 1;
        }
        double sat = inter(EvolvioMod.main.saturation(a),satB,x);
        double bri = inter(EvolvioMod.main.brightness(a),EvolvioMod.main.brightness(b),x); // I know it's dumb to do interpolation with HSL but oh well
        return EvolvioMod.main.color((float)(hue),(float)(sat),(float)(bri), alpha);
    }
    public double inter(double a, double b, double x){
        return a + (b-a)*x;
    }
    
    @Override
	public void click(int relX, int relY) {
		ColorTileDrawer.drawTileBorders = !ColorTileDrawer.drawTileBorders;
	}

	@Override
	public String getText() {
		return "Toggle Tile Borders";
	}

	@Override
	public String getSecondLineText() {
		return ColorTileDrawer.drawTileBorders ? "On" : "Off";
	}

	@Override public float getFlashAlpha() { return 0; }

	@Override public void init() { }
}
