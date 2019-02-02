package core;

import java.util.ArrayList;
import core.modAPI.*;
import defaultMods.DefaultEatBehavior;

public final class ModLoader {
       public static final ArrayList<TileAttribute> tileAttributes = new ArrayList();
       public static final ArrayList<Button> buttons               = new ArrayList();
       public static CreatureEatBehavior apiCreatureEatBehavior = new DefaultEatBehavior(); // TODO: remove placeholder code
       
       /**
        * recursively looks in folder "mods" for any classes that implemnt any API interfaces and loads them
        */
       public static void init() {
           
       }
       
       public static void creatureEatFromTile(Creature c, Tile t) {
           
       }
}
