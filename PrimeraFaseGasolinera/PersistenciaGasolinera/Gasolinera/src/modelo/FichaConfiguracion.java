package modelo;

import java.util.Objects;

/**
 * Ficha de configuración de facturación para emisión de tickets.
 * Relación 1:1 estricta con una Estación.
 */
public class FichaConfiguracion {
    private Integer idFichaConfiguracion;
    private String codigoConfiguracion;
    private String serieTickets;
    private int siguienteNumero;
    private String pieJustificante;

    public FichaConfiguracion() {
        this.siguienteNumero = 1;
    }

    public FichaConfiguracion(String codigoConfiguracion, String serieTickets, int siguienteNumero, String pieJustificante) {
        this(null, codigoConfiguracion, serieTickets, siguienteNumero, pieJustificante);
    }

    public FichaConfiguracion(Integer idFichaConfiguracion, String codigoConfiguracion, String serieTickets, int siguienteNumero, String pieJustificante) {
        this.idFichaConfiguracion = idFichaConfiguracion;
        this.codigoConfiguracion = Objects.requireNonNull(codigoConfiguracion, "El código de configuración no puede ser nulo");
        this.serieTickets = Objects.requireNonNull(serieTickets, "La serie de tickets no puede ser nula");
        if (siguienteNumero <= 0) {
            throw new IllegalArgumentException("El siguiente número de ticket debe ser mayor que cero");
        }
        this.siguienteNumero = siguienteNumero;
        this.pieJustificante = pieJustificante;
    }

    public synchronized int obtenerYSiguienteNumero() {
        int actual = this.siguienteNumero;
        this.siguienteNumero++;
        return actual;
    }

    public Integer getIdFichaConfiguracion() {
        return idFichaConfiguracion;
    }

    public void setIdFichaConfiguracion(Integer idFichaConfiguracion) {
        this.idFichaConfiguracion = idFichaConfiguracion;
    }

    public String getCodigoConfiguracion() {
        return codigoConfiguracion;
    }

    public void setCodigoConfiguracion(String codigoConfiguracion) {
        this.codigoConfiguracion = codigoConfiguracion;
    }

    public String getSerieTickets() {
        return serieTickets;
    }

    public void setSerieTickets(String serieTickets) {
        this.serieTickets = serieTickets;
    }

    public int getSiguienteNumero() {
        return siguienteNumero;
    }

    public void setSiguienteNumero(int siguienteNumero) {
        if (siguienteNumero <= 0) {
            throw new IllegalArgumentException("El siguiente número debe ser mayor que cero");
        }
        this.siguienteNumero = siguienteNumero;
    }

    public String getPieJustificante() {
        return pieJustificante;
    }

    public void setPieJustificante(String pieJustificante) {
        this.pieJustificante = pieJustificante;
    }

    @Override
    public String toString() {
        return "FichaConfiguracion{" +
                "id=" + idFichaConfiguracion +
                ", codigo='" + codigoConfiguracion + '\'' +
                ", serie='" + serieTickets + '\'' +
                ", siguiente=" + siguienteNumero +
                '}';
    }
}
