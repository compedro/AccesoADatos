import java.time.LocalDate;

public class AsignacionJefeEstacion extends AsignacionFuncion {

    private int nivelAutorizacion;

    public AsignacionJefeEstacion(Integer idAsignacion, Empleado empleado, LocalDate fechaInicio,
                                  LocalDate fechaFin, int nivelAutorizacion) {
        super(idAsignacion, empleado, TipoFuncion.JEFE_ESTACION, fechaInicio, fechaFin);
        this.nivelAutorizacion = nivelAutorizacion;
    }

    // Convenio: un nivel más alto incluye las operaciones de los niveles inferiores.
    public boolean tieneNivel(int nivelRequerido) {
        return nivelAutorizacion >= nivelRequerido;
    }

    public int getNivelAutorizacion() {
        return nivelAutorizacion;
    }

    public void setNivelAutorizacion(int nivelAutorizacion) {
        this.nivelAutorizacion = nivelAutorizacion;
    }
}
