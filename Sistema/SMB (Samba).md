# Guía instalación SMB (Samba) en el Servidor

## Linux

### 1. Instalar Samba
````sh
sudo apt update
sudo apt install samba
````
---

### 2. Configurar recurso compartido

Editar el archivo de configuración:

```
```

Añadir al final:

-----
[nextcloud-hdd-1]
   path = /mnt/nextcloud-hdd-1/
   browseable = yes
   read only = yes
   guest ok = no
   valid users = luish
-----

---

### 3. Crear usuario Samba

```
sudo smbpasswd -a luish
```

---

### 4. Reiniciar el servicio

```
sudo systemctl restart smbd
```

---

### 5. Probar conexión

```
smbclient //192.168.0.24/music -U luish
```

---

## Windows

### 1. Conectar unidad de red

Abrir PowerShell o CMD y ejecutar:

```
net use Z: \\192.168.0.24\music /user:luish
```

---

### 2. Acceso

- La unidad aparecerá como `Z:` en el explorador
- Se podrá acceder a los archivos compartidos como si fueran locales

---

### 3. Desconectar (opcional)

```
net use Z: /delete
```
