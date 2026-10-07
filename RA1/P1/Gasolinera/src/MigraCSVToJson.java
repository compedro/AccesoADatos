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
    }

    public void migrarTodo(){
        Map<Integer, Cliente> clientes = c.cargarClientes();
        for (Cliente cliente : clientes.values()) {
            g.guardarCliente(cliente);
        }
    }

}
