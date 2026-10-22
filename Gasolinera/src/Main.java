import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        GestorArchivoCSV ArchivosGasolinera = new GestorArchivoCSV();
        ArchivosGasolinera.prepararArchivo();
        Map<Integer, Cliente> clientes = ArchivosGasolinera.cargarClientes();
        Map<Integer, Repostaje> repostajes = ArchivosGasolinera.cargarRepostajes(clientes);
        GestorVentas gestorVentas = new GestorVentas(clientes,repostajes,ArchivosGasolinera);

        Scanner scanner = new Scanner(System.in);
        int opcion = -1;
        do {
            System.out.println("\n" +
                    "===GESTIÓN DE GASOLINERA ===\n" +
                    "1. Dar de alta un cliente\n" +
                    "2. Listar clientes\n" +
                    "3. Buscar clientes\n" +
                    "4. Procesar un pago de repostaje\n" +
                    "5. Consultar pagos\n" +
                    "0. Salir\n" +
                    "Opción:");
            opcion = scanner.nextInt();
            switch (opcion) {
                case 1 -> { // Dar de alta un cliente
                    System.out.println("Introduce el idCliente: ");
                    int idCliente = scanner.nextInt();
                    scanner.nextLine();
                    System.out.println("Introduce el Nombre: ");
                    String nombreCliente= scanner.nextLine();
                    System.out.println("Introduce el telefono");
                    String telefono = scanner.nextLine();
                    System.out.println("Introduce la matricula");
                    String matricula = scanner.nextLine();
                    gestorVentas.darAltaCliente(idCliente, nombreCliente,telefono,matricula);
                }

                case 2 -> {     // Listar clientes
                    gestorVentas.listarClientes();}
                case 3 -> { // Buscar clientes
                    System.out.println("Introduce el nombre o la matricula a buscar:");
                    scanner.nextLine();
                    String texto = scanner.nextLine();
                    gestorVentas.buscarClientes(texto);
                }
                case 4 -> {}// Procesar un pago de repostaje}
                case 5 -> {}// Consultar pagos}
                case 0 -> System.out.println("¡Gracias por usar la aplicación!");
                default -> System.out.println("Opción no válida. Intente de nuevo.");
                }
        } while (opcion != 0);
    }
}

