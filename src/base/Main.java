package base;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Main {

	
	public static void main(String[] args) {
		TestPredictions();
	}
	public static void generateComboRatings() {
		StatLoader sl = new StatLoader("GameData/2010/");
		List<Match> matches = sl.getMatches();
		List<PlateAppearance> appearances = sl.getAppearances();
		
		RatingRun ratings = new RatingRun(RatingMethod.EloCombined, matches, appearances);
		ratings.processAllMatches(matches);
		
		ratings.printRatings();
		ratings.playerGameEloDateToCSV("2010Combo.csv", 1);
	}

	public static void TestPredictions() {
		StatLoader sl1 = new StatLoader("GameData/2000-2018/");
		StatLoader sl2 = new StatLoader("GameData/2019/");
		List<Match> matches1 = sl1.getMatches();
		List<Match> matches2 = sl2.getMatches();
		List<PlateAppearance> appearances1 = sl1.getAppearances();
		List<PlateAppearance> appearances2 = sl2.getAppearances();
		
		RatingRun ratings1 = new RatingRun(RatingMethod.EloCombined, matches1, appearances1);
		ratings1.processAllMatches(matches1);
		//0.5727835
		
		RatingRun ratings2 = new RatingRun(RatingMethod.EloBlind, matches1);
		ratings2.processAllMatches(matches1);
		//0.5661856

		RatingRun ratings3 = new RatingRun(appearances1);
		ratings3.processAllAppearances(appearances1, "Weighted");
		//0.5727835
		
		RatingRun ratings4 = new RatingRun(appearances1);
		ratings4.processAllAppearances(appearances1, "TTO");
		//0.5538144
		
		RatingRun ratings5 = new RatingRun(appearances1);
		ratings5.processAllAppearances(appearances1, "BIP");
		//0.5764949
		
		System.out.println(ratings1.predictMatches(matches2));
		System.out.println(ratings2.predictMatches(matches2));
		System.out.println(ratings3.predictMatches(matches2));
		System.out.println(ratings4.predictMatches(matches2));
		System.out.println(ratings5.predictMatches(matches2));
	}
}
