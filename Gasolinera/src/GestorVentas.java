import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class GestorVentas {

    Map<Integer, Cliente> clientes;
    Map<Integer, Repostaje> repostajes;
    GestorArchivoCSV gestorArchivoCSV;
    GestorArchivoJSON gestorArchivoJSON;

    public GestorVentas(Map<Integer, Cliente> clientes, Map<Integer, Repostaje> repostajes, GestorArchivoCSV gestorArchivoCSV, GestorArchivoJSON gestorArchivoJSON) {
        this.clientes = clientes;
        this.repostajes = repostajes;
        this.gestorArchivoCSV = gestorArchivoCSV;
        this.gestorArchivoJSON = gestorArchivoJSON;
    }

    public void darAltaCliente(int idCliente, String nombre, String telefono, String matricula) {
        Cliente nuevoCliente = new Cliente(idCliente, nombre, telefono, matricula);
        nuevoCliente.setIdCliente(clientes.size()+1); // reasigno el idCliente sumando uno al numero de clientes almacenados
        if (!clientes.containsKey(nuevoCliente.getIdCliente())) {
            clientes.put(nuevoCliente.getIdCliente(), nuevoCliente);
            gestorArchivoCSV.guardarCliente(nuevoCliente);
            gestorArchivoJSON.guardarCliente(nuevoCliente);
        } else {
            System.out.println("El cliente ya existe");
        }
    }

    public void listarClientes() {
        if (clientes.isEmpty()) System.out.println("No hay clientes registrados todavía.");
        else {
            List<Cliente> listaClientes = new ArrayList<>(clientes.values()); //convierto el mapa en una lista
            Comparator<Cliente> comparador = Comparator     //creo el comparador con dos criterios
                    .comparing(Cliente::getNombre, String.CASE_INSENSITIVE_ORDER)   //ordenando alfabeticamente ignorando mayusculas y minusculas
                    .thenComparingInt(Cliente::getIdCliente);                       // establezco el desempate por id
            listaClientes.sort(comparador); // ordena la lista
            System.out.printf("%-5s %-20s %-15s %-10s%n", "ID", "NOMBRE", "TELÉFONO", "MATRÍCULA");
            System.out.println("------------------------------------------------------------");
            for (Cliente c : listaClientes) {
                System.out.println(c);
            }
        }
    }

    public void buscarClientes(String texto) {
        boolean encontrado = false;
        for (Cliente c : clientes.values()) {
            if ((c.getNombre().toLowerCase().contains(texto.toLowerCase())) || (c.getMatricula().toLowerCase().contains(texto.toLowerCase()))) {
                encontrado = true;
                System.out.println("ClienteId: " + c.getIdCliente() + "\n" + "Nombre: " + c.getNombre() + "\n" +
                        "Telefono: " + c.getTelefono() + "\n" + "Matricula: " + c.getMatricula()+ "\n"+ "-----");
            }
        }
        if (!encontrado) {
            System.out.println("No hay clientes que contengan el texto proporcionado");
        }
    }
    public Repostaje procesarPago(int idCliente, Repostaje repostaje) {

        if (!clientes.containsKey(idCliente)) { // Primero compruebo si el cliente ya está en el Map de clientes.
            System.out.println("Error: El cliente con ID " + idCliente + " no existe.");
            return null;
        } else {
            Cliente c = clientes.get(idCliente);  // obtengo los datos del cliente a través de idCliente proporcionado al método
            repostaje.setCliente(c);               // asigno el cliente al repostaje
            repostaje.setIdRepostaje(repostajes.size() + 1);      //asigno el idRepostaje sumando uno al numero de repostajes ya registrados
            repostajes.put(repostaje.getIdRepostaje(), repostaje); // añado el repostaje al Map de respostajes
            gestorArchivoCSV.guardarRepostaje(repostaje);       // guardo el repostaje al CSV de repostajes
            return repostaje;
        }
    }

    public void consultarPagos() {
        if (repostajes.isEmpty()) System.out.println("No hay repostajes registrados");
        else {
            System.out.printf("%-15s %-20s %-15s %-15s %-15s %-10s%n", "idRepostaje", "Cliente",
                    "Fecha","Importe","Litros", "Combustible");
            System.out.println("---------------------------------------------------------------------------------------------------------------");
            for (Repostaje r : repostajes.values()) {
                System.out.println(r);
            }
        }
    }
}
