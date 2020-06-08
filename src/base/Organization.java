package base;
import java.util.*;


public class Organization extends Team implements Rated {
	ArrayList<EloDate> eloRatings;
	List<String> playerCodes;
	public Organization(String teamCode) {
		super(teamCode);
		initializeRating();
		playerCodes = new ArrayList<String>();
	}
	@Override
	public void initializeRating() {
		eloRatings = new ArrayList<EloDate>();
	}
	@Override
	public double getElo() {
		if(eloRatings.size() == 0) {
			return 1500;
		}
		return eloRatings.get(eloRatings.size() - 1).elo;
	}
	@Override
	public void updateElo(double update, String date) {
		eloRatings.add(new EloDate(date, getElo() + update));
	}
	
}
