package vista;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;

public class PanelPlanesUI extends JPanel {

    // Contenedor principal dividido (Izquierda / Derecha)
    private JSplitPane splitPane;

    // --- LADO IZQUIERDO: TABLA ---
    private JPanel panelIzquierdo;
    private JTable tablaPlanes;
    private DefaultTableModel modeloTabla;
    private JButton btnNuevoPlan, btnEliminarPlan;

    // --- LADO DERECHO: PANEL CARD (DINÁMICO) ---
    private JPanel panelDerechoCard;
    private CardLayout cardLayoutDerecho;

    // CARD 1: Detalle del Plan Seleccionado (O vista vacía)
    private JPanel panelDetalle;
    private JLabel lblDetalleTitulo;
    private JTextArea txtAreaMateriasDetalle; // Para listar de forma linda las materias del plan seleccionado

    // CARD 2: Formulario de Alta de Plan
    private JPanel panelFormulario;
    private JTextField txtCodigo, txtNombre, txtMinOblig, txtMinOpta;
    private JComboBox<String> comboEstrategia;

    // Listas de selección múltiple para materias del sistema
    private JList<String> listaMateriasObligatorias;
    private JList<String> listaMateriasOptativas;
    private DefaultListModel<String> modeloListaOblig;
    private DefaultListModel<String> modeloListaOpta;

    private JButton btnGuardar, btnCancelar;

    public PanelPlanesUI() {
        setLayout(new BorderLayout());
        cardLayoutDerecho = new CardLayout();
        panelDerechoCard = new JPanel(cardLayoutDerecho);

        // 1. Inicializar sub-paneles
        armarPanelIzquierdo();
        armarPanelDetalle();
        armarPanelFormulario();

        // Agregar los estados al panel derecho interactivo
        panelDerechoCard.add(panelDetalle, "DETALLE");
        panelDerechoCard.add(panelFormulario, "FORMULARIO");

        // 2. Unir ambos lados usando un JSplitPane para que sea redimensionable
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelIzquierdo, panelDerechoCard);
        splitPane.setDividerLocation(550); // Posición inicial del separador
        splitPane.setResizeWeight(0.6);   // Darle más prioridad de estiramiento al lado de la tabla

        add(splitPane, BorderLayout.CENTER);

        // Estado inicial
        mostrarModoDetalleVacio();
    }

    private void armarPanelIzquierdo() {
        panelIzquierdo = new JPanel(new BorderLayout());
        panelIzquierdo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Título de sección
        JLabel lblTitulo = new JLabel("Planes de Estudio Registrados", SwingConstants.LEFT);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        panelIzquierdo.add(lblTitulo, BorderLayout.NORTH);

        // Tabla
        String[] columnas = {"Código", "Nombre del Plan", "Mín. Obligatorias", "Mín. Optativas"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tablaPlanes = new JTable(modeloTabla);
        panelIzquierdo.add(new JScrollPane(tablaPlanes), BorderLayout.CENTER);

        // Botonera inferior izquierda
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        btnNuevoPlan = new JButton("Nuevo Plan");
        btnEliminarPlan = new JButton("Eliminar Seleccionado");

        btnNuevoPlan.setFocusPainted(false);
        btnEliminarPlan.setFocusPainted(false);

        panelBotones.add(btnNuevoPlan);
        panelBotones.add(btnEliminarPlan);
        panelIzquierdo.add(panelBotones, BorderLayout.SOUTH);
    }

    private void armarPanelDetalle() {
        panelDetalle = new JPanel(new BorderLayout());
        panelDetalle.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        lblDetalleTitulo = new JLabel("Detalle del Plan", SwingConstants.CENTER);
        lblDetalleTitulo.setFont(new Font("Arial", Font.BOLD, 14));
        panelDetalle.add(lblDetalleTitulo, BorderLayout.NORTH);

        txtAreaMateriasDetalle = new JTextArea();
        txtAreaMateriasDetalle.setEditable(false);
        txtAreaMateriasDetalle.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtAreaMateriasDetalle.setMargin(new Insets(10, 10, 10, 10));

        JScrollPane scrollDetalle = new JScrollPane(txtAreaMateriasDetalle);
        scrollDetalle.setBorder(BorderFactory.createTitledBorder("Estructura Curricular (Materias)"));
        panelDetalle.add(scrollDetalle, BorderLayout.CENTER);
    }

    private void armarPanelFormulario() {
        panelFormulario = new JPanel(new BorderLayout());
        panelFormulario.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel lblTituloForm = new JLabel("Registrar Nuevo Plan de Estudio", SwingConstants.CENTER);
        lblTituloForm.setFont(new Font("Arial", Font.BOLD, 14));
        lblTituloForm.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        panelFormulario.add(lblTituloForm, BorderLayout.NORTH);

        // Formulario (GridBagLayout para distribución precisa)
        JPanel panelCampos = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Fila 0: Código
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        panelCampos.add(new JLabel("Código (Numérico):", SwingConstants.RIGHT), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        txtCodigo = new JTextField();
        panelCampos.add(txtCodigo, gbc);

        // Fila 1: Nombre
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        panelCampos.add(new JLabel("Nombre Plan:", SwingConstants.RIGHT), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        txtNombre = new JTextField();
        panelCampos.add(txtNombre, gbc);

        // Fila 2: Mínimo Obligatorias
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.3;
        panelCampos.add(new JLabel("Mín. Obligatorias:", SwingConstants.RIGHT), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        txtMinOblig = new JTextField();
        panelCampos.add(txtMinOblig, gbc);

        // Fila 3: Mínimo Optativas
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.3;
        panelCampos.add(new JLabel("Mín. Optativas:", SwingConstants.RIGHT), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        txtMinOpta = new JTextField();
        panelCampos.add(txtMinOpta, gbc);

        // Fila 4: Estrategia de Inscripción
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0.3;
        panelCampos.add(new JLabel("Estrategia:", SwingConstants.RIGHT), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        comboEstrategia = new JComboBox<>();
        panelCampos.add(comboEstrategia, gbc);

        // Fila 5: Listas de Materias (Doble Selección)
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2; gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;

        JPanel panelMateriasSeleccion = new JPanel(new GridLayout(1, 2, 10, 0));

        modeloListaOblig = new DefaultListModel<>();
        listaMateriasObligatorias = new JList<>(modeloListaOblig);
        listaMateriasObligatorias.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane scrollOblig = new JScrollPane(listaMateriasObligatorias);
        scrollOblig.setBorder(BorderFactory.createTitledBorder("Obligatorias (Ctrl+Click)"));

        modeloListaOpta = new DefaultListModel<>();
        listaMateriasOptativas = new JList<>(modeloListaOpta);
        listaMateriasOptativas.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane scrollOpta = new JScrollPane(listaMateriasOptativas);
        scrollOpta.setBorder(BorderFactory.createTitledBorder("Optativas (Ctrl+Click)"));

        panelMateriasSeleccion.add(scrollOblig);
        panelMateriasSeleccion.add(scrollOpta);
        panelCampos.add(panelMateriasSeleccion, gbc);

        panelFormulario.add(panelCampos, BorderLayout.CENTER);

        // Botonera de guardado / cancelación
        JPanel panelBotonesForm = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        btnGuardar = new JButton("Guardar");
        btnCancelar = new JButton("Cancelar");
        btnGuardar.setFocusPainted(false);
        btnCancelar.setFocusPainted(false);

        panelBotonesForm.add(btnGuardar);
        panelBotonesForm.add(btnCancelar);
        panelFormulario.add(panelBotonesForm, BorderLayout.SOUTH);
    }

    // --- MÉTODOS DE TRANSICIÓN DE PANTALLA ---

    public void mostrarModoDetalleVacio() {
        lblDetalleTitulo.setText("Información Curricular");
        txtAreaMateriasDetalle.setText("\n\n\n     Seleccione un plan de estudio del listado\n     para auditar sus asignaturas correspondientes.");
        cardLayoutDerecho.show(panelDerechoCard, "DETALLE");
    }

    public void mostrarModoDetallePlan(String nombrePlan, String listadoMaterias) {
        lblDetalleTitulo.setText("Detalles: " + nombrePlan);
        txtAreaMateriasDetalle.setText(listadoMaterias);
        cardLayoutDerecho.show(panelDerechoCard, "DETALLE");
    }

    public void mostrarModoFormulario() {
        txtCodigo.setText("");
        txtCodigo.setEditable(true);
        txtNombre.setText("");
        txtMinOblig.setText("0");
        txtMinOpta.setText("0");
        listaMateriasObligatorias.clearSelection();
        listaMateriasOptativas.clearSelection();
        cardLayoutDerecho.show(panelDerechoCard, "FORMULARIO");
    }

    // --- GETTERS & METODOS DE LLENADO ---

    public DefaultTableModel getModeloTabla() { return modeloTabla; }
    public JTable getTablaPlanes() { return tablaPlanes; }

    public String getTxtCodigo() { return txtCodigo.getText().trim(); }
    public String getTxtNombre() { return txtNombre.getText().trim(); }
    public String getTxtMinOblig() { return txtMinOblig.getText().trim(); }
    public String getTxtMinOpta() { return txtMinOpta.getText().trim(); }
    public JComboBox<String> getComboEstrategia() { return comboEstrategia; }

    public JList<String> getListaMateriasObligatorias() { return listaMateriasObligatorias; }
    public JList<String> getListaMateriasOptativas() { return listaMateriasOptativas; }

    public DefaultListModel<String> getModeloListaOblig() { return modeloListaOblig; }
    public DefaultListModel<String> getModeloListaOpta() { return modeloListaOpta; }

    // --- ESCUCHADOR ---
    public void escucharComponentes(ActionListener listener) {
        btnNuevoPlan.addActionListener(listener);
        btnEliminarPlan.addActionListener(listener);
        btnGuardar.addActionListener(listener);
        btnCancelar.addActionListener(listener);
    }
}