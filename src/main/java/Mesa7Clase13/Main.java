package Mesa7Clase13;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);
    private final static String log4jConfigFile = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "log4j2.xml";
    private static final String CREATE_TABLE_SQL = "DROP TABLE IF EXISTS ODONTOLOGOS;" +
            "CREATE TABLE ODONTOLOGOS(ID INT PRIMARY KEY AUTO_INCREMENT, NOMBRE VARCHAR(100),APELLIDO VARCHAR(100),MATRICULA VARCHAR(255));";
    private static final String INSERT_INTO = "INSERT INTO ODONTOLOGOS (NOMBRE,APELLIDO,MATRICULA) VALUES (?,?,?)";
    private static final String UPDATE_MAT= "UPDATE ODONTOLOGOS SET MATRICULA = ? WHERE ID = ?";
    private static final String SELECT_TABLE= "SELECT * FROM ODONTOLOGOS";

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:h2:~/odontologos13");
    }

    public static void main(String[] args) throws IOException, SQLException {
        var source = new ConfigurationSource(new FileInputStream(log4jConfigFile));
        Configurator.initialize(null,source);

        var odontologo = new Odontologo(1,"Facu", "Burgos", "shjdsjds12");
        Connection con = null;
        try {
            con = getConnection();
            Statement stm = con.createStatement();
            stm.execute(CREATE_TABLE_SQL);

            PreparedStatement psInsert = con.prepareStatement(INSERT_INTO);
            psInsert.setString(1,odontologo.nombre());
            psInsert.setString(2,odontologo.apellido());
            psInsert.setString(3,odontologo.matricula());
            psInsert.execute();

            ResultSet rd = stm.executeQuery(SELECT_TABLE);
            while(rd.next()){
                logger.info(rd.getInt(1) +" " + rd.getString(2) + " " + rd.getString(3)+" "+rd.getString(4));
            }

            con.setAutoCommit(false);

            PreparedStatement psUpdate = con.prepareStatement(UPDATE_MAT);
            psUpdate.setString(1,"matriculaNueva");
            psUpdate.setInt(2,odontologo.id());
            psUpdate.execute();

            con.commit();
            con.setAutoCommit(true);

            rd = stm.executeQuery(SELECT_TABLE);
            while(rd.next()){
                logger.info(rd.getInt(1) +" " + rd.getString(2) + " " + rd.getString(3)+" "+rd.getString(4));
            }

        }catch (SQLException e){
            if (con != null){
                logger.error(e);
                con.rollback();
            }
        }finally {
            if (con != null) {
                con.close();
            }
        }
    }




}
