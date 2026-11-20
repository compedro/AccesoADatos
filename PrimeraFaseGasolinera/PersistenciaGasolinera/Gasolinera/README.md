# Gasolinera

La aplicación usa SQLite por defecto (`gasolinera.db`). Para conectarla a MySQL, configura `GASOLINERA_DB=mysql` y los datos de conexión antes de ejecutar:

```powershell
$env:GASOLINERA_DB = "mysql"
$env:MYSQL_HOST = "localhost"
$env:MYSQL_PORT = "3306"
$env:MYSQL_DATABASE = "gasolinera"
$env:MYSQL_USER = "usuario"
$env:MYSQL_PASSWORD = "contraseña"
.\gradlew run
```

La base de datos indicada en `MYSQL_DATABASE` debe existir previamente. También se puede proporcionar directamente la URL JDBC mediante `MYSQL_URL` (por ejemplo, `jdbc:mysql://localhost:3306/gasolinera`). Como alternativa a las variables de entorno, se admiten las propiedades Java `gasolinera.db`, `gasolinera.mysql.host`, `gasolinera.mysql.port`, `gasolinera.mysql.database`, `gasolinera.mysql.user`, `gasolinera.mysql.password` y `gasolinera.mysql.url`.
