package modelo;

import java.util.ArrayList;

public class CondicionC extends EstrategiaDeInscripcion {

    public CondicionC (){}

    public boolean puedeInscribirse(Alumno alumno, Materia materia) {
        // 1. Check de correlativas
        for (Prerrequisito p : materia.getCorrelativas()){
            if (!p.estaSatisfecho(alumno)){
                return false;
            }
        }

        // 2. Check de cuatrimestres previos
        int cuatriActual = materia.getCuatrimestre();
        int cuatriLimite = cuatriActual - 5; // El "horizonte" de 5 cuatrimestres atrás

        // Solo verificamos si estamos lo suficientemente avanzados en la carrera
        if (cuatriLimite > 0) {
            ArrayList<Materia> todasLasMaterias = alumno.getCarreraActual().getPlanDeEstudio().getTodasLasMaterias();

            for (Materia m : todasLasMaterias) {
                // Si la materia es del cuatrimestre límite o más vieja aún
                if (m.getCuatrimestre() == cuatriLimite) {
                    if (!alumno.tieneMateriaAprobada(m)) {
                        return false; // Si debe un final viejo, no se puede anotar
                    }
                }
            }
        }
        return true; // Si pasó todo,se puede inscribir
    }


}
