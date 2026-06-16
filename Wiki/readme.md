# CloudStorage Project — Nube privada del hogar

## Objetivo
Montar y mantener una **nube privada de hogar, autoalojada y escalable**, sobre **Nextcloud**, donde la familia pueda subir y sincronizar sus fotos, documentos, música, etc., y donde además se alojen las bibliotecas de almacenamiento masivo del propietario (preservación de videojuegos, películas, series, música) y sus entornos de desarrollo.

No es un producto comercial. Es infraestructura personal/familiar diseñada para crecer de forma ordenada durante 5–10 años y, eventualmente, **descentralizarse en varios hogares** con redundancia parcial entre servidores.

> ℹ️ Si en el futuro sobrara capacidad y se quisiera comercializar, se valoraría una capa de pagos/suscripciones (Stripe) sobre la gestión de usuarios de Nextcloud. **Hoy no es prioridad y no condiciona el diseño.**

## Usuarios y cargas
- **Usuarios:** ~5 hoy (familia, incluido el propietario), hasta ~8 a futuro.
- **Cargas de datos** (ver [tiers en almacenamiento](Arquitectura/02-Almacenamiento.md)):
  - Nextcloud familiar (fotos, documentos) — *crítico, irremplazable*.
  - Preservación de videojuegos (incl. biblioteca Switch) — *semi-crítico*.
  - Películas y series — *bulk, recuperable*.
  - Música — *pequeño, crece despacio*.
  - Proyectos y entornos de desarrollo del propietario (SSH, Docker).

## Stack
- **SO:** Debian + **Cockpit** (panel web de gestión).
- **Almacenamiento:** **ZFS** (RAIDZ2).
- **Servicios:** Nextcloud, Jellyfin (multimedia, con transcodificación por hardware QuickSync), Navidrome (música), Docker, monitorización con Grafana/Prometheus.
- **Sin backup:** la única protección de datos es el **RAIDZ2** (riesgo asumido: no cubre borrados, ransomware, robo ni incendio).
- **Red futura (opcional):** 2º servidor por **subdominios** (`levelcloud.hdglabs.com` / `casa-b.levelcloud.hdglabs.com`), islas independientes. Ver [03](Arquitectura/03-Red-y-Geodistribucion.md).

## Documentación de arquitectura
1. [00 — Visión y alcance](Arquitectura/00-Vision.md)
2. [01 — Hardware](Arquitectura/01-Hardware.md)
3. [02 — Almacenamiento (ZFS / RAIDZ2)](Arquitectura/02-Almacenamiento.md)
4. [03 — Red y geo-distribución](Arquitectura/03-Red-y-Geodistribucion.md)

## Operación del sistema
Guías de instalación y operación del servidor (estado real montado):
- [Servidor (firmware, MySQL, monitorización, cron)](../Sistema/Servidor.md)
- [Nextcloud](../Sistema/Nextcloud.md)
- [SMB / Samba](../Sistema/SMB%20(Samba).md)

---
> **Nota histórica:** el proyecto arrancó como una arquitectura de microservicios (Spring Boot: Auth, Gateway, Eureka, Config, Music). **Esa parte está cancelada** y se está eliminando del repositorio. El servicio de descarga de música desde YouTube (microservicio *Music*) queda **descartado**.
