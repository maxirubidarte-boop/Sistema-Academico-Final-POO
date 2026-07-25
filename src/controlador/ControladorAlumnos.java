package controlador;

import modelo.*;
import vista.PanelAlumnosUI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JOptionPane;

public class ControladorAlumnos implements ActionListener {

    private PanelAlumnosUI vista;
    private ModeloSistemaAcademico modelo;

    // Variables de control para la paginación
    private int paginaActual = 1;
    private final int FILAS_POR_PAGINA = 10;

    // Banderas de estado para el botón "Guardar"
    private boolean esAlta = true;
    private boolean esInscripcion = false; //  Controla si el guardado es para una carrera

    //referencia al alumno que se está gestionando
    private String dniAlumnoActualCursadas = "";

    public ControladorAlumnos(PanelAlumnosUI vista, ModeloSistemaAcademico modelo) {
        this.vista = vista;
        this.modelo = modelo;

        // Ponemos al controlador a escuchar TODOS los componentes de la vista
        this.vista.escucharComponentes(this);

        // conectamos el metodo que actualiza el boton con el listener de la tabla
        this.vista.getTablaAlumnos().getSelectionModel().addListSelectionListener(e -> {
            // e.getValueIsAdjusting() evita que el código se ejecute dos veces (al hacer clic y al soltar)
            if (!e.getValueIsAdjusting()) {
                actualizarBotonCarreraSegunSeleccion();
            }
        });

        // Renderizamos la tabla por primera vez
        refrescarTablaPaginada();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String comando = e.getActionCommand();

        switch (comando) {
            case "Agregar Alumno":
                esAlta = true;
                esInscripcion = false;
                vista.mostrarModoAlta();
                break;

            case "Editar Seleccionado":
                editarSeleccionado();
                break;

            case "Verificar Egreso":
                verificarEgreso();
                break;

            case "Inscribir a Carrera":
            case "Dar Baja de Carrera":
                inscribirACarrera();
                break;

            case "Eliminar":
                eliminarAlumno();
                break;

            case "Guardar":
                ejecutarGuardado();
                break;

            case "Cancelar":
                vista.mostrarModoLista();
                break;

            case "Gestionar Cursadas":
                prepararPantallaCursadas();
                break;

            case "Inscribir a Materia":
                inscribirAMateria();
                break;

            case "Rendir Parcial":
                rendirParcial();
                break;

            case "Rendir Final":
                rendirFinal();
                break;

            case "Volver a Lista":
                volverALista();
                break;

            case "< Anterior":
                if (paginaActual > 1) {
                    paginaActual--;
                    refrescarTablaPaginada();
                }
                break;

            case "Siguiente >":
                // Calcula el total de alumnos en caliente
                int totalAlumnos = modelo.getAlumnosDelPadron().size();
                int maxPaginas = (int) Math.ceil((double) totalAlumnos / FILAS_POR_PAGINA);
                if (paginaActual < maxPaginas) {
                    paginaActual++;
                    refrescarTablaPaginada();
                }
                break;
        }
    }

    private void prepararPantallaCursadas(){
        int filaSeleccionada = vista.getTablaAlumnos().getSelectedRow();
        if (filaSeleccionada == -1){
            JOptionPane.showMessageDialog(vista, "Seleccione un alumno para gestionar sus cursadas.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        dniAlumnoActualCursadas = vista.getModeloTabla().getValueAt(filaSeleccionada, 0).toString();
        Alumno alumno = modelo.getAlumno(dniAlumnoActualCursadas);

        if (alumno == null){
            return;
        }

        refrescarDatosYAptasCursadas(alumno);
    }

    private void refrescarDatosYAptasCursadas(Alumno alumno) {
        String nombreCarrera = (alumno.getCarreraActual() != null) ? alumno.getCarreraActual().getNombre() : "No inscripto";

        // Mapeamos dinámicamente las Materias a un ArrayList<String> con sus nombres
        ArrayList<String> nombresMateriasAptas = new ArrayList<>();
        if (alumno.getCarreraActual() != null && alumno.getCarreraActual().getPlanDeEstudio() != null) {
            ArrayList<Materia> materiasAptasObjetos = alumno.getMateriasAptasACursar();
            for (Materia m : materiasAptasObjetos) {
                nombresMateriasAptas.add(m.getNombre());
            }
        }

        // Poblar la tabla de cursadas actuales del alumno
        vista.getModeloTablaCursadas().setRowCount(0);

        if (alumno.getCursadas() != null) {
            for (Cursada c : alumno.getCursadas()){
                String estadoStr = c.getEstado().getClass().getSimpleName();
                if (c.estaAprobada()) estadoStr = "APROBADA";
                else if (c.estaRegular()) estadoStr = "REGULAR";

                Object[] fila = { c.getMateria().getNombre(), estadoStr };
                vista.getModeloTablaCursadas().addRow(fila);
            }
        }

        // Le pasamos la lista limpia de Strings a la vista sin tocar la UI
        vista.mostrarModoGestionCursadas(alumno.getNombre(), alumno.getDni(), nombreCarrera, nombresMateriasAptas);
    }

    private void actualizarBotonCarreraSegunSeleccion(){
        int filaSeleccionada = vista.getTablaAlumnos().getSelectedRow();

        // si no hay nadie seleccionado, dejamos el texto por defecto y salimos
        if (filaSeleccionada == -1){
            vista.getBtnInscribirCarrera().setText("Inscribir a Carrera");
            return;
        }

        // Recuperamos el DNI de la fila seleccionada
        String dni = vista.getModeloTabla().getValueAt(filaSeleccionada, 0).toString();
        Alumno alumno = modelo.getAlumno(dni);

        if (alumno != null){
            if (alumno.getCarreraActual() != null){
                vista.getBtnInscribirCarrera().setText("Dar Baja de Carrera");
            }else {
                vista.getBtnInscribirCarrera().setText("Inscribir a Carrera");
            }
        }
    }

    private void inscribirAMateria(){
        Alumno alumno = modelo.getAlumno(dniAlumnoActualCursadas);
        String nombreMateria = vista.getMateriaSeleccionadaCombo();

        if (nombreMateria.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Seleccione una materia válida.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Materia materiaSelecionada = null;
        for (Materia m: alumno.getMateriasAptasACursar()){
            if (m.getNombre().equals(nombreMateria)){
                materiaSelecionada = m;
                break;
            }
        }

        if (materiaSelecionada != null){
            alumno.inscribirACursada(materiaSelecionada);
            JOptionPane.showMessageDialog(vista, "Inscripción exitosa a " + nombreMateria, "Éxito", JOptionPane.INFORMATION_MESSAGE);
            refrescarDatosYAptasCursadas(alumno);
        }
    }

    private void volverALista (){
        dniAlumnoActualCursadas = "";
        refrescarTablaPaginada();
        vista.mostrarModoLista();
    }

    private void rendirParcial (){
        // 1. Validar que haya una cursada seleccionada en la tabla de cursadas
        int filaSeleccionada = vista.getTablaCursadas().getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(vista, "Seleccione una cursada de la tabla para registrar el parcial.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Alumno alumno = modelo.getAlumno(dniAlumnoActualCursadas);
        if (alumno == null) return;

        // 2. Recuperar el nombre de la materia de la fila seleccionada
        String nombreMateria = vista.getModeloTablaCursadas().getValueAt(filaSeleccionada, 0).toString();
        Cursada cursadaSeleccionada = null;

        for (Cursada c:alumno.getCursadas()){
            if (c.getMateria().getNombre().equals(nombreMateria)){
                cursadaSeleccionada = c;
                break;
            }
        }

        if (cursadaSeleccionada != null) {

            if (cursadaSeleccionada.estaAprobada() || cursadaSeleccionada.estaRegular()) {
                JOptionPane.showMessageDialog(vista, "La materia " + nombreMateria + " ya se encuentra APROBADA o REGULARIZADA.\nNo se pueden registrar nuevos parciales.", "Acción Denegada", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String notaStr = JOptionPane.showInputDialog(vista, "Ingrese la nota del Parcial (1-10):", "Registrar Parcial", JOptionPane.QUESTION_MESSAGE);
            if (notaStr == null || notaStr.trim().isEmpty()) return; // Canceló o dejó vacío

            try {
                int nota = Integer.parseInt(notaStr);
                if (nota < 1 || nota > 10) {
                    JOptionPane.showMessageDialog(vista, "La nota debe estar entre 1 y 10.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                boolean aprueba = (nota >= 4);

                // Ejecuta la evaluación
                cursadaSeleccionada.rendirParcial(aprueba);

                JOptionPane.showMessageDialog(vista, "Examen parcial registrado con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                refrescarDatosYAptasCursadas(alumno); // Refresca la tabla en caliente

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(vista, "Ingrese un número entero válido.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void rendirFinal (){
        // 1. Validar que haya una cursada seleccionada en la tabla de cursadas
        int filaSeleccionada = vista.getTablaCursadas().getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(vista, "Seleccione una cursada de la tabla para registrar el final.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Alumno alumno = modelo.getAlumno(dniAlumnoActualCursadas);
        if (alumno == null) return;

        // 2. Recuperar el nombre de la materia de la fila seleccionada
        String nombreMateria = vista.getModeloTablaCursadas().getValueAt(filaSeleccionada, 0).toString();
        Cursada cursadaSeleccionada = null;

        for (Cursada c:alumno.getCursadas()){
            if (c.getMateria().getNombre().equals(nombreMateria)){
                cursadaSeleccionada = c;
                break;
            }
        }

        if (cursadaSeleccionada != null) {

            if (cursadaSeleccionada.estaAprobada() || cursadaSeleccionada.getEstado() instanceof Inscripto) {
                JOptionPane.showMessageDialog(vista, "La materia " + nombreMateria + " ya se encuentra APROBADA o no esta REGULARIZADA.\nNo se pueden registrar nuevos finales.", "Acción Denegada", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String notaStr = JOptionPane.showInputDialog(vista, "Ingrese la nota del Final (1-10):", "Registrar Final", JOptionPane.QUESTION_MESSAGE);
            if (notaStr == null || notaStr.trim().isEmpty()) return; // Canceló o dejó vacío

            try {
                int nota = Integer.parseInt(notaStr);
                if (nota < 1 || nota > 10) {
                    JOptionPane.showMessageDialog(vista, "La nota debe estar entre 1 y 10.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                boolean aprueba = (nota >= 6);

                // Ejecuta la evaluación
                if (aprueba){
                    cursadaSeleccionada.rendirFinal();
                    JOptionPane.showMessageDialog(vista, "Examen Final registrado con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    refrescarDatosYAptasCursadas(alumno); // Refresca la tabla en caliente
                }else {
                    JOptionPane.showMessageDialog(vista, "Examen Final Desaprobado.", "Nota Insuficiente", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(vista, "Ingrese un número entero válido.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void eliminarAlumno(){
        int filaEliminar = vista.getTablaAlumnos().getSelectedRow();
        if (filaEliminar == -1) {
            JOptionPane.showMessageDialog(vista, "Seleccione qué alumno desea dar de baja.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String dniEliminar = vista.getModeloTabla().getValueAt(filaEliminar, 0).toString();
        int seguro = JOptionPane.showConfirmDialog(vista, "¿Está seguro de dar de baja al DNI " + dniEliminar + "?", "Confirmar baja", JOptionPane.YES_NO_OPTION);

        if (seguro == JOptionPane.YES_OPTION) {
            modelo.eliminarAlumnoDelSistema(dniEliminar); // Usa tu método real
            refrescarTablaPaginada();
        }
    }

    private void inscribirACarrera() {
        int filaInsc = vista.getTablaAlumnos().getSelectedRow();

        // 1. Validación básica de selección
        if (filaInsc == -1) {
            JOptionPane.showMessageDialog(vista, "Seleccione un alumno para gestionar su carrera.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. Recuperamos las credenciales de la fila de la tabla
        String dniInsc = vista.getModeloTabla().getValueAt(filaInsc, 0).toString();
        String nombreInsc = vista.getModeloTabla().getValueAt(filaInsc, 2).toString();

        // 3. Vamos a buscar al Alumno real al modelo
        Alumno alumno = modelo.getAlumno(dniInsc);
        if (alumno == null) return;

        // CASO A: EL ALUMNO YA TIENE CARRERA -> PROCESAMOS LA BAJA
        if (alumno.getCarreraActual() != null) {
            int seguro = JOptionPane.showConfirmDialog(
                    vista,
                    "¿Está seguro de dar de baja al alumno " + nombreInsc + " de la carrera " + alumno.getCarreraActual().getNombre() + "?\nSe perderán todas sus cursadas activas de forma permanente.",
                    "Confirmar Baja de Carrera",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (seguro == JOptionPane.YES_OPTION) {
                alumno.setCarreraActual(null);
                alumno.getCursadas().clear(); // Vaciamos la lista para no dejar basura en memoria

                JOptionPane.showMessageDialog(vista, "Baja de carrera procesada con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);

                // Refrescamos la UI (esto vuelve a dibujar la tabla y actualiza el botón)
                refrescarTablaPaginada();
            }
            return; // Cortamos el flujo acá para que no salte al modo inscripción
        }

        // CASO B: EL ALUMNO NO TIENE CARRERA
        esAlta = false;
        esInscripcion = true;

        ArrayList<String> carrerasDisponibles = new ArrayList<>();
        ArrayList<Carrera> carreras = new ArrayList<>(modelo.getMapaCarreras().values());
        for (Carrera c : carreras) {
            carrerasDisponibles.add(c.getNombre());
        }

        // Mostramos el panel/formulario de inscripción pasándole los datos necesarios
        vista.mostrarModoInscripcionCarrera(dniInsc, nombreInsc, carrerasDisponibles);
    }

    private void verificarEgreso(){
        int filaEgreso = vista.getTablaAlumnos().getSelectedRow();
        if (filaEgreso == -1) {
            JOptionPane.showMessageDialog(vista, "Seleccione un alumno para verificar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String dniEgreso = vista.getModeloTabla().getValueAt(filaEgreso, 0).toString();
        Alumno alumnoEgreso = modelo.getAlumno(dniEgreso);

        if (alumnoEgreso != null) {
            boolean estaGraduado = false;

            // CONTROL DE EGRESO SEGURO
            // Si el alumno no tiene carrera asignada en el modelo, es imposible que esté graduado
            if (alumnoEgreso.getCarreraActual() != null) {
                // Le pedimos al modelo el plan global
                // O evaluamos dinámicamente según la carrera que tenga
                PlanDeEstudio planDelAlumno = alumnoEgreso.getCarreraActual().getPlanDeEstudio();

                if (planDelAlumno.estaGraduado(alumnoEgreso)){
                    estaGraduado = true;
                }
            }

            String msj = "🎓 Control de Egreso — UNTDF\n\n";
            msj += "Alumno: " + alumnoEgreso.getNombre() + "\n";
            msj += "Carrera: " + (alumnoEgreso.getCarreraActual() != null ? alumnoEgreso.getCarreraActual().getNombre() : "No inscripto") + "\n";
            msj += "¿Cumple condiciones de graduación?: " + (estaGraduado ? "SÍ 🎉 ¡Graduado!" : "NO 📚 (Cursada incompleta)");

            JOptionPane.showMessageDialog(vista, msj, "Resultado de Auditoría", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void editarSeleccionado(){
        int filaEdicion = vista.getTablaAlumnos().getSelectedRow();
        if (filaEdicion == -1) {
            JOptionPane.showMessageDialog(vista, "Seleccione un alumno de la tabla para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String dniEdit = vista.getModeloTabla().getValueAt(filaEdicion, 0).toString();
        String legajoEdit = vista.getModeloTabla().getValueAt(filaEdicion, 1).toString();
        String nombreEdit = vista.getModeloTabla().getValueAt(filaEdicion, 2).toString();

        esAlta = false;
        esInscripcion = false;
        vista.mostrarModoEdicion(dniEdit, legajoEdit, nombreEdit);
    }

    private void ejecutarGuardado() {
        // Si estamos inscribiendo a una carrera, la lógica es totalmente distinta
        if (esInscripcion) {
            String dni = vista.getTxtDni();

            Object itemSeleccionado = vista.getComboCarreras().getSelectedItem();

            if (itemSeleccionado == null) {
                JOptionPane.showMessageDialog(vista, "Seleccione una carrera válida de la lista.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String carreraSeleccionada = itemSeleccionado.toString();


            // Ejecuta tu lógica real del backend
            modelo.inscribirAlumnoACarrera(dni, carreraSeleccionada);
            JOptionPane.showMessageDialog(vista, "¡Inscripción registrada con éxito en " + carreraSeleccionada + "!", "Éxito", JOptionPane.INFORMATION_MESSAGE);

            esInscripcion = false; // Reseteamos la bandera
            refrescarTablaPaginada();
            vista.mostrarModoLista();
            return; // Cortamos el flujo acá
        }

        // --- FLUJO NORMAL: ALTA Y EDICIÓN ---
        String dni = vista.getTxtDni();
        String legajoStr = vista.getTxtLegajo();
        String nombre = vista.getTxtNombre();

        // 1. Validar campos vacíos
        if (dni.isEmpty() || legajoStr.isEmpty() || nombre.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Todos los campos son obligatorios.", "Campos Vacíos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. Validar que el legajo sea numérico
        int legajo;
        try {
            legajo = Integer.parseInt(legajoStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(vista, "El Legajo debe ser un número entero válido.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
            return;
        }

        //  VALIDACIÓN 2.1: Longitud y coherencia del Legajo (Mínimo 4 dígitos)
        if (legajoStr.length() < 4 || legajo <= 0) {
            JOptionPane.showMessageDialog(vista,
                    "El número de Legajo no es válido.\nDebe tener al menos 4 dígitos (Ej: 4001).",
                    "Error en Legajo",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        //  VALIDACIÓN 2.2: Formato y longitud del DNI (Solo números, entre 7 y 8 dígitos)

        if (!dni.matches("^[0-9]{7,8}$")) {
            JOptionPane.showMessageDialog(vista,
                    "El DNI debe contener solo números y tener entre 7 y 8 dígitos (sin puntos ni espacios).",
                    "Error en DNI",
                    JOptionPane.ERROR_MESSAGE);
            return; //  Frena el guardado
        }

        //  VALIDACIÓN 2.5: Que el nombre contenga SOLO letras y espacios
        // Usamos una expresión regular: ^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$
        // Esto significa: desde el inicio (^) hasta el final ($), solo letras con tildes, eñes y espacios.
        if (!nombre.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$")) {
            JOptionPane.showMessageDialog(vista,
                    "El nombre completo solo puede contener letras y espacios.\nNo se permiten números ni caracteres especiales.",
                    "Error en Nombre",
                    JOptionPane.ERROR_MESSAGE);
            return; //  Frena el guardado
        }

        // 3. Controles de duplicados (solo en Altas)
        if (esAlta) {
            if (modelo.getAlumno(dni) != null) {
                JOptionPane.showMessageDialog(vista, "El DNI " + dni + " ya existe en el sistema.", "Documento Duplicado", JOptionPane.ERROR_MESSAGE);
                return;
            }
            for (Alumno existente : modelo.getAlumnosDelPadron().values()) {
                if (existente.getLegajo() == legajo) {
                    JOptionPane.showMessageDialog(vista, "El Legajo " + legajo + " ya pertenece a: " + existente.getNombre(), "Legajo Duplicado", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        }

        // 4. Impactar backend
        if (esAlta) {
            Alumno nuevoAlumno = new Alumno(nombre, legajo, dni);
            modelo.registrarAlumnoEnSistema(nuevoAlumno);
            JOptionPane.showMessageDialog(vista, "¡Alumno incorporado con éxito al padrón!", "Alta Exitosa", JOptionPane.INFORMATION_MESSAGE);
        } else {
            Alumno alumnoExistente = modelo.getAlumno(dni);
            if (alumnoExistente != null) {
                alumnoExistente.editarDatos(nombre, legajo); // Tu método encapsulado pro
                JOptionPane.showMessageDialog(vista, "Datos modificados correctamente.", "Edición Exitosa", JOptionPane.INFORMATION_MESSAGE);
            }
        }

        refrescarTablaPaginada();
        vista.mostrarModoLista();
    }

    private void refrescarTablaPaginada() {
        vista.getModeloTabla().setRowCount(0); // Limpia la tabla por completo

        ArrayList<Alumno> listaCompleta = new ArrayList<>(modelo.getAlumnosDelPadron().values());
        int totalAlumnos = listaCompleta.size();

        int totalPaginas = (int) Math.ceil((double) totalAlumnos / FILAS_POR_PAGINA);
        if (totalPaginas == 0) totalPaginas = 1;

        int indiceInicio = (paginaActual - 1) * FILAS_POR_PAGINA;
        int indiceFin = Math.min(indiceInicio + FILAS_POR_PAGINA, totalAlumnos);

        // Cargamos las filas correspondientes en la JTable
        for (int i = indiceInicio; i < indiceFin; i++) {
            Alumno al = listaCompleta.get(i);

            // 💡 TU IDEA: Le preguntamos al objeto Alumno si tiene carrera asignada
            String carrera = "No inscripto";

            // (Asumo que tu clase Alumno tiene algo como getCarrera() o getCarreraInscripto())
            // Si devuelve un objeto Carrera, le sacamos el nombre, sino queda "No inscripto"
            if (al.getCarreraActual() != null) {
                carrera = al.getCarreraActual().getNombre(); // O como se llame el método en tu modelo
            }

            Object[] fila = { al.getDni(), al.getLegajo(), al.getNombre(), carrera };
            vista.getModeloTabla().addRow(fila);
        }

        vista.actualizarEtiquetaPaginacion(paginaActual, totalPaginas);

        // actualizamos el boton de Inscribirse Carrera/Dar Baja Carrera ya que la lista cambio
        actualizarBotonCarreraSegunSeleccion();
    }
}