import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class GestorArchivoJSON implements ItfGestorArchivo {
    private static final Path RutaClientesJS = Path.of("clientes.js");
    private static final Path RutaRepostajesJS = Path.of("repostajes.js");

    @Override
    public void prepararArchivo() {
        try {
            if (Files.notExists(RutaClientesJS)) {
                Files.createFile(RutaClientesJS);
                Files.writeString(RutaClientesJS, "{ 'clientes' : [ ");
            }
            if (Files.notExists(RutaRepostajesJS)) {
                Files.createFile(RutaRepostajesJS);
                Files.writeString(RutaRepostajesJS, "{ 'pagos' : [ ");
            }
        } catch (IOException e) {
            System.out.println("Error al inicializar los archivos; " + e.getMessage());
        }
    }

    @Override
    public boolean escribirArchivo(Path ruta, String datos) {
        try {
            Files.writeString(ruta, datos + "\n", StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);

        } catch (IOException e) {
            System.out.println("RuntimeException(e) Ojo!" + e.getMessage());
            return false;
        }
        return true;
    }

    public String toJson(Cliente cliente) {
        return "{"+"'id':" + cliente.getIdCliente() + ","+"'nombre: '" + cliente.getNombre() + ","+"'telefono: '" + cliente.getTelefono() + "," + "'matricula: '"+cliente.getMatricula()+"}";
    }

    @Override
    public boolean guardarCliente(Cliente c) {
        return escribirArchivo(RutaClientesJS, toJson(c) );
    }

    public String toJson(Repostaje repostaje) {
        return "{"+"'idRespotaje':" + repostaje.getIdRepostaje() + "," + "'idRespotaje':" + repostaje.getCliente().getIdCliente() + "," + "'Fecha': "+
                repostaje.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "," + "'Importe: '"+
                repostaje.getImporte() + "," +"'Repostaje: '"+ repostaje.getLitros() + "," +"'Combustible:' "+ repostaje.getCombustible()+"}";
    }

    @Override
    public boolean guardarRepostaje(Repostaje r) {
        return escribirArchivo(RutaRepostajesJS, toJson(r) );
    }

    public static Cliente fromJson(String linea) {
        String lineaPartida[] = linea.split(",");   // Hace falta establece la forma de eliminar el texto del limite entre etiquetes y datos
        return new Cliente(Integer.parseInt(lineaPartida[0]), lineaPartida[1], lineaPartida[2], lineaPartida[3]);
    }

    @Override
    public Map<Integer, Cliente> cargarClientes() {
        return Map.of();
    }

    @Override
    public Map<Integer, Repostaje> cargarRepostajes(Map<Integer, Cliente> clientes) {
        return Map.of();
    }
}
