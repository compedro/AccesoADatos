import java.nio.file.Path;
import java.util.Map;

public interface ItfGestorArchivo  {

    abstract void prepararArchivo();

    abstract boolean guardarCliente(Cliente c);

    abstract boolean guardarRepostaje( Repostaje r);
    abstract boolean escribirArchivo(Path p, String s);

    Map<Integer, Cliente> cargarClientes();

    Map<Integer, Repostaje> cargarRepostajes(Map<Integer, Cliente> clientes);

    boolean guardarClientesEnArchivo();  // añado el método para guardar todos los clientes de golpe desde la memoria.

    boolean guardarRepostajesEnArchivo();  // añado el método para guardar todos los repostajes de golpe desde la memoria.

    // Métodos para sincronizar los Maps de memoria con el gestor de archivo
    void setClientes(Map<Integer, Cliente> clientes);  // Sincroniza el Map de clientes
    void setRepostajes(Map<Integer, Repostaje> repostajes);  // Sincroniza el Map de repostajes

}
