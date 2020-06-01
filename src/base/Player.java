package base;

import java.util.*;
public class Player implements Rated {
	String playerCode;
	String playerName;
	ArrayList<Double> eloRatings;
	Position pos;
	
	Player(String pc, String name) {
		playerCode = pc;
		playerName = name;
		initializeRating();
	}
	@Override
	public double getElo() {
		return eloRatings.get(eloRatings.size()-1);
	}
	
	@Override
	public void initializeRating() {
		eloRatings = new ArrayList<Double>();
		eloRatings.add(1500.);
	}
	@Override
	public void updateElo(double eloUpdate) {
		eloRatings.add(getElo() + eloUpdate);
	}
}
