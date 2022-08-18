package mysql.impl;

import com.ruse.GameSettings;
import com.ruse.model.PlayerRights;
import com.ruse.model.definitions.ItemDefinition;
import com.ruse.util.Misc;
import com.ruse.world.World;
import com.ruse.world.content.PlayerLogs;
import com.ruse.world.entity.impl.player.Player;

import java.sql.*;

public class Donation implements Runnable {

    public static final String HOST = "104.161.43.58";
    public static final String USER = "wvrrwzni_master";
    public static final String PASS = "85EDE6Hyn9nHpT4HaAD8";
    public static final String DATABASE = "wvrrwzni_store";

    private Player player;
    private Connection conn;
    private Statement stmt;

    /**
     * The constructor
     *
     * @param player
     */
    public Donation(Player player) {
        this.player = player;
    }

    public static final int SAPPHIRE_DONATION_AMOUNT = 10;
    public static final int EMERALD_DONATION_AMOUNT = 50;
    public static final int RUBY_DONATION_AMOUNT = 250;
    public static final int DIAMOND_DONATION_AMOUNT = 1000;
    public static final int ONYX_DONATION_AMOUNT = 2500;
    public static final int ZENYTE_DONATION_AMOUNT = 5000;

    public static void checkForRankUpdate(Player player) {
        if (player.getRights().isStaff()) {
            return;
        }
        PlayerRights rights = null;
        if (player.getAmountDonated() >= SAPPHIRE_DONATION_AMOUNT)
            rights = PlayerRights.SAPPHIRE_DONATOR;
        if (player.getAmountDonated() >= EMERALD_DONATION_AMOUNT)
            rights = PlayerRights.EMERALD_DONATOR;
        if (player.getAmountDonated() >= RUBY_DONATION_AMOUNT)
            rights = PlayerRights.RUBY_DONATOR;
        if (player.getAmountDonated() >= DIAMOND_DONATION_AMOUNT)
            rights = PlayerRights.DIAMOND_DONATOR;
        if (player.getAmountDonated() >= ONYX_DONATION_AMOUNT)
            rights = PlayerRights.ONYX_DONATOR;
        if (player.getAmountDonated() >= ZENYTE_DONATION_AMOUNT)
            rights = PlayerRights.ZENYTE_DONATOR;
        if (rights != null && rights != player.getRights()) {
            player.getPacketSender().sendMessage(
                    "You've become a " + Misc.formatText(rights.toString().toLowerCase()) + "! Congratulations!");
            player.setRights(rights);
            player.getPacketSender().sendRights();
        }
    }

    public void run() {
        try {
            if (!connect(HOST, "wvrrwzni_store", "wvrrwzni_master", "85EDE6Hyn9nHpT4HaAD8")) {
                return;
            }
            System.out.println("connected to store database");

            player.lastDonationClaim = System.currentTimeMillis() + 30000;
            String name = player.getUsername().replace("_", " ");
            ResultSet rs = executeQuery("SELECT * FROM payments WHERE player_name='"+name+"' AND status='Completed' AND claimed=0");

            while (rs.next()) {
                int item_number = rs.getInt("item_number");
                double paid = rs.getDouble("paid");
                int quantity = rs.getInt("quantity");


                if (GameSettings.B2GO) {
                    if (quantity >= 2) {
                        quantity += (quantity / 2);
                    }
                }
                if (GameSettings.BOGO) {
                    quantity *= 2;
                }

                int id = -1;
                switch (item_number) {
/*
 * 			player.incrementAmountDonated(funds);
			player.incrementAmountDonatedToday(funds);
			player.getPointsHandler().setDonatorPoints(funds, true);
			player.getPacketSender().sendMessage("Your account has gained funds worth $" + funds
					+ ". Your total is now at $" + player.getAmountDonated() + ".");
			checkForRankUpdate(player);
			PlayerPanel.refreshPanel(player);
 */

                    case 10:
                            player.getInventory().add(23057, quantity);
                            id = 23057;
                        break;
                    case 1024:
                            player.getInventory().add(23058, quantity);
                            id = 23058;
                        break;
                    case 1025:
                            player.getInventory().add(23059, quantity);
                            id = 23059;
                        break;
                    case 1026:
                            player.getInventory().add(23060, quantity);
                            id = 23060;
                        break;
                    default:
                        player.sendMessage("No donation was found under your name.");
                        return;
                }

                PlayerLogs.logPlayerDonations(player.getUsername(), "Donated: $" + paid
                        + ", Item: " + ItemDefinition.forId(id).getName() + ", Amount: " + item_number + ", ID: " + id + ", item_number: " + item_number);

                player.sendMessage("Thanks for donating!");

                if (paid >= 5) {
                    World.sendMessage("<img=5><shad=1>@yel@Donation: @blu@" + player.getUsername()
                            + "@or2@ has donated! @red@::Donate@or2@ now to show support for @red@Solak!");
                } else {
                    player.sendMessage("Your donation message is only displayed for purchases over $5");
                }
/*
                if (paid >= 25 && paid <= 99) {
                    player.getInventory().add(23002, (int) (paid / 25));
                    player.sendMessage("You received some extra rewards for donating!");
                }
                if (paid >= 100) {
                    player.getInventory().add(22121, (int) (paid / 100));
                    player.sendMessage("You received some extra rewards for donating!");
                }*/

              /* if (paid >= 10 && paid <= 19){
                    player.getInventory().add(15003, 1);
                }else if (paid >= 20 && paid <= 49){
                    player.getInventory().add(15004, 1);
                }else if (paid >= 50 && paid <= 99){
                    player.getInventory().add(15004, 1);
                }else if (paid >= 100 && paid <= 249){
                    player.getInventory().add(15004, 1);
                    player.getInventory().add(15002, 1);
                    player.getInventory().add(15003, 1);
                }else if (paid >= 250 && paid <= 499){
                    player.getInventory().add(15004, 5);
                }else if (paid >= 500){
                    player.getInventory().add(15004, 5);
                    player.getInventory().add(10943, 1);
                }
*/

                rs.updateInt("claimed", 1);
                rs.updateRow();
            }
            destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param host     the host ip address or url
     * @param database the name of the database
     * @param user     the user attached to the database
     * @param pass     the users password
     * @return true if connected
     */
    public boolean connect(String host, String database, String user, String pass) {
        try {
            this.conn = DriverManager.getConnection("jdbc:mysql://" + host + ":3306/" + database, user, pass);
            return true;
        } catch (SQLException e) {
            System.out.println("Failing connecting to database!");
            return false;
        }
    }

    /**
     * Disconnects from the MySQL server and destroy the connection
     * and statement instances
     */
    public void destroy() {
        try {
            conn.close();
            conn = null;
            if (stmt != null) {
                stmt.close();
                stmt = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Executes an update query on the database
     *
     * @param query
     * @see {@link Statement#executeUpdate}
     */
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

    /**
     * Executres a query on the database
     *
     * @param query
     * @return the results, never null
     * @see {@link Statement#executeQuery(String)}
     */
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

