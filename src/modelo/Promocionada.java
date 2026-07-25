package modelo;

public class Promocionada implements EstadoCursada {
    private static final long serialVersionUID = 10L;
    @Override
    public void rendirParcial(Cursada cursada, boolean aprueba) {
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
        return true;
    }

    @Override
    public boolean estaAprobada() {
        return true;
    }
}
