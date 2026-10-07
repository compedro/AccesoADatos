import java.time.LocalDate;

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


    @Override
    public String toString() {
        return String.format("%-15s %-20s %-15s %-15s %-15s %-10s%n",idRepostaje, cliente.getNombre(), fecha, importe, litros, combustible);
    }
}
