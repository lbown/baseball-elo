package base;

public class PlateAppearance{
	Player batter;
	Player pitcher;
	String date;
	Outcome outcome;
	
	public PlateAppearance (Player b, Player p, Outcome o) {
		batter = b;
		pitcher = p;
		outcome = o;
	}
	
	/**
	 * 
	 * @return A value between 0 and 1 representing the outcome for the batter.
	 */
	public double valueToBatter() {
		return 0;
	}
	enum Outcome {
		Walk,
		Homerun,
		Strikeout,
	}
}
