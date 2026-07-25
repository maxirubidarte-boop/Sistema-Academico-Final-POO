package modelo;

import java.io.Serializable;
import java.util.ArrayList;

public class CondicionA extends EstrategiaDeInscripcion implements Serializable {

    private static final long serialVersionUID = 20L;

    public CondicionA (){}

    public boolean puedeInscribirse(Alumno alumno, Materia materia) {

        ArrayList<Prerrequisito> correlativas = materia.getCorrelativas();

        // La Condición A pide "Cursadas aprobadas" (REGULAR)

        for (Prerrequisito p : correlativas){
            if (!p.estaSatisfecho(alumno)){
                return false;// Si falta una cursada, no puede
            }
        }
        return true; // Cumple con todas las cursadas de sus correlativas
    }
}
