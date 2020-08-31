/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Daniela
 */
public class ConexionMySQL {
    
    private Connection conecta = null;
    
    public Connection getConexion() throws ClassNotFoundException, SQLException{
        String driver = "com.mysql.jdbc.Driver";
        String url = "jdbc:mysql://localhost:3306/canchasdb";
        Class.forName(driver);
        return DriverManager.getConnection(url, "root","");
    }
    public void cerrar() throws SQLException{
        conecta.close();
    }
}
