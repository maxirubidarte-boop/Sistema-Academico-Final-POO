package modelo;

import java.util.ArrayList;

public class CuatrimestreCurricular {
    private int cuatrimestre;
    private int año;
    private ArrayList<Materia> materias;

    public CuatrimestreCurricular (int año,int cuatrimestre){
        this.año = año;
        this.cuatrimestre = cuatrimestre;
        this.materias = new ArrayList<>();
    }

    public void addMateria(Materia materia){
        if (materia != null){
            materias.add(materia);
        }
    }

    public int getCuatrimestre() {
        return cuatrimestre;
    }

    public int getAño() {
        return año;
    }

    public ArrayList<Materia> getMaterias() {
        return materias;
    }

}
