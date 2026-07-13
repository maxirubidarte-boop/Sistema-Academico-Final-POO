package vista;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;

public class PanelMateriasUI extends JPanel {

    private JTable tablaMaterias;
    private DefaultTableModel modeloTabla;

    // Campos del formulario
    private JTextField txtCodigo;
    private JTextField txtNombre;
    private JComboBox<String> comboCuatrimestre;

    // Botones de control (Lista y Formulario)
    private JButton btnAgregar;
    private JButton btnEliminar;
    private JButton btnGuardar;
    private JButton btnCancelar;

    // Botones y etiquetas de paginación
    private JButton btnAnterior;
    private JButton btnSiguiente;
    private JLabel lblPaginacion;

    // Paneles que mutan o necesitan ser accedidos externamente
    private JPanel panelDerechoContenedor;
    private JPanel panelFormulario;
    private JLabel lblTituloFormulario;


    public PanelMateriasUI() {
        // Configuramos nuestro panel principal con un BorderLayout
        this.setLayout(new BorderLayout(10, 10));
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- NORTE: El título del módulo ---
        JLabel lblTituloModulo = new JLabel("Administración de Materias", JLabel.CENTER);
        lblTituloModulo.setFont(new Font("Arial", Font.BOLD, 18));
        this.add(lblTituloModulo, BorderLayout.NORTH);

        // --- CENTRO: Tabla y Paginación ---
        JPanel panelContenedorTabla = new JPanel(new BorderLayout(5, 5));

        String[] columnas = {"Código", "Nombre de Materia", "Cuatrimestre"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; //  Evita que editen tipeando directo en la celda
            }
        };

        tablaMaterias = new JTable(modeloTabla);
        tablaMaterias.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(tablaMaterias);
        panelContenedorTabla.add(scrollPane, BorderLayout.CENTER);

        // Sub-panel de paginación (Sur del panel centro)
        JPanel panelPaginacion = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        btnAnterior = new JButton("< Anterior");
        btnSiguiente = new JButton("Siguiente >");
        lblPaginacion = new JLabel("Página 1 de 1");

        panelPaginacion.add(btnAnterior);
        panelPaginacion.add(lblPaginacion);
        panelPaginacion.add(btnSiguiente);
        panelContenedorTabla.add(panelPaginacion, BorderLayout.SOUTH);

        this.add(panelContenedorTabla, BorderLayout.CENTER);

        // --- ESTE: Panel Lateral Intercambiable (CardLayout) ---
        // Este es el contenedor "padre" de la derecha que maneja las cartas
        panelDerechoContenedor = new JPanel(new CardLayout());
        panelDerechoContenedor.setPreferredSize(new Dimension(300, 0));

        // CARTA 1: Botones de Acción (Modo Lista)
        JPanel panelBotonesAccion = new JPanel(new GridLayout(6, 1, 0, 10));
        panelBotonesAccion.setBorder(BorderFactory.createTitledBorder("Acciones"));
        btnAgregar = new JButton("Agregar Materia");
        btnEliminar = new JButton("Eliminar Materia");

        panelBotonesAccion.add(btnAgregar);
        panelBotonesAccion.add(btnEliminar);
        // Rellenamos el resto del GridLayout para que los botones no se estiren feo
        for (int i = 0; i < 4; i++) {
            panelBotonesAccion.add(new JLabel(""));
        }

        // CARTA 2: Formulario de Carga (Modo Alta)
        panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBorder(BorderFactory.createTitledBorder("Datos de Materia"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5); // Margen interno entre componentes

        // Fila 0: Título interno del formulario
        lblTituloFormulario = new JLabel("Nueva Materia");
        lblTituloFormulario.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panelFormulario.add(lblTituloFormulario, gbc);

        // Fila 1: Campo Código
        gbc.gridwidth = 1; gbc.gridx = 0; gbc.gridy = 1;
        panelFormulario.add(new JLabel("Código (Nº):"), gbc);
        txtCodigo = new JTextField();
        gbc.gridx = 1; gbc.gridy = 1;
        panelFormulario.add(txtCodigo, gbc);

        // Fila 2: Campo Nombre
        gbc.gridx = 0; gbc.gridy = 2;
        panelFormulario.add(new JLabel("Nombre:"), gbc);
        txtNombre = new JTextField();
        gbc.gridx = 1; gbc.gridy = 2;
        panelFormulario.add(txtNombre, gbc);

        // Fila 3: Combo Cuatrimestre
        gbc.gridx = 0; gbc.gridy = 3;
        panelFormulario.add(new JLabel("Cuatrimestre:"), gbc);
        String[] opcionesFormato = {"1º Cuatrimestre", "2º Cuatrimestre", "3º Cuatrimestre", "4º Cuatrimestre", "5º Cuatrimestre", "6º Cuatrimestre"};
        comboCuatrimestre = new JComboBox<>(opcionesFormato);
        gbc.gridx = 1; gbc.gridy = 3;
        panelFormulario.add(comboCuatrimestre, gbc);

        // Fila 4: Botones Guardar/Cancelar (Metidos en un sub-panel para ir juntos)
        JPanel panelBotonesForm = new JPanel(new GridLayout(1, 2, 5, 0));
        btnGuardar = new JButton("Guardar");
        btnCancelar = new JButton("Cancelar");
        panelBotonesForm.add(btnGuardar);
        panelBotonesForm.add(btnCancelar);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        panelFormulario.add(panelBotonesForm, gbc);

        //  Agregamos las dos cartas al mazo contenedor identificándolas con un String
        panelDerechoContenedor.add(panelBotonesAccion, "Botones");
        panelDerechoContenedor.add(panelFormulario, "Formulario");

        this.add(panelDerechoContenedor, BorderLayout.EAST);
    }

    //  MÉTODOS DE CONTROL VISUAL (Intercambio de cartas)

    public void mostrarModoLista() {
        txtCodigo.setText("");
        txtNombre.setText("");
        txtCodigo.setEditable(true);

        CardLayout cl = (CardLayout) panelDerechoContenedor.getLayout();
        cl.show(panelDerechoContenedor, "Botones");

        tablaMaterias.setEnabled(true);
    }

    public void mostrarModoAlta() {
        lblTituloFormulario.setText("Nueva Materia");

        CardLayout cl = (CardLayout) panelDerechoContenedor.getLayout();
        cl.show(panelDerechoContenedor, "Formulario");

        tablaMaterias.setEnabled(false);
    }

    // -------------------------------------------------------------------------
    //  ENLACE CON EL CONTROLADOR
    // -------------------------------------------------------------------------
    public void escucharComponentes(ActionListener listener) {
        btnAgregar.addActionListener(listener);
        btnEliminar.addActionListener(listener);
        btnGuardar.addActionListener(listener);
        btnCancelar.addActionListener(listener);
        btnAnterior.addActionListener(listener);
        btnSiguiente.addActionListener(listener);
    }


    public JTable getTablaMaterias() { return tablaMaterias; }
    public DefaultTableModel getModeloTabla() { return modeloTabla; }
    public String getTxtCodigo() { return txtCodigo.getText().trim(); }
    public String getTxtNombre() { return txtNombre.getText().trim(); }
    public Integer getCuatrimestreSeleccionado() {return comboCuatrimestre.getSelectedIndex() + 1;}
    public JButton getBtnAnterior() { return btnAnterior; }
    public JButton getBtnSiguiente() { return btnSiguiente; }
    public JLabel getLblPaginacion() { return lblPaginacion; }
}