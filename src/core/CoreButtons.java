package core;

import core.modAPI.Button;

public class CoreButtons {
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
}
