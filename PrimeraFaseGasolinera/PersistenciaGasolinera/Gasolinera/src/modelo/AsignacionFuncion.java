package modelo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Periodo durante el cual un empleado desempeña una función específica con sus condiciones.
 */
public class AsignacionFuncion {
    private Integer idAsignacionFuncion;
    private Empleado empleado;
    private Funcion funcion;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    // Atributos específicos según tipo de función:
    private BigDecimal limiteDevolucion;              // Específico para CAJERO
    private String numAcreditacionCombustible;         // Específico para EXPENDEDOR
    private LocalDate fechaCaducidadAcreditacion;      // Específico para EXPENDEDOR
    private BigDecimal presupuestoCompras;             // Específico para RESPONSABLE_TIENDA
    private Integer nivelAutorizacion;                 // Específico para JEFE_ESTACION

    public AsignacionFuncion() {}

    public AsignacionFuncion(Empleado empleado, Funcion funcion, LocalDate fechaInicio) {
        this(null, empleado, funcion, fechaInicio, null);
    }

    public AsignacionFuncion(Integer idAsignacionFuncion, Empleado empleado, Funcion funcion, LocalDate fechaInicio, LocalDate fechaFin) {
        this.idAsignacionFuncion = idAsignacionFuncion;
        this.empleado = Objects.requireNonNull(empleado, "El empleado no puede ser nulo");
        this.funcion = Objects.requireNonNull(funcion, "La función no puede ser nula");
        this.fechaInicio = Objects.requireNonNull(fechaInicio, "La fecha de inicio no puede ser nula");
        if (fechaFin != null && fechaFin.isBefore(fechaInicio)) {
            throw new IllegalArgumentException("La fecha de fin no puede ser anterior a la de inicio");
        }
        this.fechaFin = fechaFin;
    }

    public boolean estaActiva() {
        return this.fechaFin == null;
    }

    public void finalizar(LocalDate fechaFin) {
        Objects.requireNonNull(fechaFin, "La fecha de finalización no puede ser nula");
        if (fechaFin.isBefore(this.fechaInicio)) {
            throw new IllegalArgumentException("La fecha de finalización no puede ser anterior al inicio");
        }
        this.fechaFin = fechaFin;
    }

    public Integer getIdAsignacionFuncion() {
        return idAsignacionFuncion;
    }

    public void setIdAsignacionFuncion(Integer idAsignacionFuncion) {
        this.idAsignacionFuncion = idAsignacionFuncion;
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
    }

    public Funcion getFuncion() {
        return funcion;
    }

    public void setFuncion(Funcion funcion) {
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

    public BigDecimal getLimiteDevolucion() {
        return limiteDevolucion;
    }

    public void setLimiteDevolucion(BigDecimal limiteDevolucion) {
        this.limiteDevolucion = limiteDevolucion;
    }

    public String getNumAcreditacionCombustible() {
        return numAcreditacionCombustible;
    }

    public void setNumAcreditacionCombustible(String numAcreditacionCombustible) {
        this.numAcreditacionCombustible = numAcreditacionCombustible;
    }

    public LocalDate getFechaCaducidadAcreditacion() {
        return fechaCaducidadAcreditacion;
    }

    public void setFechaCaducidadAcreditacion(LocalDate fechaCaducidadAcreditacion) {
        this.fechaCaducidadAcreditacion = fechaCaducidadAcreditacion;
    }

    public BigDecimal getPresupuestoCompras() {
        return presupuestoCompras;
    }

    public void setPresupuestoCompras(BigDecimal presupuestoCompras) {
        this.presupuestoCompras = presupuestoCompras;
    }

    public Integer getNivelAutorizacion() {
        return nivelAutorizacion;
    }

    public void setNivelAutorizacion(Integer nivelAutorizacion) {
        this.nivelAutorizacion = nivelAutorizacion;
    }

    @Override
    public String toString() {
        return "AsignacionFuncion{" +
                "funcion=" + funcion.getCodigoFuncion() +
                ", inicio=" + fechaInicio +
                ", activa=" + estaActiva() +
                '}';
    }
}
