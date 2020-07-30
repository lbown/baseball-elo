package base;

import java.util.*;

import base.PlateAppearance.Outcome;
import base.Player.PosType;

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
	int firstSub = 0;
	boolean team1Wins;
	boolean gameTied;
	boolean predictedBy5th;
	boolean isCorrupt;
	String date;
	List<PlateAppearance> appearances;
	
	public Match(Match m) {
		id = m.id;
		team1 = new Team(m.team1);
		team2 = new Team(m.team2);
		team1Wins = m.team1Wins;
		isCorrupt = m.isCorrupt;
		date = m.date;
		appearances = new ArrayList<PlateAppearance>(m.appearances);
	}
	public Match(String line) {
		appearances = new ArrayList<PlateAppearance>();
		String[] dataInit = line.split(",");
		String[] data = new String[dataInit.length];
		for(int i = 0; i < dataInit.length; i++) {
			data[i] = dataInit[i].replace("\"", "");
		}
		team1 = new Team(data[3]);
		team2 = new Team(data[6]);
		team1Wins = Integer.parseInt(data[9]) > Integer.parseInt(data[10]);
		
		id =  data[6] + data[0] + data[1];
		date = data[0];
		// Starting Pitchers
		if(data[101].length() != 8 || data[103].length() != 8) {
			isCorrupt = true;
			team1 = new Team("");
			team2 = new Team("");
			return;
		}
		
		team1.playerContribs.add(team1.new PlayerContrib(new Player(data[101], data[102], PosType.Pitcher, 1), 1));
		team1.startingPlayerCodes.add(data[101]);
		team2.playerContribs.add(team2.new PlayerContrib(new Player(data[103], data[104], PosType.Pitcher, 1), 1));
		team2.startingPlayerCodes.add(data[103]);
		
		// Visitor Batters
		for (int i = 105; i < 132; i+=3) {
			if(data[i].length() != 8 || data[i+2].length() > 2) {
				isCorrupt = true;
				team1 = new Team("");
				team2 = new Team("");
				return;
			} else {
				team1.playerContribs.add(team1.new PlayerContrib(new Player(data[i], data[i+1], PosType.Batter, Integer.parseInt(data[i+2])), 1));
				team1.startingPlayerCodes.add(data[i]);
			}
		}
		
		// Home Batters
		for (int i = 132; i < 159; i+=3) {
			if(data[i].length() != 8 || data[i+2].length() > 2) {
				isCorrupt = true;
				team1 = new Team("");
				team2 = new Team("");
				return;
			} else {
				team2.playerContribs.add(team2.new PlayerContrib(new Player(data[i], data[i+1], PosType.Batter, Integer.parseInt(data[i+2])), 1));
				team2.startingPlayerCodes.add(data[i]);
			}
		}
	}
	
	/**
	 * Take the lines from an update file and parse them to shorten a match to the first 5 innings
	 */
	public int TruncateWithPlays(List<String> update) {
		int homeScore = 0;
		int visScore = 0;
		int inning = 1;
		int cutoff = 5;
		Player visPitcher = team1.getStartPitcher();
		Player homePitcher = team2.getStartPitcher();
		for(String s : update) {
			String[] line = s.split(",");
			
			// Disregard match if sub happens before 5th inning
			if(inning <= cutoff && (
				line[0].equals("sub") || 
				((line[0].equals("com") && line[1].equals("\"ej") && line[3].equals("P") )))
			) {
				isCorrupt = true;
			}
			if (line[0].equals("play") 
					&& !line[line.length-1].endsWith("NP")
					&& !line[line.length-1].startsWith("SB")
					&& !line[line.length-1].startsWith("CS")
					&& !line[line.length-1].startsWith("BK")
					&& !line[line.length-1].contains("FLE")
					&& !line[line.length-1].startsWith("WP")){
						Player pitcher = null;
						Player batter = null;
						Outcome outcome = null;
						if(line[2].contentEquals("0")) {
							pitcher = homePitcher;
							batter = team1.getPlayer(line[3]);
							team1.addBatContrib(batter.playerCode);
							team2.addPitchContrib(pitcher.playerCode);
						} else if (line[2].contentEquals("1")) {
							pitcher = visPitcher;
							batter = team2.getPlayer(line[3]);
							team1.addPitchContrib(pitcher.playerCode);
							team2.addBatContrib(batter.playerCode);
						} else {
							System.err.println("UH OH");
						}
						if (line[6].charAt(0) == 'S') {
							outcome = Outcome.Single;
						} else if (line[6].charAt(0) == 'D') {
							outcome = Outcome.Double;
						} else if (line[6].charAt(0) == 'T') {
							outcome = Outcome.Triple;
						} else if (line[6].charAt(0) == 'K') {
							outcome = Outcome.Strikeout;
						} else if (line[6].charAt(0) == 'W') {
							outcome = Outcome.Walk;
						} else if (line[6].charAt(0) == 'E') {
							outcome = Outcome.Error;
						} else if (line[6].startsWith("HR")) {
							outcome = Outcome.Homerun;
						} else if (line[6].startsWith("HP")) {
							outcome = Outcome.HitByPitch;
						} else if (line[6].startsWith("IW")) {
							outcome = Outcome.IntentionalWalk;
						} else if (line[6].startsWith("FC")) {
							outcome = Outcome.FielderChoice;
						} else {
							outcome = Outcome.BIPOut;
						}
						appearances.add(new PlateAppearance(batter, pitcher, outcome, date));
			}
			if(line[0].equals("play")) {
				inning = Integer.parseInt(line[1]);
			}
			
			if (inning <= cutoff && line[0].equals("play") && (line[6].startsWith("HR"))) {
				if(line[2].equals("0")) {
					visScore++;
				} else {
					homeScore++;
				}
			}
			if (inning <= cutoff && line[0].equals("play") && line[6].length() > 2) {
				if(line[2].equals("0")) {
					visScore += numOccurrences(line[6], "-H") + numOccurrences(line[6], "SBH");
				} else {
					homeScore += numOccurrences(line[6], "-H") + numOccurrences(line[6], "SBH");
				}
			}
		}
		if (isCorrupt) return -1;
		gameTied = visScore == homeScore;
		boolean tmpT1Wins = visScore > homeScore;
		predictedBy5th = (tmpT1Wins == team1Wins) && !gameTied;
		team1Wins = tmpT1Wins;
		return 0;
	}
	private int numOccurrences(String str, String substr) {
		if(substr.length() > str.length()) {
			return 0;
		}
		if(str.startsWith(substr)) {
			return 1 + numOccurrences(str.substring(substr.length()), substr);
		} else {
			return numOccurrences(str.substring(1), substr);
		}
	}
	
	/**
	 * Take the lines from an update file and parse them to correct and add to 
	 * @param update
	 */
	public void UpdateWithPlays(List<String> update) {
		int numPlays = 0;
		String date = update.get(0).substring(6,14);
		Player visPitcher = team1.getStartPitcher();
		Player homePitcher = team2.getStartPitcher();
		int inning = 0;
		
		for(String s : update) {
			String[] line = s.split(",");
			if (line[0].equals("sub") && firstSub == 0 && inning <= 9) firstSub = inning;
			// Ignore non-plays and stolen base/caught stealing play lines
			if (line[0].equals("play") 
			&& !line[line.length-1].endsWith("NP")
			&& !line[line.length-1].startsWith("SB")
			&& !line[line.length-1].startsWith("CS")
			&& !line[line.length-1].startsWith("BK")
			&& !line[line.length-1].contains("FLE")
			&& !line[line.length-1].startsWith("WP")){
				Player pitcher = null;
				Player batter = null;
				Outcome outcome = null;
				inning = Integer.parseInt(line[1]);
				numPlays++;
				if(line[2].contentEquals("0")) {
					pitcher = homePitcher;
					batter = team1.getPlayer(line[3]);
					team1.addBatContrib(batter.playerCode);
					team2.addPitchContrib(pitcher.playerCode);
				} else if (line[2].contentEquals("1")) {
					pitcher = visPitcher;
					batter = team2.getPlayer(line[3]);
					team1.addPitchContrib(pitcher.playerCode);
					team2.addBatContrib(batter.playerCode);
				} else {
					System.err.println("UH OH");
				}
				if (line[6].charAt(0) == 'S') {
					outcome = Outcome.Single;
				} else if (line[6].charAt(0) == 'D') {
					outcome = Outcome.Double;
				} else if (line[6].charAt(0) == 'T') {
					outcome = Outcome.Triple;
				} else if (line[6].charAt(0) == 'K') {
					outcome = Outcome.Strikeout;
				} else if (line[6].charAt(0) == 'W') {
					outcome = Outcome.Walk;
				} else if (line[6].charAt(0) == 'E') {
					outcome = Outcome.Error;
				} else if (line[6].startsWith("HR")) {
					outcome = Outcome.Homerun;
				} else if (line[6].startsWith("HP")) {
					outcome = Outcome.HitByPitch;
				} else if (line[6].startsWith("IW")) {
					outcome = Outcome.IntentionalWalk;
				} else if (line[6].startsWith("FC")) {
					outcome = Outcome.FielderChoice;
				} else {
					outcome = Outcome.BIPOut;
				}
				appearances.add(new PlateAppearance(batter, pitcher, outcome, date));
			} else if (line[0].equals("sub") && line[line.length-1].contentEquals("1")) {
				if (line[3].equals("0")) {
					visPitcher = new Player(line[1], line[2].replace("\"", ""), PosType.Pitcher, 1);
					if(team1.getPlayer(line[1]) == null) {

						team1.addPlayer(line[1], line[2].replace("\"", ""), PosType.Pitcher, 13);
					}
				} else {
					homePitcher = new Player(line[1], line[2].replace("\"", ""), PosType.Pitcher, 1);
					if(team2.getPlayer(line[1]) == null) {
						team2.addPlayer(line[1], line[2].replace("\"", ""), PosType.Pitcher, 13);
					}
				}
			} else if (line[0].equals("sub")) {
				if (line[3].equals("0")) {
					if(team1.getPlayer(line[1]) == null) {
						team1.addPlayer(line[1], line[2].replace("\"", ""), PosType.Batter, Integer.parseInt(line[5]));
					}
				} else {
					if(team2.getPlayer(line[1]) == null) {
						team2.addPlayer(line[1], line[2].replace("\"", ""), PosType.Batter, Integer.parseInt(line[5]));
					}
				}
			}
		}
	}
}
