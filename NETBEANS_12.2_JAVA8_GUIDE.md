# Guía para Aplicar Actualización a Java 8 en NetBeans 12.2

## Introducción
Esta guía te ayudará a aplicar todos los cambios de actualización a Java 8 directamente desde NetBeans 12.2, aprovechando las herramientas gráficas del IDE.

## Paso 1: Verificar/Configurar JDK 8 en NetBeans

### 1.1 Agregar JDK 8 a NetBeans
1. **Abrir NetBeans 12.2**
2. **Ir a**: `Tools` → `Java Platforms`
3. **Hacer clic en**: `Add Platform...`
4. **Seleccionar**: `Java Standard Edition`
5. **Navegar** hasta la carpeta donde tienes instalado JDK 8
   - Windows: `C:\Program Files\Java\jdk1.8.0_xxx`
   - Linux: `/usr/lib/jvm/java-8-openjdk-amd64`
   - macOS: `/Library/Java/JavaVirtualMachines/jdk1.8.0_xxx.jdk/Contents/Home`
6. **Hacer clic en**: `Next` → `Finish`

### 1.2 Si no tienes JDK 8 instalado:
1. **Descargar JDK 8** desde: https://adoptium.net/temurin/releases/?version=8
2. **Instalar** siguiendo las instrucciones del instalador
3. **Repetir** el paso 1.1

## Paso 2: Abrir el Proyecto en NetBeans

### 2.1 Abrir Proyecto
1. **File** → `Open Project`
2. **Navegar** hasta la carpeta del proyecto Ceniccox
3. **Seleccionar** la carpeta del proyecto
4. **Hacer clic en**: `Open Project`

### 2.2 Verificar que se abrió correctamente
- Deberías ver la estructura del proyecto en el panel `Projects`
- Verificar que aparezcan las carpetas: `Source Packages`, `Web Pages`, `Libraries`, etc.

## Paso 3: Configurar el Proyecto para Java 8

### 3.1 Cambiar la Plataforma Java del Proyecto
1. **Clic derecho** en el nombre del proyecto (Ceniccox) en el panel Projects
2. **Seleccionar**: `Properties`
3. **En el panel izquierdo**, seleccionar: `Libraries`
4. **En "Java Platform"**, seleccionar: `JDK 1.8` (el que agregaste en el Paso 1)
5. **Hacer clic en**: `OK`

### 3.2 Verificar Configuración de Compilación
1. **Clic derecho** en el proyecto → `Properties`
2. **Seleccionar**: `Build` → `Compiling`
3. **Verificar que**:
   - `Source/Binary Format`: `JDK 8`
   - Si no aparece, seleccionarlo de la lista desplegable
4. **Hacer clic en**: `OK`

## Paso 4: Actualizar Configuración del Servidor

### 4.1 Configurar Servidor de Aplicaciones
1. **Clic derecho** en el proyecto → `Properties`
2. **Seleccionar**: `Run`
3. **En "Server"**: Verificar que tengas un servidor compatible con Java 8:
   - GlassFish 5.x o superior
   - Payara 5.x o superior
   - Tomcat 9.x o superior
4. **Si necesitas agregar un servidor**:
   - **Hacer clic en**: `Add...`
   - **Seleccionar** el tipo de servidor
   - **Seguir** el asistente de configuración

## Paso 5: Verificar y Aplicar Cambios en Archivos de Configuración

### 5.1 Verificar project.properties
1. **En el panel Projects**, expandir: `Important Files`
2. **Hacer doble clic** en: `Project Properties`
3. **Verificar** que contenga las siguientes líneas:
   ```properties
   javac.source=1.8
   javac.target=1.8
   j2ee.platform=1.8-web
   ```
4. **Si no están presentes**, agregarlas o modificarlas
5. **Guardar** el archivo (`Ctrl+S`)

### 5.2 Verificar web.xml
1. **Expandir**: `Web Pages` → `WEB-INF`
2. **Hacer doble clic** en: `web.xml`
3. **Verificar** que la primera línea contenga:
   ```xml
   <web-app version="3.1" xmlns="http://xmlns.jcp.org/xml/ns/javaee"
   ```
4. **Si es necesario**, actualizar la versión

### 5.3 Verificar persistence.xml
1. **Expandir**: `Source Packages` → `conf`
2. **Hacer doble clic** en: `persistence.xml`
3. **Verificar** que contenga:
   ```xml
   <persistence version="2.1" xmlns="http://xmlns.jcp.org/xml/ns/persistence"
   ```

## Paso 6: Actualizar Dependencias (Librerías)

### 6.1 Revisar Librerías del Proyecto
1. **En el panel Projects**, expandir: `Libraries`
2. **Revisar** las librerías listadas
3. **Si hay librerías muy antiguas**, considerar actualizarlas:

### 6.2 Agregar/Actualizar Librerías (si es necesario)
1. **Clic derecho** en `Libraries`
2. **Seleccionar**: `Add JAR/Folder...`
3. **Navegar** hasta la carpeta `lib/` del proyecto
4. **Seleccionar** las librerías actualizadas
5. **Hacer clic en**: `Open`

## Paso 7: Limpiar y Compilar el Proyecto

### 7.1 Limpiar Proyecto
1. **Clic derecho** en el proyecto
2. **Seleccionar**: `Clean`
3. **Esperar** a que termine el proceso

### 7.2 Compilar Proyecto
1. **Clic derecho** en el proyecto
2. **Seleccionar**: `Build`
3. **Revisar** la ventana `Output` para verificar que no hay errores

### 7.3 Si hay errores de compilación:
1. **Revisar** los mensajes de error en la ventana `Output`
2. **Verificar** que todas las dependencias estén correctas
3. **Asegurarse** de que el JDK 8 esté correctamente configurado

## Paso 8: Probar el Proyecto

### 8.1 Ejecutar el Proyecto
1. **Clic derecho** en el proyecto
2. **Seleccionar**: `Run`
3. **NetBeans** debería:
   - Compilar el proyecto
   - Desplegarlo en el servidor
   - Abrir el navegador con la aplicación

### 8.2 Verificar Funcionamiento
1. **Probar** las funcionalidades principales
2. **Revisar** los logs del servidor para errores
3. **Verificar** que la aplicación funcione correctamente

## Paso 9: Configuraciones Adicionales de NetBeans

### 9.1 Configurar Encoding del Proyecto
1. **Clic derecho** en el proyecto → `Properties`
2. **Seleccionar**: `Sources`
3. **En "Encoding"**: Seleccionar `UTF-8`
4. **Hacer clic en**: `OK`

### 9.2 Configurar Formato de Código
1. **Tools** → `Options`
2. **Seleccionar**: `Editor` → `Formatting`
3. **En "Language"**: Seleccionar `Java`
4. **Configurar** según tus preferencias
5. **Hacer clic en**: `OK`

## Paso 10: Aprovechar Características de Java 8 en NetBeans

### 10.1 Habilitar Sugerencias de Java 8
1. **Tools** → `Options`
2. **Seleccionar**: `Editor` → `Hints`
3. **En "Language"**: Seleccionar `Java`
4. **Expandir**: `JDK 8`
5. **Habilitar** las sugerencias que desees:
   - `Convert to Lambda`
   - `Convert to Method Reference`
   - `Use Optional`

### 10.2 Usar Refactoring Automático
1. **Seleccionar** código que pueda beneficiarse de Java 8
2. **Presionar**: `Alt+Enter` para ver sugerencias
3. **Seleccionar** refactorings como:
   - "Convert to lambda expression"
   - "Convert to method reference"
   - "Replace with Optional"

## Solución de Problemas Comunes

### Problema 1: "Source level 1.8 requires target level 1.8"
**Solución**:
1. Verificar que tanto `javac.source` como `javac.target` estén en `1.8`
2. Limpiar y recompilar el proyecto

### Problema 2: "Cannot find symbol" para características de Java 8
**Solución**:
1. Verificar que el proyecto esté usando JDK 8
2. Revisar las importaciones necesarias (ej: `java.util.stream.*`)

### Problema 3: Servidor no compatible
**Solución**:
1. Actualizar a GlassFish 5+ o Payara 5+
2. O configurar Tomcat 9+ con las librerías Java EE necesarias

### Problema 4: Errores de dependencias
**Solución**:
1. Verificar que todas las librerías en `lib/` sean compatibles con Java 8
2. Actualizar librerías muy antiguas si es necesario

## Comandos Útiles en NetBeans

### Atajos de Teclado Útiles:
- **Ctrl+Shift+I**: Organizar imports
- **Alt+Shift+F**: Formatear código
- **Ctrl+Space**: Autocompletado
- **Alt+Enter**: Mostrar sugerencias/fixes
- **F6**: Ejecutar proyecto
- **Shift+F11**: Limpiar y compilar

### Ventanas Importantes:
- **Window** → `Output`: Ver logs de compilación y servidor
- **Window** → `Services`: Gestionar servidores y bases de datos
- **Window** → `Navigator**: Navegar por la estructura del código

## Verificación Final

### Lista de Verificación:
- [ ] JDK 8 configurado en NetBeans
- [ ] Proyecto configurado para usar JDK 8
- [ ] `javac.source=1.8` y `javac.target=1.8` en project.properties
- [ ] `j2ee.platform=1.8-web` configurado
- [ ] Proyecto compila sin errores
- [ ] Aplicación se ejecuta correctamente
- [ ] Servidor compatible configurado

## Conclusión

Siguiendo estos pasos, habrás actualizado exitosamente tu proyecto Ceniccox a Java 8 usando NetBeans 12.2. El IDE te proporcionará todas las herramientas necesarias para aprovechar las nuevas características de Java 8, incluyendo sugerencias automáticas para refactorizar código existente.

**¡Tu proyecto está ahora listo para aprovechar todas las características modernas de Java 8!**

---

**Nota**: Si encuentras algún problema específico durante el proceso, revisa los logs en la ventana `Output` de NetBeans para obtener información detallada sobre los errores.
