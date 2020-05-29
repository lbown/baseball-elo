package base;

import java.io.*;
import java.util.*;

public class Main {
	public static void main(String[] args) {
		StatLoader matchHistory = new StatLoader("C:/Users/logan/Documents/Research/BaseballStats/");
		RatingRun ratings = new RatingRun(RatingMethod.EloBlind);
		
		for (Match m : matchHistory.matches) {
			ratings.Step(m);
		}
		
		printRatings(ratings);
		
		// TODO: Compare different ranking systems
	}
	
	public static void printRatings(RatingRun r) {
		Map<String, Integer> hm = new TreeMap<String, Integer>();
		for (String s : r.players.keySet()) {
			hm.put(s, (int)Math.floor(r.players.get(s)));
		}
		List<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>(hm.entrySet());
		list.sort((p1, p2) -> (Integer.compare(p1.getValue(), p2.getValue())));
		
		for(Map.Entry e : list) {
			System.out.println(e.getKey()+ " " + e.getValue());
		}
	}
	
	public static void printTeam() {
		
	}
}
