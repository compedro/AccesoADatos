import java.util.ArrayList;
import java.util.Map;

public class GestorVentas {

    Map<Integer, Cliente> clientes;
    Map<Integer, Repostaje> repostajes;
    GestorArchivoCSV gestorArchivoCSV;

    public GestorVentas(Map<Integer, Cliente> clientes, Map<Integer, Repostaje> repostajes, GestorArchivoCSV gestorArchivoCSV) {
        this.clientes = clientes;
        this.repostajes = repostajes;
        this.gestorArchivoCSV = gestorArchivoCSV;
    }

    public void darAltaCliente(int idCliente, String nombre, String telefono, String matricula) {
        Cliente nuevoCliente = new Cliente(idCliente, nombre, telefono, matricula);
        if (!clientes.containsKey(nuevoCliente.getIdCliente())) {
            clientes.put(nuevoCliente.getIdCliente(), nuevoCliente);
            gestorArchivoCSV.guardarCliente(nuevoCliente);
        } else {
            System.out.println("El cliente ya existe");
        }

    }

    public void listarClientes() {
    }

    public void buscarClientes(String texto) {

    }

    public void procesarPago(Cliente cliente, Repostaje repostaje) {
    }

    public void consultarPagos(String texto) {

    }
}
