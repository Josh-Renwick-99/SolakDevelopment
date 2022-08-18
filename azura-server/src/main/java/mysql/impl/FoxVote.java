package mysql.impl;

import com.ruse.GameSettings;
import com.ruse.motivote3.doMotivote;
import com.ruse.world.World;
import com.ruse.world.content.PlayerLogs;
import com.ruse.world.content.VoteBossDrop;
import com.ruse.world.entity.impl.player.Player;

import java.sql.*;


public class FoxVote implements Runnable {

	public static final String HOST = "104.161.43.58";
	public static final String USER = "wvrrwzni_master";
	public static final String PASS = "85EDE6Hyn9nHpT4HaAD8";
	public static final String DATABASE = "wvrrwzni_votedb";

	private Player player;
	private Connection conn;
	private Statement stmt;

	public FoxVote(Player player) {
		this.player = player;
	}

	@Override
	public void run() {
		try {
			if (!connect(HOST, DATABASE, USER, PASS)) {
				return;
			}

			String name = player.getUsername().replace(" ", " ");
			ResultSet rs = executeQuery("SELECT * FROM votes WHERE username='"+name+"' AND claimed=0 AND voted_on != -1");

			int points = 0;
			while (rs.next()) {
				String ipAddress = rs.getString("ip_address");
				int siteId = rs.getInt("site_id");

				// Regular 1 vote
				points++;

				System.out.println("[Vote] Vote claimed by "+name+". (sid: "+siteId+", ip: "+ipAddress+")");
				rs.updateInt("claimed", 1); // do not delete otherwise they can reclaim!
				rs.updateRow();
			}
			if (points >= 2){
				points += (points / 2);
			}

			// Log the actual votes
			PlayerLogs.logPlayerVotes(player.getUsername(), "Player claimed votes: " + points + ", IP: " + player.getHostAddress());


			// Double vote pet
			if (player.getSummoning() != null && player.getSummoning().getFamiliar() != null
					&& player.getSummoning().getFamiliar().getSummonNpc().getId() == 8802) {
				points *= 2;
				player.getPacketSender()
						.sendMessage("<shad=1>@yel@You get an extra vote scroll because of your pet!");
			}
			// ::voteon command
			if(GameSettings.DOUBLE_VOTE) {
				points *= 2;
			}

			if (points > 0) {
				player.getInventory().add(23020, points);
				player.getPacketSender().sendMessage("Thank you for voting!");
				player.getDailyRewards().handleVote();

				player.lastVoteTime = System.currentTimeMillis();

				player.setVoteCount(doMotivote.getVoteCount() + 1);

				if (doMotivote.getVoteCount() >= 50) {
					VoteBossDrop.handleSpawn();
				}
				World.sendMessage("<img=5>" + player.getUsername() + " has voted for " + points
						+ " Vote scrolls! ::vote now to support the server.");

			}
			destroy();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public boolean connect(String host, String database, String user, String pass) {
		try {
			this.conn = DriverManager.getConnection("jdbc:mysql://"+host+":3306/"+database, user, pass);
			return true;
		} catch (SQLException e) {
			System.out.println("Failing connecting to database!");
			return false;
		}
	}

	public void destroy() {
        try {
    		conn.close();
        	conn = null;
        	if (stmt != null) {
    			stmt.close();
        		stmt = null;
        	}
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

	public int executeUpdate(String query) {
        try {
        	this.stmt = this.conn.createStatement(1005, 1008);
            int results = stmt.executeUpdate(query);
            return results;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return -1;
    }

	public ResultSet executeQuery(String query) {
        try {
        	this.stmt = this.conn.createStatement(1005, 1008);
            ResultSet results = stmt.executeQuery(query);
            return results;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

}
