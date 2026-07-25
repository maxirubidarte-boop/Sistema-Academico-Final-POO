package modelo;

public class CursadaDesaprobada implements EstadoCursada {
    private static final long serialVersionUID = 12L;
    @Override
    public void rendirParcial(Cursada cursada,boolean aprueba) {
        cursada.setNuevoEstado(new ParcialAprobado());
    }

    @Override
    public void rendirPromocion(Cursada cursada) {
    }

    @Override
    public void finalizarCursada(Cursada cursada) {
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
