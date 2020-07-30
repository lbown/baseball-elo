package base;

import java.util.*;
public class Player implements Rated {
	String playerCode;
	String playerName;
	ArrayList<EloDate> eloBatter;
	ArrayList<EloDate> eloPitcher;
	
	ArrayList<Integer> rolesPlayed = new ArrayList<Integer>();
	double pbpUpdateThisMatch = 0;
	PosType pos;
	
//	Player(String pc, String name, PosType ptype) {
//		playerCode = pc;
//		playerName = name;
//		pos = ptype;
//		initializeRating();
//	}
	Player(Player p) {
		playerCode = p.playerCode;
		playerName = p.playerName;
		pos = p.pos;
		rolesPlayed = new ArrayList<Integer>(p.rolesPlayed);
		initializeRating();
	}
	Player(String pc, String name, PosType ptype, int fieldPos) {
		playerCode = pc;
		playerName = name;
		pos = ptype;
		rolesPlayed.add(fieldPos);
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
	public String mostCommonRole() {
		int[] roleFreq = new int[13];
		for(int i=0; i<roleFreq.length; i++) roleFreq[i] = 0;
		
		for(int i : rolesPlayed) {
			roleFreq[i-1]++;
		}
		int maxIndex = 0;
		for(int i = 1; i < roleFreq.length; i++) {
			if (roleFreq[i] > roleFreq[maxIndex]) maxIndex = i;
		}
		switch (maxIndex+1) {
		case 1:
			return "Pitcher";
		case 2:
			return "Catcher";
		case 3:
			return "First Base";
		case 4:
			return "Second Base";
		case 5:
			return "Third Base";
		case 6:
			return "Shortstop";
		case 7:
			return "Left Field";
		case 8:
			return "Center Field";
		case 9:
			return "Right Field";
		case 10:
			return "Designated Hitter";
		case 11:
			return "Pinch Hitter";
		case 12:
			return "Pinch Runner";
		case 13:
			return "Pitcher(subbed)";
		default:
			throw new IllegalArgumentException("Unexpected value: " + maxIndex);
		}
	}
	public double rolePct() {
		int[] roleFreq = new int[13];
		for(int i=0; i<roleFreq.length; i++) roleFreq[i] = 0;
		
		for(int i : rolesPlayed) {
			roleFreq[i-1]++;
		}
		int maxIndex = 0;
		for(int i = 1; i < roleFreq.length; i++) {
			if (roleFreq[i] > roleFreq[maxIndex]) maxIndex = i;
		}
		return (double)roleFreq[maxIndex]/(double)rolesPlayed.size();
	}
	public enum PosType {
		Batter,
		Pitcher
	}
}
