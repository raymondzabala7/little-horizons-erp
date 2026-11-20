# ERP Colegio Internacional "Little Horizons"

## Descripción
ERP de consola en Java que centraliza la gestión académica, extracurricular, financiera y de recursos del colegio.
**Problemas que resuelve:** pérdida de notas, falta de registro de clubes y eventos, facturación manual, préstamos sin control y poca visibilidad del rendimiento de los estudiantes.
**Funcionalidades:** ver sección 10.

## Autores
- Raymond Zabala — GitHub: raymondzabala7 — correo: _(completar)_

## Tecnologías Utilizadas
Java 17+ (probado con 21), IntelliJ IDEA, MySQL 8.0+, JDBC (`mysql-connector-j`). Sin otras librerías externas.

## Requerimientos Previos
JDK 17 o superior, **MySQL 8.0 o superior** (necesario para los CHECK), driver `mysql-connector-j` y Git.

## Configuración de la Base de Datos
1. **Creación:** restaure `docs/database.sql` (volcado de DDL y datos del script oficial del proyecto) en una base llamada `erp_little_horizons`.
2. **Credenciales:** cree el usuario (`CREATE USER 'lh_user'@'%' IDENTIFIED BY '...'; GRANT ALL ON erp_little_horizons.* TO 'lh_user'@'%';`), copie `config.properties.example` a `config.properties` en la raíz y complete la clave. Este archivo está en `.gitignore`.
3. **Driver JDBC:** coloque el `.jar` en `lib/` y agréguelo en IntelliJ (File > Project Structure > Libraries).

## Instalación y Ejecución
1. `git clone https://github.com/raymondzabala7/little-horizons-erp.git`
2. Abrir la carpeta en IntelliJ y marcar `src` como *Sources Root*.
3. Build > Build Project.
4. Compilar por terminal con `javac -encoding UTF-8` si no usa IntelliJ. Ejecutar `com.littlehorizons.erp.Main` con directorio de trabajo en la raíz del proyecto.

## Estructura del Proyecto
Arquitectura MVC con capas extra permitidas por el enunciado:
- `model/` entidades (records inmutables) y `Catalog` con los valores de los ENUM.
- `view/` pantallas de consola: menús, lectura de datos y tablas.
- `controller/` flujo de cada menú; `MainController` ensambla todo.
- `service/` reglas de negocio (cupos, conflictos de horario, promedios, facturación).
- `dao/` acceso JDBC detrás de interfaces; `Jdbc` es el ayudante común.
- `config/` conexión; `exception/` excepciones propias; `util/` logger, validador de entradas, datos del colegio.
- `docs/database.sql`, `logs/error_log.txt`, `invoices/factura_NNNNNN.txt`.

## Principios y Patrones de Diseño Aplicados
**SOLID**
- **S:** `InvoiceTextWriter` solo renderiza; `FinanceService` solo orquesta pagos.
- **O:** nuevas pantallas o DAO se agregan sin tocar el resto.
- **L:** cualquier `XxxDaoImpl` sustituye a su interfaz.
- **I:** una interfaz DAO pequeña por entidad.
- **D:** los servicios dependen de interfaces DAO y se inyectan por constructor en `MainController`.

**Patrones:** Singleton (`DatabaseConnection`), DAO, MVC, inyección por constructor, Template (`BaseController.execute`). Se usan lambdas y Stream API en promedios, reportes y morosos.

## Funcionalidades Principales
- **Estudiantes:** acudientes, matrícula por grado, estados, búsqueda, hermanos.
- **Académico:** reporte de rendimiento por materia (archivo `.txt` en `reports/`), materias, inscripciones, notas 0.0-5.0, promedios por materia/periodo/curso, historial, asistencia y reporte de más de 3 ausencias por semana.
- **Profesores:** directorio, carga académica y detección de choques de horario.
- **Extracurriculares:** clubes con control de cupo, retiros, eventos y posiciones.
- **Financiero:** conceptos, valores personalizados (becas, hermanos), cobros, pagos, historial por estudiante o acudiente, morosos y factura `.txt` con CUFE (SHA-384), QR simulado, IVA (solo en conceptos gravados, p. ej. uniformes) y resolución simuladas. Los morosos se calculan con las pensiones de febrero a noviembre sin pago.
- **Recursos:** inventario, préstamos a estudiantes y profesores, devoluciones y vencidos.
- **Reportes:** estudiantes por grado, mejores promedios, estudiantes en riesgo, participación, ocupación de clubes, recaudo.

## Manejo de Errores y Logs
Excepciones propias (`BusinessRuleException`, `DataAccessException`, `InputCancelledException`) capturadas en `BaseController`. Todas las consultas usan `PreparedStatement` y `try-with-resources`. Los logs se escriben en `logs/error_log.txt`. En cualquier campo se puede escribir `cancelar`.

## Capturas de Pantalla
_(agregar)_

## Problemas Conocidos y Limitaciones
Las facturas y los reportes se generan en `.txt`, no en PDF. La tabla `pagos` solo guarda pagos realizados, por eso la mora se deduce de los meses de pensión sin pago. Los valores fiscales son simulados.

## Futuras Mejoras
Facturas PDF, roles de usuario, pruebas unitarias, interfaz gráfica.

## Licencia
MIT

## Contacto
Repositorio: https://github.com/raymondzabala7/little-horizons-erp
