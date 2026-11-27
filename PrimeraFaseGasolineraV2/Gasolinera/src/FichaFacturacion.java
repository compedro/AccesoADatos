public class FichaFacturacion {

    private Integer idFicha; // null = aún no persistida
    private String codigoConfiguracion; // referencia de negocio, única
    private String serieTickets;
    private int siguienteNumero;
    private String textoPie;

    public FichaFacturacion(Integer idFicha, String codigoConfiguracion, String serieTickets,
                            int siguienteNumero, String textoPie) {
        this.idFicha = idFicha;
        this.codigoConfiguracion = codigoConfiguracion.trim();
        this.serieTickets = serieTickets.trim();
        this.siguienteNumero = siguienteNumero;
        this.textoPie = textoPie.trim();
    }

    public String generarNumeroTicket() {
        // La regla de numeración vive aquí, no en la vista ni en el gestor de ventas.
        String numero = String.format("%s-%04d", serieTickets, siguienteNumero);
        siguienteNumero++;
        return numero;
    }

    public Integer getIdFicha() {
        return idFicha;
    }

    public void setIdFicha(Integer idFicha) {
        this.idFicha = idFicha;
    }

    public String getCodigoConfiguracion() {
        return codigoConfiguracion;
    }

    public void setCodigoConfiguracion(String codigoConfiguracion) {
        this.codigoConfiguracion = codigoConfiguracion.trim();
    }

    public String getSerieTickets() {
        return serieTickets;
    }

    public void setSerieTickets(String serieTickets) {
        this.serieTickets = serieTickets.trim();
    }

    public int getSiguienteNumero() {
        return siguienteNumero;
    }

    public void setSiguienteNumero(int siguienteNumero) {
        this.siguienteNumero = siguienteNumero;
    }

    public String getTextoPie() {
        return textoPie;
    }

    public void setTextoPie(String textoPie) {
        this.textoPie = textoPie.trim();
    }

    @Override
    public String toString() {
        return String.format("%-5s %-15s %-8s %-10s %s%n",
                idFicha, codigoConfiguracion, serieTickets, siguienteNumero, textoPie);
    }
}
