package base;

import java.io.*;
import java.util.*;

public class RatingRun {
	RatingMethod method;
	Map<String, Double> players;
	
	/**
	 * This stores the accumulation of all players to play for a team. The teams that are part of the
	 * match data only store the players that participated that game.
	 */
	Map<String, Team> teams;
	double k;
	int step;
	File gameFile;
	
	public RatingRun(RatingMethod rm) {
		method = rm;
		players = new HashMap<String, Double>();
		teams = new HashMap<String, Team>();
		step = 0;
		k = 20;
	}
	
	public void Step(Match m) {
		// Store teams from match and update the current players on the team
		teams.putIfAbsent(m.team1.teamCode, m.team1);
		teams.putIfAbsent(m.team2.teamCode, m.team2);

		teams.get(m.team1.teamCode).players.addAll(m.team1.players);
		teams.get(m.team2.teamCode).players.addAll(m.team2.players);
		
		for(String pID : m.team1.players) {
			players.putIfAbsent(pID, 1500.0);
		}
		for(String pID : m.team2.players) {
			players.putIfAbsent(pID, 1500.0);
		}
		
		switch(method) {
			case EloBlind:
				StepEloBlind(m.team1, m.team2, m.team1Wins);
			default:
				return;
		}
		
	}
	
	private void StepEloBlind(Team team1, Team team2, boolean t1Won) {
		double t1Avg = TeamAverage(team1.players);
		double t2Avg = TeamAverage(team2.players);
		double likelihoodT1Wins = 1/(1+Math.pow(10, (t2Avg-t1Avg)/400.0));
		double likelihoodT2Wins = 1/(1+Math.pow(10, (t1Avg-t2Avg)/400.0));
		double t1Update = k * ((t1Won ? 1 : 0) - likelihoodT1Wins);
		double t2Update = k * ((t1Won ? 0 : 1) - likelihoodT2Wins);
		
		for(String pID : team1.players) {
			players.put(pID, players.get(pID) + t1Update);
		}
		team1.updateElo(t1Update);
		
		for(String pID : team2.players) {
			players.put(pID, players.get(pID) + t2Update);
		}
		team2.updateElo(t2Update);
	}
	
	public double TeamAverage(Set<String> pIDs) {
		double sum = 0;
		for(String pID : pIDs) {
			sum += players.get(pID);
		}
		return sum / pIDs.size();
	}
	
	public void ReseedPlayers() {
		for (String pID : players.keySet()) {
			players.put(pID, 1500.0);
		}
	}
}

enum RatingMethod {
	EloBlind,
	EloContrib,
	EloRole,
	EloPairwise
}
