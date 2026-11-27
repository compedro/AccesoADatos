import java.math.BigDecimal;
import java.time.LocalDate;

enum TipoCombustible {DIESEL, GASOLINA_95, GASOLINA_98}

public class Repostaje {

    private Integer idRepostaje; // null = aún no persistido
    private Cliente cliente;
    private LocalDate fecha;
    private BigDecimal importe;
    private BigDecimal litros;
    private TipoCombustible combustible;

    public Repostaje(Integer idRepostaje, Cliente cliente, LocalDate fecha,
                     BigDecimal importe, BigDecimal litros, TipoCombustible combustible) {
        this.idRepostaje = idRepostaje;
        this.cliente = cliente;
        this.fecha = fecha;
        this.importe = importe;
        this.litros = litros;
        this.combustible = combustible;
    }

    public Integer getIdRepostaje() {
        return idRepostaje;
    }

    public void setIdRepostaje(Integer idRepostaje) {
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

    public BigDecimal getImporte() {
        return importe;
    }

    public void setImporte(BigDecimal importe) {
        this.importe = importe;
    }

    public BigDecimal getLitros() {
        return litros;
    }

    public void setLitros(BigDecimal litros) {
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
