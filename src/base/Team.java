package base;
import java.util.*;

import base.Player.PosType;

public class Team {
	String teamCode;
	ArrayList<PlayerContrib> playerContribs;
	
	public Team(String tc) {
		teamCode = tc;
		playerContribs = new ArrayList<PlayerContrib>();
	}
	
	/**
	 * @return The average Elo of everyone on their team
	 */
	public double AverageElo(Map<String, Player> players) {
		double scoreSum = 0;
		int numPlayers = 0;
		for(PlayerContrib pc : playerContribs) {
			if (players.get(pc.player.playerCode) != null) {
				scoreSum += players.get(pc.player.playerCode).getElo();
			} else {
				scoreSum += 1500;
			}
			numPlayers++;
		}
		return scoreSum/numPlayers;
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
	public void addPlayer(String playerCode, String playerName, Player.PosType ptype) {
		addPlayer(playerCode, playerName, ptype, 1);
	}
	/**
	 * Adds a player to the team with specified contribution factor
	 */
	public void addPlayer(String playerCode, String playerName, Player.PosType ptype, double contrib) {
		Player p = new Player(playerCode, playerName, ptype);
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
