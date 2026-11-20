package modelo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Representa una devolución de mercancía asociada a una línea específica de venta.
 */
public class Devolucion {
    private Integer idDevolucion;
    private LineaVenta lineaVenta;
    private Empleado empleado;
    private BigDecimal cantidad;
    private String motivo;
    private LocalDateTime fechaHora;
    private BigDecimal importeReintegrado;
    private boolean aptoParaVenta;

    public Devolucion() {}

    public Devolucion(LineaVenta lineaVenta, Empleado empleado, BigDecimal cantidad, String motivo,
                      LocalDateTime fechaHora, BigDecimal importeReintegrado, boolean aptoParaVenta) {
        this(null, lineaVenta, empleado, cantidad, motivo, fechaHora, importeReintegrado, aptoParaVenta);
    }

    public Devolucion(Integer idDevolucion, LineaVenta lineaVenta, Empleado empleado, BigDecimal cantidad,
                      String motivo, LocalDateTime fechaHora, BigDecimal importeReintegrado, boolean aptoParaVenta) {
        this.idDevolucion = idDevolucion;
        this.lineaVenta = Objects.requireNonNull(lineaVenta, "La línea de venta no puede ser nula");
        this.empleado = Objects.requireNonNull(empleado, "El empleado no puede ser nulo");
        Objects.requireNonNull(cantidad, "La cantidad no puede ser nula");
        if (cantidad.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("La cantidad devuelta debe ser positiva: " + cantidad);
        }
        this.cantidad = cantidad;
        this.motivo = Objects.requireNonNull(motivo, "El motivo no puede ser nulo");
        this.fechaHora = Objects.requireNonNull(fechaHora, "La fecha y hora no pueden ser nulas");
        Objects.requireNonNull(importeReintegrado, "El importe reintegrado no puede ser nulo");
        if (importeReintegrado.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El importe reintegrado debe ser positivo: " + importeReintegrado);
        }
        this.importeReintegrado = importeReintegrado.setScale(2, RoundingMode.HALF_UP);
        this.aptoParaVenta = aptoParaVenta;
    }

    public Integer getIdDevolucion() {
        return idDevolucion;
    }

    public void setIdDevolucion(Integer idDevolucion) {
        this.idDevolucion = idDevolucion;
    }

    public LineaVenta getLineaVenta() {
        return lineaVenta;
    }

    public void setLineaVenta(LineaVenta lineaVenta) {
        this.lineaVenta = lineaVenta;
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
    }

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public BigDecimal getImporteReintegrado() {
        return importeReintegrado;
    }

    public void setImporteReintegrado(BigDecimal importeReintegrado) {
        this.importeReintegrado = importeReintegrado;
    }

    public boolean isAptoParaVenta() {
        return aptoParaVenta;
    }

    public void setAptoParaVenta(boolean aptoParaVenta) {
        this.aptoParaVenta = aptoParaVenta;
    }

    @Override
    public String toString() {
        return "Devolucion{" +
                "linea=" + lineaVenta.getIdLineaVenta() +
                ", cant=" + cantidad +
                ", reintegro=" + importeReintegrado +
                "€, apto=" + aptoParaVenta +
                ", fecha=" + fechaHora +
                '}';
    }
}
