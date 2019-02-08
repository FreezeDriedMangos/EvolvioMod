package carnivory;

import core.Board;
import core.Tile;
import core.modAPI.TileAttribute;
import evolvioOriginal.FoodLevel;

public class MeatLevel implements TileAttribute<Double> {
	double val;
	
	@Override
	public void init(int x, int y, float stepSize, Board b, Tile t) {
		val = 0;
	}

	@Override
	public String getName() {
		return "meatLevel";
	}

	@Override
	public Double getValue() {
		return val;
	}

	@Override
	public void setValue(Double v) {
		val = v;
	}

	@Override
	public void update(double lastUpdateTime, double updateTime, Tile t, Board board) {
		if(Math.abs(lastUpdateTime-updateTime) >= 0.00001){
            double growthChange = board.getGrowthOverTimeRange(lastUpdateTime,updateTime);
            
            
                if(growthChange > 0){ // It's warm enough for grass to grow, meat exponentially decays
                	val /= Math.pow(2.71828182846,growthChange*FoodLevel.FOOD_GROWTH_RATE);
                } else { // It's cold enough for grass to freeze to death, meat is preserved by the cold
                    
                }
                /*if(growableTime > 0){
                    if(foodLevel < maxGrowthLevel){
                        double foodGrowthAmount = (maxGrowthLevel-foodLevel)*fertility*FOOD_GROWTH_RATE*timeStep*growableTime;
                        addFood(foodGrowthAmount,climateType);
                    }
                }else{
                    foodLevel += maxGrowthLevel*foodLevel*FOOD_GROWTH_RATE*timeStep*growableTime;
                }*/
            
            val = Math.max(val,0);
        }
	}
}
