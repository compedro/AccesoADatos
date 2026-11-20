package modelo;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Representa una incidencia operativa ocurrida en una estación de servicio.
 */
public class Incidencia {
    private Integer idIncidencia;
    private String referenciaIncidencia;
    private Estacion estacion;
    private Empleado empleadoResponsable; // Puede ser nulo inicialmente
    private String descripcion;
    private LocalDateTime fechaHoraApertura;
    private EstadoIncidencia estado;

    public Incidencia() {
        this.estado = EstadoIncidencia.ABIERTA;
    }

    public Incidencia(String referenciaIncidencia, Estacion estacion, String descripcion, LocalDateTime fechaHoraApertura) {
        this(null, referenciaIncidencia, estacion, null, descripcion, fechaHoraApertura, EstadoIncidencia.ABIERTA);
    }

    public Incidencia(Integer idIncidencia, String referenciaIncidencia, Estacion estacion, Empleado empleadoResponsable,
                      String descripcion, LocalDateTime fechaHoraApertura, EstadoIncidencia estado) {
        this.idIncidencia = idIncidencia;
        this.referenciaIncidencia = Objects.requireNonNull(referenciaIncidencia, "La referencia de incidencia no puede ser nula");
        this.estacion = Objects.requireNonNull(estacion, "La estación no puede ser nula");
        this.empleadoResponsable = empleadoResponsable;
        this.descripcion = Objects.requireNonNull(descripcion, "La descripción no puede ser nula");
        this.fechaHoraApertura = Objects.requireNonNull(fechaHoraApertura, "La fecha y hora de apertura no pueden ser nulas");
        this.estado = Objects.requireNonNull(estado, "El estado no puede ser nulo");
    }

    public void asignarResponsable(Empleado responsable) {
        this.empleadoResponsable = responsable;
        if (responsable != null && this.estado == EstadoIncidencia.ABIERTA) {
            this.estado = EstadoIncidencia.EN_PROCESO;
        }
    }

    public void cerrar() {
        this.estado = EstadoIncidencia.CERRADA;
    }

    public Integer getIdIncidencia() {
        return idIncidencia;
    }

    public void setIdIncidencia(Integer idIncidencia) {
        this.idIncidencia = idIncidencia;
    }

    public String getReferenciaIncidencia() {
        return referenciaIncidencia;
    }

    public void setReferenciaIncidencia(String referenciaIncidencia) {
        this.referenciaIncidencia = referenciaIncidencia;
    }

    public Estacion getEstacion() {
        return estacion;
    }

    public void setEstacion(Estacion estacion) {
        this.estacion = estacion;
    }

    public Empleado getEmpleadoResponsable() {
        return empleadoResponsable;
    }

    public void setEmpleadoResponsable(Empleado empleadoResponsable) {
        this.empleadoResponsable = empleadoResponsable;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDateTime getFechaHoraApertura() {
        return fechaHoraApertura;
    }

    public void setFechaHoraApertura(LocalDateTime fechaHoraApertura) {
        this.fechaHoraApertura = fechaHoraApertura;
    }

    public EstadoIncidencia getEstado() {
        return estado;
    }

    public void setEstado(EstadoIncidencia estado) {
        this.estado = estado;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Incidencia that = (Incidencia) o;
        return Objects.equals(referenciaIncidencia, that.referenciaIncidencia);
    }

    @Override
    public int hashCode() {
        return Objects.hash(referenciaIncidencia);
    }

    @Override
    public String toString() {
        return "Incidencia{" +
                "ref='" + referenciaIncidencia + '\'' +
                ", estacion=" + estacion.getCodigoEstacion() +
                ", estado=" + estado +
                ", responsable=" + (empleadoResponsable != null ? empleadoResponsable.getNombre() : "sin asignar") +
                '}';
    }
}
