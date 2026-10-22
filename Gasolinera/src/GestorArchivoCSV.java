
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

    private static final Path RutaClientes = Path.of("clientes.csv");
    private static final Path RutaRepostajes = Path.of("repostajes.csv");

    public GestorArchivoCSV() {
    }

    public GestorArchivoCSV(Path ruta, String rutaArchivo) {

    }

    public String toCsv(Cliente cliente) {
        return cliente.getIdCliente() + ";" + cliente.getNombre() + ";" + cliente.getTelefono() + ";" + cliente.getMatricula();
    }

    public String toCsv(Repostaje repostaje) {
        return repostaje.getIdRepostaje() + ";" + repostaje.getCliente().getIdCliente() + ";" +
                repostaje.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + ";" +
                repostaje.getImporte() + ";" + repostaje.getLitros() + ";" + repostaje.getCombustible();
    }

    public static Cliente fromCsv(String linea) {
        String lineaPartida[] = linea.split(";");
        return new Cliente(Integer.parseInt(lineaPartida[0]), lineaPartida[1], lineaPartida[2], lineaPartida[3]);
    }

    public static Repostaje fromCsv(String linea, Map<Integer, Cliente> clientes) {
        String[] lineaPartida = linea.split(";");

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

    public String leerArchivo(Path ruta) {

        try {
            return Files.readString(ruta, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public boolean escribirArchivo(Path ruta, String datos) {

        try {
            Files.writeString(ruta, datos + "\n", StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);

        } catch (IOException e) {
            System.out.println("RuntimeException(e)");
            return false;
        }
        return true;
    }

    public static void main(String[] args) {
        GestorArchivoCSV gestorArchivoCSV = new GestorArchivoCSV();
//        gestorArchivo.escribirArchivo("18/9/2026,test, gasoil,20,20");

        Cliente cliente = new Cliente(1, "Ivan", "910000001", "BCD 1234");
        Repostaje repostaje = new Repostaje(1, cliente, LocalDate.now(), 50, 40, TipoCombustible.DIESEL);
        gestorArchivoCSV.escribirArchivo(RutaRepostajes, repostaje.toString());
        System.out.println(gestorArchivoCSV.leerArchivo(RutaRepostajes));
    }

    @Override
    public void prepararArchivo() {
        try {
            if (!Files.exists(RutaRepostajes)) Files.createFile(RutaRepostajes);
            if (!Files.exists(RutaClientes)) Files.createFile(RutaClientes);
        } catch (IOException e) {
            System.out.println("Error al crear archivos, comprobar ruta y/o  permisos de escritura");
        }
    }

    @Override
    public boolean guardarCliente(Cliente c) {
        return escribirArchivo(RutaClientes, toCsv(c));

    }

    @Override
    public boolean guardarRepostaje(Repostaje r) {
        return escribirArchivo(RutaRepostajes, toCsv(r));
    }

    @Override
    public Map<Integer, Cliente> cargarClientes() {

        Map<Integer, Cliente> clientes = new HashMap<>();
        try {
            List<String> lineas = Files.readAllLines(RutaClientes, StandardCharsets.UTF_8);

            for (String linea : lineas) {
                if (!linea.isBlank()) {
                    Cliente cliente = fromCsv(linea);
                    clientes.put(cliente.getIdCliente(), cliente); // put asigna el Idcliente a índice (Integer) del Map
                }
            }
        } catch (IOException e) {
            System.out.println("Error al cargar el archivo de clientes");
        }
        return clientes;
    }

    @Override
    public Map<Integer, Repostaje> cargarRepostajes(Map<Integer, Cliente> clientes) {

        Map<Integer, Repostaje> repostajes = new HashMap<>();
        try {

            List<String> lineas = Files.readAllLines(RutaRepostajes, StandardCharsets.UTF_8);
            for (String linea : lineas) {
                if (!linea.isBlank()) {
                    Repostaje repostaje = fromCsv(linea, clientes); //conversion de linea a objeto
                    if (repostaje != null) repostajes.put(repostaje.getIdRepostaje(), repostaje);
                }
            }
        } catch (IOException e) {
            System.out.println("Error al cargar el archivo repostajes");
        }
        return repostajes;
    }
}