package evolvioOriginal;

import core.Creature;
import core.Tile;
import core.modAPI.CreatureEatBehavior;
import processing.core.PApplet;

public class OriginalEatBehavior implements CreatureEatBehavior {
	double EAT_ENERGY = 0.05;
	  
	// TODO: make these CreatureAttributes
	double EAT_WHILE_MOVING_INEFFICIENCY_MULTIPLIER = 2.0; // The bigger this number is, the less effiently creatures eat when they're moving.
	double EAT_SPEED = 0.5; // 1 is instant, 0 is nonexistent, 0.001 is verrry slow.
	final double FOOD_SENSITIVITY = 0.3;
	  

	int cnt = 0;
	double avgAttemptAmountRatio = 0;
	double avgOver = 10;
	@Override
	public double getCreatureEatAmount(Creature c, Tile t, double attemptedAmount, double timeStep) {
		double amt = attemptedAmount/(1.0+PApplet.dist(0,0,(float)c.getVX(),(float)c.getVY())*EAT_WHILE_MOVING_INEFFICIENCY_MULTIPLIER); // The faster you're moving, the less efficiently you can eat.
		
//		avgAttemptAmountRatio += (attemptedAmount/amt)/avgOver;
//		cnt++;
//		cnt %= avgOver;
//		if(cnt == 0) {
//			System.out.println("Average Eat Attempt to Actual Amount Ratio: " + avgAttemptAmountRatio);
//			avgAttemptAmountRatio = 0;
//			
//			// seems to hover around 1.5, between 1.2 and 1.6
//		}
		
		
		return amt;
		//return attemptedAmount/(1.0+PApplet.dist(0,0,(float)c.getVX(),(float)c.getVY())*EAT_WHILE_MOVING_INEFFICIENCY_MULTIPLIER); // The faster you're moving, the less efficiently you can eat.
	
	
	}

	@Override
	public void creatureEatFromTile(Creature c, Tile t, double amount, double attemptedAmount, double timeStep) {

		double tFoodLevel = (Double)t.getAttribute("foodLevel").getValue();
		
		double foodToEat = tFoodLevel*(1-Math.pow((1-EAT_SPEED),amount*timeStep));
        if(foodToEat > tFoodLevel){
          foodToEat = tFoodLevel;
        }
        //t.removeFood(foodToEat, true);
        t.update();
        ((FoodLevel)t.getAttribute("foodLevel")).removeFood(foodToEat, false);
        
        c.addEnergy(foodToEat);
        c.loseEnergy(attemptedAmount*EAT_ENERGY*timeStep);
	}

	@Override
	public void creatureFailToEatFromTile(Creature c, Tile t, double amount, double attemptedAmount, double timeStep) {
        c.dropEnergy(-amount*timeStep);
        c.loseEnergy(-attemptedAmount*EAT_ENERGY*timeStep);
	}

	@Override
	public void creatureDepositFoodToTile(Creature c, Tile t, double amount) {
		t.update();
	
		FoodLevel l = (FoodLevel) t.getAttribute("foodLevel");
		l.addFood(amount, false);
	}

}
