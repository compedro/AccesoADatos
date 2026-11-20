package modelo;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Representa una tarjeta de fidelización.
 * Puede existir emitida en stock sin cliente o asignada a un único cliente registrado.
 */
public class TarjetaFidelizacion {
    private Integer idTarjeta;
    private String numeroTarjeta;
    private Cliente cliente;
    private LocalDate fechaEmision;
    private LocalDate fechaAsignacion;
    private int puntos;
    private EstadoTarjeta estado;

    public TarjetaFidelizacion() {
        this.puntos = 0;
        this.estado = EstadoTarjeta.EMITIDA;
    }

    public TarjetaFidelizacion(String numeroTarjeta, LocalDate fechaEmision) {
        this(null, numeroTarjeta, null, fechaEmision, null, 0, EstadoTarjeta.EMITIDA);
    }

    public TarjetaFidelizacion(Integer idTarjeta, String numeroTarjeta, Cliente cliente, LocalDate fechaEmision,
                               LocalDate fechaAsignacion, int puntos, EstadoTarjeta estado) {
        this.idTarjeta = idTarjeta;
        this.numeroTarjeta = Objects.requireNonNull(numeroTarjeta, "El número de tarjeta no puede ser nulo");
        this.fechaEmision = Objects.requireNonNull(fechaEmision, "La fecha de emisión no puede ser nula");
        this.puntos = Math.max(0, puntos);
        this.estado = Objects.requireNonNull(estado, "El estado no puede ser nulo");

        if (cliente != null) {
            asignarACliente(cliente, fechaAsignacion != null ? fechaAsignacion : fechaEmision);
        }
    }

    public void asignarACliente(Cliente cliente, LocalDate fechaAsignacion) {
        Objects.requireNonNull(cliente, "El cliente no puede ser nulo");
        Objects.requireNonNull(fechaAsignacion, "La fecha de asignación no puede ser nula");
        if (fechaAsignacion.isBefore(this.fechaEmision)) {
            throw new IllegalArgumentException("La fecha de asignación no puede ser anterior a la de emisión");
        }
        if (this.cliente != null && !this.cliente.equals(cliente)) {
            throw new IllegalStateException("La tarjeta ya está asignada a otro cliente");
        }
        this.cliente = cliente;
        this.fechaAsignacion = fechaAsignacion;
        this.estado = EstadoTarjeta.ASIGNADA;
        cliente.asignarTarjeta(this);
    }

    public boolean estaAsignada() {
        return this.cliente != null && this.estado == EstadoTarjeta.ASIGNADA;
    }

    public void sumarPuntos(int puntosASumar) {
        if (puntosASumar > 0) {
            this.puntos += puntosASumar;
        }
    }

    public Integer getIdTarjeta() {
        return idTarjeta;
    }

    public void setIdTarjeta(Integer idTarjeta) {
        this.idTarjeta = idTarjeta;
    }

    public String getNumeroTarjeta() {
        return numeroTarjeta;
    }

    public void setNumeroTarjeta(String numeroTarjeta) {
        this.numeroTarjeta = numeroTarjeta;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public LocalDate getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(LocalDate fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public LocalDate getFechaAsignacion() {
        return fechaAsignacion;
    }

    public int getPuntos() {
        return puntos;
    }

    public void setPuntos(int puntos) {
        this.puntos = Math.max(0, puntos);
    }

    public EstadoTarjeta getEstado() {
        return estado;
    }

    public void setEstado(EstadoTarjeta estado) {
        this.estado = estado;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TarjetaFidelizacion that = (TarjetaFidelizacion) o;
        return Objects.equals(numeroTarjeta, that.numeroTarjeta);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numeroTarjeta);
    }

    @Override
    public String toString() {
        return "TarjetaFidelizacion{" +
                "numero='" + numeroTarjeta + '\'' +
                ", puntos=" + puntos +
                ", estado=" + estado +
                ", titular=" + (cliente != null ? cliente.getNombre() : "stock libre") +
                '}';
    }
}
