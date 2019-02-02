package core.modAPI;

import core.Board;
import core.Creature;

public interface CreatureAttribute {
    
    public void init(Board b, Creature c);
    
    public String getName();
    public Object getValue();
    public void setValue(Object v);
    public Class getType();
    
    public void update(double lastUpdateTime, double updateTime, Creature c);   
}
