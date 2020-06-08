package base;
import java.util.*;

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
		for(PlayerContrib pc : playerContribs) {
			scoreSum += players.get(pc.player.playerCode).getElo();
		}
		return scoreSum/players.size();
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
			scoreSum += players.get(pc.player.playerCode).getElo() * pc.contrib;
			contribSum += pc.contrib;
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
	
	/**
	 * Supporting class to keep track of the contribution to a particular game that players have
	 * @author Logan
	 *
	 */
	class PlayerContrib {
		Player player;
		double contrib;
		//TODO: might need to extend this to interact with the player role: contribution
		// With respect to number of plate appearances/time in field versus pitching amount
		PlayerContrib(Player p, double c) {
			player = p;
			contrib = c;
		}
	}
}
