package core;

import com.github.ryanp102694.geometry.AbstractRectangleObject;

public class SoftBodyRectangleObject extends AbstractRectangleObject {
	SoftBody reference;
	
	public SoftBodyRectangleObject(SoftBody c) {
		super(c.px-c.getRadius(), c.py-c.getRadius(), c.getRadius()*2f, c.getRadius()*2f);
		this.setId(c.id + "");
		reference = c;
	}
}
