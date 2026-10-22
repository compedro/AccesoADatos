
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

    public static Cliente fromCsv(String linea) {
        String lineaPartida[] = linea.split(";");
        return new Cliente(Integer.parseInt(lineaPartida[0]), lineaPartida[1], lineaPartida[2], lineaPartida[3]);
    }

    public String toCsv(Repostaje repostaje) {
        return repostaje.getIdRepostaje() + ";" + repostaje.getCliente().getIdCliente() + ";" +
                repostaje.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + ";" +
                repostaje.getImporte() + ";" + repostaje.getLitros() + ";" + repostaje.getCombustible();
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

    }

    @Override
    public boolean guardarCliente(Cliente c) {
        return  escribirArchivo(RutaClientes, toCsv(c));

    }

    @Override
    public boolean guardarRepostaje(Repostaje r) {
        return escribirArchivo(RutaRepostajes, toCsv(r));
    }

    @Override
    public Map<Integer, Cliente> cargarClientes() {
        return Map.of();
    }

    @Override
    public Map<Integer, Repostaje> cargarRepostajes(Map<Integer, Cliente> clientes) {
        return Map.of();
    }
}