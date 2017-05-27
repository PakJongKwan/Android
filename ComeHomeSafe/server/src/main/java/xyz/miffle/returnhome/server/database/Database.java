package xyz.miffle.returnhome.server.database;

import org.json.simple.JSONObject;
import xyz.miffle.returnhome.server.comm.Commands;
import xyz.miffle.returnhome.server.util.Data;
import xyz.miffle.returnhome.server.util.Logger;

import java.sql.*;
import java.util.HashMap;

/**
 * Created by dsm on 2017-04-28.
 */
public class Database {
    private static boolean opened;
    private static Statement statement;

    private static final String TAG = "Database";

    public static boolean open() {
        Connection connection;

        try {
            Class.forName("org.sqlite.JDBC");
        } catch(Exception e) {
            e.printStackTrace();
        }

        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + DatabaseConfig.DATABASEFILE_NAME);
        } catch (SQLException e) {
            Logger.log("데이터베이스 라이브러리가 존재하지 않습니다.");
            return false;
        }

        try {
            statement = connection.createStatement();
        } catch (SQLException e) {
            Logger.log("Statement 생성 실패");
            return false;
        }

        opened = true;

        createTables();

        return true;
    }

    public static boolean isOpened() {
        return opened;
    }

    public static boolean createTables() {
        String accountCommand = "CREATE TABLE account(_id auto_increment integer, " +
                "id TEXT, pw TEXT, name TEXT, phone TEXT, address TEXT, emergency_phone TEXT, PRIMARY KEY(_id, id));";
        String friendsListCommand = "CREATE TABLE friends(sender TEXT, receiver TEXT, accepted TEXT, PRIMARY KEY(sender, receiver));";
        String locationCommands = "CREATE TABLE locations(_id integer primary key, user TEXT, x TEXT, y TEXT);";

        try {
            statement.execute(accountCommand);
            statement.execute(friendsListCommand);
            statement.execute(locationCommands);
        } catch (SQLException e) {
            return false;
        }

        return true;
    }

    public static boolean login(String id, String pw) {

        if (!isOpened()) {
            Logger.log(TAG, "데이터베이스가 초기화 되어있지 않습니다.");
            return false;
        }

        if (id != null && pw != null) {
            String command = "select pw from account where id=\"" + id + "\"";
            System.out.println(command);
            try {
                ResultSet result = statement.executeQuery(command);
                String password = result.getString(1);
                if (pw.equals(password)) {
                    return true;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    public static JSONObject getLastestLocation(String id) {
        String friendCommand = "select * from locations where user=\"" + id + "\" ORDER BY _id DESC LIMIT 1";


        try {
            return Data.resultSetToJson(statement.executeQuery(friendCommand), Commands.DATA) ;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static boolean insertLocation(String id, String x, String y) {
        String friendCommand = "insert into locations values(null, " + "\"" + id + "\", \"" + x + "\", \"" +  y + "\");";

        System.out.println(friendCommand);

        try {
            statement.execute(friendCommand);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
    public static JSONObject getPersonal(String id, String column) {

        String getCommand = "SELECT " + column + " from account where id=\"" + id + "\"";


        try {
            return Data.resultSetToJson(statement.executeQuery(getCommand), Commands.DATA);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static JSONObject userSearch(String id) {
        String userSearchCommand = "select id from account where id like \"" + id + "%\"";

        System.out.println(userSearchCommand);
        try {
            return Data.resultSetToJson(statement.executeQuery(userSearchCommand), Commands.DATA);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static boolean addFriend(String sender, String receiver) {

        String checkDuplicate = "select * from friends where sender=\"" + receiver + "\" and receiver=\"" + sender + "\"";

        System.out.println(checkDuplicate);

        try {
            ResultSet result = statement.executeQuery(checkDuplicate);

            int rowCount = 0;
            while(result.next()) {
                rowCount += 1;
            }

            if (rowCount != 0) {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String friendCommand = "insert into friends values(\"" + sender + "\", \"" + receiver + "\", \"0\")";

        System.out.println(friendCommand);
        try {
            statement.execute(friendCommand);
        } catch (SQLException e) {
            return false;
        }

        return true;
    }

    public static boolean acceptFriend(String sender, String receiver) {
        String updateCommands = "UPDATE friends set accepted=\"1\" where sender=" +
                "\"" + sender + "\" and receiver=\"" + receiver + "\"";

        System.out.println(updateCommands);
        try {
            statement.execute(updateCommands);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static JSONObject getUnAcceptedFriendsList(String user) {
        String commands = "SELECT * from friends where receiver=\"" + user + "\" and accepted=\"0\"";;

        System.out.println(commands);

        try {
            return Data.resultSetToJson(statement.executeQuery(commands), Commands.DATA);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static JSONObject getFriends(String user) {
        String commands = "SELECT * from friends where (receiver=\"" + user +
                "\" or sender=\"" + user + "\") and accepted=\"1\"";;

        System.out.println(commands);

        try {
            return Data.resultSetToJson(statement.executeQuery(commands), Commands.DATA);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static boolean updatePersonal(String id, String column, String data) {
        String updateCommands = "UPDATE account set " + column + "=\"" +
                data + "\"" + " where id=\"" + id + "\"";

        System.out.println(updateCommands);
        try {
            statement.executeUpdate(updateCommands);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean register(String id, String pw, String name,
                                   String phone, String address, String emergencyPhone) {

        if (!isOpened()) {
            Logger.log(TAG, "데이터베이스가 초기화 되어있지 않습니다.");
            return false;
        }


        if (id != null && pw != null && name != null && phone != null &&
                address != null && emergencyPhone != null) {
            String command = "insert into account values(null, \"" +id + "\", \"" + pw
                    + "\", \"" + name + "\", \"" + phone + "\", \"" + address + "\", \"" + emergencyPhone + "\");";

            System.out.println(command);
            try {
                statement.execute(command);
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
        return false;
    }
    public static boolean runCommand(String command) {
        return true;
    }

}
