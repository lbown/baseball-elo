package base;

public class PlateAppearance{
	Player batter;
	Player pitcher;
	String date;
	Outcome outcome;
	
	public PlateAppearance (Player b, Player p, Outcome o, String d) {
		batter = b;
		pitcher = p;
		outcome = o;
		date = d;
	}
	
	/**
	 * 
	 * @return A value between 0 and 1 representing the outcome for the batter.
	 */
	public double valueToBatter(String method) {
		if (method == "TTO") {
			return outcome.tto;
		} else if (method == "BIP") {
			return outcome.bip;
		} else {
			return outcome.weighted;
		} 
	}
	enum Outcome {
		Walk (1, 1, 0.6),
		Homerun (1, 1, 1),
		Strikeout (0, 0, 0),
		Single (0.5, 1, 0.7),
		Double (0.5, 1, 0.9),
		Triple (0.5, 1, 0.95),
		HitByPitch (0.5, 1, 0.6),
		IntentionalWalk (0.5, 1, 0.55),
		Error (0.5, 0, 0.2),
		FielderChoice (0.5, 0, 0.2),
		BIPOut (0.5, 0, 0.2);
		
		double tto;
		double bip;
		double weighted;
		Outcome(double t, double b, double w){
			tto = t;
			bip = b;
			weighted = w;
		}
	}
}
