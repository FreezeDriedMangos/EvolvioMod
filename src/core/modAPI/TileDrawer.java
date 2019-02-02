package core.modAPI;

import core.Tile;

public interface TileDrawer {
	public void draw(Tile t, float scaleUp, boolean showEnergy);
	public int getColor(Tile t);
}
