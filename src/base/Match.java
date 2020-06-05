package base;

import java.util.*;

/**
 * The {@code Match} class stores the statistics about one game of baseball including the teams,
 * events that happen during the game, etc.
 * @author logan
 *
 */
public class Match {
	String id;
	Team team1; // Visitor
	Team team2; // Home
	boolean team1Wins;
	boolean isCorrupt;
	
	public Match(String line) {
		String[] dataInit = line.split(",");
		String[] data = new String[dataInit.length];
		for(int i = 0; i < dataInit.length; i++) {
			data[i] = dataInit[i].replace("\"", "");
		}
		team1 = new Team(data[3]);
		team2 = new Team(data[6]);
		team1Wins = Integer.parseInt(data[9]) > Integer.parseInt(data[10]);
		
		id =  data[6] + data[0] + data[1];
		
		// Starting Pitchers
		if(data[101].length() != 8 || data[103].length() != 8) {
			isCorrupt = true;
			team1 = new Team("");
			team2 = new Team("");
			return;
		}
		team1.playerContribs.add(team1.new PlayerContrib(new Player(data[101], data[102]), 1));
		team2.playerContribs.add(team2.new PlayerContrib(new Player(data[103], data[104]), 1));
		
		// Visitor Batters
		for (int i = 105; i < 132; i+=3) {
			if(data[i].length() != 8) {
				isCorrupt = true;
				team1 = new Team("");
				team2 = new Team("");
				return;
			} else {
				team1.playerContribs.add(team1.new PlayerContrib(new Player(data[i], data[i+1]), 1));
			}
		}
		
		// Home Batters
		for (int i = 132; i < 159; i+=3) {
			if(data[i].length() != 8) {
				isCorrupt = true;
				team1 = new Team("");
				team2 = new Team("");
				return;
			} else {
				team2.playerContribs.add(team2.new PlayerContrib(new Player(data[i], data[i+1]), 1));
			}
		}
	}
	
	public void UpdateWithPlays(List<String> update) {
		
	}
}
