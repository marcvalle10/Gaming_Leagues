import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;


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

            JComboBox<String> ligasComboBox = new JComboBox<>();
            JComboBox<String> equiposComboBoxLiga1 = new JComboBox<>();
            JComboBox<String> equiposComboBoxLiga2 = new JComboBox<>();

            cargarLigasEnComboBox(ligasComboBox); // Método para cargar las ligas en el ComboBox
            cargarEquiposEnComboBox(equiposComboBoxLiga1); // Método para cargar los equipos de la primera liga en el ComboBox
            cargarEquiposEnComboBox(equiposComboBoxLiga2); // Método para cargar los equipos de la segunda liga en el ComboBox

            JButton organizarPartidoButtonLigas = new JButton("Organizar Partido");
            organizarPartidoButtonLigas.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String ligaSeleccionada = (String) ligasComboBox.getSelectedItem();
                    String equipo1Seleccionado = (String) equiposComboBoxLiga1.getSelectedItem();
                    String equipo2Seleccionado = (String) equiposComboBoxLiga2.getSelectedItem();

                    // Lógica para organizar partido entre los dos equipos de las ligas seleccionadas
                    // Implementa aquí tu lógica para organizar el partido entre los equipos seleccionados de las ligas seleccionadas
                }
            });

            panelOrganizarPartidoLigas.add(ligasComboBox, BorderLayout.NORTH);
            panelOrganizarPartidoLigas.add(equiposComboBoxLiga1, BorderLayout.CENTER);
            panelOrganizarPartidoLigas.add(equiposComboBoxLiga2, BorderLayout.CENTER);
            panelOrganizarPartidoLigas.add(organizarPartidoButtonLigas, BorderLayout.SOUTH);

            // Panel para organizar partidos entre equipos de una misma liga
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
                // Lógica para generar el reporte de jugadores más destacados en cada juego
                // Consultar la base de datos y generar el reporte
                // Mostrar el reporte en una ventana o archivo
            }

            // Método para generar el reporte de rendimiento de equipos en las ligas
            private void generarReporteRendimientoEquipos() {
                // Lógica para generar el reporte de rendimiento de equipos en las ligas
                // Consultar la base de datos y generar el reporte
                // Mostrar el reporte en una ventana o archivo
            }

            // Método para generar el reporte de historial de partidos de cada jugador
            private void generarReporteHistorialPartidos() {
                // Lógica para generar el reporte de historial de partidos de cada jugador
                // Consultar la base de datos y generar el reporte
                // Mostrar el reporte en una ventana o archivo
            }
        }


    }


    public class Entidades {
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
                try (Connection conn = AplicacionDeportiva.ConexionBD.getConexion();
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
                try (Connection conn = AplicacionDeportiva.ConexionBD.getConexion();
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
                try (Connection conn = AplicacionDeportiva.ConexionBD.getConexion();
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
                try (Connection conn = AplicacionDeportiva.ConexionBD.getConexion();
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
                    try (Connection conn = AplicacionDeportiva.ConexionBD.getConexion();
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
                    try (Connection conn = AplicacionDeportiva.ConexionBD.getConexion();
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
                    try (Connection conn = AplicacionDeportiva.ConexionBD.getConexion();
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
                    try (Connection conn = AplicacionDeportiva.ConexionBD.getConexion();
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
                    try (Connection conn = AplicacionDeportiva.ConexionBD.getConexion();
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
                    try (Connection conn = AplicacionDeportiva.ConexionBD.getConexion();
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
                    try (Connection conn = AplicacionDeportiva.ConexionBD.getConexion();
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
                    try (Connection conn = AplicacionDeportiva.ConexionBD.getConexion();
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
                    try (Connection conn = AplicacionDeportiva.ConexionBD.getConexion();
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
                    try (Connection conn = AplicacionDeportiva.ConexionBD.getConexion();
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
                    try (Connection conn = AplicacionDeportiva.ConexionBD.getConexion();
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
                    try (Connection conn = AplicacionDeportiva.ConexionBD.getConexion();
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
                    try (Connection conn = AplicacionDeportiva.ConexionBD.getConexion();
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
                    try (Connection conn = AplicacionDeportiva.ConexionBD.getConexion();
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
                    try (Connection conn = AplicacionDeportiva.ConexionBD.getConexion();
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
                    try (Connection conn = AplicacionDeportiva.ConexionBD.getConexion();
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
                    try (Connection conn = AplicacionDeportiva.ConexionBD.getConexion();
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
                    try (Connection conn = AplicacionDeportiva.ConexionBD.getConexion();
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
                    try (Connection conn = AplicacionDeportiva.ConexionBD.getConexion();
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


}
