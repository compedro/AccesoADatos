import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class GestorArchivo {
    private String rutaArchivo;

    public GestorArchivo(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
    }

    public String leerArchivo() {
        try (BufferedReader reader = new BufferedReader(new FileReader((rutaArchivo)))) {
            StringBuilder contenido = StringBuilder();
            String linea;
            while ((linea = reader.readLine()) != null) {
                contenido.append(linea).append("\n");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public void escribirArchivo(String datos) {

        try (FileWriter writer = new FileWriter(rutaArchivo, true)) {
            writer.write(datos + "\n");
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}