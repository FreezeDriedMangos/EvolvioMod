package core;

import java.util.ArrayList;
import core.modAPI.*;

public final class ModLoader {
       public static final ArrayList<ApiTileAttribute> tileAttributes = new ArrayList();
       public static final ArrayList<ApiButton> buttons               = new ArrayList();
       public static ApiCreatureEatBehavior apiCreatureEatBehavior;
       
       /**
        * recursively looks in folder "mods" for any classes that implemnt any API interfaces and loads them
        */
       public static void init() {
           
       }
       
       public static void creatureEatFromTile(Creature c, Tile t) {
           
       }
}
