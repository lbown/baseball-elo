package base;

import java.util.*;

import base.Team.PlayerContrib;
/**
 * The {@code Match} class stores the statistics about one game of baseball including 
 * @author logan
 *
 */
public class Match {
	Team team1; // Visitor	
	Team team2; // Home
	boolean team1Wins;
	public Match(String line) {
		String[] data = line.split(",");
		for(String s : data) {
			//TODO: This doesn't actually work for some reason...
			s = s.replace("\"", "");
		}
		team1 = new Team(data[3]);
		team2 = new Team(data[6]);
		team1Wins = Integer.parseInt(data[9]) > Integer.parseInt(data[10]);
		// Starting Pitchers
		
		if(data[101].length() != 10 || data[103].length() != 10) {
			team1 = new Team("");
			team2 = new Team("");
			return;
		}
		team1.playerContribs.add(team1.new PlayerContrib(new Player(data[101], data[102]), 1));
		team2.playerContribs.add(team2.new PlayerContrib(new Player(data[103], data[104]), 1));
		
		// Visitor Batters
		for (int i = 105; i < 132; i+=3) {
			if(data[i].length() !=10) {
				// TODO: mark match as invalid data and don't process.
				team1 = new Team("");
				team2 = new Team("");
				return;
			} else {
				team1.playerContribs.add(team1.new PlayerContrib(new Player(data[i], data[i+1]), 1));
			}
		}
		
		// Home Batters
		for (int i = 132; i < 159; i+=3) {
			if(data[i].length() !=10) {
				team1 = new Team("");
				team2 = new Team("");
				return;
			} else {
				team2.playerContribs.add(team2.new PlayerContrib(new Player(data[i], data[i+1]), 1));
			}
		}
	}
}
