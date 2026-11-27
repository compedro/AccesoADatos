# CONTEXT.md - Documento de Diseño y Contexto

**Estado:** APROBADO  
*(Aprobado por el usuario el 25/11/2026. El mentor puede proponer código respetando el protocolo de modificación de `AGENTS.md`.)*

---

## 1. El Objetivo (¿Qué queremos resolver?)

- **Propósito del proyecto:** Ampliar la aplicación actual de gasolinera (clientes y repostajes en CSV) para gestionar estaciones con configuración de facturación y surtidores, personal con funciones e historial, clientes con tarjetas de fidelización, catálogo de tienda con proveedores y campañas, ventas con líneas y pagos, devoluciones e incidencias; e incorporar MySQL como nueva alternativa de persistencia mediante JDBC sin perder CSV/JSON.
- **Resultado esperado de esta Fase 1:** esquema relacional documentado, scripts `01_esquema.sql`, `02_triggers.sql`, `03_datos.sql` y `04_comprobaciones.sql`, modelo Java del dominio con comportamiento y una demostración breve con las dos modalidades de producto. Proyecto compilable.
- **Alcance y límites:**
  - Incluye: tablas con PK de un único entero autoincremental en todas —incluidas asociaciones, líneas e históricos—, claves foráneas, unicidad de referencias de negocio, trigger MySQL del historial de precios, datos de ejemplo y comprobaciones; clases Java del dominio (más de 15 como referencia orientativa, no como meta rígida); conservación de las alternativas anteriores.
  - Excluye: métodos JDBC de inserción/consulta/actualización/borrado (Fase 2), historial de supervisión, gestiones de depósitos/mangueras/suministros/repostajes detallados, motor de descuentos, triggers opcionales de existencias y bloqueo de líneas, y diagrama entidad-relación (prohibido por el caso).
  - Precondiciones detectadas en el repositorio actual, no resueltas aún: los CSV tienen cabeceras mal formadas e IDs repetidos; no existe implementación JSON aunque el enunciado la da por presente; no existe clase `Pagos`, aunque el caso exige integrarla. No daremos por buena la frase «conservamos CSV y JSON» hasta decidir cómo se sanea esto, sin transformar registros antiguos silenciosamente.

---

## 2. El Enfoque (¿Cómo se puede realizar?)

- **Alternativas consideradas y decididas:**
  - *Funciones del personal:* herencia `Empleado` → `Cajero`, `Expendedor`... frente a composición `Empleado` + `AsignacionFuncion` con periodo y datos propios. Se elige la **composición**: una persona puede tener varias funciones a la vez, retirarlas y recuperarlas con otras condiciones, y el caso prevé nuevas funciones futuras.
  - *Condiciones de cada función:* clase única con campos opcionales frente a clase base más una subclase por función. Se elige la **subclase por función**: cada asignación nace con su condición obligatoria y el compilador impide mezclar campos de funciones distintas. Se acepta el coste de cuatro subclases y de un `switch` por tipo al cargar desde SQL en Fase 2.
  - *Modalidades de producto:* clase única `Producto` con `enum Modalidad` e `if/switch` frente a jerarquía `Producto` → `ProductoEnvasado` / `ProductoGranel`. Se elige la **jerarquía**: `validarCantidad` y `calcularImporte` quedan en la subclase y el gestor de ticket no repite decisiones de venta.
  - *Persistencia:* ampliar `ItfGestorArchivo` frente a mantenerlo para ficheros y crear `ItfRepositorio...` para MySQL. Se elige el **contrato separado**: `escribirArchivo(Path, String)` no tiene sentido en JDBC y mezclar responsabilidades obligaría a reescribir CSV/JSON. Consecuencia aceptada: pequeño refactor para que `GestorVentas` dependa de la interfaz y no de `GestorArchivoCSV`; se pedirá permiso antes de tocarlo.
  - *Tipos numéricos:* `double` con redondeo manual frente a `BigDecimal` con escala 2 y `DECIMAL(p,2)` en MySQL. Se elige **`BigDecimal`**: controla la precisión de pesada y hace explícito el redondeo (`HALF_UP`).
  - *Identificadores en Java:* `int` con `0` como centinela frente a `Integer` nullable. Se elige **`Integer` nullable**: `null` significa «aún no persistido» sin colisionar con IDs válidos.
- **Enfoque seleccionado:** capas `Dominio` / `Persistencia (Itf...)` / `Servicios` / `Presentación`. Dominio con objetos con comportamiento, MySQL como contrato nuevo de repositorio y CSV/JSON intactos salvo el refactor de dependencia autorizado.

---

## 3. Justificación de Decisiones (¿Por qué esta opción y no otra?)

- La composición evita el fallo de la herencia: un empleado puede ser cajero y expendedor simultáneamente y cambiar de rol sin cambiar de identidad ni de autoría de sus ventas.
- La jerarquía de producto cumple el requisito de «comportamiento, no getters/setters»: la validación y el cálculo de importe viven donde está la regla, no en la vista ni en el servicio.
- El contrato de persistencia separado protege las funcionalidades anteriores y respeta `STYLE.md`, que exige desacoplar la persistencia mediante interfaces `Itf...`.
- `BigDecimal` evita los errores binarios de `double`; la referencia de negocio (código de estación, número de tarjeta, referencia comercial, etc.) se mantiene única por separado del ID técnico autoincremental.
- `Integer` nullable distingue la identidad técnica pendiente de asignación de un registro ya guardado.
- La integridad se valida en la capa de entrada/servicio antes de instanciar, o mediante excepción comprobada propia si se decide; **prohibido lanzar `RuntimeException` genéricas en constructores de dominio**.
- Adecuación al nivel: código imperativo y explícito, sin streams, lambdas ni patrones avanzados. `BigDecimal` y el polimorfismo básico se explican antes de aplicarse.

---

## 4. Reglas de negocio: dónde quedan garantizadas

- **Base de datos:** unicidad de códigos de negocio; 1:1 exacto estación↔ficha de facturación; surtidor siempre adscrito a una estación; histórico de funciones; tarjeta 1:1 con cliente; acuerdos proveedor-producto no duplicados; inscripción única por cliente y campaña; orden de línea único por ticket; trigger de precios que registra solo cambios reales (no INSERT, no cambio de descripción) y cuyo historial desaparece con `ROLLBACK`.
- **Pendiente para Fase 2 / no resuelto solo con FK:** alta conjunta estación+ficha; baja del último surtidor; baja del último acuerdo de proveedor o producto; ciclos de supervisión; periodos solapados de una misma función; devoluciones acumuladas que no superen cantidad e importe originales; confirmación de venta cuando la suma de pagos alcanza el importe. Se documentarán como limitaciones explícitas, no como rechazos automáticos.

---

## 5. Criterios de aceptación y comprobaciones

- Scripts ejecutados en orden `01` → `02` → `03` sobre entorno vacío, sin desactivar claves foráneas.
- `04_comprobaciones.sql` separa sentencias que deben fallar de sentencias que deben funcionar, con el resultado esperado comentado.
- Datos de ejemplo: al menos cinco filas por tabla principal, diez líneas de venta repartidas en varios tickets y los casos mínimos del enunciado (varias estaciones con surtidores, tarjetas sin asignar, supervisor con varios subordinados, empleado sin funciones y otro que recupera una función, ambas modalidades, venta con varios pagos, devoluciones parciales sucesivas, cambio de precio con INSERT/UPDATE/ROLLBACK).
- Demostración Java: producto envasado y producto a granel tratados conjuntamente, validando cantidad y calculando importe, sin acceder a MySQL.
- Entregables: `README.md`, `AGENTS.md`, `AUDITORIA_IA.odt` y registro breve de tres intervenciones de IA.

---

## 6. Estado de aprobación

- Aprobado por el usuario el 25/11/2026. Se habilita la propuesta de código, siempre una clase o método a la vez y con confirmación previa antes de editar archivos, según `AGENTS.md`.

---

## 7. Estado de ejecución (actualizado 26/11/2026)

- Completado: `GestorVentas` depende de `ItfGestorArchivo`; CSV saneados; tipos migrados a `Integer` nullable y `BigDecimal` con escala 2 y `HALF_UP`; IDs por máximo + 1 en `GestorVentas.darAltaCliente` y `GestorVentas.procesarPago`.
- Decisión del usuario: no se crean copias `.bak`; los registros eran datos de prueba sin valor. No reabrir este punto.
- Completado: `GestorArchivoJSON` escribe un array JSON con un objeto por línea y conserva los mapas en el gestor (opción A). Compilación y prueba de ida y vuelta verificadas.
- Corregido: `GestorArchivoJSON.cargarRepostajes` vaciaba el mapa compartido con `Main` al usar `this.clientes`; ahora lee por el parámetro `clientesCargados`.
- Corregido: `GestorArchivoJSON.cargarClientes` y `GestorArchivoJSON.cargarRepostajes` descartan la línea `[]`, que es el estado inicial creado por `prepararArchivo`. Sin esta condición el primer arranque lanzaba `StringIndexOutOfBoundsException`.
- Completado: `Main.main` pregunta al arrancar entre CSV y JSON (opción B), sin recompilar.
- Completado: clase `Pagos` con `Integer idPago`, `BigDecimal importe`, `LocalDateTime fechaHora` y enum `MedioPago` (`EFECTIVO`, `TARJETA`). Sin `idVenta` hasta que exista `Venta`. Pendiente de confirmar qué campos y comportamientos tenía la `Pagos` anterior.
- Completado: clases `Estacion`, `FichaFacturacion`, `Surtidor` y `Empleado`. Todas compilan.
- Decisión del usuario: la relación 1:1 estación↔ficha se guarda en `Estacion` (opción A: estación → ficha). La ficha no guarda la estación; la inversa se resolverá en un servicio.
- Decisión del usuario: los contactos del empleado se guardan en dos campos, `telefono` y `email` (opción A).
- Resuelto: diseño de `AsignacionFuncion`. El usuario elige subclase por función y **clase base abstracta sin métodos abstractos**: `AsignacionFuncion.java` (con `enum TipoFuncion`) y las subclases `AsignacionCajero`, `AsignacionExpendedor`, `AsignacionResponsableTienda` y `AsignacionJefeEstacion`. Cada subclase fija su tipo en `super(...)` y lleva su condición con comportamiento propio (`AsignacionCajero.puedeAutorizar`, `AsignacionExpendedor.acreditacionVigente`, `AsignacionResponsableTienda.puedeComprar`, `AsignacionJefeEstacion.tieneNivel`). La base aporta `AsignacionFuncion.estaVigente` y `AsignacionFuncion.retirar`, que devuelve `false` si el periodo ya está cerrado.
- Completado: `Empleado` guarda `List<AsignacionFuncion>` con `Empleado.asignarFuncion`, `Empleado.retirarFuncion`, `Empleado.tieneFuncionVigente` y `Empleado.getAsignaciones`. El control de periodos solapados sigue pendiente para Fase 2 (§4).
- Verificado: compilación de todos los `.java` sin errores y prueba temporal fuera del proyecto con Laura: tres asignaciones, dos periodos de cajera con límites 150,00 y 300,00, cierre del periodo vigente sin borrar el antiguo y rechazo de un segundo cierre.
- Punto actual: tarjetas de fidelización. Falta decidir si la referencia 1:1 se guarda solo en `TarjetaFidelizacion` (opción A) o en ambos lados, añadiendo el campo a `Cliente` (opción B). En MySQL el FK irá en la tabla de tarjetas en cualquier caso.
- Próximos bloques: tarjetas de fidelización, catálogo y campañas, ventas con líneas y pagos, devoluciones e incidencias, jerarquía de producto con su demostración, scripts SQL (`01` a `04`) y entregables (`README.md`, `AUDITORIA_IA.odt` y registro de intervenciones de IA).
- Pendientes menores: espacios en la cabecera que escribe `GestorArchivoCSV.prepararArchivo`; comprobación `containsKey` redundante en `GestorVentas.darAltaCliente`.
