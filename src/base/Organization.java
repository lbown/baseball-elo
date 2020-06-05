package base;
import java.util.*;

public class Organization extends Team implements Rated {
	ArrayList<Double> eloRatings;
	List<String> playerCodes;
	public Organization(String teamCode) {
		super(teamCode);
		initializeRating();
		playerCodes = new ArrayList<String>();
	}
	@Override
	public void initializeRating() {
		eloRatings = new ArrayList<Double>();
		eloRatings.add(1500.);
	}
	@Override
	public double getElo() {
		return eloRatings.get(eloRatings.size() - 1);
	}
	@Override
	public void updateElo(double update) {
		eloRatings.add(getElo() + update);
	}
	
	
}
