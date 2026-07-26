package vista;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class VentanaPrincipalUI extends JFrame {

    private JPanel contenedorCentral;
    private JButton btnSeccionAlumnos, btnSeccionMaterias, btnSeccionCarreras, btnSeccionPlanes;

    public VentanaPrincipalUI() {
        // Configuración básica de la ventana de Swing
        setTitle("Sistema Académico UNTDF - Panel de Control Profesional");
        setSize(1300, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // La centra en la pantalla automáticamente
        setLayout(new BorderLayout());

        //  1. BARRA SUPERIOR DE MENÚ (Estilo Dashboard)
        JPanel panelMenu = new JPanel(new GridLayout(1, 4, 10, 10));
        panelMenu.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        panelMenu.setBackground(new Color(240, 242, 245)); // Un gris moderno sutil

        btnSeccionAlumnos = new JButton("Sección Alumnos");
        btnSeccionMaterias = new JButton("Sección Materias");
        btnSeccionCarreras = new JButton("Sección Carreras");
        btnSeccionPlanes = new JButton("Planes y Notas");

        // Diseños simples para quitar el estilo nativo tosco
        btnSeccionAlumnos.setFocusPainted(false);
        btnSeccionMaterias.setFocusPainted(false);
        btnSeccionCarreras.setFocusPainted(false);
        btnSeccionPlanes.setFocusPainted(false);

        panelMenu.add(btnSeccionAlumnos);
        panelMenu.add(btnSeccionMaterias);
        panelMenu.add(btnSeccionCarreras);
        panelMenu.add(btnSeccionPlanes);
        add(panelMenu, BorderLayout.NORTH);

        //  2. CONTENEDOR CENTRAL DINÁMICO
        contenedorCentral = new JPanel(new BorderLayout());
        contenedorCentral.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Pantalla de bienvenida por defecto
        JLabel lblBienvenida = new JLabel("Bienvenido al Sistema Académico. Seleccione una opción del menú superior.", SwingConstants.CENTER);
        lblBienvenida.setFont(new Font("Arial", Font.ITALIC, 14));
        contenedorCentral.add(lblBienvenida, BorderLayout.CENTER);

        add(contenedorCentral, BorderLayout.CENTER);
    }


      // El método estrella: Limpia el centro de la pantalla e inyecta el panel que queramos en caliente.

    public void setPanelCentral(JPanel nuevoPanel) {
        contenedorCentral.removeAll();       // Borra lo que había (la bienvenida u otra sección)
        contenedorCentral.add(nuevoPanel, BorderLayout.CENTER); // Pega el panel nuevo (ej: Alumnos)
        contenedorCentral.revalidate();      // Le dice a Swing que recalcule los componentes
        contenedorCentral.repaint();         // Re-dibuja visualmente la pantalla
    }


     // Registra al Controlador Principal para escuchar el menú global

    public void escucharMenu(ActionListener listener) {
        btnSeccionAlumnos.addActionListener(listener);
        btnSeccionMaterias.addActionListener(listener);
        btnSeccionCarreras.addActionListener(listener);
        btnSeccionPlanes.addActionListener(listener);
    }
}