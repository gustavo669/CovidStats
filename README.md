Funcionalidad principal

El sistema está diseñado para ejecutarse de manera programada y automática. Las tareas programadas realizan lo siguiente:

1. **Consumir regiones** desde la API y almacenarlas en la base de datos.
2. **Consumir provincias** de cada región y almacenarlas.
3. **Consumir reportes diarios** por provincia (confirmados, muertes, recuperados) y también guardarlos.

Todo esto se ejecuta de forma periódica utilizando un **scheduler**.

Tecnologías usadas

- **Java 17**
- **Spring Boot 3.2.4**
- **Spring Data JPA**
- **MySQL**
- **RestTemplate**
- **Apache HttpClient**
- **Maven**

Resultados Esperados

🚀 Ejecutando hilo para consumir la API de COVID-19...
📡 Haciendo solicitud a: https://covid-19-statistics.p.rapidapi.com/regions
💾 Región guardada: Guatemala
📡 Haciendo solicitud a: ...
💾 Provincia guardada: Guatemala
💾 Reporte guardado: 2025-04-10
