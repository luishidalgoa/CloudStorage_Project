# Objetivo
El principal objetivo de este proyecto es <u>desarrollar la logica del negocio</u> del servicio de almacenamiento de datos en la nube. Donde nuestros usuarios a traves de ``Nextcloud`` podran subir sus ``fotos``, ``canciones``, ``documentos``, etc...

## Propuesta de valor agregado:
Un enfoque que deseamos darle a este proyecto es ofrecerla la comodidad al usuario de descargar playlists enteras de canciones de youtube u otras plataformas de una manera <u>sencilla y rapida</u>. de modo que desde una futura aplicación de escritorio y movil, puedan reproducir su musica sin tener que descargarla, a la maxima calidad y sin anuncios.

# Diagramas
## Diagrama arquitectura microservicios

![Diagrama](Diagrama_Microservicios.png)

# Arquitectura del proyecto
### Reglas de trabajo:
- Usar variables de entorno siempre que se pueda
- Usar un dockerfile para cada microservicio

### Seguridad:
- Solicitar las credenciales en todos los endpoints excepto los que se indiquen en la documentación.
- Utilizar el microservicio Auth para validar las credenciales.

### Backend:
- Todos los servicios creados seran REST
- usar variables de entorno :
    - Mockups // para pruebas en las que no hay backend
    - Development // pruebas reales con otros microservicios
    - Production // pruebas en el despliegue
- Manejo de errores

# Tecnologías
- Docker
- Spring Boot
- Java 21
- Python
- maven
- Git
- Postman

## Software de 3º
- [yt-dlp](https://github.com/yt-dlp/yt-dlp)
- [ffmpeg](https://ffmpeg.org/download.html#build-windows)

## Tools

- IntelliJ

## Plugins intellij

- CodeIUM
- Prettier
- maven
