package modelo;

import java.io.Serializable;
import java.util.ArrayList;

public class CondicionE extends EstrategiaDeInscripcion implements Serializable {

    private static final long serialVersionUID = 24L;

    public CondicionE (){}

    public boolean puedeInscribirse(Alumno alumno, Materia materia) {
        // 1. Check de correlativas
        for (Prerrequisito p : materia.getCorrelativas()){
            // No me importa el "tipo" que diga el prerrequisito,
            // para la Condición E, el pibe tiene que tenerla APROBADA sí o sí.
            if (!alumno.tieneMateriaAprobada(p.getMateriaRequerida())){
                return false;
            }
        }

        // 2. Check de cuatrimestres previos
        int cuatriActual = materia.getCuatrimestre();
        int cuatriLimite = cuatriActual - 3; // El "horizonte" de 3 cuatrimestres atrás

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
