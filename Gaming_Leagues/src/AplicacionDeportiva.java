import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;



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

// Clase principal de la aplicación
public class AplicacionDeportiva {
    private static JFrame frame;
    private static JPanel mainPanel;
    private static JTabbedPane tabbedPane;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> crearYMostrarGUI());
    }

    private static void crearYMostrarGUI() {
        frame = new JFrame("Sistema de Gestión Deportiva");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);

        mainPanel = new JPanel(new BorderLayout());
        tabbedPane = new JTabbedPane();

        tabbedPane.addTab("Jugadores", new PanelJugadores());
        tabbedPane.addTab("Equipos", new PanelEquipos());
        tabbedPane.addTab("Ligas", new PanelLigas());
        tabbedPane.addTab("Juegos", new PanelJuegos());
        tabbedPane.addTab("Partidos", new PanelPartidos());
        tabbedPane.addTab("Procesos de Negocio", new PanelProcesos());
        tabbedPane.addTab("Reportes", new PanelReportes());

        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        frame.setContentPane(mainPanel);
        frame.setVisible(true);
    }

    // Clase para crear el panel de jugadores
    static class PanelJugadores extends JPanel {
        private JTable jugadoresTable;
        private DefaultTableModel tableModel;
        private Connection connection;

        public PanelJugadores() {
            setLayout(new BorderLayout());

            // Crear tabla para mostrar jugadores
            tableModel = new DefaultTableModel();
            tableModel.addColumn("ID");
            tableModel.addColumn("Nombre");
            tableModel.addColumn("Apellido");
            tableModel.addColumn("Género");
            tableModel.addColumn("Dirección");

            jugadoresTable = new JTable(tableModel);
            JScrollPane scrollPane = new JScrollPane(jugadoresTable);
            add(scrollPane, BorderLayout.CENTER);

            // Panel para agregar nuevos jugadores
            JPanel panelAgregar = new JPanel(new GridLayout(2, 2));
            panelAgregar.setBorder(BorderFactory.createTitledBorder("Agregar Jugador"));

            JTextField nombreField = new JTextField();
            JTextField apellidoField = new JTextField();
            JTextField generoField = new JTextField();
            JTextField direccionField = new JTextField();

            panelAgregar.add(new JLabel("Nombre:"));
            panelAgregar.add(nombreField);
            panelAgregar.add(new JLabel("Apellido:"));
            panelAgregar.add(apellidoField);
            panelAgregar.add(new JLabel("Género:"));
            panelAgregar.add(generoField);
            panelAgregar.add(new JLabel("Dirección:"));
            panelAgregar.add(direccionField);

            JButton agregarButton = new JButton("Agregar");
            agregarButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String nombre = nombreField.getText();
                    String apellido = apellidoField.getText();
                    String genero = generoField.getText();
                    String direccion = direccionField.getText();

                    agregarJugador(nombre, apellido, genero, direccion);
                }
            });
            panelAgregar.add(agregarButton);

            add(panelAgregar, BorderLayout.SOUTH);

            // Establecer conexión a la base de datos
            ConexionBD.conectar();
            connection = ConexionBD.getConexion();
            if (connection != null) {
                cargarJugadores();
            } else {
                JOptionPane.showMessageDialog(this, "Error al conectar a la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        // Método para cargar los jugadores desde la base de datos a la tabla
        private void cargarJugadores() {
            try {
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT * FROM Players");
                while (resultSet.next()) {
                    int id = resultSet.getInt("player_id");
                    String nombre = resultSet.getString("first_name");
                    String apellido = resultSet.getString("last_name");
                    String genero = resultSet.getString("gender");
                    String direccion = resultSet.getString("address");

                    tableModel.addRow(new Object[]{id, nombre, apellido, genero, direccion});
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al cargar jugadores desde la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }

        // Método para agregar un nuevo jugador a la base de datos y actualizar la tabla
        private void agregarJugador(String nombre, String apellido, String genero, String direccion) {
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "INSERT INTO Players (first_name, last_name, gender, address) VALUES (?, ?, ?, ?)"
                );
                preparedStatement.setString(1, nombre);
                preparedStatement.setString(2, apellido);
                preparedStatement.setString(3, genero);
                preparedStatement.setString(4, direccion);

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Jugador agregado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    tableModel.setRowCount(0); // Limpiar la tabla antes de cargar los datos actualizados
                    cargarJugadores(); // Volver a cargar los jugadores desde la base de datos
                } else {
                    JOptionPane.showMessageDialog(this, "Error al agregar el jugador.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al agregar el jugador.", "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }

    }

    // Clase para crear el panel de equipos
    static class PanelEquipos extends JPanel {
        private JTable equiposTable;
        private DefaultTableModel tableModel;
        private Connection connection;

        public PanelEquipos() {
            setLayout(new BorderLayout());

            // Crear tabla para mostrar equipos
            tableModel = new DefaultTableModel();
            tableModel.addColumn("ID");
            tableModel.addColumn("Nombre");
            tableModel.addColumn("Creado por");
            tableModel.addColumn("Fecha de Creación");
            tableModel.addColumn("Fecha de Disolución");

            equiposTable = new JTable(tableModel);
            JScrollPane scrollPane = new JScrollPane(equiposTable);
            add(scrollPane, BorderLayout.CENTER);

            // Panel para agregar nuevos equipos
            JPanel panelAgregar = new JPanel(new GridLayout(3, 2));
            panelAgregar.setBorder(BorderFactory.createTitledBorder("Agregar Equipo"));

            JTextField nombreField = new JTextField();
            JTextField creadoPorField = new JTextField();
            JTextField fechaCreacionField = new JTextField();
            JTextField fechaDisolucionField = new JTextField();

            panelAgregar.add(new JLabel("Nombre:"));
            panelAgregar.add(nombreField);
            panelAgregar.add(new JLabel("Creado por (ID del Jugador):"));
            panelAgregar.add(creadoPorField);
            panelAgregar.add(new JLabel("Fecha de Creación:"));
            panelAgregar.add(fechaCreacionField);
            panelAgregar.add(new JLabel("Fecha de Disolución:"));
            panelAgregar.add(fechaDisolucionField);

            JButton agregarButton = new JButton("Agregar");
            agregarButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String nombre = nombreField.getText();
                    int creadoPor = Integer.parseInt(creadoPorField.getText());
                    String fechaCreacion = fechaCreacionField.getText();
                    String fechaDisolucion = fechaDisolucionField.getText();

                    agregarEquipo(nombre, creadoPor, fechaCreacion, fechaDisolucion);
                }
            });
            panelAgregar.add(agregarButton);

            add(panelAgregar, BorderLayout.SOUTH);

            // Establecer conexión a la base de datos
            ConexionBD.conectar();
            connection = ConexionBD.getConexion();
            if (connection != null) {
                cargarEquipos();
            } else {
                JOptionPane.showMessageDialog(this, "Error al conectar a la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        // Método para cargar los equipos desde la base de datos a la tabla
        private void cargarEquipos() {
            try {
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT * FROM Teams");
                while (resultSet.next()) {
                    int id = resultSet.getInt("team_id");
                    String nombre = resultSet.getString("team_name");
                    int creadoPor = resultSet.getInt("created_by_player_id");
                    String fechaCreacion = resultSet.getString("date_created");
                    String fechaDisolucion = resultSet.getString("date_disbanded");

                    tableModel.addRow(new Object[]{id, nombre, creadoPor, fechaCreacion, fechaDisolucion});
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al cargar equipos desde la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }

        // Método para agregar un nuevo equipo a la base de datos y actualizar la tabla
        private void agregarEquipo(String nombre, int creadoPor, String fechaCreacion, String fechaDisolucion) {
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "INSERT INTO Teams (team_name, created_by, creation_date, dissolution_date) VALUES (?, ?, ?, ?)"
                );
                preparedStatement.setString(1, nombre);
                preparedStatement.setInt(2, creadoPor);
                preparedStatement.setString(3, fechaCreacion);
                preparedStatement.setString(4, fechaDisolucion);

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Equipo agregado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    tableModel.setRowCount(0); // Limpiar la tabla antes de cargar los datos actualizados
                    cargarEquipos(); // Volver a cargar los equipos desde la base de datos
                } else {
                    JOptionPane.showMessageDialog(this, "Error al agregar el equipo.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al agregar el equipo.", "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }


    }
    // Clase para crear el panel de ligas
    static class PanelLigas extends JPanel {
        private JTable ligasTable;
        private DefaultTableModel tableModel;
        private Connection connection;

        public PanelLigas() {
            setLayout(new BorderLayout());

            // Crear tabla para mostrar ligas
            tableModel = new DefaultTableModel();
            tableModel.addColumn("ID");
            tableModel.addColumn("Nombre");
            tableModel.addColumn("Detalles");

            ligasTable = new JTable(tableModel);
            JScrollPane scrollPane = new JScrollPane(ligasTable);
            add(scrollPane, BorderLayout.CENTER);

            // Panel para agregar nuevas ligas
            JPanel panelAgregar = new JPanel(new GridLayout(2, 2));
            panelAgregar.setBorder(BorderFactory.createTitledBorder("Agregar Liga"));

            JTextField nombreField = new JTextField();
            JTextField detallesField = new JTextField();

            panelAgregar.add(new JLabel("Nombre:"));
            panelAgregar.add(nombreField);
            panelAgregar.add(new JLabel("Detalles:"));
            panelAgregar.add(detallesField);

            JButton agregarButton = new JButton("Agregar");
            agregarButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String nombre = nombreField.getText();
                    String detalles = detallesField.getText();

                    agregarLiga(nombre, detalles);
                }
            });
            panelAgregar.add(agregarButton);

            add(panelAgregar, BorderLayout.SOUTH);

            // Establecer conexión a la base de datos
            ConexionBD.conectar();
            connection = ConexionBD.getConexion();
            if (connection != null) {
                cargarLigas();
            } else {
                JOptionPane.showMessageDialog(this, "Error al conectar a la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        // Método para cargar las ligas desde la base de datos a la tabla
        private void cargarLigas() {
            try {
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT * FROM Leagues");
                while (resultSet.next()) {
                    int id = resultSet.getInt("league_id");
                    String nombre = resultSet.getString("league_name");
                    String detalles = resultSet.getString("league_details");

                    tableModel.addRow(new Object[]{id, nombre, detalles});
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al cargar ligas desde la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }

        // Método para agregar una nueva liga a la base de datos y actualizar la tabla
        private void agregarLiga(String nombre, String detalles) {
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "INSERT INTO Leagues (league_name, details) VALUES (?, ?)"
                );
                preparedStatement.setString(1, nombre);
                preparedStatement.setString(2, detalles);

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Liga agregada exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    tableModel.setRowCount(0); // Limpiar la tabla antes de cargar los datos actualizados
                    cargarLigas(); // Volver a cargar las ligas desde la base de datos
                } else {
                    JOptionPane.showMessageDialog(this, "Error al agregar la liga.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al agregar la liga.", "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }

        // Método para cerrar la conexión a la base de datos

    }

    // Clase para crear el panel de juegos
    static class PanelJuegos extends JPanel {
        private JTable juegosTable;
        private DefaultTableModel tableModel;
        private Connection connection;

        public PanelJuegos() {
            setLayout(new BorderLayout());

            // Crear tabla para mostrar juegos
            tableModel = new DefaultTableModel();
            tableModel.addColumn("Código");
            tableModel.addColumn("Nombre");
            tableModel.addColumn("Descripción");

            juegosTable = new JTable(tableModel);
            JScrollPane scrollPane = new JScrollPane(juegosTable);
            add(scrollPane, BorderLayout.CENTER);

            // Panel para agregar nuevos juegos
            JPanel panelAgregar = new JPanel(new GridLayout(2, 2));
            panelAgregar.setBorder(BorderFactory.createTitledBorder("Agregar Juego"));

            JTextField nombreField = new JTextField();
            JTextField descripcionField = new JTextField();

            panelAgregar.add(new JLabel("Nombre:"));
            panelAgregar.add(nombreField);
            panelAgregar.add(new JLabel("Descripción:"));
            panelAgregar.add(descripcionField);

            JButton agregarButton = new JButton("Agregar");
            agregarButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String nombre = nombreField.getText();
                    String descripcion = descripcionField.getText();

                    agregarJuego(nombre, descripcion);
                }
            });
            panelAgregar.add(agregarButton);

            add(panelAgregar, BorderLayout.SOUTH);

            // Establecer conexión a la base de datos
            ConexionBD.conectar();
            connection = ConexionBD.getConexion();
            if (connection != null) {
                cargarJuegos();
            } else {
                JOptionPane.showMessageDialog(this, "Error al conectar a la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        // Método para cargar los juegos desde la base de datos a la tabla
        private void cargarJuegos() {
            try {
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT * FROM Games");
                while (resultSet.next()) {
                    int codigo = resultSet.getInt("game_code");
                    String nombre = resultSet.getString("game_name");
                    String descripcion = resultSet.getString("game_description");

                    tableModel.addRow(new Object[]{codigo, nombre, descripcion});
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al cargar juegos desde la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }

        // Método para agregar un nuevo juego a la base de datos y actualizar la tabla
        private void agregarJuego(String nombre, String descripcion) {
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "INSERT INTO Games (game_name, description) VALUES (?, ?)"
                );
                preparedStatement.setString(1, nombre);
                preparedStatement.setString(2, descripcion);

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Juego agregado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    tableModel.setRowCount(0); // Limpiar la tabla antes de cargar los datos actualizados
                    cargarJuegos(); // Volver a cargar los juegos desde la base de datos
                } else {
                    JOptionPane.showMessageDialog(this, "Error al agregar el juego.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al agregar el juego.", "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }

        // Método para cerrar la conexión a la base de datos

    }

    // Clase para crear el panel de partidos
    static class PanelPartidos extends JPanel {
        private JTable partidosTable;
        private DefaultTableModel tableModel;
        private Connection connection;

        public PanelPartidos() {
            setLayout(new BorderLayout());

            // Crear tabla para mostrar partidos
            tableModel = new DefaultTableModel();
            tableModel.addColumn("ID");
            tableModel.addColumn("Código de Juego");
            tableModel.addColumn("Jugador 1");
            tableModel.addColumn("Jugador 2");
            tableModel.addColumn("Fecha");
            tableModel.addColumn("Resultado");

            partidosTable = new JTable(tableModel);
            JScrollPane scrollPane = new JScrollPane(partidosTable);
            add(scrollPane, BorderLayout.CENTER);

            // Panel para agregar nuevos partidos
            JPanel panelAgregar = new JPanel(new GridLayout(3, 2));
            panelAgregar.setBorder(BorderFactory.createTitledBorder("Agregar Partido"));

            JTextField codigoJuegoField = new JTextField();
            JTextField jugador1Field = new JTextField();
            JTextField jugador2Field = new JTextField();
            JTextField fechaField = new JTextField();
            JTextField resultadoField = new JTextField();

            panelAgregar.add(new JLabel("Código de Juego:"));
            panelAgregar.add(codigoJuegoField);
            panelAgregar.add(new JLabel("Jugador 1:"));
            panelAgregar.add(jugador1Field);
            panelAgregar.add(new JLabel("Jugador 2:"));
            panelAgregar.add(jugador2Field);
            panelAgregar.add(new JLabel("Fecha:"));
            panelAgregar.add(fechaField);
            panelAgregar.add(new JLabel("Resultado:"));
            panelAgregar.add(resultadoField);

            JButton agregarButton = new JButton("Agregar");
            agregarButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    int codigoJuego = Integer.parseInt(codigoJuegoField.getText());
                    int jugador1 = Integer.parseInt(jugador1Field.getText());
                    int jugador2 = Integer.parseInt(jugador2Field.getText());
                    String fecha = fechaField.getText();
                    String resultado = resultadoField.getText();

                    agregarPartido(codigoJuego, jugador1, jugador2, fecha, resultado);
                }
            });
            panelAgregar.add(agregarButton);

            add(panelAgregar, BorderLayout.SOUTH);

            // Establecer conexión a la base de datos
            ConexionBD.conectar();
            connection = ConexionBD.getConexion();
            if (connection != null) {
                cargarPartidos();
            } else {
                JOptionPane.showMessageDialog(this, "Error al conectar a la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        // Método para cargar los partidos desde la base de datos a la tabla
        private void cargarPartidos() {
            try {
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT * FROM Matches");
                while (resultSet.next()) {
                    int id = resultSet.getInt("match_id");
                    int codigoJuego = resultSet.getInt("game_code");
                    int jugador1 = resultSet.getInt("player_1_id");
                    int jugador2 = resultSet.getInt("player_2_id");
                    String fecha = resultSet.getString("match_date");
                    String resultado = resultSet.getString("result");

                    tableModel.addRow(new Object[]{id, codigoJuego, jugador1, jugador2, fecha, resultado});
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al cargar partidos desde la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }

        // Método para agregar un nuevo partido a la base de datos y actualizar la tabla
        private void agregarPartido(int codigoJuego, int jugador1, int jugador2, String fecha, String resultado) {
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "INSERT INTO Matches (game_code, player1_id, player2_id, match_date, result) VALUES (?, ?, ?, ?, ?)"
                );
                preparedStatement.setInt(1, codigoJuego);
                preparedStatement.setInt(2, jugador1);
                preparedStatement.setInt(3, jugador2);
                preparedStatement.setString(4, fecha);
                preparedStatement.setString(5, resultado);

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Partido agregado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    tableModel.setRowCount(0); // Limpiar la tabla antes de cargar los datos actualizados
                    cargarPartidos(); // Volver a cargar los partidos desde la base de datos
                } else {
                    JOptionPane.showMessageDialog(this, "Error al agregar el partido.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al agregar el partido.", "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    // Clase para crear el panel de procesos de negocio
    static class PanelProcesos extends JPanel {
        // Definir las conexiones con la base de datos como variables de clase
        static Connection conn;
        static Statement stmt;
        static ResultSet rs;

        public PanelProcesos() {
            setLayout(new GridLayout(2, 1));

            // Panel para asignar jugadores a equipos
            JPanel panelAsignarJugadores = new JPanel(new BorderLayout());
            panelAsignarJugadores.setBorder(BorderFactory.createTitledBorder("Asignar Jugadores a Equipos"));

            JComboBox<String> equiposComboBox = new JComboBox<>();
            JComboBox<String> jugadoresComboBox = new JComboBox<>();

            cargarEquiposEnComboBox(equiposComboBox);
            cargarJugadoresEnComboBox(jugadoresComboBox);

            JButton asignarButton = new JButton("Asignar");
            asignarButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String equipoSeleccionado = (String) equiposComboBox.getSelectedItem();
                    String jugadorSeleccionado = (String) jugadoresComboBox.getSelectedItem();

                    // Lógica para asignar jugador seleccionado al equipo seleccionado
                    try {
                        // Abrir conexión con la base de datos
                        conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/Gaming_Leagues", "developer", "23100132");

                        // Preparar la consulta SQL para actualizar la base de datos con la asignación
                        String query = "UPDATE players SET teams = ? WHERE nombre = ?";
                        PreparedStatement preparedStmt = conn.prepareStatement(query);
                        preparedStmt.setString(1, equipoSeleccionado);
                        preparedStmt.setString(2, jugadorSeleccionado);

                        // Ejecutar la consulta
                        preparedStmt.executeUpdate();

                        // Cerrar la conexión con la base de datos
                        conn.close();

                        // Mostrar mensaje de éxito
                        JOptionPane.showMessageDialog(null, "Asignación exitosa");
                    } catch (SQLException ex) {
                        // Manejar excepciones y mostrar mensaje de error
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Error al asignar jugador al equipo");
                    }
                }
            });

            panelAsignarJugadores.add(equiposComboBox, BorderLayout.NORTH);
            panelAsignarJugadores.add(jugadoresComboBox, BorderLayout.CENTER);
            panelAsignarJugadores.add(asignarButton, BorderLayout.SOUTH);

            // Panel para organizar partidos entre dos jugadores
            JPanel panelOrganizarPartidoJugadores = new JPanel(new BorderLayout());
            panelOrganizarPartidoJugadores.setBorder(BorderFactory.createTitledBorder("Organizar Partido entre Jugadores"));

            JComboBox<String> jugadoresComboBox1 = new JComboBox<>();
            JComboBox<String> jugadoresComboBox2 = new JComboBox<>();

            cargarJugadoresEnComboBox(jugadoresComboBox1);
            cargarJugadoresEnComboBox(jugadoresComboBox2);

            JButton organizarPartidoButton = new JButton("Organizar Partido");
            organizarPartidoButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String jugador1Seleccionado = (String) jugadoresComboBox1.getSelectedItem();
                    String jugador2Seleccionado = (String) jugadoresComboBox2.getSelectedItem();

                    // Lógica para organizar partido entre los dos jugadores seleccionados
                    // Implementa aquí tu lógica para organizar el partido entre los jugadores seleccionados
                }
            });

            panelOrganizarPartidoJugadores.add(jugadoresComboBox1, BorderLayout.NORTH);
            panelOrganizarPartidoJugadores.add(jugadoresComboBox2, BorderLayout.CENTER);
            panelOrganizarPartidoJugadores.add(organizarPartidoButton, BorderLayout.SOUTH);

            // Panel para organizar partidos entre ligas
            JPanel panelOrganizarPartidoLigas = new JPanel(new BorderLayout());
            panelOrganizarPartidoLigas.setBorder(BorderFactory.createTitledBorder("Organizar Partido entre Ligas"));

            JComboBox<String> ligasComboBox1 = new JComboBox<>();

            JComboBox<String> equiposComboBoxLiga1 = new JComboBox<>();


            cargarLigasEnComboBox(ligasComboBox1); // Método para cargar las ligas en el ComboBox 1
            //cargarLigasEnComboBox(ligasComboBox2); // Método para cargar las ligas en el ComboBox 2

// Acción al seleccionar una liga en el ComboBox 1
            ligasComboBox1.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String ligaSeleccionada = (String) ligasComboBox1.getSelectedItem();
                    cargarLigasEnComboBox( equiposComboBoxLiga1); // Método para cargar los equipos de la liga seleccionada en el ComboBox 1
                }
            });

            JButton organizarPartidoButtonLigas = new JButton("Organizar Partido");
            organizarPartidoButtonLigas.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String liga1Seleccionada = (String) ligasComboBox1.getSelectedItem();
                    String equipo1Seleccionado = (String) equiposComboBoxLiga1.getSelectedItem();


                    // Lógica para organizar partido entre los equipos de las ligas seleccionadas
                    // Implementa aquí tu lógica para organizar el partido entre los equipos seleccionados de las ligas seleccionadas
                }
            });

// Crear paneles para los JComboBox de las ligas y equipos
            JPanel ligasPanel = new JPanel(new GridLayout(1, 2));
            ligasPanel.add(ligasComboBox1);


            panelOrganizarPartidoLigas.add(ligasPanel, BorderLayout.NORTH);
            panelOrganizarPartidoLigas.add(equiposComboBoxLiga1, BorderLayout.CENTER);
            panelOrganizarPartidoLigas.add(organizarPartidoButtonLigas, BorderLayout.SOUTH);



            // Panel para organizar partidos entre equipos de una misma liga
            JPanel panelOrganizarPartidoLigaUnica = new JPanel(new BorderLayout());
            panelOrganizarPartidoLigaUnica.setBorder(BorderFactory.createTitledBorder("Organizar Partido entre Equipos de una Liga"));

            JComboBox<String> ligaUnicaComboBox = new JComboBox<>();
            JComboBox<String> equiposComboBoxLigaUnica1 = new JComboBox<>();
            JComboBox<String> equiposComboBoxLigaUnica2 = new JComboBox<>();

            cargarLigasEnComboBox(ligaUnicaComboBox); // Método para cargar las ligas en el ComboBox
            cargarEquiposEnComboBox(equiposComboBoxLigaUnica1); // Método para cargar los equipos de la liga en el ComboBox
            cargarEquiposEnComboBox(equiposComboBoxLigaUnica2); // Método para cargar los equipos de la liga en el ComboBox

            JButton organizarPartidoButtonLigaUnica = new JButton("Organizar Partido");
            organizarPartidoButtonLigaUnica.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String ligaSeleccionada = (String) ligaUnicaComboBox.getSelectedItem();
                    String equipo1Seleccionado = (String) equiposComboBoxLigaUnica1.getSelectedItem();
                    String equipo2Seleccionado = (String) equiposComboBoxLigaUnica2.getSelectedItem();

                    // Lógica para organizar partido entre los dos equipos de la misma liga seleccionada
                    // Implementa aquí tu lógica para organizar el partido entre los equipos seleccionados de la misma liga
                }
            });

// Crear paneles para los JComboBox de los equipos
            JPanel equiposPanel = new JPanel(new GridLayout(1, 2));
            equiposPanel.add(equiposComboBoxLigaUnica1);
            equiposPanel.add(equiposComboBoxLigaUnica2);

            panelOrganizarPartidoLigaUnica.add(ligaUnicaComboBox, BorderLayout.NORTH);
            panelOrganizarPartidoLigaUnica.add(equiposPanel, BorderLayout.CENTER);
            panelOrganizarPartidoLigaUnica.add(organizarPartidoButtonLigaUnica, BorderLayout.SOUTH);



            add(panelAsignarJugadores);
            add(panelOrganizarPartidoJugadores);
            add(panelOrganizarPartidoLigas);
            add(panelOrganizarPartidoLigaUnica);

        }

        // Métodos para cargar datos en ComboBox

        private void cargarEquiposEnComboBox(JComboBox<String> comboBox) {
            try {
                conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/Gaming_Leagues", "developer", "23100132");
                stmt = conn.createStatement();
                rs = stmt.executeQuery("SELECT team_name FROM teams");

                while (rs.next()) {
                    comboBox.addItem(rs.getString("team_name"));
                }

                conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error al cargar equipos desde la base de datos");
            }
        }

        private void cargarJugadoresEnComboBox(JComboBox<String> comboBox) {
            try {
                conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/Gaming_Leagues", "developer", "23100132");
                stmt = conn.createStatement();
                rs = stmt.executeQuery("SELECT first_name, last_name FROM players");

                while (rs.next()) {
                    comboBox.addItem(rs.getString("first_name") + " " + rs.getString("last_name"));
                }

                conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error al cargar jugadores desde la base de datos");
            }
        }

        private void cargarLigasEnComboBox(JComboBox<String> comboBox) {
            try {
                conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/Gaming_Leagues", "developer", "23100132");
                stmt = conn.createStatement();
                rs = stmt.executeQuery("SELECT league_name FROM leagues");

                while (rs.next()) {
                    comboBox.addItem(rs.getString("league_name"));
                }

                conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error al cargar las ligas desde la base de datos");
            }
        }

        // Lógica para asignar jugador seleccionado al equipo seleccionado
        private void asignarJugadorAEquipo(String equipoSeleccionado, String jugadorSeleccionado) {
            try {
                conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/Gaming_Leagues", "developer", "23100132");
                PreparedStatement preparedStmt = conn.prepareStatement("INSERT INTO Team_Players (team_id, player_id, date_from) VALUES (?, ?, CURRENT_DATE)");
                preparedStmt.setInt(1, obtenerIdEquipoPorNombre(equipoSeleccionado));
                preparedStmt.setInt(2, obtenerIdJugadorPorNombre(jugadorSeleccionado));
                preparedStmt.executeUpdate();
                conn.close();
                JOptionPane.showMessageDialog(null, "Asignación exitosa");
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error al asignar jugador al equipo");
            }
        }

        private void organizarPartidoEntreJugadores(String jugador1Seleccionado, String jugador2Seleccionado) {
            try {
                conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/Gaming_Leagues", "developer", "23100132");
                PreparedStatement preparedStmt = conn.prepareStatement("INSERT INTO Matches (game_code, player_1_id, player_2_id, match_date) VALUES (?, ?, ?, CURRENT_DATE)");
                preparedStmt.setInt(1, obtenerCodigoJuegoPorNombre("NombreDelJuego")); // Reemplaza "NombreDelJuego" por el nombre del juego
                preparedStmt.setInt(2, obtenerIdJugadorPorNombre(jugador1Seleccionado));
                preparedStmt.setInt(3, obtenerIdJugadorPorNombre(jugador2Seleccionado));
                preparedStmt.executeUpdate();
                conn.close();
                JOptionPane.showMessageDialog(null, "Partido entre jugadores organizado exitosamente");
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error al organizar el partido entre jugadores");
            }
        }

        private void organizarPartidoEntreLigas(String ligaSeleccionada) {
            try {
                List<String> equiposLiga = obtenerEquiposDeLiga(ligaSeleccionada);

                if (equiposLiga.size() < 2) {
                    JOptionPane.showMessageDialog(null, "No hay suficientes equipos en la liga seleccionada para organizar un partido");
                    return;
                }

                Random random = new Random();
                int indiceEquipo1 = random.nextInt(equiposLiga.size());
                int indiceEquipo2 = random.nextInt(equiposLiga.size());

                while (indiceEquipo2 == indiceEquipo1) {
                    indiceEquipo2 = random.nextInt(equiposLiga.size());
                }

                String equipo1Seleccionado = equiposLiga.get(indiceEquipo1);
                String equipo2Seleccionado = equiposLiga.get(indiceEquipo2);

                organizarPartidoEntreEquiposDeLiga(ligaSeleccionada, equipo1Seleccionado, equipo2Seleccionado);
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error al organizar el partido entre ligas");
            }
        }

        private void organizarPartidoEntreEquiposDeLiga(String ligaSeleccionada, String equipo1Seleccionado, String equipo2Seleccionado) {
            try {
                conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/Gaming_Leagues", "developer", "23100132");
                PreparedStatement preparedStmt = conn.prepareStatement("INSERT INTO Matches (game_code, player_1_id, player_2_id, match_date) VALUES (?, ?, ?, CURRENT_DATE)");
                preparedStmt.setInt(1, obtenerCodigoJuegoPorNombre("NombreDelJuego")); // Reemplaza "NombreDelJuego" por el nombre del juego
                preparedStmt.setInt(2, obtenerIdEquipoPorNombre(equipo1Seleccionado));
                preparedStmt.setInt(3, obtenerIdEquipoPorNombre(equipo2Seleccionado));
                preparedStmt.executeUpdate();
                conn.close();
                JOptionPane.showMessageDialog(null, "Partido entre equipos de la misma liga organizado exitosamente");
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error al organizar el partido entre equipos de la misma liga");
            }
        }

        // Método para obtener el código del juego por su nombre
        private int obtenerCodigoJuegoPorNombre(String nombreJuego) throws SQLException {
            int codigoJuego = -1;
            conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/Gaming_Leagues", "developer", "23100132");
            PreparedStatement preparedStmt = conn.prepareStatement("SELECT game_code FROM Games WHERE game_name = ?");
            preparedStmt.setString(1, nombreJuego);
            ResultSet rs = preparedStmt.executeQuery();
            if (rs.next()) {
                codigoJuego = rs.getInt("game_code");
            }
            conn.close();
            return codigoJuego;
        }

        // Método para obtener los equipos de una liga por su nombre
        private List<String> obtenerEquiposDeLiga() throws SQLException {
            return obtenerEquiposDeLiga(null);
        }

        // Método para obtener los equipos de una liga por su nombre
        private List<String> obtenerEquiposDeLiga(String nombreLiga) throws SQLException {
            List<String> equiposLiga = new ArrayList<>();
            conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/Gaming_Leagues", "developer", "23100132");
            PreparedStatement preparedStmt = conn.prepareStatement("SELECT team_name FROM Teams WHERE team_id IN (SELECT team_id FROM Teams_Ligas WHERE league_id = (SELECT league_id FROM Leagues WHERE league_name = ?))");
            preparedStmt.setString(1, nombreLiga);
            ResultSet rs = preparedStmt.executeQuery();
            while (rs.next()) {
                equiposLiga.add(rs.getString("team_name"));
            }
            conn.close();
            return equiposLiga;
        }

        // Obtener ID de equipo por nombre
        private int obtenerIdEquipoPorNombre(String equipoSeleccionado) {
            int teamId = -1;
            try {
                conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/Gaming_Leagues", "developer", "23100132");
                PreparedStatement preparedStmt = conn.prepareStatement("SELECT team_id FROM Teams WHERE team_name = ?");
                preparedStmt.setString(1, equipoSeleccionado);
                ResultSet rs = preparedStmt.executeQuery();
                if (rs.next()) {
                    teamId = rs.getInt("team_id");
                }
                conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return teamId;
        }

        // Obtener ID de jugador por nombre
        private int obtenerIdJugadorPorNombre(String jugadorSeleccionado) {
            int playerId = -1;
            try {
                conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/Gaming_Leagues", "developer", "23100132");
                PreparedStatement preparedStmt = conn.prepareStatement("SELECT player_id FROM Players WHERE first_name || ' ' || last_name = ?");
                preparedStmt.setString(1, jugadorSeleccionado);
                ResultSet rs = preparedStmt.executeQuery();
                if (rs.next()) {
                    playerId = rs.getInt("player_id");
                }
                conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return playerId;
        }
    }


        // Clase para crear el panel de reportes
        static class PanelReportes extends JPanel {
            public PanelReportes() {
                setLayout(new GridLayout(3, 1));

                // Reporte: Jugadores más destacados en cada juego
                JButton jugadoresDestacadosBtn = new JButton("Reporte: Jugadores Destacados en Cada Juego");
                jugadoresDestacadosBtn.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        generarReporteJugadoresDestacados();
                    }
                });

                // Reporte: Rendimiento de equipos en las ligas
                JButton rendimientoEquiposBtn = new JButton("Reporte: Rendimiento de Equipos en las Ligas");
                rendimientoEquiposBtn.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        generarReporteRendimientoEquipos();
                    }
                });

                // Reporte: Historial de partidos de cada jugador
                JButton historialPartidosBtn = new JButton("Reporte: Historial de Partidos de Cada Jugador");
                historialPartidosBtn.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        generarReporteHistorialPartidos();
                    }
                });

                add(jugadoresDestacadosBtn);
                add(rendimientoEquiposBtn);
                add(historialPartidosBtn);
            }

            // Método para generar el reporte de jugadores más destacados en cada juego
            private void generarReporteJugadoresDestacados() {
                try {
                    // Establecer conexión con la base de datos
                    Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/Gaming_Leagues", "developer", "23100132");

                    // Consultar la base de datos para obtener los jugadores más destacados en cada juego
                    // Ejecutar la consulta y obtener los resultados
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery("SELECT game_name, player_name FROM player_stats ORDER BY score DESC");

                    // Procesar los resultados y generar el reporte
                    while (rs.next()) {
                        String juego = rs.getString("game_name");
                        String jugador = rs.getString("player_name");

                        // Aquí puedes imprimir o almacenar los resultados como desees
                        System.out.println("Juego: " + juego + ", Jugador destacado: " + jugador);
                    }

                    // Cerrar la conexión con la base de datos
                    conn.close();
                } catch (SQLException ex) {
                    // Manejar excepciones
                    ex.printStackTrace();
                }
            }

            // Método para generar el reporte de rendimiento de equipos en las ligas
            private void generarReporteRendimientoEquipos() {
                try {
                    // Establecer conexión con la base de datos
                    Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/Gaming_Leagues", "developer", "23100132");

                    // Consultar la base de datos para obtener el rendimiento de equipos en las ligas
                    // Ejecutar la consulta y obtener los resultados
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery("SELECT league_name, team_name, wins, losses FROM team_performance");

                    // Procesar los resultados y generar el reporte
                    while (rs.next()) {
                        String liga = rs.getString("league_name");
                        String equipo = rs.getString("team_name");
                        int victorias = rs.getInt("wins");
                        int derrotas = rs.getInt("losses");

                        // Aquí puedes imprimir o almacenar los resultados como desees
                        System.out.println("Liga: " + liga + ", Equipo: " + equipo + ", Victorias: " + victorias + ", Derrotas: " + derrotas);
                    }

                    // Cerrar la conexión con la base de datos
                    conn.close();
                } catch (SQLException ex) {
                    // Manejar excepciones
                    ex.printStackTrace();
                }
            }

            // Método para generar el reporte de historial de partidos de cada jugador
            private void generarReporteHistorialPartidos() {
                try {
                    // Establecer conexión con la base de datos
                    Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/Gaming_Leagues", "developer", "23100132");

                    // Consultar la base de datos para obtener el historial de partidos de cada jugador
                    // Ejecutar la consulta y obtener los resultados
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery("SELECT player_name, match_date, opponent FROM player_matches ORDER BY match_date DESC");

                    // Procesar los resultados y generar el reporte
                    while (rs.next()) {
                        String jugador = rs.getString("player_name");
                        Date fecha = rs.getDate("match_date");
                        String oponente = rs.getString("opponent");

                        // Aquí puedes imprimir o almacenar los resultados como desees
                        System.out.println("Jugador: " + jugador + ", Fecha del partido: " + fecha + ", Oponente: " + oponente);
                    }

                    // Cerrar la conexión con la base de datos
                    conn.close();
                } catch (SQLException ex) {
                    // Manejar excepciones
                    ex.printStackTrace();
                }
            }

        }
}
