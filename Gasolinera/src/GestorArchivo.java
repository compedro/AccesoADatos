import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;


public class GestorArchivo {
    private Path rutaArchivo;

    public GestorArchivo(String rutaArchivo) {
        this.rutaArchivo = Path.of(rutaArchivo);
    }

    public String leerArchivo() {
//        StringBuilder contenido = new StringBuilder();
//        try (BufferedReader reader = new BufferedReader(new FileReader((rutaArchivo)))) {
//            String linea;
//            while ((linea = reader.readLine()) != null) {
//                contenido.append(linea).append("\n");
//            }
        try { return Files.readString(rutaArchivo);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
//        return contenido.toString();
    }


    public void escribirArchivo(String datos) {

//        try (FileWriter writer = new FileWriter(rutaArchivo, true)) {
//            writer.write(datos + "\n");
//            writer.close();
        try { Files.writeString(rutaArchivo, datos, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        GestorArchivo gestorArchivo = new GestorArchivo("repostajes.csv");

        gestorArchivo.escribirArchivo("18/9/2026,test, gasoil,20,20");

    }
}