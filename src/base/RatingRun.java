package base;

import java.io.*;
import java.util.*;

import base.Player.PosType;
import base.Rated.EloDate;

/**
 * This initializes and seeds player and organization objects. It also computes
 * and stores the changes in ratings.
 */
public class RatingRun {
	private RatingMethod method;
	Map<String, Player> players = new HashMap<String, Player>();
	Map<String, Organization> organizations = new HashMap<String, Organization>();
	List<String> allDates = new ArrayList<String>();
	double k;
	int step;
	double pitcherRatio = 0;
	File gameFile;
	
	public RatingRun() {
		step = 0;
		k = 20;
	}
	/**
	 * Uses matches to update ratings. Requires method other than BaseAppearance
	 */
	public RatingRun(RatingMethod rm, List<Match> matches) {
		loadPlayersAndOrgs(matches);
		method = rm;
	}
	
	public RatingRun(List<PlateAppearance> appearances) {
		this();
		loadPlayersFromAppearances(appearances);
		method = RatingMethod.EloGammaAdjust;
	}
	
	public RatingRun(RatingMethod rm, List<Match> matches, List<PlateAppearance> appearances) {
		this();
		loadPlayersAndOrgs(matches);
		loadPlayersFromAppearances(appearances);
		method = rm;
	}
	
	public RatingRun(RatingMethod rm, List<Match> matches, List<PlateAppearance> appearances, double pitcherRatio) {
		this();
		loadPlayersAndOrgs(matches);
		loadPlayersFromAppearances(appearances);
		method = rm;
		this.pitcherRatio = pitcherRatio;
	}
	
	/**
	 * 
	 * @param m - Information about one match.
	 */
	public void Step(Match m) {
		if(m.isCorrupt) return;
		if(!allDates.contains(m.date)) allDates.add(m.date);
		step++;
		switch(method) {
			case EloBlind:
				StepEloBlind(m);
				break;
			case EloOrg:
				StepEloOrg(m);
				break;
			case EloCombined:
				StepCombined(m);
				break;
			case EloBlindTrunc:
				StepEloBlindTrunc(m);
				break;
			case EloGammaAdjust:
				StepPlayByPlay(m);
				break;
			case EloPlateAppearance:
				processAllAppearances(m.appearances, "Weighted");
				break;
			default:
				return;
		}
	}
	/**
	 * Assumes that the team's likelihood of winning is equally impacted by all players on the team.
	 * Computes the likelihood using an average and updates each player's rating using that likelihood.
	 * @param m
	 */
	private void StepEloBlind(Match m) {
		Team team1 = m.team1;
		Team team2 = m.team2;
		boolean t1Won = m.team1Wins;
		
		double t1Avg = team1.AverageElo(players);
		double t2Avg = team2.AverageElo(players);
		double likelihoodT1Wins = 1/(1+Math.pow(10, (t2Avg - t1Avg)/400.0));
		double likelihoodT2Wins = 1/(1+Math.pow(10, (t1Avg - t2Avg)/400.0));
		double t1Update = k * ((t1Won ? 1 : 0) - likelihoodT1Wins);
		double t2Update = k * ((t1Won ? 0 : 1) - likelihoodT2Wins);
		
		for(Player p : team1.getPlayers()) {
			players.get(p.playerCode).updateElo(t1Update, m.date);
		}
		
		for(Player p : team2.getPlayers()) {
			players.get(p.playerCode).updateElo(t2Update, m.date);
		}
	}
	private void StepEloBlindTrunc(Match m) {
		Team team1 = m.team1;
		Team team2 = m.team2;
		boolean t1Won = m.team1Wins;
		
		double t1Avg, t2Avg;
		if (pitcherRatio == 0) {
			t1Avg = team1.AverageElo(players);
			t2Avg = team2.AverageElo(players);
		} else {
			t1Avg = team1.AverageElo(players, pitcherRatio);
			t2Avg = team2.AverageElo(players, pitcherRatio);
		}
		double likelihoodT1Wins = 1/(1+Math.pow(10, (t2Avg + 27.85 - t1Avg)/400.0));
		double likelihoodT2Wins = 1/(1+Math.pow(10, (t1Avg - t2Avg - 27.85)/400.0));

		double t1Update = k * ((t1Won ? 1 : 0) - likelihoodT1Wins);
		double t2Update = k * ((t1Won ? 0 : 1) - likelihoodT2Wins);
		if (m.gameTied) {
			t1Update = k * (0.5 - likelihoodT1Wins);
			t2Update = k * (0.5 - likelihoodT2Wins);
		}
		
		double batterRatio = (1-pitcherRatio)/team1.getBatters().size();
		
		if (team1.getBatters().size() < 8  || team1.getBatters().size() > 9) {
			System.err.println("Team has " + team1.getBatters().size() + " batters?");
			for (Player p : team1.getBatters()) {
				System.out.println(p.playerCode);
			}
			System.exit(1);
		}
		
		if (true) {
			for(Player p : team1.getBatters()) {
				players.get(p.playerCode).updateBatterElo(t1Update, m.date);
				players.get(p.playerCode).rolesPlayed.add(p.rolesPlayed.get(0));
			}
			
			for(Player p : team2.getBatters()) {
				players.get(p.playerCode).updateBatterElo(t2Update, m.date);
				players.get(p.playerCode).rolesPlayed.add(p.rolesPlayed.get(0));
			}
		} else {
			for(Player p : team1.getBatters()) {
				players.get(p.playerCode).updateBatterElo(t1Update*pitcherRatio/batterRatio, m.date);
				players.get(p.playerCode).rolesPlayed.add(p.rolesPlayed.get(0));
			}
			
			for(Player p : team2.getBatters()) {
				players.get(p.playerCode).updateBatterElo(t2Update*pitcherRatio/batterRatio, m.date);
				players.get(p.playerCode).rolesPlayed.add(p.rolesPlayed.get(0));
			}
		}
		players.get(team1.getStartPitcher().playerCode).updatePitcherElo(t1Update, m.date);
		players.get(team1.getStartPitcher().playerCode).rolesPlayed.add(team1.getStartPitcher().rolesPlayed.get(0));
		players.get(team2.getStartPitcher().playerCode).updatePitcherElo(t2Update, m.date);
		players.get(team2.getStartPitcher().playerCode).rolesPlayed.add(team2.getStartPitcher().rolesPlayed.get(0));
	}
	/**
	 * Updates players' ratings based on the likelihood the team will win scaled by
	 * their contribution to the game.
	 * @param m
	 */
	private void StepCombined(Match m) {
		Team team1 = m.team1;
		Team team2 = m.team2;
		boolean t1Won = m.team1Wins;
		
		double t1Avg = team1.AverageElo(players);
		double t2Avg = team2.AverageElo(players);
		double likelihoodT1Wins = 1/(1+Math.pow(10, (t2Avg + 27.85 - t1Avg)/400.0));
		double likelihoodT2Wins = 1/(1+Math.pow(10, (t1Avg - t2Avg - 27.85)/400.0));
		double t1Update = k * ((t1Won ? 1 : 0) - likelihoodT1Wins);
		double t2Update = k * ((t1Won ? 0 : 1) - likelihoodT2Wins);
		
		processAllAppearances(m.appearances, "Weighted");
		
		for(Player p : team1.getPlayers()) {
			if (p.pos == PosType.Batter) {
				players.get(p.playerCode).updateBatterElo(t1Update, m.date);
			} else {
				players.get(p.playerCode).updatePitcherElo(t1Update, m.date);
			}
			players.get(p.playerCode).rolesPlayed.add(p.rolesPlayed.get(0));
		}
		
		for(Player p : team2.getPlayers()) {
			if (p.pos == PosType.Batter) {
				players.get(p.playerCode).updateBatterElo(t2Update, m.date);
			} else {
				players.get(p.playerCode).updatePitcherElo(t2Update, m.date);
			}
			players.get(p.playerCode).rolesPlayed.add(p.rolesPlayed.get(0));
		}
		
	}
	
	private void StepPlayByPlay(Match m) {
		processAllAppearances(m.appearances, "Weighted");
	}
	/**
	 * Treats each baseball team as it's own rated object and updates the team's rating using the
	 * elo system.
	 * @param m
	 */
	private void StepEloOrg(Match m) {
		Team team1 = m.team1;
		Team team2 = m.team2;
		boolean t1Won = m.team1Wins;
		
		double t1Elo = organizations.get(team1.teamCode).getElo();
		double t2Elo = organizations.get(team2.teamCode).getElo();
		double likelihoodT1Wins = 1/(1+Math.pow(10, (t2Elo-t1Elo)/400.0));
		double likelihoodT2Wins = 1/(1+Math.pow(10, (t1Elo-t2Elo)/400.0));
		double t1Update = k * ((t1Won ? 1 : 0) - likelihoodT1Wins);
		double t2Update = k * ((t1Won ? 0 : 1) - likelihoodT2Wins);
		
		organizations.get(team1.teamCode).updateElo(t1Update, m.date);
		organizations.get(team2.teamCode).updateElo(t2Update, m.date);
	}
	/**
	 * Load players and organizations for match based update methods.
	 * @param matches
	 */
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
							players.put(p.playerCode, new Player(p));
							organizations.get(t.teamCode).playerCodes.add(p.playerCode);
						}
						
					}
				}
			}
		}
	}
	/**
	 * Load players only for appearance based rating
	 * @param appearances
	 */
	private void loadPlayersFromAppearances(List<PlateAppearance> appearances) {
		for (PlateAppearance p : appearances) {
			if (p == null) System.out.println("p null");
			if (players == null) System.out.println("players null");
			if (p.batter == null) System.out.println("batter null");
			if (p.pitcher == null) System.out.println("pitcher null");
			
			players.put(p.batter.playerCode, new Player(p.batter));
			players.put(p.pitcher.playerCode, new Player(p.pitcher));
		}
	}
	
	public void processAllMatches(List<Match> matches) {
		for(Match m : matches) {
			Step(m);
		}
	}
	public void processAllAppearances(List<PlateAppearance> appearances, String appearanceScore) {
		for(PlateAppearance pa : appearances) {
			if(!allDates.contains(pa.date)) allDates.add(pa.date);
			String bcode = pa.batter.playerCode;
			String pcode = pa.pitcher.playerCode;
			double batterElo = players.get(bcode).batterElo();
			double pitcherElo= players.get(pcode).pitcherElo();
			
			switch(method) {
			case EloPlateAppearance:
				double likelihoodBWins = 1/(1+Math.pow(10, (pitcherElo-batterElo)/400.0));
				players.get(bcode).updateBatterElo(
						k/8 * (pa.valueToBatter(appearanceScore) - likelihoodBWins), pa.date);
				players.get(pcode).updatePitcherElo(
						k/8 * (likelihoodBWins - pa.valueToBatter(appearanceScore)), pa.date);
				break;
			case EloCombined:
			case EloGammaAdjust:
				double gamma = 128.7;
				likelihoodBWins = 1/(1+Math.pow(10, (pitcherElo + gamma - batterElo)/400.0));
				players.get(bcode).updateBatterElo(
						k/2 * (pa.valueToBatter(appearanceScore) - likelihoodBWins), pa.date);
				players.get(pcode).updatePitcherElo(
						k/2 * (likelihoodBWins - pa.valueToBatter(appearanceScore)), pa.date);
				break;
			default:
				break;
			}
		}
	}
	/**
	 * Resets the ratings of all of the players and organizations, as well as the state of this object
	 * without needing to reload the data. Need to reinit an object to transition between match and
	 * appearance modes however.
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
	/**
	 * Sorts and prints the ratings of all players updated in the elo process. 
	 */
	public void printRatings() {
		Map<String, Integer> hm = new TreeMap<String, Integer>();
		for (String s : players.keySet()) {
			Player p = players.get(s);
			hm.put(p.playerName + " - " + p.mostCommonRole() + ": " + (p.eloBatter.size() + p.eloPitcher.size()) + " games", (int)Math.floor(players.get(s).getElo()));
		}
		List<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>(hm.entrySet());
		list.sort((p1, p2) -> (Integer.compare(p1.getValue(), p2.getValue())));
		
		for(Map.Entry<String, Integer> e : list) {
			System.out.println(e.getKey()+ " Elo: " + e.getValue());
		}
	}
	/**
	 * Prints the elo of an organization
	 * @param org
	 */
	public void printOrg(Organization org) {
		System.out.println(org.teamCode + ": " + org.getElo()+ " Elo");
		for(String pc : org.playerCodes) {
			Player p = players.get(pc);
			System.out.println(p.playerName + ": " + p.getElo());
		}
	}

	public void printOrg(String code) {
		if(organizations.get(code) == null) {
			System.out.println("No organization named " + code + " was loaded.");
			return;
		}
		printOrg(organizations.get(code));
	}
	
	/**
	 * Prints all organization codes along with their Elo rating, sorted by ascending rating.
	 */
	public void printAllOrgs() {
		Map<String, Integer> hm = new TreeMap<String, Integer>();
		for(String s : organizations.keySet()) {
			hm.put(s, (int)Math.floor(organizations.get(s).getElo()));
		}
		List<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>(hm.entrySet());
		list.sort((o1, o2) -> (Integer.compare(o1.getValue(), o2.getValue())));
		for(Map.Entry<String, Integer> e : list) {
			System.out.println(e.getKey() + " " + e.getValue());
		}
	}
	public void printPositionDiff() {
		int numBatters = 0;
		int numPitchers = 0;
		double batterElo = 0;
		double pitcherElo = 0;
		for (Player p : players.values()) {
			if(p.pos == PosType.Batter) {
				numBatters++;
				batterElo += p.getElo();
			} else {
				numPitchers++;
				pitcherElo += p.getElo();
			}
		}
		System.out.println("Batter avg: " + batterElo/numBatters + " Pitcher avg: " + pitcherElo/numPitchers + " Diff: " + (batterElo/numBatters - pitcherElo/numPitchers));
	}
	public void printPlayerRatings(String playerCode) {
		Player p = players.get(playerCode);
		System.out.print("Batter:");
		for(EloDate e : p.eloBatter) {
			System.out.print(" " + e.elo + ",");
		}
		System.out.println();
		System.out.print("Pitcher:");
		for(EloDate e : p.eloPitcher) {
			System.out.print(" " + e.elo + ",");
		}
	}
	
	public float predictMatches(List<Match> matches) {
		float correct = 0;
		int numTotal = 0;
		for (Match m : matches) {
			if (!m.isCorrupt) {
				int outcome = predictMatch(m);
				if (outcome != -1) {
					correct += outcome;
					numTotal += 1;
				}
			}
		}
		System.out.println("correct " + correct + " total " + numTotal);
		return correct / numTotal;
	}
	
	public void matchPredictionCSV(List<Match> matches, String file) {
		try { 
			FileWriter csvWriter = new FileWriter(file);
			for (Match m : matches) {
				double diff = m.team2.PredictedElo(players) - m.team1.PredictedElo(players);
				double likelihoodT1Wins = 1/(1+Math.pow(10, (diff)/400.0));
				csvWriter.append(m.team1Wins + "," + likelihoodT1Wins + "\n");
			}
		} catch (IOException e ) {
			System.err.println(e);
		}
		
	}
	public int predictMatch(Match m) {
		double epsilon = 0.02;
		double diff;
		if (pitcherRatio == 0) {
			diff = m.team2.PredictedElo(players) - m.team1.PredictedElo(players);
		} else {
			diff = m.team2.PredictedElo(players, pitcherRatio) - m.team1.PredictedElo(players, pitcherRatio);
		}
		double likelihoodT1Wins = 1/(1+Math.pow(10, (diff)/400.0));
		Step(m);
		
		if (likelihoodT1Wins > 0.5 + epsilon) {
			if (m.team1Wins) return 1;
			else return 0;
		} else if (likelihoodT1Wins < 0.5 - epsilon) {
			if (m.team1Wins) return 0;
			else return 1;
		} else {
			return -1;
		}
	}
	
	public void playerGameEloDateToCSV(String file, int minGames) {
		try {
			FileWriter csvWriter = new FileWriter(file);
			csvWriter.append("Code,Name,NumGames,Position,FieldPos,%Played");
			for (int i = 1; i < allDates.size(); i++) {
				if (allDates.get(i).equals(allDates.get(i-1))) {
					allDates.remove(i);
					i--;
				}
			}
			for (String s : allDates){
				// To yyyy-mm-dd
				String formDate = s.substring(0,4) + "-" + s.substring(4,6) + "-" + s.substring(6);
				csvWriter.append("," + formDate);
			}
			for (Player p : players.values()) {
				if (p.eloBatter.size() + p.eloPitcher.size() >= minGames) {
					csvWriter.append("\n");
					csvWriter.append(p.playerCode + "," + p.playerName + "," + (p.eloBatter.size() + p.eloPitcher.size()) + "," + p.pos + "," + p.mostCommonRole() + "," + p.rolePct());
					if(p.pos == PosType.Batter) {
						double elo = 1500;
						for (int i = 0; i < allDates.size(); i++) {
							for (Rated.EloDate ed : p.eloBatter) {
								if (ed.date.equals(allDates.get(i))) {
									elo = ed.elo;
								}
							}
							csvWriter.append("," + elo);
						}
					} else {
						double elo = 1500;
						for (int i = 0; i < allDates.size(); i++) {
							for (Rated.EloDate ed : p.eloPitcher) {
								if (ed.date.equals(allDates.get(i))) {
									elo = ed.elo;
								}
							}
							csvWriter.append("," + elo);
						}
					}
				}
			}
			csvWriter.flush();
			csvWriter.close();
		} catch (IOException e) {
		}
	}
	
}

enum RatingMethod {
	EloBlind,
	EloBlindTrunc,
	EloOrg,
	EloCarry,
	EloCombined,
	EloPlateAppearance, EloGammaAdjust
}
