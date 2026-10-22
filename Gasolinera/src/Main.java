import java.time.LocalDate;
import java.util.InputMismatchException;
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
                    gestorVentas.darAltaCliente(1, nombreCliente, telefono, matricula); // asigno un id fijo porque luego se lo reasignaré en el darAltaCliente
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
                    double importe;
                    boolean importeValido = false;
                    do {
                        importe = 0;
                        try {
                            System.out.println("Importe abonado: ");
                            importe = scanner.nextDouble();
                            importe= Math.round(importe * 100) /100.0;
                            if (importe > 0) importeValido = true;
                            else {
                                System.out.println("El importe debe ser POSITIVO");
                            }
                        } catch (Exception e) {
                            System.out.println("Introduce un NÚMERO en el importe de abonado");
                            scanner.nextLine();
                        }
                    } while (!importeValido);

                    double litros;
                    boolean litrosValido = false;
                    do {
                        litros = 0;
                        try {
                            System.out.println("litros de combustible: ");
                            litros = scanner.nextDouble();
                            litros = Math.round(litros*100)/100.0;
                            if(litros>0)litrosValido = true;
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
                    Repostaje repostaje = new Repostaje(1, clientes.get(idCliente), LocalDate.now(), importe, litros, tipoCombustible);
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

