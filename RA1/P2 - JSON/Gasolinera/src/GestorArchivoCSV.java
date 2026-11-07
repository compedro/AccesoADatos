
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GestorArchivoCSV implements ItfGestorArchivo {

    private static Path RutaClientes = Path.of("clientes.csv");
    private static Path RutaRepostajes = Path.of("repostajes.csv");
    private static Path directorio;
    private Map<Integer, Cliente> clientes;
    private Map<Integer, Repostaje> repostajes;

//    public void AlmacenamientoEnCSV() {    //  Victor lo ha hecho de esta otra forma
//        directorio = Path.of(".");
//        try {
//            Files.createDirectory(directorio);
//            RutaClientes = directorio.resolve("clientes.csv"); //crea la ruta datos/clientes.csv
//            RutaRepostajes = directorio.resolve("repostajes.csv"); //crea la ruta datos/repostajes.csv
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }

    @Override
    public void prepararArchivo() {
        try {
            if (Files.notExists(RutaClientes)) {
                Files.createFile(RutaClientes);
                Files.writeString(RutaClientes, "id,nombre,telefono,matricula\n");  // encabezamiento del archivo
            }
            if (Files.notExists(RutaRepostajes)) {
                Files.createFile(RutaRepostajes);
                Files.writeString(RutaRepostajes, "idRepostaje, idCliente, Fecha, Importe, litros, combustible\n");  //encabezamiento del archivo
            }
        } catch (IOException e) {
            System.out.println("Error al inicializar los archivos; " + e.getMessage());
        }
    }

    public boolean escribirArchivo(Path ruta, String datos) {
        try {
            Files.writeString(ruta, datos + "\n", StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.out.println("RuntimeException(e)" + e.getMessage());
            return false;
        }
        return true;
    }

    public String toCsv(Cliente cliente) {
        return cliente.getIdCliente() + "," + cliente.getNombre() + "," + cliente.getTelefono() + "," + cliente.getMatricula();
    }

    @Override
    public void setClientes(Map<Integer, Cliente> clientes) {
        this.clientes = clientes;
    }

    @Override
    public void setRepostajes(Map<Integer, Repostaje> repostajes) {
        this.repostajes = repostajes;
    }

    @Override
    public boolean guardarCliente(Cliente c) {
        if (clientes == null) {
            clientes = new HashMap<>();
        }
        clientes.put(c.getIdCliente(), c);
        return escribirArchivo(RutaClientes, toCsv(c));
    }

    @Override
    public boolean guardarClientesEnArchivo() {
        try {
            StringBuilder csv = new StringBuilder("id,nombre,telefono,matricula\n");
            for (Cliente cliente : clientes.values()) {
                csv.append(toCsv(cliente)).append("\n");
            }
            Files.writeString(RutaClientes, csv.toString(), StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            return true;
        } catch (IOException e) {
            System.out.println("Error al guardar clientes en archivo: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean guardarRepostajesEnArchivo() {
        try {
            StringBuilder csv = new StringBuilder("idRepostaje, idCliente, Fecha, Importe, litros, combustible\n");
            for (Repostaje repostaje : repostajes.values()) {
                csv.append(toCsv(repostaje)).append("\n");
            }
            Files.writeString(RutaRepostajes, csv.toString(), StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            return true;
        } catch (IOException e) {
            System.out.println("Error al guardar repostajes en archivo: " + e.getMessage());
            return false;
        }
    }

    public String toCsv(Repostaje repostaje) {
        return repostaje.getIdRepostaje() + "," + repostaje.getCliente().getIdCliente() + "," +
                repostaje.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "," +
                repostaje.getImporte() + "," + repostaje.getLitros() + "," + repostaje.getCombustible();
    }

    @Override
    public boolean guardarRepostaje(Repostaje r) {
        if (repostajes == null) {
            repostajes = new HashMap<>();
        }
        repostajes.put(r.getIdRepostaje(), r);
        return escribirArchivo(RutaRepostajes, toCsv(r));
    }

    public static Cliente fromCsv(String linea) {
        String lineaPartida[] = linea.split(",");
        return new Cliente(Integer.parseInt(lineaPartida[0]), lineaPartida[1], lineaPartida[2], lineaPartida[3]);
    }


    public Map<Integer, Cliente> cargarClientes() {
        if (clientes == null) {
            clientes = new HashMap<>();
        }
        clientes.clear();
        try (BufferedReader bf = Files.newBufferedReader(RutaClientes, StandardCharsets.UTF_8)) {
            bf.readLine(); // salta la primera linea del encabezamiento del archivo
            String linea;
            while ((linea = bf.readLine()) != null) {
                if (!linea.isBlank()) {
                    Cliente cliente = fromCsv(linea);
                    clientes.put(cliente.getIdCliente(), cliente);
                }
            }
        } catch (IOException e) {
            System.out.println("Error al cargar el archivo de clientes");
        }
        return clientes;
    }


    public static Repostaje fromCsv(String linea, Map<Integer, Cliente> clientes) {
        String[] lineaPartida = linea.split(",");

        int idRepostaje = Integer.parseInt(lineaPartida[0]);
        int idCliente = Integer.parseInt(lineaPartida[1]);

        // Buscamos al cliente en el mapa usando su ID
        Cliente clienteEncontrado = clientes.get(idCliente);

        if (clienteEncontrado != null) {
            return new Repostaje(
                    idRepostaje,
                    clienteEncontrado,
                    LocalDate.parse(lineaPartida[2], DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    Double.parseDouble(lineaPartida[3]),
                    Double.parseDouble(lineaPartida[4]),
                    TipoCombustible.valueOf(lineaPartida[5])
            );
        }

        return null; // Si el cliente no existe en el mapa
    }

    /* @Override
    Cambio la forma de leer antigua introduciendo BufferedReader en su lugar
    public Map<Integer, Repostaje> cargarRepostajes(Map<Integer, Cliente> clientes) {

        Map<Integer, Repostaje> repostajes = new HashMap<>();
        try {

            List<String> lineas = Files.readAllLines(RutaRepostajes, StandardCharsets.UTF_8);
            for (int i = 1; i < lineas.size(); i++) {       // salta la primera linea del encabezamiento del archivo.
                String linea = lineas.get(i);
                if (!linea.isBlank()) {
                    Repostaje repostaje = fromCsv(linea, clientes); //conversion de linea a objeto
                    if (repostaje != null)
                        repostajes.put(repostaje.getIdRepostaje(), repostaje); //comprobacion de que no es nulo antes de incluirlo.
                }
            }
        } catch (IOException e) {
            System.out.println("Error al cargar el archivo repostajes");
        }
        return repostajes;
    }
    */

    @Override
    public Map<Integer, Repostaje> cargarRepostajes(Map<Integer, Cliente> clientes) {
        Map<Integer, Repostaje> repostajes = new HashMap<>();
        try (BufferedReader bf = Files.newBufferedReader(RutaRepostajes, StandardCharsets.UTF_8)) {
            bf.readLine(); // salta la primera linea del encabezamiento del archivo
            String linea;
            while ((linea = bf.readLine()) != null) {
                if (!linea.isBlank()) {
                    Repostaje repostaje = fromCsv(linea, clientes);
                    if (repostaje != null)
                        repostajes.put(repostaje.getIdRepostaje(), repostaje);
                }
            }
        } catch (IOException e) {
            System.out.println("Error al cargar el archivo repostajes");
        }
        return repostajes;
    }
}