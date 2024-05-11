import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.time.LocalDate;

// Clase principal de la aplicación
public class AplicacionDeportiva {
    private static JFrame frame;
    private static JPanel mainPanel;
    private static JTabbedPane tabbedPane;

    public static void main(String[] args) {
        ConexionBD.conectar(); // Conectar a la base de datos al iniciar la aplicación
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                crearYMostrarGUI();
            }
        });
    }

    private static void crearYMostrarGUI() {
        frame = new JFrame("Sistema de Gestión Deportiva");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);

        mainPanel = new JPanel(new BorderLayout());
        tabbedPane = new JTabbedPane();

        // Agregar pestañas con los componentes reutilizables
        tabbedPane.addTab("Jugadores", PanelJugadores());
        tabbedPane.addTab("Equipos", PanelEquipos());
        tabbedPane.addTab("Partidos", PanelPartidos());
        tabbedPane.addTab("Ligas", PanelLigas());
        tabbedPane.addTab("Juegos", PanelJuegos());
        tabbedPane.addTab("Clasificaciones", PanelClasificaciones());

        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        frame.setContentPane(mainPanel);
        frame.setVisible(true);
    }

    // Métodos para crear los paneles con los componentes reutilizables
    private static JPanel PanelJugadores() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new PanelInsertarJugador(), BorderLayout.NORTH);
        panel.add(new PanelConsultar("Consultar Jugadores", new String[]{"Nombre", "Equipo"}), BorderLayout.CENTER);
        return panel;
    }

    private static JPanel PanelEquipos() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new PanelInsertarEquipo(), BorderLayout.NORTH);
        panel.add(new PanelConsultar("Consultar Equipos", new String[]{"Nombre"}), BorderLayout.CENTER);
        return panel;
    }

    private static JPanel PanelPartidos() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new PanelInsertarPartido(), BorderLayout.NORTH);
        panel.add(new PanelConsultar("Consultar Partidos", new String[]{"Juego"}), BorderLayout.CENTER);
        return panel;
    }

    private static JPanel PanelLigas() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new PanelInsertarLiga(), BorderLayout.NORTH);
        panel.add(new PanelConsultar("Consultar Ligas", new String[]{"Nombre"}), BorderLayout.CENTER);
        return panel;
    }

    private static JPanel PanelJuegos() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new PanelInsertarJuego(), BorderLayout.NORTH);
        panel.add(new PanelConsultar("Consultar Juegos", new String[]{"Nombre"}), BorderLayout.CENTER);
        return panel;
    }

    private static JPanel PanelClasificaciones() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new PanelClasificacion(), BorderLayout.CENTER);
        return panel;
    }

    // Clase para crear componentes reutilizables de inserción de datos
    static class PanelInsertar extends JPanel {
        public PanelInsertar(String titulo, String[] campos) {
            setLayout(new GridLayout(campos.length + 1, 2));
            add(new JLabel(titulo));
            add(new JLabel()); // Espacio en blanco
            for (String campo : campos) {
                add(new JLabel(campo + ":"));
                add(new JTextField());
            }
            JButton guardar = new JButton("Guardar");
            guardar.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    // Lógica para manejar el envío del formulario y realizar la operación correspondiente
                    System.out.println("Formulario de inserción enviado");
                }
            });
            add(guardar);
        }
    }

    // Clase para crear componentes reutilizables de consulta de datos
    static class PanelConsultar extends JPanel {
        public PanelConsultar(String titulo, String[] campos) {
            setLayout(new GridLayout(campos.length + 1, 2));
            add(new JLabel(titulo));
            add(new JLabel()); // Espacio en blanco
            for (String campo : campos) {
                add(new JLabel(campo + ":"));
                add(new JTextField());
            }
            JButton consultar = new JButton("Consultar");
            consultar.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    // Lógica para manejar la consulta y mostrar los resultados
                    System.out.println("Consulta realizada");
                }
            });
            add(consultar);
        }
    }

    // Clase para crear componentes reutilizables de inserción de jugadores
    static class PanelInsertarJugador extends JPanel {
        public PanelInsertarJugador() {
            setLayout(new GridLayout(4, 2));
            add(new JLabel("Nombre:"));
            JTextField nombreField = new JTextField();
            add(nombreField);
            add(new JLabel("Apellido:"));
            JTextField apellidoField = new JTextField();
            add(apellidoField);
            add(new JLabel("Género:"));
            JTextField generoField = new JTextField();
            add(generoField);
            add(new JLabel("Dirección:"));
            JTextField direccionField = new JTextField();
            add(direccionField);

            JButton guardar = new JButton("Guardar");
            guardar.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    // Obtener los datos del jugador
                    String nombre = nombreField.getText();
                    String apellido = apellidoField.getText();
                    String genero = generoField.getText();
                    String direccion = direccionField.getText();
                    // Insertar el jugador en la base de datos
                    insertarJugador(nombre, apellido, genero, direccion);
                }
            });
            add(guardar);
        }

        private void insertarJugador(String nombre, String apellido, String genero, String direccion) {
            // Lógica para insertar el jugador en la base de datos
            // Puedes usar la conexión a la base de datos para ejecutar una consulta SQL INSERT
            // Por ejemplo:
            // INSERT INTO Players (first_name, last_name, gender, address) VALUES ('nombre', 'apellido', 'genero', 'direccion');
            // No olvides manejar las excepciones y mostrar mensajes de éxito o error
            try {
                Connection conexion = ConexionBD.getConexion();
                PreparedStatement statement = conexion.prepareStatement("INSERT INTO Players (first_name, last_name, gender, address) VALUES (?, ?, ?, ?)");
                statement.setString(1, nombre);
                statement.setString(2, apellido);
                statement.setString(3, genero);
                statement.setString(4, direccion);
                int filasInsertadas = statement.executeUpdate();
                if (filasInsertadas > 0) {
                    JOptionPane.showMessageDialog(null, "Jugador insertado correctamente");
                } else {
                    JOptionPane.showMessageDialog(null, "Error al insertar jugador");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error al insertar jugador: " + ex.getMessage());
            }
        }
    }

    // Clase para crear componentes reutilizables de inserción de equipos
    static class PanelInsertarEquipo extends JPanel {
        public PanelInsertarEquipo() {
            setLayout(new GridLayout(3, 2));
            add(new JLabel("Nombre del Equipo:"));
            JTextField nombreEquipoField = new JTextField();
            add(nombreEquipoField);
            add(new JLabel("ID del Jugador Creador:"));
            JTextField idCreadorField = new JTextField();
            add(idCreadorField);
            JButton guardar = new JButton("Guardar");
            guardar.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    // Obtener los datos del equipo
                    String nombreEquipo = nombreEquipoField.getText();
                    int idCreador = Integer.parseInt(idCreadorField.getText());
                    // Insertar el equipo en la base de datos
                    insertarEquipo(nombreEquipo, idCreador);
                }
            });
            add(guardar);
        }

        private void insertarEquipo(String nombreEquipo, int idCreador) {
            // Lógica para insertar el equipo en la base de datos
            // Puedes usar la conexión a la base de datos para ejecutar una consulta SQL INSERT
            // Por ejemplo:
            // INSERT INTO Teams (created_by_player_id, team_name) VALUES (idCreador, 'nombreEquipo');
            // No olvides manejar las excepciones y mostrar mensajes de éxito o error
            try {
                Connection conexion = ConexionBD.getConexion();
                PreparedStatement statement = conexion.prepareStatement("INSERT INTO Teams (created_by_player_id, team_name) VALUES (?, ?)");
                statement.setInt(1, idCreador);
                statement.setString(2, nombreEquipo);
                int filasInsertadas = statement.executeUpdate();
                if (filasInsertadas > 0) {
                    JOptionPane.showMessageDialog(null, "Equipo insertado correctamente");
                } else {
                    JOptionPane.showMessageDialog(null, "Error al insertar equipo");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error al insertar equipo: " + ex.getMessage());
            }
        }
    }

    // Clase para crear componentes reutilizables de inserción de partidos
    static class PanelInsertarPartido extends JPanel {
        public PanelInsertarPartido() {
            setLayout(new GridLayout(4, 2));
            add(new JLabel("Código del Juego:"));
            JTextField codigoJuegoField = new JTextField();
            add(codigoJuegoField);
            add(new JLabel("ID del Jugador 1:"));
            JTextField idJugador1Field = new JTextField();
            add(idJugador1Field);
            add(new JLabel("ID del Jugador 2:"));
            JTextField idJugador2Field = new JTextField();
            add(idJugador2Field);
            add(new JLabel("Resultado (win/lose/draw):"));
            JTextField resultadoField = new JTextField();
            add(resultadoField);

            JButton guardar = new JButton("Guardar");
            guardar.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    // Obtener los datos del partido
                    int codigoJuego = Integer.parseInt(codigoJuegoField.getText());
                    int idJugador1 = Integer.parseInt(idJugador1Field.getText());
                    int idJugador2 = Integer.parseInt(idJugador2Field.getText());
                    String resultado = resultadoField.getText();
                    // Insertar el partido en la base de datos
                    insertarPartido(codigoJuego, idJugador1, idJugador2, resultado);
                }
            });
            add(guardar);
        }

        private void insertarPartido(int codigoJuego, int idJugador1, int idJugador2, String resultado) {
            // Lógica para insertar el partido en la base de datos
            // Puedes usar la conexión a la base de datos para ejecutar una consulta SQL INSERT
            // Por ejemplo:
            // INSERT INTO Matches (game_code, player_1_id, player_2_id, result) VALUES (codigoJuego, idJugador1, idJugador2, 'resultado');
            // No olvides manejar las excepciones y mostrar mensajes de éxito o error
            try {
                Connection conexion = ConexionBD.getConexion();
                PreparedStatement statement = conexion.prepareStatement("INSERT INTO Matches (game_code, player_1_id, player_2_id, result) VALUES (?, ?, ?, ?)");
                statement.setInt(1, codigoJuego);
                statement.setInt(2, idJugador1);
                statement.setInt(3, idJugador2);
                statement.setString(4, resultado);
                int filasInsertadas = statement.executeUpdate();
                if (filasInsertadas > 0) {
                    JOptionPane.showMessageDialog(null, "Partido insertado correctamente");
                } else {
                    JOptionPane.showMessageDialog(null, "Error al insertar partido");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error al insertar partido: " + ex.getMessage());
            }
        }
    }

    // Clase para crear componentes reutilizables de inserción de ligas
    static class PanelInsertarLiga extends JPanel {
        public PanelInsertarLiga() {
            setLayout(new GridLayout(3, 2));
            add(new JLabel("Nombre de la Liga:"));
            JTextField nombreLigaField = new JTextField();
            add(nombreLigaField);
            add(new JLabel("Detalles de la Liga:"));
            JTextField detallesLigaField = new JTextField();
            add(detallesLigaField);
            JButton guardar = new JButton("Guardar");
            guardar.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    // Obtener los datos de la liga
                    String nombreLiga = nombreLigaField.getText();
                    String detallesLiga = detallesLigaField.getText();
                    // Insertar la liga en la base de datos
                    insertarLiga(nombreLiga, detallesLiga);
                }
            });
            add(guardar);
        }

        private void insertarLiga(String nombreLiga, String detallesLiga) {
            // Lógica para insertar la liga en la base de datos
            // Puedes usar la conexión a la base de datos para ejecutar una consulta SQL INSERT
            // Por ejemplo:
            // INSERT INTO Leagues (league_name, league_details) VALUES ('nombreLiga', 'detallesLiga');
            // No olvides manejar las excepciones y mostrar mensajes de éxito o error
            try {
                Connection conexion = ConexionBD.getConexion();
                PreparedStatement statement = conexion.prepareStatement("INSERT INTO Leagues (league_name, league_details) VALUES (?, ?)");
                statement.setString(1, nombreLiga);
                statement.setString(2, detallesLiga);
                int filasInsertadas = statement.executeUpdate();
                if (filasInsertadas > 0) {
                    JOptionPane.showMessageDialog(null, "Liga insertada correctamente");
                } else {
                    JOptionPane.showMessageDialog(null, "Error al insertar liga");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error al insertar liga: " + ex.getMessage());
            }
        }
    }

    // Clase para crear componentes reutilizables de inserción de juegos
    static class PanelInsertarJuego extends JPanel {
        public PanelInsertarJuego() {
            setLayout(new GridLayout(3, 2));
            add(new JLabel("Nombre del Juego:"));
            JTextField nombreJuegoField = new JTextField();
            add(nombreJuegoField);
            add(new JLabel("Descripción del Juego:"));
            JTextField descripcionJuegoField = new JTextField();
            add(descripcionJuegoField);
            JButton guardar = new JButton("Guardar");
            guardar.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    // Obtener los datos del juego
                    String nombreJuego = nombreJuegoField.getText();
                    String descripcionJuego = descripcionJuegoField.getText();
                    // Insertar el juego en la base de datos
                    insertarJuego(nombreJuego, descripcionJuego);
                }
            });
            add(guardar);
        }

        private void insertarJuego(String nombreJuego, String descripcionJuego) {
            // Lógica para insertar el juego en la base de datos
            // Puedes usar la conexión a la base de datos para ejecutar una consulta SQL INSERT
            // Por ejemplo:
            // INSERT INTO Games (game_name, game_description) VALUES ('nombreJuego', 'descripcionJuego');
            // No olvides manejar las excepciones y mostrar mensajes de éxito o error
            try {
                Connection conexion = ConexionBD.getConexion();
                PreparedStatement statement = conexion.prepareStatement("INSERT INTO Games (game_name, game_description) VALUES (?, ?)");
                statement.setString(1, nombreJuego);
                statement.setString(2, descripcionJuego);
                int filasInsertadas = statement.executeUpdate();
                if (filasInsertadas > 0) {
                    JOptionPane.showMessageDialog(null, "Juego insertado correctamente");
                } else {
                    JOptionPane.showMessageDialog(null, "Error al insertar juego");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error al insertar juego: " + ex.getMessage());
            }
        }
    }

    // Clase para crear componentes reutilizables de clasificación
    static class PanelClasificacion extends JPanel {
        public PanelClasificacion() {
            setLayout(new BorderLayout());
            JTextArea areaTexto = new JTextArea();
            areaTexto.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(areaTexto);
            add(scrollPane, BorderLayout.CENTER);
            JButton actualizar = new JButton("Actualizar Clasificación");
            actualizar.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    // Obtener clasificación y mostrarla en el área de texto
                    String clasificacion = obtenerClasificacion();
                    areaTexto.setText(clasificacion);
                }
            });
            add(actualizar, BorderLayout.SOUTH);
        }

        private String obtenerClasificacion() {
            // Lógica para obtener la clasificación de los jugadores en los juegos
            // Puedes usar la conexión a la base de datos para ejecutar una consulta SQL SELECT
            // y obtener los datos de clasificación
            // Por ejemplo:
            // SELECT p.first_name, p.last_name, g.game_name, pgr.ranking FROM Players p
            // JOIN Players_Game_Ranking pgr ON p.player_id = pgr.player_id
            // JOIN Games g ON pgr.game_code = g.game_code
            // ORDER BY g.game_name, pgr.ranking;
            // No olvides manejar las excepciones y devolver la clasificación como una cadena formateada
            StringBuilder clasificacion = new StringBuilder();
            try {
                Connection conexion = ConexionBD.getConexion();
                Statement statement = conexion.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT p.first_name, p.last_name, g.game_name, pgr.ranking FROM Players p JOIN Players_Game_Ranking pgr ON p.player_id = pgr.player_id JOIN Games g ON pgr.game_code = g.game_code ORDER BY g.game_name, pgr.ranking");
                while (resultSet.next()) {
                    String nombre = resultSet.getString("first_name");
                    String apellido = resultSet.getString("last_name");
                    String juego = resultSet.getString("game_name");
                    int ranking = resultSet.getInt("ranking");
                    clasificacion.append(juego).append(": ").append(nombre).append(" ").append(apellido).append(" - Ranking: ").append(ranking).append("\n");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error al obtener clasificación: " + ex.getMessage());
            }
            return clasificacion.toString();
        }
    }

    // Clases de servicio y utilidades
    class ConexionBD {
        private static Connection conexion;

        // Datos para la conexión a PostgreSQL
        private static final String URL = "jdbc:postgresql://localhost:5432/Gaming_Leagues";
        private static final String USER = "developer";
        private static final String PASSWORD = "23100132";

        // Método para establecer conexión con la base de datos
        public static void conectar() {
            try {
                conexion = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Conexión establecida con éxito.");
            } catch (SQLException e) {
                System.out.println("Error al conectar a la base de datos: " + e.getMessage());
            }
        }

        // Método para desconectar la base de datos
        public static void desconectar() {
            if (conexion != null) {
                try {
                    conexion.close();
                    System.out.println("Conexión cerrada con éxito.");
                } catch (SQLException e) {
                    System.out.println("Error al cerrar la conexión: " + e.getMessage());
                }
            }
        }

        public static Connection getConexion() {
            return conexion;
        }
    }

    // Clases de entidades
    class Jugador {
        private int playerId;
        private String firstName;
        private String lastName;
        private String gender;
        private String address;

        public Jugador(int playerId, String firstName, String lastName, String gender, String address) {
            this.playerId = playerId;
            this.firstName = firstName;
            this.lastName = lastName;
            this.gender = gender;
            this.address = address;
        }

        // Método para agregar un nuevo jugador a la base de datos
        public void agregarJugador() {
            String sql = "INSERT INTO Players (first_name, last_name, gender, address) VALUES (?, ?, ?, ?)";
            try (Connection conn = ConexionBD.getConexion();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, this.firstName);
                stmt.setString(2, this.lastName);
                stmt.setString(3, this.gender);
                stmt.setString(4, this.address);
                stmt.executeUpdate();
                System.out.println("Jugador agregado con éxito.");
            } catch (SQLException e) {
                System.out.println("Error al agregar jugador: " + e.getMessage());
            }
        }

        // Método para modificar un jugador existente
        public void modificarJugador() {
            String sql = "UPDATE Players SET first_name = ?, last_name = ?, gender = ?, address = ? WHERE player_id = ?";
            try (Connection conn = ConexionBD.getConexion();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, this.firstName);
                stmt.setString(2, this.lastName);
                stmt.setString(3, this.gender);
                stmt.setString(4, this.address);
                stmt.setInt(5, this.playerId);
                int affectedRows = stmt.executeUpdate();
                if (affectedRows > 0) {
                    System.out.println("Jugador actualizado con éxito.");
                } else {
                    System.out.println("No se encontró un jugador con el ID especificado.");
                }
            } catch (SQLException e) {
                System.out.println("Error al modificar jugador: " + e.getMessage());
            }
        }

        // Método para eliminar un jugador de la base de datos
        public void eliminarJugador() {
            String sql = "DELETE FROM Players WHERE player_id = ?";
            try (Connection conn = ConexionBD.getConexion();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, this.playerId);
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Jugador eliminado con éxito.");
                } else {
                    System.out.println("No se encontró un jugador para eliminar con el ID especificado.");
                }
            } catch (SQLException e) {
                System.out.println("Error al eliminar jugador: " + e.getMessage());
            }
        }

        // Método para visualizar detalles de un jugador
        public void visualizarJugador() {
            String sql = "SELECT * FROM Players WHERE player_id = ?";
            try (Connection conn = ConexionBD.getConexion();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, this.playerId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    System.out.println("Jugador ID: " + rs.getInt("player_id") +
                            ", Nombre: " + rs.getString("first_name") + " " + rs.getString("last_name") +
                            ", Género: " + rs.getString("gender") +
                            ", Dirección: " + rs.getString("address"));
                } else {
                    System.out.println("No se encontró un jugador con el ID especificado.");
                }
            } catch (SQLException e) {
                System.out.println("Error al visualizar jugador: " + e.getMessage());
            }
        }

        // Getters y Setters
        public int getPlayerId() {
            return playerId;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public String getGender() {
            return gender;
        }

        public String getAddress() {
            return address;
        }

        public void setPlayerId(int playerId) {
            this.playerId = playerId;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public class Direccion {
            private int playerId; // El ID del jugador para vincular la dirección, si se separa en una tabla independiente en el futuro.
            private String address;

            public Direccion(int playerId, String address) {
                this.playerId = playerId;
                this.address = address;
            }

            // Getters y Setters
            public int getPlayerId() {
                return playerId;
            }

            public void setPlayerId(int playerId) {
                this.playerId = playerId;
            }

            public String getAddress() {
                return address;
            }

            public void setAddress(String address) {
                this.address = address;
            }

            // Métodos para interacción con la base de datos podrían incluir:
            public void actualizarDireccion() {
                String sql = "UPDATE Players SET address = ? WHERE player_id = ?";
                try (Connection conn = ConexionBD.getConexion();
                     PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, this.address);
                    stmt.setInt(2, this.playerId);
                    int affectedRows = stmt.executeUpdate();
                    if (affectedRows > 0) {
                        System.out.println("Dirección actualizada con éxito para el jugador con ID: " + playerId);
                    } else {
                        System.out.println("No se encontró un jugador con el ID especificado para actualizar la dirección.");
                    }
                } catch (SQLException e) {
                    System.out.println("Error al actualizar la dirección: " + e.getMessage());
                }
            }
        }


        public class Juego {
            private int gameCode;
            private String gameName;
            private String gameDescription;

            public Juego(int gameCode, String gameName, String gameDescription) {
                this.gameCode = gameCode;
                this.gameName = gameName;
                this.gameDescription = gameDescription;
            }

            // Getters y Setters
            public int getGameCode() {
                return gameCode;
            }

            public void setGameCode(int gameCode) {
                this.gameCode = gameCode;
            }

            public String getGameName() {
                return gameName;
            }

            public void setGameName(String gameName) {
                this.gameName = gameName;
            }

            public String getGameDescription() {
                return gameDescription;
            }

            public void setGameDescription(String gameDescription) {
                this.gameDescription = gameDescription;
            }

            // Método para agregar un nuevo juego a la base de datos
            public void agregarJuego() {
                String sql = "INSERT INTO Games (game_name, game_description) VALUES (?, ?)";
                try (Connection conn = ConexionBD.getConexion();
                     PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, this.gameName);
                    stmt.setString(2, this.gameDescription);
                    stmt.executeUpdate();
                    System.out.println("Juego agregado con éxito.");
                } catch (SQLException e) {
                    System.out.println("Error al agregar juego: " + e.getMessage());
                }
            }

            // Método para modificar un juego existente
            public void modificarJuego() {
                String sql = "UPDATE Games SET game_name = ?, game_description = ? WHERE game_code = ?";
                try (Connection conn = ConexionBD.getConexion();
                     PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, this.gameName);
                    stmt.setString(2, this.gameDescription);
                    stmt.setInt(3, this.gameCode);
                    int affectedRows = stmt.executeUpdate();
                    if (affectedRows > 0) {
                        System.out.println("Juego actualizado con éxito.");
                    } else {
                        System.out.println("No se encontró un juego con el código especificado.");
                    }
                } catch (SQLException e) {
                    System.out.println("Error al modificar juego: " + e.getMessage());
                }
            }

            // Método para eliminar un juego
            public void eliminarJuego() {
                String sql = "DELETE FROM Games WHERE game_code = ?";
                try (Connection conn = ConexionBD.getConexion();
                     PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, this.gameCode);
                    int rowsAffected = stmt.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("Juego eliminado con éxito.");
                    } else {
                        System.out.println("No se encontró un juego para eliminar con el código especificado.");
                    }
                } catch (SQLException e) {
                    System.out.println("Error al eliminar juego: " + e.getMessage());
                }
            }

            // Método para visualizar detalles de un juego
            public void visualizarJuego() {
                String sql = "SELECT game_code, game_name, game_description FROM Games WHERE game_code = ?";
                try (Connection conn = ConexionBD.getConexion();
                     PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, this.gameCode);
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        System.out.println("Código de Juego: " + rs.getInt("game_code") +
                                ", Nombre: " + rs.getString("game_name") +
                                ", Descripción: " + rs.getString("game_description"));
                    } else {
                        System.out.println("No se encontró un juego con el código especificado.");
                    }
                } catch (SQLException e) {
                    System.out.println("Error al visualizar juego: " + e.getMessage());
                }
            }
        }

        public class Liga {
            private int leagueId;
            private String leagueName;
            private String leagueDetails;

            public Liga(int leagueId, String leagueName, String leagueDetails) {
                this.leagueId = leagueId;
                this.leagueName = leagueName;
                this.leagueDetails = leagueDetails;
            }

            // Getters y Setters
            public int getLeagueId() {
                return leagueId;
            }

            public void setLeagueId(int leagueId) {
                this.leagueId = leagueId;
            }

            public String getLeagueName() {
                return leagueName;
            }

            public void setLeagueName(String leagueName) {
                this.leagueName = leagueName;
            }

            public String getLeagueDetails() {
                return leagueDetails;
            }

            public void setLeagueDetails(String leagueDetails) {
                this.leagueDetails = leagueDetails;
            }

            // Método para agregar una nueva liga a la base de datos
            public void crearLiga() {
                String sql = "INSERT INTO Leagues (league_name, league_details) VALUES (?, ?)";
                try (Connection conn = ConexionBD.getConexion();
                     PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, this.leagueName);
                    stmt.setString(2, this.leagueDetails);
                    stmt.executeUpdate();
                    System.out.println("Liga creada con éxito.");
                } catch (SQLException e) {
                    System.out.println("Error al crear liga: " + e.getMessage());
                }
            }

            // Método para modificar una liga existente
            public void modificarLiga() {
                String sql = "UPDATE Leagues SET league_name = ?, league_details = ? WHERE league_id = ?";
                try (Connection conn = ConexionBD.getConexion();
                     PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, this.leagueName);
                    stmt.setString(2, this.leagueDetails);
                    stmt.setInt(3, this.leagueId);
                    int affectedRows = stmt.executeUpdate();
                    if (affectedRows > 0) {
                        System.out.println("Liga actualizada con éxito.");
                    } else {
                        System.out.println("No se encontró una liga con el ID especificado.");
                    }
                } catch (SQLException e) {
                    System.out.println("Error al modificar liga: " + e.getMessage());
                }
            }

            // Método para eliminar una liga
            public void eliminarLiga() {
                String sql = "DELETE FROM Leagues WHERE league_id = ?";
                try (Connection conn = ConexionBD.getConexion();
                     PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, this.leagueId);
                    int rowsAffected = stmt.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("Liga eliminada con éxito.");
                    } else {
                        System.out.println("No se encontró una liga para eliminar con el ID especificado.");
                    }
                } catch (SQLException e) {
                    System.out.println("Error al eliminar liga: " + e.getMessage());
                }
            }

            // Método para visualizar detalles de una liga
            public void visualizarLiga() {
                String sql = "SELECT league_id, league_name, league_details FROM Leagues WHERE league_id = ?";
                try (Connection conn = ConexionBD.getConexion();
                     PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, this.leagueId);
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        System.out.println("Liga ID: " + rs.getInt("league_id") +
                                ", Nombre: " + rs.getString("league_name") +
                                ", Detalles: " + rs.getString("league_details"));
                    } else {
                        System.out.println("No se encontró una liga con el ID especificado.");
                    }
                } catch (SQLException e) {
                    System.out.println("Error al visualizar liga: " + e.getMessage());
                }
            }
        }

        public class Equipo {
            private int teamId;
            private int createdByPlayerId;
            private String teamName;
            private LocalDate dateCreated;
            private LocalDate dateDisbanded;

            public Equipo(int teamId, int createdByPlayerId, String teamName, LocalDate dateCreated, LocalDate dateDisbanded) {
                this.teamId = teamId;
                this.createdByPlayerId = createdByPlayerId;
                this.teamName = teamName;
                this.dateCreated = dateCreated;
                this.dateDisbanded = dateDisbanded;
            }

            // Getters and setters
            public int getTeamId() {
                return teamId;
            }

            public void setTeamId(int teamId) {
                this.teamId = teamId;
            }

            public int getCreatedByPlayerId() {
                return createdByPlayerId;
            }

            public void setCreatedByPlayerId(int createdByPlayerId) {
                this.createdByPlayerId = createdByPlayerId;
            }

            public String getTeamName() {
                return teamName;
            }

            public void setTeamName(String teamName) {
                this.teamName = teamName;
            }

            public LocalDate getDateCreated() {
                return dateCreated;
            }

            public void setDateCreated(LocalDate dateCreated) {
                this.dateCreated = dateCreated;
            }

            public LocalDate getDateDisbanded() {
                return dateDisbanded;
            }

            public void setDateDisbanded(LocalDate dateDisbanded) {
                this.dateDisbanded = dateDisbanded;
            }

            // Métodos CRUD
            public void crearEquipo() {
                String sql = "INSERT INTO Teams (created_by_player_id, team_name, date_created) VALUES (?, ?, ?)";
                try (Connection conn = ConexionBD.getConexion();
                     PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, this.createdByPlayerId);
                    stmt.setString(2, this.teamName);
                    stmt.setDate(3, java.sql.Date.valueOf(this.dateCreated));
                    stmt.executeUpdate();
                    System.out.println("Equipo creado con éxito.");
                } catch (SQLException e) {
                    System.out.println("Error al crear equipo: " + e.getMessage());
                }
            }

            public void modificarEquipo() {
                String sql = "UPDATE Teams SET team_name = ?, date_disbanded = ? WHERE team_id = ?";
                try (Connection conn = ConexionBD.getConexion();
                     PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, this.teamName);
                    stmt.setDate(2, java.sql.Date.valueOf(this.dateDisbanded));
                    stmt.setInt(3, this.teamId);
                    int affectedRows = stmt.executeUpdate();
                    if (affectedRows > 0) {
                        System.out.println("Equipo actualizado con éxito.");
                    } else {
                        System.out.println("No se encontró un equipo con el ID especificado.");
                    }
                } catch (SQLException e) {
                    System.out.println("Error al modificar equipo: " + e.getMessage());
                }
            }

            public void eliminarEquipo() {
                String sql = "DELETE FROM Teams WHERE team_id = ?";
                try (Connection conn = ConexionBD.getConexion();
                     PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, this.teamId);
                    int rowsAffected = stmt.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("Equipo eliminado con éxito.");
                    } else {
                        System.out.println("No se encontró un equipo para eliminar con el ID especificado.");
                    }
                } catch (SQLException e) {
                    System.out.println("Error al eliminar equipo: " + e.getMessage());
                }
            }

            // Método para visualizar detalles de un equipo
            public void visualizarEquipo() {
                String sql = "SELECT team_id, created_by_player_id, team_name, date_created, date_disbanded FROM Teams WHERE team_id = ?";
                try (Connection conn = ConexionBD.getConexion();
                     PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, this.teamId);
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        System.out.println("Equipo ID: " + rs.getInt("team_id") +
                                ", Creado por Jugador ID: " + rs.getInt("created_by_player_id") +
                                ", Nombre: " + rs.getString("team_name") +
                                ", Fecha de Creación: " + rs.getDate("date_created") +
                                ", Fecha de Disolución: " + rs.getDate("date_disbanded"));
                    } else {
                        System.out.println("No se encontró un equipo con el ID especificado.");
                    }
                } catch (SQLException e) {
                    System.out.println("Error al visualizar equipo: " + e.getMessage());
                }
            }
        }

        public class Partido {
            private int matchId;
            private int gameCode;
            private int player1Id;
            private int player2Id;
            private LocalDate matchDate;
            private String result;

            public Partido(int matchId, int gameCode, int player1Id, int player2Id, LocalDate matchDate, String result) {
                this.matchId = matchId;
                this.gameCode = gameCode;
                this.player1Id = player1Id;
                this.player2Id = player2Id;
                this.matchDate = matchDate;
                this.result = result;
            }

            // Getters and Setters
            public int getMatchId() {
                return matchId;
            }

            public void setMatchId(int matchId) {
                this.matchId = matchId;
            }

            public int getGameCode() {
                return gameCode;
            }

            public void setGameCode(int gameCode) {
                this.gameCode = gameCode;
            }

            public int getPlayer1Id() {
                return player1Id;
            }

            public void setPlayer1Id(int player1Id) {
                this.player1Id = player1Id;
            }

            public int getPlayer2Id() {
                return player2Id;
            }

            public void setPlayer2Id(int player2Id) {
                this.player2Id = player2Id;
            }

            public LocalDate getMatchDate() {
                return matchDate;
            }

            public void setMatchDate(LocalDate matchDate) {
                this.matchDate = matchDate;
            }

            public String getResult() {
                return result;
            }

            public void setResult(String result) {
                this.result = result;
            }

            // Método para agregar un nuevo partido
            public void agregarPartido() {
                String sql = "INSERT INTO Matches (game_code, player_1_id, player_2_id, match_date, result) VALUES (?, ?, ?, ?, ?)";
                try (Connection conn = ConexionBD.getConexion();
                     PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, this.gameCode);
                    stmt.setInt(2, this.player1Id);
                    stmt.setInt(3, this.player2Id);
                    stmt.setDate(4, java.sql.Date.valueOf(this.matchDate));
                    stmt.setString(5, this.result);
                    stmt.executeUpdate();
                    System.out.println("Partido agregado con éxito.");
                } catch (SQLException e) {
                    System.out.println("Error al agregar partido: " + e.getMessage());
                }
            }

            // Método para actualizar un partido
            public void modificarPartido() {
                String sql = "UPDATE Matches SET game_code = ?, player_1_id = ?, player_2_id = ?, match_date = ?, result = ? WHERE match_id = ?";
                try (Connection conn = ConexionBD.getConexion();
                     PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, this.gameCode);
                    stmt.setInt(2, this.player1Id);
                    stmt.setInt(3, this.player2Id);
                    stmt.setDate(4, java.sql.Date.valueOf(this.matchDate));
                    stmt.setString(5, this.result);
                    stmt.setInt(6, this.matchId);
                    int affectedRows = stmt.executeUpdate();
                    if (affectedRows > 0) {
                        System.out.println("Partido actualizado con éxito.");
                    } else {
                        System.out.println("No se encontró un partido con el ID especificado.");
                    }
                } catch (SQLException e) {
                    System.out.println("Error al modificar partido: " + e.getMessage());
                }
            }

            // Método para eliminar un partido
            public void eliminarPartido() {
                String sql = "DELETE FROM Matches WHERE match_id = ?";
                try (Connection conn = ConexionBD.getConexion();
                     PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, this.matchId);
                    int rowsAffected = stmt.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("Partido eliminado con éxito.");
                    } else {
                        System.out.println("No se encontró un partido para eliminar con el ID especificado.");
                    }
                } catch (SQLException e) {
                    System.out.println("Error al eliminar partido: " + e.getMessage());
                }
            }

            // Método para visualizar detalles de un partido
            public void visualizarPartido() {
                String sql = "SELECT match_id, game_code, player_1_id, player_2_id, match_date, result FROM Matches WHERE match_id = ?";
                try (Connection conn = ConexionBD.getConexion();
                     PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, this.matchId);
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        System.out.println("Partido ID: " + rs.getInt("match_id") +
                                ", Código de Juego: " + rs.getInt("game_code") +
                                ", Jugador 1 ID: " + rs.getInt("player_1_id") +
                                ", Jugador 2 ID: " + rs.getInt("player_2_id") +
                                ", Fecha del Partido: " + rs.getDate("match_date") +
                                ", Resultado: " + rs.getString("result"));
                    } else {
                        System.out.println("No se encontró un partido con el ID especificado.");
                    }
                } catch (SQLException e) {
                    System.out.println("Error al visualizar partido: " + e.getMessage());
                }
            }
        }


        public class Ranking {
            private int playerId;
            private int gameCode;
            private int ranking;

            public Ranking(int playerId, int gameCode, int ranking) {
                this.playerId = playerId;
                this.gameCode = gameCode;
                this.ranking = ranking;
            }

            // Getters y Setters
            public int getPlayerId() {
                return playerId;
            }

            public void setPlayerId(int playerId) {
                this.playerId = playerId;
            }

            public int getGameCode() {
                return gameCode;
            }

            public void setGameCode(int gameCode) {
                this.gameCode = gameCode;
            }

            public int getRanking() {
                return ranking;
            }

            public void setRanking(int ranking) {
                this.ranking = ranking;
            }

            // Método para actualizar el ranking de un jugador en un juego
            public void actualizarRanking() {
                String sql = "UPDATE Players_Game_Ranking SET ranking = ? WHERE player_id = ? AND game_code = ?";
                try (Connection conn = ConexionBD.getConexion();
                     PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, this.ranking);
                    stmt.setInt(2, this.playerId);
                    stmt.setInt(3, this.gameCode);
                    int affectedRows = stmt.executeUpdate();
                    if (affectedRows > 0) {
                        System.out.println("Ranking actualizado con éxito.");
                    } else {
                        System.out.println("No se encontró el ranking para actualizar.");
                    }
                } catch (SQLException e) {
                    System.out.println("Error al actualizar el ranking: " + e.getMessage());
                }
            }

            // Método para visualizar el ranking de un jugador para un juego específico
            public void visualizarRanking() {
                String sql = "SELECT ranking FROM Players_Game_Ranking WHERE player_id = ? AND game_code = ?";
                try (Connection conn = ConexionBD.getConexion();
                     PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, this.playerId);
                    stmt.setInt(2, this.gameCode);
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        System.out.println("Ranking actual: " + rs.getInt("ranking") +
                                " para el Jugador ID: " + this.playerId +
                                " en el Juego ID: " + this.gameCode);
                    } else {
                        System.out.println("No se encontró ranking para mostrar.");
                    }
                } catch (SQLException e) {
                    System.out.println("Error al visualizar el ranking: " + e.getMessage());
                }
            }
        }
    }
}

