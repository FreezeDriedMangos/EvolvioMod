package evolvioOriginal;

import core.EvolvioMod;
import core.Tile;
import core.modAPI.Button;
import core.modAPI.TileDrawer;

public class OriginalTileDrawer implements TileDrawer, Button {

	private static final float FOOD_TRANSPARENCY = 0.75f;
	
	public final int FERTILE_COLOR = EvolvioMod.main.color(31f/360f, 0.4f, 0.1f);//0.2f);
	public final int BARREN_COLOR = EvolvioMod.main.color(31f/360f, 0.56f, 0.82f);
	public final int GRASS_COLOR = EvolvioMod.main.color(112f/360f, 0.8f, 0.35f);
	public final int MEAT_COLOR = EvolvioMod.main.color(349f/360f, 0.98f, 0.35f);
	public final int WATER_COLOR = EvolvioMod.main.color(244f/360f, 0.73f,0.56f);
	
	static boolean drawTileBorders = false;
    public float blendRadius = 0.1f;
	
	@Override
	public void draw(Tile t, float scaleUp) {
		EvolvioMod.main.stroke(0, 0, 0, 1);
		EvolvioMod.main.strokeWeight(2);
		if(!drawTileBorders) { EvolvioMod.main.noStroke(); }
		
		float tileSize = drawTileBorders? 1 : 1.1f;
		int landColor = getColor(t);
		EvolvioMod.main.fill(landColor);
		EvolvioMod.main.rect(t.getPosX() * scaleUp, t.getPosY() * scaleUp, tileSize * scaleUp, tileSize * scaleUp);
	}

	@Override
	public void drawBlendLayer(Tile t, float scaleUp) {
		if(t.isWater()) {return;}
		
		// Purely visual thing for the user of the program. The creatures won't see this blending
		float tileSize = drawTileBorders? 1 : 1;//1.1f;
		int color = getColor(t);
		color = EvolvioMod.main.color(EvolvioMod.main.hue(color), EvolvioMod.main.saturation(color), EvolvioMod.main.brightness(color), 0.25f);
		EvolvioMod.main.fill(color);
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
		
		EvolvioMod.main.textAlign(EvolvioMod.main.CENTER);
		EvolvioMod.main.textFont(EvolvioMod.main.font, /* 21 */35 * EvolvioMod.main.WINDOW_SCALE());
		EvolvioMod.main.text(EvolvioMod.main.nf((float) (100 * foodLevel), 0, 2) + " yums",
				(t.getPosX() + 0.5f) * scaleUp, (t.getPosY() + 0.3f) * scaleUp);
	}

	@Override
	public int getColor(Tile t) {
		if(t.isWater()) {
			return WATER_COLOR;
		}
		
		float fertility = (float)((Double)t.getAttribute("fertility").getValue()).doubleValue();
		double grassLevel = (Double)t.getAttribute("foodLevel").getValue();
		
		int color = BARREN_COLOR;
		color = EvolvioMod.main.lerpColor(color, FERTILE_COLOR, fertility, EvolvioMod.main.RGB);
		color = EvolvioMod.main.lerpColor(color, GRASS_COLOR, FOOD_TRANSPARENCY * (float)(grassLevel/FoodLevel.MAX_GROWTH_LEVEL), EvolvioMod.main.RGB);
		
		return color;
	}
	
	@Override
	public void click(int relX, int relY) {
		OriginalTileDrawer.drawTileBorders = !OriginalTileDrawer.drawTileBorders;
	}

	@Override
	public String getText() {
		return "Toggle Tile Borders";
	}

	@Override
	public String getSecondLineText() {
		return OriginalTileDrawer.drawTileBorders ? "On" : "Off";
	}

	@Override public float getFlashAlpha() { return 0; }

	@Override public void init() { }
}