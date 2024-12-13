# Comprobar si Debian ya está instalada
if (wsl.exe --list --verbose | Select-String -Pattern "Debian") {
    Write-Output "Se detectó una instalación existente de Debian."

    # Solicitar confirmación del usuario
    $confirmation = Read-Host "¿Deseas desinstalar la instancia actual de Debian? (sí/no)"
    
    if ($confirmation -eq "sí") {
        Write-Output "Desinstalando la instancia existente de Debian..."
        wsl --unregister Debian
    } else {
        Write-Output "No se desinstalará la instancia existente. Finalizando script."
        exit
    }
}

# Importar la nueva instancia de Debian
Write-Output "Importando la nueva instancia de Debian..."
wsl --import Debian ./Debian ./cloud-storage-dev-container

# Listar las distribuciones para confirmar la instalación
Write-Output "Estado actual de WSL:"
wsl --list --verbose

wsl -d Debian

wsl -d Debian cd /Workspace