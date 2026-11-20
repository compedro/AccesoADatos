package modelo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Representa a un trabajador adscrito a una estación de servicio.
 * Incluye relación reflexiva de supervisión e historial de asignaciones de funciones.
 */
public class Empleado {
    private Integer idEmpleado;
    private String codigoEmpleado;
    private String dni;
    private String nombre;
    private String telefono;
    private String email;
    private LocalDate fechaIncorporacion;
    private Estacion estacion;
    private Empleado supervisor;
    private final List<AsignacionFuncion> asignaciones = new ArrayList<>();

    public Empleado() {}

    public Empleado(String codigoEmpleado, String dni, String nombre, LocalDate fechaIncorporacion, Estacion estacion) {
        this(null, codigoEmpleado, dni, nombre, null, null, fechaIncorporacion, estacion, null);
    }

    public Empleado(Integer idEmpleado, String codigoEmpleado, String dni, String nombre, LocalDate fechaIncorporacion, Estacion estacion) {
        this(idEmpleado, codigoEmpleado, dni, nombre, null, null, fechaIncorporacion, estacion, null);
    }

    public Empleado(Integer idEmpleado, String codigoEmpleado, String dni, String nombre, String telefono, String email,
                    LocalDate fechaIncorporacion, Estacion estacion, Empleado supervisor) {
        this.idEmpleado = idEmpleado;
        this.codigoEmpleado = Objects.requireNonNull(codigoEmpleado, "El código de empleado no puede ser nulo");
        this.dni = Objects.requireNonNull(dni, "El DNI no puede ser nulo");
        this.nombre = Objects.requireNonNull(nombre, "El nombre no puede ser nulo");
        this.telefono = telefono;
        this.email = email;
        this.fechaIncorporacion = Objects.requireNonNull(fechaIncorporacion, "La fecha de incorporación no puede ser nula");
        this.estacion = Objects.requireNonNull(estacion, "La estación no puede ser nula");
        asignarSupervisor(supervisor);
    }

    public void asignarSupervisor(Empleado supervisor) {
        if (supervisor != null) {
            if (this.equals(supervisor) || (this.idEmpleado != null && this.idEmpleado.equals(supervisor.getIdEmpleado()))) {
                throw new IllegalArgumentException("Un empleado no puede supervisarse a sí mismo");
            }
        }
        this.supervisor = supervisor;
    }

    public AsignacionFuncion asignarFuncion(Funcion funcion, LocalDate fechaInicio) {
        Objects.requireNonNull(funcion, "La función no puede ser nula");
        Objects.requireNonNull(fechaInicio, "La fecha de inicio no puede ser nula");

        // Validar que no tenga activa ya esta misma función
        for (AsignacionFuncion asig : asignaciones) {
            if (asig.estaActiva() && asig.getFuncion().getCodigoFuncion() == funcion.getCodigoFuncion()) {
                throw new IllegalStateException("El empleado ya tiene activa la función " + funcion.getCodigoFuncion());
            }
        }

        AsignacionFuncion nuevaAsignacion = new AsignacionFuncion(this, funcion, fechaInicio);
        this.asignaciones.add(nuevaAsignacion);
        return nuevaAsignacion;
    }

    public List<AsignacionFuncion> obtenerFuncionesActivas() {
        List<AsignacionFuncion> activas = new ArrayList<>();
        for (AsignacionFuncion a : asignaciones) {
            if (a.estaActiva()) {
                activas.add(a);
            }
        }
        return Collections.unmodifiableList(activas);
    }

    public Integer getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(Integer idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public String getCodigoEmpleado() {
        return codigoEmpleado;
    }

    public void setCodigoEmpleado(String codigoEmpleado) {
        this.codigoEmpleado = codigoEmpleado;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getFechaIncorporacion() {
        return fechaIncorporacion;
    }

    public void setFechaIncorporacion(LocalDate fechaIncorporacion) {
        this.fechaIncorporacion = fechaIncorporacion;
    }

    public Estacion getEstacion() {
        return estacion;
    }

    public void setEstacion(Estacion estacion) {
        this.estacion = estacion;
    }

    public Empleado getSupervisor() {
        return supervisor;
    }

    public List<AsignacionFuncion> getAsignaciones() {
        return Collections.unmodifiableList(asignaciones);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Empleado empleado = (Empleado) o;
        return Objects.equals(codigoEmpleado, empleado.codigoEmpleado);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigoEmpleado);
    }

    @Override
    public String toString() {
        return "Empleado{" +
                "codigo='" + codigoEmpleado + '\'' +
                ", nombre='" + nombre + '\'' +
                ", estacion=" + (estacion != null ? estacion.getCodigoEstacion() : "sin adscribir") +
                '}';
    }
}
