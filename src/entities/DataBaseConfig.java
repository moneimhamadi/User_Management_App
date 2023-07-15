package entities;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class DataBaseConfig implements IUserManagement {
	private static final String DB_URL = "jdbc:mysql://localhost:3306/mydatabase";
	private static final String DB_USER = "root";
	private static final String DB_PASSWORD = "";

	private List<String> postes;
	private static final String[] POSTE_OPTIONS = { "Developer", "Director", "Analyst" };

	public static final String RESET = "\u001B[0m";
	public static final String BLUE = "\u001B[34m";
	public static final String RED = "\u001B[31m";
	public static final String GREEN = "\u001B[32m";

	private List<User> users;
	private Scanner scanner;

	private static final String ADMIN_USERNAME = "admin";
	private static final String ADMIN_PASSWORD = "password";

	public DataBaseConfig() {
		users = new ArrayList<>();
		postes = new ArrayList<>();
		postes.add("Manager");
		postes.add("Engineer");
		postes.add("Analyst");
		postes.add("Developer");
	}

	public List<String> getPostes() {
		return postes;
	}

	@Override
	public boolean isTableExists(Connection connection, String tableName) throws SQLException {
		DatabaseMetaData databaseMetaData = connection.getMetaData();
		try (ResultSet resultSet = databaseMetaData.getTables(null, null, tableName, null)) {
			return resultSet.next();
		}
	}

	@Override
	public void createUserTableIfNotExists() {
		try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
			if (!isTableExists(connection, "users")) {
				try (Statement statement = connection.createStatement()) {
					String createTableSQL = "CREATE TABLE users (" + "id INT AUTO_INCREMENT PRIMARY KEY,"
							+ "name VARCHAR(100) NOT NULL," + "salary DOUBLE NOT NULL," + "age INT NOT NULL,"
							+ "poste VARCHAR(100) NOT NULL" + ")";
					statement.executeUpdate(createTableSQL);
					System.out.println("Table 'users' created successfully.");
				}
			}
		} catch (SQLException e) {
			System.out.println("Error creating 'users' table: " + e.getMessage());
		}
	}

	@Override
	public void insertUser(String name, double salary, int age, String poste) {
		try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
				PreparedStatement statement = connection
						.prepareStatement("INSERT INTO users (name, salary, age, poste) VALUES (?, ?, ?, ?)")) {

			statement.setString(1, name);
			statement.setDouble(2, salary);
			statement.setInt(3, age);
			statement.setString(4, poste);

			statement.executeUpdate();
			System.out.println("User inserted successfully into the database.");

		} catch (SQLException e) {
			System.out.println("Error inserting user into the database: " + e.getMessage());
		}
	}

	@Override
	public void deleteUser(String name) {
		try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
				PreparedStatement statement = connection.prepareStatement("DELETE FROM users WHERE name = ?")) {

			statement.setString(1, name);

			int rowsAffected = statement.executeUpdate();
			if (rowsAffected > 0) {
				System.out.println("User deleted successfully from the database.");
			} else {
				System.out.println("No user found with the name '" + name + "'.");
			}

		} catch (SQLException e) {
			System.out.println("Error deleting user from the database: " + e.getMessage());
		}
	}

	@Override
	public void updateUser(String name, double salary, int age, String poste) {
		try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
				PreparedStatement statement = connection
						.prepareStatement("UPDATE users SET salary = ?, age = ?, poste = ? WHERE name = ?")) {

			statement.setDouble(1, salary);
			statement.setInt(2, age);
			statement.setString(3, poste);
			statement.setString(4, name);

			int rowsAffected = statement.executeUpdate();
			if (rowsAffected > 0) {
				System.out.println("User updated successfully in the database.");
			} else {
				System.out.println("No user found with the name '" + name + "'.");
			}

		} catch (SQLException e) {
			System.out.println("Error updating user in the database: " + e.getMessage());
		}
	}

	@Override
	public List<User> getUsers() {
		List<User> users = new ArrayList<>();
		try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
				Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery("SELECT * FROM users")) {

			while (resultSet.next()) {
				String name = resultSet.getString("name");
				double salary = resultSet.getDouble("salary");
				int age = resultSet.getInt("age");
				String poste = resultSet.getString("poste");

				users.add(new User(name, salary, age, poste));
			}

		} catch (SQLException e) {
			System.out.println("Error retrieving users from the database: " + e.getMessage());
		}
		return users;
	}

	
	 @Override
	public double calculateAverageSalary() {
		try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
				Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery("SELECT AVG(salary) AS average_salary FROM users")) {

			if (resultSet.next()) {
				return resultSet.getDouble("average_salary");
			}

		} catch (SQLException e) {
			System.out.println("Error calculating average salary: " + e.getMessage());
		}
		return 0; // Return 0 in case of an error or no data available
	}

	
	 @Override
	public List<User> getUsersBetweenAge(int minAge, int maxAge) {
		List<User> users = new ArrayList<>();
		try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
				PreparedStatement statement = connection
						.prepareStatement("SELECT * FROM users WHERE age BETWEEN ? AND ?")) {

			statement.setInt(1, minAge);
			statement.setInt(2, maxAge);

			try (ResultSet resultSet = statement.executeQuery()) {
				while (resultSet.next()) {
					String name = resultSet.getString("name");
					double salary = resultSet.getDouble("salary");
					int age = resultSet.getInt("age");
					String poste = resultSet.getString("poste");

					users.add(new User(name, salary, age, poste));
				}
			}

		} catch (SQLException e) {
			System.out.println("Error retrieving users between age from the database: " + e.getMessage());
		}
		return users;
	}

	 @Override
	public List<User> getUsersByPoste(String poste) {
		List<User> users = new ArrayList<>();
		try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
				PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE poste = ?")) {

			statement.setString(1, poste);

			try (ResultSet resultSet = statement.executeQuery()) {
				while (resultSet.next()) {
					String name = resultSet.getString("name");
					double salary = resultSet.getDouble("salary");
					int age = resultSet.getInt("age");

					users.add(new User(name, salary, age, poste));
				}
			}

		} catch (SQLException e) {
			System.out.println("Error retrieving users by poste from the database: " + e.getMessage());
		}
		return users;
	}

	
	 @Override
	public List<User> getUsersSortedByAge() {
		List<User> users = new ArrayList<>();
		try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
				Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery("SELECT * FROM users ORDER BY age")) {

			while (resultSet.next()) {
				String name = resultSet.getString("name");
				double salary = resultSet.getDouble("salary");
				int age = resultSet.getInt("age");
				String poste = resultSet.getString("poste");

				users.add(new User(name, salary, age, poste));
			}

		} catch (SQLException e) {
			System.out.println("Error retrieving users sorted by age from the database: " + e.getMessage());
		}
		return users;
	}

	
	 @Override
	public List<User> getUsersSortedBySalary() {
		List<User> users = new ArrayList<>();
		try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
				Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery("SELECT * FROM users ORDER BY salary")) {

			while (resultSet.next()) {
				String name = resultSet.getString("name");
				double salary = resultSet.getDouble("salary");
				int age = resultSet.getInt("age");
				String poste = resultSet.getString("poste");

				users.add(new User(name, salary, age, poste));
			}

		} catch (SQLException e) {
			System.out.println("Error retrieving users sorted by salary from the database: " + e.getMessage());
		}
		return users;
	}
	
	 @Override
	 public int getTotalUsers() {
	        int totalUsers = 0;
	        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
	             Statement statement = connection.createStatement();
	             ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) AS total FROM users")) {

	            if (resultSet.next()) {
	                totalUsers = resultSet.getInt("total");
	            }

	        } catch (SQLException e) {
	            System.out.println("Error retrieving total number of users: " + e.getMessage());
	        }
	        return totalUsers;
	    }
	 
	 @Override
	 public void displayUsersGroupedByPoste() {
	        Map<String, List<User>> usersByPoste = new HashMap<>();

	        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
	             Statement statement = connection.createStatement();
	             ResultSet resultSet = statement.executeQuery("SELECT * FROM users")) {

	            while (resultSet.next()) {
	                String name = resultSet.getString("name");
	                double salary = resultSet.getDouble("salary");
	                int age = resultSet.getInt("age");
	                String poste = resultSet.getString("poste");

	                User user = new User(name, salary, age, poste);

	                usersByPoste.putIfAbsent(poste, new ArrayList<>());
	                usersByPoste.get(poste).add(user);
	            }

	            for (Map.Entry<String, List<User>> entry : usersByPoste.entrySet()) {
	                String poste = entry.getKey();
	                List<User> users = entry.getValue();

	                System.out.println("Poste: " + poste);
	                for (User user : users) {
	                    System.out.println(user);
	                }
	                System.out.println();
	            }

	        } catch (SQLException e) {
	            System.out.println("Error retrieving users from the database: " + e.getMessage());
	        }
	    }
	 
	 @Override
	 public void exportUsersToCSV(List<User> users, String filePath) {
	        try (FileWriter writer = new FileWriter(filePath)) {
	            // Write CSV header
	            writer.append("Name,Salary,Age,Poste\n");

	            // Write user data to the CSV file
	            for (User user : users) {
	                writer.append(user.getName()).append(",");
	                writer.append(String.valueOf(user.getSalary())).append(",");
	                writer.append(String.valueOf(user.getAge())).append(",");
	                writer.append(user.getPoste()).append("\n");
	            }

	            System.out.println("Users data exported to " + filePath);
	        } catch (IOException e) {
	            System.out.println("Error exporting users data to CSV file: " + e.getMessage());
	        }
	    }
	public static void main(String[] args) {
		DataBaseConfig userManagement = new DataBaseConfig();
		Scanner scanner = new Scanner(System.in);

		userManagement.createUserTableIfNotExists();
		boolean loggedIn = false;
		while (!loggedIn) {
			System.out.println("Login");
			System.out.print("Username: ");
			String username = scanner.nextLine();

			System.out.print("Password: ");
			String password = scanner.nextLine();

			if (username.equals(ADMIN_USERNAME) && password.equals(ADMIN_PASSWORD)) {
				System.out.println(GREEN + "Login successful. Access granted." + RESET);
				for (int i = 0; i < 2; i++) {
					System.out.println();
				}
				loggedIn = true;
			} else {
				System.out.println(RED + "Login failed. Access denied. Please try again ." + RESET);
			}
		}
		while (true) {
			System.out.println("Menu:");
			System.out.println("1. Insert User");
			System.out.println("2. Delete a user with name");
			System.out.println("3. Update a user's information");
			System.out.println("4. Display All Users");
			System.out.println("5. Calculate Average Salary");
			System.out.println("6. Get Users Between Age");
			System.out.println("7. Display Users by Poste");
			System.out.println("8. Display Users Sorted by Salary");
			System.out.println("9. Display Users Sorted by Age");
		    System.out.println("10. Display Total Number of Users");
		    System.out.println("11. Users Grouped by Poste ");
	        System.out.println("12. Export Users to CSV");

            System.out.println("13. Exit");

			System.out.print("Enter your choice: ");
			int choice = scanner.nextInt();
			scanner.nextLine();

			switch (choice) {
			case 1:
				System.out.print("Enter name: ");
				String name = scanner.nextLine();

				List<String> postes = userManagement.getPostes();
				System.out.println("Available Postes:");
				for (int i = 0; i < postes.size(); i++) {
					System.out.println((i + 1) + ". " + postes.get(i));
				}

				System.out.print("Choose poste (enter the number): ");
				int posteChoice = scanner.nextInt();
				scanner.nextLine();

				if (posteChoice >= 1 && posteChoice <= postes.size()) {
					String poste = postes.get(posteChoice - 1);

					System.out.print("Enter salary: ");
					double salary = scanner.nextDouble();
					System.out.print("Enter age: ");
					int age = scanner.nextInt();
					userManagement.insertUser(name, salary, age, poste);
				} else {
					System.out.println("Invalid poste choice. User not inserted.");
				}
				break;
			case 2:
				System.out.print("Enter name of the user to delete: ");
				String nameToDelete = scanner.nextLine();
				userManagement.deleteUser(nameToDelete);
				break;
			case 3:
				System.out.print("Enter name of the user to update: ");
				String nameToUpdate = scanner.nextLine();

				List<String> postesList = userManagement.getPostes();
				System.out.println("Available Postes:");
				for (int i = 0; i < postesList.size(); i++) {
					System.out.println((i + 1) + ". " + postesList.get(i));
				}

				System.out.print("Choose new poste (enter the number): ");
				int posteeChoice = scanner.nextInt();
				scanner.nextLine();

				if (posteeChoice >= 1 && posteeChoice <= postesList.size()) {
					String newPoste = postesList.get(posteeChoice - 1);

					System.out.print("Enter new salary: ");
					double newSalary = scanner.nextDouble();
					System.out.print("Enter new age: ");
					int newAge = scanner.nextInt();
					userManagement.updateUser(nameToUpdate, newSalary, newAge, newPoste);
				} else {
					System.out.println("Invalid poste choice. User not updated.");
				}
				break;
			case 4:
				List<User> allUsers = userManagement.getUsers();
				for (User user : allUsers) {
					System.out.println(user);
				}
				break;
			case 5:
				double averageSalary = userManagement.calculateAverageSalary();
				System.out.println("Average Salary: " + averageSalary);
				break;
			case 6:
				System.out.print("Enter minimum age: ");
				int minAge = scanner.nextInt();
				System.out.print("Enter maximum age: ");
				int maxAge = scanner.nextInt();
				List<User> usersBetweenAge = userManagement.getUsersBetweenAge(minAge, maxAge);
				for (User user : usersBetweenAge) {
					System.out.println(user);
				}
				break;
			case 7:
				// Display the available postes
				List<String> Allpostes = userManagement.getPostes();
				System.out.println("Available Postes:");
				for (int i = 0; i < Allpostes.size(); i++) {
					System.out.println((i + 1) + ". " + Allpostes.get(i));
				}

				System.out.print("Choose poste (enter the number): ");
				posteChoice = scanner.nextInt();
				scanner.nextLine(); // Consume the newline character

				// Validate the poste choice
				if (posteChoice >= 1 && posteChoice <= Allpostes.size()) {
					String poste = Allpostes.get(posteChoice - 1);

					List<User> usersByPoste = userManagement.getUsersByPoste(poste);
					for (User user : usersByPoste) {
						System.out.println(user);
					}
				} else {
					System.out.println("Invalid poste choice.");
				}
				break;

			case 8:
				List<User> usersSortedBySalary = userManagement.getUsersSortedBySalary();
				for (User user : usersSortedBySalary) {
					System.out.println(user);
				}
				break;

			case 9:
			    List<User> usersSortedByAge = userManagement.getUsersSortedByAge();
			    for (User user : usersSortedByAge) {
			        System.out.println(user);
			    }
			    scanner.nextLine(); // Consume the newline character
			    break;
			    
			 case 10:
                 int totalUsers = userManagement.getTotalUsers();
                 System.out.println("Total number of users: " + totalUsers);
                 break;
			 case 11:
                 userManagement.displayUsersGroupedByPoste();
                 break;
                 
			 case 12:
	                // Export Users to CSV
	                List<User> ToCsvUsers = userManagement.getUsers();
	                String filePath = "C:\\Users\\monei\\Documents\\users.csv";
	                userManagement.exportUsersToCSV(ToCsvUsers, filePath);
	                break;
			case 13:
				System.out.println("Exiting...");
				System.exit(0);

			default:
				System.out.println("Invalid choice. Try again.");
			}
			System.out.println();
		}
	}
}
