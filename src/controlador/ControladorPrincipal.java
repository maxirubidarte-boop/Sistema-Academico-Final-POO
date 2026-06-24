package controlador;

import modelo.ModeloSistemaAcademico;
import modelo.Alumno;
import vista.VentanaPrincipalUI;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ControladorPrincipal implements ActionListener {

    private VentanaPrincipalUI vista;
    private ModeloSistemaAcademico modelo;

    public ControladorPrincipal(VentanaPrincipalUI vista, ModeloSistemaAcademico modelo) {
        this.vista = vista;
        this.modelo = modelo;

        // Le decimos a la vista que nosotros manejamos sus clics
        this.vista.escucharBotones(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String comando = e.getActionCommand();

        switch (comando) {
            case "Agregar":
                // Desaparece la lista, aparece el formulario
                vista.mostrarModoFormulario();
                break;

            case "Cancelar":
                // Vuelve atrás sin hacer nada
                vista.mostrarModoLista();
                break;

            case "Guardar":
                // 1. Extrae los datos de la vista
                String nombre = vista.getNombreIngresado();
                String dni = vista.getDniIngresado();
                int legajo = Integer.parseInt(vista.getLegajoIngresado());

                // 2. Impacta el modelo real que ya testeamos
                Alumno nuevo = new Alumno(nombre, legajo, dni);
                modelo.registrarAlumnoEnSistema(nuevo);

                System.out.println("✅ [MVC] Alumno guardado en el modelo: " + nombre);

                // 3. Redirige a la página anterior
                vista.mostrarModoLista();
                // (Acá meteríamos la recarga de la tabla real)
                break;

            case "Editar":
                System.out.println("Presionó Editar");
                break;

            case "Eliminar":
                System.out.println("Presionó Eliminar");
                break;
        }
    }
}