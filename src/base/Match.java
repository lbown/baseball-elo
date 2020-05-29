package base;

import java.util.*;

public class Match {
	Team team1; // Visitor
	// TODO
	// ArrayList<Pair<Player, double>> team
	// factor in amount of time played in game
	
	Team team2; // Home
	boolean team1Wins;
	
	public Match(String line) {
		String[] data = line.split(",");
		for(String s : data) {
			s = s.replace("\"", "");
		}

		team1 = new Team(data[3]);
		team2 = new Team(data[6]);
		team1Wins = Integer.parseInt(data[9]) > Integer.parseInt(data[10]);
		// Starting Pitchers
		
		if(data[101].length() != 8 || data[103].length() != 8) {
			return;
		}
		team1.players.add(data[101]);
		team2.players.add(data[103]);
		
		// Visitor Batters
		for (int i = 105; i < 132; i+=3) {
			team1.players.add(data[i]);
			if(data[i].length() !=8) {
				System.out.println(data[i]);
				return;
			}
		}
		
		// Home Batters
		for (int i = 132; i < 159; i+=3) {
			team2.players.add(data[i]);
			if(data[i].length() !=8) {
				System.out.println(data[i]);
				return;
			}
		}
	}
}
