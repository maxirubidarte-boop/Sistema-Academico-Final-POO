package modelo;

public abstract class EstrategiaDeInscripcion {

    public EstrategiaDeInscripcion (){}

    public abstract boolean puedeInscribirse(Alumno alumno, Materia materia);
}
