package base;

import java.util.*;

public class Main {
	static RatingRun ratings;
	
	public static void main(String[] args) {
		StatLoader sl = new StatLoader("C:/Users/logan/Documents/Research/BaseballStats/");
		List<Match> matches = sl.getMatches();
		
		ratings = new RatingRun(RatingMethod.EloBlind, matches);
		
		ratings.processAllMatches(matches);
		
		Organization org = ratings.organizations.get("NYA");
		
		//printAllOrgs();
		printRatings();
		
		// TODO: Compare different ranking systems
	}
	
	public static void printRatings() {
		Map<String, Integer> hm = new TreeMap<String, Integer>();
		for (String s : ratings.players.keySet()) {
			hm.put(ratings.players.get(s).playerName, (int)Math.floor(ratings.players.get(s).getElo()));
		}
		List<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>(hm.entrySet());
		list.sort((p1, p2) -> (Integer.compare(p1.getValue(), p2.getValue())));
		
		for(Map.Entry<String, Integer> e : list) {
			System.out.println(e.getKey()+ " " + e.getValue());
		}
	}
	
	/**
	 * Prints the Elo rating of an organization, as well as the ratings of all players to play for organization
	 */
	public static void printOrg(Organization org) {
		System.out.println(org.teamCode + ": " + org.getElo()+ " Elo");
		for(String pc : org.playerCodes) {
			Player p = ratings.players.get(pc);
			System.out.println(p.playerName + " " + p.getElo());
		}
	}
	
	public static void printOrg(String code) {
		if(ratings.organizations.get(code) == null) {
			System.out.println("No organization named " + code + " was loaded.");
			return;
		}
		printOrg(ratings.organizations.get(code));
	}
	
	/**
	 * Prints all organization codes along with their Elo rating, sorted by ascending rating.
	 */
	public static void printAllOrgs() {
		Map<String, Integer> hm = new TreeMap<String, Integer>();
		for(String s : ratings.organizations.keySet()) {
			hm.put(s, (int)Math.floor(ratings.organizations.get(s).getElo()));
		}
		List<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>(hm.entrySet());
		list.sort((o1, o2) -> (Integer.compare(o1.getValue(), o2.getValue())));
		for(Map.Entry<String, Integer> e : list) {
			System.out.println(e.getKey() + " " + e.getValue());
		}
	}
}
