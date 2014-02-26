package datareader;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;

public class PostGisDBConnect {

	private static PostGisDBConnect instance = null;
	private static Connection conn;

	private PostGisDBConnect() {
		try {
			Class.forName("org.postgresql.Driver");
			String url = "jdbc:postgresql://localhost:5432/runkeeper_test";
			conn = DriverManager.getConnection(url, "postgres", "");
			((org.postgresql.PGConnection) conn).addDataType("geometry", Class.forName("org.postgis.PGgeometry"));
			((org.postgresql.PGConnection) conn).addDataType("box3d", Class.forName("org.postgis.PGbox3d"));

		} catch (ClassNotFoundException cnfErr) {
			cnfErr.printStackTrace();
		} catch (SQLException err) {
			err.printStackTrace();
		}
	}

	private static PostGisDBConnect getInstance() {
		if (instance == null)
			return new PostGisDBConnect();
		else
			return instance;
	}

	public static Connection getConnection() {
		return getInstance().conn;
	}
}
