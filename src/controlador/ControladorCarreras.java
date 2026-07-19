package controlador;

import modelo.Carrera;
import modelo.ModeloSistemaAcademico;
import vista.PanelCarrerasUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class ControladorCarreras implements ActionListener {

    private PanelCarrerasUI vista;
    private ModeloSistemaAcademico modelo;

    // Bandera de estado para el botón "Guardar"
    private boolean esAlta = true;

    // VARIABLES DE ESTADO PARA GESTIONAR PLAN DE ESTUDIO
    private boolean enModoPlanDeEstudio = false;
    private boolean enModoSeleccionPlanDeEstudio = false;

    // Guardamos la carrera con la que estamos trabajando actualmente en los sub-paneles
    private Carrera carreraSeleccionada = null;

    // Variables para controlar la paginación de a 10 filas
    private int paginaActual = 1;
    private final int FILAS_POR_PAGINA = 10;

    public ControladorCarreras(PanelCarrerasUI vista, ModeloSistemaAcademico modelo) {
        this.vista = vista;
        this.modelo = modelo;

        // Enlazamos este controlador para que escuche los botones de la vista
        this.vista.escucharComponentes(this);

        // Cargamos la tabla por primera vez al arrancar
        this.actualizarTabla();
    }

    // --- LA CENTRAL DE EVENTOS ---
    @Override
    public void actionPerformed(ActionEvent e) {
        String comando = e.getActionCommand();

        try {
            switch (comando) {
                case "Agregar Carrera":
                    this.abrirFormularioAlta();
                    break;
                case "Guardar":
                    this.guardarCarrera();
                    break;
                case "Cancelar":
                    this.cancelarFormulario();
                    break;
                case "Eliminar Carrera":
                    this.eliminarCarrera();
                    break;
                case "Editar Carrera":
                    this.editarCarrera();
                    break;
                case "Plan de Estudio":
                    this.abrirGestionPlanDeEstudio();
                    break;
                case "Agregar Plan de Estudio":
                    this.abrirSeleccionPlanDeEstudio();
                    break;
                case "Confirmar Selección":
                    this.confirmarSeleccionPlanDeEstudio();
                    break;
                case "Eliminar Plan de Estudio":
                    this.eliminarPlanDeEstudio();
                    break;
                case "Volver":
                    this.procesarVolver();
                    break;
                case "< Anterior":
                    this.paginaAnterior();
                    break;
                case "Siguiente >":
                    this.paginaSiguiente();
                    break;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(vista, "Ocurrió un error inesperado: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // --- MÉTODOS OPERATIVOS ---

    private void abrirFormularioAlta() {
        esAlta = true;
        vista.mostrarModoAlta();
    }

    private void cancelarFormulario() {
        esAlta = true;
        vista.mostrarModoLista();
    }

    private void guardarCarrera() {
        // 1. EXTRACCIÓN DE DATOS DESDE LA VISTA
        String txtCodigo = vista.getTxtCodigo();
        String nombre = vista.getTxtNombre();

        // 2. VALIDACIONES GENERALES
        if (txtCodigo.isEmpty() || nombre.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Todos los campos son obligatorios.", "Error de Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int codigo;
        try {
            codigo = Integer.parseInt(txtCodigo);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(vista, "El código de la carrera debe ser un número entero válido.", "Error de Tipo", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (codigo <= 0) {
            JOptionPane.showMessageDialog(vista, "El código debe ser un número mayor a cero.", "Error de Rango", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 3. BIFURCACIÓN DE LÓGICA DE NEGOCIO
        if (esAlta) {
            // CAMINO A: REGISTRAR CARRERA NUEVA
            if (existeCodigoCarrera(codigo)) {
                JOptionPane.showMessageDialog(vista, "Ya existe una carrera registrada con el código " + codigo, "Carrera Duplicada", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Usamos el nombre como clave del HashMap (asumiendo estructura de tu modelo)
            if (modelo.getMapaCarreras().containsKey(nombre)) {
                JOptionPane.showMessageDialog(vista, "Ya existe una carrera registrada con el nombre '" + nombre + "'", "Carrera Duplicada", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Guardamos en el modelo
            modelo.addCarrera(codigo, nombre);
            JOptionPane.showMessageDialog(vista, "Carrera guardada con éxito.", "Operación Exitosa", JOptionPane.INFORMATION_MESSAGE);

        } else {
            // CAMINO B: MODIFICAR CARRERA EXISTENTE (El código NO se edita)
            Carrera carreraAEditar = null;
            for (Carrera c : modelo.getMapaCarreras().values()) {
                if (c.getCodigoCarrera() == codigo) {
                    carreraAEditar = c;
                    break;
                }
            }

            if (carreraAEditar != null) {
                String nombreViejo = carreraAEditar.getNombre();

                if (!nombreViejo.equals(nombre)) {
                    if (modelo.getMapaCarreras().containsKey(nombre)) {
                        JOptionPane.showMessageDialog(vista, "No se puede renombrar. Ya existe otra carrera llamada '" + nombre + "'", "Nombre Duplicado", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    // Actualizamos clave del mapa en el modelo
                    modelo.getMapaCarreras().remove(nombreViejo);
                }

                carreraAEditar.setNombre(nombre);
                modelo.getMapaCarreras().put(nombre, carreraAEditar);

                JOptionPane.showMessageDialog(vista, "Carrera modificada con éxito.", "Operación Exitosa", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(vista, "Error crítico: No se encontró la carrera original.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        // 4. LIMPIEZA Y REFRESCO
        esAlta = true;
        vista.mostrarModoLista();
        this.actualizarTabla();
    }

    private void editarCarrera() {
        int filaEdicion = vista.getTablaCarreras().getSelectedRow();
        if (filaEdicion == -1) {
            JOptionPane.showMessageDialog(vista, "Seleccione una carrera de la tabla para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Según tus columnas: 0 = Nombre, 1 = Código
        String nombreEdit = vista.getModeloTabla().getValueAt(filaEdicion, 0).toString();
        String codigoEdit = vista.getModeloTabla().getValueAt(filaEdicion, 1).toString();

        esAlta = false;
        vista.mostrarModoEdicion(codigoEdit, nombreEdit);
    }

    private void eliminarCarrera() {
        int filaSeleccionada = vista.getTablaCarreras().getSelectedRow();

        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(vista, "Seleccione la carrera que desea remover de la lista.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Sacamos los datos de la fila seleccionada de la JTable
        String nombreCarrera = (String) vista.getTablaCarreras().getValueAt(filaSeleccionada, 0);

        int confirmacion = JOptionPane.showConfirmDialog(
                vista,
                "¿Está seguro de quitar la carrera '" + nombreCarrera + "'?",
                "Confirmar Borrado",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirmacion == JOptionPane.YES_OPTION) {
            // Remueve la carrera del modelo (usando la clave que es el nombre)
            if (modelo.getMapaCarreras().containsKey(nombreCarrera)) {
                modelo.getMapaCarreras().remove(nombreCarrera);
                JOptionPane.showMessageDialog(vista, "Carrera eliminada exitosamente.", "Baja Exitosa", JOptionPane.INFORMATION_MESSAGE);
                this.actualizarTabla();
            } else {
                JOptionPane.showMessageDialog(vista, "Error al intentar eliminar la carrera.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void abrirGestionPlanDeEstudio() {
        int filaSeleccionada = vista.getTablaCarreras().getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(vista, "Por favor, seleccione una carrera para gestionar su Plan de Estudio.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String nombreCarrera = vista.getModeloTabla().getValueAt(filaSeleccionada, 0).toString();
        carreraSeleccionada = modelo.getMapaCarreras().get(nombreCarrera);

        if (carreraSeleccionada != null) {
            enModoPlanDeEstudio = true;

            // Verificamos si tiene un plan o no para mostrarlo en el título
            String nombrePlan = (carreraSeleccionada.getPlanDeEstudio() != null)
                    ? carreraSeleccionada.getPlanDeEstudio().getNombre()
                    : "Ninguno asignado";

            vista.mostrarModoGestionPlanDeEstudio(carreraSeleccionada.getNombre(), nombrePlan);
            this.actualizarTabla();
        }
    }

    private void abrirSeleccionPlanDeEstudio() {
        if (carreraSeleccionada == null) return;

        //  LA BARRERA: Si la carrera ya tiene un plan, no lo dejamos avanzar
        if (carreraSeleccionada.getPlanDeEstudio() != null) {
            JOptionPane.showMessageDialog(
                    vista,
                    "La carrera ya tiene un plan de estudio asignado.\nDebe eliminar el plan actual antes de poder asignar uno nuevo.",
                    "Acción no permitida",
                    JOptionPane.WARNING_MESSAGE
            );
            return; // Corta el flujo acá y no abre la pantalla de selección
        }

        // Si no tiene plan, el flujo sigue normal:
        enModoSeleccionPlanDeEstudio = true;
        vista.mostrarModoSeleccionPlanDeEstudio(carreraSeleccionada.getNombre());
        this.actualizarTabla();
    }

    private void confirmarSeleccionPlanDeEstudio() {
        int filaSeleccionada = vista.getTablaCarreras().getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(vista, "Por favor, seleccione un Plan de Estudio de la lista.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // 1. Conseguimos el código del plan (columna 1) y lo parseamos a Integer
            String codigoStr = vista.getModeloTabla().getValueAt(filaSeleccionada, 1).toString();
            Integer codigoPlanElegido = Integer.parseInt(codigoStr);

            // 2. Buscamos el plan real en el modelo usando la clave correcta (Integer)
            var planElegido = modelo.getMapaPlanes().get(codigoPlanElegido);

            if (planElegido != null && carreraSeleccionada != null) {
                // Asignamos el plan a la carrera seleccionada
                carreraSeleccionada.setPlanDeEstudio(planElegido);

                JOptionPane.showMessageDialog(vista, "¡Plan asignado exitosamente!", "Éxito", JOptionPane.INFORMATION_MESSAGE);

                enModoSeleccionPlanDeEstudio = false;
                vista.mostrarModoGestionPlanDeEstudio(carreraSeleccionada.getNombre(), planElegido.getNombre());
                this.actualizarTabla();
            } else {
                JOptionPane.showMessageDialog(vista, "Error: No se encontró el plan de estudio en el sistema.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(vista, "Error al procesar el código del plan de estudio.", "Error de Datos", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarPlanDeEstudio() {
        if (carreraSeleccionada == null) return;

        if (carreraSeleccionada.getPlanDeEstudio() == null) {
            JOptionPane.showMessageDialog(vista, "La carrera no tiene ningún plan asignado actualmente.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(
                vista,
                "¿Seguro que desea desvincular el plan de estudio de esta carrera?",
                "Confirmar Acción",
                JOptionPane.YES_NO_OPTION
        );

        if (confirmacion == JOptionPane.YES_OPTION) {
            carreraSeleccionada.setPlanDeEstudio(null);
            JOptionPane.showMessageDialog(vista, "Plan de estudio desvinculado con éxito.", "Operación Exitosa", JOptionPane.INFORMATION_MESSAGE);

            vista.mostrarModoGestionPlanDeEstudio(carreraSeleccionada.getNombre(), "Ninguno asignado");
            this.actualizarTabla();
        }
    }

    private void procesarVolver() {
        if (enModoSeleccionPlanDeEstudio) {
            enModoSeleccionPlanDeEstudio = false;
            String nombrePlan = (carreraSeleccionada.getPlanDeEstudio() != null)
                    ? carreraSeleccionada.getPlanDeEstudio().getNombre()
                    : "Ninguno asignado";
            vista.mostrarModoGestionPlanDeEstudio(carreraSeleccionada.getNombre(), nombrePlan);
            this.actualizarTabla();
            return;
        }

        if (enModoPlanDeEstudio) {
            enModoPlanDeEstudio = false;
            carreraSeleccionada = null;
            vista.mostrarModoLista();
            this.actualizarTabla();
        }
    }

    // --- AUXILIARES Y PAGINACIÓN ---

    private boolean existeCodigoCarrera(int codigoBuscado) {
        for (Carrera c : modelo.getMapaCarreras().values()) {
            if (c.getCodigoCarrera() == codigoBuscado) {
                return true;
            }
        }
        return false;
    }

    private void actualizarTabla() {
        DefaultTableModel dtm = vista.getModeloTabla();
        dtm.setRowCount(0);

        // =====================================================================
        // CASO A: MODO SELECCIÓN DE PLAN (Mostramos todos los planes del sistema)
        // =====================================================================
        if (enModoPlanDeEstudio && enModoSeleccionPlanDeEstudio && carreraSeleccionada != null) {
            dtm.setColumnIdentifiers(new String[]{"Nombre del Plan", "Código de Plan", "Cantidad Materias", ""});

            // Listamos los planes del sistema
            for (var plan : modelo.getMapaPlanes().values()) {
                Object[] fila = {
                        plan.getNombre(),
                        plan.getCodigo(),
                        plan.getTodasLasMaterias().size(),
                        "" // Relleno por la cuarta columna de la vista
                };
                dtm.addRow(fila);
            }
            vista.getLblPaginacion().setText("Seleccione un plan de la lista");
            vista.getBtnAnterior().setEnabled(false);
            vista.getBtnSiguiente().setEnabled(false);
            return;
        }

        // =====================================================================
        // CASO B: MODO GESTIÓN (Muestra el plan que actualmente tiene la carrera)
        // =====================================================================
        if (enModoPlanDeEstudio && carreraSeleccionada != null) {
            dtm.setColumnIdentifiers(new String[]{"Nombre del Plan", "Código de Plan", "Cantidad Materias", ""});

            var plan = carreraSeleccionada.getPlanDeEstudio();
            if (plan != null) {
                Object[] fila = {
                        plan.getNombre(),
                        plan.getCodigo(),
                        plan.getTodasLasMaterias().size(),
                        ""
                };
                dtm.addRow(fila);
            }

            vista.getLblPaginacion().setText("Plan actual asignado");
            vista.getBtnAnterior().setEnabled(false);
            vista.getBtnSiguiente().setEnabled(false);
            return;
        }

        // =====================================================================
        // CASO C: MODO NORMAL (LISTADO DE CARRERAS)
        // =====================================================================
        dtm.setColumnIdentifiers(new String[]{"Nombre de Carrera", "Codigo de Carrera", "Plan de Estudio", "Numero de Inscriptos"});

        List<Carrera> listaCarreras = new ArrayList<>(modelo.getMapaCarreras().values());

        // Las ordenamos por código
        listaCarreras.sort((c1, c2) -> Integer.compare(c1.getCodigoCarrera(), c2.getCodigoCarrera()));

        int totalRegistros = listaCarreras.size();
        int paginasMaximas = (int) Math.ceil((double) totalRegistros / FILAS_POR_PAGINA);
        if (paginasMaximas == 0) paginasMaximas = 1;

        if (paginaActual > paginasMaximas) {
            paginaActual = paginasMaximas;
        }

        int indiceInicio = (paginaActual - 1) * FILAS_POR_PAGINA;
        int indiceFin = Math.min(indiceInicio + FILAS_POR_PAGINA, totalRegistros);

        for (int i = indiceInicio; i < indiceFin; i++) {
            Carrera c = listaCarreras.get(i);
            String nombrePlan = (c.getPlanDeEstudio() != null) ? c.getPlanDeEstudio().getNombre() : "Ninguno";

            // Suponemos que tenés un getter para el número de inscritos en Carrera, si no, poné 0 o llamá al método correspondiente.
            int inscriptos = 0;
            try {
                inscriptos = c.getMapaAlumnos().size();
            } catch(Exception ignored) {}

            Object[] fila = {
                    c.getNombre(),
                    c.getCodigoCarrera(),
                    nombrePlan,
                    inscriptos
            };
            dtm.addRow(fila);
        }

        vista.getLblPaginacion().setText("Página " + paginaActual + " de " + paginasMaximas);
        vista.getBtnAnterior().setEnabled(paginaActual > 1);
        vista.getBtnSiguiente().setEnabled(paginaActual < paginasMaximas);
    }

    private void paginaAnterior() {
        if (paginaActual > 1) {
            paginaActual--;
            this.actualizarTabla();
        }
    }

    private void paginaSiguiente() {
        int totalRegistros = modelo.getMapaCarreras().size();
        int paginasMaximas = (int) Math.ceil((double) totalRegistros / FILAS_POR_PAGINA);

        if (paginaActual < paginasMaximas) {
            paginaActual++;
            this.actualizarTabla();
        }
    }
}