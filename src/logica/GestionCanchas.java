/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logica;

import clases.Cancha;
import data.DataCanchas;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author FERCHO
 */
public class GestionCanchas {
    
    public ArrayList<Cancha> obtenerCanchas() throws SQLException, ClassNotFoundException {
        DataCanchas objCancha = new DataCanchas();
        Cancha ca;
        ResultSet reCliente = objCancha.obtenerCancha();
        ArrayList<Cancha> listCanchas = new ArrayList<>();
        while (reCliente.next()) {

            //ca = new Cancha(reCliente.getInt(1), reCliente.getString(2), reCliente.getString(3));
            ca = new Cancha();
            ca.setId(reCliente.getInt(1));
            ca.setTipo(reCliente.getString(2));
            ca.setDescripcion(reCliente.getString(3));
            listCanchas.add(ca);

        }
        return listCanchas;
    }
    
    public ArrayList<Cancha> obtenerCanchas2() throws SQLException, ClassNotFoundException {
        DataCanchas objCancha = new DataCanchas();
        Cancha ca;
        ResultSet reCliente = objCancha.obtenerCancha2();
        ArrayList<Cancha> listCanchas = new ArrayList<>();
        while (reCliente.next()) {

            //ca = new Cancha(reCliente.getInt(1), reCliente.getString(2), reCliente.getString(3));
            ca = new Cancha();
            ca.setId(reCliente.getInt(1));
            ca.setTipo(reCliente.getString(2));
            ca.setDescripcion(reCliente.getString(3));
            listCanchas.add(ca);

        }
        return listCanchas;
    }
    
    public void guardarCancha(Cancha cancha) throws ClassNotFoundException, SQLException {
        DataCanchas dataReserva = new DataCanchas();
        dataReserva.guardarCancha(cancha);
    }
    
    public void guardarCancha(ArrayList canchas) throws ClassNotFoundException, SQLException {
        DataCanchas dataReserva = new DataCanchas();
        dataReserva.guardarCancha(canchas);
    }
}
