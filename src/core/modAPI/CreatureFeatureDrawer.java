package core.modAPI;

import core.Board;
import core.Creature;

public interface CreatureFeatureDrawer {
	public void preCreatureDraw(Creature c, Board b, float scaleUp, float camZoom, boolean overworldDraw);
	public void postCreatureDraw(Creature c, Board b, float scaleUp, float camZoom, boolean overworldDraw);
}
