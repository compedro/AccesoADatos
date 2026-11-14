import java.time.LocalDate;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;
import java.sql.*;

public class Main {
    public static void main(String[] args) {

        // Inicializar base de datos y probar conexión
        DatabaseManager.inicializarBaseDeDatos();

        // Prueba de conexión: insertar un cliente
        try {
            Cliente clientePrueba = new Cliente(0, "Juan Perez", "600123456", "1234ABC");
            String sql = "INSERT INTO clientes(nombre, telefono, matricula) VALUES(?, ?, ?)";

            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                /*Prepara la sentencia SQL almacenada en sql (el INSERT)
                Statement.RETURN_GENERATED_KEYS indica que queremos recuperar las claves generadas automáticamente (el ID)*/

                pstmt.setString(1, clientePrueba.getNombre());          //Asigna el primer parámetro (?) de la sentencia SQL con el nombre del cliente
                pstmt.setString(2, clientePrueba.getTelefono());        // el segundo (?) al telefono
                pstmt.setString(3, clientePrueba.getMatricula());       // el tercero (?) a la matricula


                int filasInsertadas = pstmt.executeUpdate();                         // ejecuta el insert en la BBDD asignando el numero de filas insertadas (1)
                System.out.println("Cliente insertado. Filas afectadas: " + filasInsertadas);

/*                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int idGenerado = rs.getInt(1);
                        clientePrueba.setIdCliente(idGenerado);
                        System.out.println("ID generado: " + idGenerado);
                    }
                }*/
            }

            // Prueba de lectura: recuperar el cliente
            String sqlSelect = "SELECT * FROM clientes WHERE id_cliente = ?";
            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sqlSelect)) {

                pstmt.setInt(1, 2);
                // El metodo setInt asigna el valor 2 al primer placeholder (?) de la sentencia SQL
                // clientePrueba.getIdCliente() obtiene el valor del ID del objeto cliente
                ResultSet rs = pstmt.executeQuery();    // ejecuta la query y devuelve TRUE/FALSE

                if (rs.next()) {
                    System.out.println("Cliente recuperado de la BD:");
                    System.out.println("ID: " + rs.getInt("id_cliente"));
                    System.out.println("Nombre: " + rs.getString("nombre"));
                    System.out.println("Teléfono: " + rs.getString("telefono"));
                    System.out.println("Matrícula: " + rs.getString("matricula"));
                    System.out.println("¡Conexión a SQLite funcionando correctamente!");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en la prueba de conexión: " + e.getMessage());
            e.printStackTrace();
        }




        // Código anterior del proyecto...




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

