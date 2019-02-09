package core;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.Panel;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.font.TextAttribute;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;

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
		
		for(int i = 0; i < paths.size(); i++) {
			String fileName = paths.get(i).getFileName().toString();
			String extention = fileName.split("\\.")[1];
			if(!extention.equals("class"))
				paths.remove(i--);
		}
		
		
		// display the mods in a window
			
		JFrame frame = new JFrame("Mod Select");
		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE); // allows me to manually close the window
		frame.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){
                int confirmLoad = JOptionPane.showConfirmDialog(null, "Are these mods okay?");
                if(confirmLoad == 0) {
                	System.out.println("calling finishLoading with " + paths);
                	finishLoading(paths);
                	frame.dispose();
                }
            }
        });
		
		frame.setSize(500,500);
		
		Panel contentPanel = new Panel();
		BoxLayout vert = new BoxLayout(contentPanel, BoxLayout.PAGE_AXIS);
		contentPanel.setLayout(vert);
		
		JScrollPane pane = new JScrollPane(contentPanel);
		frame.add(pane);
		
		
		
		int index = 0;
		while(index < paths.size()) {
			index = makeListingForMod(paths, index, contentPanel);
		}
		
		frame.pack();
		frame.setVisible(true);
		frame.setAlwaysOnTop(true);
//		frame.validate();
//        frame.repaint();
		frame.toFront();
			
	}
	
	private static int makeListingForMod(ArrayList<Path> paths, int startIndex, Panel contentPanel) {
		
		int index = startIndex;
		String currentModname = paths.get(startIndex).getName(1).toString();
		String currentModnameReadable = splitCamelCase(currentModname);
		
		ArrayList<Path> thisModsPaths = new ArrayList<>();
		ArrayList<JCheckBox> thisModsCheckBoxes = new ArrayList<>();
		
		/* Make the header for this mod */ {
			Panel panel = new Panel();
			BoxLayout layout = new BoxLayout(panel, BoxLayout.LINE_AXIS);
			panel.setLayout(layout);
			
			JCheckBox box = new JCheckBox();
			box.setSelected(true);
			box.addItemListener(new ItemListener() { 
				ArrayList<Path> thesePaths = thisModsPaths;
				ArrayList<JCheckBox> theseBoxes = thisModsCheckBoxes;
				
	            public void itemStateChanged(ItemEvent e) {  
	            	 if(e.getStateChange() == 1) {
	            		 paths.addAll(thesePaths);
	            	 } else {
	            		 paths.removeAll(thesePaths);
	            	 }
	            	 
	            	 for(JCheckBox box : theseBoxes) {
	            		 box.setSelected(e.getStateChange() == 1);
	            	 }
	              }    
	           });    
			

			JLabel label = new JLabel(currentModnameReadable);
			Font font = label.getFont();
			font = font.deriveFont(
			    Collections.singletonMap(
			        TextAttribute.SIZE, 20));
			label.setFont(font);
			
			panel.add(box, Component.LEFT_ALIGNMENT);
			panel.add(label, Component.LEFT_ALIGNMENT);
			panel.add(new JLabel("    "));
			panel.add(Box.createHorizontalGlue()); // this forces left alignment (because Component.LEFT_ALIGNMENT didn't work)
			contentPanel.add(panel);
		}
		
		// make listings for every file of this mod
		for(; index < paths.size(); index++) {
			if(!paths.get(index).getName(1).toString().equals(currentModname)) {
				return index;
			}
			Path p = paths.get(index);
			
			Panel panel = new Panel();
			BoxLayout layout = new BoxLayout(panel, BoxLayout.LINE_AXIS);
			panel.setLayout(layout);
			
			
			panel.add(new JLabel("    "), Component.LEFT_ALIGNMENT);
			
			JCheckBox box = new JCheckBox();
			box.setSelected(true);
			box.addItemListener(new ItemListener() { 
				Path path = p;
				
	            public void itemStateChanged(ItemEvent e) {  
	            	 if(e.getStateChange() == 1) {
	            		 paths.add(path);
	            		 System.out.println("added " + path.toString());
	            	 } else {
	            		 paths.remove(path);
	            		 System.out.println("removed " + path.toString());
	            	 }
	              }    
	           });    
			
			thisModsCheckBoxes.add(box);
			
			panel.add(box, Component.LEFT_ALIGNMENT);
			panel.add(new JLabel(p.subpath(1, p.getNameCount()).toString()), Component.LEFT_ALIGNMENT);
			panel.add(new JLabel("    "));
			panel.add(Box.createHorizontalGlue());
			contentPanel.add(panel, Component.LEFT_ALIGNMENT);
		}
		
		return index;
	}
	
	// function credit to polygenelubricants on StackOverflow
	// https://stackoverflow.com/questions/2559759/how-do-i-convert-camelcase-into-human-readable-names-in-java
	private static String splitCamelCase(String string) {
		if(string.equals("")) return "";
		
		string = Character.toUpperCase(string.charAt(0)) + string.substring(1);
		
		return string.replaceAll(
			      String.format("%s|%s|%s",
			         "(?<=[A-Z])(?=[A-Z][a-z])",
			         "(?<=[^A-Z])(?=[A-Z])",
			         "(?<=[A-Za-z])(?=[^A-Za-z])"
			      ),
			      " "
			   );
	}

	private static void finishLoading(ArrayList<Path> modList) {
		// load the mods
		
		System.out.println("final modlist " + modList);
		
		for (Path p : modList) {
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
//							if(inter.getCanonicalName().equals("core.modAPI.CreaturePeripheralDrawer")) {
//								creaturePeripheralDrawers.add((CreaturePeripheralDrawer) c.getConstructor().newInstance()); 
//							}
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
//							if(inter.getCanonicalName().equals("core.modAPI.BrainInput")) {
//								brainInputs.add((Class<BrainInput>) c);
//							}
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
		String missing = "";
		missing += brainModel          == null? "Brain.java"               : "";  
		missing += brainDrawer         == null? "BrainDrawer.java"         : ""; 
		missing += creatureEatBehavior == null? "CreatureEatBehavior.java" : ""; 
		missing += tileDrawer          == null? "TileDrawer.java"          : ""; 
		
		if(!missing.equals("")) {
			JOptionPane.showConfirmDialog(null, "Warning: missing implementations for some critical interfaces. Please try enabling more mods.\nMissing implementations:\n"+missing);
		}
		
		EvolvioMod.main.finishSetup();
		
//		EvolvioMod.finishStartup(); // temp
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
