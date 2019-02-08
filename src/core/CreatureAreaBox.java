package core;

import com.pelletier.geometry.AbstractRectangleObject;

/**
 * Not to be confused with a hitbox. Creatures actually have circular hitboxes, however the quadtree implementation
 * that I'm using requires rectangles. Therefore, I called this class an "AreaBox"
 * @author clay
 *
 */
public class CreatureAreaBox extends AbstractRectangleObject {

	public CreatureAreaBox(Creature c) {
		super(c.px, c.py, c.getRadius()*2, c.getRadius()*2);
		super.setId(""+c.id);
	}
}
