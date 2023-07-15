package entities;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface IUserManagement {

	public boolean isTableExists(Connection connection, String tableName)throws SQLException;
	public void createUserTableIfNotExists();
	public void insertUser(String name, double salary, int age,String poste);
	public void deleteUser(String name);
	public void updateUser(String name, double salary, int age, String poste);
	public List<User> getUsers();
	void exportUsersToCSV(List<User> users, String filePath);
	double calculateAverageSalary();
	List<User> getUsersBetweenAge(int minAge, int maxAge);
	List<User> getUsersByPoste(String poste);
	List<User> getUsersSortedByAge();
	List<User> getUsersSortedBySalary();
	int getTotalUsers();
	void displayUsersGroupedByPoste();
}
