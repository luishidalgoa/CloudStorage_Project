# Pasos de instalación del entorno de desarrollo
## Requisitos previos
- Instalar docker desktop en window o docker-core en linux

## Ejecución

1. Hacer un docker run de la imagen publicada en docker hub:
````sh
docker compose up -d
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