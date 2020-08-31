/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clases;

/**
 *
 * @author FERCHO
 */
public class Cancha {
    private int id;
    private String tipo;
    private String descripcion;

    public Cancha(String tipo, String descripcion) {
        //this.id = id;
        this.tipo = tipo;
        this.descripcion = descripcion;
    }

    public Cancha() {
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public String toString(){
        return String.format("%s",getTipo());
    }
}
