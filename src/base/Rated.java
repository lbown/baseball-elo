package base;

public interface Rated {
	/**
	 * Set or reset the rating to the default (1500) and create a new array to store rating updates
	 */
	void initializeRating();
	
	/**
	 * 
	 * @return The most recent update to this object's rating
	 */
	double getElo();
	/**
	 * @param update amount to adjust elo
	 */
	void updateElo(double update, String date);
	
	class EloDate {
		double elo;
		String date;
		public EloDate(String date, double elo) {
			this.elo = elo;
			this.date = date;
		}
	}
}
