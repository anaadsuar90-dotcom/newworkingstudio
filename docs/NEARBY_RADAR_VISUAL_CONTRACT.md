# Contrato visual de Nearby Radar

Esta especificación conecta el prototipo visual de GPT Sites con la futura aplicación Android.

## Alcance del MVP

- Un único evento visible.
- Dos modos: **Networking** y **Amistad**.
- Radar apagado por defecto.
- Un único control para activar o desaparecer del radar.
- Cero personas al iniciar.
- Solo se muestra una persona cuando el módulo BLE detecta un anuncio válido de Nearby Radar.
- La acción **Conectar** solo aparecerá dentro de una detección real.

## Recorrido

1. Ver el evento actual.
2. Elegir Networking o Amistad.
3. Activar el radar voluntariamente.
4. Esperar una detección BLE real.
5. Abrir la persona detectada y decidir si conectar.

## Dirección visual

- Fondo azul noche.
- Radar central luminoso en cian y azul eléctrico.
- Verde únicamente para indicar visibilidad activa.
- Superficies oscuras, limpias y con profundidad moderada.
- Tipografía grande y legible.
- Muy pocos controles y sin navegación inferior.

## Estados obligatorios

- Radar apagado: “Nadie puede detectarte”.
- Radar activo sin resultados: contador 0 y “Buscando personas cercanas”.
- Detección real: tarjeta con proximidad aproximada, nunca metros exactos.
- Bluetooth o permiso no disponible: explicación breve y una única acción para resolverlo.

## Prohibido

- Perfiles, saludos, conexiones, distancias o coincidencias ficticias.
- Agenda, chat, contactos, pase digital, filtros o menús adicionales en el MVP.
- Datos personales en el anuncio BLE.
- Presentar la vista web como detección Bluetooth real.

## Referencia

Prototipo visual privado en GPT Sites: https://nearby-radar.ximoteo.chatgpt.site
