package modelo;

import java.io.Serializable;

public abstract class Persona implements Serializable {

    private static final long serialVersionUID = 25L;

    private String nombre;
    private Integer legajo;
    private String dni;

    public Persona (String nombre, Integer legajo, String dni){
        this.nombre = nombre;
        this.legajo = legajo;
        this.dni = dni;
    }

    public Integer Legajo (){
        return legajo;
    }

    public  String Nombre (){return nombre;}

    public String getDni (){return dni;}

    public void setNombre (String nombre){
        this.nombre = nombre;
    }

    public void editarMisDatos (String nombre, Integer legajo){
        this.legajo = legajo;
        this.nombre = nombre;
    }
}
