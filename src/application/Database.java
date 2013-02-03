package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Database is a class that specifies the interface to the movie database. Uses
 * JDBC and the MySQL Connector/J driver.
 */
public class Database {
	/**
	 * The database connection.
	 */
	private Connection conn;

	/**
	 * An SQL statement object.
	 */
	private Statement stmt;

	/**
	 * Create the database interface object. Connection to the database is
	 * performed later.
	 */
	public Database() {
		conn = null;
	}

	/**
	 * Open a connection to the database, using the specified user name and
	 * password.
	 * 
	 * @param userName
	 *            The user name.
	 * @param password
	 *            The user's password.
	 * @return true if the connection succeeded, false if the supplied user name
	 *         and password were not recognized. Returns false also if the JDBC
	 *         driver isn't found.
	 */
	public boolean openConnection(String userName, String password) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(
					"jdbc:mysql://puccini.cs.lth.se/" + userName, userName,
					password);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * Close the connection to the database.
	 */
	public void closeConnection() {
		try {
			if (conn != null) {
				conn.close();
			}
		} catch (SQLException e) {
		}
		conn = null;
	}

	/**
	 * Check if the connection to the database has been established
	 * 
	 * @return true if the connection has been established
	 */
	public boolean isConnected() {
		return conn != null;
	}

	/* --- insert own code here --- */

	public boolean login(String userId) {
		String sql = "SELECT username FROM Users WHERE username = ?";
		CurrentUser user = CurrentUser.instance();
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, userId);

			ResultSet rs = ps.executeQuery();
			user.loginAs((rs.first()) ? rs.getString("username") : null);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return user.isLoggedIn();
	}

	public ArrayList<String> getMovieList() {
		ArrayList<String> m = new ArrayList<String>();
		String sql = "SELECT title FROM Movies";
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next()){
				m.add(rs.getString("title"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return m;
	}

	public ArrayList<String> getPerformanceDateList(String movieName) {
		ArrayList<String> pd = new ArrayList<String>();
		String sql = "SELECT date FROM Performances WHERE movie = ?";
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, movieName);
			
			ResultSet rs = ps.executeQuery();
			while(rs.next()){
				pd.add(rs.getString("date"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return pd;
	}

	public Performance getPerformance(String movieName, String date) {
		String sql = "SELECT * FROM Performances " +
					 "INNER JOIN Theaters ON Performances.theater = Theaters.name " +
					 "WHERE movie = ? AND date = ?";
		
		Performance p = new Performance();
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, movieName);
			ps.setString(2, date);
			
			ResultSet rs = ps.executeQuery();
			if(rs.first()) return  p.setPerformanceId(rs.getInt("performance_id"))
									.setMovie(rs.getString("movie"))
									.setTheater(rs.getString("theater"))
									.setDate(rs.getString("date"))
									.setAvailableSeats(rs.getInt("available_seats"))
									.setReservedSeats(rs.getInt("reserved_seats"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Introduces concurrency errors since we don't use transactions.
	 * 
	 * @param movieName
	 * @param date
	 * @return
	 */
	public boolean createReservation(String movieName, String date) {
		Performance p = getPerformance(movieName, date);

		String insertReservation = "INSERT INTO Reservations VALUES (NULL, ?, ?)";
		String updateSeats = "UPDATE Performances SET reserved_seats = reserved_seats+1 WHERE performance_id = ?";
		
		if(p.getFreeSeats() <= 0) return false;
		try {
			PreparedStatement ps1 = conn.prepareStatement(insertReservation);
			PreparedStatement ps2 = conn.prepareStatement(updateSeats);
			
			ps1.setString(1, p.toString());
			ps1.setString(2, CurrentUser.instance().getCurrentUserId());
			
			ps2.setString(1, p.toString());
			
			if(ps1.executeUpdate() == 1){
				ps2.executeUpdate();
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

}
