package base;

import java.io.*;
import java.util.*;

public class Main {
	static RatingRun ratings;
	
	public static void main(String[] args) {
		StatLoader sl = new StatLoader("C:/Users/logan/Documents/Research/BaseballStats/");
		List<Match> matches = sl.getMatches();
		
		ratings = new RatingRun(RatingMethod.EloBlind, matches);
		
		ratings.processAllMatches(matches);
		
		Organization org = ratings.organizations.get("\"NYA\"");
		printOrg(org);
		
		//printRatings(ratings);
		
		// TODO: Compare different ranking systems
	}
	
	public static void printRatings() {
		Map<String, Integer> hm = new TreeMap<String, Integer>();
		for (String s : ratings.players.keySet()) {
			hm.put(s, (int)Math.floor(ratings.players.get(s).getElo()));
		}
		List<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>(hm.entrySet());
		list.sort((p1, p2) -> (Integer.compare(p1.getValue(), p2.getValue())));
		
		for(Map.Entry e : list) {
			System.out.println(e.getKey()+ " " + e.getValue());
		}
	}
	
	public static void printOrg(Organization org) {
		System.out.println(org.teamCode + ": " + org.getElo()+ " Elo");
		for(Player p : org.getPlayers()) {
			System.out.println(p.playerCode + " " + p.getElo());
		}
	}
	public static void printAllOrgs() {
		for(Organization o : ratings.organizations.values()) {
			System.out.println(o.teamCode + ": " + o.getElo());
		}
	}
}
