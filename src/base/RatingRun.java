package base;

import java.io.*;
import java.util.*;

/**
 * This initializes and seeds player and organization objects. It also computes
 * and stores the changes in ratings.
 */
public class RatingRun {
	RatingMethod method;
	Map<String, Player> players;
	Map<String, Organization> organizations;
	double k;
	int step;
	File gameFile;
	
	public RatingRun(List<Match> matches) {
		players = new HashMap<String, Player>();
		organizations = new HashMap<String, Organization>();
		loadPlayersAndOrgs(matches);
		step = 0;
		k = 20;
	}
	
	public RatingRun(RatingMethod rm, List<Match> matches) {
		this(matches);
		method = rm;
	}
	
	/**
	 * 
	 * @param m - Information about one match.
	 */
	public void Step(Match m) {
		if(m.isCorrupt) return;
		
		step++;
		switch(method) {
			case EloBlind:
				StepEloBlind(m.team1, m.team2, m.team1Wins);
				break;
			case EloOrg:
				StepEloOrg(m.team1, m.team2, m.team1Wins);
				break;
			default:
				return;
		}
	}
	
	private void StepEloBlind(Team team1, Team team2, boolean t1Won) {
		double t1Avg = team1.ComputeTeamscore(players);
		double t2Avg = team2.ComputeTeamscore(players);
		double likelihoodT1Wins = 1/(1+Math.pow(10, (t2Avg-t1Avg)/400.0));
		double likelihoodT2Wins = 1/(1+Math.pow(10, (t1Avg-t2Avg)/400.0));
		double t1Update = k * ((t1Won ? 1 : 0) - likelihoodT1Wins);
		double t2Update = k * ((t1Won ? 0 : 1) - likelihoodT2Wins);
		
		for(Player p : team1.getPlayers()) {
			players.get(p.playerCode).updateElo(t1Update);
		}
		
		for(Player p : team2.getPlayers()) {
			players.get(p.playerCode).updateElo(t2Update);
		}
	}
	
	private void StepEloOrg(Team team1, Team team2, boolean t1Won) {
		double t1Elo = organizations.get(team1.teamCode).getElo();
		double t2Elo = team2.ComputeTeamscore(players);
		double likelihoodT1Wins = 1/(1+Math.pow(10, (t2Elo-t1Elo)/400.0));
		double likelihoodT2Wins = 1/(1+Math.pow(10, (t1Elo-t2Elo)/400.0));
		double t1Update = k * ((t1Won ? 1 : 0) - likelihoodT1Wins);
		double t2Update = k * ((t1Won ? 0 : 1) - likelihoodT2Wins);
		
		organizations.get(team1.teamCode).updateElo(t1Update);
		organizations.get(team2.teamCode).updateElo(t2Update);		
	}
	
	private void loadPlayersAndOrgs(List<Match> matches) {
		for(Match m : matches) {
			if(!m.isCorrupt) {
				Team[] ts = {m.team1, m.team2};
				for(Team t : ts) {
					if (!organizations.containsKey(t.teamCode)) {
						Organization newOrg = new Organization(t.teamCode);
						organizations.put(t.teamCode, newOrg);
					}
					for(Player p : t.getPlayers()) {
						if (!players.containsKey(p.playerCode)) {
							players.put(p.playerCode, p);
							organizations.get(t.teamCode).playerCodes.add(p.playerCode);
						}
						
						//TODO: Add players onto organization. Maybe orgs need a set of players that have played for them
						// Maybe they need a running PlayerContribution for that player
					}
				}
			}
		}
	}
	
	
	public void processAllMatches(List<Match> matches) {
		for(Match m : matches) {
			Step(m);
		}
		
	}
	public void processAllAppearances(List<BattingAppearance> appearances) {
		
	}
	/**
	 * Resets the ratings of all of the players and organizations, as well as the state of this object
	 * without needing to reload the data.
	 */
	public void reset() {
		reseedPlayers();
		reseedOrgs();
		step = 0;
	}
	
	public void reseedPlayers() {
		for(Player p : players.values()) {
			p.initializeRating();
		}
	}
	
	public void reseedOrgs() {
		for(Organization o : organizations.values()) {
			o.initializeRating();
		}
	}
}

enum RatingMethod {
	EloBlind,
	EloOrg,
	EloContrib,
	EloRole,
	BaseAppearance
}
