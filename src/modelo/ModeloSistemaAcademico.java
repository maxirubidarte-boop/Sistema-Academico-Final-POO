package modelo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.io.Serializable;

public class ModeloSistemaAcademico implements Serializable{
    private static final long serialVersionUID = 1L;

    private HashMap<String, Carrera> carreras;
    private HashMap<String, Alumno> padronGeneral;
    private HashMap<Integer, PlanDeEstudio> planesDeEstudio;
    private HashMap<String,Materia> materias;
    private FabricaPlanDeEstudio fabricaPlanDeEstudio;
    private static ModeloSistemaAcademico instancia;

    private ModeloSistemaAcademico(){
        this.carreras = new HashMap<>();
        this.padronGeneral = new HashMap<>();
        this.planesDeEstudio = new HashMap<>();
        this.materias = new HashMap<>();
        this.fabricaPlanDeEstudio = new FabricaPlanDeEstudio();
        // CONSTRUCTOR PRIVADO PARA EVITAR INSTANCIAS EXTERNAS
    }

    public static ModeloSistemaAcademico getInstancia (){
        if (instancia == null){
            instancia = new ModeloSistemaAcademico();
        }
        return instancia;
        // METODO GETINSTANCIA PARA LA INSTANCIA UNICA
    }


    public static void setInstancia(ModeloSistemaAcademico nuevaInstancia) {
        instancia = nuevaInstancia;
    }


    //----------------------------------------- METODOS REFERIDOS A LOS PLANES -----------------------------------------------------------------------------------------------------//

    // 🔥 CORREGIDO: Ahora recibe una única EstrategiaGlobal y listas de IDs simples
    public boolean crearPlanCompleto(String nombrePlan, Integer codigoPlan, int minOblig, int minOpta,
                                     EstrategiaDeInscripcion estrategiaGlobal,
                                     ArrayList<Integer> obligatoriasIDs,
                                     ArrayList<Integer> optativasIDs) {

        // 1. Escudos mínimos esenciales
        if (codigoPlan == null || nombrePlan == null || nombrePlan.trim().isEmpty() || planesDeEstudio.containsKey(codigoPlan)) {
            System.out.println("❌ [ERR] Datos del plan inválidos o código duplicado.");
            return false;
        }
        if (obligatoriasIDs == null || optativasIDs == null || minOblig < 0 || minOpta < 0 || estrategiaGlobal == null) {
            System.out.println("❌ [ERR] Parámetros, mínimos o estrategia nulos/negativos.");
            return false;
        }

        ArrayList<Materia> obligatorias = new ArrayList<>();
        ArrayList<Materia> optativas = new ArrayList<>();
        ArrayList<CuatrimestreCurricular> cuatrimestres = new ArrayList<>();

        // 2. Carga de obligatorias buscando por ID en los valores del mapa maestro
        for (Integer id : obligatoriasIDs) {
            Materia m = null;
            for (Materia mat : materias.values()) {
                if (mat.getCodigoMateria().equals(id)) {
                    m = mat;
                    break;
                }
            }
            if (m != null) {
                obligatorias.add(m);
            }
        }

        // 3. Carga de optativas buscando por ID
        for (Integer id : optativasIDs) {
            Materia m = null;
            for (Materia mat : materias.values()) {
                if (mat.getCodigoMateria().equals(id)) {
                    m = mat;
                    break;
                }
            }
            if (m != null) {
                optativas.add(m);
            }
        }

        // 4. Validación lógica de mínimos vs cantidad cargada
        if (minOblig > obligatorias.size() || minOpta > optativas.size()) {
            System.out.println("❌ [ERR] Mínimos exigidos superan las materias cargadas.");
            return false;
        }

        // 5. Creación e inserción al mapa general usando tu fábrica adaptada
        // (Encapsulamos la estrategia en el objeto CondicionDeInscripcion)
        CondicionDeInscripcion condicionGlobal = new CondicionDeInscripcion(estrategiaGlobal);

        // Pasamos la condición única a la fábrica en vez del mapa de estrategias viejo
        PlanDeEstudio nuevoPlan = fabricaPlanDeEstudio.crearPlanDeEstudio(
                obligatorias, optativas, cuatrimestres, condicionGlobal, minOblig, minOpta, codigoPlan, nombrePlan
        );

        return addPlanAlSistema(nuevoPlan);
    }


    // GET PLAN
    public PlanDeEstudio getPlan(Integer codigoPlan) {
        if (codigoPlan != null){
            return planesDeEstudio.get(codigoPlan);
        }
        return null;
    }

    // GET MAPA PLANES
    public HashMap<Integer, PlanDeEstudio> getMapaPlanes() {
        return planesDeEstudio;
    }


    // ADD PLAN AL SISTEMA
    public boolean addPlanAlSistema(PlanDeEstudio plan) {
        if (plan != null && !planesDeEstudio.containsKey(plan.getCodigo())) {
            planesDeEstudio.put(plan.getCodigo(), plan);
            return true;
        }
        return false;
    }


    // ELIMINAR PLAN DEL SISTEMA
    public boolean eliminarPlanDelSistema (Integer codigoDelPlan){
        if (codigoDelPlan != null && planesDeEstudio.containsKey(codigoDelPlan)){
            // Limpiamos las carreras que tengan el plan
            for (Carrera c : carreras.values()){
                if (c.getPlanDeEstudio() != null && c.getPlanDeEstudio().getCodigo().equals(codigoDelPlan)){ //compara los planes con el codigo para ver si es el mismo
                    c.setPlanDeEstudio(null);
                }
            }

            planesDeEstudio.remove(codigoDelPlan);
            return true;
        }
        return false;
    }

    // ASIGNAR PLAN DE ESTUDIO A CARRERA
    public void asignarPlanDeEstudioACarrera (Integer codigoPlan,String nombreCarrera){
        if (codigoPlan != null && nombreCarrera != null){
            Carrera carrera = carreras.get(nombreCarrera);
            PlanDeEstudio planBuscado = planesDeEstudio.get(codigoPlan);
            if (carrera != null && planBuscado != null){
                carrera.setPlanDeEstudio(planBuscado);
            }
        }
    }

    //  Edita un plan existente pisando sus datos y manteniendo sus relaciones intactas
    public boolean editarPlan(int codigo, String nombre, int minOblig, int minOpta,
                              EstrategiaDeInscripcion estrategia,
                              ArrayList<Integer> obligatoriasIDs, ArrayList<Integer> optativasIDs) {

        // 1. Buscamos el plan original en nuestro mapa general por su código
        PlanDeEstudio plan = planesDeEstudio.get(codigo);
        if (plan == null) {
            System.out.println(" [ERR] No se encontró ningún plan con el código: " + codigo);
            return false;
        }

        // 2. Validaciones básicas de integridad de datos
        if (nombre == null || nombre.trim().isEmpty() || minOblig < 0 || minOpta < 0 || estrategia == null) {
            System.out.println(" [ERR] Parámetros inválidos para la actualización.");
            return false;
        }

        // 3. Resolvemos las materias obligatorias buscando por ID (tal como lo hacés en la creación)
        ArrayList<Materia> obligatoriasNuevas = new ArrayList<>();
        for (Integer id : obligatoriasIDs) {
            Materia m = null;
            for (Materia mat : materias.values()) {
                if (mat.getCodigoMateria().equals(id)) {
                    m = mat;
                    break;
                }
            }
            if (m != null) {
                obligatoriasNuevas.add(m);
            }
        }

        // 4. Resolvemos las materias optativas buscando por ID
        ArrayList<Materia> optativasNuevas = new ArrayList<>();
        for (Integer id : optativasIDs) {
            Materia m = null;
            for (Materia mat : materias.values()) {
                if (mat.getCodigoMateria().equals(id)) {
                    m = mat;
                    break;
                }
            }
            if (m != null) {
                optativasNuevas.add(m);
            }
        }

        // 5. Validación de mínimos vs cantidad real de materias resueltas
        if (minOblig > obligatoriasNuevas.size() || minOpta > optativasNuevas.size()) {
            System.out.println(" [ERR] Mínimos requeridos superan las materias asignadas.");
            return false;
        }

        // 6. Pisamos los datos primitivos y el nombre
        plan.setNombre(nombre);
        plan.setMinObligatorias(minOblig);
        plan.setMinOptativas(minOpta);

        // 7. Creamos la nueva condición de inscripción con la estrategia elegida y la asignamos
        CondicionDeInscripcion nuevaCondicion = new CondicionDeInscripcion(estrategia);
        plan.setCondicion(nuevaCondicion);

        // 8. Limpiamos y rellenamos las listas internas usando .clear() y .addAll()
        // Esto mantiene la misma referencia de los ArrayList originales de la clase PlanDeEstudio,
        // evitando que cualquier otra parte del sistema que apunte a ellos se desincronice.
        plan.getObligatorias().clear();
        plan.getObligatorias().addAll(obligatoriasNuevas);

        plan.getOptativas().clear();
        plan.getOptativas().addAll(optativasNuevas);

        System.out.println("[OK] Plan de Estudio [ID: " + codigo + "] modificado con éxito en el sistema.");
        return true;
    }



    //----------------------------------------- METODOS REFERIDOS A LAS MATERIAS -----------------------------------------------------------------------------------------------------//


    // REGISTRAR MATERIA
    public boolean registrarMateria(Integer codigo, String nombre, int cuatri) {
        if (codigo != null && nombre != null && !nombre.isEmpty()) {
            if (!materias.containsKey(nombre)) {
                materias.put(nombre, new Materia(codigo, nombre, cuatri));
                return true;
            }
        }
        return false;
    }



    public boolean eliminarMateriaDelSistema(String nombre) {
        Materia m = materias.get(nombre);
        if (m == null) return false;

        // --- BLOQUE DE SEGURIDAD E INTEGRIDAD ---
        for (PlanDeEstudio plan : planesDeEstudio.values()) {

            // 1. Verificación para OBLIGATORIAS
            if (plan.getObligatorias().contains(m)) {
                // Si al sacarla, el total de materias que quedan es menor al mínimo para graduarse...
                if (plan.getObligatorias().size() - 1 < plan.getMinObligatorias()) {
                    System.out.println("❌ ERROR: No se puede eliminar '" + m.getNombre() +
                            "'. El plan '" + plan.getNombre() + "' quedaría con " + (plan.getObligatorias().size() - 1) +
                            " obligatorias, pero exige " + plan.getMinObligatorias() + " para graduarse.");
                    return false;
                }
            }

            // 2. Verificación para OPTATIVAS
            if (plan.getOptativas().contains(m)) {
                if (plan.getOptativas().size() - 1 < plan.getMinOptativas()) {
                    System.out.println("❌ ERROR: No se puede eliminar '" + m.getNombre() +
                            "'. El plan '" + plan.getNombre() + "' quedaría con menos optativas de las " +
                            plan.getMinOptativas() + " que pide como mínimo.");
                    return false;
                }
            }
        }

        // --- SI PASÓ EL FILTRO, SE ELIMINA DE TODOS LADOS ---
        for (PlanDeEstudio plan : planesDeEstudio.values()) {
            plan.eliminarMateria(m);
        }

        // Lo borramos del mapa maestro de materias del sistema
        materias.remove(nombre);
        return true;
    }



    public HashMap<String, Materia> getMapaMaterias() {
        return materias;
    }



    public boolean addCorrelativaAMateria(String materia,String correlativa,boolean regular) {
        Materia materiaBuscada = materias.get(materia);
        Materia materiaCorrelativa = materias.get(correlativa);
        if (materiaBuscada != null && materiaCorrelativa != null && !materia.equals(correlativa)){ // Verificamos que la correlativa no sea la misma materia
            if (regular){
                materiaBuscada.addCorrelativa(new Prerrequisito(materiaCorrelativa, Prerrequisito.TipoPrerrequisito.REGULAR));
                return true;
            }else {
                materiaBuscada.addCorrelativa(new Prerrequisito(materiaCorrelativa, Prerrequisito.TipoPrerrequisito.APROBADA));
                return true;
            }
        }
        return false;
    }



    public boolean editarMateria(String nombre, String nuevoNombre, int nuevoCuatri) {
        Materia materia = materias.get(nombre);
        if (materia != null && !nuevoNombre.isEmpty()) {
            materia.setNombre(nuevoNombre);
            materia.setCuatrimestre(nuevoCuatri);
            return true;
        }
        return false;
    }



    public boolean agregarMateriaAPlan(Integer codigoPlan, String materia, boolean esObligatoria) {
        PlanDeEstudio plan = planesDeEstudio.get(codigoPlan);
        Materia materiaBuscada = materias.get(materia);

        if (plan != null && materiaBuscada != null) {
            plan.addMateria(materiaBuscada, esObligatoria);
            return true;
        }
        return false;
    }



    public boolean eliminarMateriaDePlan(Integer codigoPlan, String materia) {
        PlanDeEstudio plan = planesDeEstudio.get(codigoPlan);
        Materia m = materias.get(materia);
        if (plan != null && m != null) {
            plan.eliminarMateria(m); // Este borra de las listas internas del plan
            return true;
        }
        return false;
    }

    //----------------------------------------- METODOS REFERIDOS A LAS CARRERAS -----------------------------------------------------------------------------------------------------//



    //EDITAR NOMBRE CARRERA
    public boolean editarNombreCarrera (Integer codigo,String nombreCarrera){
        if (codigo != null && !nombreCarrera.isEmpty()){
            Carrera carreraBuscada = carreras.get(nombreCarrera);
            if (carreraBuscada != null){
                carreraBuscada.setNombre(nombreCarrera);
                return true;
            }
        }
        return false;
    }

    // ELIMINAR CARRERA
    public boolean eliminarCarrera(String nombreCarrera) {
        Carrera carrera = carreras.get(nombreCarrera);
        if (carrera != null) {
            // 1. Avisar a todos los alumnos de esa carrera que ya no pertenecen a ella
            for (Alumno a : carrera.getMapaAlumnos().values()) {
                a.setCarreraActual(null);
            }
            // 2. Borrar la carrera del sistema
            carreras.remove(nombreCarrera);
            return true;
        }
        return false;
    }

    // GET CARRERAS
    public HashMap<String, Carrera> getMapaCarreras() {
        return carreras;
    }


     // GET CARRERA
    public Carrera getCarrera(String nombreCarrera) {
        return carreras.get(nombreCarrera);
    }


    // ADD CARRERA
    public boolean addCarrera(Integer codigoCarrera, String nombreCarrera) {
        // Verificación simple
        if (nombreCarrera == null || nombreCarrera.isEmpty()) {
            System.out.println("Datos de la carrera incompletos.");
            return false;
        }else {
            Carrera nuevaCarrera = new Carrera(codigoCarrera, nombreCarrera);
            carreras.put(nuevaCarrera.getNombre(), nuevaCarrera);
            return true;
        }
    }


    //----------------------------------------- METODOS REFERIDOS A LOS ALUMNOS -----------------------------------------------------------------------------------------------------//


    // GET ALUMNO
    public Alumno getAlumno(String dni) {
        if (dni != null){
            return padronGeneral.get(dni);
        }
        return null;

    }

    // GET ALUMNOS DEL PADRON
    public HashMap<String, Alumno> getAlumnosDelPadron (){
        return padronGeneral;
    }

    // ELIMINAR ALUMNO DE CARRERA
    public boolean eliminarAlumnoDeCarrera(String nombreCarrera, String dni) {
        Carrera carrera = carreras.get(nombreCarrera);
        if (carrera != null) {
            Alumno alumno = carrera.getAlumno(dni);
            if (alumno != null) {
                carrera.eliminarAlumno(dni); // Lo saca de la carrera
                alumno.setCarreraActual(null);  // El alumno queda "sin carrera" pero vivo en el padrón
                return true;
            }
        }
        return false;
    }

    // ELIMINAR ALUMNO DE SISTEMA
    public boolean eliminarAlumnoDelSistema(String dni) {
        Alumno alumno = padronGeneral.get(dni);
        if (alumno != null) {
            // 1. Si está en una carrera, pedirle a la carrera que lo borre de su lista
            if (alumno.getCarreraActual() != null) {
                alumno.getCarreraActual().getMapaAlumnos().remove(dni);
            }
            // 2. Borrarlo del padrón global
            padronGeneral.remove(dni);
            return true;
        }
        return false;
    }

    // INSCRIBIR ALUMNO A CARRERA
    public boolean inscribirAlumnoACarrera(String dni, String nombreCarrera) {
        Alumno alumno = padronGeneral.get(dni); // Lo buscamos en el padrón global
        Carrera carrera = carreras.get(nombreCarrera);

        if (alumno != null && carrera != null) {
            // Verificamos que no esté ya inscripto para evitar duplicados en la lista de la carrera
            if (carrera.getAlumno(dni) == null) {
                carrera.inscribirAlumno(alumno); // Lo agrega a su lista interna
                alumno.setCarreraActual(carrera); // Le avisa al alumno cuál es su carrera
                return true;
            }
        }
        return false;
    }

    // REGISTRAR ALUMNO EN EL SISTEMA
    public boolean registrarAlumnoEnSistema(Alumno alumno) {
        if (alumno != null && !padronGeneral.containsKey(alumno.getDni())) {
            padronGeneral.put(alumno.getDni(), alumno);
            return true;
        }
        return false;
    }

    // EDITAR ALUMNO
    public boolean editarAlumno (String nombre,Integer legajo, String dni){
        if (!nombre.isEmpty() && legajo != null && !dni.isEmpty()){
            Alumno alumnoBuscado = padronGeneral.get(dni);
            if (alumnoBuscado != null){
                alumnoBuscado.editarDatos(nombre,legajo);
                return true;
            }
        }
        return false;
    }

    //INSCRIBIR ALUMNOA MATERIA
    public boolean inscribirAlumnoAMateria(String dni,String nombreMateria) {
        Alumno a = getAlumno(dni);
        Materia m = materias.get(nombreMateria);
        if (a != null && m != null) {
            // Podés agregar aquí la validación de aptas antes de inscribir
            if (a.getMateriasAptasACursar().contains(m)) {
                a.inscribirACursada(m);
                return true;
            }
        }
        return false;
    }

    // RENDIR PARCIAL
    public void rendirParcial(String dni, String materia, boolean aprobado) {
        Alumno a = getAlumno(dni);
        Materia m = materias.get(materia);
        if (a != null && m != null) a.rendirParcial(m, aprobado);
    }

    // RENDIR FINAL
    public void rendirFinal(String dni, String materia) {
        Alumno a = getAlumno(dni);
        Materia m = materias.get(materia);
        if (a != null && m != null) a.rendirFinal(m);
    }



}
