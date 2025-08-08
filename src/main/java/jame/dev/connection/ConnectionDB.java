package jame.dev.connection;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.java.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Log
public class ConnectionDB {
    public static volatile ConnectionDB instance;
    private static final Dotenv dotenv = Dotenv.load();
    private static final String URL = dotenv.get("DB_URL");
    private static final String USER = dotenv.get("DB_USER");
    private static final String PWD = dotenv.get("DB_PWD");

    private Connection con;

    private ConnectionDB(){
        this.con = connect();
    }

    public static ConnectionDB getInstance(){
        if(instance == null){
            synchronized (ConnectionDB.class) {
                if(instance == null)
                    instance = new ConnectionDB();
            }
        }
        log.info("Instance of ConnectionDB: " + instance + "\n");
        return instance;
    }

    private Connection connect(){
        try{
            Connection con = DriverManager.getConnection(URL, USER, PWD);
            log.info("Connection established. " + '\n');
            con.setAutoCommit(false);
            return con;
        }catch (SQLException e){
            log.warning("Error trying connect to the DB: " + e.getSQLState() + '\n');
            return null;
        }
    }

    public Connection getConnection(){
        try {
            if (this.con == null || this.con.isClosed() || !this.con.isValid(2)) {
                this.con = connect();
            }
        } catch (SQLException e) {
            log.warning("Error validating connection: " + e.getMessage() + '\n');
        }
        return this.con;
    }

    public void close(){
        try{
            if(this.con != null && !this.con.isClosed())
                this.con.close();
        }catch (SQLException e){
            log.warning("Error trying to close the connection. \n");
        }
    }
}
