import java.util.Date;

public class Repostaje {

    private int IdRepostaje;
    private int IdCliente;
    private Date Fecha;
    private double  Importe;
    private double Combustible;


    public Repostaje(int idRepostaje, int idCliente, Date fecha, double importe, double combustible) {
        IdRepostaje = idRepostaje;
        IdCliente = idCliente;
        Fecha = fecha;
        Importe = importe;
        Combustible = combustible;
    }

    public int getIdRepostaje() {
        return IdRepostaje;
    }

    public void setIdRepostaje(int idRepostaje) {
        IdRepostaje = idRepostaje;
    }

    public int getIdCliente() {
        return IdCliente;
    }

    public void setIdCliente(int idCliente) {
        IdCliente = idCliente;
    }

    public Date getFecha() {
        return Fecha;
    }

    public void setFecha(Date fecha) {
        Fecha = fecha;
    }

    public double getImporte() {
        return Importe;
    }

    public void setImporte(double importe) {
        Importe = importe;
    }

    public double getCombustible() {
        return Combustible;
    }

    public void setCombustible(double combustible) {
        Combustible = combustible;
    }
}
