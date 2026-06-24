package modelo;

public class CondicionDeInscripcion {
    private EstrategiaDeInscripcion estrategia;

    public CondicionDeInscripcion (EstrategiaDeInscripcion estrategia){
        this.estrategia = estrategia;
    }

    public boolean cumpleCondicionDeInscripcion (Alumno alumno, Materia materia){
        return this.estrategia.puedeInscribirse(alumno,materia);

    }

    public EstrategiaDeInscripcion getEstrategia(){
        return estrategia;
    }

}
