import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Empleado {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private Integer idEmpleado; // null = aún no persistido
    private String numeroInterno; // referencia de negocio, única en la empresa
    private String documentoIdentidad;
    private String nombre;
    private String telefono;
    private String email;
    private LocalDate fechaIncorporacion;
    private Estacion estacion; // adscripción actual
    private Empleado supervisor; // puede ser null; autorreferencia
    private List<AsignacionFuncion> asignaciones; // una por periodo; nunca se elimina

    public Empleado(Integer idEmpleado, String numeroInterno, String documentoIdentidad,
                    String nombre, String telefono, String email, LocalDate fechaIncorporacion,
                    Estacion estacion, Empleado supervisor) {
        this.idEmpleado = idEmpleado;
        this.numeroInterno = numeroInterno.trim();
        this.documentoIdentidad = documentoIdentidad.trim();
        this.nombre = nombre.trim();
        this.telefono = telefono.trim();
        this.email = email.trim();
        this.fechaIncorporacion = fechaIncorporacion;
        this.estacion = estacion;
        this.supervisor = supervisor;
        this.asignaciones = new ArrayList<>();
    }

    public Integer getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(Integer idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public String getNumeroInterno() {
        return numeroInterno;
    }

    public void setNumeroInterno(String numeroInterno) {
        this.numeroInterno = numeroInterno.trim();
    }

    public String getDocumentoIdentidad() {
        return documentoIdentidad;
    }

    public void setDocumentoIdentidad(String documentoIdentidad) {
        this.documentoIdentidad = documentoIdentidad.trim();
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email.trim();
    }

    public LocalDate getFechaIncorporacion() {
        return fechaIncorporacion;
    }

    public void setFechaIncorporacion(LocalDate fechaIncorporacion) {
        this.fechaIncorporacion = fechaIncorporacion;
    }

    public Estacion getEstacion() {
        return estacion;
    }

    public void setEstacion(Estacion estacion) {
        this.estacion = estacion;
    }

    public Empleado getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(Empleado supervisor) {
        this.supervisor = supervisor;
    }

    public void asignarFuncion(AsignacionFuncion asignacion) {
        asignacion.setEmpleado(this);
        asignaciones.add(asignacion);
    }

    public boolean retirarFuncion(TipoFuncion funcion, LocalDate fecha) {
        for (AsignacionFuncion asignacion : asignaciones) {
            if (asignacion.getFuncion() == funcion && asignacion.getFechaFin() == null) {
                return asignacion.retirar(fecha);
            }
        }
        return false;
    }

    public boolean tieneFuncionVigente(TipoFuncion funcion, LocalDate fecha) {
        for (AsignacionFuncion asignacion : asignaciones) {
            if (asignacion.getFuncion() == funcion && asignacion.estaVigente(fecha)) {
                return true;
            }
        }
        return false;
    }

    public List<AsignacionFuncion> getAsignaciones() {
        return asignaciones;
    }

    @Override
    public String toString() {
        String textoSupervisor = supervisor == null ? "-" : supervisor.getNumeroInterno();
        return String.format("%-5s %-12s %-20s %-15s %-25s %-12s %s%n",
                idEmpleado, numeroInterno, nombre, telefono, email,
                fechaIncorporacion.format(FORMATO_FECHA), textoSupervisor);
    }
}
