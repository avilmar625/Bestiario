# Bestiario · AppMejorada4

Una aplicación Android para descubrir criaturas y leyendas, crear un bestiario propio y documentar encuentros en el mundo real. Esta rama es una evolución de AppMejorada3 con una biblioteca online, edición de criaturas, acceso cartográfico a los avistamientos y una experiencia visual unificada.

> **Estado del proyecto:** versión avanzada de desarrollo. Los cambios están en GitHub, pero esta rama todavía necesita sincronización, compilación y pruebas en Android Studio/dispositivo antes de considerarse una versión de producción o publicarse en una tienda.

## 1. Qué ofrece

### Bestiario personal
- Catálogo local que funciona sin conexión.
- Búsqueda por nombre y descripción.
- Filtros para todas las criaturas, leyendas originales y creaciones propias.
- Selección aleatoria dentro del conjunto filtrado.
- Fichas con fotografía, descripción y sonido cuando está disponible.
- Favoritos persistentes.

### Crea y gestiona tus criaturas
- Crea criaturas con nombre, historia y fotografía.
- Captura imágenes con la cámara o utiliza el selector de imágenes del sistema.
- Edita criaturas propias ya guardadas.
- Elimina criaturas con confirmación.
- Valida campos obligatorios y evita nombres duplicados (sin distinguir mayúsculas/minúsculas).
- Las leyendas originales no se pueden editar ni borrar desde el formulario de criaturas propias.

### Diario de exploración
- Registra criatura, lugar, fecha, fotografía y coordenadas opcionales.
- Consulta todos los encuentros guardados en Room.
- Toca un registro con coordenadas para abrir la aplicación de mapas disponible en el dispositivo.
- Mantén pulsado un registro para borrarlo después de confirmar.
- Los registros históricos se conservan si se elimina una criatura propia.

### Estadísticas
- Total de avistamientos.
- Especies diferentes observadas.
- Lugares distintos registrados.
- Criatura más frecuente.
- Último encuentro registrado.

Las estadísticas se calculan a partir de los datos existentes; no se guardan contadores duplicados que puedan quedar desactualizados.

### Biblioteca global online
- Búsqueda de mitos, criaturas y leyendas en Wikipedia en español.
- Resultados con título y fragmento.
- Indicador de carga, mensaje de búsqueda vacía y gestión de errores de conexión.
- Al tocar un resultado se abre el artículo completo en el navegador.

La búsqueda se realiza contra la API pública de MediaWiki. Requiere conexión a Internet. El catálogo propio, los favoritos, las fotografías y el diario siguen siendo locales y no necesitan una cuenta. El texto de búsqueda se envía a Wikipedia para poder realizar la consulta; no se envían las coordenadas ni el diario del usuario.

## 2. Guía rápida

1. En la pantalla principal busca una criatura o utiliza los filtros.
2. Abre una tarjeta para consultar su ficha.
3. Marca una criatura como favorita si quieres tenerla a mano.
4. En **Crear mi criatura**, añade una leyenda propia. Puedes editarla posteriormente desde su ficha.
5. En **Registrar un encuentro**, indica el lugar y añade opcionalmente foto y ubicación.
6. En **Diario**, toca un registro con coordenadas para abrir el mapa; mantén pulsado para eliminarlo.
7. En **Explorar mitos y leyendas online**, busca información adicional en Wikipedia.
8. Consulta **Estadísticas** para ver tu actividad.

## 3. Diseño y experiencia

La interfaz utiliza una paleta oscura de azul petróleo, superficies azul grisáceo y acentos dorados. Las pantallas principales comparten colores, jerarquía tipográfica y tarjetas. El diseño se ha mantenido en layouts XML nativos para que sea sencillo de adaptar y mantener en Android Studio.

## 4. Arquitectura

Código principal: `app/src/main/java/com/example/bestiario/`

| Archivo | Responsabilidad |
|---|---|
| `MainActivity.kt` | Catálogo, filtros, búsqueda y navegación |
| `DetalleActivity.kt` | Ficha, favoritos, registro, cámara y ubicación |
| `CrearMonstruoActivity.kt` | Alta, edición y borrado de criaturas propias |
| `FavoritosActivity.kt` | Colección de favoritos |
| `AvistamientoActivity.kt` | Diario de encuentros |
| `EstadisticasActivity.kt` | Resumen calculado de la actividad |
| `ExplorarOnlineActivity.kt` | Interfaz de búsqueda online |
| `Monstruo.kt` | Modelo común de criatura |
| `Catalogo.kt` | Catálogo original y criaturas almacenadas por el usuario |
| `data/BestiarioRepository.kt` | Acceso de las pantallas a datos locales |
| `data/WikipediaRepository.kt` | Acceso HTTP a la API de Wikipedia |
| `AppDatabase.kt` | Base de datos Room |
| `Avistamiento.kt`, `AvistamientoDAO.kt` | Entidad y consultas del diario |
| `MonstruoUsuario.kt`, `MonstruoUsuarioDao.kt` | Entidad y operaciones CRUD de criaturas propias |
| `MonstruoAdapter.kt`, `AvistamientoAdapter.kt` | Adaptadores RecyclerView |
| `ExplorarOnlineAdapter.kt` | Resultados online |
| `Fotos.kt` | Gestión de fotografías |
| `ReproductorMonstruo.kt` | Ciclo de vida del reproductor |
| `ServicioUbicacion.kt` | Consulta de ubicación |

### Separación por responsabilidades
Las Activities coordinan la interfaz. `BestiarioRepository` concentra operaciones de datos locales, mientras que `WikipediaRepository` encapsula la petición HTTP. Las llamadas de red se ejecutan en `Dispatchers.IO`, con tiempos límite y desconexión garantizada. La búsqueda online no bloquea el hilo de interfaz.

Esta es una arquitectura por capas inicial, no una implementación completa de MVVM. Una evolución posterior podría introducir ViewModel, StateFlow y consultas Flow de Room.

## 5. Persistencia y compatibilidad

- Catálogo original: `Catalogo.kt`.
- Criaturas propias: tabla `monstruos_usuario`.
- Encuentros: tabla `avistamientos`.
- Favoritos: SharedPreferences (`MisDatos`, clave `FAVORITOS`).
- Fotografías: ficheros en almacenamiento privado de la aplicación; Room guarda sus rutas.

La base de datos conserva el nombre `bestiario_db` y la versión de esquema Room **4**. Esta rama no cambia las entidades persistidas ni el esquema de base de datos, por lo que no requiere una migración Room. No se ha activado el borrado automático de datos.

**Importante sobre fotografías:** se almacenan en el espacio privado de la instalación. Al desinstalar la aplicación, Android elimina esos ficheros y la base de datos local. Esta versión no incorpora todavía una copia de seguridad completa ni sincronización entre dispositivos.

## 6. Internet, permisos y privacidad

| Permiso | Finalidad |
|---|---|
| INTERNET | Consultar la API pública de Wikipedia |
| CAMERA | Capturar fotografías |
| ACCESS_FINE_LOCATION | Obtener ubicación precisa cuando el usuario lo solicita |
| ACCESS_COARSE_LOCATION | Permitir ubicación aproximada |

No se solicita ubicación en segundo plano. La ubicación solo se obtiene desde la ficha al pulsar el botón correspondiente. El resto de las funciones locales se puede utilizar sin conexión. Las búsquedas online envían el término introducido a Wikipedia; el diario y las coordenadas no se transmiten a ese servicio.

Los artículos y fragmentos proceden de Wikipedia; al abrir un resultado se muestra el artículo original con su contexto y atribución. Respeta las condiciones de reutilización y licencias de los contenidos de Wikimedia si se reutilizan fuera de la aplicación.

## 7. Configuración

- Application ID / namespace: `com.example.bestiario`
- minSdk: 24
- targetSdk: 36
- compileSdk: 37
- versionCode: 2
- versionName: 1.1
- Kotlin, XML, AndroidX, Room, RecyclerView, corrutinas, Play Services Location y MediaPlayer.

### Abrir la rama

```bash
git fetch origin
git switch --track origin/AppMejorada4
```

Después, abre el proyecto en Android Studio y espera a que finalice **Sync Project with Gradle Files**.

## 8. Pruebas recomendadas antes de distribuir

1. Ejecutar **Build > Make Project** y corregir cualquier error de sincronización o compilación.
2. Instalar desde cero y comprobar que abre el catálogo.
3. Probar búsqueda, filtros, favoritos y selección aleatoria.
4. Crear una criatura, editarla, cambiar su foto y eliminarla.
5. Verificar que se rechaza un nombre duplicado y que no se pueden guardar campos vacíos.
6. Registrar encuentros con y sin fotografía y con y sin ubicación.
7. Abrir en mapas un encuentro con coordenadas y comprobar el comportamiento de un registro sin ellas.
8. Borrar un encuentro y comprobar estadísticas.
9. Probar búsquedas online con conexión, sin conexión y sin resultados.
10. Probar permisos denegados, cámara no disponible y selector de imágenes cancelado.
11. Confirmar que los datos existentes de AppMejorada3 siguen presentes al instalar la actualización sobre esa versión.

## 9. Qué faltaría para comercializarla de verdad

La aplicación ya tiene un conjunto amplio de funciones, pero no sería responsable afirmar que está lista para vender o publicar sin las siguientes tareas:
- Compilación reproducible y pruebas automatizadas (unitarias, UI y dispositivos reales).
- Revisión de accesibilidad, tamaños de pantalla, contraste, traducciones y rendimiento.
- Política de privacidad definitiva, ficha de tienda, clasificación por edades y cumplimiento de las políticas de Google Play.
- Pruebas de seguridad, gestión de errores y revisión de dependencias.
- Copias de seguridad y restauración de datos, especialmente fotografías.
- Sincronización multi-dispositivo y autenticación solo si se decide ofrecer cuentas.
- Una clave y configuración propias si se incorpora Google Maps SDK; esta versión abre coordenadas con la aplicación de mapas instalada y no necesita clave.
- Contenido mitológico propio o con licencias verificadas si se quiere ofrecer una biblioteca editorial curada.

---

**Objetivo de producto:** una aplicación local-first, visualmente coherente y útil sin conexión, que incorpora exploración online cuando el usuario la solicita y conserva el control de sus datos personales.
