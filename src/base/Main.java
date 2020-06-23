package base;

import java.util.*;

public class Main {

	
	public static void main(String[] args) {
		StatLoader sl = new StatLoader("C:/Users/logan/Documents/Research/BaseballStats/");
		
		List<Match> matches = sl.getMatches();
		List<PlateAppearance> apps = sl.getAppearances();
		
//		RatingRun ratings = new RatingRun(apps);
//		RatingRun ratings = new RatingRun(RatingMethod.EloBlind, matches);
		RatingRun ratings = new RatingRun(RatingMethod.EloCombined, matches, apps);
//		ratings.processAllAppearances(apps, "Weighted");
		ratings.processAllMatches(matches);
		//printAllOrgs();
		ratings.printRatings();
//		ratings.printPositionDiff();
//		ratings.printPlayerRatings("zobrb001");
		ratings.playerGameEloDateToCSV("C:/Users/logan/Documents/Game Dev/Eclipse Workspace/ELOCompute/2010Combo.csv", 1);
	}
}
