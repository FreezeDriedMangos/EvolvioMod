package core.modAPI;

import core.Board;
import core.Tile;
import core.Creature;

public interface ApiCreatureEatBehavior {
    public double getCreatureEatAmount(Creature c, Tile t, double attemptedAmount, double timeStep);
    public void creatureEatFromTile(Creature c, Tile t, double amount, double timeStep);
    public void creatureFailToEatFromTile(Creature c, Tile t, double amount, double timeStep);
}
