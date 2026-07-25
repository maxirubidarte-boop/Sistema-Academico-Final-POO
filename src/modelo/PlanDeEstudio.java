package modelo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.io.Serializable;

public class PlanDeEstudio implements Serializable{
    private static final long serialVersionUID = 4L;

    private ArrayList<Materia> materiasObligatorias;
    private ArrayList<Materia> materiasOptativas;
    private int minObligatorias;
    private int minOptativas;
    private Integer codigo;
    private String nombre;
    private ArrayList<CuatrimestreCurricular> cuatrimestres;
    private CondicionDeInscripcion condicionGlobal;

    public PlanDeEstudio (){
        this.condicionGlobal = null;
        this.materiasObligatorias = new ArrayList<>();
        this.materiasOptativas = new ArrayList<>();
        this.cuatrimestres = new ArrayList<>();
        this.minObligatorias = 0;
        this.minOptativas = 0;
        this.codigo = null;
        this.nombre = ""; // Valor por defecto
    }

    public void setCondicion(CondicionDeInscripcion condicion) {
        this.condicionGlobal = condicion;
    }

    public CondicionDeInscripcion getCondicion() {
        return condicionGlobal;
    }

    public void addMateria(Materia materia, boolean esObligatoria) {
        if (materia != null) {
            if (esObligatoria) {
                if (!materiasObligatorias.contains(materia)) materiasObligatorias.add(materia);
            } else {
                if (!materiasOptativas.contains(materia)) materiasOptativas.add(materia);
            }
        }
    }

    public void eliminarMateria(Materia materia) {
        materiasObligatorias.remove(materia);
        materiasOptativas.remove(materia);
    }

    public  void setCodigo (Integer codigo){
        this.codigo = codigo;
    }

    public void setNombre(String nombre) { this.nombre = nombre; }

    public  ArrayList<Materia> getOptativas (){
        return materiasOptativas;
    }

    public  ArrayList<Materia> getObligatorias (){
        return materiasObligatorias;
    }

    public int getMinObligatorias (){
        return minObligatorias;
    }

    public int getMinOptativas(){
        return minOptativas;
    }

    public String getNombre() { return nombre; }

    public Integer getCodigo(){return codigo;}

    public void setMateriasObligatorias (ArrayList<Materia> materiasObligatorias){
        this.materiasObligatorias = materiasObligatorias;
    }

    public void setMateriasOptativas (ArrayList<Materia> materiasOptativas){
        this.materiasOptativas = materiasOptativas;
    }

    public void setMinObligatorias(int minObligatorias) {
        this.minObligatorias = minObligatorias;
    }

    public void setMinOptativas(int minOptativas) {
        this.minOptativas = minOptativas;
    }

    public void addCuatrimestre (CuatrimestreCurricular cuatri){
        if (cuatri != null){
            cuatrimestres.add(cuatri);
        }
    }

    public PlanDeEstudio getPlanDeEstudio (){return this;}

    public ArrayList<Materia> getTodasLasMaterias (){
        ArrayList<Materia> todas = new ArrayList<>();
        todas.addAll(this.materiasObligatorias);
        todas.addAll(this.materiasOptativas);
        return todas;
    }

    public boolean estaGraduado(Alumno alumno) {
        int obligatoriasAprobadas = 0;
        int optativasAprobadas = 0;

        // Contamos cuántas obligatorias del plan tiene el alumno
        for (Materia m : materiasObligatorias) {
            if (alumno.tieneMateriaAprobada(m)) {
                obligatoriasAprobadas++;
            }
        }

        // Contamos cuántas optativas del plan tiene el alumno
        for (Materia m : materiasOptativas) {
            if (alumno.tieneMateriaAprobada(m)) {
                optativasAprobadas++;
            }
        }

        // Comparamos contra los mínimos del modelo.PlanDeEstudio
        return obligatoriasAprobadas >= this.minObligatorias &&
                optativasAprobadas >= this.minOptativas;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("================================================================================\n");
        // Protección: si el nombre es null, usamos un genérico para que no explote
        String nombreDisplay = (nombre != null) ? nombre.toUpperCase() : "PLAN SIN NOMBRE";
        sb.append(String.format("       PLAN DE ESTUDIO: %-40s\n", nombreDisplay));
        sb.append("================================================================================\n");
        sb.append(String.format("%-5s | %-25s | %-4s | %-12s | %s\n", "ID", "NOMBRE", "CUAT", "CONDICIÓN", "CORRELATIVAS"));
        sb.append("--------------------------------------------------------------------------------\n");

        // 1. Unificamos todas las materias para listarlas
        ArrayList<Materia> todas = getTodasLasMaterias();

        // 2. Las ordenamos por cuatrimestre para que el reporte sea lógico
        todas.sort((m1, m2) -> Integer.compare(m1.getCuatrimestre(), m2.getCuatrimestre()));

        // 3. Recorremos y formateamos cada fila
        for (Materia m : todas) {
            // Formatear correlativas
            String correlativasStr = "[Ninguna]";
            if (!m.getCorrelativas().isEmpty()) {
                ArrayList<String> nombres = new ArrayList<>();
                for (Prerrequisito p : m.getCorrelativas()) {
                    nombres.add(p.getMateriaRequerida().getNombre() + " (" + p.getTipo() + ")");
                }
                correlativasStr = String.join(", ", nombres);
            }

            // 🔥 CORREGIDO: Leemos el nombre de la estrategia desde la condición global única del plan
            String nombreCondicion = "N/A";
            if (condicionGlobal != null && condicionGlobal.getEstrategia() != null) {
                nombreCondicion = condicionGlobal.getEstrategia().getClass().getSimpleName();
            }

            sb.append(String.format("%-5d | %-25s | %-4d | %-12s | %s\n",
                    m.getCodigoMateria(),
                    m.getNombre(),
                    m.getCuatrimestre(),
                    nombreCondicion,
                    correlativasStr));
        }

        sb.append("--------------------------------------------------------------------------------\n");
        sb.append("Código de Plan: " + codigo + " | Requisitos: " + minObligatorias + " Oblig, " + minOptativas + " Opta.\n");
        sb.append("================================================================================\n");

        return sb.toString();
    }

}
