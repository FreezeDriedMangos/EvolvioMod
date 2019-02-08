package carnivory;

import core.Creature;
import core.Tile;
import core.modAPI.CreatureAttribute;
import core.modAPI.CreatureEatBehavior;
import core.modAPI.TileAttribute;
import evolvioOriginal.FoodLevel;
import processing.core.PApplet;

public class CarnivoryEatBehavior implements CreatureEatBehavior {
	private static final float TYPICAL_MAX_MEAT_LEVEL = 1.0f;
	private static final double MEAT_ENERGY_SCALAR = 2f * FoodLevel.MAX_GROWTH_LEVEL/TYPICAL_MAX_MEAT_LEVEL; // this allows carnivores to experience the same delights of eating a full meal as herbivores do
	public static final double MEAT_IMPORTANCE_SCALAR = 10;
	private static final double GRASS_EATING_EFFICIENCY = 0.5;
	
	double EAT_WHILE_MOVING_INEFFICIENCY_MULTIPLIER = 2.0; // The bigger this number is, the less effiently creatures eat when they're moving.
	public double EAT_SPEED = 0.5;
	double EAT_ENERGY = 0.05;
	
	@Override
	public double getCreatureEatAmount(Creature c, Tile t, double attemptedAmount, double timeStep) {
		attemptedAmount = attemptedAmount/(1.0+PApplet.dist(0,0,(float)c.getVX(),(float)c.getVY())*EAT_WHILE_MOVING_INEFFICIENCY_MULTIPLIER); // The faster you're moving, the less efficiently you can eat.
		
		double carnivory = (Double)c.getAttribute("carnivory").getValue();
		double grassLevel = (Double)t.getAttribute("foodLevel").getValue();
		double meatLevel = (Double)t.getAttribute("meatLevel").getValue();
		double totalFood = grassLevel + meatLevel;
		
		attemptedAmount *= getEatingEfficiencyMultiplier(carnivory, grassLevel, meatLevel);
		return attemptedAmount;
	}
	
	// desmos equation where c=carnivory, g=grassLevel, m=meatLevel, and t_{food}=m+g :  
	// a=\frac{\left(1+\left\{c=0:0,c<0:\frac{g}{t_{food}}\cdot\left(-c\right)+\frac{m}{t_{food}}\cdot c,c>0:\frac{m}{t_{food}}\cdot\left(c\right)-\frac{g}{t_{food}}\cdot c\right\}\right)}{2}
	public double getEatingEfficiencyMultiplier(double carnivory, double grassLevel, double meatLevel) {
		grassLevel /= MEAT_IMPORTANCE_SCALAR;
		
		double totalLevel = grassLevel + meatLevel;
		if(totalLevel == 0) return 1;
		
		double grassPercent = grassLevel / totalLevel;
		double meatPercent = meatLevel / totalLevel;
		
		double multiplier = 1;
		if(carnivory < 0) {
			multiplier += grassPercent*(-carnivory) + meatPercent*carnivory;
		} else if (carnivory > 0) {
			multiplier += meatPercent*(carnivory) - grassPercent*carnivory;
		} else {
			multiplier += 0;
		}
		multiplier /= 2f;
		
		return multiplier;
	}

	@Override
	public void creatureEatFromTile(Creature c, Tile t, double amount, double attemptedAmount, double timeStep) {
		// TODO Auto-generated method stub
		
		// removes grass food from the tile proportional to 1-carnivory and meat food proportional to 1+carnivory
		
		// hurts the creature by grassLevel*(-carnivory) and meatLevel*(carnivory), but ignores either term if it's positive
		
		// helps the creature by grassLevel*(1-carnivory) and meatLevel*(1+carnivory), but ignores either term if it's positive
		
		double carnivory = (Double)c.getAttribute("carnivory").getValue();
		double grassLevel = (Double)t.getAttribute("foodLevel").getValue();
		double meatLevel = (Double)t.getAttribute("meatLevel").getValue();
		double totalFood = grassLevel + meatLevel;
		
		if(totalFood == 0) totalFood = 1;
		
		double meatPercent = meatLevel / totalFood;
		double grassPercent = grassLevel / totalFood;
		
//		System.out.println("grass% = " + grassPercent + " = " + grassLevel + " / " + totalFood);
//		System.out.println("meat% = " + meatPercent + " = " + meatLevel + " / " + totalFood);
		
		double meatAmount = amount * meatPercent;
		double grassAmount = amount * grassPercent;
		
		double grassToEat = grassLevel*(1-Math.pow((1-EAT_SPEED),grassAmount*timeStep));
        if(grassToEat > grassLevel){
        	grassToEat = grassLevel;
        }
        ((TileAttribute<Double>)t.getAttribute("foodLevel")).setValue(Double.valueOf(grassLevel-grassToEat));
        
        double meatToEat = meatLevel*(1-Math.pow((1-EAT_SPEED),meatAmount*timeStep));
        if(meatToEat > meatLevel){
        	meatToEat = meatLevel;
        }
        ((TileAttribute<Double>)t.getAttribute("meatLevel")).setValue(Double.valueOf(meatLevel-meatToEat));
        
//        System.out.println(meatLevel + " " + meatToEat + " grass -> " + grassLevel + " " + grassToEat + " " + grassAmount);
        
        // creature gains energy = multiplier() * totalFood

        c.addEnergy(MEAT_ENERGY_SCALAR*meatToEat+GRASS_EATING_EFFICIENCY*grassToEat);
        c.loseEnergy(attemptedAmount*EAT_ENERGY*timeStep);
        
        
        t.update();
        
        /*
        
        t.update();
        ((FoodLevel)t.getAttribute("foodLevel")).removeFood(foodToEat, false);
        
        double cMouthHue = (Double)c.getAttribute("mouthHue").getValue();
        double tFoodHue = (Double)t.getAttribute("foodHue").getValue();
        
        double foodDistance = Math.abs(tFoodHue-cMouthHue); //TODO make foodType a TileAttribute
        double multiplier = 1.0-foodDistance/FOOD_SENSITIVITY;
        if(multiplier >= 0){
          c.addEnergy(foodToEat*multiplier);
        }else{
          c.loseEnergy(-foodToEat*multiplier);
        }
        c.loseEnergy(attemptedAmount*EAT_ENERGY*timeStep);
        
         */
	}

	@Override
	public void creatureFailToEatFromTile(Creature c, Tile t, double amount, double attemptedAmount, double timeStep) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void creatureDepositFoodToTile(Creature c, Tile t, double amount) {
		// TODO Auto-generated method stub
		
		MeatLevel ml = (MeatLevel) t.getAttribute("meatLevel");
		ml.setValue((Double)ml.getValue()+amount);
	}

}
