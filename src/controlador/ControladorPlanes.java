package controlador;

import modelo.EstrategiaDeInscripcion;
import modelo.CondicionA;
import modelo.CondicionB;
import modelo.CondicionC;
import modelo.CondicionD;
import modelo.CondicionE;
import modelo.ModeloSistemaAcademico;
import modelo.PlanDeEstudio;
import modelo.Materia;
import vista.PanelPlanesUI;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class ControladorPlanes implements ActionListener, ListSelectionListener {

    private PanelPlanesUI vista;
    private ModeloSistemaAcademico modelo;

    // Flag para evitar bucles infinitos al actualizar las listas dinámicamente
    private boolean actualizandoListasMutuas = false;

    // 🌟 NUEVO: Flag para saber si el formulario está en modo "Crear" o "Editar"
    private boolean editando = false;

    public ControladorPlanes(PanelPlanesUI vista, ModeloSistemaAcademico modelo) {
        this.vista = vista;
        this.modelo = modelo;

        // Registrar listeners de la vista (incluye el botón Editar Plan)
        this.vista.escucharComponentes(this);

        // Listener de la tabla de planes (Lado izquierdo)
        this.vista.getTablaPlanes().getSelectionModel().addListSelectionListener(this);

        // Listeners para el filtrado dinámico de las listas de materias
        configurarListenersDeMaterias();

        inicializarVista();
    }

    private void inicializarVista() {
        actualizarTablaPlanes();
        cargarEstrategiasInscripcion();
        cargarListadoMateriasParaSeleccion();
        vista.mostrarModoDetalleVacio();
    }

    private void configurarListenersDeMaterias() {
        vista.getListaMateriasObligatorias().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                filtrarListasMutuamente();
            }
        });

        vista.getListaMateriasOptativas().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                filtrarListasMutuamente();
            }
        });
    }

    // Filtra las materias para que no se puedan duplicar en ambas listas
    private void filtrarListasMutuamente() {
        if (actualizandoListasMutuas) return;
        actualizandoListasMutuas = true;

        List<String> obligatoriasSeleccionadas = vista.getListaMateriasObligatorias().getSelectedValuesList();
        List<String> optativasSeleccionadas = vista.getListaMateriasOptativas().getSelectedValuesList();

        // Reconstruir obligatorias (excluyendo lo seleccionado en optativas)
        vista.getModeloListaOblig().clear();
        List<Materia> todas = new ArrayList<>(modelo.getMapaMaterias().values());
        for (Materia m : todas) {
            String item = m.getCodigoMateria() + " - " + m.getNombre();
            if (!optativasSeleccionadas.contains(item)) {
                vista.getModeloListaOblig().addElement(item);
            }
        }

        // Reconstruir optativas (excluyendo lo seleccionado en obligatorias)
        vista.getModeloListaOpta().clear();
        for (Materia m : todas) {
            String item = m.getCodigoMateria() + " - " + m.getNombre();
            if (!obligatoriasSeleccionadas.contains(item)) {
                vista.getModeloListaOpta().addElement(item);
            }
        }

        // Restaurar las selecciones visuales
        restaurarSeleccion(vista.getListaMateriasObligatorias(), vista.getModeloListaOblig(), obligatoriasSeleccionadas);
        restaurarSeleccion(vista.getListaMateriasOptativas(), vista.getModeloListaOpta(), optativasSeleccionadas);

        actualizandoListasMutuas = false;
    }

    private void restaurarSeleccion(JList<String> jList, DefaultListModel<String> modeloLista, List<String> seleccionadasPrevias) {
        List<Integer> indicesARestablecer = new ArrayList<>();
        for (String item : seleccionadasPrevias) {
            int index = modeloLista.indexOf(item);
            if (index != -1) {
                indicesARestablecer.add(index);
            }
        }
        int[] indicesArray = indicesARestablecer.stream().mapToInt(i -> i).toArray();
        jList.setSelectedIndices(indicesArray);
    }

    private void actualizarTablaPlanes() {
        vista.getModeloTabla().setRowCount(0);
        List<PlanDeEstudio> planes = new ArrayList<>(modelo.getMapaPlanes().values());
        for (PlanDeEstudio p : planes) {
            Object[] fila = {
                    p.getCodigo(),
                    p.getNombre(),
                    p.getMinObligatorias(),
                    p.getMinOptativas()
            };
            vista.getModeloTabla().addRow(fila);
        }
    }

    private void cargarEstrategiasInscripcion() {
        vista.getComboEstrategia().removeAllItems();
        vista.getComboEstrategia().addItem("Condición A (Cursadas de Correlativas)");
        vista.getComboEstrategia().addItem("Condición B (Finales de Correlativas)");
        vista.getComboEstrategia().addItem("Condición C (Correlativas + Finales 5 Cuatris Previos)");
        vista.getComboEstrategia().addItem("Condición D (Correlativas + Finales 3 Cuatris Previos)");
        vista.getComboEstrategia().addItem("Condición E (Finales Correlativas + Finales 3 Cuatris Previos)");
    }

    private void cargarListadoMateriasParaSeleccion() {
        actualizandoListasMutuas = true;
        vista.getModeloListaOblig().clear();
        vista.getModeloListaOpta().clear();

        List<Materia> materiasDisponibles = new ArrayList<>(modelo.getMapaMaterias().values());
        for (Materia m : materiasDisponibles) {
            String item = m.getCodigoMateria() + " - " + m.getNombre();
            vista.getModeloListaOblig().addElement(item);
            vista.getModeloListaOpta().addElement(item);
        }
        actualizandoListasMutuas = false;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String comando = e.getActionCommand();

        switch (comando) {
            case "Nuevo Plan":
                editando = false; // Indicamos modo creación
                cargarListadoMateriasParaSeleccion();
                vista.mostrarModoFormulario();
                break;

            case "Editar Plan":
                prepararEdicionDePlan();
                break;

            case "Cancelar":
                editando = false;
                vista.mostrarModoDetalleVacio();
                break;

            case "Guardar":
                guardarPlan();
                break;

            case "Eliminar Seleccionado":
                eliminarPlanSeleccionado();
                break;
        }
    }

    // 🌟 NUEVO: Se encarga de buscar el plan seleccionado y pintar el formulario
    private void prepararEdicionDePlan() {
        int filaSel = vista.getTablaPlanes().getSelectedRow();
        if (filaSel == -1) {
            JOptionPane.showMessageDialog(vista, "Seleccione un plan de la tabla para editar.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int codigoPlan = (int) vista.getTablaPlanes().getValueAt(filaSel, 0);
        PlanDeEstudio plan = modelo.getPlan(codigoPlan);

        if (plan != null) {
            editando = true; // Indicamos modo edición activo!

            // 1. Mostramos el formulario bloqueando el código
            vista.mostrarModoEdicion(
                    String.valueOf(plan.getCodigo()),
                    plan.getNombre(),
                    String.valueOf(plan.getMinObligatorias()),
                    String.valueOf(plan.getMinOptativas())
            );

            // 2. Seteamos la estrategia actual en el combo box
            seleccionarEstrategiaEnCombo(plan.getCondicion().getEstrategia());

            // 3. Cargamos y pre-seleccionamos las materias correspondientes
            cargarYPreseleccionarMateriasDelPlan(plan);
        }
    }

    // Auxiliar para pre-seleccionar la estrategia de inscripción actual en el JComboBox
    private void seleccionarEstrategiaEnCombo(EstrategiaDeInscripcion est) {
        if (est == null) return;
        String clase = est.getClass().getSimpleName();
        for (int i = 0; i < vista.getComboEstrategia().getItemCount(); i++) {
            String item = vista.getComboEstrategia().getItemAt(i);
            if (clase.equals("Condición A") && item.contains("Condición A")) {
                vista.getComboEstrategia().setSelectedIndex(i);
                break;
            } else if (clase.equals("Condición B") && item.contains("Condición B")) {
                vista.getComboEstrategia().setSelectedIndex(i);
                break;
            } else if (clase.equals("Condición C") && item.contains("Condición C")) {
                vista.getComboEstrategia().setSelectedIndex(i);
                break;
            } else if (clase.equals("Condición D") && item.contains("Condición D")) {
                vista.getComboEstrategia().setSelectedIndex(i);
                break;
            } else if (clase.equals("Condición E") && item.contains("Condición E")) {
                vista.getComboEstrategia().setSelectedIndex(i);
                break;
            }
        }
    }

    // 🌟 LA MAGIA DE EDICIÓN: Prepara las JList con lo que ya está grabado en el plan
    private void cargarYPreseleccionarMateriasDelPlan(PlanDeEstudio plan) {
        actualizandoListasMutuas = true;

        // Limpiamos los modelos
        vista.getModeloListaOblig().clear();
        vista.getModeloListaOpta().clear();

        List<Materia> todasLasMaterias = new ArrayList<>(modelo.getMapaMaterias().values());

        // Creamos listas con los textos para mapear fácil
        List<String> obligatoriasTextos = new ArrayList<>();
        for (Materia m : plan.getObligatorias()) {
            obligatoriasTextos.add(m.getCodigoMateria() + " - " + m.getNombre());
        }

        List<String> optativasTextos = new ArrayList<>();
        for (Materia m : plan.getOptativas()) {
            optativasTextos.add(m.getCodigoMateria() + " - " + m.getNombre());
        }

        // Llenamos la lista de OBLIGATORIAS (excluyendo las que son optativas en este plan)
        for (Materia m : todasLasMaterias) {
            String item = m.getCodigoMateria() + " - " + m.getNombre();
            if (!optativasTextos.contains(item)) {
                vista.getModeloListaOblig().addElement(item);
            }
        }

        // Llenamos la lista de OPTATIVAS (excluyendo las que son obligatorias en este plan)
        for (Materia m : todasLasMaterias) {
            String item = m.getCodigoMateria() + " - " + m.getNombre();
            if (!obligatoriasTextos.contains(item)) {
                vista.getModeloListaOpta().addElement(item);
            }
        }

        actualizandoListasMutuas = false;

        // Ahora pintamos (seleccionamos) de azul las materias correspondientes
        restaurarSeleccion(vista.getListaMateriasObligatorias(), vista.getModeloListaOblig(), obligatoriasTextos);
        restaurarSeleccion(vista.getListaMateriasOptativas(), vista.getModeloListaOpta(), optativasTextos);
    }

    // 🌟 MODIFICADO: Ahora decide inteligentemente si llama a Crear o Modificar
    private void guardarPlan() {
        String codigoStr = vista.getTxtCodigo();
        String nombre = vista.getTxtNombre();
        String minObligStr = vista.getTxtMinOblig();
        String minOptaStr = vista.getTxtMinOpta();

        if (codigoStr.isEmpty() || nombre.isEmpty() || minObligStr.isEmpty() || minOptaStr.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Por favor, complete todos los campos.", "Error de Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int codigo = Integer.parseInt(codigoStr);
            int minOblig = Integer.parseInt(minObligStr);
            int minOpta = Integer.parseInt(minOptaStr);

            if (minOblig == 0 && minOpta == 0) {
                JOptionPane.showMessageDialog(vista, "El plan no puede requerir 0 de ambas materias.", "Error Lógico", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Si es un plan NUEVO, validamos que el código no esté repetido
            if (!editando && modelo.getPlan(codigo) != null) {
                JOptionPane.showMessageDialog(vista, "Ya existe un Plan con el código: " + codigo, "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            ArrayList<Integer> obligatoriasIDs = obtenerIDsDesdeSeleccion(vista.getListaMateriasObligatorias());
            ArrayList<Integer> optativasIDs = obtenerIDsDesdeSeleccion(vista.getListaMateriasOptativas());

            String seleccionCombo = (String) vista.getComboEstrategia().getSelectedItem();
            EstrategiaDeInscripcion estrategiaInterna = null;

            switch (seleccionCombo) {
                case "Condición A (Cursadas de Correlativas)":
                    estrategiaInterna = new CondicionA();
                    break;
                case "Condición B (Finales de Correlativas)":
                    estrategiaInterna = new CondicionB();
                    break;
                case "Condición C (Correlativas + Finales 5 Cuatris Previos)":
                    estrategiaInterna = new CondicionC();
                    break;
                case "Condición D (Correlativas + Finales 3 Cuatris Previos)":
                    estrategiaInterna = new CondicionD();
                    break;
                case "Condición E (Finales Correlativas + Finales 3 Cuatris Previos)":
                    estrategiaInterna = new CondicionE();
                    break;
            }

            boolean exito;
            if (editando) {
                // 🌟 MODO EDICIÓN:

                exito = modelo.editarPlan(codigo, nombre, minOblig, minOpta, estrategiaInterna, obligatoriasIDs, optativasIDs);
            } else {
                // MODO CREACIÓN:
                exito = modelo.crearPlanCompleto(nombre, codigo, minOblig, minOpta, estrategiaInterna, obligatoriasIDs, optativasIDs);
            }

            if (exito) {
                JOptionPane.showMessageDialog(vista, "Plan de Estudio guardado con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                actualizarTablaPlanes();
                editando = false;
                vista.mostrarModoDetalleVacio();
            } else {
                JOptionPane.showMessageDialog(vista, "Error al guardar. Verifique los requisitos mínimos de materias seleccionadas.", "Error del Modelo", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(vista, "Código y Mínimos deben ser numéricos.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(vista, "Error al procesar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private ArrayList<Integer> obtenerIDsDesdeSeleccion(JList<String> listaSwing) {
        ArrayList<Integer> ids = new ArrayList<>();
        List<String> seleccionadas = listaSwing.getSelectedValuesList();

        for (String item : seleccionadas) {
            String codigoStr = item.split(" - ")[0];
            try {
                ids.add(Integer.parseInt(codigoStr));
            } catch (NumberFormatException ignored) {}
        }
        return ids;
    }

    private void eliminarPlanSeleccionado() {
        int filaSel = vista.getTablaPlanes().getSelectedRow();
        if (filaSel == -1) {
            JOptionPane.showMessageDialog(vista, "Seleccione un plan de la tabla para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int codigoPlan = (int) vista.getTablaPlanes().getValueAt(filaSel, 0);
        String nombrePlan = (String) vista.getTablaPlanes().getValueAt(filaSel, 1);

        int confirmacion = JOptionPane.showConfirmDialog(
                vista,
                "¿Está seguro de que desea eliminar el plan '" + nombrePlan + "'?\nEsta acción no se puede deshacer.",
                "Confirmar Eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                modelo.eliminarPlanDelSistema(codigoPlan);
                JOptionPane.showMessageDialog(vista, "Plan eliminado exitosamente.", "Información", JOptionPane.INFORMATION_MESSAGE);
                actualizarTablaPlanes();
                vista.mostrarModoDetalleVacio();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(vista, "No se pudo eliminar el plan: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            int filaSel = vista.getTablaPlanes().getSelectedRow();
            if (filaSel != -1) {
                int codigoPlan = (int) vista.getTablaPlanes().getValueAt(filaSel, 0);
                PlanDeEstudio plan = modelo.getPlan(codigoPlan);

                if (plan != null) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("=========================================\n");
                    sb.append(" CÓDIGO: ").append(plan.getCodigo()).append("\n");
                    sb.append(" NOMBRE: ").append(plan.getNombre()).append("\n");
                    sb.append("=========================================\n\n");

                    sb.append(">> MATERIAS OBLIGATORIAS:\n");
                    List<Materia> obligatorias = plan.getObligatorias();
                    if (obligatorias == null || obligatorias.isEmpty()) {
                        sb.append("   (Ninguna asignada)\n");
                    } else {
                        for (Materia m : obligatorias) {
                            sb.append("   • [Code: ").append(m.getCodigoMateria()).append("] ").append(m.getNombre()).append("\n");
                        }
                    }

                    sb.append("\n>> MATERIAS OPTATIVAS:\n");
                    List<Materia> optativas = plan.getOptativas();
                    if (optativas == null || optativas.isEmpty()) {
                        sb.append("   (Ninguna asignada)\n");
                    } else {
                        for (Materia m : optativas) {
                            sb.append("   • [Code: ").append(m.getCodigoMateria()).append("] ").append(m.getNombre()).append("\n");
                        }
                    }

                    sb.append("\n-----------------------------------------\n");
                    sb.append("Estructura de inscripción activa:\n");

                    if (plan.getCondicion() != null && plan.getCondicion().getEstrategia() != null) {
                        sb.append("   ").append(plan.getCondicion().getEstrategia().getClass().getSimpleName());
                    } else {
                        sb.append("   Inscripción Estándar / Sin Restricción");
                    }

                    vista.mostrarModoDetallePlan(plan.getNombre(), sb.toString());
                }
            }
        }
    }
}