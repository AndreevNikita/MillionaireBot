package space.andreev.millionairebot;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;

import space.andreev.millionairebot.handlers.MillionaireGameHandler.Question;

public class DB {

	private static final String url = "jdbc:mysql://localhost:3306/millionaire";
    private static final String user = "<Пользователь бд>";
    private static final String password = "<Пароль для бд>";

    // JDBC variables for opening and managing connection
    private static Connection con;
    private static Statement stmt;
    private static ResultSet rs;
    
    public static Thread reconnectThread = new Thread(new Runnable() {
    	
		@Override
		public void run() {
			while(!reconnectThread.isInterrupted()) {
				try {
					stmt.executeQuery("SELECT id FROM questions LIMIT 0").close();
				} catch(Exception e) {
					connect();
				}
				try {Thread.sleep(60000);} catch(Exception e) {}
			}
		}
	});
    
    public static boolean connect() {
    	try {
	    	con = DriverManager.getConnection(url, user, password);
	        stmt = con.createStatement();
	        if(!reconnectThread.isAlive())
	        	reconnectThread.start();
	        return true;
    	} catch(Exception e) {
    		e.printStackTrace();
    		return false;
    	}
    }
    
    public static void updateUploadedId(String path, int network, String fileId) {
    	try {
	    	ResultSet rs = stmt.executeQuery("SELECT id FROM uploaded_files WHERE path = '" + path + "' AND network = " + network);
			if(rs.next()) {
				stmt.execute("UPDATE uploaded_files SET file_id = '" + fileId + "' WHERE id = " + rs.getInt(1));
				rs.close();
	    	} else {
	    		stmt.execute("INSERT INTO uploaded_files (path, network, file_id) VALUES ('" + path + "', " + network + ", '" + fileId + "')");
	    	}
    	} catch(Exception e) {
			e.printStackTrace();
		}
    }
    
    public static String getUploadedId(String path, int network) {
    	path = path.toLowerCase();
    	try {
			ResultSet rs = stmt.executeQuery("SELECT file_id FROM uploaded_files WHERE path = '" + path + "' AND network = " + network);
			if(!rs.next()) {
				rs.close();
				return null;
			} else {
				String fileId = rs.getString(1);
				rs.close();
				return fileId;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
    }
    
    public static Question getQuestionById(int id) {
    	try {
			ResultSet rs = stmt.executeQuery("SELECT id, number, text, photo, answers FROM questions WHERE id = " + id);
			rs.next();
			Question question = new Question(rs.getInt(1), rs.getInt(2), rs.getString(3), rs.getString(4), true,rs.getString(5).split(":"));
			rs.close();
			return question;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
    }
    
    public static Question getRandomQuestion(int number) {
    	try {
    		ResultSet rs1 = stmt.executeQuery("SELECT COUNT(*) FROM questions WHERE number = " + number);
    		rs1.next();
    		int rowNumber = (new Random().nextInt(rs1.getInt(1)));
			ResultSet rs = stmt.executeQuery("SELECT id, number, text, photo, answers FROM questions WHERE number = " + number + " ORDER BY id LIMIT 1 OFFSET " + rowNumber);
			rs1.close();
			rs.next();
			Question question = new Question(rs.getInt(1), rs.getInt(2), rs.getString(3), rs.getString(4), true, rs.getString(5).split(":"));
			rs.close();
			return question;
		} catch (SQLException e) {
			System.out.println("SELECT id, number, text, answers FROM questions WHERE number = " + number + " ORDER BY RAND() LIMIT 1");
			e.printStackTrace();
			return null;
		}
    	
    } 
    
    public static void setHistory(UserInfo userInfo, String history) {
    	try {
    		stmt.execute("UPDATE users SET history = '" + history + "' WHERE id = " + userInfo.id);
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
    }
    
    public static UserInfo registerUser(int inNetworkId, int network) {
    	try {
    		stmt.execute("INSERT INTO users (network, in_network_id) VALUES (" + network + ", '" + inNetworkId + "')");
    		return getUserInfo(inNetworkId, network);
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
    	return null;
    }
	
	public static UserInfo getUserInfo(int inNetworkId, int network) {
		try {
			ResultSet rs = stmt.executeQuery("SELECT id, properties, history FROM users WHERE network = " + network + " AND in_network_id = " + inNetworkId);
			if(rs.next()) {
				UserInfo userInfo = new UserInfo(rs.getInt(1), network, inNetworkId, rs.getInt(2), rs.getString(3));
				rs.close();
				return userInfo;
			} else {
				return registerUser(inNetworkId, network);
			}
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static class UserInfo {
		public final int properties;
		public String history;
		public final int inNetworkId;
		public final int network;
		public final int id;
		
		public UserInfo(int id, int network, int inNetworkId, int properties, String history) {
			this.id = id;
			this.inNetworkId = inNetworkId;
			this.network = network;
			this.properties = properties;
			this.history = history;
		}
		
		public boolean hasHistory() {
			return history != null && !history.isEmpty();
		}
	}
	
}
