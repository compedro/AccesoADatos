import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        GestorArchivoCSV ArchivosGasolinera = new GestorArchivoCSV();
        ArchivosGasolinera.prepararArchivo();
        Map<Integer, Cliente> clientes = ArchivosGasolinera.cargarClientes();
        Map<Integer, Repostaje> repostajes = ArchivosGasolinera.cargarRepostajes(clientes);

        Scanner scanner = new Scanner(System.in);
        int opcion = -1;
        do {
            System.out.println("===GESTIÓN DE GASOLINERA ===\n" +
                    "1. Dar de alta un cliente\n" +
                    "2. Listar clientes\n" +
                    "3. Buscar clientes\n" +
                    "4. Procesar un pago de repostaje\n" +
                    "5. Consultar pagos\n" +
                    "0. Salir\n" +
                    "Opción:");
            opcion = scanner.nextInt();
        } while (opcion != 0);
        GestorVentas gestorVentas = new GestorVentas();
        switch (opcion) {
            case 1 -> gestorVentas.darAltaCliente();// Dar de alta un cliente
            case 2 -> // Listar clientes
            case 3 -> // Buscar clientes
            case 4 -> // Procesar un pago de repostaje
            case 5 -> // Consultar pagos
            case 0 -> System.out.println("¡Gracias por usar la aplicación!");
            default -> System.out.println("Opción no válida. Intente de nuevo.");
        }
    }


}

