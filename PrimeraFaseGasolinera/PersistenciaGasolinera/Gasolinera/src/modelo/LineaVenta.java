package modelo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Línea de detalle de un ticket de venta.
 * Almacena la cantidad vendida, el precio realmente aplicado (histórico) y su importe.
 */
public class LineaVenta {
    private Integer idLineaVenta;
    private Venta venta;
    private int numeroOrden;
    private Producto producto;
    private BigDecimal cantidad;
    private BigDecimal precioUnitarioAplicado;
    private BigDecimal importe;
    private final List<Devolucion> devoluciones = new ArrayList<>();

    public LineaVenta() {}

    public LineaVenta(Venta venta, int numeroOrden, Producto producto, BigDecimal cantidad, BigDecimal precioUnitarioAplicado) {
        this(null, venta, numeroOrden, producto, cantidad, precioUnitarioAplicado, null);
    }

    public LineaVenta(Integer idLineaVenta, Venta venta, int numeroOrden, Producto producto,
                      BigDecimal cantidad, BigDecimal precioUnitarioAplicado, BigDecimal importe) {
        this.idLineaVenta = idLineaVenta;
        this.venta = Objects.requireNonNull(venta, "La venta asociada no puede ser nula");
        if (numeroOrden <= 0) {
            throw new IllegalArgumentException("El número de orden debe ser positivo");
        }
        this.numeroOrden = numeroOrden;
        this.producto = Objects.requireNonNull(producto, "El producto no puede ser nulo");
        Objects.requireNonNull(cantidad, "La cantidad no puede ser nula");
        producto.validarCantidad(cantidad);
        this.cantidad = cantidad;

        Objects.requireNonNull(precioUnitarioAplicado, "El precio unitario aplicado no puede ser nulo");
        if (precioUnitarioAplicado.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El precio unitario debe ser mayor que cero");
        }
        this.precioUnitarioAplicado = precioUnitarioAplicado.setScale(2, RoundingMode.HALF_UP);

        if (importe != null) {
            this.importe = importe.setScale(2, RoundingMode.HALF_UP);
        } else {
            this.importe = this.precioUnitarioAplicado.multiply(this.cantidad).setScale(2, RoundingMode.HALF_UP);
        }
    }

    public Devolucion registrarDevolucion(Empleado empleado, BigDecimal cantidadADevolver, String motivo, LocalDateTime fechaHora, boolean aptoParaVenta) {
        Objects.requireNonNull(empleado, "El empleado que acepta la devolución no puede ser nulo");
        Objects.requireNonNull(cantidadADevolver, "La cantidad a devolver no puede ser nula");
        Objects.requireNonNull(motivo, "El motivo no puede ser nulo");

        // Validar que la cantidad respete las reglas de modalidad del producto
        this.producto.validarCantidad(cantidadADevolver);

        // Validar que la cantidad acumulada no exceda la comprada originalmente
        BigDecimal disponible = getCantidadDisponibleDevolucion();
        if (cantidadADevolver.compareTo(disponible) > 0) {
            throw new IllegalArgumentException(String.format(
                    "No se puede devolver %s: solo restan %s unidades/kg disponibles para devolución de esta línea",
                    cantidadADevolver, disponible
            ));
        }

        // Calcular importe proporcional reintegrado según el precio cobrado originalmente
        BigDecimal reintegro = this.precioUnitarioAplicado.multiply(cantidadADevolver).setScale(2, RoundingMode.HALF_UP);

        Devolucion dev = new Devolucion(this, empleado, cantidadADevolver, motivo, fechaHora != null ? fechaHora : LocalDateTime.now(), reintegro, aptoParaVenta);
        this.devoluciones.add(dev);
        return dev;
    }

    public BigDecimal getCantidadDevueltaTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (Devolucion d : devoluciones) {
            total = total.add(d.getCantidad());
        }
        return total;
    }

    public BigDecimal getImporteReintegradoTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (Devolucion d : devoluciones) {
            total = total.add(d.getImporteReintegrado());
        }
        return total.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getCantidadDisponibleDevolucion() {
        return this.cantidad.subtract(getCantidadDevueltaTotal());
    }

    public Integer getIdLineaVenta() {
        return idLineaVenta;
    }

    public void setIdLineaVenta(Integer idLineaVenta) {
        this.idLineaVenta = idLineaVenta;
    }

    public Venta getVenta() {
        return venta;
    }

    public void setVenta(Venta venta) {
        this.venta = venta;
    }

    public int getNumeroOrden() {
        return numeroOrden;
    }

    public void setNumeroOrden(int numeroOrden) {
        this.numeroOrden = numeroOrden;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecioUnitarioAplicado() {
        return precioUnitarioAplicado;
    }

    public void setPrecioUnitarioAplicado(BigDecimal precioUnitarioAplicado) {
        this.precioUnitarioAplicado = precioUnitarioAplicado;
    }

    public BigDecimal getImporte() {
        return importe;
    }

    public void setImporte(BigDecimal importe) {
        this.importe = importe;
    }

    public List<Devolucion> getDevoluciones() {
        return Collections.unmodifiableList(devoluciones);
    }

    @Override
    public String toString() {
        return "LineaVenta{" +
                "orden=" + numeroOrden +
                ", prod=" + producto.getReferenciaProducto() +
                ", cant=" + cantidad +
                ", precioCobrado=" + precioUnitarioAplicado +
                "€, importe=" + importe + "€}";
    }
}
