# Guía instalación SMB (Samba) en el Servidor

## Linux

### 1. Instalar Samba
````sh
sudo apt update && sudo apt upgrade -y
sudo apt install samba -y
````
---

### 2. Configurar recurso compartido

Editar el archivo de configuración:

```
sudo nano /etc/samba/smb.conf
```

Añadir al final:

-----
[nextcloud_hdd_1]
   path = /mnt/nextcloud_hdd_1/
   browseable = yes
   read only = no
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

### 5. Habilitar el Firewall
````
sudo ufw allow samba
````

---

### 6. Probar conexión

```
smbclient //192.168.0.24/nextcloud_hdd_1 -U luish
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
