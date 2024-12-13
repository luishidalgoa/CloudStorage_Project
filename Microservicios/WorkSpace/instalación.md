## Levantar el entorno de desarrollo
En el directorio Microservicios/WorkSpace encontraras un script de ps1 (`POWERSHELL`) 
para levantar el entorno de desarrollo para la aplicacion en cuestion de segundos y 
con todo  <u>*pre-configurado*</u>

### <u>Este entorno incluye</u>:
- `Python` + `eyed3` + `yt-dlp` + `ffmpeg`
- `MySQL/Mariadb`
- `Apache2` + `php8.2` + `Nextcloud`

Cuando levantes el contenedor. Podras tener acceso a una version del servidor Nextcloud
y de la base de datos que utiliza el servidor nextcloud.

## Pre-requisitos
Solo hay un unico pre-requisito que es tener habilitada la caracteristica `Window Subsystem for Linux (WSL)`

- [Como habilitar WSL en window](https://tutowindow.com/como-habilitar-wsl2-en-windows-11/?expand_article=1)

## Instalar el contenedor
1. Descarga el fichero <u>*`cloud-storage-dev-container`*</u> a traves del siguiente enlace:

    [Descargar imagen del contenedor](https://1drv.ms/u/s!AuA970VMKOuw1NI6YqT90SGBc6Tdpg?e=aVAJeJ)

1. Ubica el fichero en la carpeta `.\Microservicios\WorkSpace\`
1. Abre un terminal `Powershell`
1. Ubicate en el directorio del proyecto `.\Microservicios\WorkSpace`
    ````sh
    cd ruta\del\proyecto\Microservicios\WorkSpace
    ````
    > Encontraras un fichero llamado <u>*`dev-container-install.ps1`*</u>

1. Ejecutalo desde la consola de powershell.
    ````bash
    powershell.exe .\dev-container-install.ps1
    ````

# ¡Pruebalo!

## Test Apache2

prueba a acceder al servidor nextcloud desde tu navegador web.

### [http://localhost/nextcloud](http://localhost/nextcloud)
````sh
# El primer inicio de sesion tardara aproximadamente 20s. Nextcloud creara los datos del usuario
````

### Usuario de acceso como Admin a nextcloud
**Username**:
*`admin`*

**Password**:
*`ContraseñaEjemplo`*

### Usuario y contraseña de mysql (nextcloud)
**Username**:
*`nextclouduser`*

**Password**:
*`1234`*

## Usalo en Intellij o VSCode
### Intellij IDEA
1. En `File`->`Remote Development...` clicka sobre "**Connect to WSL**" y selecciona la maquina `Debian`
### Visual Studio Code

1. Instala la extension [Remote SSH](https://marketplace.visualstudio.com/items?itemName=ms-vscode-remote.remote-ssh)
2. Siga los pasos que se indican en la pagina de la extension
## Conectate a la bbdd
Desde su herramienta de visualizacion de bbdd

> (`Workbench`, `DataFlare`, `Intellij Database`...)

conectese a traves de la ip `127.0.0.1` o `localhost` y el puerto `3306` con el usuario `nextclouduser` y la password `1234`