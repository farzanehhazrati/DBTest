
import sepam.Encryptor;

import java.sql.*;


public class JDBCPreparedConnection {
    Connection dbConnection = null;
    PreparedStatement preparedStatement = null;
    ResultSet rs = null;

    public JDBCPreparedConnection(String configFileName) {
        dbConnection = getConnection(configFileName);

    }


    public ResultSet getResultSet(String query) {

        try {
            preparedStatement = dbConnection.prepareStatement(query);
            rs = preparedStatement.executeQuery();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return rs;
    }

    public void close() {
        System.out.println("close connection");
        try {

            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (rs != null) {
                rs.close();
            }

            if (dbConnection != null) {
                dbConnection.close();
            }
        } catch (SQLException e) {

            System.out.println(e.getMessage());

        }
    }

    public Connection getConnection(String configFileName) {

        LoadPropertiesFile config = new LoadPropertiesFile(configFileName);
        System.out.println("getConnection using propertiesFile ....");

        try {
            Class.forName(config.getProp().getProperty("DRIVER"));

            String DBPassword = "";
            if (config.getProp().containsKey("DBPASSWORD"))
                DBPassword = config.getProp().getProperty("DBPASSWORD");
            if (DBPassword.trim().equals("") && config.getProp().containsKey("DBEncryptedPassword"))
                DBPassword = new Encryptor().decrypt(config.getProp().getProperty("DBEncryptedPassword"));

            return DriverManager.getConnection(config.getProp().getProperty("DBURL"), config.getProp().getProperty("DBUSERNAME"), DBPassword);

        } catch (Exception e) {

            System.out.println(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>  ");
            System.out.println(" >>>>>>>       connecting to database is failed !!!");
            System.out.println(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>  ");
            e.printStackTrace();
        }

        return null;
    }
}

