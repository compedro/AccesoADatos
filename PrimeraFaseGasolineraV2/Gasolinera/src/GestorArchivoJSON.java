import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GestorArchivoJSON implements ItfGestorArchivo {

    private static final Path RUTA_CLIENTES = Path.of("clientes.json");
    private static final Path RUTA_REPOSTAJES = Path.of("repostajes.json");
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // El gestor conserva los mapas: al añadir hay que reescribir también los registros antiguos.
    private final Map<Integer, Cliente> clientes = new HashMap<>();
    private final Map<Integer, Repostaje> repostajes = new HashMap<>();

    @Override
    public void prepararArchivo() {
        try {
            if (Files.notExists(RUTA_CLIENTES)) {
                Files.writeString(RUTA_CLIENTES, "[]\n", StandardCharsets.UTF_8);
            }
            if (Files.notExists(RUTA_REPOSTAJES)) {
                Files.writeString(RUTA_REPOSTAJES, "[]\n", StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            System.out.println("Error al inicializar los archivos JSON: " + e.getMessage());
        }
    }

    @Override
    public boolean escribirArchivo(Path ruta, String datos) {
        // A diferencia de CSV, aquí sobrescribimos: el corchete de cierre no puede quedar a mitad de fichero.
        try {
            Files.writeString(ruta, datos + "\n", StandardCharsets.UTF_8);
            return true;
        } catch (IOException e) {
            System.out.println("Error al escribir el archivo JSON: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean guardarCliente(Cliente cliente) {
        Cliente anterior = clientes.put(cliente.getIdCliente(), cliente);
        if (!escribirArchivo(RUTA_CLIENTES, serializarClientes())) {
            // Si falla la escritura, dejamos la memoria como estaba.
            if (anterior == null) {
                clientes.remove(cliente.getIdCliente());
            } else {
                clientes.put(cliente.getIdCliente(), anterior);
            }
            return false;
        }
        return true;
    }

    @Override
    public boolean guardarRepostaje(Repostaje repostaje) {
        Repostaje anterior = repostajes.put(repostaje.getIdRepostaje(), repostaje);
        if (!escribirArchivo(RUTA_REPOSTAJES, serializarRepostajes())) {
            if (anterior == null) {
                repostajes.remove(repostaje.getIdRepostaje());
            } else {
                repostajes.put(repostaje.getIdRepostaje(), anterior);
            }
            return false;
        }
        return true;
    }

    private String serializarClientes() {
        StringBuilder resultado = new StringBuilder("[\n");
        boolean primero = true;
        for (Cliente cliente : clientes.values()) {
            if (!primero) {
                resultado.append(",\n");
            }
            resultado.append("  ").append(aJson(cliente));
            primero = false;
        }
        resultado.append("\n]");
        return resultado.toString();
    }

    private String serializarRepostajes() {
        StringBuilder resultado = new StringBuilder("[\n");
        boolean primero = true;
        for (Repostaje repostaje : repostajes.values()) {
            if (!primero) {
                resultado.append(",\n");
            }
            resultado.append("  ").append(aJson(repostaje));
            primero = false;
        }
        resultado.append("\n]");
        return resultado.toString();
    }

    private String aJson(Cliente cliente) {
        return "{\"id\":" + cliente.getIdCliente()
                + ",\"nombre\":\"" + escapar(cliente.getNombre()) + "\""
                + ",\"telefono\":\"" + escapar(cliente.getTelefono()) + "\""
                + ",\"matricula\":\"" + escapar(cliente.getMatricula()) + "\"}";
    }

    private String aJson(Repostaje repostaje) {
        return "{\"idRepostaje\":" + repostaje.getIdRepostaje()
                + ",\"idCliente\":" + repostaje.getCliente().getIdCliente()
                + ",\"fecha\":\"" + repostaje.getFecha().format(FORMATO_FECHA) + "\""
                + ",\"importe\":" + repostaje.getImporte()
                + ",\"litros\":" + repostaje.getLitros()
                + ",\"combustible\":\"" + repostaje.getCombustible() + "\"}";
    }

    private String escapar(String texto) {
        return texto.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    @Override
    public Map<Integer, Cliente> cargarClientes() {
        clientes.clear();
        try {
            List<String> lineas = Files.readAllLines(RUTA_CLIENTES, StandardCharsets.UTF_8);
            for (String linea : lineas) {
                String objeto = linea.trim();
                if (objeto.isEmpty() || objeto.equals("[") || objeto.equals("]") || objeto.equals("[]")) {
                    continue;
                }
                if (objeto.endsWith(",")) {
                    objeto = objeto.substring(0, objeto.length() - 1).trim();
                }
                Cliente cliente = desdeJsonCliente(objeto);
                clientes.put(cliente.getIdCliente(), cliente);
            }
        } catch (IOException e) {
            System.out.println("Error al cargar los clientes JSON: " + e.getMessage());
        }
        return clientes;
    }

    @Override
    public Map<Integer, Repostaje> cargarRepostajes(Map<Integer, Cliente> clientesCargados) {
        repostajes.clear();
        try {
            List<String> lineas = Files.readAllLines(RUTA_REPOSTAJES, StandardCharsets.UTF_8);
            for (String linea : lineas) {
                String objeto = linea.trim();
                if (objeto.isEmpty() || objeto.equals("[") || objeto.equals("]") || objeto.equals("[]")) {
                    continue;
                }
                if (objeto.endsWith(",")) {
                    objeto = objeto.substring(0, objeto.length() - 1).trim();
                }
                int idCliente = Integer.parseInt(leerNumero(objeto, "idCliente"));
                Cliente cliente = clientesCargados.get(idCliente);
                if (cliente != null) {
                    Repostaje repostaje = desdeJsonRepostaje(objeto, cliente);
                    repostajes.put(repostaje.getIdRepostaje(), repostaje);
                }
            }
        } catch (IOException e) {
            System.out.println("Error al cargar los repostajes JSON: " + e.getMessage());
        }
        return repostajes;
    }

    private Cliente desdeJsonCliente(String objeto) {
        return new Cliente(
                Integer.valueOf(leerNumero(objeto, "id")),
                leerTexto(objeto, "nombre"),
                leerTexto(objeto, "telefono"),
                leerTexto(objeto, "matricula"));
    }

    private Repostaje desdeJsonRepostaje(String objeto, Cliente cliente) {
        return new Repostaje(
                Integer.valueOf(leerNumero(objeto, "idRepostaje")),
                cliente,
                LocalDate.parse(leerTexto(objeto, "fecha"), FORMATO_FECHA),
                new BigDecimal(leerNumero(objeto, "importe")).setScale(2, RoundingMode.HALF_UP),
                new BigDecimal(leerNumero(objeto, "litros")).setScale(2, RoundingMode.HALF_UP),
                TipoCombustible.valueOf(leerTexto(objeto, "combustible")));
    }

    private String leerTexto(String objeto, String clave) {
        String marca = "\"" + clave + "\":\"";
        int inicio = objeto.indexOf(marca) + marca.length();
        int fin = inicio;
        boolean escapado = false;
        while (fin < objeto.length()) {
            char actual = objeto.charAt(fin);
            if (actual == '"' && !escapado) {
                break;
            }
            escapado = (actual == '\\' && !escapado);
            fin++;
        }
        return objeto.substring(inicio, fin).replace("\\\"", "\"").replace("\\\\", "\\");
    }

    private String leerNumero(String objeto, String clave) {
        String marca = "\"" + clave + "\":";
        int inicio = objeto.indexOf(marca) + marca.length();
        int fin = inicio;
        while (fin < objeto.length() && objeto.charAt(fin) != ',' && objeto.charAt(fin) != '}') {
            fin++;
        }
        return objeto.substring(inicio, fin).trim();
    }
}
