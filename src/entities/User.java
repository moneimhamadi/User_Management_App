package entities;

public class User {
	private String name;
	private String poste;
	private double salary;
	private int age;

	public User(String name, double salary, int age,String poste ) {
		this.name = name;
		this.salary = salary;
		this.age = age;
		this.poste = poste;
	}

	public double getSalary() {
		return salary;
	}

	public int getAge() {
		return age;
	}
	
	public String getPoste() {
		return poste;
	}
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "Name: " + name + ", Salary: " + salary + ", Age: " + age + ", Poste: " + poste;
	}
}
