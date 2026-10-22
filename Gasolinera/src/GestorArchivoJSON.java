import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
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

    @Override
    public boolean guardarRepostaje(Repostaje r) {
        return false;
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
