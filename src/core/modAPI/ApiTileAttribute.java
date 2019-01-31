package core.modAPI;

import core.Board;
import core.Tile;

public interface ApiTileAttribute {
    
    public void init(int x, int y, Board b, Tile t);
    
    public String getName();
    public Object getValue();
    public void setValue(Object v);
    public Class getType();
    
    public void update(double lastUpdateTime, double updateTime, Tile t);
}
