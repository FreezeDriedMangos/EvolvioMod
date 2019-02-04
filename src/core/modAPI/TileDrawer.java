package core.modAPI;

import core.Tile;

public interface TileDrawer {
	public void draw(Tile t, float scaleUp);
	public void drawBlendLayer(Tile t, float scaleUp);
	public void drawInformation(Tile t, float scaleUp);
	
	public int getColor(Tile t);
}
