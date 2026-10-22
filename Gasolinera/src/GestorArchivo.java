
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Date;


public class GestorArchivo {
    private Path rutaArchivo;

    public GestorArchivo(String rutaArchivo) {
        this.rutaArchivo = Path.of(rutaArchivo);
    }

    public String leerArchivo() {

        try { return Files.readString(rutaArchivo, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }


    public void escribirArchivo(String datos) {

        try { Files.writeString(rutaArchivo, datos+ "\n", StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        GestorArchivo gestorArchivo = new GestorArchivo("repostajes.csv");
//        gestorArchivo.escribirArchivo("18/9/2026,test, gasoil,20,20");

        Cliente cliente = new Cliente(1,"Ivan","910000001", "BCD 1234");
        Repostaje repostaje = new Repostaje(1, cliente,new Date(),50,40, TipoCombustible.DIESEL);
        gestorArchivo.escribirArchivo(repostaje.toString());
        System.out.println(gestorArchivo.leerArchivo());
    }
}