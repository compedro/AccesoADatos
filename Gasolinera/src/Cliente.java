import org.w3c.dom.ls.LSOutput;

public class Cliente {
    private int idCliente;
    private String nombre;
    private String telefono;
    private String matricula;

    public Cliente(int idCliente, String nombre, String telefono, String matricula) {
        this.idCliente = idCliente;
        this.nombre = nombre.trim();
        this.telefono = telefono.trim();
        this.matricula = matricula.trim().toUpperCase();
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre.trim();
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono.trim();
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula.trim().toUpperCase();
    }

    public String toCsv(){
        return getIdCliente()+";"+getNombre()+";"+getTelefono()+";"+getMatricula(); }

    public static Cliente fromCsv(String linea){
        String lineaPartida []   = linea.split(";");
        return new Cliente(Integer.parseInt(lineaPartida[0]),lineaPartida[1],lineaPartida[2], lineaPartida[3]);
        }

    @Override
    public String toString() {
        return "Cliente{" +
                "idCliente=" + idCliente +
                ", nombre='" + nombre + '\'' +
                ", telefono='" + telefono + '\'' +
                ", matricula='" + matricula + '\'' +
                '}';
    }
}
