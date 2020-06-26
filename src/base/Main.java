package base;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Main {

	
	public static void main(String[] args) {
		StatLoader sl = new StatLoader("C:/Users/logan/Documents/Research/BaseballStats/2000-2018/");
		StatLoader sl2 = new StatLoader("C:/Users/logan/Documents/Research/BaseballStats/2019/");
		
		List<Match> matches = sl.getMatches();
		List<PlateAppearance> apps = sl.getAppearances();
		

		List<Match> matches2 = sl2.getMatches();
		List<PlateAppearance> apps2 = sl2.getAppearances();
		
		RatingRun ratings = new RatingRun(apps);
		RatingRun ratings2 = new RatingRun(apps2);
//		RatingRun ratings = new RatingRun(RatingMethod.EloBlind, matches);
//		RatingRun ratings2 = new RatingRun(RatingMethod.EloBlind, matches2);
//		RatingRun ratings = new RatingRun(RatingMethod.EloCombined, matches, apps);
//		RatingRun ratings2 = new RatingRun(RatingMethod.EloCombined, matches2, apps2);
		
//		ratings.processAllAppearances(apps, "Weighted");
//		ratings2.processAllAppearances(apps2, "Weighted");
//		ratings.processAllMatches(matches);
//		ratings2.processAllMatches(matches2);
		
		ArrayList<Player> diffs = new ArrayList<Player>();
		
		for (Player p : ratings2.players.values()) {
			if (ratings.players.containsKey(p.playerCode)) {
				Player newP = new Player(p.playerCode, p.playerName, p.pos);
				System.out.println(p.getElo() + " " + ratings.players.get(p.playerCode).getElo());
				newP.updateElo(-1500+(p.getElo() - ratings.players.get(p.playerCode).getElo()), "");
				diffs.add(newP);
			}
		}
		
		try {
			FileWriter csvWriter = new FileWriter("Test.csv");
			csvWriter.append("Name,Position,Diff");
			for (Player p : diffs) {
				csvWriter.append("\n" + p.playerName + "," + p.pos + "," + p.getElo());
			}
			csvWriter.flush();
			csvWriter.close();
		} catch (IOException e) {
			
		}
		
//		ratings.printRatings();
//		ratings.printPositionDiff();
//		ratings.printPlayerRatings("zobrb001");
//		ratings.playerGameEloDateToCSV("C:/Users/logan/Documents/Game Dev/Eclipse Workspace/ELOCompute/2010Blind.csv", 1);
	}
}
