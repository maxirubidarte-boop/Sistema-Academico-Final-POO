package modelo;

import java.util.HashMap;
import java.util.Map;

public class Carrera {
    private String nombre;
    private Integer codigo;
    private PlanDeEstudio planDeEstudio;
    private Map<String, Alumno> alumnosInscritos;

    public Carrera (Integer codigoCarrera,String nombre){
        this.nombre = nombre;
        this.codigo = codigoCarrera;
        this.planDeEstudio = null;
        this.alumnosInscritos = new HashMap<>();
    }


    public void setNombre (String nombre){
        this.nombre = nombre;
    }


    public void addPlanDeEstudio (PlanDeEstudio plan){
        this.planDeEstudio = plan;
    }

    public String getNombre (){return nombre;}
    public PlanDeEstudio getPlanDeEstudio(){return planDeEstudio;}

    public int getCodigoCarrera (){return codigo;}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Carrera carrera = (Carrera) o;
        // Comparamos por el código único de la carrera
        return this.getCodigoCarrera() == carrera.getCodigoCarrera();
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(this.getCodigoCarrera());
    }


    public void inscribirAlumno(Alumno alumno) {
        if (alumno != null) {
            alumnosInscritos.put(alumno.getDni(), alumno);
        } else {
            System.out.println("Error: El alumno no puede ser null");
        }
    }


    public Alumno getAlumno(String dni) {
        return alumnosInscritos.get(dni); // Si no está, devuelve null solito.
    }


    public void eliminarAlumno(String dni) {
        if (dni != null){
            Alumno alumnoBuscado = alumnosInscritos.get(dni);
            if ( alumnoBuscado != null){
                alumnoBuscado.setCarreraActual(null);
                alumnosInscritos.remove(dni);
            }
        }
    }

    // Obtener todos los alumnos de esta carrera
    public Map<String, Alumno> getMapaAlumnos() {
        return alumnosInscritos;
    }

}
