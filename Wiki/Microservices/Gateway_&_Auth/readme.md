# Microservicio Gateway & Auth

## Objetivo
El funcionamiento principal de este microservicio sera actuar como gateway principal entre los microservicios,
esto quiere decir que como si de un router se tratase, todas las llamadas que se hagan a la aplicación desde el exterior,
deben ser al gateway y este mismo las redireccionara a los distintos microservicios de la aplicación.

Ademas al implementar un sistema de authenticación Basic-Auth (debido a las limitaciones de la api de nextcloud). El Servidor de autenticación sera el propio gateway, ya que a traves de el capturaremos las autoridades de los encabezados "Authentication" y si son correctas, se habilitara el flujo logico de la aplicación entre los microservicios de recursos de la aplicación.

## Dependencias
- Gateway
- Spring Web
- Spring Security
- OpenFeign
- Actuator
- Prometheus
- Config client
- Eureka Client

## Desarrollo

### Sprint 1
- [ ❌ ] Se ha configurado las rutas de acceso que estaran permitidas sin autenticacion y las que requeriran autenticacion ademas de permisos de roles. [[^1]](#1)

- [ ❌ ] Se consume para recibir las credenciales de los usuarios, el microservicio User el cual contiene toda la informacion de los usuarios

- [ ✅ ] Esta usando la configuracion del servidor Config

### [[^1]]()
- Rutas sin autenticacion:
  - /auth/register

## Endpoints

### `/register` (POST) [[Example]](#urlapiauthregister)
- **Descripción**: El servidor registra un nuevo usuario.
- **Flujo**:
  1. El servidor obtiene los datos del usuario y los encapsula en un objeto `UserDTO` despues de setearle la contraseña encriptada.
  1. Envia el objeto `UserDTO` al microservicio User donde se guardara en la base de datos.
  1. El servidor devuelve un HttpStatus


## Endpoints consumidos

### `/api/user/userDetails`

### `/api/user/register`


# Ejemplos de los endpoints

### `{{url}}/api/auth/register`
> Registra un nuevo usuario en la aplicación

**Type**

`POST`

**Body:**

```json
{
    "username": "string",
    "email": "string",
    "password": "string"
}
```
**Response:**

```json
{
    "201 Created."
}
```

**Errores:**

- **502**: Bad roouting to User.
- **409**: User already exists.


