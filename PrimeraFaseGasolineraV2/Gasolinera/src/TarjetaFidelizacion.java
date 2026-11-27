import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TarjetaFidelizacion {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private Integer idTarjeta; // null = aún no persistida
    private String numeroTarjeta; // referencia de negocio, única
    private LocalDate fechaEmision;
    private Cliente titular; // null = tarjeta aún no entregada
    private LocalDate fechaAsignacion; // null mientras no tenga titular

    public TarjetaFidelizacion(Integer idTarjeta, String numeroTarjeta, LocalDate fechaEmision) {
        this.idTarjeta = idTarjeta;
        this.numeroTarjeta = numeroTarjeta.trim();
        this.fechaEmision = fechaEmision;
        this.titular = null;
        this.fechaAsignacion = null;
    }

    /**
     * Devuelve false en lugar de lanzar una excepción: la capa de servicio
     * decidirá cómo informar al usuario. Así el constructor sigue limpio.
     */
    public boolean asignarTitular(Cliente titular, LocalDate fechaAsignacion) {
        if (titular == null || fechaAsignacion == null) {
            return false;
        }
        if (estaAsignada()) {
            return false;
        }
        this.titular = titular;
        this.fechaAsignacion = fechaAsignacion;
        return true;
    }

    // Sin setTitular: la fecha de asignación no puede quedar separada del titular.
    public boolean estaAsignada() {
        return titular != null;
    }

    public Integer getIdTarjeta() {
        return idTarjeta;
    }

    public void setIdTarjeta(Integer idTarjeta) {
        this.idTarjeta = idTarjeta;
    }

    public String getNumeroTarjeta() {
        return numeroTarjeta;
    }

    public void setNumeroTarjeta(String numeroTarjeta) {
        this.numeroTarjeta = numeroTarjeta.trim();
    }

    public LocalDate getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(LocalDate fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public Cliente getTitular() {
        return titular;
    }

    public LocalDate getFechaAsignacion() {
        return fechaAsignacion;
    }

    @Override
    public String toString() {
        String textoTitular = titular == null ? "-" : String.valueOf(titular.getIdCliente());
        String textoAsignacion = fechaAsignacion == null
                ? "-" : fechaAsignacion.format(FORMATO_FECHA);
        return String.format("%-5s %-15s %-12s %-12s %s%n",
                idTarjeta, numeroTarjeta, fechaEmision.format(FORMATO_FECHA),
                textoAsignacion, textoTitular);
    }
}
