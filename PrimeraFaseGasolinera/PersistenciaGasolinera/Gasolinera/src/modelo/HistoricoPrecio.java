package modelo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Registro histórico de auditoría generado tras un cambio real de precio de venta en catálogo.
 */
public class HistoricoPrecio {
    private Integer idHistoricoPrecio;
    private Producto producto;
    private BigDecimal precioAnterior;
    private BigDecimal precioNuevo;
    private LocalDateTime fechaModificacion;

    public HistoricoPrecio() {}

    public HistoricoPrecio(Producto producto, BigDecimal precioAnterior, BigDecimal precioNuevo, LocalDateTime fechaModificacion) {
        this(null, producto, precioAnterior, precioNuevo, fechaModificacion);
    }

    public HistoricoPrecio(Integer idHistoricoPrecio, Producto producto, BigDecimal precioAnterior, BigDecimal precioNuevo, LocalDateTime fechaModificacion) {
        this.idHistoricoPrecio = idHistoricoPrecio;
        this.producto = Objects.requireNonNull(producto, "El producto no puede ser nulo");
        this.precioAnterior = Objects.requireNonNull(precioAnterior, "El precio anterior no puede ser nulo");
        this.precioNuevo = Objects.requireNonNull(precioNuevo, "El precio nuevo no puede ser nulo");
        if (precioAnterior.compareTo(precioNuevo) == 0) {
            throw new IllegalArgumentException("El precio anterior y nuevo no pueden ser idénticos en un cambio de precio");
        }
        this.fechaModificacion = Objects.requireNonNull(fechaModificacion, "La fecha de modificación no puede ser nula");
    }

    public Integer getIdHistoricoPrecio() {
        return idHistoricoPrecio;
    }

    public void setIdHistoricoPrecio(Integer idHistoricoPrecio) {
        this.idHistoricoPrecio = idHistoricoPrecio;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public BigDecimal getPrecioAnterior() {
        return precioAnterior;
    }

    public void setPrecioAnterior(BigDecimal precioAnterior) {
        this.precioAnterior = precioAnterior;
    }

    public BigDecimal getPrecioNuevo() {
        return precioNuevo;
    }

    public void setPrecioNuevo(BigDecimal precioNuevo) {
        this.precioNuevo = precioNuevo;
    }

    public LocalDateTime getFechaModificacion() {
        return fechaModificacion;
    }

    public void setFechaModificacion(LocalDateTime fechaModificacion) {
        this.fechaModificacion = fechaModificacion;
    }

    @Override
    public String toString() {
        return "HistoricoPrecio{" +
                "producto=" + producto.getReferenciaProducto() +
                ", anterior=" + precioAnterior +
                "€, nuevo=" + precioNuevo +
                "€, fecha=" + fechaModificacion +
                '}';
    }
}
