package modelo;

public class ParcialDesaprobado implements EstadoCursada {
    private static final long serialVersionUID = 11L;
    @Override
    public void rendirParcial(Cursada cursada,boolean aprueba) {
        cursada.setNuevoEstado(new ParcialAprobado());
    }

    @Override
    public void rendirPromocion(Cursada cursada) {
    }

    @Override
    public void finalizarCursada(Cursada cursada) {
        cursada.setNuevoEstado(new CursadaDesaprobada());
    }

    @Override
    public void rendirFinal(Cursada cursada) {
    }

    @Override
    public boolean estaRegular() {
        return false;
    }

    @Override
    public boolean estaAprobada() {
        return false;
    }
}
