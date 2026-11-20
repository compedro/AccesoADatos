package modelo;

import java.util.Objects;

/**
 * Representa un equipo de dispensación de combustible.
 * Pertenece a una única estación y posee un número visible único dentro de ella.
 */
public class Surtidor {
    private Integer idSurtidor;
    private String codigoEquipo;
    private int numeroVisible;
    private String tipoCombustible;
    private EstadoServicio estadoServicio;

    public Surtidor() {
        this.estadoServicio = EstadoServicio.ACTIVO;
    }

    public Surtidor(String codigoEquipo, int numeroVisible, String tipoCombustible) {
        this(null, codigoEquipo, numeroVisible, tipoCombustible, EstadoServicio.ACTIVO);
    }

    public Surtidor(Integer idSurtidor, String codigoEquipo, int numeroVisible, String tipoCombustible, EstadoServicio estadoServicio) {
        this.idSurtidor = idSurtidor;
        this.codigoEquipo = Objects.requireNonNull(codigoEquipo, "El código de equipo no puede ser nulo");
        if (numeroVisible <= 0) {
            throw new IllegalArgumentException("El número visible del surtidor debe ser positivo");
        }
        this.numeroVisible = numeroVisible;
        this.tipoCombustible = Objects.requireNonNull(tipoCombustible, "El tipo de combustible no puede ser nulo");
        this.estadoServicio = Objects.requireNonNull(estadoServicio, "El estado de servicio no puede ser nulo");
    }

    public Integer getIdSurtidor() {
        return idSurtidor;
    }

    public void setIdSurtidor(Integer idSurtidor) {
        this.idSurtidor = idSurtidor;
    }

    public String getCodigoEquipo() {
        return codigoEquipo;
    }

    public void setCodigoEquipo(String codigoEquipo) {
        this.codigoEquipo = codigoEquipo;
    }

    public int getNumeroVisible() {
        return numeroVisible;
    }

    public void setNumeroVisible(int numeroVisible) {
        if (numeroVisible <= 0) {
            throw new IllegalArgumentException("El número visible debe ser mayor que cero");
        }
        this.numeroVisible = numeroVisible;
    }

    public String getTipoCombustible() {
        return tipoCombustible;
    }

    public void setTipoCombustible(String tipoCombustible) {
        this.tipoCombustible = tipoCombustible;
    }

    public EstadoServicio getEstadoServicio() {
        return estadoServicio;
    }

    public void setEstadoServicio(EstadoServicio estadoServicio) {
        this.estadoServicio = estadoServicio;
    }

    @Override
    public String toString() {
        return "Surtidor{" +
                "codigo='" + codigoEquipo + '\'' +
                ", num=" + numeroVisible +
                ", combustible='" + tipoCombustible + '\'' +
                ", estado=" + estadoServicio +
                '}';
    }
}
