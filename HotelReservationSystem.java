import java.sql.*;
import java.util.Scanner;

public class HotelReservationSystem {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/hotel_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "mysql@33#06";

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Scanner scanner = new Scanner(System.in)) {

            System.out.println("Welcome to Hotel Reservation System");

            while (true) {
                System.out.println("\nMenu:");
                System.out.println("1. Add Room");
                System.out.println("2. View Available Rooms");
                System.out.println("3. Book Room");
                System.out.println("4. View Bookings");
                System.out.println("5. Exit");
                System.out.print("Enter your choice: ");

                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        addRoom(connection, scanner);
                        break;
                    case 2:
                        viewAvailableRooms(connection);
                        break;
                    case 3:
                        bookRoom(connection, scanner);
                        break;
                    case 4:
                        viewBookings(connection);
                        break;
                    case 5:
                        System.out.println("Exiting... Thank you!");
                        return;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void addRoom(Connection connection, Scanner scanner) throws SQLException {
        System.out.print("Enter Room Number: ");
        int roomNumber = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Enter Room Type (e.g., Single, Double, Suite): ");
        String roomType = scanner.nextLine();

        System.out.print("Enter Room Price: ");
        double price = scanner.nextDouble();

        String sql = "INSERT INTO rooms (room_number, room_type, price, is_booked) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, roomNumber);
            stmt.setString(2, roomType);
            stmt.setDouble(3, price);
            stmt.setBoolean(4, false);
            stmt.executeUpdate();
            System.out.println("Room added successfully!");
        }
    }

    private static void viewAvailableRooms(Connection connection) throws SQLException {
        String sql = "SELECT * FROM rooms WHERE is_booked = false";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println("\nAvailable Rooms:");
            while (rs.next()) {
                System.out.println("Room Number: " + rs.getInt("room_number") + ", Type: " + rs.getString("room_type") + ", Price: " + rs.getDouble("price"));
            }
        }
    }

    private static void bookRoom(Connection connection, Scanner scanner) throws SQLException {
        System.out.print("Enter Room Number to Book: ");
        int roomNumber = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Enter Guest Name: ");
        String guestName = scanner.nextLine();

        String checkRoomSql = "SELECT * FROM rooms WHERE room_number = ? AND is_booked = false";
        try (PreparedStatement checkStmt = connection.prepareStatement(checkRoomSql)) {
            checkStmt.setInt(1, roomNumber);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    String bookRoomSql = "UPDATE rooms SET is_booked = true WHERE room_number = ?";
                    try (PreparedStatement bookStmt = connection.prepareStatement(bookRoomSql)) {
                        bookStmt.setInt(1, roomNumber);
                        bookStmt.executeUpdate();
                    }

                    String insertBookingSql = "INSERT INTO bookings (room_number, guest_name) VALUES (?, ?)";
                    try (PreparedStatement bookingStmt = connection.prepareStatement(insertBookingSql)) {
                        bookingStmt.setInt(1, roomNumber);
                        bookingStmt.setString(2, guestName);
                        bookingStmt.executeUpdate();
                        System.out.println("Room booked successfully for " + guestName);
                    }
                } else {
                    System.out.println("Room is not available or does not exist.");
                }
            }
        }
    }

    private static void viewBookings(Connection connection) throws SQLException {
        String sql = "SELECT * FROM bookings";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println("\nBookings:");
            while (rs.next()) {
                System.out.println("Booking ID: " + rs.getInt("booking_id") + ", Room Number: " + rs.getInt("room_number") + ", Guest Name: " + rs.getString("guest_name"));
            }
        }
    }
}
