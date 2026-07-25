package modelo;

import java.io.Serializable;

public abstract class EstrategiaDeInscripcion implements Serializable {
    private static final long serialVersionUID = 14L;

    public EstrategiaDeInscripcion (){}

    public abstract boolean puedeInscribirse(Alumno alumno, Materia materia);
}
