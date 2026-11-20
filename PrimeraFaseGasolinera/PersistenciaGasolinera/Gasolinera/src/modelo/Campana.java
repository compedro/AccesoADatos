package modelo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Campaña promocional con vigencia temporal.
 */
public class Campana {
    private Integer idCampana;
    private String codigoCampana;
    private String nombre;
    private String descripcion;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private final List<InscripcionCampana> inscripciones = new ArrayList<>();

    public Campana() {}

    public Campana(String codigoCampana, String nombre, LocalDate fechaInicio, LocalDate fechaFin) {
        this(null, codigoCampana, nombre, null, fechaInicio, fechaFin);
    }

    public Campana(Integer idCampana, String codigoCampana, String nombre, String descripcion, LocalDate fechaInicio, LocalDate fechaFin) {
        this.idCampana = idCampana;
        this.codigoCampana = Objects.requireNonNull(codigoCampana, "El código de campaña no puede ser nulo");
        this.nombre = Objects.requireNonNull(nombre, "El nombre de la campaña no puede ser nulo");
        this.descripcion = descripcion;
        this.fechaInicio = Objects.requireNonNull(fechaInicio, "La fecha de inicio no puede ser nula");
        this.fechaFin = Objects.requireNonNull(fechaFin, "La fecha de fin no puede ser nula");
        if (fechaFin.isBefore(fechaInicio)) {
            throw new IllegalArgumentException("La fecha de fin no puede ser anterior a la de inicio");
        }
    }

    public boolean estaVigente(LocalDate fecha) {
        Objects.requireNonNull(fecha, "La fecha a comprobar no puede ser nula");
        return !fecha.isBefore(fechaInicio) && !fecha.isAfter(fechaFin);
    }

    public InscripcionCampana inscribirCliente(Cliente cliente, LocalDate fechaInscripcion) {
        Objects.requireNonNull(cliente, "El cliente no puede ser nulo");
        Objects.requireNonNull(fechaInscripcion, "La fecha de inscripción no puede ser nula");

        for (InscripcionCampana insc : inscripciones) {
            if (insc.getCliente().equals(cliente)) {
                throw new IllegalStateException("El cliente ya está inscrito en la campaña " + codigoCampana);
            }
        }

        InscripcionCampana nuevaInscripcion = new InscripcionCampana(this, cliente, fechaInscripcion);
        this.inscripciones.add(nuevaInscripcion);
        return nuevaInscripcion;
    }

    public Integer getIdCampana() {
        return idCampana;
    }

    public void setIdCampana(Integer idCampana) {
        this.idCampana = idCampana;
    }

    public String getCodigoCampana() {
        return codigoCampana;
    }

    public void setCodigoCampana(String codigoCampana) {
        this.codigoCampana = codigoCampana;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public List<InscripcionCampana> getInscripciones() {
        return Collections.unmodifiableList(inscripciones);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Campana campana = (Campana) o;
        return Objects.equals(codigoCampana, campana.codigoCampana);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigoCampana);
    }

    @Override
    public String toString() {
        return "Campana{" +
                "codigo='" + codigoCampana + '\'' +
                ", nombre='" + nombre + '\'' +
                ", vigencia=" + fechaInicio + " a " + fechaFin +
                ", inscritos=" + inscripciones.size() +
                '}';
    }
}
