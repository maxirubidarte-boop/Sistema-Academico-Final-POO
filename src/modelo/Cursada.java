package modelo;
import java.io.Serializable;

public class Cursada implements Serializable{
    private static final long serialVersionUID = 6L;

    private Materia materia;
    private Alumno alumno;
    private EstadoCursada estadoActual;


    public Cursada (Materia materia, Alumno alumno){
        this.materia = materia;
        this.alumno = alumno;
        this.estadoActual = new Inscripto();
    }

    public boolean estaRegular() {
       return estadoActual.estaRegular();
    }

    public boolean estaAprobada (){
        return  estadoActual.estaAprobada();
    }

    public void setNuevoEstado (EstadoCursada nuevoEstado){
        estadoActual = nuevoEstado;
    }

    public Materia getMateria (){return materia;}

    public void rendirParcial(boolean aprueba){
            this.estadoActual.rendirParcial(this,aprueba);
    }

    public void rendirFinal(){
        this.estadoActual.rendirFinal(this);
    }

    public void rendirPromocion(){
        this.estadoActual.rendirPromocion(this);
    }

    public Alumno getAlumno (){return alumno;}

    public EstadoCursada getEstado (){
        return estadoActual;
    }


}
