# Algunos endpoints de microservicios

## **Auth microservice:**

### `{{url}}/auth/api/v1/me`

**Descripción:**

> Obtiene la información del usuario autenticado.

**Method:** GET

**Header:**

```bash
Authorization: Basic {{base64_username:password}}
```

**Response:**

```json
{
  "valid": true,
  "userId": "string",
  "roles": ["string"]
}
```

---

### `{{url}}/auth/api/v1/verify-token`

**Descripción:**

> Verifica si el token es válido.

**Header:**

```bash
Authorization: Basic {{base64_username:password}}
```

**Response:**

```json
true
```

## **NextCloud**

### `/nextcloud/api/v1/directory-list`

**Descripción:**

> Devuelve los elementos hijos dentro del directorio indicado del usuario dueño del token.

**Header:**

```bash
Authorization: Basic {{base64_username:password}}
```

**Body:**

```json
{
    "user": {
        "uid": "string"
    },
    "path": "/" // Indica el directorio raíz del usuario
}
```

**Response:**

```json
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
```

---

### `{{url}}/api/nextcloud/upload/status`

**Descripción:**

> 
**Header:**

```bash
Authorization: Basic {{base64_username:password}}
```

**Body:**

```json
{
    "path": "/new%folder", // Ruta de descarga indicada por el usuario
    "size": 4194304 // Tamaño en bytes del archivo
}
```

**Response:**

```json
{
  "success": true
}
```

**Errores:**

- **1002**: Almacenamiento insuficiente.
- **1003**: Ruta de descarga incorrecta.

### `{{url}}/api/nextcloyd/upload`

**Response:**

```json
{
  "url": "https://cloud.example.com/remote.php/dav/files/luishidalgoa/My%20Folder/My%20File.mp3"
}
```