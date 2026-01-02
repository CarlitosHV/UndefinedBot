Este proyecto es un bot de música para Discord desarrollado en Java y gestionado con Gradle. Está pensado para reproducir audio en canales de voz de forma sencilla, estable y extensible.

Características
Reproducción de música desde enlaces (por ejemplo, YouTube/URLs de audio soportadas por la librería de audio que utilices).

Comandos básicos de control: reproducir, pausar, reanudar, saltar, detener.

Sistema de cola por servidor (guild) para gestionar múltiples canciones.

Prefijo configurable para los comandos.

Arquitectura modular para facilitar la ampliación con nuevos comandos.

Requisitos
Java 17 o superior instalado (configurable según TU versión objetivo).

Gradle 7+ instalado globalmente o uso del wrapper incluido (./gradlew / gradlew.bat).

Token de bot de Discord creado en el portal de desarrolladores de Discord.

Dependencias típicas: librería de Discord (por ejemplo, JDA) y librería de audio (por ejemplo, LavaPlayer).
