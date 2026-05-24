# Estructura del Proyecto UnitWise

Esta es la organización de carpetas y archivos del proyecto:

```text
UnitWise/
├── app/                        # Módulo principal de la aplicación
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/br444n/unitwise/app/
│   │   │   │   ├── core/       # Utilidades y componentes base
│   │   │   │   ├── data/       # Capa de datos (Local, Repositorios)
│   │   │   │   │   ├── local/  # Base de datos Room, DAOs, Entidades
│   │   │   │   │   └── repository/ # Implementaciones de repositorios
│   │   │   │   ├── di/         # Inyección de dependencias (Hilt)
│   │   │   │   ├── domain/     # Lógica de negocio (Modelos, Casos de uso)
│   │   │   │   ├── feature/    # Características principales (UI + ViewModels)
│   │   │   │   │   ├── home/
│   │   │   │   │   ├── history/
│   │   │   │   │   ├── comparison/
│   │   │   │   │   ├── shoppingList/
│   │   │   │   │   └── ...
│   │   │   │   ├── navigation/ # Configuración de NavHost y rutas
│   │   │   │   ├── ui/         # Temas, componentes globales y estilos
│   │   │   │   ├── MainActivity.kt
│   │   │   │   └── UnitWiseApplication.kt
│   │   │   └── res/            # Recursos (Layouts, Drawables, Strings)
│   │   │       ├── drawable/
│   │   │       ├── values/     # Strings, Colores, Dimens
│   │   │       └── ...
│   ├── build.gradle.kts        # Configuración de build del módulo
│   └── ...
├── gradle/                     # Gradle Wrapper
├── build.gradle.kts            # Configuración de build del proyecto
├── settings.gradle.kts         # Configuración de módulos del proyecto
├── README.md                   # Documentación principal
└── project-structure.md        # Este archivo
```
