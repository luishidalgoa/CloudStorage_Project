# NextCloud (RaspberryPI OS)

## Índice

1. [NextCloud (RaspberryPI OS)](#nextcloud-raspberrypi-os)
    1. [Requisitos previos](#requisitos-previos)
    1. [Configuración MySQL](#configuracion-mysql)
    1. [Descarga e instalación de NextCloud](#descarga-e-instalación-de-nextcloud)
    1. [Configura Apache](#configura-apache)
    1. [Comprobamos que todo funciona](#comprobamos-que-todo-funciona)
1. [Configuración de certificado SSL](#configuracion-de-certificado-ssl)
    1. [Pasos previos](#pasos-previos)
    1. [¡Empecemos!](#empecemos)
1. [Cambiar ubicación de la cache temporal](#cambiar-ubicación-de-la-cache-temporal)
1. [Arranque de inicio de la máquina WSL](#arranque-de-inicio-de-la-maquina-wsl)
1. [Plugins Nextcloud](#plugins-nextcloud)

## Requisitos previos

```bash
sudo apt update
```
```bash
sudo apt install apache2 libapache2-mod-php default-mysql-server default-mysql-client-core \
php php-apcu php-mysql php-xml php-mbstring php-curl php-zip php-gd php-intl php-bcmath php-imagick php-gmp wget -y
sudo phpenmod apcu
```
```bash
sudo a2enmod rewrite headers env dir mime
sudo systemctl restart apache2
```
## Configuracion MySQL
Aseguramos MySQL 
```bash
sudo mysql_secure_installation
```
iniciamos sesion
```bash
sudo mysql -u root -p
```
creamos la base de datos y el usuario de la misma
```sql
CREATE DATABASE nextcloud;
CREATE USER 'nextclouduser'@'%' IDENTIFIED BY 'Tu-contraseña-segura';
GRANT ALL PRIVILEGES ON nextcloud.* TO 'nextclouduser'@'%';
FLUSH PRIVILEGES;
```
comprobamos el plugin de autentificación que usamos
```sql
SELECT user, host, plugin FROM mysql.user WHERE user = 'nextclouduser';
```
> **NOTA** si el plugin no es mysql_native_password lo cambiamos
```sql
ALTER USER 'nextclouduser'@'localhost' IDENTIFIED WITH mysql_native_password BY 'Tu-contraseña-segura'
EXIT;
```
## Descarga e instalación de NextCloud
Vamos a irnos a nuestro directorio donde descargaremos el fichero zip
```bash
sudo cd
```
Descargamos el fichero zip
```bash
wget https://download.nextcloud.com/server/releases/latest.zip
```	
Descomprimimos el fichero zip
```bash
unzip latest.zip
sudo mv nextcloud /var/www/
```
Cambiamos los permisos de la carpeta
```bash
sudo chown -R www-data:www-data /var/www/nextcloud
sudo chmod -R 750 /var/www/nextcloud
```
## Configura apache
1. Vamos a crear un fichero de configuracion para http con el nombre de nextcloud.conf. Con el objetivo de no sobreescribir el fichero 000-default.conf
```bash
sudo nano /etc/apache2/sites-available/nextcloud.conf
```	
Una vez hayamos creado el fichero, lo editamos
```apache
<VirtualHost *:80>
    ServerName tu-dominio-o-ip
    DocumentRoot /var/www/nextcloud

    <Directory /var/www/nextcloud/>
        Require all granted
        AllowOverride All
        Options FollowSymLinks MultiViews
    </Directory>

    ErrorLog ${APACHE_LOG_DIR}/nextcloud_error.log
    CustomLog ${APACHE_LOG_DIR}/nextcloud_access.log combined
</VirtualHost>
```
2. Vamos a indicarle apache que use el fichero de configuracion que acabamos de crear y reiniciamos apache
```bash
sudo a2ensite nextcloud.conf
sudo a2enmod rewrite
sudo systemctl restart apache2
```
## Comprobamos que todo funciona
Vamos a acceder a la web de NextCloud
```bash
http://tu-dominio-o-ip
```

## Configuracion de certificado SSL
### Pasos previos
> si estas en WSL <br>
Deberas redireccionar el puerto 443 de tu maquina windows a la maquina WSL
```bash
netsh interface portproxy add v4tov4 listenport=443 listenaddress=0.0.0.0 connectport=443 connectaddress=172.24.12.198
```

Permite el redireccionamiento del puerto 443 desde tu router a la maquina windows.
Para ello acceder a la ip de tu Gateway por ejemplo `http://192.168.0.1`

### ¡Empecemos!
Instalamos cerbot
```bash
sudo apt install certbot python3-certbot-apache
```
```bash
sudo certbot --apache
```

Sigue las intrucciones que aparecen en la consola. te apareceran las siguientes instrucciones:
1. introduce el correo electronico que usaras para registrarte
2. acepta los terminos de servicio
3. acepta compartir tu correo electronico con el servicio
4. Introduce el dns por ejemplo `luishidalgoa.ddns.net`

una vez completados los pasos anteriores, deberiamos tener un certificado SSL en nuestro dominio. para ello desde el navegador accedemos a `https://luishidalgoa.ddns.net`

## Cambiar ubicación de la cache temporal
> **Nota**: Se detecto que la microSD donde tenia alojado el Sistema operativo y Apache2, en el se estaba haciendo uso de lectura y escritura. sin embargo me interesa mas que este proceso se haga en el HDD
Si deseas que el 100% de las lecturas se realicen en otro dispositivo de almacenamiento, sigue las siguientes instrucciónes

Configuramos donde PHP almacenara la cache
```bash
sudo apt install php8.2-fpm
sudo nano /etc/php/8.2/fpm/php.ini
```

Filtra buscando la linea correspondiente con la tecla `ctrl` + `w`:
	- `session.save_path` 
	- `opcache.file_cache`
```bash
session.save_path = <Unidad de almacenamiento> # ejemp "/mnt/nextcloud_hdd_1/sessions"
opcache.file_cache = <Unidad de almacenamiento> #ejemp "/mnt/nextcloud_hdd_1/cache"
```
Creamos los directorios previamente indicados y le asignamos permisos
```bash
sudo mkdir -p /mnt/nextcloud_hdd_1/sessions
sudo chown www-data:www-data /mnt/nextcloud_hdd_1/sessions

sudo mkdir -p /mnt/nextcloud_hdd_1/cache
sudo chown www-data:www-data /mnt/nextcloud_hdd_1/cache
```
> **Paso importante**
Le indicamos a NextCloud donde almacenaremos la cache que se descargara de los ficheros de nuestros clientes
```bash
sudo nano /var/www/nextcloud/config/config.php
```
Dentro del archivo, al final del todo pegaremos estas 3 lineas
```php
'memcache.local' => '\OC\Memcache\APCu',
'filelocking.enabled' => true,
'cache_path' => '/mnt/nextcloud_hdd_1/nextcloud_cache',
```
por ultimo, reiniciaremos el servicio de Apache
```bash
sudo systemctl restart apache2
```

## Arranque de inicio de la maquina WSL
1. **Escribimos en un bloc de notas el siguiente comando**
```bash
wsl.exe -d Ubuntu
```
2. **Lo guardamos con la extension .bat**
3. **Abre el Programador de tareas**:
   - Pulsa la tecla Windows y busca "Programador de tareas".
   - En el Programador de tareas, selecciona **Crear tarea** en el panel derecho.

4. **En la ventana Crear tarea**:
   - En la pestaña **General**, asigna un nombre a la tarea, por ejemplo, "Iniciar WSL Ubuntu".
   - Marca la opción **Ejecutar con los privilegios más altos**.
   
5. **Ve a la pestaña Desencadenadores** y haz clic en **Nuevo**:
   - En el cuadro de diálogo **Nuevo desencadenador**, selecciona **Al iniciar el sistema**.
   - Haz clic en **Aceptar**.

6. **Ve a la pestaña Acciones** y haz clic en **Nuevo**:
   - En **Acción**, selecciona **Iniciar un programa**.
   - En **Programa/script**, busca y selecciona el archivo `.bat` que creaste anteriormente, por ejemplo, `iniciar_wsl.bat`.
   - Haz clic en **Aceptar**.

7. **En la pestaña Condiciones**, puedes desmarcar las opciones si no deseas que la tarea dependa de condiciones específicas, como la conexión a la red.

8. **Haz clic en Aceptar** para crear la tarea.

## Plugins Nextcloud

- registration
- group folders
- quota warnings
- music
- memories
