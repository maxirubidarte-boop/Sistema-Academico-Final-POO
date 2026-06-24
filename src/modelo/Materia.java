package modelo;

import java.util.ArrayList;

public class Materia {
    private String nombre;
    private Integer codigo;
    private int cuatrimestre;
    private ArrayList<Prerrequisito> correlativas;

    public Materia(Integer codigo,String nombre,int cuatrimestre){
        this.nombre = nombre;
        this.codigo = codigo;
        this.cuatrimestre = cuatrimestre;
        this.correlativas = new ArrayList<>();
    }


    /* BORRAMOS

    ATRIBUTO -> CondicionDeInscripcion condicion;

    public void setCondicion(CondicionDeInscripcion condicion) {
        this.condicion = condicion;
    }
    public CondicionDeInscripcion getCondicion() {return condicion;}

     */


    // NUEVOOOOOOOOOOOO
    public void addCorrelativa(Prerrequisito p){
        if (p != null){
            correlativas.add(p);
        }
    }

    // NUEVOOOOOOOOOOOO
    public void eliminarCorrelativa(Integer codigoCorrelativa) {
        correlativas.removeIf(p -> p.getMateriaRequerida().getCodigoMateria() == codigoCorrelativa);
    }

    public ArrayList<Prerrequisito> getCorrelativas(){return correlativas;}

    //NUEVOOOOOOOOOOOOOOOOO
    public void setCuatrimestre (int nuevoCuatri){
        this.cuatrimestre = nuevoCuatri;
    }
    public int getCuatrimestre() {
        return cuatrimestre;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Materia materia = (Materia) o;
        return codigo == materia.codigo; // Compara por el ID único
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(codigo);
    }

    // NUEVOOOOOOOOOOOOOOOOOOOOO
    public void setNombre (String nuevoNombre){
        this.nombre = nuevoNombre;
    }
    public String getNombre (){return nombre;}

    public Integer getCodigoMateria (){return codigo;}

    @Override
    public String toString() {
        return"- ID: " + codigo + " | Nombre: " + nombre + " | Cuatrimestre: " + cuatrimestre; // O el formato que prefieras
    }

}

