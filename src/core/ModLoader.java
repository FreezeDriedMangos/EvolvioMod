package core;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;

import core.modAPI.Button;
import core.modAPI.CreatureAttribute;
import core.modAPI.CreatureEatBehavior;
import core.modAPI.TileAttribute;
import core.modAPI.TileDrawer;
import evolvioColor.ColorEatBehavior;

public final class ModLoader {
	public static final ArrayList<Class<TileAttribute>> tileAttributes = new ArrayList<>();
	public static final ArrayList<Class<CreatureAttribute>> creatureAttributes = new ArrayList<>();
	public static final ArrayList<Class<Button>> buttons = new ArrayList<>();
	public static CreatureEatBehavior creatureEatBehavior;
	public static TileDrawer          tileDrawer;

	/**
	 * recursively looks in folder "mods" for any classes that implemnt any API
	 * interfaces and loads them
	 */
	public static void init() {
		ArrayList<Path> paths = new ArrayList<>();
		try {
			Files.find(Paths.get("mods/"),
			           Integer.MAX_VALUE,
			           (filePath, fileAttr) ->  fileAttr.isRegularFile())
			        .forEach(paths::add);
			
			Files.find(Paths.get("bin/"),
			           Integer.MAX_VALUE,
			           (filePath, fileAttr) -> fileAttr.isRegularFile() && !filePath.startsWith("bin/core"))
			        .forEach(paths::add);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println(paths);
		for (Path p : paths) {
			// if the path doesn't end with ".class", skip this one
			String fileName = p.getFileName().toString();
			String extention = fileName.split("\\.")[1];
			if(!extention.equals("class"))
				continue;
			
			try {
				String className = p.subpath(1, p.getNameCount()).toString().replace('/', '.');
				className = className.substring(0, className.length()-(".class".length()));
				Class<?> c = Class.forName(className);
				
				System.out.println(c);
				
				// WHEN YOU ADD MORE MOD API INTERFACES, ADD AN IF STATEMENT FOR EACH OF
				// THEM HERE, THAT WILL TAKE CARE OF LOADING THEM
				
				// TO ADD MODS, SIMPLY DROP A FOLDER OF THE .class FILES IN EITHER THE 
				// "bin" FOLDER, OR THE "mods" FOLDER
				for (Class<?> inter : c.getInterfaces()) {
					System.out.println("\t"+inter.getCanonicalName());
					
					if(inter.getCanonicalName().equals("core.modAPI.CreatureEatBehavior")) {
						creatureEatBehavior = (CreatureEatBehavior) c.getConstructor().newInstance(); 
					}
					
					if(inter.getCanonicalName().equals("core.modAPI.TileDrawer")) {
						tileDrawer = (TileDrawer) c.getConstructor().newInstance(); 
					}
					
					if(inter.getCanonicalName().equals("core.modAPI.TileAttribute")) {
						tileAttributes.add((Class<TileAttribute>) c);
					}
					
					if(inter.getCanonicalName().equals("core.modAPI.CreatureAttribute")) {
						creatureAttributes.add((Class<CreatureAttribute>) c);
					}
				}
				
	            if(c.getSuperclass().getCanonicalName().equals("Battle")){
	                
	            }
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void creatureEatFromTile(Creature c, Tile t) {

	}

	public static void initializeAttributes(Tile tile, Board board, float stepSize) {
		for(Class<TileAttribute> attribute : tileAttributes) {
			try {
				TileAttribute a = attribute.newInstance();
				a.init(tile.posX, tile.posY, stepSize, board, tile);
				tile.attributes.put(a.getName(), a);
			} catch (InstantiationException | IllegalAccessException e) { e.printStackTrace(); }
		}
	}
	
	public static void initializeAttributes(Creature creature, Board board) {
		for(Class<CreatureAttribute> attribute : creatureAttributes) {
			try {
				CreatureAttribute a = attribute.newInstance();
				a.init(board, creature);
				creature.attributes.put(a.getName(), a);
			} catch (InstantiationException | IllegalAccessException e) { e.printStackTrace(); }
		}
	}

	public static void drawTile(Tile tile, float scaleUp, boolean showEnergy) {
		tileDrawer.draw(tile, scaleUp, showEnergy);
	}
}
