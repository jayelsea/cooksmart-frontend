# CookSmart

CookSmart es una aplicación Android para gestionar y visualizar recetas de cocina.

## Requisitos
- Android Studio (recomendado: versión más reciente)
- JDK 17 o superior
- Conexión a internet para descargar dependencias

## Cómo iniciar el proyecto en otro dispositivo

1. **Clona el repositorio:**
   ```bash
   git clone https://github.com/jayelsea/cooksmart-frontend.git
   ```
  

2. **Abre el proyecto en Android Studio:**
   - Selecciona "Open an existing project" y navega a la carpeta `CookSmart`.

3. **Espera a que Android Studio sincronice y descargue las dependencias:**
   - El proceso puede tardar unos minutos la primera vez.
   - Si se solicita, instala los componentes de Android necesarios.

4. **Configura el archivo `local.properties`:**
   - Android Studio lo generará automáticamente con la ruta de tu SDK.
   - Si no se genera, crea el archivo y añade la línea:
     ```
     sdk.dir=/ruta/a/tu/android/sdk
     ```

5. **Configura Google Services (si usas Firebase):**
   - Coloca tu archivo `google-services.json` en la carpeta `app/` si usas servicios de Google/Firebase.

6. **Ejecuta la app:**
   - Selecciona un dispositivo físico o emulador y haz clic en el botón de ejecución (Run).

## Estructura principal del proyecto
- `app/src/main/java/` : Código fuente Kotlin
- `app/src/main/res/` : Recursos gráficos y layouts
- `app/build.gradle.kts` : Configuración de dependencias
- `.gitignore` : Archivos y carpetas ignorados por git

## Notas
- No subas el archivo `local.properties` ni tus credenciales al repositorio.
- Si tienes problemas con dependencias, usa "File > Sync Project with Gradle Files".
- Para soporte adicional, consulta la documentación oficial de [Android Studio](https://developer.android.com/studio) y [Kotlin](https://kotlinlang.org/).

---



