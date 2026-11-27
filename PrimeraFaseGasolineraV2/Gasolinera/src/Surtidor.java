enum EstadoSurtidor { OPERATIVO, AVERIADO }

public class Surtidor {

    private Integer idSurtidor; // null = aún no persistido
    private String codigoEquipo; // referencia de negocio, única en la empresa
    private int numeroVisible; // único dentro de su estación
    private TipoCombustible combustible;
    private EstadoSurtidor estado;
    private Estacion estacion;

    public Surtidor(Integer idSurtidor, String codigoEquipo, int numeroVisible,
                    TipoCombustible combustible, EstadoSurtidor estado, Estacion estacion) {
        this.idSurtidor = idSurtidor;
        this.codigoEquipo = codigoEquipo.trim();
        this.numeroVisible = numeroVisible;
        this.combustible = combustible;
        this.estado = estado;
        this.estacion = estacion;
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
        this.codigoEquipo = codigoEquipo.trim();
    }

    public int getNumeroVisible() {
        return numeroVisible;
    }

    public void setNumeroVisible(int numeroVisible) {
        this.numeroVisible = numeroVisible;
    }

    public TipoCombustible getCombustible() {
        return combustible;
    }

    public void setCombustible(TipoCombustible combustible) {
        this.combustible = combustible;
    }

    public EstadoSurtidor getEstado() {
        return estado;
    }

    public void setEstado(EstadoSurtidor estado) {
        this.estado = estado;
    }

    public Estacion getEstacion() {
        return estacion;
    }

    public void setEstacion(Estacion estacion) {
        this.estacion = estacion;
    }

    @Override
    public String toString() {
        return String.format("%-5s %-15s %-10s %-15s %-12s %s%n",
                idSurtidor, codigoEquipo, numeroVisible, combustible, estado,
                estacion.getCodigoInterno());
    }
}
