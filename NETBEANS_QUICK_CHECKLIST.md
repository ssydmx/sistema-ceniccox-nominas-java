# ✅ Lista de Verificación Rápida - NetBeans 12.2 + Java 8

## 🚀 Pasos Esenciales (5 minutos)

### 1. ⚙️ Configurar JDK 8
```
Tools → Java Platforms → Add Platform → Seleccionar JDK 8
```

### 2. 🔧 Configurar Proyecto
```
Clic derecho en proyecto → Properties → Libraries → Java Platform: JDK 1.8
```

### 3. 📝 Verificar Archivos Clave
**Important Files → Project Properties:**
```properties
javac.source=1.8
javac.target=1.8
j2ee.platform=1.8-web
```

### 4. 🧹 Limpiar y Compilar
```
Clic derecho en proyecto → Clean
Clic derecho en proyecto → Build
```

### 5. ▶️ Ejecutar
```
Clic derecho en proyecto → Run
```

## 🎯 Verificación Rápida

| ✅ | Elemento | Estado |
|---|----------|---------|
| ⬜ | JDK 8 agregado a NetBeans | |
| ⬜ | Proyecto usa JDK 1.8 | |
| ⬜ | javac.source=1.8 | |
| ⬜ | javac.target=1.8 | |
| ⬜ | j2ee.platform=1.8-web | |
| ⬜ | Compila sin errores | |
| ⬜ | Aplicación ejecuta | |

## 🔍 Atajos Útiles NetBeans

| Acción | Atajo |
|--------|-------|
| Ejecutar proyecto | `F6` |
| Limpiar y compilar | `Shift+F11` |
| Organizar imports | `Ctrl+Shift+I` |
| Formatear código | `Alt+Shift+F` |
| Sugerencias Java 8 | `Alt+Enter` |

## 🆘 Problemas Comunes

**Error: "Source level 1.8 requires target level 1.8"**
→ Verificar project.properties tiene ambos en 1.8

**Error: "Cannot find symbol" para lambdas**
→ Verificar que proyecto usa JDK 1.8 en Properties → Libraries

**Servidor no inicia**
→ Usar GlassFish 5+ o Payara 5+ compatible con Java 8

## 📞 Soporte

Si tienes problemas:
1. Revisar ventana `Output` en NetBeans
2. Consultar `NETBEANS_12.2_JAVA8_GUIDE.md` para guía detallada
3. Verificar logs del servidor en `Services` → `Servers`

---
**¡Listo! Tu proyecto Ceniccox ahora usa Java 8 en NetBeans 12.2** 🎉
