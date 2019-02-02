package core.modAPI;

import core.Board;
import core.Tile;

public interface TileAttribute<T> {
    
    public void init(int x, int y, float stepSize, Board b, Tile t);
    
    public String getName();
    public T getValue();
    public void setValue(T v);
    
    public void update(double lastUpdateTime, double updateTime, Tile t, Board b);
}
