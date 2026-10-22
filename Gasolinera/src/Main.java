import java.time.LocalDate;
import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        GestorArchivoCSV ArchivosGasolinera = new GestorArchivoCSV();
        ArchivosGasolinera.prepararArchivo();
        Map<Integer, Cliente> clientes = ArchivosGasolinera.cargarClientes();
        Map<Integer, Repostaje> repostajes = ArchivosGasolinera.cargarRepostajes(clientes);
        GestorVentas gestorVentas = new GestorVentas(clientes, repostajes, ArchivosGasolinera);

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
                    String nombreCliente = scanner.nextLine();
                    System.out.println("Introduce el telefono");
                    String telefono = scanner.nextLine();
                    System.out.println("Introduce la matricula");
                    String matricula = scanner.nextLine();
                    gestorVentas.darAltaCliente(idCliente, nombreCliente, telefono, matricula);
                }

                case 2 -> {     // Listar clientes
                    gestorVentas.listarClientes();
                }
                case 3 -> { // Buscar clientes
                    System.out.println("Introduce el nombre o la matricula a buscar: ");
                    scanner.nextLine();
                    String texto = scanner.nextLine();
                    gestorVentas.buscarClientes(texto);
                }
                case 4 -> {// Procesar un pago de repostaje
                    System.out.println("Introduce el id del cliente: ");
                    int idCliente = scanner.nextInt();
                    System.out.println("==Introduce los datos del repostaje ==");
                    System.out.println("Importe abonado: ");
                    double importe = scanner.nextDouble();
                    System.out.println("litros de combustible: ");
                    double litros = scanner.nextDouble();
                    System.out.println("Tipo de combustible: ");
                    TipoCombustible tipoCombustible = null;
                    int opcionCombustible = -1;
                    do {
                        System.out.println("1- DIESEL\n" +
                                "2- GASOLINA 95\n" +
                                "3- GASOLINA 98\n" +
                                "Opcion: ");
                        opcionCombustible = scanner.nextInt();
                        switch (opcionCombustible) {
                            case 1 -> tipoCombustible=TipoCombustible.DIESEL;
                            case 2 -> tipoCombustible=TipoCombustible.GASOLINA_95;
                            case 3 -> tipoCombustible=TipoCombustible.GASOLINA_98;
                            default -> System.out.println("Combustible no disponible");
                            }
                    }
                    while (tipoCombustible==null);
                    Repostaje repostaje = new Repostaje(1, clientes.get(idCliente), LocalDate.now(), importe, litros, tipoCombustible);
                    if (gestorVentas.procesarPago(idCliente, repostaje))
                        System.out.println("Pago procesado correctamente");
                    else {
                        System.out.println("El pago no se ha procesado correctamente");
                    }
                    ;
                }
                case 5 -> { // Consultar pagos
                    gestorVentas.consultarPagos();
                }
                case 0 -> System.out.println("¡Gracias por usar la aplicación!");
                default -> System.out.println("Opción no válida. Intente de nuevo.");
            }
        }
        while (opcion != 0);
    }
}

