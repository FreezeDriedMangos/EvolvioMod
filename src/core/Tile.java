package core;

import java.util.HashMap;
import core.modAPI.TileAttribute;

public class Tile{
//    public final float FOOD_GROWTH_RATE = 1.0f;
    
    //double fertility;
    //double foodLevel;
    //final float maxGrowthLevel = 3.0f;
    int posX;
    int posY;
    double lastUpdateTime = 0;
    boolean isWater = false;
    
    public double climateType;
    public double foodHue;
    
    HashMap<String, TileAttribute> attributes = new HashMap();
    
    Board board;
    
    public Tile(int x, int y, double f, float food, float type, Board b, float stepSize){
        posX = x;
        posY = y;
        //fertility = Math.max(0,f);
        //foodLevel = Math.max(0,food);
        climateType = foodHue = type;
        board = b;
        
        isWater = f > 1;
        
        ModLoader.initializeAttributes(this, b, stepSize);
    }
    
//    public double getFertility(){
//        return fertility;
//    }
//    public double getFoodLevel(){
//        return foodLevel;
//    }
//    public void setFertility(double f){
//        fertility = f;
//    }
//    public void setFoodLevel(double f){
//        foodLevel = f;
//    }
    
    public TileAttribute getAttribute(String name) {
    	return attributes.get(name);
    }
    
    public void drawTile(float scaleUp, boolean showEnergy){
    	update();
    	ModLoader.drawTile(this, scaleUp, showEnergy);
    	
    	// TODO: put this in the evolvioColor mod
//    	EvolvioMod.main.stroke(0,0,0,1);
//    	EvolvioMod.main.strokeWeight(2);
//        int landColor = getColor();
//        EvolvioMod.main.fill(landColor);
//        EvolvioMod.main.rect(posX*scaleUp,posY*scaleUp,scaleUp,scaleUp);
//        if(showEnergy){
//            if(EvolvioMod.main.brightness(landColor) >= 0.7){
//            	EvolvioMod.main.fill(0,0,0,1);
//            }else{
//            	EvolvioMod.main.fill(0,0,1,1);
//            }
//            EvolvioMod.main.textAlign(EvolvioMod.main.CENTER);
//            EvolvioMod.main.textFont(EvolvioMod.main.font,/*21*/35*EvolvioMod.main.WINDOW_SCALE());
//            EvolvioMod.main.text(EvolvioMod.main.nf((float)(100*foodLevel),0,2)+" yums",(posX+0.5f)*scaleUp,(posY+0.3f)*scaleUp);
//            EvolvioMod.main.text("Clim: "+EvolvioMod.main.nf((float)(climateType),0,2),(posX+0.5f)*scaleUp,(posY+0.6f)*scaleUp);
//            EvolvioMod.main.text("Food: "+EvolvioMod.main.nf((float)(foodHue),0,2),(posX+0.5f)*scaleUp,(posY+0.9f)*scaleUp);
//        }
    }
    
    // TODO: make board call this function on update
    public void update(){
        double updateTime = board.year;
        
        for(TileAttribute<?> at : attributes.values()) {
        	at.update(lastUpdateTime, updateTime, this, board);
        }
        lastUpdateTime = updateTime;
        
        // TODO: remove this and put it in a few tile attributes
//        if(Math.abs(lastUpdateTime-updateTime) >= 0.00001){
//            double growthChange = board.getGrowthOverTimeRange(lastUpdateTime,updateTime);
//            if(fertility > 1){ // This means the tile is water.
//                foodLevel = 0;
//            }else{
//                if(growthChange > 0){ // Food is growing. Exponentially approach maxGrowthLevel.
//                    if(foodLevel < maxGrowthLevel){
//                        double newDistToMax = (maxGrowthLevel-foodLevel)*Math.pow(2.71828182846,-growthChange*fertility*FOOD_GROWTH_RATE);
//                        double foodGrowthAmount = (maxGrowthLevel-newDistToMax)-foodLevel;
//                        addFood(foodGrowthAmount,climateType,false);
//                    }
//                }else{ // Food is dying off. Exponentially approach 0.
//                    removeFood(foodLevel-foodLevel*Math.pow(2.71828182846,growthChange*FOOD_GROWTH_RATE),false);
//                }
//                /*if(growableTime > 0){
//                    if(foodLevel < maxGrowthLevel){
//                        double foodGrowthAmount = (maxGrowthLevel-foodLevel)*fertility*FOOD_GROWTH_RATE*timeStep*growableTime;
//                        addFood(foodGrowthAmount,climateType);
//                    }
//                }else{
//                    foodLevel += maxGrowthLevel*foodLevel*FOOD_GROWTH_RATE*timeStep*growableTime;
//                }*/
//            }
//            foodLevel = Math.max(foodLevel,0);
//            lastUpdateTime = updateTime;
//        }
    }
//    public void addFood(double amount, double addedFoodType, boolean canCauseIteration){
//        if(canCauseIteration){
//            iterate();
//        }
//        foodLevel += amount;
//        /*if(foodLevel > 0){
//            foodType += (addedFoodType-foodType)*(amount/foodLevel); // We're adding new plant growth, so we gotta "mix" the colors of the tile.
//        }*/
//    }
//    public void removeFood(double amount, boolean canCauseIteration){
//        if(canCauseIteration){
//            iterate();
//        }
//        foodLevel -= amount;
//    }
    
//    public int getColor(){
//        int foodColor = EvolvioMod.main.color((float)(foodHue),1,1);
//        if(fertility > 1){
//            return waterColor;
//        }else if(foodLevel < maxGrowthLevel){
//            return interColorFixedHue(interColor(barrenColor,fertileColor,fertility),foodColor,foodLevel/maxGrowthLevel,EvolvioMod.main.hue(foodColor));
//        }else{
//            return interColorFixedHue(foodColor,blackColor,1.0-maxGrowthLevel/foodLevel,EvolvioMod.main.hue(foodColor));
//        }
//    }
    
    

    public int getPosX() {
		return posX;
	}
    public int getPosY() {
		return posY;
	}

	public boolean isWater() {
		return isWater;
	}
}
