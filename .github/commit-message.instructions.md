# Instrucciones Básicas para Mensajes de Commit con GitHub Copilot

## Objetivo

Utilizar GitHub Copilot para generar mensajes de commit claros, concisos y estandarizados, preferiblemente siguiendo el formato **Conventional Commits**.

# **Importante** los mensajes de commit deben ser en **inglés**.

## Guía Rápida del Formato Conventional Commits (para tu referencia)

1.  **`<tipo>`:** Obligatorio. Define la naturaleza del cambio:

    - `feat`: Una nueva funcionalidad (feature).
    - `fix`: Una corrección de bug.
    - `chore`: Tareas de mantenimiento, build, dependencias (sin impacto en código de producción).
    - `docs`: Cambios en la documentación.
    - `style`: Cambios de formato, espacios, comas (sin cambio lógico).
    - `refactor`: Refactorización de código sin cambiar funcionalidad externa.
    - `test`: Añadir o corregir tests.
    - `perf`: Mejora de rendimiento.
    - `ci`: Cambios en la configuración de CI/CD.
    - `build`: Cambios que afectan el sistema de build o dependencias externas.

2.  **`(<ámbito opcional>)`:** Opcional. Especifica la parte del código afectada (ej: `api`, `ui`, `auth`, `producto-service`). Ayuda a dar contexto rápido.

3.  **`:`:** Separador obligatorio después del tipo/ámbito.

4.  **`<descripción corta>`:** Obligatoria. Resumen conciso del cambio:

    - En **minúsculas**.
    - En **modo imperativo, tiempo presente** (ej: "añadir", "corregir", "actualizar", NO "añadido", "corrigiendo").
    - **Sin punto final**.
    - Idealmente **menos de 50 caracteres**.

5.  **(Opcional) Cuerpo:** Párrafo(s) explicando el _por qué_ del cambio, contexto adicional. Separado del encabezado por una línea en blanco.

6.  **(Opcional) Pie de Página:** Para referenciar issues (`Closes #123`, `Refs #456`) o indicar `BREAKING CHANGE:`. Separado del cuerpo por una línea en blanco.