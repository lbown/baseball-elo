package base;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Main {

	
	public static void main(String[] args) {
		generatePlateAppRatings();
	}
	public static void countWins(String name) {
		int numWins = 0;
		int numGames = 0;
		StatLoader sl = new StatLoader("GameData/2010-2019/");
		List<Match> matches = sl.getTruncMatches();
		for (Match m : matches) {
			if(m.team1.startingPlayerCodes.contains(name)) {
				numGames++;
				if(m.team1Wins) numWins++;
			}
			if(m.team2.startingPlayerCodes.contains(name)) {
				numGames++;
				if(!m.team1Wins) numWins++;
			}
		}
		System.out.println(numGames + " " + numWins);
	}
	public static void computeDiff() {
		StatLoader sl = new StatLoader("GameData/1916/");
		List<Match> matches = sl.getMatches();
		List<PlateAppearance> appearances = sl.getAppearances();
		RatingRun ratings = new RatingRun(RatingMethod.EloGammaAdjust, matches, appearances);
		ratings.processAllAppearances(appearances, "Weighted");
		ratings.printPositionDiff();
	}
	
	public static void generateBlindRatings() {
		StatLoader sl = new StatLoader("GameData/2010/");
		List<Match> matches = sl.getMatches();
		List<PlateAppearance> appearances = sl.getAppearances();
		
		RatingRun ratings = new RatingRun(RatingMethod.EloBlind, matches, appearances);
		ratings.processAllMatches(matches);
		
		ratings.printRatings();
		ratings.playerGameEloDateToCSV("2010Blind.csv", 1);
	}
	public static void truncBlindRatings() {
		StatLoader sl = new StatLoader("GameData/2010-2019/");
		List<Match> truncated = sl.getTruncMatches();
		List<PlateAppearance> appearances = sl.getAppearances();
		double numPredicted = 0;
		double numMatches = 0;
		
		RatingRun ratings = new RatingRun(RatingMethod.EloBlindTrunc, truncated, appearances);
		ratings.processAllMatches(truncated);
		
		for (Match m : truncated) {
			if (!m.isCorrupt && !m.gameTied) {
				numMatches++;
				if (m.predictedBy5th) {
					numPredicted++;
				}
			}
		}
		
		ratings.printRatings();
		System.out.println("Fraction predicted by 5th inning score: " + numPredicted/numMatches);
		ratings.playerGameEloDateToCSV("2010sTrunc.csv", 1);
	}
	public static void generatePBPTruncRatings() {
		
	}
	
	public static void generateComboRatings() {
		StatLoader sl = new StatLoader("GameData/2010-2019/");
		List<Match> matches = sl.getTruncMatches();
		List<PlateAppearance> appearances = sl.getAppearances();
		
		RatingRun ratings = new RatingRun(RatingMethod.EloCombined, matches, appearances);
		ratings.processAllMatches(matches);
		
		ratings.printRatings();
		ratings.playerGameEloDateToCSV("2010sCombo.csv", 1);
	}
	public static void generatePlateAppRatings() {
		StatLoader sl = new StatLoader("GameData/2010-2019/");
		List<Match> matches = sl.getMatches();
		List<PlateAppearance> appearances = sl.getAppearances();
		
		RatingRun ratings = new RatingRun(RatingMethod.EloGammaAdjust, matches, appearances);
		ratings.processAllMatches(matches);
		
		ratings.printRatings();
		ratings.playerGameEloDateToCSV("2010sPlayByPlay.csv", 1);
	}
	public static void FindMaxInning() {
		int[] counts = new int[10];
		StatLoader sl = new StatLoader("GameData/2000-2018/");
		List<Match> matches = sl.getMatches();
		for (Match m : matches) {
			counts[m.firstSub]++;
		}
		for (int i : counts) {
			System.out.println(i);
		}
	}
	public static void TestPredictions() {
		StatLoader sl1 = new StatLoader("GameData/2000-2018/");
		StatLoader sl2 = new StatLoader("GameData/2019/");
		List<Match> matches1 = sl1.getMatches();
		List<Match> trunc1 = sl1.getTruncMatches();
		List<Match> matches2 = sl2.getMatches();
		
		List<PlateAppearance> appearances1 = sl1.getAppearances();
		List<PlateAppearance> appearances2 = sl2.getAppearances();
		
		List<Match> both = new ArrayList<Match>(matches1);
		both.addAll(matches2);
		List<PlateAppearance> bothApps = new ArrayList<PlateAppearance>(appearances1);
		bothApps.addAll(appearances2);
		
		RatingRun ratings1 = new RatingRun(RatingMethod.EloCombined, both, bothApps);
		ratings1.processAllMatches(trunc1);
		//0.5727835
		
		RatingRun ratings2 = new RatingRun(RatingMethod.EloBlindTrunc, both, bothApps);
		ratings2.processAllMatches(trunc1);
		//0.5661856

		RatingRun ratings3 = new RatingRun(RatingMethod.EloGammaAdjust, both, bothApps);
		ratings3.processAllMatches(trunc1);
		//0.5727835
		
		
		System.out.println("Combined " + ratings1.predictMatches(matches2));
		System.out.println("Trunc " + ratings2.predictMatches(matches2));
		System.out.println("Plays " + ratings3.predictMatches(matches2));
	}
	public static void TestTruncPredictions() {
		StatLoader sl1 = new StatLoader("GameData/2000-2018/");
		StatLoader sl2 = new StatLoader("GameData/2019/");
		List<Match> train = sl1.getTruncMatches();
		List<Match> test = sl2.getTruncMatches();
		
		List<Match> both = new ArrayList<Match>(train);
		both.addAll(test);
		RatingRun ratings = new RatingRun(RatingMethod.EloBlindTrunc, both);
		ratings.processAllMatches(train);
		
		System.out.println(ratings.predictMatches(test));
	}
	public static void HomeFieldPctWon() {
		StatLoader sl = new StatLoader("GameData/2000-2018/");
		List<Match> ms = sl.getMatches();
		int visWins = 0;
		for(Match m : ms) {
			if (m.team1Wins) visWins++;
		}
		System.out.println((double)visWins/ms.size());
	}
}
