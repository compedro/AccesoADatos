import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        ItfGestorArchivo archivosGasolinera = null;
        boolean persistenciaValida = false;
        do {
            System.out.println("=== PERSISTENCIA ===");
            System.out.println("1. CSV");
            System.out.println("2. JSON");
            System.out.println("Opción:");
            try {
                int opcionPersistencia = scanner.nextInt();
                if (opcionPersistencia == 1) {
                    archivosGasolinera = new GestorArchivoCSV();
                    persistenciaValida = true;
                } else if (opcionPersistencia == 2) {
                    archivosGasolinera = new GestorArchivoJSON();
                    persistenciaValida = true;
                } else {
                    System.out.println("Opción no válida.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Por favor, introduce un número válido.");
                scanner.nextLine();
            }
        } while (!persistenciaValida);

        archivosGasolinera.prepararArchivo();
        Map<Integer, Cliente> clientes = archivosGasolinera.cargarClientes();
        Map<Integer, Repostaje> repostajes = archivosGasolinera.cargarRepostajes(clientes);
        GestorVentas gestorVentas = new GestorVentas(clientes, repostajes, archivosGasolinera);

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
            try {
                opcion = scanner.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Por favor, introduce un número válido.");
                opcion = -1;
                scanner.nextLine();
            }
            switch (opcion) {
                case 1 -> { // Dar de alta un cliente
                    scanner.nextLine();
                    String nombreCliente;
                    do {
                        System.out.println("Introduce el Nombre: ");
                        nombreCliente = scanner.nextLine().trim();
                    } while (nombreCliente.isEmpty());
                    String telefono;
                    do {
                        System.out.println("Introduce el telefono");
                        telefono = scanner.nextLine().trim();
                    } while (telefono.isEmpty());
                    String matricula;
                    do {
                        System.out.println("Introduce la matricula");
                        matricula = scanner.nextLine().trim();
                    } while (matricula.isEmpty());
                    gestorVentas.darAltaCliente(nombreCliente, telefono, matricula);
                }

                case 2 -> {     // Listar clientes
                    gestorVentas.listarClientes();
                }
                case 3 -> { // Buscar clientes
                    String texto;
                    do {
                        System.out.println("Introduce el nombre o la matricula a buscar: ");
                        scanner.nextLine();
                        texto = scanner.nextLine().trim();
                    } while (texto.isEmpty());
                    gestorVentas.buscarClientes(texto);
                }
                case 4 -> {// Procesar un pago de repostaje
                    System.out.println("Introduce el id del cliente: ");
                    int idCliente;
                    boolean idValido = false;
                    do {
                        idCliente = 0;
                        try {
                            idCliente = scanner.nextInt();
                            idValido = true;
                        } catch (Exception e) {
                            System.out.println("Introduce un NÚMERO en el id del cliente");
                            scanner.nextLine();
                        }
                    } while (!idValido);

                    System.out.println("==Introduce los datos del repostaje ==");
                    BigDecimal importe;
                    boolean importeValido = false;
                    do {
                        importe = BigDecimal.ZERO;
                        try {
                            System.out.println("Importe abonado: ");
                            importe = scanner.nextBigDecimal().setScale(2, RoundingMode.HALF_UP);
                            if (importe.compareTo(BigDecimal.ZERO) > 0) importeValido = true;
                            else {
                                System.out.println("El importe debe ser POSITIVO");
                            }
                        } catch (Exception e) {
                            System.out.println("Introduce un NÚMERO en el importe de abonado");
                            scanner.nextLine();
                        }
                    } while (!importeValido);

                    BigDecimal litros;
                    boolean litrosValido = false;
                    do {
                        litros = BigDecimal.ZERO;
                        try {
                            System.out.println("litros de combustible: ");
                            litros = scanner.nextBigDecimal().setScale(2, RoundingMode.HALF_UP);
                            if (litros.compareTo(BigDecimal.ZERO) > 0) litrosValido = true;
                            else {
                                System.out.println("Los litros deben ser un numero POSITIVO");
                            }
                        } catch (Exception e) {
                            System.out.println("Introduce un NÚMERO en los litros");
                            scanner.nextLine();
                        }
                    } while (!litrosValido);

                    System.out.println("Tipo de combustible: ");
                    TipoCombustible tipoCombustible = null;
                    int opcionCombustible = -1;
                    boolean combustibleValido = false;
                    do {
                        try {
                            System.out.println("1- DIESEL\n" +
                                    "2- GASOLINA 95\n" +
                                    "3- GASOLINA 98\n" +
                                    "Opcion: ");
                            opcionCombustible = scanner.nextInt();
                            combustibleValido = true;
                        } catch (Exception e) {
                            System.out.println("Introduce un NÚMERO");
                            scanner.nextLine();
                        }
                        switch (opcionCombustible) {
                            case 1 -> tipoCombustible = TipoCombustible.DIESEL;
                            case 2 -> tipoCombustible = TipoCombustible.GASOLINA_95;
                            case 3 -> tipoCombustible = TipoCombustible.GASOLINA_98;
                            default -> System.out.println("Combustible no disponible");
                        }
                    }
                    while (tipoCombustible == null);
                    Repostaje repostaje = new Repostaje(null, clientes.get(idCliente), LocalDate.now(), importe, litros, tipoCombustible);
                    if (gestorVentas.procesarPago(idCliente, repostaje)!=null) {
                        System.out.println("Pago procesado correctamente");
                        System.out.println("Pago " + repostaje.getIdRepostaje() + " registrado para " + clientes.get(idCliente).getNombre() + " " +repostaje.getImporte() + " € ");
                    }else {
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

