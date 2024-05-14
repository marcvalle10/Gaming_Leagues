import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.text.ParseException;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.io.*;

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
        tabbedPane.addTab("Partidos/Equipos", new PanelPartidosEquipos());
        tabbedPane.addTab("Jugadores/Equipos", new PanelJE());
        tabbedPane.addTab("Ligas/Equipos", new PanelLE());
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

    // Clase para crear el panel de partidos entre jugadores
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

    //Clase para crear el panel de partidos entre equipos
    static class PanelPartidosEquipos extends JPanel {
        private JTable partidosTable;
        private DefaultTableModel tableModel;
        private Connection connection;

        public PanelPartidosEquipos() {
            setLayout(new BorderLayout());

            // Crear tabla para mostrar partidos
            tableModel = new DefaultTableModel();
            tableModel.addColumn("ID");
            tableModel.addColumn("Liga");
            tableModel.addColumn("Equipo 1");
            tableModel.addColumn("Equipo 2");
            tableModel.addColumn("Fecha");
            tableModel.addColumn("Código de Juego");

            partidosTable = new JTable(tableModel);
            JScrollPane scrollPane = new JScrollPane(partidosTable);
            add(scrollPane, BorderLayout.CENTER);

            // Panel para agregar nuevos partidos
            JPanel panelAgregar = new JPanel(new GridLayout(3, 2));
            panelAgregar.setBorder(BorderFactory.createTitledBorder("Agregar Partido"));

            JTextField ligaField = new JTextField();
            JTextField equipo1Field = new JTextField();
            JTextField equipo2Field = new JTextField();
            JTextField fechaField = new JTextField();
            JTextField codigoJuegoField = new JTextField();

            panelAgregar.add(new JLabel("Liga:"));
            panelAgregar.add(ligaField);
            panelAgregar.add(new JLabel("Equipo 1:"));
            panelAgregar.add(equipo1Field);
            panelAgregar.add(new JLabel("Equipo 2:"));
            panelAgregar.add(equipo2Field);
            panelAgregar.add(new JLabel("Fecha:"));
            panelAgregar.add(fechaField);
            panelAgregar.add(new JLabel("Código de Juego:"));
            panelAgregar.add(codigoJuegoField);

            JButton agregarButton = new JButton("Agregar");
            agregarButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String liga = ligaField.getText();
                    String equipo1 = equipo1Field.getText();
                    String equipo2 = equipo2Field.getText();
                    String fecha = fechaField.getText();
                    int codigoJuego = Integer.parseInt(codigoJuegoField.getText());

                    agregarPartido(liga, equipo1, equipo2, fecha, codigoJuego);
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
                ResultSet resultSet = statement.executeQuery("SELECT * FROM Matches_Teams");
                while (resultSet.next()) {
                    int id = resultSet.getInt("match_id");
                    String liga = obtenerNombreLiga(resultSet.getInt("league_id"));
                    String equipo1 = obtenerNombreEquipo(resultSet.getInt("team1_id"));
                    String equipo2 = obtenerNombreEquipo(resultSet.getInt("team2_id"));
                    String fecha = resultSet.getString("match_date");
                    int codigoJuego = resultSet.getInt("game_code");

                    tableModel.addRow(new Object[]{id, liga, equipo1, equipo2, fecha, codigoJuego});
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al cargar partidos desde la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }

        // Método para agregar un nuevo partido a la base de datos y actualizar la tabla
        private void agregarPartido(String liga, String equipo1, String equipo2, String fecha, int codigoJuego) {
            try {
                int idLiga = obtenerIdLiga(liga);
                int idEquipo1 = obtenerIdEquipo(equipo1);
                int idEquipo2 = obtenerIdEquipo(equipo2);

                PreparedStatement preparedStatement = connection.prepareStatement(
                        "INSERT INTO Matches_Teams (league_id, team1_id, team2_id, match_date, game_code) VALUES (?, ?, ?, ?, ?)"
                );
                preparedStatement.setInt(1, idLiga);
                preparedStatement.setInt(2, idEquipo1);
                preparedStatement.setInt(3, idEquipo2);
                preparedStatement.setString(4, fecha);
                preparedStatement.setInt(5, codigoJuego);

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

        // Método para obtener el nombre de la liga a partir de su ID
        private String obtenerNombreLiga(int idLiga) {
            String nombreLiga = "";
            try {
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT league_name FROM Leagues WHERE league_id = ?");
                preparedStatement.setInt(1, idLiga);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    nombreLiga = resultSet.getString("league_name");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return nombreLiga;
        }

        // Método para obtener el nombre de un equipo a partir de su ID
        private String obtenerNombreEquipo(int idEquipo) {
            String nombreEquipo = "";
            try {
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT team_name FROM Teams WHERE team_id = ?");
                preparedStatement.setInt(1, idEquipo);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    nombreEquipo = resultSet.getString("team_name");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return nombreEquipo;
        }

        // Método para obtener el ID de una liga a partir de su nombre
        private int obtenerIdLiga(String nombreLiga) {
            int idLiga = -1;
            try {
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT league_id FROM Leagues WHERE league_name = ?");
                preparedStatement.setString(1, nombreLiga);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    idLiga = resultSet.getInt("league_id");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return idLiga;
        }

        // Método para obtener el ID de un equipo a partir de su nombre
        private int obtenerIdEquipo(String nombreEquipo) {
            int idEquipo = -1;
            try {
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT team_id FROM Teams WHERE team_name = ?");
                preparedStatement.setString(1, nombreEquipo);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    idEquipo = resultSet.getInt("team_id");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return idEquipo;
        }
    }

    // Clase para crear el panel de relación entre Jugadores y Equipos
    static class PanelJE extends JPanel {
        private JTable JETable;
        private DefaultTableModel tableModel;
        private Connection connection;

        public PanelJE() {
            setLayout(new BorderLayout());

            // Crear tabla para mostrar relación entre Jugadores y Equipos
            tableModel = new DefaultTableModel();
            tableModel.addColumn("ID_Equipo");
            tableModel.addColumn("ID_Jugador");
            tableModel.addColumn("Fecha_Desde");
            tableModel.addColumn("Fecha_Hasta");

            JETable = new JTable(tableModel);
            JScrollPane scrollPane = new JScrollPane(JETable);
            add(scrollPane, BorderLayout.CENTER);

            // Panel para asignar jugadores a equipos
            JPanel panelAsignar = new JPanel(new GridLayout(2, 2));
            panelAsignar.setBorder(BorderFactory.createTitledBorder("Asignar Jugador a Equipo"));

            JTextField ID_JugadorField = new JTextField();
            JTextField ID_EquipoField = new JTextField();
            JTextField Fecha_AsignacionField = new JTextField();

            panelAsignar.add(new JLabel("ID_Equipo:"));
            panelAsignar.add(ID_EquipoField);
            panelAsignar.add(new JLabel("ID_Jugador:"));
            panelAsignar.add(ID_JugadorField);
            panelAsignar.add(new JLabel("Fecha_Asignacion:"));
            panelAsignar.add(Fecha_AsignacionField);

            JButton asignarButton = new JButton("Asignar");
            asignarButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String teamID = ID_JugadorField.getText();
                    String playerID = ID_EquipoField.getText();
                    String dateFrom = Fecha_AsignacionField.getText();

                    agregarJugador(teamID, playerID, dateFrom);
                }
            });
            panelAsignar.add(asignarButton);

            add(panelAsignar, BorderLayout.SOUTH);

            // Establecer conexión a la base de datos
            ConexionBD.conectar();
            connection = ConexionBD.getConexion();
            if (connection != null) {
                cargarJugadoresEquipos();
            } else {
                JOptionPane.showMessageDialog(this, "Error al conectar a la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        // Método para cargar la relación entre Jugadores y Equipos desde la base de datos a la tabla
        private void cargarJugadoresEquipos() {
            try {
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT * FROM Team_Players");
                while (resultSet.next()) {
                    int teamID = resultSet.getInt("team_id");
                    int playerID = resultSet.getInt("player_id");
                    Date dateFrom = resultSet.getDate("date_from");
                    Date dateTo = resultSet.getDate("date_to");

                    tableModel.addRow(new Object[]{teamID, playerID, dateFrom, dateTo});
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al cargar jugadores y equipos desde la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }

        // Método para asignar un jugador a un equipo en la base de datos y actualizar la tabla
        private void agregarJugador(String teamID, String playerID, String dateFrom) {
            try {
                int teamId = Integer.parseInt(teamID);
                int playerId = Integer.parseInt(playerID);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                java.util.Date parsedDateFrom = sdf.parse(dateFrom);
                java.sql.Date assignedDateFrom = new java.sql.Date(parsedDateFrom.getTime());


                PreparedStatement preparedStatement = connection.prepareStatement(
                        "INSERT INTO Team_Players (team_id, player_id, date_from, date_to) VALUES (?, ?, ?, null)"
                );
                preparedStatement.setInt(1, teamId);
                preparedStatement.setInt(2, playerId);
                preparedStatement.setDate(3, assignedDateFrom);


                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Jugador asignado a un equipo exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    tableModel.setRowCount(0); // Limpiar la tabla antes de cargar los datos actualizados
                    cargarJugadoresEquipos(); // Volver a cargar los datos desde la base de datos
                } else {
                    JOptionPane.showMessageDialog(this, "Error al asignar el jugador.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Formato de fecha incorrecto. Utilice yyyy-MM-dd.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al asignar el jugador.", "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }

    }

    // Clase para crear el panel de relación entre Ligas y Equipos
    static class PanelLE extends JPanel {
        private JTable LETable;
        private DefaultTableModel tableModel;
        private Connection connection;

        public PanelLE() {
            setLayout(new BorderLayout());

            // Crear tabla para mostrar relación entre Ligas y Equipos
            tableModel = new DefaultTableModel();
            tableModel.addColumn("ID_Liga");
            tableModel.addColumn("ID_Equipo");

            LETable = new JTable(tableModel);
            JScrollPane scrollPane = new JScrollPane(LETable);
            add(scrollPane, BorderLayout.CENTER);

            // Panel para establecer relación entre ligas y equipos
            JPanel panelRelacion = new JPanel(new GridLayout(2, 2));
            panelRelacion.setBorder(BorderFactory.createTitledBorder("Establecer Relación entre Liga y Equipo"));

            JTextField ID_LigaField = new JTextField();
            JTextField ID_EquipoField = new JTextField();

            panelRelacion.add(new JLabel("ID_Liga:"));
            panelRelacion.add(ID_LigaField);
            panelRelacion.add(new JLabel("ID_Equipo:"));
            panelRelacion.add(ID_EquipoField);

            JButton relacionButton = new JButton("Establecer Relación");
            relacionButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String ID_Liga = ID_LigaField.getText();
                    String ID_Equipo = ID_EquipoField.getText();

                    establecerRelacionLigaEquipo(ID_Liga, ID_Equipo);
                }
            });
            panelRelacion.add(relacionButton);

            add(panelRelacion, BorderLayout.SOUTH);

            // Establecer conexión a la base de datos
            ConexionBD.conectar();
            connection = ConexionBD.getConexion();
            if (connection != null) {
                cargarLigasEquipos();
            } else {
                JOptionPane.showMessageDialog(this, "Error al conectar a la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        // Método para cargar la relación entre Ligas y Equipos desde la base de datos a la tabla
        private void cargarLigasEquipos() {
            try {
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT * FROM Leagues_Teams");
                while (resultSet.next()) {
                    int ID_Liga = resultSet.getInt("league_id");
                    int ID_Equipo = resultSet.getInt("team_id");

                    tableModel.addRow(new Object[]{ID_Liga, ID_Equipo});
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al cargar la relación entre ligas y equipos desde la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }

        // Método para establecer la relación entre una liga y un equipo en la base de datos y actualizar la tabla
        private void establecerRelacionLigaEquipo(String ID_Liga, String ID_Equipo) {
            try {
                int leagueID = Integer.parseInt(ID_Liga);
                int teamID = Integer.parseInt(ID_Equipo);

                PreparedStatement preparedStatement = connection.prepareStatement(
                        "INSERT INTO Leagues_Teams (league_id, team_id) VALUES (?, ?)"
                );
                preparedStatement.setInt(1, leagueID);
                preparedStatement.setInt(2, teamID);

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Relación establecida exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    tableModel.setRowCount(0); // Limpiar la tabla antes de cargar los datos actualizados
                    cargarLigasEquipos(); // Volver a cargar los datos desde la base de datos
                } else {
                    JOptionPane.showMessageDialog(this, "Error al establecer la relación.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "ID de liga y equipo deben ser números enteros.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al establecer la relación.", "Error", JOptionPane.ERROR_MESSAGE);
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

                    asignarJugadorAEquipo(equipoSeleccionado, jugadorSeleccionado);
                }
            });

            panelAsignarJugadores.add(equiposComboBox, BorderLayout.NORTH);
            panelAsignarJugadores.add(jugadoresComboBox, BorderLayout.CENTER);
            panelAsignarJugadores.add(asignarButton, BorderLayout.SOUTH);

            // Panel para organizar partidos entre dos jugadores
            JPanel panelOrganizarPartidoJugadores = new JPanel(new BorderLayout());
            panelOrganizarPartidoJugadores.setBorder(BorderFactory.createTitledBorder("Organizar Partido entre Jugadores"));

            // Crear el recuadro de texto para ingresar el código de juego
            JTextField codigoJuegoTextField = new JTextField();
            codigoJuegoTextField.setPreferredSize(new Dimension(200, 25)); // Ajustar el tamaño según sea necesario

            // Panel para los JComboBox de jugadores
            JPanel jugadoresPanel = new JPanel(new GridLayout(2, 1));

            JComboBox<String> jugadoresComboBox1 = new JComboBox<>();
            JComboBox<String> jugadoresComboBox2 = new JComboBox<>();

            // Cargar los jugadores en los ComboBox
            cargarJugadoresEnComboBox(jugadoresComboBox1);
            cargarJugadoresEnComboBox(jugadoresComboBox2);

            jugadoresPanel.add(jugadoresComboBox1);
            jugadoresPanel.add(jugadoresComboBox2);

            // Botón para organizar el partido
            JButton organizarPartidoButton = new JButton("Organizar Partido");
            organizarPartidoButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String jugador1Seleccionado = (String) jugadoresComboBox1.getSelectedItem();
                    String jugador2Seleccionado = (String) jugadoresComboBox2.getSelectedItem();
                    String codigoJuego = codigoJuegoTextField.getText(); // Obtener el código de juego ingresado

                    organizarPartidoEntreJugadores(jugador1Seleccionado, jugador2Seleccionado, codigoJuego);
                }
            });

            // Panel para organizar los componentes
            JPanel formPanel = new JPanel(new BorderLayout());

            JPanel codigoJuegoPanel = new JPanel(new GridLayout(1, 2)); // Panel para el texto "Código de Juego"
            codigoJuegoPanel.add(new JLabel("Código de Juego:"));
            codigoJuegoPanel.add(codigoJuegoTextField);

            formPanel.add(codigoJuegoPanel, BorderLayout.NORTH); // Agregar el campo de texto del código de juego
            formPanel.add(jugadoresPanel, BorderLayout.CENTER); // Agregar el panel de jugadores

            // Agregar el formulario y el botón al panel principal
            panelOrganizarPartidoJugadores.add(formPanel, BorderLayout.CENTER);
            panelOrganizarPartidoJugadores.add(organizarPartidoButton, BorderLayout.SOUTH);


            // Panel para organizar partidos entre ligas
            JPanel panelOrganizarPartidoLigas = new JPanel(new BorderLayout());
            panelOrganizarPartidoLigas.setBorder(BorderFactory.createTitledBorder("Organizar Partido entre Ligas"));

            // ComboBox y ActionListener para la primera liga
            JComboBox<String> ligasComboBox1 = new JComboBox<>();
            JComboBox<String> equiposComboBoxLiga1 = new JComboBox<>();
            cargarLigasEnComboBox(ligasComboBox1);

            ligasComboBox1.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String ligaSeleccionada = (String) ligasComboBox1.getSelectedItem();
                    cargarEquiposDeLigaEnComboBox(ligaSeleccionada, equiposComboBoxLiga1);
                }
            });

            // ComboBox y ActionListener para la segunda liga
            JComboBox<String> ligasComboBox2 = new JComboBox<>();
            JComboBox<String> equiposComboBoxLiga2 = new JComboBox<>();
            cargarLigasEnComboBox(ligasComboBox2);

            ligasComboBox2.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String ligaSeleccionada = (String) ligasComboBox2.getSelectedItem();
                    cargarEquiposDeLigaEnComboBox(ligaSeleccionada, equiposComboBoxLiga2);
                }
            });

            // Campo de texto para ingresar el código del juego
            JTextField codigoJuegoTextFieldLigas = new JTextField();
            codigoJuegoTextFieldLigas.setPreferredSize(new Dimension(200, 25)); // Ajustar el tamaño según sea necesario

            // Botón para organizar el partido entre las ligas seleccionadas
            JButton organizarPartidoButtonLigas = new JButton("Organizar Partido");
            organizarPartidoButtonLigas.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String liga1Seleccionada = (String) ligasComboBox1.getSelectedItem();
                    String equipo1Seleccionado = (String) equiposComboBoxLiga1.getSelectedItem();

                    String liga2Seleccionada = (String) ligasComboBox2.getSelectedItem();
                    String equipo2Seleccionado = (String) equiposComboBoxLiga2.getSelectedItem();

                    String codigoJuego = codigoJuegoTextFieldLigas.getText(); // Obtener el código de juego ingresado

                    // Lógica para organizar el partido entre equipos de las ligas seleccionadas
                    panelOrganizarPartidoLigas(liga1Seleccionada, equipo1Seleccionado, liga2Seleccionada, equipo2Seleccionado, codigoJuego);
                }
            });

            // Panel para mostrar y organizar los componentes
            JPanel ligasPanel = new JPanel(new GridLayout(3, 2)); // GridLayout con tres filas y dos columnas
            ligasPanel.add(new JLabel("Liga 1:")); // Etiqueta para identificar el ComboBox de la primera liga
            ligasPanel.add(ligasComboBox1);
            ligasPanel.add(new JLabel("Liga 2:")); // Etiqueta para identificar el ComboBox de la segunda liga
            ligasPanel.add(ligasComboBox2);
            ligasPanel.add(new JLabel("Código del Juego:")); // Etiqueta para identificar el campo de texto del código de juego
            ligasPanel.add(codigoJuegoTextFieldLigas);

            // Agregar los componentes al panel principal
            panelOrganizarPartidoLigas.add(ligasPanel, BorderLayout.NORTH);
            panelOrganizarPartidoLigas.add(equiposComboBoxLiga1, BorderLayout.WEST);
            panelOrganizarPartidoLigas.add(equiposComboBoxLiga2, BorderLayout.EAST);
            panelOrganizarPartidoLigas.add(organizarPartidoButtonLigas, BorderLayout.SOUTH);


// Panel para organizar partidos entre equipos de una misma liga
            JPanel panelOrganizarPartidoLigaUnica = new JPanel(new BorderLayout());
            panelOrganizarPartidoLigaUnica.setBorder(BorderFactory.createTitledBorder("Organizar Partido entre Equipos de una Liga"));

            // ComboBox y ActionListener para seleccionar la liga
            JComboBox<String> ligaUnicaComboBox = new JComboBox<>();
            JComboBox<String> equiposComboBoxLigaUnica1 = new JComboBox<>();
            JComboBox<String> equiposComboBoxLigaUnica2 = new JComboBox<>();

            // Cargar las ligas y equipos en los ComboBox
            cargarLigasEnComboBox(ligaUnicaComboBox);
            cargarEquiposEnComboBox(equiposComboBoxLigaUnica1);
            cargarEquiposEnComboBox(equiposComboBoxLigaUnica2);

            // Campo de texto para ingresar el código del juego
            JTextField codigoJuegoTextFieldLigaUnica = new JTextField();

            // Botón para organizar el partido
            JButton organizarPartidoButtonLigaUnica = new JButton("Organizar Partido");
            organizarPartidoButtonLigaUnica.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String ligaSeleccionada = (String) ligaUnicaComboBox.getSelectedItem();
                    String equipo1Seleccionado = (String) equiposComboBoxLigaUnica1.getSelectedItem();
                    String equipo2Seleccionado = (String) equiposComboBoxLigaUnica2.getSelectedItem();

                    String codigoJuego = codigoJuegoTextFieldLigaUnica.getText(); // Obtener el código de juego ingresado

                    organizarPartidoEntreEquiposDeLiga(ligaSeleccionada, equipo1Seleccionado, equipo2Seleccionado, codigoJuego);
                }
            });

            // Panel para organizar los componentes
            formPanel = new JPanel(new BorderLayout());
            JPanel equiposPanel = new JPanel(new GridLayout(3, 1));
            equiposPanel.add(new JLabel("Liga:")); // Etiqueta para identificar el ComboBox de ligas
            equiposPanel.add(ligaUnicaComboBox);
            equiposPanel.add(new JLabel("Equipos:")); // Etiqueta para identificar los ComboBox de equipos

            // Panel para los ComboBox de equipos
            JPanel equiposComboBoxPanel = new JPanel(new GridLayout(1, 2));
            equiposComboBoxPanel.add(equiposComboBoxLigaUnica1);
            equiposComboBoxPanel.add(equiposComboBoxLigaUnica2);

            // Panel para el campo de texto del código del juego
            codigoJuegoPanel = new JPanel(new GridLayout(1, 2));
            codigoJuegoPanel.add(new JLabel("Código del Juego:"));
            codigoJuegoPanel.add(codigoJuegoTextFieldLigaUnica);

            // Añadir los paneles al formulario
            formPanel.add(equiposPanel, BorderLayout.NORTH);
            formPanel.add(equiposComboBoxPanel, BorderLayout.CENTER);
            formPanel.add(codigoJuegoPanel, BorderLayout.SOUTH);

            // Agregar el formulario y el botón al panel principal
            panelOrganizarPartidoLigaUnica.add(formPanel, BorderLayout.CENTER);
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

        private void cargarEquiposDeLigaEnComboBox(String nombreLiga, JComboBox<String> comboBox) {
            try {
                conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/Gaming_Leagues", "developer", "23100132");
                PreparedStatement preparedStmt = conn.prepareStatement("SELECT team_name FROM teams WHERE team_id IN (SELECT team_id FROM leagues_teams WHERE league_id = (SELECT league_id FROM leagues WHERE league_name = ?))");
                preparedStmt.setString(1, nombreLiga);
                ResultSet rs = preparedStmt.executeQuery();
                while (rs.next()) {
                    comboBox.addItem(rs.getString("team_name"));
                }
                conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error al cargar equipos de la liga desde la base de datos");
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

        // Método para organizar partido entre jugadores
        private void organizarPartidoEntreJugadores(String jugador1Seleccionado, String jugador2Seleccionado, String codigoJuego) {
            try {
                conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/Gaming_Leagues", "developer", "23100132");
                int gameCode = Integer.parseInt(codigoJuego); // Convertir el código de juego a entero
                PreparedStatement preparedStmt = conn.prepareStatement(
                        "INSERT INTO Matches (game_code, player_1_id, player_2_id, match_date) VALUES (?, ?, ?, CURRENT_DATE)");
                preparedStmt.setInt(1, gameCode);
                preparedStmt.setInt(2, obtenerIdJugadorPorNombre(jugador1Seleccionado));
                preparedStmt.setInt(3, obtenerIdJugadorPorNombre(jugador2Seleccionado));
                preparedStmt.executeUpdate();
                conn.close();
                JOptionPane.showMessageDialog(null, "Partido entre jugadores organizado exitosamente");
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error al organizar el partido entre jugadores", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "El código de juego debe ser un número entero válido", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        //Metodos partidas entre ligas
        private void panelOrganizarPartidoLigas(String liga1Seleccionada, String equipo1Seleccionado, String liga2Seleccionada, String equipo2Seleccionado, String codigoJuego) {
            try {
                // Establecer la conexión con la base de datos
                conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/Gaming_Leagues", "developer", "23100132");

                // Obtener la lista de equipos de ambas ligas
                List<String> equiposLiga1 = obtenerEquiposDeLiga(liga1Seleccionada);
                List<String> equiposLiga2 = obtenerEquiposDeLiga(liga2Seleccionada);

                // Verificar que hay al menos un equipo en cada liga
                if (equiposLiga1.size() < 1 || equiposLiga2.size() < 1) {
                    JOptionPane.showMessageDialog(null, "No hay suficientes equipos en alguna de las ligas seleccionadas para organizar un partido");
                    return;
                }

                // Seleccionar aleatoriamente un equipo de cada liga
                Random random = new Random();
                int indiceEquipo1 = random.nextInt(equiposLiga1.size());
                int indiceEquipo2 = random.nextInt(equiposLiga2.size());

                equipo1Seleccionado = equiposLiga1.get(indiceEquipo1);
                equipo2Seleccionado = equiposLiga2.get(indiceEquipo2);

                // Insertar el partido en la base de datos
                insertarPartidoEnBaseDeDatos(liga1Seleccionada, equipo1Seleccionado, liga2Seleccionada, equipo2Seleccionado, codigoJuego);

                // Cerrar la conexión con la base de datos
                conn.close();

                // Mostrar un mensaje de éxito
                JOptionPane.showMessageDialog(null, "Partido entre equipos de las ligas seleccionadas organizado exitosamente");
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error al organizar el partido entre equipos de las ligas seleccionadas", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        // Método para organizar partido entre equipos de la misma liga
        private void organizarPartidoEntreEquiposDeLiga(String ligaSeleccionada, String equipo1Seleccionado, String equipo2Seleccionado, String codigoJuego) {
            try {
                conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/Gaming_Leagues", "developer", "23100132");
                int gameCode = Integer.parseInt(codigoJuego); // Obtener el código del juego proporcionado
                PreparedStatement preparedStmt = conn.prepareStatement(
                        "INSERT INTO Matches_Teams (game_code, team1_id, team2_id, match_date) VALUES (?, ?, ?, CURRENT_DATE)");
                preparedStmt.setInt(1, gameCode);
                preparedStmt.setInt(2, obtenerIdEquipoPorNombre(equipo1Seleccionado));
                preparedStmt.setInt(3, obtenerIdEquipoPorNombre(equipo2Seleccionado));
                preparedStmt.executeUpdate();
                conn.close();
                JOptionPane.showMessageDialog(null, "Partido entre equipos de la misma liga organizado exitosamente");
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error al organizar el partido entre equipos de la misma liga", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "El código de juego debe ser un número entero válido", "Error", JOptionPane.ERROR_MESSAGE);
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
        private List<String> obtenerEquiposDeLiga(String nombreLiga) throws SQLException {
            List<String> equiposLiga = new ArrayList<>();
            conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/Gaming_Leagues", "developer", "23100132");
            PreparedStatement preparedStmt = conn.prepareStatement("SELECT team_name FROM Teams WHERE team_id IN (SELECT team_id FROM leagues_teams WHERE league_id = (SELECT league_id FROM Leagues WHERE league_name = ?))");
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

        // Método para insertar partido en la base de datos
        private void insertarPartidoEnBaseDeDatos(String liga1Seleccionada, String equipo1Seleccionado, String liga2Seleccionada, String equipo2Seleccionado, String codigoJuego) {
            try {
                conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/Gaming_Leagues", "developer", "23100132");
                PreparedStatement preparedStmt = conn.prepareStatement(
                        "INSERT INTO Matches_Teams (game_code, team1_id, team2_id, match_date) VALUES (?, ?, ?, CURRENT_DATE)");
                preparedStmt.setInt(1, Integer.parseInt(codigoJuego)); // Usar el código de juego proporcionado
                preparedStmt.setInt(2, obtenerIdEquipoPorNombre(equipo1Seleccionado));
                preparedStmt.setInt(3, obtenerIdEquipoPorNombre(equipo2Seleccionado));
                preparedStmt.executeUpdate();
                conn.close();
                JOptionPane.showMessageDialog(null, "Partido entre equipos de las ligas seleccionadas organizado exitosamente");
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error al organizar el partido entre equipos de las ligas seleccionadas", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "El código de juego debe ser un número entero válido", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

    }


    static class PanelReportes extends JPanel {
        private JButton jugadoresDestacadosBtn;
        private JButton rendimientoEquiposBtn;
        private JButton historialPartidosBtn;
        private JButton historialPartidosEquipoBtn;

        public PanelReportes() {
            setLayout(new GridLayout(4, 1));

            // Inicializar los botones
            jugadoresDestacadosBtn = new JButton("Reporte: Jugadores Destacados en Cada Juego");
            rendimientoEquiposBtn = new JButton("Reporte: Rendimiento de Equipos en las Ligas");
            historialPartidosBtn = new JButton("Reporte: Historial de Partidos de Cada Jugador");
            historialPartidosEquipoBtn = new JButton("Reporte: Historial de Partidos de Equipo");

            // Asignar oyentes de acción a los botones
            jugadoresDestacadosBtn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    generarReporteJugadoresDestacados();
                }
            });

            rendimientoEquiposBtn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    generarReporteRendimientoEquipos();
                }
            });

            historialPartidosBtn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    generarReporteHistorialPartidos();
                }
            });

            historialPartidosEquipoBtn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    generarReporteHistorialPartidosEquipo();
                }
            });

            // Agregar los botones al panel
            add(jugadoresDestacadosBtn);
            add(rendimientoEquiposBtn);
            add(historialPartidosBtn);
            add(historialPartidosEquipoBtn);
        }

        // Método para generar el reporte de jugadores más destacados en cada juego
        private void generarReporteJugadoresDestacados() {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("reporte_jugadores_destacados.txt"))) {
                // Establecer conexión con la base de datos
                Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/Gaming_Leagues", "developer", "23100132");

                // Consultar la base de datos para obtener los jugadores más destacados en cada juego
                // Ejecutar la consulta y obtener los resultados
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT g.game_name, p.first_name, p.last_name FROM Players p INNER JOIN Players_Game_Ranking pr ON p.player_id = pr.player_id INNER JOIN Games g ON pr.game_code = g.game_code ORDER BY pr.ranking DESC");

                // Procesar los resultados y escribir en el archivo de texto
                while (rs.next()) {
                    String juego = rs.getString("game_name");
                    String jugador = rs.getString("first_name") + " " + rs.getString("last_name");

                    // Escribir en el archivo
                    writer.write("Juego: " + juego + ", Jugador destacado: " + jugador);
                    writer.newLine();
                }

                // Cerrar la conexión con la base de datos
                conn.close();
            } catch (SQLException | IOException ex) {
                // Manejar excepciones
                ex.printStackTrace();
            }
        }

        // Método para generar el reporte de rendimiento de equipos en las ligas
        private void generarReporteRendimientoEquipos() {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("reporte_rendimiento_equipos.txt"))) {
                // Establecer conexión con la base de datos
                Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/Gaming_Leagues", "developer", "23100132");

                // Consultar la base de datos para obtener el rendimiento de equipos en las ligas
                // Ejecutar la consulta y obtener los resultados
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT l.league_name, t.team_name, COUNT(mt.match_id) AS matches_played, SUM(CASE WHEN mt.result = 'win' THEN 1 ELSE 0 END) AS wins, SUM(CASE WHEN mt.result = 'lose' THEN 1 ELSE 0 END) AS losses FROM Matches_Teams mt INNER JOIN Leagues l ON mt.league_id = l.league_id INNER JOIN Teams t ON mt.team1_id = t.team_id OR mt.team2_id = t.team_id GROUP BY l.league_name, t.team_name");

                // Procesar los resultados y escribir en el archivo de texto
                while (rs.next()) {
                    String liga = rs.getString("league_name");
                    String equipo = rs.getString("team_name");
                    int victorias = rs.getInt("wins");
                    int derrotas = rs.getInt("losses");

                    // Escribir en el archivo
                    writer.write("Liga: " + liga + ", Equipo: " + equipo + ", Victorias: " + victorias + ", Derrotas: " + derrotas);
                    writer.newLine();
                }

                // Cerrar la conexión con la base de datos
                conn.close();
            } catch (SQLException | IOException ex) {
                // Manejar excepciones
                ex.printStackTrace();
            }
        }

        // Método para generar el reporte de historial de partidos de cada jugador
        private void generarReporteHistorialPartidos() {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("reporte_historial_partidos_jugador.txt"))) {
                // Establecer conexión con la base de datos
                Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/Gaming_Leagues", "developer", "23100132");

                // Consultar la base de datos para obtener el historial de partidos de cada jugador
                // Ejecutar la consulta y obtener los resultados
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT m.match_date, CONCAT(p1.first_name, ' ', p1.last_name) AS player1, CONCAT(p2.first_name, ' ', p2.last_name) AS player2, m.result FROM Matches m INNER JOIN Players p1 ON m.player_1_id = p1.player_id INNER JOIN Players p2 ON m.player_2_id = p2.player_id");

                // Procesar los resultados y escribir en el archivo de texto
                while (rs.next()) {
                    Date fecha = rs.getDate("match_date");
                    String jugador1 = rs.getString("player1");
                    String jugador2 = rs.getString("player2");
                    String resultado = rs.getString("result");

                    // Escribir en el archivo
                    writer.write("Fecha del partido: " + fecha + ", Jugador 1: " + jugador1 + ", Jugador 2: " + jugador2 + ", Resultado: " + resultado);
                    writer.newLine();
                }

                // Cerrar la conexión con la base de datos
                conn.close();
            } catch (SQLException | IOException ex) {
                // Manejar excepciones
                ex.printStackTrace();
            }
        }

        // Método para generar el reporte de historial de partidos de equipo
        private void generarReporteHistorialPartidosEquipo() {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("reporte_historial_partidos_equipo.txt"))) {
                // Establecer conexión con la base de datos
                Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/Gaming_Leagues", "developer", "23100132");

                // Consultar la base de datos para obtener el historial de partidos de equipo
                // Ejecutar la consulta y obtener los resultados
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT mt.match_date, t1.team_name AS team1, t2.team_name AS team2, mt.result FROM Matches_Teams mt INNER JOIN Teams t1 ON mt.team1_id = t1.team_id INNER JOIN Teams t2 ON mt.team2_id = t2.team_id");

                // Procesar los resultados y escribir en el archivo de texto
                while (rs.next()) {
                    Date fecha = rs.getDate("match_date");
                    String equipo1 = rs.getString("team1");
                    String equipo2 = rs.getString("team2");
                    String resultado = rs.getString("result");

                    // Escribir en el archivo
                    writer.write("Fecha del partido: " + fecha + ", Equipo 1: " + equipo1 + ", Equipo 2: " + equipo2 + ", Resultado: " + resultado);
                    writer.newLine();
                }

                // Cerrar la conexión con la base de datos
                conn.close();
            } catch (SQLException | IOException ex) {
                // Manejar excepciones
                ex.printStackTrace();
            }
        }
    }
}

