package persistencia;

import modelo.ModeloSistemaAcademico;
import java.io.*;

public class GestorPersistencia {

    // Guarda el modelo completo en un archivo binario
    public static void guardarDatos(ModeloSistemaAcademico modelo, String rutaArchivo) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(rutaArchivo))) {
            oos.writeObject(modelo);
            System.out.println("Datos serializados y guardados con éxito.");
        } catch (IOException e) {
            System.err.println("Error al guardar los datos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Lee el archivo binario y reconstruye el modelo
    public static ModeloSistemaAcademico cargarDatos(String rutaArchivo) {
        File archivo = new File(rutaArchivo);
        if (!archivo.exists()) {
            System.out.println("No se encontró archivo previo. Instanciando modelo vacío.");
            return ModeloSistemaAcademico.getInstancia(); // Si no existe, devuelve un modelo limpio
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo))) {
            return (ModeloSistemaAcademico) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error al cargar los datos: " + e.getMessage());
            return ModeloSistemaAcademico.getInstancia(); // Fallback defensivo para que la app no crasheee
        }
    }
}