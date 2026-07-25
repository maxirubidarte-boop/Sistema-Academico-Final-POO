package modelo;


public class Inscripto implements EstadoCursada{
    private static final long serialVersionUID = 7L;

    @Override
    public void rendirParcial(Cursada cursada,boolean aprueba) {
        if (aprueba){
            cursada.setNuevoEstado(new ParcialAprobado());
        }else {
            cursada.setNuevoEstado(new ParcialDesaprobado());
        }
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
    public boolean estaRegular() { return false; }

    @Override
    public boolean estaAprobada() { return false; }
}