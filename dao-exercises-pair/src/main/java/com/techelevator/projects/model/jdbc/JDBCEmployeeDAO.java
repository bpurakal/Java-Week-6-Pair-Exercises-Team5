package com.techelevator.projects.model.jdbc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.techelevator.projects.model.Department;
import com.techelevator.projects.model.Employee;
import com.techelevator.projects.model.EmployeeDAO;

public class JDBCEmployeeDAO implements EmployeeDAO {


	private JdbcTemplate jdbcTemplate;

	public JDBCEmployeeDAO(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Override
	public List<Employee> getAllEmployees() {

		ArrayList<Employee> employees = new ArrayList<>();
		String sqlfindAllEmployees = "SELECT * " + "FROM employee";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlfindAllEmployees);
		while (results.next()) {
			Employee theEmployee = mapRowToEmployee(results);
			employees.add(theEmployee);
		}
		return employees;
	}

	@Override
	public List<Employee> searchEmployeesByName(String firstNameSearch, String lastNameSearch) {
		ArrayList<Employee> employees = new ArrayList<>();
		
		String sqlFindEmployeesByName = "SELECT * FROM EMPLOYEE "+
										"WHERE first_name ILIKE ? "+
										"OR last_name ILIKE ?";
		
		firstNameSearch = "%" + firstNameSearch +"%";
		lastNameSearch = "%" + lastNameSearch + "%";
		
		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlFindEmployeesByName,firstNameSearch,lastNameSearch);
		
		while (results.next()) {
			Employee theEmployee = mapRowToEmployee(results);
			employees.add(theEmployee);
		}
			return employees;				
	}
	
	
	//  test comment getEmployeesByProjectId has not been tested in console.
	@Override
	public List<Employee> getEmployeesByDepartmentId(long id) {
		ArrayList<Employee> employees = new ArrayList<>();
		String sqlFindEmployeesOnThisProject =
		"SELECT * FROM EMPLOYEE "+
		"WHERE DEPARTMENT_ID = ?";
				
		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlFindEmployeesOnThisProject, id);
		while(results.next()) {
			Employee theEmployee = mapRowToEmployee(results);
			employees.add(theEmployee);
		}
		return employees;
	}

	@Override
	public List<Employee> getEmployeesWithoutProjects() {
		ArrayList<Employee> employees = new ArrayList<>();
		String sqlFindEmployeesWithOutProjects =
		"SELECT * FROM EMPLOYEE "+
		"LEFT JOIN PROJECT_EMPLOYEE AS PE ON EMPLOYEE.EMPLOYEE_ID = PE.EMPLOYEE_ID "+
		"WHERE PE.EMPLOYEE_ID IS NULL";
				
		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlFindEmployeesWithOutProjects);
		while(results.next()) {
			Employee theEmployee = mapRowToEmployee(results);
			employees.add(theEmployee);
		}
		return employees;
	}

	
	
		//  test comment getEmployeesByProjectId has not been tested in console. db vis results are good.
	@Override
	public List<Employee> getEmployeesByProjectId(Long projectId) {
		ArrayList<Employee> employees = new ArrayList<>();
		String sqlFindEmployeesByProjects =
		"SELECT * FROM PROJECT_EMPLOYEE AS PE "+
		"JOIN EMPLOYEE ON EMPLOYEE.EMPLOYEE_ID = PE.EMPLOYEE_ID "+
		"WHERE PE.PROJECT_ID = ?";
		
		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlFindEmployeesByProjects, projectId);
				
		while(results.next()) {
			Employee theEmployee = mapRowToEmployee(results);
			employees.add(theEmployee);
		}
		return employees;
	}
	
	
	
	
	@Override
	public void changeEmployeeDepartment(Long employeeId, Long departmentId) {
		String updateEmployeeDepartment = "UPDATE employee set department_id = ? where employee_id = ?";
		jdbcTemplate.update(updateEmployeeDepartment, departmentId, employeeId);
	}	
	
	
	
	
	
	
	
	private Employee mapRowToEmployee(SqlRowSet results) {
		Employee theEmployee;
		theEmployee = new Employee();
		theEmployee.setId(results.getLong("employee_id"));
		theEmployee.setDepartmentId(results.getLong("department_id"));
		theEmployee.setFirstName(results.getString("first_name"));
		theEmployee.setLastName(results.getString("last_name"));
		theEmployee.setBirthDay(results.getDate("birth_date").toLocalDate());
		theEmployee.setGender(results.getString("gender").charAt(0));
		theEmployee.setHireDate(results.getDate("hire_date").toLocalDate());


		return theEmployee;
	}
}