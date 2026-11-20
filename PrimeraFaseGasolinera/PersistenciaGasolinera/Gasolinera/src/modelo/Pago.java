package modelo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Representa un pago asociado a un ticket de venta.
 * Soporta cobros fraccionados o múltiples medios de pago.
 */
public class Pago {
    private Integer idPago;
    private Venta venta;
    private MedioPago medioPago;
    private BigDecimal importe;
    private LocalDateTime fechaHora;
    private String referenciaTransaccion;

    public Pago() {}

    public Pago(Venta venta, MedioPago medioPago, BigDecimal importe, LocalDateTime fechaHora, String referenciaTransaccion) {
        this(null, venta, medioPago, importe, fechaHora, referenciaTransaccion);
    }

    public Pago(Integer idPago, Venta venta, MedioPago medioPago, BigDecimal importe, LocalDateTime fechaHora, String referenciaTransaccion) {
        this.idPago = idPago;
        this.venta = Objects.requireNonNull(venta, "La venta no puede ser nula");
        this.medioPago = Objects.requireNonNull(medioPago, "El medio de pago no puede ser nulo");
        setImporte(importe);
        this.fechaHora = Objects.requireNonNull(fechaHora, "La fecha y hora no pueden ser nulas");
        this.referenciaTransaccion = referenciaTransaccion;
    }

    public Integer getIdPago() {
        return idPago;
    }

    public void setIdPago(Integer idPago) {
        this.idPago = idPago;
    }

    public Venta getVenta() {
        return venta;
    }

    public void setVenta(Venta venta) {
        this.venta = venta;
    }

    public MedioPago getMedioPago() {
        return medioPago;
    }

    public void setMedioPago(MedioPago medioPago) {
        this.medioPago = medioPago;
    }

    public BigDecimal getImporte() {
        return importe;
    }

    public void setImporte(BigDecimal importe) {
        Objects.requireNonNull(importe, "El importe no puede ser nulo");
        if (importe.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El importe del pago debe ser mayor que cero: " + importe);
        }
        this.importe = importe.setScale(2, RoundingMode.HALF_UP);
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getReferenciaTransaccion() {
        return referenciaTransaccion;
    }

    public void setReferenciaTransaccion(String referenciaTransaccion) {
        this.referenciaTransaccion = referenciaTransaccion;
    }

    @Override
    public String toString() {
        return "Pago{" +
                "medio=" + medioPago +
                ", importe=" + importe +
                "€, fecha=" + fechaHora +
                (referenciaTransaccion != null ? ", ref='" + referenciaTransaccion + '\'' : "") +
                '}';
    }
}
