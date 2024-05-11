# Proyecto Final del Curso:

# Gaming_Leagues

Esta aplicación es un "Sistema de Gestión Deportiva" que permite gestionar diferentes aspectos relacionados con partidas y competiciones referentes a videojuegos. Está diseñada con una interfaz gráfica de usuario (GUI) utilizando la biblioteca Swing de Java. Aquí le dejo un resumen de cada uno de los paneles y su funcionalidad:

Panel de Jugadores:

Permite mostrar una tabla de jugadores.
Ofrece la funcionalidad de agregar nuevos jugadores a la base de datos.
Panel de Equipos:

Muestra una tabla de equipos.
Permite agregar nuevos equipos, especificando detalles como el nombre, el jugador que lo creó, y las fechas de creación y disolución.
Panel de Ligas:

Muestra una tabla de ligas.
Permite agregar nuevas ligas, proporcionando un nombre y detalles adicionales.
Panel de Juegos:

Muestra una tabla de juegos.
Permite agregar nuevos juegos, especificando un nombre y una descripción.
Panel de Partidos:

Muestra una tabla de partidos.
Permite agregar nuevos partidos, indicando el código del juego, los IDs de los jugadores participantes, la fecha y el resultado.
Panel de Procesos de Negocio:

Contiene subpaneles para asignar jugadores a equipos y organizar partidos dentro de ligas.
Permite realizar acciones relacionadas con la gestión deportiva, como asignar jugadores a equipos o programar partidos.
Panel de Reportes:

Ofrece botones para generar diferentes tipos de reportes, como los jugadores más destacados en cada juego, el rendimiento de los equipos en las ligas y el historial de partidos de cada jugador.

Además de los paneles de la interfaz, la aplicación incluye una clase de servicio ConexionBD para manejar la conexión a la base de datos PostgreSQL. Esta clase facilita la conexión y desconexión con la base de datos utilizando los datos de conexión proporcionados (URL, usuario y contraseña).
