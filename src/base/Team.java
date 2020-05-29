package base;
import java.util.*;

public class Team {
	String teamCode;
	double elo;
	Set<String> players;
	
	public Team(String tc) {
		teamCode = tc;
		players = new HashSet<String>();
		elo = 1500;
	}
	
	public void updateElo(double change) {
		elo += change;
	}
}
