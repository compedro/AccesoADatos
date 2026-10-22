import java.lang.reflect.Constructor;

public class Cliente {
   private int IdCliente;
   private String Nombre;
   private String Telefono;
   private String Matricula;


    public Cliente(int idCliente, String nombre, String telefono, String matricula) {
        IdCliente = idCliente;
        Nombre = nombre;
        Telefono = telefono;
        Matricula = matricula;
    }

    public int getIdCliente() {
        return IdCliente;
    }

    public void setIdCliente(int idCliente) {
        IdCliente = idCliente;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public String getTelefono() {
        return Telefono;
    }

    public void setTelefono(String telefono) {
        Telefono = telefono;
    }

    public String getMatricula() {
        return Matricula;
    }

    public void setMatricula(String matricula) {
        Matricula = matricula;
    }

    @Override
    public String toString() {
        return "Cliente{" +
                "IdCliente=" + IdCliente +
                ", Nombre='" + Nombre + '\'' +
                ", Telefono='" + Telefono + '\'' +
                ", Matricula='" + Matricula + '\'' +
                '}';
    }
}
