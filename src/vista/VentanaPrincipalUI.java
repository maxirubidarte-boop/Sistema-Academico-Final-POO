package vista;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class VentanaPrincipalUI extends JFrame {

    // Componentes del Modo Lista
    private JTable tablaDatos;
    private JButton btnAgregar, btnEditar, btnEliminar;
    private JPanel panelLista;

    // Componentes del Modo Formulario
    private JPanel panelFormulario;
    private JTextField txtNombre, txtDni, txtLegajo;
    private JButton btnGuardar, btnCancelar;

    // Contenedor principal que intercambia las pantallas
    private JPanel contenedorDinamico;
    private CardLayout navegador;

    public VentanaPrincipalUI() {
        setTitle("Sistema Académico UNTDF - Control de Consola Única");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        navegador = new CardLayout();
        contenedorDinamico = new JPanel(navegador);

        // Construimos las dos pantallas
        armarPanelLista();
        armarPanelFormulario();

        // Las agregamos al contenedor con una clave en String para llamarlas
        contenedorDinamico.add(panelLista, "LISTA");
        contenedorDinamico.add(panelFormulario, "FORMULARIO");

        add(contenedorDinamico);

        // Arranca mostrando la lista con los 3 botones estándar
        mostrarModoLista();
    }

    private void armarPanelLista() {
        panelLista = new JPanel(new BorderLayout());

        // Tabla simple de muestra
        String[] columnas = {"Nombre", "Legajo", "DNI"};
        String[][] datosMuestra = {{"Maxi Rubidarte", "4001", "45123456"}};
        tablaDatos = new JTable(datosMuestra, columnas);
        panelLista.add(new JScrollPane(tablaDatos), BorderLayout.CENTER);

        // Botones ABM estándar
        JPanel panelBotones = new JPanel();
        btnAgregar = new JButton("Agregar");
        btnEditar = new JButton("Editar");
        btnEliminar = new JButton("Eliminar");

        panelBotones.add(btnAgregar);
        panelBotones.add(btnEditar);
        panelBotones.add(btnEliminar);
        panelLista.add(panelBotones, BorderLayout.SOUTH);
    }

    private void armarPanelFormulario() {
        panelFormulario = new JPanel(new GridLayout(4, 2, 10, 10));

        panelFormulario.add(new JLabel("  Nombre:"));
        txtNombre = new JTextField();
        panelFormulario.add(txtNombre);

        panelFormulario.add(new JLabel("  DNI:"));
        txtDni = new JTextField();
        panelFormulario.add(txtDni);

        panelFormulario.add(new JLabel("  Legajo:"));
        txtLegajo = new JTextField();
        panelFormulario.add(txtLegajo);

        btnGuardar = new JButton("Guardar");
        btnCancelar = new JButton("Cancelar");
        panelFormulario.add(btnGuardar);
        panelFormulario.add(btnCancelar);
    }

    // Métodos de navegación que va a usar el Controlador
    public void mostrarModoFormulario() {
        limpiarFormulario();
        navegador.show(contenedorDinamico, "FORMULARIO");
    }

    public void mostrarModoLista() {
        navegador.show(contenedorDinamico, "LISTA");
    }

    public void limpiarFormulario() {
        txtNombre.setText("");
        txtDni.setText("");
        txtLegajo.setText("");
    }

    // GETTERS de datos para el controlador
    public String getNombreIngresado() { return txtNombre.getText(); }
    public String getDniIngresado() { return txtDni.getText(); }
    public String getLegajoIngresado() { return txtLegajo.getText(); }

    // Conectores de eventos (Los escucha el controlador)
    public void escucharBotones(ActionListener listener) {
        btnAgregar.addActionListener(listener);
        btnEditar.addActionListener(listener);
        btnEliminar.addActionListener(listener);
        btnGuardar.addActionListener(listener);
        btnCancelar.addActionListener(listener);
    }
}