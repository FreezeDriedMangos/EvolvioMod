package core;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import core.modAPI.AdditionalBrainIO;
import core.modAPI.Brain;
import core.modAPI.BrainDrawer;
import core.modAPI.Button;
import core.modAPI.CreatureAction;
import core.modAPI.CreatureAttribute;
import core.modAPI.CreatureEatBehavior;
import core.modAPI.CreaturePeripheral;
//import core.modAPI.CreaturePeripheralDrawer;
import core.modAPI.TileAttribute;
import core.modAPI.TileDrawer;

public final class ModLoader {
	public static final ArrayList<Class<Button>> buttons = new ArrayList<>();
	public static final ArrayList<Class<TileAttribute>> tileAttributes = new ArrayList<>();
	public static final ArrayList<Class<CreatureAttribute>> creatureAttributes = new ArrayList<>();
	public static final ArrayList<Class<CreatureAction>> creatureActions = new ArrayList<>();
	public static final ArrayList<Class<CreaturePeripheral>> creaturePeripherals = new ArrayList<>();

	public static ArrayList<String> brainOutputs = new ArrayList<>();
	//public static ArrayList<String> brainInputs  = new ArrayList<>();
	//public static ArrayList<CreaturePeripheralDrawer> creaturePeripheralDrawers = new ArrayList<>();
	
	public static Class<Brain> brainModel;
	
	public static CreatureEatBehavior creatureEatBehavior;
	public static TileDrawer          tileDrawer;
	public static BrainDrawer         brainDrawer;
	/**
	 * recursively looks in folder "mods" for any classes that implemnt any API
	 * interfaces and loads them
	 */
	@SuppressWarnings("unchecked")
	public static void init() {
		// default-required outputs
		brainOutputs.add("hue");
		brainOutputs.add("accelerate");
		brainOutputs.add("turn");
		brainOutputs.add("eat");
		brainOutputs.add("fight");
		brainOutputs.add("reproduce");
		
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
//					if(inter.getCanonicalName().equals("core.modAPI.CreaturePeripheralDrawer")) {
//						creaturePeripheralDrawers.add((CreaturePeripheralDrawer) c.getConstructor().newInstance()); 
//					}
					if(inter.getCanonicalName().equals("core.modAPI.TileDrawer")) {
						tileDrawer = (TileDrawer) c.getConstructor().newInstance(); 
					}
					if(inter.getCanonicalName().equals("core.modAPI.BrainDrawer")) {
						brainDrawer = (BrainDrawer) c.getConstructor().newInstance(); 
					}
					if(inter.getCanonicalName().equals("core.modAPI.AdditionalBrainIO")) {
						AdditionalBrainIO io = ((AdditionalBrainIO) c.getConstructor().newInstance());
						brainOutputs.addAll(io.getOutputs());
						//brainInputs. addAll(io.getInputs());
					}
					
					if(inter.getCanonicalName().equals("core.modAPI.Brain")) {
						brainModel = (Class<Brain>) c;
					}
					
					//creature action, brain outputs

					if(inter.getCanonicalName().equals("core.modAPI.Button")) {
						buttons.add((Class<Button>) c);
					}
					if(inter.getCanonicalName().equals("core.modAPI.TileAttribute")) {
						tileAttributes.add((Class<TileAttribute>) c);
					}
					if(inter.getCanonicalName().equals("core.modAPI.CreatureAttribute")) {
						creatureAttributes.add((Class<CreatureAttribute>) c);
					}
					if(inter.getCanonicalName().equals("core.modAPI.CreatureAction")) {
						creatureActions.add((Class<CreatureAction>) c);
					}
					if(inter.getCanonicalName().equals("core.modAPI.CreaturePeripheral")) {
						creaturePeripherals.add((Class<CreaturePeripheral>) c);
					}
//					if(inter.getCanonicalName().equals("core.modAPI.BrainInput")) {
//						brainInputs.add((Class<BrainInput>) c);
//					}
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
		
		System.out.println(tileAttributes);
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
		for(Class<CreatureAction> action : creatureActions) {
			try {
				CreatureAction a = action.newInstance();
				creature.actions.add(a);
			} catch (InstantiationException | IllegalAccessException e) { e.printStackTrace(); }
		}
		for(Class<CreaturePeripheral> peripheral : creaturePeripherals) {
			try {
				CreaturePeripheral p = peripheral.newInstance();
				creature.peripherals.add(p);
			} catch (InstantiationException | IllegalAccessException e) { e.printStackTrace(); }
		}
	}
	
	public static List<CreaturePeripheral> createPeripherals() {
		List<CreaturePeripheral> p = new ArrayList<>();
		for(Class<CreaturePeripheral> cl : creaturePeripherals) {
			try {
				CreaturePeripheral per = (cl.getConstructor().newInstance());
				per.init();
				p.add(per);
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
			}
		}
		
		return p;
	}

	public static Brain createBrain(Creature c, Board b) {
		// create peripherals
		ArrayList<String> inputs = new ArrayList<>();
		for(CreaturePeripheral peripheral : c.peripherals) {
			inputs.addAll(peripheral.getInputNames());
		}
		
		try {
//			ArrayList<BrainInput> inputs = new ArrayList<>();
//			for(Class<BrainInput> in : brainInputs) {
//				BrainInput inp = in.getConstructor().newInstance();
//				inputs.add(inp);
//			}
			//ArrayList<String> inputs = new ArrayList<>();
			//inputs.addAll(brainInputs);

			ArrayList<String> outputs = new ArrayList<>();
			outputs.addAll(brainOutputs);
			
			Brain brain = brainModel.getConstructor().newInstance();
			brain.init(c, b, inputs, outputs);
			
			return brain;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} 
	}

	public static Brain getOffspringBrain(List<CreaturePeripheral> babyPeripherals, ArrayList<Creature> parents) {
		ArrayList<String> inputs = new ArrayList<>();
		for(CreaturePeripheral peripheral : babyPeripherals) {
			inputs.addAll(peripheral.getInputNames());
		}
		
		try {
//			ArrayList<BrainInput> inputs = new ArrayList<>();
//			for(Class<BrainInput> in : brainInputs) {
//				BrainInput inp = in.getConstructor().newInstance();
//				inputs.add(inp);
//			}
//			ArrayList<String> inputs = new ArrayList<>();
//			inputs.addAll(brainInputs);
//			
			ArrayList<String> outputs = new ArrayList<>();
			outputs.addAll(brainOutputs);
			
			return brainModel.getConstructor().newInstance().getOffspring(parents, inputs, outputs);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		return null;
	}

	public static void setOffspringAttributes(Creature baby, ArrayList<Creature> parents, Board board) {
		System.out.println(parents);
		for(Class<CreatureAttribute> attribute : creatureAttributes) {
			try {
				String attributeName = attribute.getConstructor().newInstance().getName();
				System.out.println(attributeName);
				
				ArrayList<CreatureAttribute> parentAttributes = new ArrayList<>();
				for(Creature parent : parents) {
					parentAttributes.add(parent.getAttribute(attributeName));
				}
				
				CreatureAttribute a = attribute.newInstance();
				a.initFromParents(parentAttributes, board);
				
				baby.attributes.put(a.getName(), a);
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) { e.printStackTrace(); }
		}
	}
}
