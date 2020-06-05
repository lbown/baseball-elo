package base;

import java.io.*;
import java.util.*;

public class StatLoader {
	private Map<String, Match> matches;
	
	public StatLoader(String dataFolderLoc) {
		String matchFolderLoc = dataFolderLoc + "Matches/";
		String playbyplayLoc = dataFolderLoc + "Plays/";
		matches = new HashMap<String, Match>();
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
			matches.put(m.id, m);
		}
		sc.close();
	}
	
	/**
	 * Goes through the entire play-by-play file, compiles the set of lines of that file, and pushes that
	 * update to the match object
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
			}
			matchUpdate.add(line);
		}
		pushUpdate(matchUpdate);
	}
	
	private void pushUpdate(List<String> matchUpdate) {
		String id = matchUpdate.get(0).substring(3);
		if (matches.containsKey(id) == false) {
			System.out.println("Can't find match " + id + ".");
		}
		matches.get(id).UpdateWithPlays(matchUpdate);
	}
	
	public List<Match> getMatches() {
		return new ArrayList<Match>(matches.values());
	}
	
}