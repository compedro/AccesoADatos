import java.math.BigDecimal;
import java.time.LocalDate;

public class AsignacionResponsableTienda extends AsignacionFuncion {

    private BigDecimal presupuestoAutorizado;

    public AsignacionResponsableTienda(Integer idAsignacion, Empleado empleado, LocalDate fechaInicio,
                                       LocalDate fechaFin, BigDecimal presupuestoAutorizado) {
        super(idAsignacion, empleado, TipoFuncion.RESPONSABLE_TIENDA, fechaInicio, fechaFin);
        this.presupuestoAutorizado = presupuestoAutorizado;
    }

    public boolean puedeComprar(BigDecimal importeCompra) {
        return importeCompra.compareTo(presupuestoAutorizado) <= 0;
    }

    public BigDecimal getPresupuestoAutorizado() {
        return presupuestoAutorizado;
    }

    public void setPresupuestoAutorizado(BigDecimal presupuestoAutorizado) {
        this.presupuestoAutorizado = presupuestoAutorizado;
    }
}
