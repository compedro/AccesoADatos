import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Estacion {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private Integer idEstacion; // null = aún no persistida
    private String codigoInterno; // referencia de negocio, única en la empresa
    private String nombreComercial;
    private String direccion;
    private String telefono;
    private LocalDate fechaApertura;
    private FichaFacturacion fichaFacturacion; // relación 1:1, opción A

    public Estacion(Integer idEstacion, String codigoInterno, String nombreComercial,
                    String direccion, String telefono, LocalDate fechaApertura,
                    FichaFacturacion fichaFacturacion) {
        this.idEstacion = idEstacion;
        this.codigoInterno = codigoInterno.trim();
        this.nombreComercial = nombreComercial.trim();
        this.direccion = direccion.trim();
        this.telefono = telefono.trim();
        this.fechaApertura = fechaApertura;
        this.fichaFacturacion = fichaFacturacion;
    }

    public Integer getIdEstacion() {
        return idEstacion;
    }

    public void setIdEstacion(Integer idEstacion) {
        this.idEstacion = idEstacion;
    }

    public String getCodigoInterno() {
        return codigoInterno;
    }

    public void setCodigoInterno(String codigoInterno) {
        this.codigoInterno = codigoInterno.trim();
    }

    public String getNombreComercial() {
        return nombreComercial;
    }

    public void setNombreComercial(String nombreComercial) {
        this.nombreComercial = nombreComercial.trim();
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion.trim();
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono.trim();
    }

    public LocalDate getFechaApertura() {
        return fechaApertura;
    }

    public void setFechaApertura(LocalDate fechaApertura) {
        this.fechaApertura = fechaApertura;
    }

    public FichaFacturacion getFichaFacturacion() {
        return fichaFacturacion;
    }

    public void setFichaFacturacion(FichaFacturacion fichaFacturacion) {
        this.fichaFacturacion = fichaFacturacion;
    }

    @Override
    public String toString() {
        return String.format("%-5s %-12s %-20s %-25s %-15s %-12s%n",
                idEstacion, codigoInterno, nombreComercial, direccion, telefono,
                fechaApertura.format(FORMATO_FECHA));
    }
}
