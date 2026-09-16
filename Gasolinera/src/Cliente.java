public class Cliente {
   private int idCliente;
   private String nombre ;
   private String telefono;
   private String matricula;

    public Cliente(int idCliente, String nombre, String telefono, String matricula) {
        this.idCliente = idCliente;
        this.nombre = nombre;
        this.telefono = telefono;
        this.matricula = matricula;
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
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
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
