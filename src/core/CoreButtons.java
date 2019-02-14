package core;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import core.modAPI.Button;

public class CoreButtons {
	static class BlendTilesButton implements Button {

		@Override
		public void click(int relX, int relY) {
			EvolvioMod.main.evoBoard.BLEND_TILES = !EvolvioMod.main.evoBoard.BLEND_TILES;
		}

		@Override
		public String getText() {
			return "Toggle simple tile blending";
		}
		@Override public String getSecondLineText() { return EvolvioMod.main.evoBoard.BLEND_TILES ? "On" : "Off"; }

		@Override public float getFlashAlpha() { return 0; }
		@Override public void init() {}
		
	}
	
	static class ControlButton implements Button {
		@Override
		public void click(int relX, int relY) {
			EvolvioMod.main.evoBoard.userControl = !EvolvioMod.main.evoBoard.userControl;
		}

		@Override
		public String getText() {
			return EvolvioMod.main.evoBoard.userControl? "Keyboard Control" : "Brain Control";
		}

		@Override public float getFlashAlpha() { return 0; }
		@Override public void init() {}
		@Override public String getSecondLineText() { return null; }
	}
	static class MaintainPopButton implements Button {

		@Override
		public void click(int relX, int relY) {
			if(relX < Button.STANDARD_BUTTON_WIDTH/2) {
				EvolvioMod.main.evoBoard.creatureMinimum -= EvolvioMod.main.evoBoard.creatureMinimumIncrement;
			} else {
				EvolvioMod.main.evoBoard.creatureMinimum += EvolvioMod.main.evoBoard.creatureMinimumIncrement;
			}
		}

		@Override
		public String getText() {
			return "Maintain pop. at " + EvolvioMod.main.evoBoard.creatureMinimum;
		}
		
		@Override public float getFlashAlpha() { return 0; }
		@Override public void init() { }

		@Override
		public String getSecondLineText() {
			return "-" + EvolvioMod.main.evoBoard.creatureMinimumIncrement + "                    +" + EvolvioMod.main.evoBoard.creatureMinimumIncrement;
		}
	}
	static class PlaySpeedButton implements Button {

		@Override
		public void click(int relX, int relY) {
			if(relX < Button.STANDARD_BUTTON_WIDTH / 2) {
				EvolvioMod.main.evoBoard.playSpeed /= 2;
			} else {
				if(EvolvioMod.main.evoBoard.playSpeed == 0) {
					EvolvioMod.main.evoBoard.playSpeed = 1;
				} else if (EvolvioMod.main.evoBoard.playSpeed < EvolvioMod.MAX_PLAY_SPEED) {
					EvolvioMod.main.evoBoard.playSpeed *= 2;
				}
			}
		}

		@Override
		public String getText() {
			return "-    Play Speed (" + EvolvioMod.main.evoBoard.playSpeed + "x)    +";
		}

		@Override
		public String getSecondLineText() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public float getFlashAlpha() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public void init() {
			// TODO Auto-generated method stub
			
		}
	}
	static class ReorderButton implements Button {
		static final String[] sorts = { "Biggest", "Smallest", "Youngest", "Oldest", "A to Z", "Z to A", "Highest Gen", "Lowest Gen" };
		
		@Override
		public void click(int relX, int relY) {
			EvolvioMod.main.evoBoard.creatureRankMetric++;
			EvolvioMod.main.evoBoard.creatureRankMetric %= sorts.length;
		}

		@Override
		public String getText() {
			return "Sort by: " + sorts[EvolvioMod.main.evoBoard.creatureRankMetric];
		}

		@Override public String getSecondLineText() { return null; }
		@Override public float getFlashAlpha() { return 0; }
		@Override public void init() { }
	}
	static class ResetZoomButton implements Button {

		@Override
		public void click(int relX, int relY) {
			EvolvioMod.main.resetZoom();
		}

		@Override
		public String getText() {
			return "Reset Zoom";
		}

		@Override public String getSecondLineText() { return null; }
		@Override public float getFlashAlpha() { return 0; }
		@Override public void init() {}
	}
	static class SaveWorldToFileButton implements Button {
		final float FLASH_FALLOFF = 0.1f;
		float flashVal = 0;
		
		String folder = "worlds/world0/";
		
		@Override
		public void click(int relX, int relY) {
			// TODO Auto-generated method stub
			flashVal = 1+FLASH_FALLOFF;
			
			try {
				ArrayList<Creature> creatures = EvolvioMod.main.evoBoard.creatures;
				for(int i = 0; i < creatures.size(); i++) {
					PrintWriter w = new PrintWriter(folder+"creatures/creature"+i+"_" + creatures.get(i).name +".dat", "UTF-8");
					w.println(creatures.get(i).toString());
					w.close();
				}
				
			} catch (Exception e) {} 
			// save to a .wld file (I made up that extention :D)
			try {
				PrintWriter w = new PrintWriter(folder+"world.dat", "UTF-8");
				w.println("World seed: " + EvolvioMod.main.SEED);
				w.println("Mods\n" + ModLoader.finalModList);
				w.println("\\Mods");
				w.close();
			} catch (FileNotFoundException | UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}

		@Override
		public String getText() {
			return "Save to File";
		}

		@Override
		public String getSecondLineText() {
			// TODO Auto-generated method stub
			return "not implemented yet";
		}

		@Override
		public float getFlashAlpha() {
			flashVal -= FLASH_FALLOFF;
			return flashVal;
		}

		@Override
		public void init() {
			// TODO Auto-generated method stub
			

			// make a new folder for this run of the program
		}
		
	}
}
