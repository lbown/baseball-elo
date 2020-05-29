package base;

import java.io.*;
import java.util.*;

public class StatLoader {
	ArrayList<Match> matches;
	
	public StatLoader(String dataFolderLoc) {
		matches = new ArrayList<Match>();
		for (File f : new File(dataFolderLoc).listFiles()) {
			AddData(f);
		}
	}
	
	public void AddData(File f) {
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
			matches.add(m);
		}
		sc.close();
	}
	
}