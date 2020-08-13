package base;
import java.util.*;

import base.Player.PosType;

public class Team {
	String teamCode;
	ArrayList<PlayerContrib> playerContribs;
	ArrayList<String> startingPlayerCodes = new ArrayList<String>();
	
	public Team(String tc) {
		teamCode = tc;
		playerContribs = new ArrayList<PlayerContrib>();
	}
	public Team(Team t) {
		teamCode = t.teamCode;
		playerContribs = new ArrayList<Team.PlayerContrib>(t.playerContribs);
		startingPlayerCodes = new ArrayList<String>(t.startingPlayerCodes);
	}
	
	/**
	 * @return The average Elo of everyone on their team
	 */
	public double AverageElo(Map<String, Player> players) {
		double scoreSum = 0;
		int numPlayers = 0;
		for(PlayerContrib pc : playerContribs) {
			if (players.get(pc.player.playerCode) != null) {
				if(pc.player.pos == PosType.Batter)
					scoreSum += players.get(pc.player.playerCode).batterElo();
				else
					scoreSum += players.get(pc.player.playerCode).pitcherElo();
			} else {
				scoreSum += 1500;
			}
			numPlayers++;
		}
		return scoreSum/numPlayers;
	}
	public double AverageElo(Map<String, Player> players, double pitcherRatio) {
		double scoreSum = 0;
		int numPlayers = 0;
		double batterRatio = 1-pitcherRatio;
		int numBatters = 0;
		int numPitchers = 0;
		
		for(PlayerContrib pc : playerContribs) {
			if (players.get(pc.player.playerCode) != null) {
				if(pc.player.pos == PosType.Batter) {
					numBatters++;
				} else {
					numPitchers++;
				}
			}
		}
		if (numPitchers != 1) {
			System.err.println("You can't have " + numPitchers + " on your team...");
		}
		
		batterRatio = batterRatio / numBatters;
		
		float sanityCheck = 0;
		
		for(PlayerContrib pc : playerContribs) {
			if (players.get(pc.player.playerCode) != null) {
				if(pc.player.pos == PosType.Batter) {
					scoreSum += players.get(pc.player.playerCode).batterElo() * batterRatio;
					sanityCheck += batterRatio;
				} else {
					scoreSum += players.get(pc.player.playerCode).pitcherElo() * pitcherRatio;
					sanityCheck += pitcherRatio;
				}
			} else {
				System.err.println("U goofed");
				//scoreSum += 1500;
			}
		}
		if (sanityCheck < 0.95) {
			System.out.println("U goofed again, ratio sum is " + sanityCheck);
			for (PlayerContrib p2 : playerContribs) {
				System.out.println(p2.player.pos);
			}
			System.out.println("Batter Ratio: " + batterRatio);
			System.out.println("Pitcher Ratio: " + pitcherRatio);
			System.exit(1);
		}
		return scoreSum/sanityCheck;
	}
	public double PredictedElo(Map<String, Player> players) {
		double scoreSum = 0;
		int numPlayers = 0;
		for (String s : startingPlayerCodes) {
			if (players.get(s) == null) {
				scoreSum += 1500;
			} else {
				scoreSum += players.get(s).getElo();
			}
			numPlayers++;
		}
		return scoreSum / numPlayers;
	}
	public double PredictedElo(Map<String, Player> players, double pitcherRatio) {
		double scoreSum = 0;
		int numPlayers = 0;
		double batterRatio = (1-pitcherRatio)/startingPlayerCodes.size();
		double sumRatios = 0;
		if (startingPlayerCodes.size() == 0) {
			for (PlayerContrib pc : playerContribs) {
				System.out.println(pc.player.playerCode);
			}
			System.exit(1);
		}
		if (players.get(startingPlayerCodes.get(0)) == null) {
			scoreSum += 1500 * pitcherRatio;
		} else {
			scoreSum += players.get(startingPlayerCodes.get(0)).getElo() * pitcherRatio;
		}
		sumRatios += pitcherRatio;
		
		for (int i = 1; i < startingPlayerCodes.size(); i++) {
			if (players.get(startingPlayerCodes.get(i)) == null) {
				scoreSum += 1500 * batterRatio;
			} else {
				scoreSum += players.get(startingPlayerCodes.get(i)).getElo() * batterRatio;
			}
			sumRatios += batterRatio;
		}
		return scoreSum / sumRatios;
	}

	
	/**
	 * {@code ComputeTeamsmcore} computes a weighted average elo for this team of players using the Elo ratings
	 * of the players passed in through the parameter because teams are initialized with blank players that have
	 * no reference to their current elo.
	 * 
	 * @Return The average Elo of the team weighted by their contribution
	 * @param players The map of player objects that have their ratings tracked in a RatingRun instance
	 */
	public double ComputeTeamscore(Map<String, Player> players) {
		double contribSum = 0;
		double scoreSum = 0;
		for(PlayerContrib pc : playerContribs) {
			scoreSum += players.get(pc.player.playerCode).getElo() * pc.getContrib();
			contribSum += pc.getContrib();
		}
		return scoreSum/contribSum;
	}
	/**
	 * Adds a player to the team with contribution 1
	 */
	public void addPlayer(String playerCode, String playerName, Player.PosType ptype, int pos) {
		addPlayer(playerCode, playerName, ptype, 1, pos);
	}
	/**
	 * Adds a player to the team with specified contribution factor
	 */
	public void addPlayer(String playerCode, String playerName, Player.PosType ptype, double contrib, int pos) {
		Player p = new Player(playerCode, playerName, ptype, pos);
		playerContribs.add(new PlayerContrib(p, contrib));
	}
	/**
	 * 
	 * @return A list of the player objects on a team
	 */
	public List<Player> getPlayers() {
		List<Player> players = new ArrayList<Player>(playerContribs.size());
		for(PlayerContrib pc : playerContribs) {
			players.add(pc.player);
		}
		return players;
	}
	public List<Player> getBatters() {
		List<Player> players = new ArrayList<Player>(playerContribs.size());
		for(PlayerContrib pc : playerContribs) {
			if(pc.player.pos == PosType.Batter)
			players.add(pc.player);
		}
		return players;
	}
	public Player getPlayer(String code) {
		for (PlayerContrib pc : playerContribs) {
			if (pc.player.playerCode.equals(code)) {
				return pc.player;
			}
		}
		return null;
	}
	public PlayerContrib getPC(String code) {
		for (PlayerContrib pc : playerContribs) {
			if (pc.player.playerCode.equals(code))
				return pc;
		}
		return null;
	}
	public Player getStartPitcher() {
		for (Player p : getPlayers()) {
			if(p.pos == PosType.Pitcher) return p;
		}
		System.err.println("None found");
		return null;
	}
	/**
	 * Adds a contribution from a particular player to their playercontribution
	 * @param playerCode
	 */
	public void addBatContrib(String playerCode) {
		for (PlayerContrib pc : playerContribs) {
			if (pc.player.playerCode.equals(playerCode)) {
				pc.numBats += 1;
			}
		}
	}
	public void addPitchContrib(String playerCode) {
		for (PlayerContrib pc : playerContribs) {
			if (pc.player.playerCode.equals(playerCode)) {
				pc.appearancesPitched += 1;
			}
		}
	}
	public void addOutfieldContrib(String playerCode) {
		for (PlayerContrib pc : playerContribs) {
			if (pc.player.playerCode.equals(playerCode)) {
				pc.timeDefending += 1;
			}
		}
	}
	public void setTeamBats(int num) {
		for (PlayerContrib pc : playerContribs) {
				pc.totalTeamBats = num;
		}
	}
	public void setOppTeamBats(int num) {
		for (PlayerContrib pc : playerContribs) {
				pc.totalOpposingBats = num;
		}
	}
	/**
	 * Supporting class to keep track of the contribution to a particular game that players have
	 * @author Logan
	 *
	 */
	class PlayerContrib {
		Player player;
		int numBats = 0;
		int totalTeamBats = 0;
		int timeDefending = 0;
		int appearancesPitched = 0;
		int totalOpposingBats = 0;
		final double defenseWeight = 0.2;
		
		PlayerContrib(Player p, double c) {
			player = p;
		}
		/**
		 * 
		 * @return An amount of contribution to the game. When rating is updated, 
		 */
		public double getContrib() {
			if (player.pos == PosType.Pitcher)
			return (numBats * 9/totalTeamBats) * (1-defenseWeight) + (timeDefending / totalOpposingBats) * defenseWeight;
			else
			return appearancesPitched / totalOpposingBats;
			// This doesn't quite solve the problem of closing pitchers
		}
	}
}
