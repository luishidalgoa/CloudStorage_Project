# Microservicio Auth

## Objetivo
El objetivo princiapl de este microservicio es implementar un sistema de authenticación Basic-Auth. El Servidor de autenticación se encargara de capturar las credenciales de los encabezados "Authentication" y si son correctas y el usuario tiene los permisos necesarios, se informara al microservicio Gateway para que este continue con el flujo de la aplicación en base a la solicitud recibida

## Dependencias
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

- [ ❌ ] Esta usando la configuracion del servidor Config

### [[^1]]()
- Rutas sin autenticacion:
  - /auth/register
  - /music/**

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


