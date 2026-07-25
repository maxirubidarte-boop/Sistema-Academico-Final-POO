package modelo;

import java.io.Serializable;
import java.util.ArrayList;

public class CuatrimestreCurricular implements Serializable {
    private static final long serialVersionUID = 13L;

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
