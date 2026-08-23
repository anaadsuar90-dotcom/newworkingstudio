# Nearby Radar — MVP BLE Android

Aplicación Android nativa en Kotlin y Jetpack Compose para comprobar, primero con dos móviles reales, el núcleo de detección BLE de Nearby Radar.

## Estado de esta iteración

- Radar desactivado al iniciar.
- BLE advertising y scanning nativos de Android, sin Bluetooth clásico, emparejamiento ni conexiones GATT.
- El anuncio contiene solo un UUID de servicio propio y un identificador aleatorio temporal que rota cada 15 minutos.
- El escáner ignora cualquier anuncio que no pertenezca al protocolo Nearby Radar.
- No se envían ni se leen nombres, MAC, fotografías, teléfonos, correo, perfiles ni redes sociales por BLE.
- Cada resultado muestra RSSI suavizado y una banda de proximidad: **Muy cerca**, **Cerca** o **A cierta distancia**. No se presenta como distancia exacta.
- Los resultados desaparecen tras 25 segundos sin recibir un anuncio válido.

Todavía no hay backend, perfiles compartidos, chat, intercambio de contactos ni notificaciones en segundo plano. Las pantallas generadas por IA se conservan como base visual, pero el radar parte vacío: nunca debe inventar asistentes, saludos o coincidencias.

## Permisos

- Android 12 o superior: `BLUETOOTH_SCAN`, `BLUETOOTH_ADVERTISE` y `BLUETOOTH_CONNECT`.
- Android 6 a 11: ubicación precisa solo porque Android la exige para BLE scanning en esas versiones.

No se solicita Internet, Wi‑Fi, contactos, cámara ni ubicación en Android 12+.

## Prueba con dos móviles

1. Abre el proyecto en Android Studio y espera a que Gradle sincronice.
2. Conecta cada Android con un cable USB de datos, activa **Opciones de desarrollador → Depuración USB** y acepta el aviso RSA.
3. Ejecuta la variante `debug` en ambos dispositivos físicos. El emulador no permite validar advertising/scanning BLE real.
4. En el móvil A activa la visibilidad/radar para iniciar advertising.
5. En el móvil B activa el escaneo. Debe aparecer un único “Usuario Nearby” con un ID temporal, RSSI y banda de proximidad.
6. Acerca y aleja los teléfonos para observar que cambia RSSI y la banda. Las paredes, el cuerpo y los modelos de móvil afectan mucho a la señal.

## Privacidad

El radar es siempre voluntario. Esta fase solo funciona mientras la app está abierta y no permite rastreo persistente. La siguiente fase debe diseñar de forma explícita el consentimiento para revelar cualquier perfil o enviar un saludo.
