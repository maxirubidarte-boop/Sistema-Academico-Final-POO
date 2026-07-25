package modelo;

import java.io.Serializable;
import java.util.ArrayList;
import persistencia.GestorPersistencia;

public class DemoSistema implements Serializable {

    private static final long serialVersionUID = 19L;
    private static final String ARCHIVO_DATOS = "sistema_academico.dat";

    public static void main(String[] args) {
        System.out.println("====== [SISTEMA] INICIALIZANDO MOTOR ACADÉMICO ======");

        // 1. CARGAR LOS DATOS EXISTENTES
        ModeloSistemaAcademico modelo = GestorPersistencia.cargarDatos(ARCHIVO_DATOS);
        ModeloSistemaAcademico.setInstancia(modelo);
        System.out.println(" [PERSISTENCIA] Archivo base cargado correctamente.");

        // ==================================================================
        // 2. MÉTODO POBLADOR
        // ==================================================================

        //popularDatosIniciales(ModeloSistemaAcademico.getInstancia());

        // ==================================================================

        // 3. ENCENDIDO DEL PANEL GRÁFICO
        System.out.println(" [INTERFAZ] Abriendo Panel de Control Gráfico...");

        javax.swing.SwingUtilities.invokeLater(() -> {
            vista.VentanaPrincipalUI ventana = new vista.VentanaPrincipalUI();
            controlador.ControladorPrincipal controladorGlobal = new controlador.ControladorPrincipal(ventana, ModeloSistemaAcademico.getInstancia());

            // Configuramos el guardado automático al tocar la cruz de la ventana
            ventana.setDefaultCloseOperation(javax.swing.JFrame.DO_NOTHING_ON_CLOSE);
            ventana.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent e) {
                    System.out.println("\n [PERSISTENCIA] Guardando estado actual antes de salir...");
                    ModeloSistemaAcademico modeloActual = ModeloSistemaAcademico.getInstancia();

                    GestorPersistencia.guardarDatos(modeloActual, ARCHIVO_DATOS);
                    System.out.println(" [SISTEMA] Datos asegurados con éxito. ¡Nos vemos!");
                    ventana.dispose();
                    System.exit(0);
                }
            });

            ventana.setVisible(true);
        });
    }


     // Método Auxiliar para cargar el entorno de pruebas inicial.

    private static void popularDatosIniciales(ModeloSistemaAcademico modelo) {
        System.out.println("\n🚀 [POBLADOR] Cargando datos estáticos de prueba de Máxima Densidad (4 Carreras / 10 Alumnos)...");

        // ==========================================
        // 1. ALTAS DE CARRERAS (4 Carreras)
        // ==========================================
        modelo.addCarrera(10, "Licenciatura en Sistemas");
        modelo.addCarrera(20, "Licenciatura en Turismo");
        modelo.addCarrera(30, "Licenciatura en Biologia Marina");
        modelo.addCarrera(40, "Ingenieria Forestal");

        // ==========================================
        // 2. REGISTRO DE MATERIAS (5 por carrera: 3 Obligatorias + 2 Optativas)
        // ==========================================
        // Materias - Sistemas (Código 1xx)
        modelo.registrarMateria(101, "Introduccion a la Programacion", 1);
        modelo.registrarMateria(102, "Algebra I", 1);
        modelo.registrarMateria(103, "Programacion II", 2);
        modelo.registrarMateria(104, "Bases de Datos I", 2); // Optativa 1
        modelo.registrarMateria(105, "Laboratorio de Sistemas", 3); // Optativa 2

        // Materias - Turismo (Código 2xx)
        modelo.registrarMateria(201, "Introduccion al Turismo", 1);
        modelo.registrarMateria(202, "Geografia Turistica", 1);
        modelo.registrarMateria(203, "Hoteleria y Servicios", 2);
        modelo.registrarMateria(204, "Circuitos Regionales", 2); // Optativa 1
        modelo.registrarMateria(205, "Portugues Aplicado", 3); // Optativa 2

        // Materias - Biología Marina (Código 3xx)
        modelo.registrarMateria(301, "Biologia General", 1);
        modelo.registrarMateria(302, "Quimica Inorganica", 1);
        modelo.registrarMateria(303, "Oceanografia", 2);
        modelo.registrarMateria(304, "Morfologia de Invertebrados", 2); // Optativa 1
        modelo.registrarMateria(305, "Ecologia Estuarina", 3); // Optativa 2

        // Materias - Ingeniería Forestal (Código 4xx)
        modelo.registrarMateria(401, "Botanica General", 1);
        modelo.registrarMateria(402, "Morfologia Vegetal", 1);
        modelo.registrarMateria(403, "Dendrologia", 2);
        modelo.registrarMateria(404, "Sistemas de Informacion Geografica", 2); // Optativa 1
        modelo.registrarMateria(405, "Incendios Forestales", 3); // Optativa 2

        // ==========================================
        // 3. CONFIGURACIÓN DE CORRELATIVAS
        // ==========================================
        modelo.addCorrelativaAMateria("Programacion II", "Introduccion a la Programacion", false);
        modelo.addCorrelativaAMateria("Hoteleria y Servicios", "Introduccion al Turismo", false);
        modelo.addCorrelativaAMateria("Oceanografia", "Biologia General", false);
        modelo.addCorrelativaAMateria("Dendrologia", "Botanica General", false);

        // ==========================================
        // 4. PLANES DE ESTUDIO (Cada uno con 3 obligatorias y 2 optativas)
        // ==========================================
        // Plan Sistemas
        ArrayList<Integer> obligatoriasSistemas = new ArrayList<>(java.util.List.of(101, 102, 103));
        ArrayList<Integer> optativasSistemas = new ArrayList<>(java.util.List.of(104, 105));
        modelo.crearPlanCompleto("SISTEMAS INFORMATICOS - 2026", 123, 3, 1, new CondicionA(), obligatoriasSistemas, optativasSistemas);
        modelo.asignarPlanDeEstudioACarrera(123, "Licenciatura en Sistemas");

        // Plan Turismo
        ArrayList<Integer> obligatoriasTurismo = new ArrayList<>(java.util.List.of(201, 202, 203));
        ArrayList<Integer> optativasTurismo = new ArrayList<>(java.util.List.of(204, 205));
        modelo.crearPlanCompleto("TURISMO REGIONAL Y GLOBAL - 2026", 456, 3, 1, new CondicionB(), obligatoriasTurismo, optativasTurismo);
        modelo.asignarPlanDeEstudioACarrera(456, "Licenciatura en Turismo");

        // Plan Biología Marina
        ArrayList<Integer> obligatoriasBiologia = new ArrayList<>(java.util.List.of(301, 302, 303));
        ArrayList<Integer> optativasBiologia = new ArrayList<>(java.util.List.of(304, 305));
        modelo.crearPlanCompleto("BIOLOGIA MARINA AUSTRAL - 2026", 789, 3, 1, new CondicionD(), obligatoriasBiologia, optativasBiologia);
        modelo.asignarPlanDeEstudioACarrera(789, "Licenciatura en Biologia Marina");

        // Plan Ingeniería Forestal
        ArrayList<Integer> obligatoriasForestal = new ArrayList<>(java.util.List.of(401, 402, 403));
        ArrayList<Integer> optativasForestal = new ArrayList<>(java.util.List.of(404, 405));
        modelo.crearPlanCompleto("INGENIERIA FORESTAL - 2026", 1011, 3, 1, new CondicionE(), obligatoriasForestal, optativasForestal);
        modelo.asignarPlanDeEstudioACarrera(1011, "Ingenieria Forestal");

        // ==========================================
        // 5. REGISTRO DE ALUMNOS (10 Alumnos en total)
        // ==========================================
        Alumno alumno1 = new Alumno("Maximiliano Rubidarte", 4001, "45123456");
        Alumno alumno2 = new Alumno("Ezequiel Rubidarte", 4002, "47987654");
        Alumno alumno3 = new Alumno("Luana Fernandez", 4003, "46111222");
        Alumno alumno4 = new Alumno("Lucas Gomez", 4004, "44333444");
        Alumno alumno5 = new Alumno("Martina Rossi", 4005, "45555666");
        Alumno alumno6 = new Alumno("Santiago Lopez", 4006, "46777888");
        Alumno alumno7 = new Alumno("Camila Benitez", 4007, "43888999");
        Alumno alumno8 = new Alumno("Tomas Castro", 4008, "42999111");
        Alumno alumno9 = new Alumno("Valentina Diaz", 4009, "44111333");
        Alumno alumno10 = new Alumno("Mateo Romero", 4010, "45222444");

        modelo.registrarAlumnoEnSistema(alumno1);
        modelo.registrarAlumnoEnSistema(alumno2);
        modelo.registrarAlumnoEnSistema(alumno3);
        modelo.registrarAlumnoEnSistema(alumno4);
        modelo.registrarAlumnoEnSistema(alumno5);
        modelo.registrarAlumnoEnSistema(alumno6);
        modelo.registrarAlumnoEnSistema(alumno7);
        modelo.registrarAlumnoEnSistema(alumno8);
        modelo.registrarAlumnoEnSistema(alumno9);
        modelo.registrarAlumnoEnSistema(alumno10);

        // ==========================================
        // 6. DISTRIBUCIÓN E INSCRIPCIÓN A CARRERAS
        // ==========================================
        //  REQUISITO: Repartir al menos 6 alumnos distribuidos asegurando las 4 carreras
        modelo.inscribirAlumnoACarrera("45123456", "Licenciatura en Sistemas");     // 1. Maxi
        modelo.inscribirAlumnoACarrera("46111222", "Licenciatura en Turismo");      // 2. Luana
        modelo.inscribirAlumnoACarrera("44333444", "Licenciatura en Biologia Marina"); // 3. Lucas
        modelo.inscribirAlumnoACarrera("43888999", "Ingenieria Forestal");          // 4. Camila
        modelo.inscribirAlumnoACarrera("42999111", "Ingenieria Forestal");          // 5. Tomas
        modelo.inscribirAlumnoACarrera("44111333", "Licenciatura en Biologia Marina"); // 6. Valentina

        // Los 4 alumnos restantes los dispersamos libremente
        modelo.inscribirAlumnoACarrera("47987654", "Licenciatura en Sistemas");     // Ezequiel
        modelo.inscribirAlumnoACarrera("45555666", "Licenciatura en Turismo");      // Martina
        modelo.inscribirAlumnoACarrera("46777888", "Licenciatura en Sistemas");     // Santiago
        modelo.inscribirAlumnoACarrera("45222444", "Ingenieria Forestal");          // Mateo

        // ==========================================
        // 7. FORZAR GUARDADO EN DISCO
        // ==========================================
        GestorPersistencia.guardarDatos(modelo, ARCHIVO_DATOS);
        System.out.println("✅ [POBLADOR] Base de datos de alta densidad inicializada con éxito.\n");
    }
}