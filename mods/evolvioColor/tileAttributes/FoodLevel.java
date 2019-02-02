package evolvioColor.tileAttributes;

import core.Board;
import core.Tile;
import core.modAPI.TileAttribute;

public class FoodLevel implements TileAttribute<Double> {

	double foodLevel;
    public static final float MAX_GROWTH_LEVEL = 3.0f;
	public static final float FOOD_GROWTH_RATE = 1.0f;
	
	@Override
	public void init(int x, int y, float stepSize, Board b, Tile t) {
		foodLevel = 0;
	}

	@Override
	public String getName() {
		return "foodLevel";
	}

	@Override
	public Double getValue() {
		return foodLevel;
	}

	@Override
	public void setValue(Double v) {
		foodLevel = v;
	}

	@Override
	public void update(double lastUpdateTime, double updateTime, Tile t, Board board) {
		if(Math.abs(lastUpdateTime-updateTime) >= 0.00001){
            double growthChange = board.getGrowthOverTimeRange(lastUpdateTime,updateTime);
            
            double fertility = (Double) t.getAttribute("fertility").getValue();
            double climateType = (Double) t.getAttribute("foodHue").getValue();
            
            
            if(fertility > 1){ // This means the tile is water.
                foodLevel = 0;
            }else{
                if(growthChange > 0){ // Food is growing. Exponentially approach maxGrowthLevel.
                    if(foodLevel < MAX_GROWTH_LEVEL){
                        double newDistToMax = (MAX_GROWTH_LEVEL-foodLevel)*Math.pow(2.71828182846,-growthChange*fertility*FOOD_GROWTH_RATE);
                        double foodGrowthAmount = (MAX_GROWTH_LEVEL-newDistToMax)-foodLevel;
                        addFood(foodGrowthAmount,climateType,false);
                    }
                }else{ // Food is dying off. Exponentially approach 0.
                    removeFood(foodLevel-foodLevel*Math.pow(2.71828182846,growthChange*FOOD_GROWTH_RATE),false);
                }
                /*if(growableTime > 0){
                    if(foodLevel < maxGrowthLevel){
                        double foodGrowthAmount = (maxGrowthLevel-foodLevel)*fertility*FOOD_GROWTH_RATE*timeStep*growableTime;
                        addFood(foodGrowthAmount,climateType);
                    }
                }else{
                    foodLevel += maxGrowthLevel*foodLevel*FOOD_GROWTH_RATE*timeStep*growableTime;
                }*/
            }
            foodLevel = Math.max(foodLevel,0);
            lastUpdateTime = updateTime;
        }
	}
	
	public void addFood(double amount, double addedFoodType, boolean canCauseIteration){
        if(canCauseIteration){
            //iterate();
        	System.err.println("You should've called update() - addFood");
        	for(StackTraceElement s : Thread.currentThread().getStackTrace()) {
        		System.err.println(s);
        	}
        	System.err.println();
        	System.exit(-1);
        }
        foodLevel += amount;
        /*if(foodLevel > 0){
            foodType += (addedFoodType-foodType)*(amount/foodLevel); // We're adding new plant growth, so we gotta "mix" the colors of the tile.
        }*/
    }
    public void removeFood(double amount, boolean canCauseIteration){
        if(canCauseIteration){
        	System.err.println("You should've called update() - removeFood");
        	for(StackTraceElement s : Thread.currentThread().getStackTrace()) {
        		System.err.println(s);
        	}
        	System.err.println();
        	System.exit(-1);
            //iterate();
        }
        foodLevel -= amount;
    }

}
