package modelo;

import java.io.Serializable;

public class Prerrequisito implements Serializable {
    private static final long serialVersionUID = 18L;

    private Materia materiaRequerida;
    private TipoPrerrequisito tipo;

    public enum TipoPrerrequisito {
        REGULAR,
        APROBADA
    }

    public Prerrequisito (Materia materia, TipoPrerrequisito tipo) {
        this.materiaRequerida = materia;
        this.tipo = tipo;
    }

    public boolean estaSatisfecho(Alumno alumno) {
        if (this.tipo == TipoPrerrequisito.APROBADA) {
            // Buscamos si la materia requerida está en la lista de aprobadas del alumno
            return alumno.tieneMateriaAprobada(this.materiaRequerida);
        } else if (this.tipo == TipoPrerrequisito.REGULAR) {
            // Buscamos si el alumno tiene la cursada de esa materia
            return alumno.tieneMateriaRegular(this.materiaRequerida);
        }
        return false;
    }

    // Getters necesarios
    public Materia getMateriaRequerida() {
        return materiaRequerida;
    }

    public TipoPrerrequisito getTipo() {
        return tipo;
    }


}
