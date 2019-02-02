package core.modAPI;

import core.Board;
import core.Tile;
import core.Creature;

public interface CreatureEatBehavior {
    public double getCreatureEatAmount(Creature c, Tile t, double attemptedAmount, double timeStep);
    public void creatureEatFromTile(Creature c, Tile t, double amount, double attemptedAmount, double timeStep);
    public void creatureFailToEatFromTile(Creature c, Tile t, double amount, double attemptedAmount, double timeStep);

    public void creatureDepositFoodToTile(Creature c, Tile t, double amount);
}
