# Ampliación de la aplicación de gasolinera con MySQL

## Fase 1 Diseño de datos y preparación del modelo Java

**Módulo:** Acceso a Datos · **Curso:** 2.º de DAM

### El encargo

La aplicación que habéis desarrollado hasta ahora trabaja con las clases `Gasolinera` y `Pagos` y permite conservar información en CSV y JSON. La empresa quiere ampliar ese programa para gestionar también la tienda y parte de la actividad diaria de sus estaciones. Para ello incorporará una nueva alternativa de persistencia en MySQL mediante el driver JDBC.

El trabajo continúa sobre vuestro proyecto actual. La nueva persistencia debe convivir con las alternativas anteriores, conservando sus funcionalidades y la información que ya manejaban. Revisad las interfaces existentes y mantened la separación entre el modelo, la lógica de la aplicación y los mecanismos de almacenamiento. Si necesitáis adaptar alguna parte, justificad el cambio y comprobad sus efectos sobre CSV y JSON.

En esta primera fase prepararéis el diseño relacional, crearéis la base de datos y sus datos de ejemplo, desarrollaréis las clases Java del dominio e implementaréis un registro automático de cambios de precio en MySQL (en MySQL, no en Java. Recuerda el RA5 del curso pasado). Partimos de que ya sabéis establecer la conexión JDBC. La ejecución de consultas y las operaciones de persistencia desde Java se trabajarán en la siguiente fase.

El relato siguiente recoge las necesidades de la empresa. Debéis deducir a partir de él qué información almacenar, cómo organizarla y qué restricciones aplicar. No se proporciona un catálogo de tablas ni una jerarquía de clases resuelta. Es tu labor tomar estas decisiones. Tus becarios escribirán el código e implementarán el diseño que tú elabores. **No se pide un diagrama entidad-relación.**

### Las estaciones y su puesta en servicio

La empresa gestiona varias gasolineras. De cada una necesita conocer su código interno, que no se repite en la empresa, su nombre comercial, dirección, teléfono y fecha de apertura.

Cada estación dispone de una ficha propia de configuración de facturación, con un código de configuración único, la serie de sus tickets, el siguiente número que debe utilizar y el texto que aparece al pie de los justificantes. El servicio de soporte identifica la ficha por ese código cuando revisa problemas de emisión. Esta ficha configura la emisión de documentos; no debe confundirse con los datos fiscales de la empresa, que pueden ser comunes a varias estaciones.

Una estación debe tener exactamente una ficha y una ficha solo puede corresponder a una estación. La empresa considera que ambas forman una única alta: no admite estaciones sin su ficha ni fichas independientes, y tampoco comparte una ficha entre varias estaciones. Si el procedimiento de alta requiere pasos intermedios, estos no deben dejar una operación confirmada incompleta. El diseño deberá explicar cómo se consigue.

Cada estación tiene al menos un surtidor registrado. De cada surtidor se conserva un código de equipo único en toda la empresa, su número visible dentro de la estación, el tipo de combustible dispensado y su estado de servicio. Dos surtidores de una misma estación no pueden tener el mismo número visible; ese número sí puede repetirse en estaciones distintas.

Un surtidor pertenece siempre a una única estación y no se registran equipos que todavía no tengan destino. Una estación puede tener varios. Que un surtidor esté temporalmente averiado no significa que deje de pertenecer a ella. No se permite retirar su último surtidor dejando la estación sin ninguno registrado.

Para acotar esta ampliación, la gestión detallada de depósitos, mangueras, suministros de combustible y repostajes queda fuera del encargo. La información y las operaciones de la aplicación anterior se conservan.

### El personal y la organización del trabajo

Cada empleado tiene un número interno único, documento de identidad, nombre, datos de contacto y fecha de incorporación. En esta versión trabaja adscrito a una única estación, que puede tener varios empleados. Una misma persona conserva su identidad y antigüedad aunque cambien su estación o sus responsabilidades. Los cambios de adscripción no alteran la estación que consta en sus ventas anteriores.

En la documentación interna se habla de distintos tipos de empleado: cajeros, expendedores, responsables de tienda y jefes de estación. Los cajeros tienen un límite autorizado para devoluciones; los expendedores disponen de una acreditación para manipular combustibles, con número y caducidad; los responsables de tienda tienen un presupuesto autorizado de compras; y los jefes de estación cuentan con un nivel de autorización para operaciones excepcionales.

Habitualmente una persona comienza desempeñando una sola función. Sin embargo, en estaciones pequeñas puede trabajar como cajero y expendedor a la vez; un jefe puede atender la caja, y un responsable de tienda puede asumir temporalmente la jefatura.

Estas funciones se asignan y retiran de manera independiente. De cada periodo deben conservarse la fecha de inicio, la fecha de fin cuando termine y las condiciones específicas que tuvo esa asignación. Una persona puede abandonar una función y recuperarla después con otras condiciones. El historial anterior debe permanecer intacto, sin que se creen empleados nuevos ni cambie la autoría de sus operaciones. No se admiten periodos solapados de una misma función para una misma persona, aunque sí puede ejercer funciones diferentes simultáneamente.

Durante una incorporación o un cambio de responsabilidades puede haber empleados temporalmente sin funciones. La empresa prevé añadir otras funciones en el futuro, con información específica propia. Vuestro modelo deberá poder representar estas situaciones; justificad las decisiones de diseño Java que toméis para ello.

Además de las funciones, la empresa necesita reflejar la supervisión del personal. Un empleado puede no tener supervisor o tener uno solo, que también será un empleado. Una persona puede supervisar a varias o a ninguna. Nadie puede supervisarse a sí mismo ni depender, directa o indirectamente, de alguien que esté bajo su supervisión. La supervisión no equivale necesariamente a desempeñar la función de jefe: un trabajador experimentado puede supervisar a una nueva incorporación.

En esta fase se conserva la supervisión actual; no se exige llevar también su historial.

### Los clientes y las tarjetas de fidelización

La tienda atiende tanto a clientes identificados como a personas que compran sin registrarse. De los clientes registrados se guarda un código único, nombre y datos de contacto. No es obligatorio facilitar todos los medios de contacto; decidid cuáles pueden faltar y documentad el criterio.

La empresa dispone de tarjetas de fidelización identificadas por un número único. Puede recibir un lote de tarjetas y registrarlo antes de entregarlas: esas tarjetas permanecen sin titular. Un cliente puede no tener tarjeta o tener una sola, y una tarjeta puede estar sin asignar o pertenecer a un único cliente. No se comparten tarjetas y no se asignan dos simultáneamente a una misma persona.

Se guarda la fecha de emisión de la tarjeta y, si se entrega, la fecha de asignación. El número de tarjeta y el código de cliente siguen siendo referencias de negocio; no sustituyen al identificador técnico que debéis utilizar en la base de datos.

### La tienda y sus productos

Cada producto del catálogo tiene una referencia comercial única, descripción, precio de venta vigente y estado de comercialización. Los productos retirados deben seguir siendo identificables en las operaciones antiguas.

La tienda vende productos envasados, que se cobran por unidades completas, y productos a granel, como frutos secos de un dispensador, que se cobran por kilogramos. En los primeros, la cantidad comprada es un número entero positivo de unidades. En los segundos, puede ser decimal y debe respetar la precisión de pesada admitida por el producto.

Cada producto pertenece a una única modalidad y la mantiene durante toda su comercialización. Una bolsa preparada y una venta a granel del mismo alimento son referencias comerciales diferentes.

Para los productos envasados se indica el formato del envase; para los productos a granel se conserva la precisión de pesada admitida. Cuando la aplicación prepare una línea de ticket, deberá validar la cantidad y calcular el importe según las reglas de su modalidad. El proceso que prepara el ticket debe poder trabajar con productos de ambas modalidades sin repetir en él todas las decisiones sobre cómo se vende cada uno.

Los precios de venta deben estar siempre informados y ser mayores que cero. Definid también un criterio común de redondeo monetario y utilizad tipos adecuados para precios, cantidades e importes. Una cantidad de 1,5 no es válida para un producto vendido por unidades completas, aunque pudiera almacenarse en una columna decimal.

### Los proveedores del catálogo

De los proveedores se guarda un código interno único, identificación fiscal, razón social y datos de contacto. El catálogo incluye exclusivamente proveedores con los que ya existe algún acuerdo de suministro; los contactos comerciales todavía en negociación quedan fuera de esta aplicación.

Un proveedor puede suministrar varios productos y un producto puede comprarse a varios proveedores. Para cada acuerdo interesa conocer la referencia que utiliza ese proveedor, el precio de compra acordado y el plazo habitual de entrega. No se duplicará el mismo acuerdo vigente entre un proveedor y un producto.

Todo proveedor registrado debe suministrar al menos un producto y todo producto del catálogo debe tener al menos un proveedor. Estas condiciones también se aplican a productos retirados que se mantienen por trazabilidad: se conserva al menos su último acuerdo como referencia. No se exige un historial completo de las condiciones de suministro.

No se puede confirmar un alta incompleta ni eliminar el último acuerdo dejando aislado al proveedor o al producto. Explicad cómo organizaréis el alta y los cambios para respetar estas reglas, teniendo en cuenta que los elementos pueden necesitar crearse en varios pasos.

### Las campañas promocionales

La empresa prepara campañas con un código único, nombre, descripción y fechas de inicio y fin. La fecha de fin no puede ser anterior a la de inicio. Puede registrarlas antes de que se apunte ningún cliente, y un cliente puede no participar en ninguna.

Un cliente puede inscribirse en varias campañas y cada campaña puede tener numerosos inscritos. Se conserva la fecha de cada inscripción y no se permite inscribir dos veces al mismo cliente en la misma campaña.

La participación requiere que el cliente esté registrado, pero no que tenga tarjeta. En esta fase solo se gestiona la inscripción: no se exige calcular descuentos ni introducir un motor de promociones.

### Las ventas y sus pagos

Cada venta de tienda se realiza en una única estación y la registra un empleado. Tiene una referencia de ticket única en la empresa, fecha y hora, y puede estar asociada a un cliente registrado. Las compras anónimas también deben poder guardarse.

Una venta registrada contiene al menos una línea. Cada línea pertenece a una sola venta y corresponde a un producto del catálogo. Dentro de un ticket, las líneas tienen un número de orden que no se repite. Si la interfaz prepara un carrito vacío, este todavía no se considera una venta registrada.

Cada línea conserva la cantidad vendida, el precio unitario realmente aplicado y el importe resultante. El precio del catálogo puede cambiar después: esos cambios no deben recalcular ni modificar tickets anteriores. Tampoco se reconstruirá un precio antiguo buscando el cambio más cercano en un historial; el precio cobrado debe quedar guardado en la propia línea.

La información de cobro se integrará con `Pagos`. Un cobro de tienda corresponde a una única venta y una venta puede cobrarse mediante uno o varios pagos, por ejemplo parte en efectivo y parte con tarjeta. Se necesita conocer importe, fecha y hora, y medio de pago. Mientras se completa el cobro puede no haber todavía pagos suficientes; al confirmar que la venta está pagada, la suma debe coincidir con su importe. Definid cómo distinguiréis esos estados.

No se deben perder los campos ni los comportamientos que `Pagos` ya tuviera en la práctica anterior. Documentad cómo encaja la nueva información con el modelo existente, sin reinterpretar ni transformar silenciosamente los registros antiguos.

### Las devoluciones

La tienda acepta devoluciones de los productos comprados cuando cumplen su política comercial. Cada devolución registrada corresponde a una línea concreta de una venta ya pagada e indica cantidad, motivo, fecha y hora, empleado que la acepta e importe reintegrado. Si se devuelven productos de varias líneas, se registran las devoluciones correspondientes a cada una; no es obligatorio crear un documento que las agrupe.

Se permiten devoluciones parciales y varias devoluciones sucesivas de una misma línea, siempre que la cantidad total aceptada no supere la comprada. Debe respetarse la modalidad de venta: unidades completas para productos envasados y cantidades compatibles con la precisión de pesada para productos a granel.

El importe se calcula a partir del precio que se cobró en la venta original, no del precio vigente. El criterio de redondeo debe evitar que varias devoluciones parciales permitan reintegrar más del importe de la línea. El reintegro se registra como parte de la devolución, separado de los pagos recibidos por la venta.

Una devolución no borra la línea original ni modifica sus cantidades o precios. Venta y devoluciones permiten reconstruir lo sucedido.

También se indica si la mercancía devuelta es apta para volver a venderse. Registrar una devolución a granel no implica que el alimento pueda volver al dispensador. En esta fase no se exige implementar la gestión de existencias.

La regla que impide devolver más de lo comprado debe quedar recogida en el diseño. Su implementación completa, considerando devoluciones anteriores y operaciones simultáneas, se abordará posteriormente.

### Las incidencias de trabajo

En cada estación se registran incidencias con una referencia única, descripción, fecha y hora de apertura y estado. Al abrir una incidencia puede no conocerse todavía quién la atenderá.

Una incidencia puede permanecer sin asignar o tener un único empleado responsable en cada momento. Un empleado puede no tener ninguna incidencia o hacerse cargo de varias. La asignación puede cambiar, pero en esta fase solo es necesario conservar al responsable actual.

No debe confundirse al responsable de la incidencia con quien supervisa al empleado ni con sus funciones de trabajo: son necesidades distintas.

### El registro automático de cambios de precio

La empresa necesita consultar cuándo han cambiado los precios de su catálogo. Cada vez que se modifique realmente el precio de venta de un producto, MySQL deberá registrar automáticamente el producto afectado, el precio anterior, el nuevo y la fecha y hora del cambio.

Este comportamiento se implementará mediante un **trigger de la base de datos** y funcionará también si el precio se modifica directamente desde una herramienta SQL. El programa Java no será el encargado de insertar ese registro.

Guardar otra vez el mismo precio no genera una entrada. Modificar únicamente la descripción u otro dato tampoco. La creación inicial del producto no se considera un cambio de precio y no añade una entrada al historial. Una vez creado el producto, la aplicación no ofrecerá operaciones ordinarias para editar o borrar sus entradas históricas.

Este registro recoge cambios de precio confirmados, no todos los sucesos de la aplicación ni los intentos fallidos. Con las tablas InnoDB exigidas en esta práctica, si se modifica un precio dentro de una transacción y después se deshace, también debe desaparecer la entrada histórica producida por ese cambio. No se pide construir un registro externo que sobreviva a la cancelación de la transacción.

El historial de precios no sustituye al de funciones del personal ni al registro de ventas y devoluciones: cada uno conserva hechos diferentes.

Como ampliaciones opcionales para una fase posterior, podéis proponer un trigger que registre una entrada de existencias cuando se acepte una devolución apta para la venta, o un mecanismo que impida modificar las líneas de ventas ya cerradas. No se exige implementarlos en esta entrega ni pueden sustituir al trigger obligatorio de precios.

### El trabajo de diseño

A partir del caso, preparad el modelo relacional. Para cada tabla, explicad su finalidad, columnas, tipos, claves, campos obligatorios u opcionales, valores predeterminados y restricciones. Indicad las referencias entre tablas y qué debe suceder cuando se intenta modificar o eliminar información relacionada.

Todas las tablas, incluidas las que representen asociaciones, asignaciones, líneas e históricos, tendrán una clave primaria formada por **un único entero autoincremental**. Las referencias comerciales y los números de negocio deberán mantener las condiciones de unicidad del caso aunque no sean claves primarias. Identificad también las combinaciones de campos que no pueden repetirse.

Diferenciad la identidad técnica de cada registro de sus referencias de negocio. Evitad duplicar información salvo que exista una razón, como conservar el precio realmente aplicado en una operación.

Para cada regla del relato, indicad dónde queda garantizada: por una restricción de la base de datos, por un trigger o por controles adicionales que habrá que desarrollar. No basta con escribir que una regla se cumple porque los datos de ejemplo la respetan.

Algunas condiciones afectan a varios registros o requieren comprobar el conjunto de una operación. Identificad cuáles no quedan resueltas únicamente con claves foráneas, campos obligatorios o restricciones de unicidad. Explicad cómo las abordaríais en la siguiente fase, incluyendo altas conjuntas, bajas del último elemento relacionado, ciclos de supervisión, periodos solapados y devoluciones acumuladas. No se exige implementar ahora todos esos controles ni resolverlos artificialmente con referencias circulares imposibles de cargar.

El resultado será un esquema relacional con sus claves y referencias, acompañado de las explicaciones necesarias. Podéis presentarlo mediante una notación textual y tablas de documentación. **No elaboréis un diagrama entidad-relación.**

### La preparación del modelo Java

La ampliación debe dar lugar a un modelo de dominio de, probablemente, más de  **15 clases Java**. Es una referencia de tamaño para el conjunto del modelo, no una invitación a inventar clases vacías ni un máximo rígido que obligue a eliminar información. No se cuentan interfaces DAO, utilidades de conexión, excepciones ni clases de pruebas. Si las decisiones justificadas sobre asignaciones o registros históricos hacen que el número se aparte ligeramente, explicadlo.

Parte de esas clases tendrá una correspondencia directa con información persistente. No se exige una equivalencia mecánica de una tabla por clase: debéis justificar cómo representaréis las asociaciones, los datos específicos y las posibles jerarquías, y cómo se conservarán en MySQL.

Las clases incluirán atributos, tipos adecuados, constructores y métodos coherentes con su responsabilidad. Decidid cómo representar las referencias a otros objetos y los casos en que todavía no existe una asociación o un identificador generado por la base de datos.

Justificad especialmente cómo representaréis las funciones cambiantes del personal y las modalidades estables de producto. El diseño debe soportar las situaciones descritas y aportar el comportamiento necesario, no limitarse a reproducir columnas mediante getters y setters.

Implementad en Java la validación de cantidades y el cálculo de importes según la modalidad del producto. Preparad una demostración breve que trate conjuntamente productos de las dos modalidades y muestre sus comportamientos. No necesita guardar ni leer datos de MySQL.

Las clases del dominio no abrirán conexiones ni ejecutarán SQL. Conservad la conexión JDBC ya preparada y la organización del proyecto para incorporar después la persistencia MySQL. Todavía no debéis implementar los métodos JDBC de inserción, consulta, actualización o borrado.

El proyecto debe compilar y permitir construir objetos de ejemplo relacionados entre sí. Comprobad además que CSV y JSON siguen funcionando con las funcionalidades anteriores.

### La base de datos y las comprobaciones

Entregad los siguientes scripts, con instrucciones claras sobre su orden de ejecución:

- `01_esquema.sql`: creación de la base de datos, tablas, claves y restricciones.
- `02_triggers.sql`: creación del trigger obligatorio del historial de precios.
- `03_datos.sql`: carga de los datos de ejemplo.
- `04_comprobaciones.sql`: operaciones y consultas para comprobar el diseño y el trigger, con comentarios que indiquen el resultado esperado y los errores que se provocan deliberadamente.

Los tres primeros se ejecutarán en ese orden sobre un entorno vacío y dejarán una base de datos coherente. Separad las comprobaciones que deben fallar para que un error esperado no interrumpa la preparación inicial. No desactivéis las claves foráneas para ocultar un diseño o una carga incorrectos.

Podéis ejecutar y revisar estos scripts desde una herramienta SQL o desde la terminal. Esto no adelanta la implementación de consultas JDBC desde Java.

Como referencia, incluid al menos cinco registros por tabla de datos principales, salvo catálogos o casos cuyo contenido justifique una cantidad menor, y al menos diez líneas de venta repartidas entre varios tickets. En las tablas de asociaciones y de historial, incluid los registros necesarios para demostrar sus reglas; no añadáis filas de relleno para alcanzar un número.

Los ejemplos deben permitir comprobar, como mínimo:

- Varias estaciones, cada una con su configuración y surtidores, incluyendo una con varios equipos.
- Clientes con y sin tarjeta, y tarjetas todavía sin asignar.
- Personal sin supervisor, personal con supervisor y una persona que supervise a varias.
- Un empleado con varias funciones simultáneas, otro sin funciones y alguien que haya recuperado una función después de un periodo anterior finalizado.
- Productos de las dos modalidades y un cálculo con unidades enteras frente a otro con cantidad decimal válida.
- Un proveedor que suministre varios productos y un producto con varios proveedores, sin proveedores ni productos aislados.
- Una campaña sin inscritos, un cliente sin campañas y participaciones múltiples en ambos sentidos.
- Una incidencia sin asignar, un empleado sin incidencias y otro responsable de varias.
- Ventas anónimas e identificadas, tickets con varias líneas y una venta cobrada con más de un pago.
- Devoluciones parciales sucesivas de una misma línea, conservando la venta y sin superar su cantidad ni su importe originales.
- Un cambio real de precio que genere una entrada histórica, una actualización al mismo precio y otra de descripción que no la generen, y una venta anterior cuyo precio permanezca intacto.
- Una modificación de precio dentro de una transacción que después se deshaga, comprobando la reversión tanto del precio como de su entrada histórica.

Incluid también intentos de introducir referencias de negocio duplicadas, asociaciones repetidas y referencias a registros inexistentes, para demostrar las restricciones que hayáis implementado. En los casos cuya protección quede pendiente para la siguiente fase, explicad esa limitación expresamente; no presentéis como rechazo automático algo que todavía no lo es.

### El uso de IA durante la práctica

Podéis utilizar **OpenCode** para analizar el repositorio, trabajar desde la terminal, generar y ejecutar scripts o investigar errores, y **GitHub Copilot en IntelliJ IDEA** para completar, proponer y revisar código. Se permite utilizarlos durante todo el proceso, también para comparar alternativas de diseño.

La responsabilidad del resultado es vuestra. Antes de incorporar una propuesta, comprobad que respeta el caso y que podéis explicar su funcionamiento. Una solución generada que compila o un script que se ejecuta no demuestran por sí solos que el diseño sea correcto.

Incluid un registro breve de tres intervenciones relevantes de la IA: qué pedisteis, qué propuso, qué aceptasteis o modificasteis y cómo lo verificasteis. Al menos una debe tratar una decisión de diseño y otra una comprobación de comportamiento. No es necesario entregar conversaciones completas.

### La entrega

El enlace del repositorio ordenado con:

1. El proyecto Java actualizado y compilable, conservando las alternativas CSV y JSON.
2. El modelo relacional y el diccionario de datos, sin diagrama entidad-relación.
3. Los scripts SQL, en una carpeta ordenada
4. Un `README.md` con requisitos, configuración de la conexión sin credenciales personales, orden de preparación y ejecución de la demostración Java.
5. Un `AGENTS.md` con información para los agentes.
6. La justificación de las decisiones sobre el modelo Java y su correspondencia con MySQL.
7. Una relación de reglas de negocio que distinga las ya garantizadas de las pendientes, con la propuesta de tratamiento de estas últimas y las evidencias de las comprobaciones realizadas.
8. La descripción de tres operaciones futuras que se implementarán mediante JDBC, indicando sus datos de entrada y las reglas que deberán respetar.
9. Un `AUDITORIA_IA.odt` con ejemplos de casos concretos en los que la IA ha hecho especialmente bien y especialmente mal.

Se valorarán la fidelidad al caso, la integridad y trazabilidad de los datos, la coherencia entre el modelo Java y el relacional, la conservación de las funcionalidades anteriores y vuestra capacidad para explicar y verificar las decisiones tomadas.
