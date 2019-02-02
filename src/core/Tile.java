package core;

import java.util.HashMap;
import core.modAPI.TileAttribute;

public class Tile{
    public final int barrenColor = EvolvioMod.main.color(0,0,1);
    public final int fertileColor = EvolvioMod.main.color(0,0,0.2f);
    public final int blackColor = EvolvioMod.main.color(0,1,0);
    public final int waterColor = EvolvioMod.main.color(0,0,0);
    public final float FOOD_GROWTH_RATE = 1.0f;
    
    double fertility;
    double foodLevel;
    final float maxGrowthLevel = 3.0f;
    int posX;
    int posY;
    double lastUpdateTime = 0;
    
    public double climateType;
    public double foodType;
    
    HashMap<String, TileAttribute> attributes = new HashMap();
    
    Board board;
    
    public Tile(int x, int y, double f, float food, float type, Board b){
        posX = x;
        posY = y;
        fertility = Math.max(0,f);
        foodLevel = Math.max(0,food);
        climateType = foodType = type;
        board = b;
    }
    
    public double getFertility(){
        return fertility;
    }
    public double getFoodLevel(){
        return foodLevel;
    }
    public void setFertility(double f){
        fertility = f;
    }
    public void setFoodLevel(double f){
        foodLevel = f;
    }
    
    public void setAttribute(String name, Object value) {
            attributes.get(name).setValue(value);
    }
    public Object getAttribute(String name) {
            return attributes.get(name).getValue();
    }
    public Class getAttributeType(String name) {
            return attributes.get(name).getType();
    }
    
    public void drawTile(float scaleUp, boolean showEnergy){
    	EvolvioMod.main.stroke(0,0,0,1);
    	EvolvioMod.main.strokeWeight(2);
        int landColor = getColor();
        EvolvioMod.main.fill(landColor);
        EvolvioMod.main.rect(posX*scaleUp,posY*scaleUp,scaleUp,scaleUp);
        if(showEnergy){
            if(EvolvioMod.main.brightness(landColor) >= 0.7){
            	EvolvioMod.main.fill(0,0,0,1);
            }else{
            	EvolvioMod.main.fill(0,0,1,1);
            }
            EvolvioMod.main.textAlign(EvolvioMod.main.CENTER);
            EvolvioMod.main.textFont(EvolvioMod.main.font,/*21*/35*EvolvioMod.main.WINDOW_SCALE());
            EvolvioMod.main.text(EvolvioMod.main.nf((float)(100*foodLevel),0,2)+" yums",(posX+0.5f)*scaleUp,(posY+0.3f)*scaleUp);
            EvolvioMod.main.text("Clim: "+EvolvioMod.main.nf((float)(climateType),0,2),(posX+0.5f)*scaleUp,(posY+0.6f)*scaleUp);
            EvolvioMod.main.text("Food: "+EvolvioMod.main.nf((float)(foodType),0,2),(posX+0.5f)*scaleUp,(posY+0.9f)*scaleUp);
        }
    }
    public void iterate(){
        double updateTime = board.year;
        if(Math.abs(lastUpdateTime-updateTime) >= 0.00001){
            double growthChange = board.getGrowthOverTimeRange(lastUpdateTime,updateTime);
            if(fertility > 1){ // This means the tile is water.
                foodLevel = 0;
            }else{
                if(growthChange > 0){ // Food is growing. Exponentially approach maxGrowthLevel.
                    if(foodLevel < maxGrowthLevel){
                        double newDistToMax = (maxGrowthLevel-foodLevel)*Math.pow(2.71828182846,-growthChange*fertility*FOOD_GROWTH_RATE);
                        double foodGrowthAmount = (maxGrowthLevel-newDistToMax)-foodLevel;
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
            iterate();
        }
        foodLevel += amount;
        /*if(foodLevel > 0){
            foodType += (addedFoodType-foodType)*(amount/foodLevel); // We're adding new plant growth, so we gotta "mix" the colors of the tile.
        }*/
    }
    public void removeFood(double amount, boolean canCauseIteration){
        if(canCauseIteration){
            iterate();
        }
        foodLevel -= amount;
    }
    public int getColor(){
        iterate();
        int foodColor = EvolvioMod.main.color((float)(foodType),1,1);
        if(fertility > 1){
            return waterColor;
        }else if(foodLevel < maxGrowthLevel){
            return interColorFixedHue(interColor(barrenColor,fertileColor,fertility),foodColor,foodLevel/maxGrowthLevel,EvolvioMod.main.hue(foodColor));
        }else{
            return interColorFixedHue(foodColor,blackColor,1.0-maxGrowthLevel/foodLevel,EvolvioMod.main.hue(foodColor));
        }
    }
    public int interColor(int a, int b, double x){
        double hue = inter(EvolvioMod.main.hue(a),EvolvioMod.main.hue(b),x);
        double sat = inter(EvolvioMod.main.saturation(a),EvolvioMod.main.saturation(b),x);
        double bri = inter(EvolvioMod.main.brightness(a),EvolvioMod.main.brightness(b),x); // I know it's dumb to do interpolation with HSL but oh well
        return EvolvioMod.main.color((float)(hue),(float)(sat),(float)(bri));
    }
    public int interColorFixedHue(int a, int b, double x, double hue){
        double satB = EvolvioMod.main.saturation(b);
        if(EvolvioMod.main.brightness(b) == 0){ // I want black to be calculated as 100% saturation
            satB = 1;
        }
        double sat = inter(EvolvioMod.main.saturation(a),satB,x);
        double bri = inter(EvolvioMod.main.brightness(a),EvolvioMod.main.brightness(b),x); // I know it's dumb to do interpolation with HSL but oh well
        return EvolvioMod.main.color((float)(hue),(float)(sat),(float)(bri));
    }
    public double inter(double a, double b, double x){
        return a + (b-a)*x;
    }
}
