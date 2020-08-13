package base;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class StatLoader {
	private Map<String, Match> matches = new HashMap<String, Match>();
	List<PlateAppearance> appearances = new ArrayList<PlateAppearance>();
	private Map<String, Match> truncMatches = new HashMap<String, Match>();
	
	public StatLoader(String dataFolderLoc) {
		String matchFolderLoc = dataFolderLoc + "Matches/";
		String playbyplayLoc = dataFolderLoc + "Plays/";
		System.out.println("Loading data from " + matchFolderLoc);
		for (File f : new File(matchFolderLoc).listFiles()) {
			AddMatchData(f);
		}
		for (File f : new File(playbyplayLoc).listFiles()) {
			AugmentMatchData(f);
		}
	}
	
	public void AddMatchData(File f) {
		Scanner sc;
		try {
			sc = new Scanner(f);
		} catch (FileNotFoundException e) {
			System.err.println(e.getMessage());
			return;
		}
		while (sc.hasNextLine()) {
			String line = sc.nextLine();
			
			Match m = new Match(line);
			matches.put(m.id, new Match(m));
			truncMatches.put(m.id, new Match(m));
		}
		sc.close();
	}
	
	/**
	 * Goes through the entire play-by-play file, compiles the set of lines
	 * of that file, and pushes that
	 * update to the match object. Also loads plate appearances.
	 * 
	 * @param f The file to augment matches with
	 */
	private void AugmentMatchData(File f) {
		Scanner sc;
		List<String> matchUpdate = new ArrayList<String>();
		try {
			sc = new Scanner(f);
		} catch (FileNotFoundException e) {
			System.err.println(e.getMessage());
			return;
		} 
		while (sc.hasNextLine()) {
			String line = sc.nextLine();
			if (line.substring(0, 2).equals("id") && matchUpdate.size() > 0) {
				pushUpdate(matchUpdate);
				matchUpdate = new ArrayList<String>();
			} else if (line.substring(0, 2).equals("id") && matchUpdate.size() < 10) {
				matchUpdate = new ArrayList<String>();
			}
			matchUpdate.add(line);
		}
		pushUpdate(matchUpdate);
	}
	
	private void pushUpdate(List<String> matchUpdate) {
		if (matchUpdate.size() < 10) return;
		String id = matchUpdate.get(0).substring(3);
		if(matches.get(id).isCorrupt) return;		
		if (matches.containsKey(id) == false) {
			System.out.println("Can't find match " + id + ".");
		}
		//matches.get(id).UpdateWithPlays(matchUpdate);
		truncMatches.get(id).TruncateWithPlays(matchUpdate);
		appearances.addAll(matches.get(id).appearances);
	}
	
	public List<Match> getMatches() {
		// Sort by date
		ArrayList<Map.Entry<String, Match>> entries =
				new ArrayList<Map.Entry<String, Match>>(matches.entrySet());
		entries.sort((e1,e2) -> compareEntries(e1, e2));
		ArrayList<Match> ret = new ArrayList<Match>();
		for (Map.Entry<String, Match> e : entries) {
			ret.add(e.getValue());
		}
		return ret;
	}
	
	public List<Match> getTruncMatches() {
		// Sort by date
		ArrayList<Map.Entry<String, Match>> entries =
				new ArrayList<Map.Entry<String, Match>>(truncMatches.entrySet());
		entries.sort((e1,e2) -> compareEntries(e1, e2));
		ArrayList<Match> ret = new ArrayList<Match>();
		for (Map.Entry<String, Match> e : entries) {
			ret.add(e.getValue());
		}
		return ret;
	}
	private int compareEntries(Map.Entry<String, Match> e1, Map.Entry<String, Match> e2) {
		String s1 = e1.getKey();
		String s2 = e2.getKey();
		Date d1 = new Date(Integer.parseInt(s1.substring(3,7)),
						   Integer.parseInt(s1.substring(7,9)),
						   Integer.parseInt(s1.substring(9, 11)));
		Date d2 = new Date(Integer.parseInt(s2.substring(3,7)),
					       Integer.parseInt(s2.substring(7,9)),
				           Integer.parseInt(s2.substring(9, 11)));
		return d1.compareTo(d2);
	}
	
	public List<PlateAppearance> getAppearances() {
		appearances.sort((a1, a2) -> a1.dateObj.compareTo(a2.dateObj));
		return appearances;
	}
}