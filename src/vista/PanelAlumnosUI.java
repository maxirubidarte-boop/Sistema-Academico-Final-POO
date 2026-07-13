package vista;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;

public class PanelAlumnosUI extends JPanel {

    private CardLayout navegador;
    private JPanel contenedorDinamico;

    // Componentes Modo LISTA
    private JPanel panelLista;
    private JTable tablaAlumnos;
    private DefaultTableModel modeloTabla;
    private JButton btnAgregar, btnEditar, btnEliminar, btnInscribirCarrera, btnVerificarEgreso;
    private JButton btnAnterior, btnSiguiente;
    private JLabel lblPaginacion;

    // Componentes Modo FORMULARIO (Reutilizable)
    private JPanel panelFormulario;
    private JTextField txtNombre, txtDni, txtLegajo;
    private JComboBox<String> comboCarreras; //  Desplegable dinámico
    private JLabel lblLegajoForm, lblCarreraForm; // Etiquetas dinámicas para ocultar/mostrar
    private JButton btnGuardar, btnCancelar;
    private JLabel lblTituloFormulario;

    public PanelAlumnosUI() {
        setLayout(new BorderLayout());
        navegador = new CardLayout();
        contenedorDinamico = new JPanel(navegador);

        armarPanelLista();
        armarPanelFormulario();

        contenedorDinamico.add(panelLista, "LISTA");
        contenedorDinamico.add(panelFormulario, "FORMULARIO");

        add(contenedorDinamico, BorderLayout.CENTER);
        mostrarModoLista();
    }

    private void armarPanelLista() {
        panelLista = new JPanel(new BorderLayout());

        // Configuración de la Tabla
        String[] columnas = {"DNI", "Legajo", "Nombre", "Carrera Actual"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaAlumnos = new JTable(modeloTabla);
        panelLista.add(new JScrollPane(tablaAlumnos), BorderLayout.CENTER);

        // Barra inferior: Paginación y Botones de Acción
        JPanel panelInferior = new JPanel(new BorderLayout());

        // Sub-panel de paginación (Izquierda)
        JPanel panelPaginacion = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnAnterior = new JButton("< Anterior");
        btnSiguiente = new JButton("Siguiente >");
        lblPaginacion = new JLabel("Página 1 de 1");

        btnAnterior.setFocusPainted(false);
        btnSiguiente.setFocusPainted(false);

        panelPaginacion.add(btnAnterior);
        panelPaginacion.add(lblPaginacion);
        panelPaginacion.add(btnSiguiente);

        // Sub-panel de acciones CRUD e Inscripciones (Derecha)
        JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnAgregar = new JButton("Agregar Alumno");
        btnEditar = new JButton("Editar Seleccionado");
        btnInscribirCarrera = new JButton("Inscribir a Carrera");
        btnVerificarEgreso = new JButton("Verificar Egreso");
        btnEliminar = new JButton("Eliminar");

        // Quitar bordes feos
        btnAgregar.setFocusPainted(false);
        btnEditar.setFocusPainted(false);
        btnInscribirCarrera.setFocusPainted(false);
        btnVerificarEgreso.setFocusPainted(false);
        btnEliminar.setFocusPainted(false);

        panelAcciones.add(btnAgregar);
        panelAcciones.add(btnEditar);
        panelAcciones.add(btnInscribirCarrera);
        panelAcciones.add(btnVerificarEgreso);
        panelAcciones.add(btnEliminar);

        panelInferior.add(panelPaginacion, BorderLayout.WEST);
        panelInferior.add(panelAcciones, BorderLayout.EAST);
        panelLista.add(panelInferior, BorderLayout.SOUTH);
    }

    private void armarPanelFormulario() {
        panelFormulario = new JPanel(new BorderLayout());

        lblTituloFormulario = new JLabel("Formulario de Alumno", SwingConstants.CENTER);
        lblTituloFormulario.setFont(new Font("Arial", Font.BOLD, 18));
        lblTituloFormulario.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
        panelFormulario.add(lblTituloFormulario, BorderLayout.NORTH);

        // GridBagLayout para mantener el bloque flotando en el medio exacto
        JPanel contenedorCentrado = new JPanel(new GridBagLayout());

        // Ahora son 4 filas (DNI, Legajo, Nombre, Carrera)
        JPanel panelCampos = new JPanel(new GridLayout(4, 2, 10, 15));
        panelCampos.setPreferredSize(new Dimension(420, 160)); // Un cachito más alto para el combo

        panelCampos.add(new JLabel("Documento (DNI):", SwingConstants.RIGHT));
        txtDni = new JTextField();
        panelCampos.add(txtDni);

        lblLegajoForm = new JLabel("Número de Legajo:", SwingConstants.RIGHT);
        panelCampos.add(lblLegajoForm);
        txtLegajo = new JTextField();
        panelCampos.add(txtLegajo);

        panelCampos.add(new JLabel("Nombre Completo:", SwingConstants.RIGHT));
        txtNombre = new JTextField();
        panelCampos.add(txtNombre);

        lblCarreraForm = new JLabel("Seleccionar Carrera:", SwingConstants.RIGHT);
        panelCampos.add(lblCarreraForm);
        comboCarreras = new JComboBox<>();
        panelCampos.add(comboCarreras);

        contenedorCentrado.add(panelCampos);
        panelFormulario.add(contenedorCentrado, BorderLayout.CENTER);

        // Botones Inferiores del Formulario
        JPanel panelBotonesForm = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        btnGuardar = new JButton("Guardar");
        btnCancelar = new JButton("Cancelar");
        btnGuardar.setFocusPainted(false);
        btnCancelar.setFocusPainted(false);

        panelBotonesForm.add(btnGuardar);
        panelBotonesForm.add(btnCancelar);
        panelFormulario.add(panelBotonesForm, BorderLayout.SOUTH);
    }

    // --- MÉTODOS DE CAMBIO DE ESTADO (CARDLAYOUT) ---

    private void restaurarComponentesFormulario() {
        // Hace visibles todos los componentes estándar del formulario básico
        lblLegajoForm.setVisible(true);
        txtLegajo.setVisible(true);
        lblCarreraForm.setVisible(false);
        comboCarreras.setVisible(false);
    }

    public void mostrarModoLista() {
        navegador.show(contenedorDinamico, "LISTA");
    }

    public void mostrarModoAlta() {
        restaurarComponentesFormulario();
        lblTituloFormulario.setText("Registrar Nuevo Alumno");
        txtDni.setText("");
        txtDni.setEditable(true);
        txtLegajo.setText("");
        txtNombre.setText("");
        navegador.show(contenedorDinamico, "FORMULARIO");
    }

    public void mostrarModoEdicion(String dni, String legajo, String nombre) {
        restaurarComponentesFormulario();
        lblTituloFormulario.setText("Modificar Datos del Alumno");
        txtDni.setText(dni);
        txtDni.setEditable(false); // Bloqueado
        txtLegajo.setText(legajo);
        txtNombre.setText(nombre);
        navegador.show(contenedorDinamico, "FORMULARIO");
    }

    public void mostrarModoInscripcionCarrera(String dni, String nombre, String[] carrerasDisponibles) {
        lblTituloFormulario.setText("Inscripción Formal a Carrera");

        txtDni.setText(dni);
        txtDni.setEditable(false);
        txtNombre.setText(nombre);
        txtNombre.setEditable(false);

        // 🪄 Ocultamos el legajo y mostramos el combo desplegable
        lblLegajoForm.setVisible(false);
        txtLegajo.setVisible(false);
        lblCarreraForm.setVisible(true);

        comboCarreras.removeAllItems();
        for (String c : carrerasDisponibles) {
            comboCarreras.addItem(c);
        }
        comboCarreras.setVisible(true);

        navegador.show(contenedorDinamico, "FORMULARIO");
    }

    // --- GETTERS ---
    public DefaultTableModel getModeloTabla() { return modeloTabla; }
    public JTable getTablaAlumnos() { return tablaAlumnos; }
    public String getTxtDni() { return txtDni.getText().trim(); }
    public String getTxtLegajo() { return txtLegajo.getText().trim(); }
    public String getTxtNombre() { return txtNombre.getText().trim(); }
    public JComboBox<String> getComboCarreras() { return comboCarreras; }

    // Getters para que el controlador escuche los botones nuevos
    public JButton getBtnInscribirCarrera() { return btnInscribirCarrera; }
    public JButton getBtnVerificarEgreso() { return btnVerificarEgreso; }

    public void actualizarEtiquetaPaginacion(int paginaActual, int totalPaginas) {
        lblPaginacion.setText("Página " + paginaActual + " de " + totalPaginas);
    }

    public void escucharComponentes(ActionListener listener) {
        btnAgregar.addActionListener(listener);
        btnEditar.addActionListener(listener);
        btnEliminar.addActionListener(listener);
        btnInscribirCarrera.addActionListener(listener);
        btnVerificarEgreso.addActionListener(listener);
        btnAnterior.addActionListener(listener);
        btnSiguiente.addActionListener(listener);
        btnGuardar.addActionListener(listener);
        btnCancelar.addActionListener(listener);
    }
}