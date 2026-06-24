package modelo;

public abstract class Persona {
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

    //NUEVOOOOOOO NO ESTA EN EL DIAGRAMA
    public String getDni (){return dni;}

    public void setNombre (String nombrel){
        this.nombre = nombre;
    }

    public void editarMisDatos (String nombre, Integer legajo){
        this.legajo = legajo;
        this.nombre = nombre;
    }
}
