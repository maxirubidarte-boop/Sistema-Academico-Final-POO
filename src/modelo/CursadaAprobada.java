package modelo;

public class CursadaAprobada implements EstadoCursada {
    private static final long serialVersionUID = 9L;

    @Override
    public void rendirParcial(Cursada cursada,boolean aprueba) {
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