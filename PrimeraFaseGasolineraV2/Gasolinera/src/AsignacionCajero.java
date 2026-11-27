import java.math.BigDecimal;
import java.time.LocalDate;

public class AsignacionCajero extends AsignacionFuncion {

    private BigDecimal limiteDevolucion;

    public AsignacionCajero(Integer idAsignacion, Empleado empleado, LocalDate fechaInicio,
                            LocalDate fechaFin, BigDecimal limiteDevolucion) {
        super(idAsignacion, empleado, TipoFuncion.CAJERO, fechaInicio, fechaFin);
        this.limiteDevolucion = limiteDevolucion;
    }

    // El límite se aplica a una operación; la suma acumulada de devoluciones se controlará en el servicio.
    public boolean puedeAutorizar(BigDecimal importeDevolucion) {
        return importeDevolucion.compareTo(limiteDevolucion) <= 0;
    }

    public BigDecimal getLimiteDevolucion() {
        return limiteDevolucion;
    }

    public void setLimiteDevolucion(BigDecimal limiteDevolucion) {
        this.limiteDevolucion = limiteDevolucion;
    }
}
