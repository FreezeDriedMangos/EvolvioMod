package core.modAPI;

import java.util.ArrayList;

import core.Board;
import core.Creature;

public interface CreatureAttribute<T> {
    
    public void init(Board b, Creature c);
    
    public String getName();
    public T getValue();
    public void setValue(T v);
    
    public void update(double lastUpdateTime, double updateTime, Creature c, Board b);

	public void initFromParents(ArrayList<CreatureAttribute> parentAttributes, Board board);
	
	//TODO: future update: speciation
//	public float getSpeciesDelta(Creature other);
//	/**
//	 * Two creatures will be allowed to mate if all attributes and brains in both creatures return true here
//	 * @param totalSpeciesDelta = brain.getSpeciesDelta(other.brain) * 0.5 + creatureAttributesSpeciesDelta_Sum * 0.5 
//	 * @return
//	 */
//	public boolean canMate(float totalSpeciesDelta);
}
