# ERP Colegio Internacional "Little Horizons"

## Descripción
ERP de consola en Java que centraliza la gestión académica, extracurricular, financiera y de recursos del colegio[cite: 17].
* **Problemas que resuelve:** Pérdida de notas, falta de registro de clubes y eventos, facturación manual, préstamos sin control y poca visibilidad del rendimiento de los estudiantes[cite: 17].
* **Funcionalidades:** Ver sección de Funcionalidades Principales[cite: 17].

## Autores
* **Raymond Javier Zabala Sanchez** — GitHub: [raymondzabala7](https://github.com/raymondzabala7) — Correo: raymondzabala994@gmail.com[cite: 17]

## Tecnologías Utilizadas
Java 17+ (probado con 21), IntelliJ IDEA, MySQL 8.0+, JDBC (`mysql-connector-j`). Sin otras librerías externas[cite: 17].

## Requerimientos Previos
JDK 17 o superior, MySQL 8.0 o superior (necesario para los `CHECK`), driver `mysql-connector-j` y Git[cite: 17].

## Configuración de la Base de Datos
1. **Creación:** Ejecute `docs/database.sql` completo (crea `erp_little_horizons`, tablas y datos de prueba). Borra y recrea las tablas[cite: 17].
2. **Credenciales:** Cree el usuario (`CREATE USER 'lh_user'@'%' IDENTIFIED BY '...'; GRANT ALL ON erp_little_horizons.* TO 'lh_user'@'%';`), copie `config.properties.example` a `config.properties` en la raíz y complete la clave. Este archivo está en `.gitignore`[cite: 17].
3. **Driver JDBC:** Coloque el `.jar` en `lib/` y agréguelo en IntelliJ (`File > Project Structure > Libraries`)[cite: 17].

## Instalación y Ejecución
1. `git clone https://github.com/raymondzabala7/little-horizons-erp.git`[cite: 17]
2. Abrir la carpeta en IntelliJ y marcar `src` como Sources Root[cite: 17].
3. `Build > Build Project`[cite: 17].
4. Ejecutar `com.littlehorizons.erp.Main` con directorio de trabajo en la raíz del proyecto[cite: 17].

## Estructura del Proyecto
Arquitectura MVC con capas extra permitidas por el enunciado[cite: 17]:
* `model/` entidades (records inmutables) y `Catalog` con los valores de los ENUM[cite: 17].
* `view/` pantallas de consola: menús, lectura de datos y tablas[cite: 17].
* `controller/` flujo de cada menú; `MainController` ensambla todo[cite: 17].
* `service/` reglas de negocio (cupos, conflictos de horario, promedios, facturación)[cite: 17].
* `dao/` acceso JDBC detrás de interfaces; `Jdbc` es el ayudante común[cite: 17].
* `config/` conexión; `exception/` excepciones propias; `util/` logger, validador de entradas, datos del colegio[cite: 17].
* `docs/database.sql`, `logs/error_log.txt`, `invoices/factura_NNNNNN.txt`[cite: 17].

## Principios y Patrones de Diseño Aplicados
**SOLID**[cite: 17]
* **S:** `InvoiceTextWriter` solo renderiza; `FinanceService` solo orquesta pagos[cite: 17].
* **O:** Nuevas pantallas o DAO se agregan sin tocar el resto[cite: 17].
* **L:** Cualquier `XxxDaoImpl` sustituye a su interfaz[cite: 17].
* **I:** Una interfaz DAO pequeña por entidad[cite: 17].
* **D:** Los servicios dependen de interfaces DAO y se inyectan por constructor en `MainController`[cite: 17].

**Patrones:** Singleton (`DatabaseConnection`), DAO, MVC, inyección por constructor, Template (`BaseController.execute`). Se usan lambdas y Stream API en promedios, reportes y morosos[cite: 17].

## Funcionalidades Principales
* **Estudiantes:** Acudientes, matrícula por grado, estados, búsqueda, hermanos[cite: 17].
* **Académico:** Materias, inscripciones, notas 0.0-5.0, promedios por materia/periodo/curso, historial, asistencia y reporte de más de 3 ausencias por semana[cite: 17].
* **Profesores:** Directorio, carga académica y detección de choques de horario[cite: 17].
* **Extracurriculares:** Clubes con control de cupo, retiros, eventos y posiciones[cite: 17].
* **Financiero:** Conceptos, valores personalizados (becas, hermanos), cobros, pagos, historial por estudiante o acudiente, morosos y factura `.txt` con CUFE (SHA-384), QR simulado, IVA y resolución simuladas[cite: 17].
* **Recursos:** Inventario, préstamos a estudiantes y profesores, devoluciones y vencidos[cite: 17].
* **Reportes:** Estudiantes por grado, mejores promedios, estudiantes en riesgo, participación, ocupación de clubes, recaudo[cite: 17].

## Manejo de Errores y Logs
Excepciones propias (`BusinessRuleException`, `DataAccessException`, `InputCancelledException`) capturadas en `BaseController`. Todas las consultas usan `PreparedStatement` y `try-with-resources`. Los logs se escriben en `logs/error_log.txt`. En cualquier campo se puede escribir `cancelar`[cite: 17].

---

## Capturas de Pantalla

### 1. Extracurriculares (Clubes con cupos)
![Clubes con cupos](capturas/1_extracurriculares_clubes.png)[cite: 18]

### 2. Estudiantes (Listado general)
![Listado de Estudiantes](capturas/2_estudiantes_listado.png)[cite: 19]

### 3. Académico (Promedios por materia, periodo y curso)
![Promedios Académicos](capturas/3_academico_promedios.png)[cite: 20]

### 4. Financiero (Confirmar pago y generación de factura)
![Confirmación de Pago](capturas/4_financiero_pago.png)[cite: 21]

### 5. Factura Electrónica (Visualización del archivo .txt con CUFE y QR)
![Factura TXT](capturas/5_factura_txt.png)[cite: 22]

### 6. Financiero (Reporte de Morosos)
![Reporte de Morosos](capturas/6_morosos.png)[cite: 23]

### 7. Registro de Logs (Archivo error_log.txt)
![Logs de Error](capturas/7_logs_error.png)[cite: 24]

### 8. Recursos (Listado de préstamos abiertos y vencidos)
![Préstamos de Recursos](capturas/8_recursos_prestamos.png)

---

## Problemas Conocidos y Limitaciones
Las facturas se generan en `.txt` (acordado), no en PDF. Los valores fiscales son simulados[cite: 17].

## Futuras Mejoras
Facturas PDF, roles de usuario, pruebas unitarias, interfaz gráfica[cite: 17].

## Licencia
MIT[cite: 17]

## Contacto
Repositorio: [https://github.com/raymondzabala7/little-horizons-erp](https://github.com/raymondzabala7/little-horizons-erp)[cite: 17]