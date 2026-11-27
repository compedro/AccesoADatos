import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GestorVentas {

    Map<Integer, Cliente> clientes;
    Map<Integer, Repostaje> repostajes;
    ItfGestorArchivo gestorArchivo;

    public GestorVentas(Map<Integer, Cliente> clientes, Map<Integer, Repostaje> repostajes, ItfGestorArchivo gestorArchivo) {
        this.clientes = clientes;
        this.repostajes = repostajes;
        this.gestorArchivo = gestorArchivo;
    }


    public void darAltaCliente(String codigoCliente, String nombre, String telefono, String matricula) {
        if (codigoCliente == null || codigoCliente.trim().isEmpty()) {
            System.out.println("El código de cliente no puede estar vacío");
            return;
        }
        codigoCliente = codigoCliente.trim();
        if (existeCodigoCliente(codigoCliente)) {
            System.out.println("Ya existe un cliente con ese código");
            return;
        }
        Cliente nuevoCliente = new Cliente(null, codigoCliente, nombre, telefono, matricula);
        // size()+1 reutiliza un ID si hay huecos; máximo+1 respeta los ya asignados
        nuevoCliente.setIdCliente(calcularSiguienteId(clientes.keySet()));
        if (clientes.containsKey(nuevoCliente.getIdCliente())) {
            System.out.println("El cliente ya existe");
            return;
        }
        boolean guardado = gestorArchivo.guardarCliente(nuevoCliente);
        if (guardado) {
            clientes.put(nuevoCliente.getIdCliente(), nuevoCliente);
        } else {
            System.out.println("No se ha podido guardar el cliente");
        }
    }

    private boolean existeCodigoCliente(String codigoCliente) {
        for (Cliente cliente : clientes.values()) {
            if (codigoCliente.equals(cliente.getCodigoCliente())) {
                return true;
            }
        }
        return false;
    }

    private int calcularSiguienteId(Set<Integer> claves) {
        int maximo = 0;
        for (Integer clave : claves) {
            if (clave > maximo) {
                maximo = clave;
            }
        }
        return maximo + 1;
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
        }
        Cliente c = clientes.get(idCliente);  // obtengo los datos del cliente a través de idCliente proporcionado al método
        repostaje.setCliente(c);
        repostaje.setIdRepostaje(calcularSiguienteId(repostajes.keySet())); // máximo+1, no size()+1
        boolean guardado = gestorArchivo.guardarRepostaje(repostaje);
        if (!guardado) {
            // devolver null es la señal de fallo que Main.main ya sabe interpretar
            System.out.println("Error: No se ha podido guardar el repostaje.");
            return null;
        }
        repostajes.put(repostaje.getIdRepostaje(), repostaje);
        return repostaje;
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
