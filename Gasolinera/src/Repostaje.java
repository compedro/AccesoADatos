import java.time.LocalDate;
import java.util.Date;
import java.util.List;

enum TipoCombustible { DIESEL, GASOLINA_95, GASOLINA_98}

public class Repostaje {

    private int idRepostaje;
    private Cliente cliente;
    private Date fecha;
    private double  importe;
    private double litros;
    private TipoCombustible combustible;

    public Repostaje(int idRepostaje, Cliente cliente, Date fecha, double importe, double litros, TipoCombustible combustible) {
        this.idRepostaje = idRepostaje;
        this.cliente = cliente;
        this.fecha = fecha;
        this.importe = importe;
        this.litros = litros;
        this.combustible = combustible;
    }

    public int getIdRepostaje() {
        return idRepostaje;
    }

    public void setIdRepostaje(int idRepostaje) {
        this.idRepostaje = idRepostaje;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public double getImporte() {
        return importe;
    }

    public void setImporte(double importe) {
        this.importe = importe;
    }

    public double getLitros() {
        return litros;
    }

    public void setLitros(double litros) {
        this.litros = litros;
    }

    public TipoCombustible getCombustible() {
        return combustible;
    }

    public void setCombustible(TipoCombustible combustible) {
        this.combustible = combustible;
    }

    public String toCsv() {
        return getIdRepostaje() + ";" +getCliente().getIdCliente() + ";" + getFecha() + ";" + getImporte() + ";" + getLitros() + ";" + getCombustible();
    }

    public static Repostaje fromCsv(String linea, List<Cliente> clientes){
        String lineaPartida [] = linea.split(";");
        return new Repostaje( Integer.parseInt(lineaPartida[0]),Cliente lineaPartida[1], LocalDate.parse(lineaPartida[2]), Double.parseDouble(lineaPartida[3]),Double.parseDouble( lineaPartida[4]),TipoCombustible.valueOf(lineaPartida[5]));
    }

    @Override
    public String toString() {
//        return "Repostaje{" +
//                "idRepostaje=" + idRepostaje +
//                ", cliente=" + cliente +
//                ", fecha=" + fecha +
//                ", importe=" + importe +
//                ", litros=" + litros +
//                ", combustible=" + combustible +
//                '}';
        return idRepostaje + ","+ cliente+ ","+ fecha+ ","+ importe+ ","+ litros+ ","+ combustible;
    }
}
