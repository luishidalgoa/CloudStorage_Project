# Instalacion del servidor
## Indice

1. [Firmware](#firmware)
    1. [Crear y habilitar un servicio de `systemd`](#paso-1-crear-y-habilitar-un-servicio-de-systemd)
    1. [Crear el script que buscará `pwm1` y creará el enlace simbólico](#paso-2-crear-el-script-que-buscará-pwm1-y-creará-el-enlace-simbólico)
    1. [Creamos un script que controle el ventilador](#3-creamos-un-script-que-controle-el-ventilador)
    1. [Automatizamos el proceso cada 10s](#4-automatizamos-el-proceso-cada-10s)
1. [Requisitos](#requisitos)
    1. [Sistema](#sistema)
    1. [NEXTCLOUD](#nextcloud)
1. [Monitoreo](#monitoreo)
1. [Estrategia de datos](#estrategia-de-datos)
    1. [Ventajas y desventajas de usar RAID 5](#ventajas-y-desventajas-de-usar-raid-5)
1. [Reubicar la MySQL en el HDD](#reubicar-la-mysql-en-el-hdd)
    1. [Reubicar el archivo MySQL con las bbdd](#reubicar-el-archivo-mysql-con-las-bbdd)
    1. [Indicar en la configuración de MySQL/Mariadb la nueva ubicación de las bbdd](#indicar-en-la-configuración-de-mysqlmariadb-la-nueva-ubicación-de-las-bbdd)
    1. [Configurar permisos](#configurar-permisos)
    1. [Finalizar](#finalizar)
1. [Firewall](#firewall)
1. [NextCloud](#nextcloud)
    1. [Optimizar copiados de archivos](#optimizar-copiados-de-archivos)
    1. [google smtp (notificaciones push)](#google-smtp-notificaciones-push)
1. [Soluciones](#soluciones)
    1. [SLOW QUERY Mysql/Mariadb](#slow-query-mysqlmariadb)
    1. [Resolucion DNS](#resolucion-dns)
    1. [Desactivar Firewall del puerto 3306 y activar redireccionamiento del router](#desactivar-firewall-del-puerto-3306-y-activar-redireccionamiento-del-router)
1. [Restic Backup y Restauración de Datos](#restic-backup-y-restauración-de-datos)
    1. [Instalacion](#instalar-restic)
    1. [Inicializar el directorio](#inicializar-el-directorio)
    1. [Crear script de backup](#crear-script-de-backup)
    1. [Prueba de ejecución del script](#prueba-de-ejecución-del-script)
    1. [Programar la frecuencia de ejecución](#programar-la-frecuencia-de-ejecución)
    1. [Restauración](#restauración)
    1. [Ventajas](#ventajas-de-restic)
1. [Tareas programadas Cron](#tareas-programadas-cron)
    1. [Programar apagado del equipo](#apagar-el-equipo)

## Firmware
actualizamos el firmware
```bash
sudo apt update
sudo apt full-upgrade
sudo rpi-update
sudo apt install bc
```
### Paso 1: Crear y habilitar un servicio de `systemd`

Este servicio buscará el archivo `pwm1` en los directorios `hwmon*` y creará el enlace simbólico `/dev/pwmfan`.

#### 1.1 Crea el archivo del servicio
Crea un archivo llamado `update_pwmfan.service` en el directorio de `systemd`:

````bash
sudo nano /usr/local/bin/update_pwmfan.sh
````

#### 1.2 Añade el siguiente contenido al archivo del servicio

```ini
#!/bin/bash
[Unit]  
Description=Actualizar enlace simbólico del ventilador PWM  
After=multi-user.target  

[Service]  
Type=oneshot  
ExecStart=/usr/local/bin/update_pwmfan.sh  

[Install]  
WantedBy=multi-user.target  
```

#### 1.3 Recarga los servicios de `systemd`

````bash
sudo systemctl daemon-reload
````

#### 1.4 Habilita y arranca el servicio para que se ejecute al inicio

```bash
sudo systemctl enable update_pwmfan.service  
sudo systemctl start update_pwmfan.service  
```

---

### Paso 2: Crear el script que buscará `pwm1` y creará el enlace simbólico

Este script se ejecutará cada vez que el servicio `systemd` se ejecute, o lo puedes ejecutar manualmente en cualquier momento.

#### 2.1 Crea el archivo del script

Crea un archivo llamado `update_pwmfan.sh` en `/usr/local/bin/`:

````bash
sudo nano /usr/local/bin/update_pwmfan.sh
````

#### 2.2 Añade el siguiente contenido al archivo del script

Este script busca entre los directorios `hwmon*` para encontrar el archivo `pwm1`, y luego crea el enlace simbólico `/dev/pwmfan`.

```bash
#!/bin/bash
# Buscar pwm1 dentro de los directorios hwmon0 a hwmon3
for dir in /sys/class/hwmon/hwmon{0..3}; do
    if [ -f "$dir/pwm1" ]; then
        # Si encontramos pwm1, creamos el enlace simbólico
        ln -sf "$dir/pwm1" /dev/pwmfan
        exit 0
    fi
done

# Si no se encuentra pwm1 en ningún directorio
echo "Error: No se encontró el dispositivo pwm1."
exit 1
```

#### 2.3 Haz el script ejecutable

```bash
sudo chmod +x /usr/local/bin/update_pwmfan.sh
sudo systemctl enable update_pwmfan.service
sudo systemctl start update_pwmfan.service
```

---

### 3. Creamos un script que controle el ventilador
```
sudo mkdir /home/luish/scripts
sudo nano /home/luish/scripts/control_fan.sh
```
dentro del fichero pegamos lo siguiente
```sh                        
#!/bin/bash
# Obtener la temperatura de la CPU
TEMP=$(vcgencmd measure_temp | sed "s/[^0-9.]//g")

# Define los rangos de temperatura y las velocidades del ventilador
if (($(echo "$TEMP < 40" | bc -l))); then
    # Temperatura menor a 40°C -> 30% velocidad
     FAN_SPEED=80
elif (($(echo "$TEMP >= 40 && $TEMP <= 44" | bc -l))); then
    # Temperatura entre 40°C y 44°C -> 50% velocidad
    FAN_SPEED=125
elif (($(echo "$TEMP >= 45 && $TEMP <= 49" | bc -l))); then
    # Temperatura entre 45°C y 49°C -> 70% velocidad
    FAN_SPEED=190
elif (($(echo "$TEMP >= 50" | bc -l))); then
    # Temperatura mayor o igual a 50°C -> 100% velocidad
    FAN_SPEED=255
fi

# Cambiar la velocidad del ventilador
echo $FAN_SPEED | sudo tee /dev/pwmfan

# Mostrar mensaje con la temperatura y la velocidad establecida
echo "Temperatura: $TEMP°C"
echo "Velocidad del ventilador: $FAN_SPEED"
```

### 4. Automatizamos el proceso cada `10s`
Vamos a crear un servicio que ejecute el script cada 10s y lo ejecute la primera vez al inicio del sistema
```bash
sudo nano /etc/systemd/system/control_fan.service
```
pegamos al final del todo la siguiente linea
```ini
#!/bin/bash
[Unit]
Description=Control Fan Script
After=network.target

[Service]
ExecStart=/bin/bash /home/luish/scripts/control_fan.sh
Restart=always
RestartSec=10
User=luish
Environment=HOME=/home/luish

[Install]
WantedBy=multi-user.target
```
Habilitamos el servicio
```bash
sudo systemctl daemon-reload
sudo systemctl enable control_fan.service
sudo systemctl start control_fan.service
```
Agregamos estas 3 lineas de codigo en cualquier parte del fichero `/boot/firmware/config.txt`
```bash
sudo nano /boot/firmware/config.txt
```
```bash
dtparam=fan_temp0=40000
dtparam=fan_temp0_hyst=10000
dtparam=fan_temp0_speed=255
```
Ejecutamos el script manualmente para comprobar que funciona
    > si muestra algo como `Temperatura: 40.6ºC` es que funciona
```bash
sudo chmod +x /home/luish/scripts/control_fan.sh
sudo /home/luish/scripts/control_fan.sh
```

```bash
sudo reboot
```
## Requisitos

### Sistema:
- Unzip
- ufw (firewall)
````bash
sudo apt update
sudo apt install unzip ufw
````

### NEXTCLOUD:
- Apache2
- certbot (SSL)
- PHP7.4
- MySQL


## Monitoreo:
> **Temperatura del sistema**
- **lm-sensors**:
    ```bash
    sudo apt install lm-sensors
    ```
    Comando ejecucion: `sensors`
> **prueba de velocidad**
- **speedtest-cli**:
    ```bash
    sudo apt install speedtest-cli
    ```
    Comando ejecucion: `speedtest-cli`
> **Ancho de banda**
- **nload**:
    ```bash
    sudo apt install nload
    ```
    Comando ejecucion: `nload eth0`
> **monitoreo de recursos**
- htop
    ```bash	
    sudo apt install htop
    ```
    Comando ejecucion: `htop`
### Grafana
- [Raspberry Pi & Docker Monitoring](https://github.com/oijkn/Docker-Raspberry-PI-Monitoring/tree/main) [<u>Seguir el tutorial</u>]
- [Monitoreo Nextcloud](https://github.com/xperimental/nextcloud-exporter)

#### Docker-compose.yml
```bash
apt install git
git clone https://github.com/oijkn/Docker-Raspberry-PI-Monitoring.git

cd Docker-Raspberry-PI-Monitoring
```
> **Nota** Asegurate previamente de tener la siguiente estructura de documentos
```bash
|mnt/nextcloud_hdd_1/monitoring/
|   |grafana/ # lo encontraras en el clone del repo Raspberry-Pi-Monitoring
|   |   |-- data/
|   |   |-- provisioning/
|   |   |   |-- datasources/
|   |   |   |-- dashboard.json
|   |   |-- .env
|   |prometheus/ # lo encontraras en el clone del repo Raspberry-Pi-Monitoring
|   |   |-- data/
|   |   |-- prometheus.yml
```
```bash
sudo nano docker-compose.yml
```
```yml
services:
  grafana:
    container_name: monitoring-grafana
    image: grafana/grafana:latest
    hostname: rpi-grafana
    restart: unless-stopped
    user: "472"
    networks:
      - internal
    ports:
      - "3000:3000"
    env_file:
      - /mnt/nextcloud_hdd_1/monitoring/grafana/.env
    volumes:
      - /mnt/nextcloud_hdd_1/monitoring/grafana/data:/var/lib/grafana
      - /mnt/nextcloud_hdd_1/monitoring/grafana/provisioning:/etc/grafana/provisioning
    depends_on:
      - prometheus

  cadvisor:
    container_name: monitoring-cadvisor
    image: gcr.io/cadvisor/cadvisor:v0.49.1
    hostname: rpi-cadvisor
    restart: unless-stopped
    privileged: true
    networks:
      - internal
    expose:
      - 8080
    command:
      - '-housekeeping_interval=15s'
      - '-docker_only=true'
      - '-store_container_labels=false'
    devices:
      - /dev/kmsg
    volumes:
      - /:/rootfs:ro
      - /var/run:/var/run:rw
      - /sys:/sys:ro
      - /var/lib/docker/:/var/lib/docker:ro
      - /dev/disk/:/dev/disk:ro
      - /etc/machine-id:/etc/machine-id:ro

  node-exporter:
    container_name: monitoring-node-exporter
    image: prom/node-exporter:latest
    hostname: rpi-exporter
    restart: unless-stopped
    networks:
      - internal
    expose:
      - 9100
    command:
      - --path.procfs=/host/proc
      - --path.sysfs=/host/sys
      - --path.rootfs=/host
      - --collector.filesystem.ignored-mount-points
      - ^/(sys|proc|dev|host|etc|rootfs/var/lib/docker/containers|rootfs/var/lib/docker/overlay2|rootfs/run/docker/netns|rootfs/)
    volumes:
      - /proc:/host/proc:ro
      - /sys:/host/sys:ro
      - /:/rootfs:ro
      - /:/host:ro,rslave

  prometheus:
    container_name: monitoring-prometheus
    image: prom/prometheus:latest
    hostname: rpi-prometheus
    restart: unless-stopped
    user: "nobody"
    command:
      - '--config.file=/etc/prometheus/prometheus.yml'
      - '--storage.tsdb.path=/prometheus'
      - '--storage.tsdb.retention.time=1y'
    networks:
      - internal
    ports:
      - "9090:9090"
    expose:
      - 9090
    volumes:
      - /mnt/nextcloud_hdd_1/monitoring/prometheus/data:/prometheus
      - /mnt/nextcloud_hdd_1/monitoring/prometheus:/etc/prometheus/
    depends_on:
      - cadvisor
      - node-exporter
    links:
      - cadvisor:cadvisor
      - node-exporter:node-exporter

  nextcloud-exporter:
    container_name: monitoring-nextcloud-exporter
    image: ghcr.io/xperimental/nextcloud-exporter:latest
    hostname: rpi-nextcloud-exporter
    restart: unless-stopped
    networks:
      - internal
    ports:
      - "9205:9205"
    expose:
      - 9205
    environment:
      - NEXTCLOUD_SERVER=${NEXTCLOUD_SERVER} # ejemp  https://luishidalgoa.ddns.net
      - NEXTCLOUD_USERNAME=${NEXTCLOUD_USERNAME} # ejemp admin
      - NEXTCLOUD_PASSWORD=${NEXTCLOUD_PASSWORD} # ejemp admin
    depends_on:
      - prometheus

networks:
  internal:
    driver: bridge
```
#### Prometheus.yml
En el archivo `/mnt/nextcloud_hdd_1/monitoring/prometheus/prometheus.yml` se encuentra la configuración de prometheus, si quieres cambiarla lo puedes hacer desde la web de prometheus.
```yml
global:
  scrape_interval: 10s
  evaluation_interval: 10s
alerting:
  alertmanagers:
  - scheme: http
    static_configs:
    - targets:
      - "alertmanager:9093"
      
scrape_configs:
  - job_name: 'prometheus'
    scrape_interval: 10s
    static_configs:
         - targets: ['localhost:9090']

  - job_name: 'cadvisor'
    scrape_interval: 10s
    static_configs:
      - targets: ['cadvisor:8080']

  - job_name: 'node-exporter'
    scrape_interval: 10s
    static_configs:
      - targets: ['node-exporter:9100']

  - job_name: 'nextcloud-exporter'
    scrape_interval: 30s
    static_configs:
      - targets: ['nextcloud-exporter:9205']
```
## Estrategia de datos
La idea inicial es tener las bbdd en el HDD y evitar lo mas posible las tareas de escritura en la microSD.
Vamos a guardar los datos de NextCloud en el HDD

En el futuro montaremos un NAS con RAID 5 

### Ventjas y desventajas de usar RAID 5
Ideal para: Necesitas más capacidad y mayor redundancia, sin perder mucho rendimiento.

**Ventajas**:
- **Redundancia y rendimiento**: Usa tres o más discos, distribuyendo los datos y la paridad entre ellos. Si un disco falla, puedes reconstruir los datos usando la paridad.

- **Almacenamiento eficiente**: A diferencia de RAID 1, no duplicas los datos, por lo que la capacidad útil es más alta (en una configuración de 3 discos, pierdes el tamaño de un disco por paridad).

- **Rendimiento bueno en lectura y aceptable en escritura**: Las lecturas son rápidas, pero las escrituras pueden ser más lentas debido al cálculo de la paridad.

**Desventajas**:
- **Más complejidad**: Necesitas al menos 3 discos.

- **Rendimiento de escritura**: Puede ser un poco más lento comparado con RAID 1, ya que tiene que calcular la paridad.

## Reubicar la MySQL en el HDD
### Reubicar el archivo MySQL con las bbdd
```bash
sudo rsync -av /var/lib/mysql /mnt/nextcloud_hdd_1/mysql
```
### Indicar en la configuración de MySQL/Mariadb la nueva ubicación de las bbdd
```bash
sudo nano /etc/mysql/mariadb.conf.d/50-server.cnf
```
> Buscar la linea datadir y escribir lo siguiente
```bash
datadir = /mnt/nextcloud_hdd_1/mysql
```
### Configurar permisos
```bash
sudo chown -R mysql:mysql /mnt/nextcloud_hdd_1/mysql
```
### Finalizar
1. Reiniciamos el servicio
```bash
sudo systemctl start mysql
```
2. Verificamos la nueva ubicación de las bbdd
> Dentro de MySQL ejecutamos el siguiente comando para verificar que la base de datos esta usando la nueva ruta:
```sql
SHOW VARIABLES LIKE 'datadir';
```

## Firewall
Activaremos los puertos 
- 22 (SSH),
- 443 (HTTPS) 
- 3306 (MySQL)
- 5900 (VNC)
```bash
sudo ufw allow 22/tcp
sudo ufw allow 443/tcp
sudo ufw allow 3306/tcp
sudo ufw allow 5900/tcp
```

## NextCloud

### Optimizar copiados de archivos
```bash
rsync -av --ignore-existing --remove-source-files /media/luish/origen /media/luish/destino
```

### google smtp (notificaciones push)
password icqf ayzs gmff nsmy

# SOLUCIÓNES
## SLOW QUERY Mysql/Mariadb
Vamos asegurarnos de que el usuario de nuestro servicio MySql se puede acceder desde el dns y la IP local
si aun así sigue sin resolverse, editaremos el archivo `/etc/hosts` para resolver manualmente el DNS con la ip publica
### Privilegios del usuario
```sql
GRANT ALL PRIVILEGES ON *.* TO 'nextclouduser'@'<DNS O IP>' IDENTIFIED BY 'Tu-contraseña-valida'
FLUSH PRIVILEGES;
```
### Resolver DNS
```bash
sudo nano /etc/host
```
Agregamos la siguiente linea de código
```bash
<IP PUBLICA> <DNS>
# Ejemplo
85.136.249.122 luishidalgoa.ddns.net
```
### Desactivar Firewall del puerto 3306 y activar redireccionamiento del router
Puede ser que el corta fuegos este bloqueando el acceso al servicio mysql/mariadb. Es por eso que estableceremos una regla al firewall para que permita el acceso
```bash
sudo ufw 3306/tcp
```
> Nota Ahora redirigiremos el puerto desde el router hacia nuestra maquina local para permitir conexiones externas a la red.
Para ello dirigete a tu router y garantiza que estas redireccionando

## Restic Backup y Restauración de Datos
Restic es una herramienta avanzada de backup que prioriza la simplicidad y la seguridad. Funciona con deduplicación y encriptación, y permite guardar backups locales o remotos.

Ventajas:
- Compatible con almacenamiento remoto como S3, FTP, etc.
- Fácil de configurar.
- Alta velocidad y seguridad.
### Instalar Restic
```bash
sudo apt update
sudo apt install restic
restic version
```

### Inicializar el directorio
En este directorio se guardarán los backups
> **NOTA**: Se solicitara una contraseña, asegurate de no olvidarla porque sera necesaria para restaurar los backups
```bash
restic init --repo /mnt/nextcloud_hdd_1/restic_repo
```

### Crear script de backup
Crea un script para automatizar el proceso de backup y limpieza de versiones antiguas.
```bash
sudo nano /usr/local/bin/restic_backup.sh
```
Introduzca el siguiente contenido:
```bash
#!/bin/bash

# Variables
REPO="/mnt/nextcloud_hdd_1/restic_repo"
PASSWORD="your_secure_password"  # Cambia esto por tu contraseña segura o usa un archivo seguro
EXCLUDES=(
  "--exclude=/proc"
  "--exclude=/sys"
  "--exclude=/dev"
  "--exclude=/tmp"
  "--exclude=/run"
  "--exclude=/media"
  "--exclude=/mnt"
)

# Exportar la contraseña como variable de entorno
export RESTIC_PASSWORD=$PASSWORD

# Realizar el backup
restic -r $REPO backup / "${EXCLUDES[@]}"

# Limpiar backups antiguos
restic -r $REPO forget --keep-daily 7 --keep-weekly 4 --keep-monthly 6

# Verificar la integridad del repositorio (opcional, descomentar si deseas verificar)
# restic -r $REPO check
```
Le damos permisos de ejecución al script
```bash
sudo chmod +x /usr/local/bin/restic_backup.sh
```
### Prueba de ejecución del script
Antes de programarlo, prueba el script manualmente para asegurarte de que funciona correctamente:
```bash
sudo /usr/local/bin/restic_backup.sh
```
Revisa el contenido del repositorio de backup para confirmar que los datos se están guardando:
```bash
restic -r /mnt/nextcloud_hdd_1/restic_repo snapshots
```
### Programar la frecuencia de ejecución
Con `cron` vamos a programar todos los sabados a las 1:00 am
```bash
sudo crontab -e
```
```bash
0 1 * * 6 /usr/local/bin/restic_backup.sh
```

### Restauración
- Para restaurar los backups, ejecutar el siguiente comando:
    ```bash	
    restic -r /mnt/nextcloud_hdd_1/restic_repo snapshots
    ```
- Restaura un snapshot específico (cambia `SNAPSHOT_ID` por el ID del snapshot que quieras restaurar):
    ```bash	
    restic -r /mnt/nextcloud_hdd_1/restic_repo restore SNAPSHOT_ID --target /
    ```

### Ventajas de Restic
| Herramienta | Deduplicación | Compresión | Cifrado | Retención automática | Fácil restauración |
|-------------|---------------|------------|---------|----------------------|--------------------|
| Restic      | ✅             | ✅          | ✅       | ✅                    | ✅                  |


# Tareas programadas Cron

## Apagar el equipo
Si la variable global es true, el equipo se apagara a las 1:00 de Lunes-Viernes

```bash
sudo crontab -e
```
agregamos esta linea de código:
```bash
0 1 * * * [ "$APAGAR_AUTOMATICO" == "true" ] && sudo shutdown now
```
En el futuro si queremos definir la variable de entorno como true o false para habilitar el apagado del equipo o desactivarlo, usaremos el siguiente comando en la consola:
```bash
export APAGAR_AUTOMATICO=true   # o false según se desee
```



