public class Cliente {
    private Integer idCliente; // null = aún no persistido; evita el centinela 0
    private String codigoCliente; // referencia de negocio, única
    private String nombre;
    private String telefono;
    private String matricula;

    public Cliente(Integer idCliente, String codigoCliente, String nombre, String telefono, String matricula) {
        this.idCliente = idCliente;
        this.codigoCliente = codigoCliente.trim();
        this.nombre = nombre.trim();
        this.telefono = telefono.trim();
        this.matricula = matricula.trim().toUpperCase();
    }

    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public String getCodigoCliente() {
        return codigoCliente;
    }

    public void setCodigoCliente(String codigoCliente) {
        this.codigoCliente = codigoCliente.trim();
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


    @Override
    public String toString() {
        return String.format("%-5s %-12s %-20s %-15s %-10s%n",
                idCliente, codigoCliente, nombre, telefono, matricula);
    }
}
