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
        System.out.println("📂 [PERSISTENCIA] Archivo base cargado correctamente.");

        // ==================================================================
        // 2. MÉTODO POBLADOR
        // ==================================================================

         // popularDatosIniciales(ModeloSistemaAcademico.getInstancia());

        // ==================================================================

        // 3. ENCENDIDO DEL PANEL GRÁFICO
        System.out.println("🖥️ [INTERFAZ] Abriendo Panel de Control Gráfico...");

        javax.swing.SwingUtilities.invokeLater(() -> {
            vista.VentanaPrincipalUI ventana = new vista.VentanaPrincipalUI();
            controlador.ControladorPrincipal controladorGlobal = new controlador.ControladorPrincipal(ventana, ModeloSistemaAcademico.getInstancia());

            // Configuramos el guardado automático al tocar la cruz de la ventana
            ventana.setDefaultCloseOperation(javax.swing.JFrame.DO_NOTHING_ON_CLOSE);
            ventana.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent e) {
                    System.out.println("\n💾 [PERSISTENCIA] Guardando estado actual antes de salir...");
                    ModeloSistemaAcademico modeloActual = ModeloSistemaAcademico.getInstancia();

                    GestorPersistencia.guardarDatos(modeloActual, ARCHIVO_DATOS);
                    System.out.println("👋 [SISTEMA] Datos asegurados con éxito. ¡Nos vemos!");
                    ventana.dispose();
                    System.exit(0);
                }
            });

            ventana.setVisible(true);
        });
    }


     // Método Auxiliar para cargar el entorno de pruebas inicial.

    private static void popularDatosIniciales(ModeloSistemaAcademico modelo) {
        System.out.println("\n🚀 [POBLADOR] Cargando datos estáticos de prueba por primera vez...");

        // Altas de Carreras
        modelo.addCarrera(10, "Licenciatura en Sistemas");
        modelo.addCarrera(20, "Licenciatura en Turismo");

        // Alta de Materias
        modelo.registrarMateria(101, "Introduccion a la Programacion", 1);
        modelo.registrarMateria(102, "Algebra", 1);
        modelo.registrarMateria(103, "Programacion II", 2);
        modelo.registrarMateria(201, "Introduccion al Turismo", 1);
        modelo.registrarMateria(202, "Geografia Turistica", 1);

        // Configuración de Correlativas
        modelo.addCorrelativaAMateria("Programacion II", "Introduccion a la Programacion", false);

        // Plan de Estudios (Sistemas)
        ArrayList<Integer> obligatoriasIDs = new ArrayList<>();
        obligatoriasIDs.add(101); obligatoriasIDs.add(102); obligatoriasIDs.add(103);
        ArrayList<Integer> optativasIDs = new ArrayList<>();

        modelo.crearPlanCompleto("SISTEMAS INFORMATICOS - 2026", 123, 2, 0, new CondicionA(), obligatoriasIDs, optativasIDs);
        modelo.asignarPlanDeEstudioACarrera(123, "Licenciatura en Sistemas");

        // Plan de Estudios (Turismo)
        ArrayList<Integer> obligatoriasTurismo = new ArrayList<>(); obligatoriasTurismo.add(201);
        ArrayList<Integer> optativasTurismo = new ArrayList<>(); optativasTurismo.add(202);

        modelo.crearPlanCompleto("PLAN TURISMO GLOBAL - 2026", 456, 1, 1, new CondicionA(), obligatoriasTurismo, optativasTurismo);
        modelo.asignarPlanDeEstudioACarrera(456, "Licenciatura en Turismo");

        // Alumnos iniciales
        Alumno alumno1 = new Alumno("Maximiliano Rubidarte", 4001, "45123456");
        Alumno alumno2 = new Alumno("Ezequiel Rubidarte", 4002, "47987654");

        modelo.registrarAlumnoEnSistema(alumno1);
        modelo.registrarAlumnoEnSistema(alumno2);

        modelo.inscribirAlumnoACarrera("45123456", "Licenciatura en Sistemas");
        modelo.inscribirAlumnoACarrera("47987654", "Licenciatura en Sistemas");

        // Forzamos el primer guardado en el archivo físico para dejarlo asentado
        GestorPersistencia.guardarDatos(modelo, ARCHIVO_DATOS);
        System.out.println("✅ [POBLADOR] Datos iniciales guardados con éxito en el archivo.\n");
    }
}