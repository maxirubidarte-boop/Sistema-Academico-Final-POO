package modelo;

import java.util.ArrayList;

public class Alumno extends Persona {
    private ArrayList<Materia> materiasAprobadas;
    private ArrayList<Cursada> cursadas;
    private Carrera carreraActual;

    public Alumno(String nombre,Integer legajo, String dni) {
        super(nombre, legajo, dni);
        this.materiasAprobadas = new ArrayList<>();
        this.cursadas = new ArrayList<>();
    }

    public void inscribirACursada (Materia materia){
        if (materia != null){
            Cursada nuevaCursada = new Cursada(materia,this);
            this.cursadas.add(nuevaCursada);
        }
    }

    public void rendirFinal (Materia materia){
        if (materia != null){
            Cursada cursada = getCursadaDeMateria(materia);
            if (cursada != null){
                cursada.rendirFinal();
            }

        }
    }

    public void rendirParcial (Materia materia, boolean aprobado){
        if (materia != null){
            Cursada cursada = getCursadaDeMateria(materia);
            if(aprobado && cursada != null){
                cursada.rendirParcial(aprobado);
            }
        }
    }

    public void rendirPromocion (Materia materia){
        if (materia != null){
            Cursada cursada = getCursadaDeMateria(materia);
            if (cursada != null){
                cursada.rendirPromocion();
            }

        }
    }



    public void editarDatos (String nombre, Integer legajo){
        this.editarMisDatos(nombre,legajo);
    }



    public Carrera getCarreraActual() {
        return carreraActual;
    }

    public Integer getLegajo (){return Legajo();}

    public String getNombre (){return Nombre();}

    public void setCarreraActual (Carrera carreraActual){
        this.carreraActual = carreraActual;
    }

    public boolean tieneMateriaAprobada (Materia materia){
        return this.materiasAprobadas.contains(materia);
    }

    public Cursada getCursadaDeMateria(Materia m) {
        Cursada ultima = null;
        for (Cursada c : cursadas) {
            if (c.getMateria().equals(m)) {
                ultima = c; // Se queda con la última que encuentre en la lista
            }
        }
        return ultima;
    }

    public boolean tieneMateriaRegular (Materia materia){
        if (materiasAprobadas.contains(materia)){
            return true;
        }
        Cursada c = getCursadaDeMateria(materia);
        return (c != null && c.estaRegular());
    }

    // Lógica para saber si está graduado
    public boolean estaGraduado() {
        // El alumno se gradúa si cumple con el Plan de Estudio de su carrera
        return this.carreraActual.getPlanDeEstudio().estaGraduado(this);
    }

    public void addMateriaAprobada (Materia materia){
        if (materia != null){
            materiasAprobadas.add(materia);
        }
    }


    public ArrayList<Materia> getMateriasAptasACursar(){
        ArrayList<Materia> aptas = new ArrayList<>();
        if (carreraActual != null){
            PlanDeEstudio plan = this.carreraActual.getPlanDeEstudio();

            CondicionDeInscripcion condicion = plan.getCondicion();

            ArrayList<Materia> todasLasMaterias = plan.getTodasLasMaterias();

            for (Materia m : todasLasMaterias){
                // 1. Verificamos que el alumno no la haya aprobado ya
                if (!this.tieneMateriaAprobada(m)){

                    // 3. Si la condición dice que puede, la agregamos
                    if (condicion != null && condicion.cumpleCondicionDeInscripcion(this, m)) {
                        aptas.add(m);
                    }
                }
            }
            return aptas;
        }
        return aptas;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Alumno alumno = (Alumno) o;
        return this.getLegajo() == alumno.getLegajo();
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(this.getLegajo());
    }


    public void getInfoCursadas() {
        System.out.println("\n========= HISTORIAL DE CURSADAS: " + this.getNombre().toUpperCase() + " =========");
        if (cursadas.isEmpty()) {
            System.out.println("   > El alumno no registra cursadas actualmente.");
        } else {
            System.out.printf("%-25s | %-15s\n", "MATERIA", "ESTADO ACTUAL");
            System.out.println("------------------------------------------------------------");

            for (Cursada c : cursadas) {
                // Determinamos el texto del estado según los métodos de tu clase modelo.Cursada
                String textoEstado = "modelo.Inscripto";
                if (c.estaAprobada()) {
                    textoEstado = "APROBADA";
                } else if (c.estaRegular()) {
                    textoEstado = "REGULAR";
                }

                System.out.printf("%-25s | %-15s\n",
                        c.getMateria().getNombre(),
                        textoEstado);
            }
        }
        System.out.println("==================================================================\n");
    }
}
