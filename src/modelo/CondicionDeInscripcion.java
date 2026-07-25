package modelo;

import java.io.Serializable;

public class CondicionDeInscripcion implements Serializable {
    private static final long serialVersionUID = 15L;

    private EstrategiaDeInscripcion estrategia;

    public CondicionDeInscripcion (EstrategiaDeInscripcion estrategia){
        this.estrategia = estrategia;
    }

    public boolean cumpleCondicionDeInscripcion (Alumno alumno, Materia materia){
        return this.estrategia.puedeInscribirse(alumno,materia);

    }

    public EstrategiaDeInscripcion getEstrategia(){
        return estrategia;
    }

}
