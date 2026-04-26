# 🧱 NAS Home Server - Documentación

## 📦 Hardware

- **NAS**: Asustor Lockerstor 6 AS6706T
- **Precio NAS**: 874€
- **RAM**: De serie
- **SSD NVMe**: 250GB (para apps / cache)

## 💾 Discos

- **Modelo**: Seagate IronWolf 6TB 7200RPM
- **Cantidad**: 6 discos
- **Precio unitario**: 229,99€
- **Total discos**: 1379,94€

---

## 🧠 Configuración de almacenamiento

- **Sistema de archivos**: ZFS
- **Tipo RAID**: RAIDZ2

### Características:
- Se pierden 2 discos en paridad
- Alta tolerancia a fallos (hasta 2 discos)
- Protección contra corrupción de datos

### Capacidad estimada:
- Bruto: 36 TB
- Útil teórico: ~24 TB
- Útil real seguro: ~18–20 TB (considerando overhead y margen)

---

## ⚙️ Configuración ZFS

- **Compresión**: Activada (LZ4 recomendado)
- **Snapshots**: Pendiente de definir

### 📌 Nota sobre compresión:
ZFS comprime los datos automáticamente:
- Ahorra espacio (especialmente en texto, fotos, documentos)
- Mejora rendimiento en muchos casos
- No afecta negativamente (es prácticamente gratis en CPU moderna)

---

## 🌐 Red

- **Velocidad**: 1 Gbps
- **IP local fija**: 192.168.0.24
- **IP pública**: Dinámica (ISP)

---

## 👥 Usuarios

- **Usuarios reales**: 4
- **Usuarios máximos previstos**: 6

### Distribución:
- Espacio repartido entre usuarios individuales
- Carpeta compartida:
  - Tamaño: 380 GB
  - Compartida entre varios usuarios
  - El espacio se descuenta proporcionalmente de 4 usuarios

---

## 🧩 Servicios

- Nextcloud → almacenamiento y sincronización
- Docker → contenedores
- Jellyfin → multimedia
- Navidrome → música

---

## 🔐 Seguridad

- **Dominio**: CloudDNS
- **Acceso externo**: Sí

### ⚠️ Pendiente:
- Configurar port forwarding en router
- Revisar exposición de servicios
- Considerar uso de HTTPS obligatorio

---

## ⚠️ Riesgos conocidos

### ❌ Sin backups externos
- Si hay fallo grave → pérdida total de datos
- RAID ≠ backup

### ❌ Sin SAI (UPS)
- Corte de luz → posible corrupción del sistema
- Riesgo real en ZFS durante escritura

---

## 📊 Uso recomendado

- No superar el 80% del almacenamiento
- Monitorizar estado SMART de discos
- Revisar salud del pool periódicamente

---

## 🔧 Notas futuras

- Evaluar snapshots automáticos
- Evaluar backup externo (USB o nube)
- Posible ampliación de RAM
- Evaluar red 2.5GbE en el futuro

---

## 🧨 Resumen

Sistema NAS con:
- 6 discos en RAIDZ2
- Alta tolerancia a fallos
- Servicios multimedia y cloud personal

Configuración potente pero con riesgos por:
- ausencia de backup
- ausencia de protección eléctrica
