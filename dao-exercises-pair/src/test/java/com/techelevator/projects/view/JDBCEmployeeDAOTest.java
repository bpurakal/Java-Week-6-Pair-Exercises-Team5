package com.techelevator.projects.view;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import com.techelevator.projects.model.Department;
import com.techelevator.projects.model.Employee;
import com.techelevator.projects.model.jdbc.JDBCDepartmentDAO;
import com.techelevator.projects.model.jdbc.JDBCEmployeeDAO;

public class JDBCEmployeeDAOTest {

	private static SingleConnectionDataSource dataSource;
	private JDBCEmployeeDAO testThing;
	private Long newEmployeeId;
	private Long newDepartmentId;
	private Long newProjectId;
	private Long newDepartmentChangeId; 
	private Long employeeIdWithOutProject;

	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		dataSource = new SingleConnectionDataSource();
		dataSource.setUrl("jdbc:postgresql://localhost:5432/project_organizer2");
		dataSource.setUsername("postgres");
		
		/*This turns off auto commit on each call to the database */
		dataSource.setAutoCommit(false);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		dataSource.destroy();
	}

	@Before
	public void setUp() throws Exception {
		testThing = new JDBCEmployeeDAO(dataSource);
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

		//making department for employee to be apart of.
		String newDep = "INSERT INTO department (name) VALUES (?) RETURNING department_id";
		newDepartmentId = jdbcTemplate.queryForObject(newDep, Long.class, "TEST");
		
		//making project for employee to be apart of.
		String newProject = "INSERT INTO project (name) VALUES (?) RETURNING project_id";
		newProjectId = jdbcTemplate.queryForObject(newProject, Long.class, "TESTPROJECT");
		
		
		//making department for employee to be apart of.
		String newDepChange = "INSERT INTO department (name) VALUES (?) RETURNING department_id";
		newDepartmentChangeId = jdbcTemplate.queryForObject(newDep, Long.class, "TESTDEPARTMENT");
				
		//making new test employee and assigning department to them
		String newEmp = "INSERT INTO employee (department_id, first_name, last_name, birth_date, gender, hire_date) VALUES (?, ?, ?, ?, ?, ?) RETURNING employee_id";
		newEmployeeId = jdbcTemplate.queryForObject(newEmp, Long.class, newDepartmentId, "Bob", "Smith",  LocalDate.parse("1953-07-15"), "M", LocalDate.parse("2001-04-01"));
		
		//employee without project
		String newEmpWithoutProject = "INSERT INTO employee (department_id, first_name, last_name, birth_date, gender, hire_date) VALUES (?, ?, ?, ?, ?, ?) RETURNING employee_id";
		employeeIdWithOutProject = jdbcTemplate.queryForObject(newEmp, Long.class, newDepartmentId, "Bob", "Smith",  LocalDate.parse("1953-07-15"), "M", LocalDate.parse("2001-04-01"));
		
		//making project employee table and assigning employees to projects
		String newProjTable = "INSERT INTO project_employee (employee_id, project_id) VALUES (?, ?)";
		jdbcTemplate.update(newProjTable, newEmployeeId, newProjectId);

//		private Long employeeId;
//		private Long departmentId;
//		private String firstName;
//		private String lastName;
//		private LocalDate birthDay;
//		private char gender;
//		private LocalDate hireDate;
		
	
	}

	@After
	public void tearDown() throws Exception {
		dataSource.getConnection().rollback();
	}

	@Test
	public void testGetAllEmployees() {
		List<Employee> allEmployees = testThing.getAllEmployees();
		
		for(Employee emp: allEmployees) {
		if (emp.getFirstName().equals("Bob") && emp.getId().equals(newEmployeeId)) {
		assertEquals(newEmployeeId, emp.getId());
		assertEquals("Bob", emp.getFirstName());
		assertEquals("Smith", emp.getLastName());
		assertEquals(LocalDate.parse("1953-07-15"), emp.getBirthDay());
		assertEquals('M', emp.getGender());
		assertEquals(LocalDate.parse("2001-04-01"), emp.getHireDate());
		assertEquals(newDepartmentId, emp.getDepartmentId());

		return;
		}
		}
		fail("Test Department not found");
	}

	@Test
	public void testSearchEmployeesByName() {
		List<Employee> emps = testThing.searchEmployeesByName("Bob", "Smith");
		
		Employee emp = emps.get(0);
		assertNotNull(emp);
		assertEquals(newEmployeeId, emp.getId());
		assertEquals("Bob", emp.getFirstName());
		assertEquals("Smith", emp.getLastName());

	}

	@Test
	public void testGetEmployeesByDepartmentId() {
		
		List <Employee> employeesInDepartment = testThing.getEmployeesByDepartmentId(newDepartmentId);

		
		Employee emp = employeesInDepartment.get(0);
		assertNotNull(emp);
		assertEquals(newEmployeeId, emp.getId());
		assertEquals("Bob", emp.getFirstName());
		assertEquals("Smith", emp.getLastName());
	}

	@Test
	public void testGetEmployeesWithoutProjects() {
		
		//remove sut from project_employee table
//				JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
//
//				String rmvProjEmpEntry = "DELETE FROM  project_employee where employee_id = ? and  project_id = ?";
//				jdbcTemplate.update(rmvProjEmpEntry, newEmployeeId, newProjectId);
		
		// run method
		
		List <Employee> employeesWithOutProjects = testThing.getEmployeesWithoutProjects();
		
		for(Employee emp: employeesWithOutProjects) {
			
			if (emp.getId().equals(newEmployeeId)) {

			fail("Employees Without Projects is not working");

			}
			}
		return;
		
	}
	
	
	@Test //Alternative Method
	public void testGetEmployeesWithoutProjects2() {
		List <Employee> employeesWithoutDepartment = testThing.getEmployeesWithoutProjects();
		for(Employee emp : employeesWithoutDepartment) {
			if (emp.getId().equals(employeeIdWithOutProject)) {
				return;
			}
			
		}
		fail("Employees without Projects is not working");
		
	}

	@Test
	public void testGetEmployeesByProjectId() {
	List <Employee> employeesInDepartment = testThing.getEmployeesByProjectId(newProjectId);

		
		Employee emp = employeesInDepartment.get(0);
		assertNotNull(emp);
		assertEquals(newEmployeeId, emp.getId());
		assertEquals("Bob", emp.getFirstName());
		assertEquals("Smith", emp.getLastName());
	}

	@Test
	public void testChangeEmployeeDepartment() {
		
		  testThing.changeEmployeeDepartment(newEmployeeId, newDepartmentChangeId);
		
		 List <Employee> employeesOnNewDep = testThing.getEmployeesByDepartmentId(newDepartmentChangeId);

			
			Employee emp = employeesOnNewDep.get(0);
			assertNotNull(emp);
			assertEquals(newEmployeeId, emp.getId());
			assertEquals("Bob", emp.getFirstName());
			assertEquals("Smith", emp.getLastName());
			
	
	
	}

}
