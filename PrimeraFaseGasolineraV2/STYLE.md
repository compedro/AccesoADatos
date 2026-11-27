# STYLE.md - Guía de Estilo y Convenciones de Código (Java)

## 1. Nomenclatura e Idioma
- **Idioma:** Todo el código (clases, métodos, variables, comentarios y mensajes) en **español**.
- **Clases:** `PascalCase` con nombres descriptivos (ej. `Cliente`, `GestorVentas`).
- **Interfaces:** `PascalCase` comenzando con el prefijo **`Itf`** (ej. `ItfGestorArchivo`, `ItfRepositorioCliente`).
- **Métodos y Variables:** `camelCase` (ej. `buscarPorId`, `telefono`).
- **Constantes:** `SCREAMING_SNAKE_CASE` (ej. `TAMANO_MAXIMO`, `VALOR_DEFECTO`).
- **Enumerados (Enums):** Tipo en `PascalCase` y valores en `SCREAMING_SNAKE_CASE`.

---

## 2. Principios de Arquitectura y Diseño
- **Separación de Responsabilidades en Capas:**
  - **Dominio / Modelo:** POJOs limpios con atributos privados, constructores y getters/setters. Saneamiento básico de cadenas (`trim()`).
  - **Persistencia / Acceso a Datos:** Desacoplada siempre mediante interfaces (`Itf...`). La implementación se encarga del almacenamiento (ficheros, BBDD, etc.).
  - **Lógica de Negocio / Servicios:** Clases que coordinan los datos y aplican las reglas del negocio, sin acceder directamente a la vista.
  - **Presentación / Entrada:** Gestiona la interacción con el usuario (consola, interfaz o argumentos) sin mezclar lógica de negocio.

---

## 3. Calidad y Buenas Prácticas
- **Integridad del Dominio:**
  - Prohibido lanzar `RuntimeException` genéricas en los constructores. La validación se realiza en la capa de entrada/servicio antes de instanciar, o mediante excepciones comprobadas de dominio.
- **Gestión de Errores y Excepciones:**
  - Evitar bloques `catch` vacíos o genéricos sin control.
  - Usar *try-with-resources* siempre que se manejen recursos cerrables (streams, conexiones, ficheros).
- **Colecciones y Estructuras de Datos:**
  - Elegir la colección adecuada según el problema: `List` para secuencias ordenadas, `Map` para accesos por clave/ID, `Set` para elementos únicos.
- **Legibilidad:**
  - Priorizar código estructurado e imperativo frente a soluciones crípticas.
  - Formato limpio y consistente en las salidas de datos (tabulaciones claras y legibles).
