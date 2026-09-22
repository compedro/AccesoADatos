import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

enum TipoCombustible {DIESEL, GASOLINA_95, GASOLINA_98}

public class Repostaje {

    private int idRepostaje;
    private Cliente cliente;
    private LocalDate fecha;
    private double importe;
    private double litros;
    private TipoCombustible combustible;

    public Repostaje(int idRepostaje, Cliente cliente, LocalDate fecha, double importe, double litros, TipoCombustible combustible) {
        this.idRepostaje = idRepostaje;
        this.cliente = cliente;
        this.fecha = fecha;
        this.importe = importe;
        this.litros = litros;
        this.combustible = combustible;
    }

    public int getIdRepostaje() {
        return idRepostaje;
    }

    public void setIdRepostaje(int idRepostaje) {
        this.idRepostaje = idRepostaje;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public double getImporte() {
        return importe;
    }

    public void setImporte(double importe) {
        this.importe = importe;
    }

    public double getLitros() {
        return litros;
    }

    public void setLitros(double litros) {
        this.litros = litros;
    }

    public TipoCombustible getCombustible() {
        return combustible;
    }

    public void setCombustible(TipoCombustible combustible) {
        this.combustible = combustible;
    }



    public static Repostaje fromCsv(String linea, Map<Integer, Cliente> clientes) {
        String lineaPartida[] = linea.split(";");
        int idClienteBuscado = Integer.parseInt((lineaPartida[1]));
        Cliente clienteEncontrado = clientes.get(idClienteBuscado);

        if (clienteEncontrado != null) {
            return new Repostaje(
                    Integer.parseInt(lineaPartida[0]),
                    clienteEncontrado,
                    LocalDate.parse(lineaPartida[2],
                            DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    Double.parseDouble(lineaPartida[3]),
                    Double.parseDouble(lineaPartida[4]),
                    TipoCombustible.valueOf(lineaPartida[5])
            );
        }
        return null;
    }

    @Override
    public String toString() {
//        return "Repostaje{" +
//                "idRepostaje=" + idRepostaje +
//                ", cliente=" + cliente +
//                ", fecha=" + fecha +
//                ", importe=" + importe +
//                ", litros=" + litros +
//                ", combustible=" + combustible +
//                '}';
        return idRepostaje + "," + cliente + "," + fecha + "," + importe + "," + litros + "," + combustible;
    }
}
