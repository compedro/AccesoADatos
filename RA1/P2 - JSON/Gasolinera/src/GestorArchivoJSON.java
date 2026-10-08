import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class GestorArchivoJSON implements ItfGestorArchivo {
    private static final Path RutaClientesJS = Path.of("clientes.js");
    private static final Path RutaRepostajesJS = Path.of("repostajes.js");

    @Override
    public void prepararArchivo() {
        try {
            if (Files.notExists(RutaClientesJS)) {
                Files.createFile(RutaClientesJS);
                Files.writeString(RutaClientesJS, "{ 'clientes' : [ ");
            }
            if (Files.notExists(RutaRepostajesJS)) {
                Files.createFile(RutaRepostajesJS);
                Files.writeString(RutaRepostajesJS, "{ 'pagos' : [ ");
            }
        } catch (IOException e) {
            System.out.println("Error al inicializar los archivos; " + e.getMessage());
        }
    }

    @Override
    public boolean escribirArchivo(Path ruta, String datos) {
        try {
            Files.writeString(ruta, datos + "\n", StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);

        } catch (IOException e) {
            System.out.println("RuntimeException(e) Ojo!" + e.getMessage());
            return false;
        }
        return true;
    }

    public String toJson(Cliente cliente) {
       return  "{\"id\": " + cliente.getIdCliente() + ",\"nombre\": \"" + cliente.getNombre() + "\",\"telefono\": \""
                + cliente.getTelefono() + "\",\"matricula\": \"" + cliente.getMatricula() + "\"}";
    }

    @Override
    public boolean guardarCliente(Cliente c) {
        return escribirArchivo(RutaClientesJS, toJson(c) );
    }

    public String toJson(Repostaje repostaje) {
        return "{\"id\": " + repostaje.getIdRepostaje() + ",\"id_cliente\": " + repostaje.getCliente().getIdCliente()
                + ",\"fecha\": \"" + repostaje.getFecha() + "\",\"importe\": " + repostaje.getImporte()
                + ",\"litros\": " + repostaje.getLitros() + ",\"combustible\": \"" + repostaje.getCombustible() + "\"}";

    }

    @Override
    public boolean guardarRepostaje(Repostaje r) {
        return escribirArchivo(RutaRepostajesJS, toJson(r) );
    }

    public static Cliente fromJson(String linea) {
        String limpio = linea.replaceAll("[\"{}]", "");
        String[] campos = limpio.split(",");
        
        int id = Integer.parseInt(campos[0].split(":")[1].trim());
        String nombre = campos[1].split(":")[1].trim();
        String telefono = campos[2].split(":")[1].trim();
        String matricula = campos[3].split(":")[1].trim();
        
        return new Cliente(id, nombre, telefono, matricula);
    }

    public static Repostaje fromJson(String linea, Map<Integer, Cliente> clientes) {
        String limpio = linea.replaceAll("[\"{}]", "");
        String[] campos = limpio.split(",");
        
        int idRepostaje = Integer.parseInt(campos[0].split(":")[1].trim());
        int idCliente = Integer.parseInt(campos[1].split(":")[1].trim());
        
        Cliente clienteEncontrado = clientes.get(idCliente);
        
        if (clienteEncontrado != null) {
            String fechaStr = campos[2].split(":")[1].trim();
            double importe = Double.parseDouble(campos[3].split(":")[1].trim());
            double litros = Double.parseDouble(campos[4].split(":")[1].trim());
            TipoCombustible combustible = TipoCombustible.valueOf(campos[5].split(":")[1].trim());
            
            return new Repostaje(idRepostaje, clienteEncontrado, 
                                LocalDate.parse(fechaStr), importe, litros, combustible);
        }
        
        return null;
    }

    @Override
    public Map<Integer, Cliente> cargarClientes() {
        Map<Integer, Cliente> clientes = new HashMap<>();
        try (BufferedReader br = Files.newBufferedReader(RutaClientesJS, StandardCharsets.UTF_8)) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.contains("\"id\":") && linea.contains("\"nombre\":")) {
                    try {
                        Cliente cliente = fromJson(linea);
                        clientes.put(cliente.getIdCliente(), cliente);
                    } catch (Exception e) {
                        System.out.println("Error al parsear línea: " + linea);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error al cargar el archivo de clientes JSON: " + e.getMessage());
        }
        return clientes;
    }

    @Override
    public Map<Integer, Repostaje> cargarRepostajes(Map<Integer, Cliente> clientes) {
        Map<Integer, Repostaje> repostajes = new HashMap<>();
        try (BufferedReader br = Files.newBufferedReader(RutaRepostajesJS, StandardCharsets.UTF_8)) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.contains("\"id\":") && linea.contains("\"id_cliente\":")) {
                    try {
                        Repostaje repostaje = fromJson(linea, clientes);
                        if (repostaje != null) {
                            repostajes.put(repostaje.getIdRepostaje(), repostaje);
                        }
                    } catch (Exception e) {
                        System.out.println("Error al parsear línea: " + linea);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error al cargar el archivo de repostajes JSON: " + e.getMessage());
        }
        return repostajes;
    }
}
