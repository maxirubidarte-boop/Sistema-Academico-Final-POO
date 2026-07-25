package modelo;

import java.io.Serializable;
import java.util.ArrayList;

public class PlanEstudioBuilder implements Serializable {
    private static final long serialVersionUID = 17L;

    private ArrayList<Materia> materiasObligatorias = new ArrayList<>();
    private ArrayList<Materia> materiasOptativas = new ArrayList<>();
    private int minObligatorias;
    private int minOptativas;
    private Integer codigo;
    private String nombre;
    private ArrayList<CuatrimestreCurricular> cuatrimestres = new ArrayList<>();
    private CondicionDeInscripcion condicionGlobal;

    public PlanEstudioBuilder () {
    }

    private void reset() {
        this.materiasObligatorias.clear();
        this.materiasOptativas.clear();
        this.cuatrimestres.clear();
        this.minObligatorias = 0;
        this.minOptativas = 0;
        this.codigo = null;
        this.nombre = "";
        this.condicionGlobal = null;
    }

    public PlanEstudioBuilder setNombre(String nombre) {
        this.nombre = nombre;
        return this;
    }

    public PlanEstudioBuilder setCodigo (Integer codigo){
        if (codigo != null){
            this.codigo = codigo;
        }
        return this;
    }


    public PlanEstudioBuilder setCondicionGlobal(CondicionDeInscripcion condicion) {
        this.condicionGlobal = condicion;
        return this;
    }

    public PlanEstudioBuilder addCuatrimestre(CuatrimestreCurricular cuatri) {
        if (cuatri != null) {
            this.cuatrimestres.add(cuatri);
        }
        return this;
    }


    public PlanEstudioBuilder addMateriaObligatoria (Materia materia){
        if (materia != null) {
            this.materiasObligatorias.add(materia);
        }
        return this;
    }


    public PlanEstudioBuilder addMateriaOptativa (Materia materia){
        if (materia != null) {
            this.materiasOptativas.add(materia);
        }
        return this;
    }

    public PlanEstudioBuilder setMinObligatorias(int num) {
        this.minObligatorias = num;
        return this;
    }

    public PlanEstudioBuilder setMinOptativas(int num) {
        this.minOptativas = num;
        return this;
    }

    public PlanDeEstudio build (){
        PlanDeEstudio nuevoPlan = new PlanDeEstudio();
        nuevoPlan.setNombre(this.nombre);
        nuevoPlan.setCodigo(codigo);
        nuevoPlan.setMinOptativas(minOptativas);
        nuevoPlan.setMinObligatorias(minObligatorias);

        nuevoPlan.setMateriasObligatorias(new ArrayList<>(this.materiasObligatorias));
        nuevoPlan.setMateriasOptativas(new ArrayList<>(this.materiasOptativas));


        nuevoPlan.setCondicion(this.condicionGlobal);


        for (CuatrimestreCurricular c : this.cuatrimestres) {
            nuevoPlan.addCuatrimestre(c);
        }

        this.reset();
        return nuevoPlan;
    }
}