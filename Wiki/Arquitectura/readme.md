# Arquitectura — Índice

Documentación de arquitectura de la nube privada del hogar. Lee en orden:

| # | Documento | Contenido |
|---|---|---|
| 00 | [Visión y alcance](00-Vision.md) | Objetivo, usuarios, tiers de datos, principios de diseño, hoja de ruta |
| 01 | [Hardware](01-Hardware.md) | Servidor actual, lista de compra definitiva, ampliaciones |
| 02 | [Almacenamiento](02-Almacenamiento.md) | ZFS, RAIDZ2, capacidad real, crecimiento por vdevs, datasets/tiering |
| 03 | [Red y geo-distribución](03-Red-y-Geodistribucion.md) | Red actual, acceso externo, descentralización en 2 hogares |
| 04 | [Servicios y operación](04-Servicios-y-Operacion.md) | Dependencias del sistema, servicios del host, contenedores, migración, firewall |

## Resumen ejecutivo

- **Qué:** un único servidor x86 (torre) con Debian + ZFS RAIDZ2 sirviendo Nextcloud y servicios multimedia/dev para ~5–8 usuarios del hogar.
- **Almacenamiento:** 1 pool ZFS, 1 vdev RAIDZ2 de **6× 12 TB** (≈33 TiB útiles cómodos). Crece añadiendo un 2º vdev en el futuro.
- **Datos por tier:** clasificación hot/cold para organizar los datasets. **Sin backup:** la única protección es el RAIDZ2.
- **Futuro (opcional):** 2º servidor en casa de un familiar por **subdominios** (`levelcloud` / `casa-b.levelcloud.hdglabs.com`), islas **independientes sin copia entre casas**.
