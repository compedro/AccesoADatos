import java.nio.file.Path;
import java.util.Map;

public interface ItfGestorArchivo  {

    abstract void prepararArchivo();

    abstract boolean guardarCliente(Cliente c);

    abstract boolean guardarRepostaje( Repostaje r);
    abstract boolean escribirArchivo(Path p, String s);

    Map<Integer, Cliente> cargarClientes();

    Map<Integer, Repostaje> cargarRepostajes(Map<Integer, Cliente> clientes);

}
