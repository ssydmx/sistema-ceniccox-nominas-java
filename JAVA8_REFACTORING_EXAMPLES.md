# Ejemplos de Refactorización para Java 8 - Proyecto Ceniccox

## Introducción

Este documento presenta ejemplos de cómo refactorizar el código existente para aprovechar las nuevas características de Java 8, incluyendo expresiones lambda, Stream API, Optional, y las nuevas APIs de fecha/hora.

## Ejemplos de Refactorización

### 1. Refactorización de Métodos de Combo usando Streams

#### Código Original (Java 7):
```java
public List<SelectItem> getComboMeses() {
    List<SelectItem> combo = new ArrayList<>();
    combo.add(new SelectItem(1, "ENERO"));
    combo.add(new SelectItem(2, "FEBRERO"));
    combo.add(new SelectItem(3, "MARZO"));
    combo.add(new SelectItem(4, "ABRIL"));
    combo.add(new SelectItem(5, "MAYO"));
    combo.add(new SelectItem(6, "JUNIO"));
    combo.add(new SelectItem(7, "JULIO"));
    combo.add(new SelectItem(8, "AGOSTO"));
    combo.add(new SelectItem(9, "SEPTIEMBRE"));
    combo.add(new SelectItem(10, "OCTUBRE"));
    combo.add(new SelectItem(11, "NOVIEMBRE"));
    combo.add(new SelectItem(12, "DICIEMBRE"));
    return combo;
}
```

#### Código Refactorizado (Java 8):
```java
import java.util.stream.IntStream;
import java.util.stream.Collectors;

public List<SelectItem> getComboMeses() {
    String[] meses = {"ENERO", "FEBRERO", "MARZO", "ABRIL", "MAYO", "JUNIO",
                      "JULIO", "AGOSTO", "SEPTIEMBRE", "OCTUBRE", "NOVIEMBRE", "DICIEMBRE"};
    
    return IntStream.range(0, meses.length)
            .mapToObj(i -> new SelectItem(i + 1, meses[i]))
            .collect(Collectors.toList());
}

// Alternativa más funcional:
public List<SelectItem> getComboMesesFuncional() {
    return Arrays.asList("ENERO", "FEBRERO", "MARZO", "ABRIL", "MAYO", "JUNIO",
                        "JULIO", "AGOSTO", "SEPTIEMBRE", "OCTUBRE", "NOVIEMBRE", "DICIEMBRE")
            .stream()
            .map(mes -> new SelectItem(
                Arrays.asList("ENERO", "FEBRERO", "MARZO", "ABRIL", "MAYO", "JUNIO",
                             "JULIO", "AGOSTO", "SEPTIEMBRE", "OCTUBRE", "NOVIEMBRE", "DICIEMBRE")
                      .indexOf(mes) + 1, mes))
            .collect(Collectors.toList());
}
```

### 2. Refactorización de Métodos de Validación de Servicios

#### Código Original (Java 7):
```java
public boolean isServicioGrupoBakertilly() {
    String nombrecorto = ControladorSesiones.getInstance().getCompaniaSession().getNombreCorto();
    boolean isServicio = false;
    switch (nombrecorto) {
        case Parametros.SERVICIO_EPAM:
        case Parametros.SERVICIO_EGELHOF:
        case Parametros.SERVICIO_BAKERTILLY:
        // ... más casos
            isServicio = true;
            break;
    }
    return isServicio;
}
```

#### Código Refactorizado (Java 8):
```java
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;

// Definir conjunto de servicios como constante de clase
private static final Set<String> SERVICIOS_GRUPO_BAKERTILLY = new HashSet<>(Arrays.asList(
    Parametros.SERVICIO_EPAM,
    Parametros.SERVICIO_EGELHOF,
    Parametros.SERVICIO_BAKERTILLY,
    Parametros.SERVICIO_IREP,
    Parametros.SERVICIO_ESENTTIA,
    // ... resto de servicios
));

public boolean isServicioGrupoBakertilly() {
    return Optional.ofNullable(ControladorSesiones.getInstance().getCompaniaSession())
            .map(Compania::getNombreCorto)
            .map(SERVICIOS_GRUPO_BAKERTILLY::contains)
            .orElse(false);
}

// Versión más concisa usando método de referencia
public boolean isServicioGrupoBakertillyV2() {
    return Optional.ofNullable(ControladorSesiones.getInstance().getCompaniaSession())
            .map(Compania::getNombreCorto)
            .filter(SERVICIOS_GRUPO_BAKERTILLY::contains)
            .isPresent();
}
```

### 3. Uso de Optional para Manejo Seguro de Nulos

#### Código Original (Java 7):
```java
public String getNombreCortoCompania() {
    return ControladorSesiones.getInstance().getCompaniaSession().getNombreCorto();
}
```

#### Código Refactorizado (Java 8):
```java
import java.util.Optional;

public Optional<String> getNombreCortoCompania() {
    return Optional.ofNullable(ControladorSesiones.getInstance().getCompaniaSession())
            .map(Compania::getNombreCorto);
}

// Para mantener compatibilidad con JSF, versión que retorna String
public String getNombreCortoCompaniaSeguro() {
    return Optional.ofNullable(ControladorSesiones.getInstance().getCompaniaSession())
            .map(Compania::getNombreCorto)
            .orElse("Sin Compañía");
}
```

### 4. Refactorización usando Nuevas APIs de Fecha/Hora

#### Código Original (Java 7):
```java
import java.util.Date;

public Date getFechaActual() {
    return new Date();
}

public Date getHoy() {
    return new Date();
}
```

#### Código Refactorizado (Java 8):
```java
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

// Usando nuevas APIs de fecha/hora
public LocalDate getFechaActual() {
    return LocalDate.now();
}

public LocalDateTime getFechaHoraActual() {
    return LocalDateTime.now();
}

public ZonedDateTime getFechaHoraConZona() {
    return ZonedDateTime.now();
}

// Para compatibilidad con JSF (que espera Date)
public Date getFechaActualCompatible() {
    return Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
}

// Método utilitario para formateo
public String getFechaFormateada() {
    return LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
}
```

### 5. Creación de Utilidades Funcionales

#### Nueva Clase Utilitaria (Java 8):
```java
package com.lam.cenicco.util;

import javax.faces.model.SelectItem;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.function.Function;
import java.util.Arrays;

/**
 * Utilidades funcionales para Java 8
 * @author Sistema Ceniccox
 */
public class Java8Utils {
    
    /**
     * Crea una lista de SelectItem a partir de un mapa
     */
    public static List<SelectItem> createSelectItems(Map<Object, String> items) {
        return items.entrySet().stream()
                .map(entry -> new SelectItem(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }
    
    /**
     * Crea una lista de SelectItem numerada
     */
    public static List<SelectItem> createNumberedSelectItems(String[] labels) {
        return IntStream.range(0, labels.length)
                .mapToObj(i -> new SelectItem(i + 1, labels[i]))
                .collect(Collectors.toList());
    }
    
    /**
     * Crea una lista de SelectItem a partir de un enum
     */
    public static <E extends Enum<E>> List<SelectItem> createEnumSelectItems(Class<E> enumClass) {
        return Arrays.stream(enumClass.getEnumConstants())
                .map(e -> new SelectItem(e, e.name()))
                .collect(Collectors.toList());
    }
    
    /**
     * Filtra y transforma una lista usando predicados y funciones
     */
    public static <T, R> List<R> filterAndTransform(List<T> list, 
                                                   java.util.function.Predicate<T> filter,
                                                   Function<T, R> mapper) {
        return list.stream()
                .filter(filter)
                .map(mapper)
                .collect(Collectors.toList());
    }
}
```

### 6. Refactorización de Validaciones con Predicados

#### Código Original (Java 7):
```java
// Múltiples métodos similares para validar servicios
public boolean isServicioPumasa() {
    String nombrecorto = ControladorSesiones.getInstance().getCompaniaSession().getNombreCorto();
    return Parametros.SERVICIO_PUMASA.equals(nombrecorto);
}

public boolean isServicioStone() {
    String nombrecorto = ControladorSesiones.getInstance().getCompaniaSession().getNombreCorto();
    return Parametros.SERVICIO_STONE.equals(nombrecorto);
}
```

#### Código Refactorizado (Java 8):
```java
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Método genérico para validar servicios
 */
private boolean validarServicio(String servicioEsperado) {
    return Optional.ofNullable(ControladorSesiones.getInstance().getCompaniaSession())
            .map(Compania::getNombreCorto)
            .filter(servicioEsperado::equals)
            .isPresent();
}

// Uso de Supplier para lazy evaluation
private final Supplier<String> nombreCortoCompania = () -> 
    Optional.ofNullable(ControladorSesiones.getInstance().getCompaniaSession())
            .map(Compania::getNombreCorto)
            .orElse("");

public boolean isServicioPumasa() {
    return validarServicio(Parametros.SERVICIO_PUMASA);
}

public boolean isServicioStone() {
    return validarServicio(Parametros.SERVICIO_STONE);
}

// Predicado reutilizable
private final Predicate<String> esServicioValido = servicio -> 
    !servicio.isEmpty() && servicio.length() > 3;

public boolean tieneServicioValido() {
    return esServicioValido.test(nombreCortoCompania.get());
}
```

## Beneficios de la Refactorización

### 1. **Código más Conciso**
- Menos líneas de código
- Mayor expresividad
- Reducción de código repetitivo

### 2. **Mejor Rendimiento**
- Lazy evaluation con Streams
- Operaciones paralelas cuando sea apropiado
- Mejor gestión de memoria

### 3. **Manejo Seguro de Nulos**
- Uso de Optional elimina NullPointerException
- Código más robusto y predecible

### 4. **Mejor Mantenibilidad**
- Código más legible y expresivo
- Separación clara de responsabilidades
- Reutilización de componentes funcionales

### 5. **APIs Modernas**
- Uso de nuevas APIs de fecha/hora
- Mejor integración con frameworks modernos

## Recomendaciones de Implementación

### 1. **Migración Gradual**
```java
// Mantener métodos originales para compatibilidad
@Deprecated
public List<SelectItem> getComboMesesOld() {
    // implementación original
}

// Nuevo método con Java 8
public List<SelectItem> getComboMeses() {
    // implementación con streams
}
```

### 2. **Pruebas Unitarias**
```java
@Test
public void testComboMesesJava8() {
    List<SelectItem> meses = appBean.getComboMeses();
    assertEquals(12, meses.size());
    assertEquals("ENERO", meses.get(0).getLabel());
    assertEquals(1, meses.get(0).getValue());
}
```

### 3. **Documentación**
```java
/**
 * Genera lista de meses usando Java 8 Streams API
 * @return Lista de SelectItem con meses del año
 * @since Java 8 Migration
 */
public List<SelectItem> getComboMeses() {
    // implementación
}
```

## Consideraciones de Compatibilidad

### 1. **JSF Compatibility**
- JSF espera ciertos tipos de retorno (Date, List, etc.)
- Mantener interfaces compatibles cuando sea necesario
- Usar adaptadores cuando sea apropiado

### 2. **Serialización**
- Lambdas no son serializables por defecto
- Usar clases anónimas o implementaciones serializables cuando sea necesario

### 3. **Rendimiento**
- Streams tienen overhead para colecciones pequeñas
- Evaluar caso por caso si la refactorización mejora el rendimiento

## Conclusión

La refactorización a Java 8 ofrece múltiples beneficios en términos de legibilidad, mantenibilidad y robustez del código. La implementación debe ser gradual y bien probada para asegurar la compatibilidad con el sistema existente.

---

**Nota**: Estos ejemplos son sugerencias de refactorización. La implementación real debe considerar las necesidades específicas del proyecto y mantener la compatibilidad con JSF y otras dependencias existentes.
