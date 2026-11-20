package modelo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Representa una estación de servicio de la red de gasolineras.
 */
public class Estacion {
    private Integer idEstacion;
    private String codigoEstacion;
    private String nombreComercial;
    private String direccion;
    private String telefono;
    private LocalDate fechaApertura;
    private FichaConfiguracion fichaConfiguracion;
    private final List<Surtidor> surtidores = new ArrayList<>();

    public Estacion() {}

    public Estacion(String codigoEstacion, String nombreComercial, String direccion, String telefono, LocalDate fechaApertura) {
        this(null, codigoEstacion, nombreComercial, direccion, telefono, fechaApertura);
    }

    public Estacion(Integer idEstacion, String codigoEstacion, String nombreComercial, String direccion, String telefono, LocalDate fechaApertura) {
        this.idEstacion = idEstacion;
        this.codigoEstacion = Objects.requireNonNull(codigoEstacion, "El código de estación no puede ser nulo");
        this.nombreComercial = Objects.requireNonNull(nombreComercial, "El nombre comercial no puede ser nulo");
        this.direccion = Objects.requireNonNull(direccion, "La dirección no puede ser nula");
        this.telefono = Objects.requireNonNull(telefono, "El teléfono no puede ser nulo");
        this.fechaApertura = Objects.requireNonNull(fechaApertura, "La fecha de apertura no puede ser nula");
    }

    public void asignarFichaConfiguracion(FichaConfiguracion ficha) {
        this.fichaConfiguracion = Objects.requireNonNull(ficha, "La ficha de configuración no puede ser nula");
    }

    public void agregarSurtidor(Surtidor surtidor) {
        Objects.requireNonNull(surtidor, "El surtidor no puede ser nulo");
        for (Surtidor s : surtidores) {
            if (s.getNumeroVisible() == surtidor.getNumeroVisible()) {
                throw new IllegalArgumentException("Ya existe un surtidor con el número visible " + surtidor.getNumeroVisible() + " en esta estación");
            }
            if (s.getCodigoEquipo().equalsIgnoreCase(surtidor.getCodigoEquipo())) {
                throw new IllegalArgumentException("Ya existe un surtidor con el código de equipo " + surtidor.getCodigoEquipo());
            }
        }
        this.surtidores.add(surtidor);
    }

    public Integer getIdEstacion() {
        return idEstacion;
    }

    public void setIdEstacion(Integer idEstacion) {
        this.idEstacion = idEstacion;
    }

    public String getCodigoEstacion() {
        return codigoEstacion;
    }

    public void setCodigoEstacion(String codigoEstacion) {
        this.codigoEstacion = codigoEstacion;
    }

    public String getNombreComercial() {
        return nombreComercial;
    }

    public void setNombreComercial(String nombreComercial) {
        this.nombreComercial = nombreComercial;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public LocalDate getFechaApertura() {
        return fechaApertura;
    }

    public void setFechaApertura(LocalDate fechaApertura) {
        this.fechaApertura = fechaApertura;
    }

    public FichaConfiguracion getFichaConfiguracion() {
        return fichaConfiguracion;
    }

    public List<Surtidor> getSurtidores() {
        return Collections.unmodifiableList(surtidores);
    }

    @Override
    public String toString() {
        return "Estacion{" +
                "codigo='" + codigoEstacion + '\'' +
                ", nombre='" + nombreComercial + '\'' +
                ", surtidores=" + surtidores.size() +
                '}';
    }
}
