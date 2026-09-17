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
        StringBuilder contenido = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader((rutaArchivo)))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                contenido.append(linea).append("\n");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return contenido.toString();
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