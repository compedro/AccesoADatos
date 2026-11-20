package modelo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Clase base abstracta para artículos comercializados en la gasolinera y tienda.
 * Define el contrato polimórfico para validación de cantidades y cálculo de importes.
 */
public abstract class Producto {
    private Integer idProducto;
    private String referenciaProducto;
    private String descripcion;
    private BigDecimal precioVenta;
    private EstadoComercializacion estadoComercializacion;

    public Producto() {
        this.estadoComercializacion = EstadoComercializacion.ACTIVO;
    }

    public Producto(String referenciaProducto, String descripcion, BigDecimal precioVenta) {
        this(null, referenciaProducto, descripcion, precioVenta, EstadoComercializacion.ACTIVO);
    }

    public Producto(Integer idProducto, String referenciaProducto, String descripcion, BigDecimal precioVenta, EstadoComercializacion estadoComercializacion) {
        this.idProducto = idProducto;
        this.referenciaProducto = Objects.requireNonNull(referenciaProducto, "La referencia de producto no puede ser nula");
        this.descripcion = Objects.requireNonNull(descripcion, "La descripción no puede ser nula");
        setPrecioVenta(precioVenta);
        this.estadoComercializacion = Objects.requireNonNull(estadoComercializacion, "El estado de comercialización no puede ser nulo");
    }

    /**
     * Retorna la modalidad inmutable del producto (ENVASADO o GRANEL).
     */
    public abstract ModalidadProducto getModalidad();

    /**
     * Valida que la cantidad cumpla las reglas de negocio de su modalidad:
     * - Envasados: enteros estrictos mayores que cero.
     * - Granel: valores mayores que cero compatibles con la precisión de pesada admitida.
     *
     * @throws IllegalArgumentException si la cantidad no es válida
     */
    public abstract void validarCantidad(BigDecimal cantidad) throws IllegalArgumentException;

    /**
     * Calcula el importe exacto para la cantidad especificada aplicando el criterio
     * de redondeo común HALF_UP a 2 decimales.
     */
    public BigDecimal calcularImporte(BigDecimal cantidad) {
        validarCantidad(cantidad);
        return this.precioVenta.multiply(cantidad).setScale(2, RoundingMode.HALF_UP);
    }

    public Integer getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Integer idProducto) {
        this.idProducto = idProducto;
    }

    public String getReferenciaProducto() {
        return referenciaProducto;
    }

    public void setReferenciaProducto(String referenciaProducto) {
        this.referenciaProducto = referenciaProducto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(BigDecimal precioVenta) {
        Objects.requireNonNull(precioVenta, "El precio de venta no puede ser nulo");
        if (precioVenta.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El precio de venta debe ser estrictamente mayor que cero: " + precioVenta);
        }
        this.precioVenta = precioVenta.setScale(2, RoundingMode.HALF_UP);
    }

    public EstadoComercializacion getEstadoComercializacion() {
        return estadoComercializacion;
    }

    public void setEstadoComercializacion(EstadoComercializacion estadoComercializacion) {
        this.estadoComercializacion = estadoComercializacion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Producto producto = (Producto) o;
        return Objects.equals(referenciaProducto, producto.referenciaProducto);
    }

    @Override
    public int hashCode() {
        return Objects.hash(referenciaProducto);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "ref='" + referenciaProducto + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", precio=" + precioVenta +
                "€, modalidad=" + getModalidad() +
                '}';
    }
}
