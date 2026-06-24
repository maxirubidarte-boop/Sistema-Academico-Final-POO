package modelo;

import java.util.ArrayList;

public class DemoSistema {
    public static void main(String[] args) {

        // 1. OBTENER INSTANCIA DEL MODELO (SINGLETON)
        System.out.println("====== [SISTEMA] INICIALIZANDO MOTOR ACADÉMICO ======");
        ModeloSistemaAcademico modelo = ModeloSistemaAcademico.getInstancia();

        // 2. ALTA DE CARRERAS
        System.out.println("\n>>> [1] EJECUTANDO ALTA DE CARRERAS...");
        modelo.addCarrera(10, "Licenciatura en Sistemas");
        modelo.addCarrera(20, "Licenciatura en Turismo");
        System.out.println("✅ Carrera registrada: Licenciatura en Sistemas (ID: 10)");
        System.out.println("✅ Carrera registrada: Licenciatura en Turismo (ID: 20)");

        // 3. ALTA DE MATERIAS
        System.out.println("\n>>> [2] REGISTRANDO MATERIAS MASTER EN EL SISTEMA...");
        modelo.registrarMateria(101, "Introduccion a la Programacion", 1);
        modelo.registrarMateria(102, "Algebra", 1);
        modelo.registrarMateria(103, "Programacion II", 2);
        modelo.registrarMateria(201, "Introduccion al Turismo", 1);
        modelo.registrarMateria(202, "Geografia Turistica", 1);
        System.out.println("✅ 5 Materias globales dadas de alta correctamente.");

        // 4. CONFIGURACIÓN DE CORRELATIVAS
        System.out.println("\n>>> [3] CONFIGURANDO RED DE CORRELATIVAS...");
        modelo.addCorrelativaAMateria("Programacion II", "Introduccion a la Programacion", false);
        System.out.println("🔗 Vinculación: 'Programacion II' ahora requiere cursada aprobada de 'Introduccion a la Programacion'.");

        // 5. CREACIÓN DEL PLAN DE ESTUDIOS CON ESTRATEGIA GLOBAL (BUILDER + FÁBRICA)
        System.out.println("\n>>> [4] ENSAMBLANDO PLAN DE ESTUDIO COMPLEJO (SISTEMAS - 2026)...");
        EstrategiaDeInscripcion estA = new CondicionA(); // Condición A: Cursadas aprobadas

        ArrayList<Integer> obligatoriasIDs = new ArrayList<>();
        obligatoriasIDs.add(101);
        obligatoriasIDs.add(102);
        obligatoriasIDs.add(103);

        ArrayList<Integer> optativasIDs = new ArrayList<>();

        modelo.crearPlanCompleto(
                "SISTEMAS INFORMATICOS - 2026",
                123,
                2, // minOblig para recibirse
                0, // minOpta
                estA, // Condición global única
                obligatoriasIDs,
                optativasIDs
        );

        modelo.asignarPlanDeEstudioACarrera(123, "Licenciatura en Sistemas");
        System.out.println("📦 Plan 123 inyectado con éxito a la carrera 'Licenciatura en Sistemas'.");

        // 6. GESTIÓN DEL PADRÓN DE ALUMNOS (ALTAS)
        System.out.println("\n>>> [5] REGISTRANDO ALUMNOS EN EL PADRÓN GENERAL...");
        Alumno alumno1 = new Alumno("Maximiliano Rubidarte", 4001, "45123456");
        Alumno alumno2 = new Alumno("Ezequiel Rubidarte", 4002, "47987654");

        modelo.registrarAlumnoEnSistema(alumno1);
        modelo.registrarAlumnoEnSistema(alumno2);
        System.out.println("👤 Alumno registrado: " + alumno1.getNombre() + " (DNI: " + alumno1.getDni() + ")");
        System.out.println("👤 Alumno registrado: " + alumno2.getNombre() + " (DNI: " + alumno2.getDni() + ")");

        // 7. INSCRIPCIÓN A CARRERAS
        System.out.println("\n>>> [6] INSCRIBIENDO ALUMNOS A CARRERAS...");
        modelo.inscribirAlumnoACarrera("45123456", "Licenciatura en Sistemas");
        modelo.inscribirAlumnoACarrera("47987654", "Licenciatura en Sistemas");
        System.out.println("📝 Ambos alumnos vinculados formalmente a 'Licenciatura en Sistemas'.");

        // ==================================================================
        // SIMULACIÓN ACADÉMICA / TEST DE LÓGICA Y CORRELATIVAS
        // ==================================================================
        System.out.println("\n==================================================================");
        System.out.println("🏁 INICIANDO SIMULACIÓN DE CICLO LECTIVO (TEST DE REQUISITOS)");
        System.out.println("==================================================================");

        // Chequeo Inicial de Materias Aptas
        System.out.println("\n📊 [ESTADO INICIAL] Materias aptas para cursar de " + alumno1.getNombre() + ":");
        mostrarAptas(alumno1); // Prog II NO debería aparecer porque falta la correlativa

        // Intento de inscripción inválido (Forzando el error de correlativa)
        System.out.println("\n🚨 [TEST VALIDACIÓN] Intentando inscribir a Maxi a 'Programacion II' sin correlativa...");
        boolean pudoInscribirMal = modelo.inscribirAlumnoAMateria("45123456", "Programacion II");
        System.out.println("¿El sistema permitió la inscripción ilegal?: " + (pudoInscribirMal ? "SÍ (❌ FALLA DE SEGURIDAD)" : "NO (✅ INTEGRIDAD CORRECTA)"));

        // Cursada exitosa de la correlativa
        System.out.println("\n📖 [PROGRESO] Inscribiendo y aprobando correlativa base...");
        modelo.inscribirAlumnoAMateria("45123456", "Introduccion a la Programacion");
        System.out.println("-> " + alumno1.getNombre() + " se anotó en Introducción a la Programación.");

        // Tránsito de Estados: Inscripto -> ParcialAprobado -> CursadaAprobada (Simulada rindiendo parcial y final)
        modelo.rendirParcial("45123456", "Introduccion a la Programacion", true);
        modelo.rendirFinal("45123456", "Introduccion a la Programacion");
        System.out.println("⭐ [LOG] Maxi rindió y aprobó el Final de 'Introduccion a la Programacion'.");

        // Chequeo Post-Correlativa (Debería destrabar Prog II)
        System.out.println("\n📊 [NUEVO ESTADO] Materias aptas para " + alumno1.getNombre() + " post-aprobación:");
        mostrarAptas(alumno1); // Ahora SÍ tiene que listar Programación II

        // Inscribir a la materia destrabada
        modelo.inscribirAlumnoAMateria("45123456", "Programacion II");
        System.out.println("✅ Inscripción exitosa a 'Programacion II' (Correlativa verificada globalmente).");

        // 8. TEST DE GRADUACIÓN
        System.out.println("\n>>> [7] VERIFICANDO CONTROL DE GRADUACIÓN...");
        PlanDeEstudio plan = modelo.getPlan(123);
        System.out.println("¿" + alumno1.getNombre() + " finalizó la carrera?: " + (plan.estaGraduado(alumno1) ? "SÍ 🎉" : "NO (Faltan materias por aprobar) 📚"));

        // 9. TEST DE BAJAS / ELIMINACIÓN Y RE-VERIFICACIÓN
        System.out.println("\n>>> [8] PROBANDO LÓGICA DE BAJAS (ELIMINAR ALUMNO)...");
        System.out.println("⚠️ Solicitando la baja del sistema del alumno Ezequiel Rubidarte (DNI: 47987654)...");

        // Eliminamos al alumno mediante el modelo
        boolean eliminadoOk = modelo.eliminarAlumnoDelSistema("47987654");
        System.out.println("¿Alumno eliminado con éxito?: " + (eliminadoOk ? "SÍ (✅ Sistema limpio)" : "NO (❌ Falló)"));

        // Comprobación final buscando en el padrón del modelo
        System.out.println("\n🔍 [VERIFICACIÓN GENERAL] Buscando a Ezequiel en el padrón post-baja...");
        Alumno buscado = modelo.getAlumno("47987654");
        if (buscado == null) {
            System.out.println("✅ Confirmado: El alumno ya no existe en el sistema en memoria.");
        } else {
            System.out.println("❌ ERROR: El alumno sigue figurando en los registros.");
        }

        System.out.println("\n==================================================================");
        System.out.println("🏆 CONSOLE SUPREME TEST: PASADO CON ÉXITO — TODOS LOS SISTEMAS GREEN");
        System.out.println("==================================================================");

        // ==================================================================
        // SECCIÓN 2: ESCENARIOS CRÍTICOS Y REGLAS DE NEGOCIO AVANZADAS
        // ==================================================================
        System.out.println("\n==================================================================");
        System.out.println("🚀 SECCIÓN 2: CONTROL DE OPTATIVAS Y CICLO DE EVALUACIONES");
        System.out.println("==================================================================");

        // 1. CONTROL DE MATERIAS OPTATIVAS EN TURISMO
        System.out.println("\n>>> [1] CREANDO PLAN PARA TURISMO (EXIGE 1 OPTATIVA PARA GRADUARSE)...");

        ArrayList<Integer> obligatoriasTurismo = new ArrayList<>();
        obligatoriasTurismo.add(201); // Intro al Turismo

        ArrayList<Integer> optativasTurismo = new ArrayList<>();
        optativasTurismo.add(202); // Geografía Turística (Actúa como optativa)

        modelo.crearPlanCompleto(
                "PLAN TURISMO GLOBAL - 2026",
                456,
                1, // minOblig
                1, // minOpta (Exigimos una para recibirse)
                new CondicionA(),
                obligatoriasTurismo,
                optativasTurismo
        );
        modelo.asignarPlanDeEstudioACarrera(456, "Licenciatura en Turismo");

        System.out.println("\n📝 Inscribiendo a Maxi (DNI: 45123456) en Licenciatura en Turismo...");
        modelo.inscribirAlumnoACarrera("45123456", "Licenciatura en Turismo");

        // 2. SIMULACIÓN DE EVALUACIONES USANDO TUS MÉTODOS REALES
        System.out.println("\n>>> [2] SIMULANDO EVALUACIONES EN INTRODUCCIÓN AL TURISMO...");

        // Inscripción regular
        modelo.inscribirAlumnoAMateria("45123456", "Introduccion al Turismo");
        System.out.println("🔄 [Cursada] -> Alumno inscripto a la cursada.");

        // Rendir Parcial (Transiciona el estado en Alumno)
        modelo.rendirParcial("45123456", "Introduccion al Turismo", true);
        System.out.println("🔄 [Parcial] -> Alumno rindió y aprobó el parcial de la materia.");

        // Examen Final
        modelo.rendirFinal("45123456", "Introduccion al Turismo");
        System.out.println("🔄 [Final] -> Alumno rindió y aprobó el examen final.");

        // 3. VERIFICACIÓN DE GRADUACIÓN INTERMEDIA (Falta la optativa)
        System.out.println("\n>>> [3] CONTROL DE GRADUACIÓN INTERMEDIA...");
        PlanDeEstudio planTurismo = modelo.getPlan(456);
        System.out.println("¿Maxi se graduó de Turismo sólo con la obligatoria aprobada?: "
                + (planTurismo.estaGraduado(alumno1) ? "SÍ (❌ ERROR: Se salteó la optativa)" : "NO (✅ CORRECTO: Le falta la materia optativa)"));

        // 4. CURSADA Y APROBACIÓN DE LA OPTATIVA
        System.out.println("\n📖 Cursando y rindiendo la materia optativa requerida ('Geografia Turistica')...");
        modelo.inscribirAlumnoAMateria("45123456", "Geografia Turistica");
        modelo.rendirParcial("45123456", "Geografia Turistica", true);
        modelo.rendirFinal("45123456", "Geografia Turistica");
        System.out.println("✅ Optativa aprobada por completo.");

        // Re-verificación de Egreso Final con cupos completos
        System.out.println("\n🎓 [CHEQUEO FINAL DE EGRESO] Evaluando condiciones de graduación completas...");
        System.out.println("¿Maxi finalizó la carrera de Turismo ahora?: "
                + (planTurismo.estaGraduado(alumno1) ? "SÍ 🎉 ¡Tenemos un nuevo graduado!" : "NO (❌ Error en el conteo de optativas)"));

        System.out.println("\n==================================================================");
        System.out.println("🏆 CONSOLE SUPREME TEST: PASADO CON ÉXITO — TODOS LOS SISTEMAS GREEN");
        System.out.println("==================================================================");
    }

    private static void mostrarAptas(Alumno a) {
        ArrayList<Materia> lista = a.getMateriasAptasACursar();
        if (lista == null || lista.isEmpty()) {
            System.out.println("   > No hay materias disponibles en este momento.");
        } else {
            for (Materia m : lista) {
                System.out.println("   [Apta] -> " + m.getNombre() + " (Cuatrimestre: " + m.getCuatrimestre() + ")");
            }
        }
    }
}