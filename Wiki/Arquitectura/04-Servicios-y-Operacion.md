# 04 — Servicios y operación

Inventario del **servidor de servicios** y guía de puesta en marcha: dependencias del sistema, servicios del host, contenedores y migración. Objetivo: que con este documento se pueda **levantar el servidor desde cero** de forma profesional.

> 🔒 **Secretos fuera de git.** Este documento **no** contiene contraseñas, claves ni tokens. Viven en el **runbook privado** (OneDrive) y/o en un gestor de secretos. Al final se lista *qué* secretos hacen falta, sin sus valores.
>
> Dominio de ejemplo: `hdglabs.com` (subdominios `levelcloud.`, `minio.`, `s3.minio.`, `music.`, `grafana.`, `gamehub.`…). Ver [03 — Red](03-Red-y-Geodistribucion.md).

## Capas
1. **Sistema operativo:** Debian.
2. **Servicios del host (systemd):** la base; deben estar **arriba antes** que los stacks Docker.
3. **Stacks Docker:** cada servicio en `~/services/<nombre>`, se levanta con `./up.sh`.

## Servicios del host (systemd)
| Servicio | Qué es | Notas |
|---|---|---|
| `docker` | runtime de contenedores | base de todo |
| `mariadb` | BBDD de Nextcloud + Grafana (`/var/lib/mysql`) | usuarios: `nextclouduser`, `grafana`, `exporter` |
| `wg-quick@wg0` | WireGuard VPN (`10.0.0.0/24`) | config en `/etc/wireguard/` |
| `apache2` | **reverse proxy + TLS** (event MPM, HTTP/2) | vhosts en `/etc/apache2/sites-available`; certs Let's Encrypt (`certbot`) |
| `cron` | tareas programadas (incl. respaldo de config/BBDD a OneDrive vía `rclone`) | — |

> **Orden de arranque:** primero los servicios del host —sobre todo **MariaDB**, de la que dependen Nextcloud y el monitoring— y luego los stacks Docker.

## Reverse proxy y exposición
- **Hoy:** **Apache** hace de reverse proxy + TLS, con un **vhost por dominio** + `certbot`.
- **Recomendado** (ver [03 — Red](03-Red-y-Geodistribucion.md)): **registro DNS wildcard** `*.levelcloud.hdglabs.com` + reverse proxy que enruta por cabecera `Host` → evita registrar cada subdominio (y el límite de ~50 registros del proveedor). Vale tanto Apache (wildcard vhost) como **Caddy / Nginx Proxy Manager** (HTTPS automático).
- El **router** solo necesita **port-forward 80/443**. El enrutado por subdominio lo hace el reverse proxy, no el DNS ni el router.

## Stacks Docker
Cada uno en `~/services/<nombre>`; `./up.sh` para levantarlo. Rutas `/mnt/...` parametrizadas con `.env` (`${VAR:-ruta_por_defecto}`) y el dominio vía variable (ver [Portabilidad](#portabilidad)).

| Stack | Imagen | Subdominio | Datos (bind mount) | Depende de |
|---|---|---|---|---|
| **watchtower** | containrrr/watchtower | — | `/var/run/docker.sock` | docker |
| **Minio** | pgsty/minio | `minio.` / `s3.minio.` | `…/minio_data` | — |
| **Monitoring** | grafana · prometheus · cadvisor · node/mysqld/nextcloud-exporter | `grafana.` (vhost) | `/var/lib/grafana`, `/var/lib/docker` (ro) | **MariaDB host** :3306 |
| **Navidrome** | deluan/navidrome | `music.` | `…/Navidrome/{music,data}` | — |
| **yt-subs** | ghcr.io/luishidalgoa/yt-subs | — | escribe en la música de Navidrome | Navidrome |
| **GameHub** | ghcr.io/luishidalgoa/gamehub | `gamehub.` | carpeta del repo | — |
| **AudioMuse-AI** | audiomuse-ai + postgres + redis | — | `/mnt/music`, datos pg | — |
| **jellyfin** | jellyfin/jellyfin | (parado) | `…/Jellyfin/*`, `…/Movies/*` | — |

## Dependencias del sistema (paquetes)
- **Contenedores:** Docker (+ Compose).
- **BBDD:** MariaDB (servidor + cliente).
- **Web / proxy:** Apache2 (+ módulos `rewrite`, `headers`, `proxy`, `http2`, `ssl`), `certbot` (+ plugin apache), PHP para Nextcloud (php-fpm + extensiones).
- **Red / VPN:** WireGuard, `ufw`.
- **Respaldo de config:** `rclone` (destino OneDrive).
- **Compartición:** Samba (`smbd`).
- **Utilidades:** `unzip`, `git`, monitor (`htop`, `lm-sensors`…).

## Arranque
```bash
cd ~/services && ./up.sh        # todos, en orden
./up.sh Minio                   # solo uno
```

## Migración a un servidor nuevo
Pensado para ser **plug-and-play y reanudable**:
```bash
# copiar ~/services al server nuevo, luego:
cd ~/services && ./migrate.sh        # ejecuta los pasos; se para en lo manual
./migrate.sh --status                # solo diagnostica qué falta (no toca nada)
```
`migrate.sh` orquesta: `bootstrap-packages.sh` (paquetes) · `restore.sh` (restaura los zips del backup) · `setup-firewall.sh` (ufw) · `up.sh` (stacks).

**Restaurar el backup de config/BBDD** (`restore.sh`): cubre `nextcloud_www→/var/www`, `~/services`, `system_scripts`, `apache_vhosts→/etc/apache2`, y `nextcloud_db` + `grafana_db`→MariaDB.

> ⚠️ Ese backup es de **configuración + BBDD (metadatos)**, **no de los datos de usuario** (`/mnt/...`). Los datos de usuario solo están protegidos por **RAIDZ2** (ver [02](02-Almacenamiento.md)) — decisión asumida.

**Pasos manuales** (los pide `migrate.sh`): copiar `/etc/wireguard/`, vhosts de Apache + `certbot`, dumps SQL + **usuarios/grants**, `rclone.conf` + cron, y `rsync` de `/mnt/...`.

## Firewall (ufw)
| Puerto | Acceso |
|---|---|
| 22 (SSH) | idealmente solo LAN/VPN |
| 80, 443 (web) | Anywhere |
| 51820/udp (WireGuard) | Anywhere |
| 3306 (MariaDB) | solo VPN `10.0.0.0/24` + LAN `192.168.0.0/24` + docker `172.16.0.0/12` |
| Samba (SMB) | solo LAN + VPN |
| 1900, 7359/udp (DLNA/Jellyfin) | solo LAN |
| 5900 (VNC), 25565 (Minecraft) | **cerrados** |

## Portabilidad
- **Paths:** cada servicio con `.env` y variables `${VAR:-ruta_por_defecto}` (Minio→`MINIO_DATA`, Navidrome→`NAVIDROME_MUSIC/DATA`, jellyfin→`JELLYFIN_*`, AudioMuse→`AUDIOMUSE_MUSIC`, yt-subs→`YTSUBS_MUSIC/TEMP`). Sin `.env`, usan el fallback.
- **Dominio:** variable por servicio + prefijos fijos. Cambiar de dominio:
  ```bash
  ./set-domain.sh                    # muestra el actual
  ./set-domain.sh nuevo.dominio.net  # lo cambia en los servicios
  ./up.sh                            # recrea los contenedores
  ```
  `set-domain.sh` no toca lo de fuera de Docker: al final imprime los pasos manuales (DNS/DDNS, `ServerName` + `certbot` de Apache, y las redirect URIs de Google OIDC de MinIO y Grafana).

## Secretos necesarios (valores fuera de git)
- **MariaDB:** contraseñas de `nextclouduser`, `grafana`, `exporter`.
- **WireGuard:** claves privadas/públicas de los peers (`/etc/wireguard/`).
- **TLS:** certificados Let's Encrypt (regenerables con `certbot`).
- **rclone:** `rclone.conf` (acceso a OneDrive).
- **Google OIDC:** client id/secret para el login de MinIO y Grafana.
- **SMTP (Google app password):** notificaciones de Nextcloud.
