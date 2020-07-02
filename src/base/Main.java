package base;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Main {

	
	public static void main(String[] args) {
		StatLoader sl = new StatLoader("GameData/2010/");
		List<Match> matches = sl.getMatches();
		List<PlateAppearance> appearances = sl.getAppearances();
		
		RatingRun ratings = new RatingRun(appearances);
		ratings.processAllAppearances(appearances, "Weighted");
		
		ratings.printRatings();

//		ratings.printPositionDiff();
//		ratings.printPlayerRatings("zobrb001");
		ratings.playerGameEloDateToCSV("2010Plays.csv", 1);
	}
}
