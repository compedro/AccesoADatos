import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GestorArchivoJSON implements ItfGestorArchivo {

    private static final Path RutaClientes = Path.of("clientes.json");
    private static final Path RutaRepostajes = Path.of("repostajes.json");

    private final ObjectMapper mapper;

    public GestorArchivoJSON() {
        this.mapper = new ObjectMapper();
        // Registra el módulo para soportar LocalDate
        this.mapper.registerModule(new JavaTimeModule());
        // Desactiva escribir fechas como timestamps numéricos para guardarlas legibles (YYYY-MM-DD)
        this.mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // Formatea el JSON con sangrías e indentación legible
        this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Override
    public void prepararArchivo() {
        try {
            if (Files.notExists(RutaClientes)) {
                Files.writeString(RutaClientes, "[]\n", StandardCharsets.UTF_8);
            }
            if (Files.notExists(RutaRepostajes)) {
                Files.writeString(RutaRepostajes, "[]\n", StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            System.err.println("Error al inicializar los archivos JSON: " + e.getMessage());
        }
    }

    @Override
    public boolean escribirArchivo(Path ruta, String datos) {
        try {
            Files.writeString(ruta, datos + "\n", StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            return true;
        } catch (IOException e) {
            System.err.println("Error al escribir archivo: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean guardarCliente(Cliente c) {
        try {
            List<Cliente> lista = obtenerListaClientes();
            lista.add(c);
            mapper.writeValue(RutaClientes.toFile(), lista);
            return true;
        } catch (IOException e) {
            System.err.println("Error al guardar cliente en JSON: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Map<Integer, Cliente> cargarClientes() {
        Map<Integer, Cliente> clientesMap = new HashMap<>();
        List<Cliente> lista = obtenerListaClientes();
        for (Cliente c : lista) {
            clientesMap.put(c.getIdCliente(), c);
        }
        return clientesMap;
    }

    private List<Cliente> obtenerListaClientes() {
        if (!Files.exists(RutaClientes)) {
            return new ArrayList<>();
        }
        try {
            if (Files.size(RutaClientes) == 0) {
                return new ArrayList<>();
            }
            return mapper.readValue(RutaClientes.toFile(), new TypeReference<List<Cliente>>() {});
        } catch (IOException e) {
            System.err.println("Error al leer clientes.json: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public boolean guardarRepostaje(Repostaje r) {
        try {
            List<Repostaje> lista = obtenerListaRepostajes();
            lista.add(r);
            mapper.writeValue(RutaRepostajes.toFile(), lista);
            return true;
        } catch (IOException e) {
            System.err.println("Error al guardar repostaje en JSON: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Map<Integer, Repostaje> cargarRepostajes(Map<Integer, Cliente> clientes) {
        Map<Integer, Repostaje> repostajesMap = new HashMap<>();
        List<Repostaje> lista = obtenerListaRepostajes();
        for (Repostaje r : lista) {
            // Vinculamos la instancia existente en memoria del cliente si coincide el ID
            if (r.getCliente() != null && clientes.containsKey(r.getCliente().getIdCliente())) {
                r.setCliente(clientes.get(r.getCliente().getIdCliente()));
            }
            repostajesMap.put(r.getIdRepostaje(), r);
        }
        return repostajesMap;
    }

    private List<Repostaje> obtenerListaRepostajes() {
        if (!Files.exists(RutaRepostajes)) {
            return new ArrayList<>();
        }
        try {
            if (Files.size(RutaRepostajes) == 0) {
                return new ArrayList<>();
            }
            return mapper.readValue(RutaRepostajes.toFile(), new TypeReference<List<Repostaje>>() {});
        } catch (IOException e) {
            System.err.println("Error al leer repostajes.json: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
