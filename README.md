# Bestiario

Aplicación Android para consultar un pequeño catálogo de monstruos, crear criaturas propias y llevar un diario de sus avistamientos. El proyecto está desarrollado con Kotlin, vistas XML y AndroidX.

Esta documentación incluye una guía de uso y una explicación de la organización interna del código.

## 1. Funcionalidades

- Consultar los monstruos incluidos en el catálogo.
- Abrir la ficha de cada monstruo y escuchar su sonido, cuando dispone de uno.
- Marcar y desmarcar monstruos como favoritos.
- Crear monstruos propios con nombre, descripción y fotografía opcional.
- Hacer fotografías con la cámara o seleccionar imágenes de la galería.
- Registrar avistamientos indicando el lugar, la fecha, una fotografía opcional y las coordenadas GPS opcionales.
- Consultar el diario de avistamientos y borrar registros con una pulsación larga y confirmación.

Los monstruos originales forman parte del código. Los monstruos creados por el usuario y los avistamientos se guardan en una base de datos local. Los favoritos se guardan mediante preferencias compartidas.

## 2. Cómo utilizar la aplicación

### Pantalla principal

Al abrir la aplicación aparece el catálogo de monstruos. Toca una fila para entrar en la ficha del monstruo.

Desde esta pantalla también se puede acceder a:
- **Ver mis favoritos**: muestra los monstruos marcados como favoritos.
- **Diario de avistamientos**: consulta los registros guardados.
- **Crear monstruo**: abre el formulario para añadir una criatura propia.

### Ficha de un monstruo

En la ficha se muestra su imagen, nombre y descripción. Si el monstruo tiene un sonido asociado, se reproduce al abrir la pantalla.

- **Añadir a favoritos / Quitar de favoritos**: cambia el estado de favorito.
- **¿Dónde lo has visto?**: escribe el lugar o una descripción del sitio.
- **Hacer foto del avistamiento**: abre la cámara. La fotografía se adjuntará al próximo registro.
- **Obtener mi ubicación**: solicita permiso de ubicación si todavía no se ha concedido e intenta obtener las coordenadas actuales.
- **Registrar avistamiento**: guarda el registro. Es necesario indicar un lugar; la foto y las coordenadas son opcionales.

Después de guardar, el formulario limpia la foto y las coordenadas para evitar que se reutilicen accidentalmente en el siguiente registro.

La ubicación se guarda como latitud y longitud. La aplicación no convierte actualmente esas coordenadas en el nombre de una ciudad o dirección.

### Crear un monstruo

Es necesario introducir un nombre y una descripción. La imagen es opcional y puede obtenerse de dos maneras:
- **Hacer foto**: utiliza la cámara del dispositivo.
- **Elegir de la galería**: abre el selector de imágenes de Android.

Al pulsar **Guardar monstruo**, la criatura se almacena y aparecerá en el catálogo principal. Los monstruos personalizados no tienen un sonido propio asignado.

### Favoritos

La pantalla de favoritos muestra los monstruos marcados. Para quitar uno de la lista, entra en su ficha y pulsa **Quitar de favoritos**.

### Diario de avistamientos

Se muestran los registros ordenados del más reciente al más antiguo. Cada fila puede incluir monstruo, lugar, fecha, coordenadas y fotografía.

Para borrar un registro, mantén pulsada su fila y confirma la operación en el diálogo.

## 3. Permisos

Android solicita los permisos cuando se intenta utilizar la función correspondiente:

| Permiso | Para qué se utiliza |
|---|---|
| Cámara | Hacer fotografías del monstruo o del avistamiento |
| Ubicación precisa o aproximada | Obtener las coordenadas del avistamiento |

La aplicación puede utilizarse sin conceder estos permisos, pero las funciones asociadas no estarán disponibles. La selección de imágenes de la galería utiliza el selector visual del sistema Android.

## 4. Tecnologías utilizadas

- **Kotlin** como lenguaje principal.
- **Layouts XML** para las interfaces.
- **Activities e Intents** para navegar entre pantallas.
- **RecyclerView** y adaptadores para mostrar catálogos y registros.
- **Room** para almacenar monstruos personalizados y avistamientos.
- **SharedPreferences** para guardar los favoritos.
- **Kotlin Coroutines** y `lifecycleScope` para ejecutar operaciones de base de datos sin bloquear la interfaz.
- **Activity Result API** para gestionar cámara, selector de imágenes y solicitudes de permisos.
- **FileProvider** para compartir de forma segura con la cámara los archivos de imagen creados por la aplicación.
- **Fused Location Provider** para obtener la ubicación del dispositivo.
- **MediaPlayer** para reproducir los sonidos de los monstruos originales.

## 5. Organización interna del proyecto

El código Kotlin se encuentra en:

`app/src/main/java/com/example/bestiario/`

### Pantallas (Activities)

| Archivo | Responsabilidad |
|---|---|
| `MainActivity.kt` | Pantalla inicial. Carga el catálogo y permite navegar a las demás secciones. |
| `DetalleActivity.kt` | Muestra la ficha de un monstruo, gestiona favoritos, cámara, ubicación y registro de avistamientos. |
| `CrearMonstruoActivity.kt` | Recoge los datos de una criatura propia y permite añadir una fotografía. |
| `FavoritosActivity.kt` | Filtra el catálogo según los nombres guardados como favoritos. |
| `AvistamientoActivity.kt` | Consulta y actualiza la lista de avistamientos guardados. |

### Modelo y catálogo

| Archivo | Responsabilidad |
|---|---|
| `Monstruo.kt` | Modelo que representa un monstruo mostrado en la aplicación. Incluye nombre, descripción, recurso de imagen, sonido opcional y ruta de fotografía opcional. |
| `Catalogo.kt` | Define los monstruos originales y combina esa lista con los monstruos personalizados recuperados de Room. |

El catálogo devuelve una lista común de objetos `Monstruo`, de modo que la interfaz puede mostrar juntos los elementos originales y los creados por el usuario.

### Persistencia: Room

| Archivo | Responsabilidad |
|---|---|
| `AppDatabase.kt` | Define la base de datos Room y proporciona una instancia compartida. |
| `MonstruoUsuario.kt` | Entidad que representa los monstruos creados por el usuario. |
| `MonstruoUsuarioDao.kt` | Operaciones para insertar, consultar y borrar monstruos personalizados. |
| `Avistamiento.kt` | Entidad que representa un registro del diario. |
| `AvistamientoDAO.kt` | Operaciones para insertar, consultar y borrar avistamientos. |

La base de datos local se llama `bestiario_db` y actualmente utiliza la versión de esquema **4**. No se ha activado el borrado automático de la base de datos ante cambios de esquema: si en una ampliación futura se modifican las entidades, habrá que implementar y probar una migración de Room antes de distribuir esa versión.

### Adaptadores y utilidades

| Archivo | Responsabilidad |
|---|---|
| `MonstruoAdapter.kt` | Convierte cada objeto del catálogo en una fila del RecyclerView. |
| `AvistamientoAdapter.kt` | Muestra los datos de cada avistamiento y solicita confirmación antes de borrarlo. |
| `Fotos.kt` | Crea archivos privados para las fotografías, genera URI mediante FileProvider, copia imágenes de la galería y carga versiones reducidas para mostrarlas. |

### Recursos Android

Los recursos de interfaz se encuentran en `app/src/main/res/`:
- `layout/`: diseños XML de las pantallas y filas de los RecyclerView.
- `drawable/`: imágenes de los monstruos originales y otros recursos gráficos.
- `raw/`: sonidos de los monstruos originales.
- `xml/file_paths.xml`: rutas que FileProvider puede compartir con otras aplicaciones.
- `values/`: temas, colores y otros valores de interfaz.

El manifiesto está en `app/src/main/AndroidManifest.xml`. Declara las Activities, el proveedor de archivos y los permisos de cámara y ubicación.

## 6. Cómo se guardan los datos

- **Monstruos originales**: están definidos en `Catalogo.kt`; no se insertan en Room.
- **Monstruos personalizados**: sus datos se guardan en la tabla `monstruos_usuario`.
- **Avistamientos**: se guardan en la tabla `avistamientos`, incluyendo fecha en milisegundos, lugar, coordenadas opcionales y ruta de la foto.
- **Favoritos**: se guardan como un conjunto de nombres en las preferencias `MisDatos`, bajo la clave `FAVORITOS`.
- **Fotografías**: se guardan como archivos en el almacenamiento privado de la aplicación. En Room se almacena la ruta del archivo, no la imagen completa.

Al desinstalar la aplicación, Android elimina sus datos privados locales.

## 7. Abrir y ejecutar el proyecto

1. Clona el repositorio o descarga la rama que quieras utilizar.
2. Abre la carpeta del proyecto en Android Studio.
3. Espera a que termine la sincronización de Gradle.
4. Ejecuta la aplicación en un dispositivo o emulador compatible.

Configuración declarada actualmente:
- Paquete: `com.example.bestiario`
- `minSdk`: 24
- `targetSdk`: 36
- `compileSdk`: 37

Para probar la cámara y la ubicación, es preferible utilizar un dispositivo físico. En un emulador hay que configurar una cámara virtual y establecer una ubicación desde los controles del propio emulador.

## 8. Posibles ampliaciones

Algunas mejoras que encajan con la estructura actual y pueden servir para seguir aprendiendo:
- Mostrar la ciudad o dirección a partir de las coordenadas mediante Geocoder.
- Integrar Google Maps para visualizar los avistamientos.
- Separar progresivamente la lógica de las Activities mediante Repository y ViewModel.
- Utilizar Flow para observar cambios de Room.
- Añadir migraciones de Room cuando cambie el modelo de datos.
- Permitir editar y eliminar monstruos personalizados.
- Añadir pruebas unitarias y de interfaz.

---

**Nota de desarrollo:** esta documentación describe el código de la rama `AppMejorada2`. Antes de dar por terminada una modificación, sincroniza Gradle y compila y ejecuta la aplicación en Android Studio; la actualización remota del repositorio no sustituye esa comprobación.
