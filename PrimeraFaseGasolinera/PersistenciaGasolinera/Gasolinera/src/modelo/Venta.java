package modelo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Representa una venta (ticket de caja) emitida en una estación de servicio.
 * Permite ventas identificadas y compras anónimas (cliente nulo).
 */
public class Venta {
    private Integer idVenta;
    private String referenciaTicket;
    private Estacion estacion;
    private Empleado empleado;
    private Cliente cliente; // Nulo en caso de compra anónima
    private LocalDateTime fechaHora;
    private EstadoPago estadoPago;
    private final List<LineaVenta> lineas = new ArrayList<>();
    private final List<Pago> pagos = new ArrayList<>();

    public Venta() {
        this.estadoPago = EstadoPago.PENDIENTE;
    }

    public Venta(String referenciaTicket, Estacion estacion, Empleado empleado, Cliente cliente, LocalDateTime fechaHora) {
        this(null, referenciaTicket, estacion, empleado, cliente, fechaHora, EstadoPago.PENDIENTE);
    }

    public Venta(Integer idVenta, String referenciaTicket, Estacion estacion, Empleado empleado,
                 Cliente cliente, LocalDateTime fechaHora, EstadoPago estadoPago) {
        this.idVenta = idVenta;
        this.referenciaTicket = Objects.requireNonNull(referenciaTicket, "La referencia del ticket no puede ser nula");
        this.estacion = Objects.requireNonNull(estacion, "La estación no puede ser nula");
        this.empleado = Objects.requireNonNull(empleado, "El empleado no puede ser nulo");
        this.cliente = cliente; // Puede ser nulo (venta anónima)
        this.fechaHora = Objects.requireNonNull(fechaHora, "La fecha y hora no pueden ser nulas");
        this.estadoPago = Objects.requireNonNull(estadoPago, "El estado de pago no puede ser nulo");
    }

    public LineaVenta agregarLinea(Producto producto, BigDecimal cantidad) {
        Objects.requireNonNull(producto, "El producto no puede ser nulo");
        Objects.requireNonNull(cantidad, "La cantidad no puede ser nula");

        int siguienteOrden = this.lineas.size() + 1;
        LineaVenta nuevaLinea = new LineaVenta(this, siguienteOrden, producto, cantidad, producto.getPrecioVenta());
        this.lineas.add(nuevaLinea);
        return nuevaLinea;
    }

    public BigDecimal calcularTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (LineaVenta lv : lineas) {
            total = total.add(lv.getImporte());
        }
        return total.setScale(2, RoundingMode.HALF_UP);
    }

    public Pago registrarPago(MedioPago medioPago, BigDecimal importe, LocalDateTime fechaHora, String refTransaccion) {
        Objects.requireNonNull(medioPago, "El medio de pago no puede ser nulo");
        Objects.requireNonNull(importe, "El importe del pago no puede ser nulo");
        if (importe.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El importe del pago debe ser mayor que cero");
        }

        BigDecimal pendiente = getSaldoPendiente();
        if (importe.compareTo(pendiente) > 0) {
            throw new IllegalArgumentException(String.format("El importe del pago (%.2f€) supera el saldo pendiente (%.2f€)", importe, pendiente));
        }

        Pago nuevoPago = new Pago(this, medioPago, importe, fechaHora != null ? fechaHora : LocalDateTime.now(), refTransaccion);
        this.pagos.add(nuevoPago);

        if (getSaldoPendiente().compareTo(BigDecimal.ZERO) == 0) {
            this.estadoPago = EstadoPago.PAGADA;
        }

        return nuevoPago;
    }

    public BigDecimal getTotalPagado() {
        BigDecimal total = BigDecimal.ZERO;
        for (Pago p : pagos) {
            total = total.add(p.getImporte());
        }
        return total.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getSaldoPendiente() {
        return calcularTotal().subtract(getTotalPagado()).setScale(2, RoundingMode.HALF_UP);
    }

    public boolean estaCompletamentePagada() {
        return !lineas.isEmpty() && getSaldoPendiente().compareTo(BigDecimal.ZERO) == 0;
    }

    public Integer getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(Integer idVenta) {
        this.idVenta = idVenta;
    }

    public String getReferenciaTicket() {
        return referenciaTicket;
    }

    public void setReferenciaTicket(String referenciaTicket) {
        this.referenciaTicket = referenciaTicket;
    }

    public Estacion getEstacion() {
        return estacion;
    }

    public void setEstacion(Estacion estacion) {
        this.estacion = estacion;
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public EstadoPago getEstadoPago() {
        return estadoPago;
    }

    public void setEstadoPago(EstadoPago estadoPago) {
        this.estadoPago = estadoPago;
    }

    public List<LineaVenta> getLineas() {
        return Collections.unmodifiableList(lineas);
    }

    public List<Pago> getPagos() {
        return Collections.unmodifiableList(pagos);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Venta venta = (Venta) o;
        return Objects.equals(referenciaTicket, venta.referenciaTicket);
    }

    @Override
    public int hashCode() {
        return Objects.hash(referenciaTicket);
    }

    @Override
    public String toString() {
        return "Venta{" +
                "ticket='" + referenciaTicket + '\'' +
                ", total=" + calcularTotal() +
                "€, estado=" + estadoPago +
                ", cliente=" + (cliente != null ? cliente.getNombre() : "ANÓNIMO") +
                ", lineas=" + lineas.size() +
                '}';
    }
}
