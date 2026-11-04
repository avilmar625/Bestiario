# Bestiario · AppMejorada3

Aplicación Android de exploración de criaturas y leyendas. Esta versión evoluciona el proyecto anterior con una interfaz renovada, búsqueda y filtros, selección aleatoria, estadísticas y una primera separación entre interfaz y acceso a datos.

**Tecnologías:** Kotlin, XML, AndroidX, Room, RecyclerView, corrutinas, SharedPreferences, cámara, selector de imágenes, GPS y MediaPlayer.

## 1. Novedades de AppMejorada3

### Catálogo renovado
- Diseño oscuro con tonos azul petróleo y detalles dorados.
- Tarjetas con imagen, nombre, descripción y etiqueta de tipo.
- Búsqueda inmediata por nombre o descripción.
- Filtros para ver todas las criaturas, las leyendas originales o las creadas por el usuario.
- Botón para abrir una criatura aleatoria entre los resultados que se están mostrando.

### Estadísticas
Nueva pantalla **Tu actividad**, con datos calculados a partir del diario:
- Número total de avistamientos.
- Número de criaturas distintas observadas.
- Número de lugares diferentes registrados.
- Criatura más avistada.
- Fecha y criatura del último registro.

Las estadísticas se recalculan al abrir la pantalla; no se almacenan valores duplicados.

### Organización del código
- Se incorpora `BestiarioRepository`, una primera capa de acceso a datos entre las pantallas y Room/catálogo.
- `MainActivity` delega la carga del catálogo en el repositorio.
- Se mantiene el modelo de monstruo común para criaturas originales y personalizadas.
- Las operaciones de base de datos siguen realizándose mediante corrutinas ligadas al ciclo de vida.

## 2. Funcionalidades anteriores
- Consultar los monstruos originales y reproducir sus sonidos.
- Marcar y desmarcar favoritos.
- Crear monstruos propios con nombre, descripción y fotografía opcional.
- Hacer fotografías con la cámara o elegir imágenes de la galería.
- Registrar avistamientos con lugar, fecha, foto opcional y coordenadas GPS opcionales.
- Consultar el diario y borrar registros mediante pulsación larga y confirmación.

## 3. Guía de uso

### Pantalla principal
1. Escribe en el buscador para filtrar por nombre o descripción.
2. Usa **Todos**, **Leyendas** o **Mis criaturas** para cambiar el tipo de resultados.
3. Pulsa una tarjeta para abrir la ficha.
4. Pulsa **Sorpréndeme** para abrir una criatura aleatoria dentro del resultado filtrado.
5. Usa la barra inferior para entrar en Favoritos, Diario o Estadísticas.
6. Pulsa **Crear mi criatura** para añadir una nueva.

### Ficha de una criatura
Consulta su imagen, nombre y descripción. Puedes añadirla o quitarla de favoritos, indicar dónde la has visto, hacer una fotografía, solicitar las coordenadas y registrar el avistamiento. El lugar es obligatorio; la foto y las coordenadas son opcionales.

La ubicación se almacena como latitud y longitud. No se convierte automáticamente en una dirección o nombre de ciudad.

### Crear una criatura
Introduce nombre y descripción. Puedes añadir una foto con la cámara o con el selector de imágenes. La imagen es opcional.

### Diario y estadísticas
El diario muestra los registros guardados. Mantén pulsada una fila para iniciar el borrado y confirma en el diálogo. La pantalla de estadísticas resume los datos existentes y se actualiza al volver a abrirla.

## 4. Permisos

| Permiso | Uso |
|---|---|
| Cámara | Fotografiar criaturas y avistamientos |
| Ubicación precisa o aproximada | Obtener coordenadas del dispositivo |

Los permisos se solicitan cuando se utiliza la función correspondiente. Se puede utilizar el resto de la aplicación sin concederlos. La galería utiliza el selector visual del sistema Android.

## 5. Arquitectura y organización

Código Kotlin: `app/src/main/java/com/example/bestiario/`

| Archivo / paquete | Responsabilidad |
|---|---|
| `MainActivity.kt` | Pantalla principal, búsqueda, filtros y navegación |
| `DetalleActivity.kt` | Ficha, favoritos, cámara, GPS y registro |
| `CrearMonstruoActivity.kt` | Formulario de creación |
| `FavoritosActivity.kt` | Lista de favoritos |
| `AvistamientoActivity.kt` | Diario de registros |
| `EstadisticasActivity.kt` | Cálculo y presentación de estadísticas |
| `Monstruo.kt` | Modelo de criatura |
| `Catalogo.kt` | Catálogo fijo y combinación con criaturas propias |
| `data/BestiarioRepository.kt` | Punto de acceso a los datos para las pantallas |
| `AppDatabase.kt` | Configuración y acceso a Room |
| `Avistamiento.kt`, `AvistamientoDAO.kt` | Entidad y operaciones del diario |
| `MonstruoUsuario.kt`, `MonstruoUsuarioDao.kt` | Entidad y operaciones de criaturas propias |
| `MonstruoAdapter.kt` | Tarjetas del catálogo |
| `AvistamientoAdapter.kt` | Filas del diario |
| `Fotos.kt` | Creación, copia y carga de imágenes |
| `ReproductorMonstruo.kt` | Reproducción y liberación de audio |
| `ServicioUbicacion.kt` | Consulta de ubicación |

### ¿Qué aporta Repository?
Antes, una pantalla podía consultar directamente el DAO de Room o el catálogo. Ahora `BestiarioRepository` ofrece operaciones con nombres orientados a lo que necesita la aplicación, por ejemplo `obtenerMonstruos()` y `obtenerAvistamientos()`. Esto reduce el conocimiento que necesita cada Activity sobre el origen de los datos y prepara el proyecto para incorporar ViewModel y Flow más adelante. Es una primera capa de organización, no una implementación completa de MVVM.

### Recursos Android
- `res/layout/`: interfaces XML.
- `res/drawable/`: imágenes y fondos de tarjetas/campos.
- `res/raw/`: sonidos originales.
- `res/values/`: colores y tema.
- `res/xml/file_paths.xml`: rutas compartidas mediante FileProvider.
- `AndroidManifest.xml`: Activities, proveedor y permisos.

## 6. Persistencia
- Monstruos originales: definidos en `Catalogo.kt`.
- Criaturas personalizadas: tabla `monstruos_usuario` de Room.
- Avistamientos: tabla `avistamientos` de Room.
- Favoritos: conjunto de nombres en SharedPreferences (`MisDatos`, clave `FAVORITOS`).
- Fotografías: archivos privados de la aplicación; la base de datos guarda su ruta.

La base de datos se llama `bestiario_db` y mantiene el esquema de Room versión **4**. No se ha modificado el esquema en esta ampliación. Si una futura versión cambia entidades o columnas, habrá que crear y probar una migración antes de distribuirla. No se debe activar el borrado automático como sustituto de una migración.

## 7. Configuración y ejecución
1. Clona el repositorio o descarga la rama `AppMejorada3`.
2. Abre el proyecto en Android Studio.
3. Espera a que finalice la sincronización de Gradle.
4. Compila y ejecuta en un dispositivo o emulador.

Configuración declarada: namespace y aplicación `com.example.bestiario`, minSdk 24, targetSdk 36 y compileSdk 37.

Para probar cámara y GPS, es recomendable un dispositivo físico. En un emulador configura una cámara virtual y una ubicación desde sus controles.

## 8. Ideas para próximas versiones
- Google Maps para representar los avistamientos.
- Geocoder para convertir coordenadas en ciudad o dirección.
- ViewModel y StateFlow para separar aún más la interfaz de la lógica.
- Flow de Room para actualizar listas automáticamente.
- Edición y borrado de criaturas personalizadas.
- Filtros del diario por fecha, criatura y lugar.
- Exportación e importación de una copia de seguridad.
- Pruebas unitarias y de interfaz.

## 9. Comprobaciones pendientes
Los cambios se han incorporado al repositorio remoto, pero no se ha ejecutado Android Studio ni un build de Gradle en un entorno de compilación. Antes de utilizar esta rama en clase:
1. Sincroniza Gradle.
2. Ejecuta **Build > Make Project**.
3. Prueba búsqueda, filtros y selección aleatoria.
4. Crea una criatura, registra varios avistamientos y comprueba las estadísticas.
5. Comprueba cámara, galería, permisos y GPS en dispositivo o emulador.
6. Verifica que los favoritos y los datos existentes de Room siguen disponibles.

**Objetivo didáctico:** que el alumnado pueda reconocer qué parte corresponde a interfaz, modelo, persistencia y acceso a datos, y que cada nueva funcionalidad introduzca una idea de Android que se pueda estudiar y modificar.
