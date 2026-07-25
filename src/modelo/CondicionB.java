package modelo;

import java.io.Serializable;
import java.util.ArrayList;

public class CondicionB extends EstrategiaDeInscripcion implements Serializable {

    private static final long serialVersionUID = 21L;

    public CondicionB (){}

    public boolean puedeInscribirse(Alumno alumno, Materia materia) {
        ArrayList<Prerrequisito> correlativas = materia.getCorrelativas();

        for (Prerrequisito p : correlativas){
            if(!p.estaSatisfecho(alumno)){
                return false;
            }
        }
        return true;
    }
}
