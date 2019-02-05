package evolvioColor;

import java.util.ArrayList;
import java.util.List;

import core.modAPI.AdditionalBrainIO;

public class ColorBrainIO implements AdditionalBrainIO {

	@Override
	public List<String> getOutputs() {
		ArrayList<String> list = new ArrayList<>();
		
		list.add("mouthHue");
		
		return list;
	}

//	@Override
//	public List<String> getInputs() {
//		// TODO Auto-generated method stub
//		return null;
//	}

}
