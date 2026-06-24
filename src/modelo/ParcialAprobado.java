package modelo;

public class ParcialAprobado implements EstadoCursada {
    @Override
    public void rendirParcial(Cursada cursada,boolean aprueba) {
    }

    @Override
    public void rendirPromocion(Cursada cursada) {
        cursada.setNuevoEstado(new Promocionada());

        // finalizamos la cursada por que promociono
        finalizarCursada(cursada);
    }

    @Override
    public void finalizarCursada(Cursada cursada) {
        // Agregamos la materia a la lista de aprobadas de el alumno
        Alumno a = cursada.getAlumno();
        a.addMateriaAprobada(cursada.getMateria());
    }

    @Override
    public void rendirFinal(Cursada cursada) {
        cursada.setNuevoEstado(new CursadaAprobada());

       // finalizamos la cursada
        finalizarCursada(cursada);
    }

    @Override
    public boolean estaRegular() {
        return true;
    }

    @Override
    public boolean estaAprobada() {
        return false;
    }
}
