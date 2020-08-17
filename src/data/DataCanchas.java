/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

import clases.Cancha;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 *
 * @author FERCHO
 */
public class DataCanchas {
    private ConexionMySQL conecta = new ConexionMySQL();
    
    public ResultSet obtenerCancha() throws SQLException, ClassNotFoundException {
        Statement consulta = conecta.getConexion().createStatement();
        String sentencia = "SELECT* FROM canchas;";
        return consulta.executeQuery(sentencia);
    }
    
    public ResultSet obtenerCancha2() throws SQLException, ClassNotFoundException {
        Statement consulta = conecta.getConexion().createStatement();
        String sentencia = "SELECT id, tipo, descripcion FROM canchas;";
        return consulta.executeQuery(sentencia);
    }
    
    public void guardarCancha(Cancha cancha) throws ClassNotFoundException, SQLException{
        Statement consulta = conecta.getConexion().createStatement();
        String sentencia = "INSERT INTO canchas (TIPO, DESCRIPCION) VALUES('"+cancha.getTipo()+"','"+cancha.getDescripcion()+"');";
        consulta.executeUpdate(sentencia);
        consulta.close();
    }
    public void guardarCancha(ArrayList<Cancha> canchas) throws ClassNotFoundException, SQLException{
        Statement consulta = conecta.getConexion().createStatement();
        for (Cancha cancha : canchas) {
            String sentencia = "INSERT INTO canchas (TIPO, DESCRIPCION) VALUES('"+cancha.getTipo()+"','"+cancha.getDescripcion()+"');";
            consulta.executeUpdate(sentencia);
        } 
        consulta.close();
    }
}
