# Proyecto Music Downloader

# Proposito del proyecto

El objetivo de este proyecto es desarrollar un microservicio que en principio seria consumido por una futura aplicacion web.
Esta api permitira a los usuarios descargar canciones de youtube de forma mas rapida y sencilla, almacenando estas mismas canciones dentro del propio almacenamiento en la nube del usuario que estara descargando la musica.

## Enfoque del proyecto

Este microservicio va a <u>consumir algunos microservicios (_'MS'_)</u> como `Auth MS`, `Nextcloud MS` y `Subscription MS`.

Cuando un usuario desee descargar una canción, se llamara al endpoint /download del microservicio "`MusicDownloader`" Este endpoint se encargara de realizar algunas validaciones previas a la ejecucion de la logica del microservicio. Algunas de las validaciones son:

- El usuario debe estar autenticado
- El usuario debe tener un plan de subscripción activo
- El usuario debe tener suficiente espacio en su almacenamiento para descargar las canciones
- La ruta de descarga debe ser correcta

Una vez todas esas validaciones pasen, se realizara la descarga de la cancion utilizando la herramienta `yt-dlp` que se encargara de descargar la cancion y almacenarla en la ruta indicada por el usuario.

## Endpoints

### `{{url}}/MusicDownloader/api/v1/download`:

> Devuelve el link de la carpeta donde se almacenaron las canciones descargadas.

`Header`:

```json
Authorization: Basic bHVpc2hpZGFsZ29hOkNvbnRyYXNlbmFFamVtcGxv
```

`Body`:

```json
{
  "user": {
    "uid": "string"
  },
  "data": {
    "externalUrl": "https://www.youtube.com/watch?v=QH2_TGUlwu4", //Url de la cancion
    "DirectoryPath": "/new%folder" //Ruta de descarga indicada por el usuario
  }
}
```

`Errors`:

- 401: Credenciales invalidas.
- 404: El enlace de descarga no existe.
- 1001: El usuario no tiene un plan activo
- 1002: El usuario no tiene suficiente espacio en su almacenamiento para descargar la cancion
- 1003: La ruta de descarga no existe

# Tecnologías

- Docker
- Spring Boot
- Java 21
- maven
- Git
- Postman

## Tools

- IntelliJ

## Plugins intellij

- CodeIUM
- Prettier
- maven

# Arquitectura del proyecto

lorem hipsus

# Diagramas

## Diagrama arquitectura microservicios

![Diagrama](../Diagrama_Microservicios.png)

## Diagrama de secuencia

![Diagrama](./Diagrama_secuencia.png)

# Microservicios consumidos

## **Auth microservice**:

### `{{url}}/auth/api/v1/me`:

`descripción`:

> Obtiene la información del usuario autenticado.

`Method`: GET

`Header`:

```json
Authorization: Basic bHVpc2hpZGFsZ29hOkNvbnRyYXNlbmFFamVtcGxv
```

`Response`:`

```json
{
  "valid": true,
  "userId": "string",
  "roles": ["string"]
}
```

---

### `{{url}}/auth/api/v1/verify-token`

`descripción`:

> Verifica si el token es valido.

`Header`:

```json
Authorization: Basic bHVpc2hpZGFsZ29hOkNvbnRyYXNlbmFFamVtcGxv
```

`Response`:

```json
true
```

## NextCloud

### `/nextcloud/api/v1/directory-list`:

`Descripción`:

> Devuelve los elementos hijos dentro del directorio indicado del usuarío dueño del token.

`Header`:

```json
Authorization: Basic bHVpc2hpZGFsZ29hOkNvbnRyYXNlbmFFamVtcGxv
```

```json
    "user": {
        "uid": "string"
    },
    "path"= "/" //Indica el directorio raiz del usuario
```

`Response`:

```json
{
    [
        {
        "href": "/nextcloud/remote.php/dav/files/luishidalgoa/",
        "propstat": {
          "prop": {
            "getlastmodified": "Wed, 11 Dec 2024 15:27:54 GMT",
            "resourcetype": {
              "collection": {}
            },
            "quota-used-bytes": 365435363,
            "quota-available-bytes": 214382929437,
            "getetag": "\"6759af7a33f89\""
          },
          "status": "HTTP/1.1 200 OK"
        }
      },
      {
        "href": "/nextcloud/remote.php/dav/files/luishidalgoa/Arquitectura.excalidraw",
        "propstat": {
          "prop": {
            "getlastmodified": "Wed, 04 Dec 2024 23:04:44 GMT",
            "getcontentlength": 197240,
            "resourcetype": {},
            "getetag": "\"7704b908aa6f925fb31a67f700401aac\"",
            "getcontenttype": "application/vnd.excalidraw+json"
          },
          "status": "HTTP/1.1 200 OK"
        }
      },
      {
        "href": "/nextcloud/remote.php/dav/files/luishidalgoa/Nueva%20carpeta/",
        "propstat": {
          "prop": {
            "getlastmodified": "Wed, 11 Dec 2024 09:48:13 GMT",
            "resourcetype": {
              "collection": {}
            },
            "quota-used-bytes": 1678,
            "quota-available-bytes": 214382929437,
            "getetag": "\"67595fdd6cb14\""
          },
          "status": "HTTP/1.1 200 OK"
        }
      }
    ]
}
```

---

### `/nextcloud/api/v1/downloadCheck`:

`descripción`:

> Verifica si la ruta de descarga indicada es correcta y si hay suficiente espacio disponible en el almacenamiento del usuario. devolviendo un true o false en base a si hay o no suficiente espacio y si la ruta es correcta.

`Header`:

```json
Authorization: Basic bHVpc2hpZGFsZ29hOkNvbnRyYXNlbmFFamVtcGxv
```

`Body`:

```json
{
    "user": {
        "uid": "string"
    },
    "path": "/new%folder" //Ruta de descarga indicada por el usuario
    "size": 4194304 //tamaño en bytes del archivo
}
```

`Response`:

```json
{
  "success": true
}
```

`Errors`:

- 1002: Almacenamiento insuficiente
- 1003: Ruta de descarga incorrecta

---
