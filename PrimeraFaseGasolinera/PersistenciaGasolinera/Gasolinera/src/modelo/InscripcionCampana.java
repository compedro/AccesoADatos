package modelo;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Asociación entre un Cliente registrado y una Campaña en la que participa.
 */
public class InscripcionCampana {
    private Integer idInscripcionCampana;
    private Campana campana;
    private Cliente cliente;
    private LocalDate fechaInscripcion;

    public InscripcionCampana() {}

    public InscripcionCampana(Campana campana, Cliente cliente, LocalDate fechaInscripcion) {
        this(null, campana, cliente, fechaInscripcion);
    }

    public InscripcionCampana(Integer idInscripcionCampana, Campana campana, Cliente cliente, LocalDate fechaInscripcion) {
        this.idInscripcionCampana = idInscripcionCampana;
        this.campana = Objects.requireNonNull(campana, "La campaña no puede ser nula");
        this.cliente = Objects.requireNonNull(cliente, "El cliente no puede ser nulo");
        this.fechaInscripcion = Objects.requireNonNull(fechaInscripcion, "La fecha de inscripción no puede ser nula");
    }

    public Integer getIdInscripcionCampana() {
        return idInscripcionCampana;
    }

    public void setIdInscripcionCampana(Integer idInscripcionCampana) {
        this.idInscripcionCampana = idInscripcionCampana;
    }

    public Campana getCampana() {
        return campana;
    }

    public void setCampana(Campana campana) {
        this.campana = campana;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public LocalDate getFechaInscripcion() {
        return fechaInscripcion;
    }

    public void setFechaInscripcion(LocalDate fechaInscripcion) {
        this.fechaInscripcion = fechaInscripcion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InscripcionCampana that = (InscripcionCampana) o;
        return Objects.equals(campana, that.campana) && Objects.equals(cliente, that.cliente);
    }

    @Override
    public int hashCode() {
        return Objects.hash(campana, cliente);
    }

    @Override
    public String toString() {
        return "InscripcionCampana{" +
                "campana=" + campana.getCodigoCampana() +
                ", cliente=" + cliente.getCodigoCliente() +
                ", fecha=" + fechaInscripcion +
                '}';
    }
}
