package base;

import java.util.*;
public class Player implements Rated {
	String playerCode;
	String playerName;
	ArrayList<EloDate> eloBatter;
	ArrayList<EloDate> eloPitcher;
	double pbpUpdateThisMatch = 0;
	PosType pos;
	
	Player(String pc, String name, PosType ptype) {
		playerCode = pc;
		playerName = name;
		pos = ptype;
		initializeRating();
	}
	@Override
	public double getElo() {
		if (pos == PosType.Batter) {
			return batterElo();
		} else {
			return pitcherElo();
		}
	}
	public double batterElo() {
		if (eloBatter.size() == 0) return 1500;
		return eloBatter.get(eloBatter.size()-1).elo;
	}
	public double pitcherElo() {
		if (eloPitcher.size() == 0) return 1500;
		return eloPitcher.get(eloPitcher.size()-1).elo;
	}
	
	@Override
	public void initializeRating() {
		eloBatter = new ArrayList<EloDate>();
		eloPitcher = new ArrayList<EloDate>();
	}
	/**
	 * Update the elo by adding a new EloDate object to the end of the list.
	 */
	@Override
	public void updateElo(double eloUpdate, String date) {
		if (pos == PosType.Batter) {
			updateBatterElo(eloUpdate, date);
		} else {
			updatePitcherElo(eloUpdate, date);			
		}
	}
	
	public void updateBatterElo(double eloUpdate, String date) {
		eloBatter.add(new EloDate(date, batterElo() + eloUpdate));
	}
	
	public void updatePitcherElo(double eloUpdate, String date) {
		eloPitcher.add(new EloDate(date, pitcherElo() + eloUpdate));
	}
	public enum PosType {
		Batter,
		Pitcher
	}
}
