package modelo;

public class CursadaAprobada implements EstadoCursada {

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