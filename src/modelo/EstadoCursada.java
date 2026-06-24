package modelo;

public interface EstadoCursada {
    void rendirParcial(Cursada cursada,boolean aprueba);
    void rendirPromocion(Cursada cursada);
    void finalizarCursada(Cursada cursada);
    void rendirFinal(Cursada cursada);

    // Estos métodos le sirven al modelo.Alumno para sus estrategias
    boolean estaRegular();
    boolean estaAprobada();
}