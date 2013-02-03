package application;

public class Performance {

	private long performanceId;
	
	private String date;
	
	private String movie;
	
	private String theater;
	
	private int availableSeats;
	
	private int reservedSeats;
	

	public long getPerformanceId() {
		return performanceId;
	}
	
	public Performance setPerformanceId(long performanceId) {
		this.performanceId = performanceId;
		return this;
	}
	
	public String getDate() {
		return date;
	}
	
	public Performance setDate(String date) {
		this.date = date;
		return this;
	}
	
	public String getMovie() {
		return movie;
	}
	
	public Performance setMovie(String movie) {
		this.movie = movie;
		return this;
	}
	
	public String getTheater() {
		return theater;
	}
	
	public Performance setTheater(String theater) {
		this.theater = theater;
		return this;
	}
	
	
	public int getAvailableSeats() {
		return availableSeats;
	}

	public Performance setAvailableSeats(int availableSeats) {
		this.availableSeats = availableSeats;
		return this;
	}

	public int getReservedSeats() {
		return reservedSeats;
	}

	public Performance setReservedSeats(int reservedSeats) {
		this.reservedSeats = reservedSeats;
		return this;
	}

	public int getFreeSeats() {
		return availableSeats - reservedSeats;
	}
	
    public String toString(){
    	return  Long.toString(getPerformanceId());
    }
	
}
