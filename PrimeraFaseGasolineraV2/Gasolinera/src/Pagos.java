import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

enum MedioPago { EFECTIVO, TARJETA }

public class Pagos {

    private static final DateTimeFormatter FORMATO_FECHA_HORA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private Integer idPago; // null = aún no persistido
    private BigDecimal importe;
    private LocalDateTime fechaHora;
    private MedioPago medioPago;

    public Pagos(Integer idPago, BigDecimal importe, LocalDateTime fechaHora, MedioPago medioPago) {
        this.idPago = idPago;
        this.importe = importe;
        this.fechaHora = fechaHora;
        this.medioPago = medioPago;
    }

    public Integer getIdPago() {
        return idPago;
    }

    public void setIdPago(Integer idPago) {
        this.idPago = idPago;
    }

    public BigDecimal getImporte() {
        return importe;
    }

    public void setImporte(BigDecimal importe) {
        this.importe = importe;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public MedioPago getMedioPago() {
        return medioPago;
    }

    public void setMedioPago(MedioPago medioPago) {
        this.medioPago = medioPago;
    }

    @Override
    public String toString() {
        return String.format("%-5s %-10s %-20s %-10s%n",
                idPago, importe, fechaHora.format(FORMATO_FECHA_HORA), medioPago);
    }
}
