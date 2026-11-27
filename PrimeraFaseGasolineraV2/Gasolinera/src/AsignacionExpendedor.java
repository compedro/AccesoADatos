import java.time.LocalDate;

public class AsignacionExpendedor extends AsignacionFuncion {

    private String numeroAcreditacion;
    private LocalDate fechaCaducidad;

    public AsignacionExpendedor(Integer idAsignacion, Empleado empleado, LocalDate fechaInicio,
                                LocalDate fechaFin, String numeroAcreditacion, LocalDate fechaCaducidad) {
        super(idAsignacion, empleado, TipoFuncion.EXPENDEDOR, fechaInicio, fechaFin);
        this.numeroAcreditacion = numeroAcreditacion.trim();
        this.fechaCaducidad = fechaCaducidad;
    }

    // No se llama estaVigente: ese nombre en la base ya significa "periodo abierto".
    public boolean acreditacionVigente(LocalDate fecha) {
        return !fecha.isAfter(fechaCaducidad);
    }

    public String getNumeroAcreditacion() {
        return numeroAcreditacion;
    }

    public void setNumeroAcreditacion(String numeroAcreditacion) {
        this.numeroAcreditacion = numeroAcreditacion.trim();
    }

    public LocalDate getFechaCaducidad() {
        return fechaCaducidad;
    }

    public void setFechaCaducidad(LocalDate fechaCaducidad) {
        this.fechaCaducidad = fechaCaducidad;
    }
}
