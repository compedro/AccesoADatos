import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MigraCSVToJson {
    private static final Path RutaClientes = Path.of("clientes.csv");
    private static final Path RutaRepostajes = Path.of("repostajes.csv");
    private static final Path RutaClientesJS = Path.of("clientes.js");
    private static final Path RutaRepostajesJS = Path.of("repostajes.js");

    private GestorArchivoJSON g;
    private GestorArchivoCSV c;


    public void migrarCSVToJson(){
        g = new GestorArchivoJSON();
        c = new GestorArchivoCSV();

        // Prepara el archivo CSV para asegurar que está inicializado
        c.prepararArchivo();

        // Eliminar archivos JSON existentes para evitar conflictos
        try {
            if (Files.exists(RutaClientesJS)) {
                Files.delete(RutaClientesJS);
            }
            if (Files.exists(RutaRepostajesJS)) {
                Files.delete(RutaRepostajesJS);
            }
        } catch (IOException e) {
            System.out.println("Error al eliminar archivos JSON existentes: " + e.getMessage());
        }

        // Carga los clientes desde el archivo CSV
        Map<Integer, Cliente> clientes = c.cargarClientes();
        System.out.println("Clientes cargados desde CSV: " + clientes.size());

        // Sincroniza el Map de clientes con el gestor JSON
        g.setClientes(clientes);

        // Migrar los clientes: guardarlos en el archivo JSON
        boolean guardadoClientes = g.guardarClientesEnArchivo();
        System.out.println("Clientes guardados en JSON: " + guardadoClientes);

        // Cargar los repostajes desde el archivo CSV
        Map<Integer, Repostaje> repostajes = c.cargarRepostajes(clientes);
        System.out.println("Repostajes cargados desde CSV: " + repostajes.size());

        // Sincroniza el Map de repostajes con el gestor JSON
        g.setRepostajes(repostajes);

        // Migrar los repostajes: guardarlos en el archivo JSON
        boolean guardadoRepostajes = g.guardarRepostajesEnArchivo();
        System.out.println("Clientes guardados en JSON: " + guardadoRepostajes);

        // Migrar los repostajes: guardarlos en el archivo JSON
        g.guardarRepostajesEnArchivo();
    }

}
