# Control de Gastos Personales

Una aplicación Android moderna diseñada para ayudar a los usuarios a gestionar sus finanzas personales de manera eficiente y segura. Desarrollada con las últimas tecnologías de Android, esta app permite registrar gastos, categorizarlos y visualizar totales mensuales, todo sincronizado en la nube.

## 🚀 Características Principales

- **Autenticación Segura**: Sistema de inicio de sesión y registro utilizando **Firebase Authentication**.
- **Google Sign-In**: Integración rápida y sencilla con cuentas de Google.
- **Gestión de Gastos**: Registro detallado de gastos incluyendo nombre, monto, categoría y fecha.
- **Almacenamiento en la Nube**: Sincronización en tiempo real mediante **Cloud Firestore**.
- **Interfaz Moderna**: UI reactiva y fluida construida íntegramente con **Jetpack Compose** y **Material Design 3**.
- **Análisis de Gastos**: Filtrado por categorías y cálculo automático del total de gastos mensuales.

## 🛠️ Tecnologías Utilizadas

- **Lenguaje**: [Kotlin](https://kotlinlang.org/)
- **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose)
- **Backend**: [Firebase](https://firebase.google.com/) (Authentication & Firestore)
- **Arquitectura**: MVVM (Model-View-ViewModel)
- **Componentes de Arquitectura**: 
  - StateFlow para el manejo de estados reactivos.
  - Coroutines para operaciones asíncronas.
  - ViewModel para separar la lógica de negocio de la UI.
- **Gestión de Dependencias**: Gradle con Kotlin DSL y Version Catalogs (libs.versions.toml).

## 📂 Estructura del Proyecto

El código fuente principal se encuentra en `app/src/main/java/com/example/controlgastos/`:

- `MainActivity.kt`: Punto de entrada y lógica de navegación.
- `LoginScreen.kt` & `RegisterScreen.kt`: Pantallas de autenticación.
- `HomeScreen.kt`: Pantalla de bienvenida y acceso principal.
- `GastosScreen.kt`: Interfaz principal para la gestión y visualización de gastos.
- `AuthViewModel.kt`: Maneja la lógica de autenticación y estado del usuario.
- `GastosViewModel.kt`: Gestiona la recuperación, filtrado y cálculos de los gastos.

## ⚙️ Configuración e Instalación

Para ejecutar este proyecto localmente, sigue estos pasos:

1. **Clonar el repositorio**:
   ```bash
   git clone https://github.com/RachelSaenz/FORO2-DSM.git
   ```

2. **Configurar Firebase**:
   - Crea un proyecto en la [Consola de Firebase](https://console.firebase.google.com/).
   - Añade una aplicación Android con el paquete `com.example.controlgastos`.
   - Descarga el archivo `google-services.json` y colócalo en el directorio `app/` del proyecto.
   - Habilita **Email/Password** y **Google** como proveedores de inicio de sesión.
   - Crea una base de datos de **Cloud Firestore**.

3. **Configurar Variables Locales**:
   - En tu archivo `local.properties`, añade tu Web Client ID de Google para que el inicio de sesión funcione:
     ```properties
     GOOGLE_WEB_CLIENT_ID=TU_WEB_CLIENT_ID_AQUI
     ```

4. **Compilar y Ejecutar**:
   - Abre el proyecto en **Android Studio**.
   - Sincroniza el proyecto con los archivos Gradle.
   - Ejecuta la aplicación en un emulador o dispositivo físico.

---
*Este proyecto fue desarrollado como parte del curso de Desarrollo de Software Móvil (DSM) de la Universidad Don Bosco.*
