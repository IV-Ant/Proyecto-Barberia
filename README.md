# BarberHub

Aplicación móvil Android para la gestión de barberías, diseñada para centralizar el agendamiento de citas, la administración de barberos, el seguimiento de clientes y la visualización de información operativa del negocio.

> **Taller ABP - Entrega 1: Proyecto, Presentación y Repositorio en GitHub**  
> Curso: Diseño de Aplicaciones Móviles  
> Modalidad: Individual  
> Autor: Ivan David Galeano Salgado

---

## 1. Descripción del proyecto

BarberHub es un prototipo de aplicación móvil orientado a barberías, barberos independientes y clientes. La solución propone digitalizar procesos que normalmente se realizan de forma manual o mediante canales dispersos como llamadas, mensajes de WhatsApp y visitas presenciales.

La aplicación permite representar diferentes roles del ecosistema de una barbería:

- **Cliente:** consulta información, servicios, barberos y disponibilidad para solicitar citas.
- **Barbero:** gestiona su agenda, sus servicios, clientes y citas asignadas.
- **Propietario o administrador:** visualiza información general del negocio, barberos, clientes, servicios y métricas operativas.

El proyecto se desarrolla como una aplicación Android nativa, con una arquitectura organizada por capas y preparada para evolucionar hacia una solución conectada a servicios en la nube.

---

## 2. Situación problema

Muchas barberías tradicionales gestionan sus citas mediante llamadas telefónicas, mensajes de WhatsApp, redes sociales o atención presencial. Este modelo genera problemas como:

- Dificultad para consultar horarios disponibles en tiempo real.
- Cruces, duplicados o pérdida de citas.
- Dependencia de la comunicación manual entre cliente y barbero.
- Falta de recordatorios para reducir cancelaciones y ausencias.
- Escaso control de la información de clientes, servicios e ingresos.
- Dificultad para que barberos independientes administren su disponibilidad y desempeño.
- Ausencia de una fuente centralizada de información para propietarios y administradores.

Como consecuencia, se afectan tanto la experiencia del cliente como la eficiencia operativa de la barbería.

### Pregunta de investigación

> ¿Cómo puede una aplicación móvil facilitar la gestión de citas, pagos y reseñas en barberías, mejorando la experiencia del cliente y la eficiencia operativa de barberos independientes, empleados y propietarios?

---

## 3. Justificación tecnológica

BarberHub propone una solución móvil porque el teléfono es el canal más accesible para clientes y prestadores de servicios. Una aplicación permite centralizar la información, reducir tareas manuales y proporcionar una experiencia más inmediata para consultar servicios, disponibilidad y citas.

La solución se plantea de manera incremental:

1. **MVP local actual:** aplicación Android nativa con datos persistentes en el dispositivo.
2. **Evolución a nube:** sincronización de datos, autenticación, notificaciones y almacenamiento de comprobantes mediante servicios cloud.
3. **Escalabilidad:** separación de responsabilidades mediante una arquitectura por capas, que facilita reemplazar la fuente local de datos por servicios remotos sin modificar completamente la interfaz.

La propuesta es viable para una barbería pequeña o mediana porque puede iniciar con costos reducidos y crecer de acuerdo con la cantidad de usuarios, locales y transacciones.

---

## 4. Objetivos

### Objetivo general

Desarrollar una aplicación móvil que facilite la gestión de citas y la administración operativa de barberías, mejorando la experiencia de clientes, barberos independientes, empleados y propietarios.

### Objetivos específicos

- Permitir que los clientes consulten barberos, servicios y disponibilidad para solicitar una cita.
- Centralizar la gestión de agenda, clientes y citas asignadas para los barberos.
- Proporcionar a los propietarios una vista general de la operación, incluyendo información de barberos, servicios y métricas.
- Implementar persistencia local para conservar la información básica de la aplicación.
- Aplicar una arquitectura organizada y escalable que permita integrar servicios en la nube en futuras iteraciones.
- Documentar el diseño, arquitectura, instalación y evidencias del proyecto en un repositorio público.

---

## 5. Funcionalidades

### Funcionalidades implementadas en el prototipo

- Navegación entre pantallas principales.
- Visualización de información para los roles de cliente, barbero y propietario.
- Gestión estructurada de datos mediante entidades locales.
- Persistencia local con Room.
- Acceso a los datos mediante DAO y repositorio.
- Manejo del estado de la interfaz mediante ViewModel.
- Componentes reutilizables para mantener consistencia visual.
- Documentación de arquitectura dentro de la aplicación.

### Funcionalidades proyectadas

- Registro e inicio de sesión de usuarios.
- Agendamiento de citas con disponibilidad en tiempo real.
- Confirmación, cancelación y recordatorios de citas.
- Notificaciones push.
- Registro de pagos en efectivo o transferencia.
- Carga de comprobantes de pago.
- Reseñas verificadas luego de completar una cita.
- Panel de métricas para propietarios.
- Código QR para consultar información de cada barbería.
- Sincronización de datos mediante servicios en la nube.

---

## 6. Arquitectura de la solución

La aplicación adopta una arquitectura basada en el patrón **MVVM (Model - View - ViewModel)**, organizado en capas para separar la interfaz, la lógica de presentación y el acceso a datos.

```text
┌───────────────────────────────────────────────┐
│                   UI / Compose                │
│ Pantallas, navegación y componentes visuales  │
└───────────────────────┬───────────────────────┘
                        │
                        ▼
┌───────────────────────────────────────────────┐
│                  ViewModel                    │
│ Estado de pantalla y lógica de presentación   │
└───────────────────────┬───────────────────────┘
                        │
                        ▼
┌───────────────────────────────────────────────┐
│                 Repository                    │
│ Abstracción y coordinación del acceso a datos │
└───────────────────────┬───────────────────────┘
                        │
                        ▼
┌───────────────────────────────────────────────┐
│               Data Layer / Room                │
│ Entidades, DAO, base de datos y convertidores │
└───────────────────────────────────────────────┘
```

### Componentes principales

| Capa | Responsabilidad | Implementación actual |
|---|---|---|
| Interfaz | Presentar datos e interactuar con el usuario | Jetpack Compose |
| Presentación | Mantener estado y lógica de las pantallas | ViewModel |
| Dominio / datos | Centralizar el acceso a la información | Repository |
| Persistencia | Guardar y consultar información local | Room, DAO, entidades |
| Utilidades | Formatear datos y apoyar procesos comunes | Clases utilitarias |

### Arquitectura cloud proyectada

La propuesta futura contempla una arquitectura híbrida donde la aplicación Android se conecte a servicios en la nube:

```text
Cliente Android
      │
      ▼
Servicios de autenticación
      │
      ▼
API o servicios backend
      │
      ├──────────► Base de datos remota
      ├──────────► Almacenamiento de imágenes y comprobantes
      └──────────► Servicio de notificaciones push
```

Servicios que podrían integrarse en futuras versiones:

| Necesidad | Alternativa tecnológica |
|---|---|
| Autenticación | Firebase Authentication |
| Notificaciones | Firebase Cloud Messaging |
| Archivos y comprobantes | Firebase Storage |
| API backend | Node.js con Express o Python con FastAPI |
| Base de datos remota | PostgreSQL o MySQL |
| Alojamiento backend | Render, Google Cloud o servicio equivalente |
| Generación de QR | Librería QR compatible con Android o backend |

> La integración de nube se presenta como una evolución planificada. El prototipo actual utiliza persistencia local con Room para demostrar la arquitectura y el flujo de datos de manera funcional.

---

## 7. Tecnologías utilizadas

| Tecnología | Uso dentro del proyecto |
|---|---|
| Kotlin | Lenguaje principal de desarrollo |
| Android Studio | Entorno de desarrollo |
| Jetpack Compose | Construcción de la interfaz de usuario |
| Material Design 3 | Componentes y lineamientos visuales |
| Room | Persistencia local de datos |
| SQLite | Motor de base de datos local utilizado por Room |
| ViewModel | Manejo del estado y lógica de presentación |
| MVVM | Patrón de arquitectura |
| Gradle Kotlin DSL | Gestión de dependencias y compilación |
| Git y GitHub | Control de versiones, documentación y publicación del repositorio |

---

## 8. Estructura del repositorio

```text
Proyecto-Barberia/
│
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/
│   │   │   │   ├── data/
│   │   │   │   │   ├── local/
│   │   │   │   │   ├── model/
│   │   │   │   │   └── repository/
│   │   │   │   ├── ui/
│   │   │   │   │   ├── components/
│   │   │   │   │   ├── screens/
│   │   │   │   │   ├── theme/
│   │   │   │   │   └── viewmodel/
│   │   │   │   └── util/
│   │   │   └── res/
│   │   └── test/
│   ├── build.gradle.kts
│   └── proguard-rules.pro
│
├── docs/
│   ├── arquitectura/
│   ├── evidencias/
│   └── presentacion/
│
├── gradle/
├── .env.example
├── .gitignore
├── README.md
├── build.gradle.kts
└── settings.gradle.kts
```

> Las carpetas `docs/arquitectura`, `docs/evidencias` y `docs/presentacion` se usan para alojar diagramas, capturas y el material de presentación del taller.

---

## 9. Buenas prácticas aplicadas

- Separación de responsabilidades mediante arquitectura MVVM.
- Organización del código por capas y funcionalidades.
- Uso de Repository para desacoplar la interfaz del origen de los datos.
- Persistencia local mediante Room y acceso controlado por DAO.
- Componentes de interfaz reutilizables.
- Uso de ViewModel para evitar que las pantallas administren directamente la lógica de datos.
- Gestión de dependencias con Gradle Kotlin DSL.
- Control de versiones con Git y repositorio público en GitHub.
- Archivo `.gitignore` para prevenir la publicación de archivos locales, credenciales, llaves y artefactos generados.
- Documentación técnica centralizada en este README y en la carpeta `docs`.

---

## 10. Requisitos de ejecución

Para abrir y ejecutar el proyecto se requiere:

- Android Studio actualizado.
- JDK compatible con la versión configurada en el proyecto.
- Android SDK instalado.
- Emulador Android configurado o dispositivo físico con depuración USB.
- Conexión a internet para descargar dependencias de Gradle durante la primera compilación.

---

## 11. Instalación y ejecución

1. Clonar el repositorio:

   ```bash
   git clone https://github.com/IV-Ant/Proyecto-Barberia.git
   ```

2. Ingresar a la carpeta del proyecto:

   ```bash
   cd Proyecto-Barberia
   ```

3. Abrir la carpeta del proyecto desde Android Studio.

4. Esperar a que Android Studio sincronice las dependencias de Gradle.

5. Seleccionar un emulador Android o conectar un dispositivo físico.

6. Ejecutar la aplicación desde Android Studio con el botón **Run** o con el atajo:

   ```text
   Shift + F10
   ```

---

## 12. Configuración y seguridad

El proyecto utiliza un archivo `.gitignore` para evitar que información local o sensible sea publicada en GitHub.

No se deben subir al repositorio:

```text
.env
local.properties
*.jks
*.keystore
debug.keystore
google-services.json
```

Si en futuras versiones se utilizan variables de entorno, se debe conservar un archivo de ejemplo como:

```text
.env.example
```

sin contraseñas, tokens ni claves reales.

---

## 13. Despliegue

### Estado actual

El prototipo se ejecuta localmente en emuladores Android o dispositivos físicos mediante Android Studio.

### Estrategia de despliegue futura

Para una versión productiva se plantea:

1. Generar una versión firmada de Android, en formato APK o AAB.
2. Publicar la aplicación a través de Google Play Console o distribuir el APK en un entorno de pruebas.
3. Integrar autenticación y almacenamiento seguro en la nube.
4. Implementar un backend desplegado en un proveedor cloud.
5. Usar una base de datos remota para sincronizar clientes, citas, servicios y pagos.
6. Incorporar notificaciones push para confirmaciones y recordatorios.

---

## 14. Estimación de costos del MVP en nube

La siguiente estimación corresponde a una futura versión MVP conectada a servicios cloud. Los valores pueden variar según el proveedor, el número de usuarios y el consumo de recursos.

| Servicio | Uso propuesto | Costo estimado mensual |
|---|---|---:|
| Firebase Authentication | Registro e inicio de sesión | USD $0 en nivel inicial |
| Firebase Cloud Messaging | Notificaciones push | USD $0 |
| Firebase Storage | Comprobantes e imágenes | USD $0 en nivel inicial |
| Backend en Render/Vercel | API y lógica de negocio | USD $0 a $7 |
| PostgreSQL administrado | Datos sincronizados | USD $0 en nivel inicial |
| Dominio web opcional | Identidad del proyecto | Aproximadamente USD $12 al año |
| **Total estimado inicial** | MVP de baja escala | **USD $0 a $7 al mes** |

---

## 15. Evidencias

Las evidencias visuales de la aplicación y los diagramas técnicos se alojarán en la carpeta:

```text
docs/evidencias/
```

Los diagramas de arquitectura se alojarán en:

```text
docs/arquitectura/
```

La presentación ejecutiva y técnica del proyecto se encontrará en:

```text
docs/presentacion/
```

### Evidencias incluidas o proyectadas

- Pantalla principal de la aplicación.
- Vista de cliente.
- Vista de barbero.
- Vista de propietario.
- Visualización de información y navegación.
- Estructura de datos local con Room.
- Diagrama de arquitectura MVVM.
- Diagrama de evolución hacia servicios cloud.

---

## 16. Resultados esperados

Con BarberHub se espera:

- Reducir la dependencia de canales manuales para solicitar y organizar citas.
- Facilitar la consulta de servicios y disponibilidad.
- Mejorar el control de agenda para barberos.
- Centralizar información operativa para propietarios.
- Proporcionar una base técnica modular para integrar funcionalidades cloud.
- Preparar un MVP escalable para validación con barberías y usuarios reales.

---

## 17. Limitaciones actuales

- El proyecto se encuentra en fase de prototipo funcional.
- Los datos se almacenan localmente en el dispositivo.
- No se ha implementado autenticación de usuarios en nube.
- No se ha integrado un sistema de pagos real.
- Las notificaciones push y el almacenamiento de comprobantes se plantean para una siguiente etapa.
- Las métricas administrativas pueden requerir datos adicionales y sincronización remota para una operación multiusuario real.

---

## 18. Trabajo futuro

- Integrar Firebase Authentication para registro e inicio de sesión.
- Implementar sincronización de citas, usuarios y servicios en una base de datos remota.
- Incorporar Firebase Cloud Messaging para recordatorios automáticos.
- Permitir la carga de comprobantes de transferencias.
- Implementar pagos digitales mediante una pasarela apropiada.
- Añadir reseñas verificadas luego de la finalización de las citas.
- Generar códigos QR por barbería o local.
- Agregar reportes gráficos para el propietario.
- Implementar pruebas unitarias, de integración y de interfaz.
- Realizar pruebas de usabilidad con clientes y barberos reales.
- Preparar la publicación de una versión beta en Google Play.

---

## 19. Metodología de trabajo

El proyecto se organiza con una adaptación de **Mobile Agile / Scrum**, combinando prototipado de experiencia de usuario y desarrollo incremental.

| Etapa | Actividades principales |
|---|---|
| Análisis | Identificación del problema, usuarios, necesidades y objetivos |
| Diseño | Definición de arquitectura, modelo de datos, flujos y pantallas |
| Desarrollo | Implementación incremental de pantallas, datos y lógica |
| Pruebas | Validación de navegación, persistencia y experiencia de usuario |
| Documentación | Actualización de README, diagramas, evidencias y presentación |
| Entrega | Publicación del repositorio público y presentación técnica |

---

## 20. Autor

**Ivan David Galeano Salgado**

Proyecto académico desarrollado para el curso de **Diseño de Aplicaciones Móviles**.

Repositorio público:  
[https://github.com/IV-Ant/Proyecto-Barberia](https://github.com/IV-Ant/Proyecto-Barberia)