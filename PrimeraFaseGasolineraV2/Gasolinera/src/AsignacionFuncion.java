import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

enum TipoFuncion { CAJERO, EXPENDEDOR, RESPONSABLE_TIENDA, JEFE_ESTACION }

public abstract class AsignacionFuncion {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private Integer idAsignacion; // null = aún no persistida
    private Empleado empleado;
    private TipoFuncion funcion;
    private LocalDate fechaInicio;
    private LocalDate fechaFin; // null = periodo todavía abierto

    public AsignacionFuncion(Integer idAsignacion, Empleado empleado, TipoFuncion funcion,
                             LocalDate fechaInicio, LocalDate fechaFin) {
        this.idAsignacion = idAsignacion;
        this.empleado = empleado;
        this.funcion = funcion;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }

    // Comparar una fecha con un fin null sería un error; aquí "abierta" siempre está vigente.
    public boolean estaVigente(LocalDate fecha) {
        if (fecha.isBefore(fechaInicio)) {
            return false;
        }
        return fechaFin == null || !fecha.isAfter(fechaFin);
    }

    // Retirar cierra el periodo, nunca lo borra: el historial debe permanecer.
    public boolean retirar(LocalDate fecha) {
        if (fechaFin != null) {
            return false;
        }
        this.fechaFin = fecha;
        return true;
    }

    public Integer getIdAsignacion() {
        return idAsignacion;
    }

    public void setIdAsignacion(Integer idAsignacion) {
        this.idAsignacion = idAsignacion;
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
    }

    public TipoFuncion getFuncion() {
        return funcion;
    }

    public void setFuncion(TipoFuncion funcion) {
        this.funcion = funcion;
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

    @Override
    public String toString() {
        String fin = fechaFin == null ? "abierta" : fechaFin.format(FORMATO_FECHA);
        return String.format("%-5s %-20s %-12s %-12s %s%n",
                idAsignacion, funcion, fechaInicio.format(FORMATO_FECHA), fin,
                empleado.getNumeroInterno());
    }
}
