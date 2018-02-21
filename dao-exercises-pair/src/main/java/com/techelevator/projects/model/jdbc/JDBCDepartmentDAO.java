package com.techelevator.projects.model.jdbc;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.techelevator.projects.model.Department;
import com.techelevator.projects.model.DepartmentDAO;

public class JDBCDepartmentDAO implements DepartmentDAO {

	private JdbcTemplate jdbcTemplate;

	public JDBCDepartmentDAO(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);

	}

	@Override // done?
	public List<Department> getAllDepartments() {
		ArrayList<Department> departments = new ArrayList<>();
		String sqlfindAllDepartments = "SELECT department_id, name " + "FROM department      ";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlfindAllDepartments);
		while (results.next()) {
			Department theDepartment = mapRowToDepartment(results);
			departments.add(theDepartment);
		}
		return departments;
	}

	@Override // refactor later
	public List<Department> searchDepartmentsByName(String nameSearch) {
		ArrayList<Department> departments = new ArrayList<>();
		String sqlFindDepartmentByName = "SELECT department_id, name " + "FROM department " + "WHERE name ILIKE ?";
		nameSearch = "%" + nameSearch + "%";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlFindDepartmentByName, nameSearch);
		while (results.next()) {
			Department theDepartment = mapRowToDepartment(results);
			departments.add(theDepartment);
		}
		return departments;
	}

	@Override
	public void updateDepartmentName(Long departmentId, String departmentName) {
		// Department theDepartment = new Department(); //creating a new object
		// necessary?
		String updateDepartment = "UPDATE department set name = ? where department_id= ?";
		jdbcTemplate.update(updateDepartment, departmentName, departmentId);
	}

	@Override
	public Department createDepartment(String departmentName) {
		// Department theDepartment = new Department();
		String sqlInsertDepartment = "INSERT INTO department (name) VALUES (?) RETURNING department_id";
	
		return getDepartmentById(jdbcTemplate.queryForObject(sqlInsertDepartment,Long.class,departmentName));
	}

	@Override 
	public Department getDepartmentById(Long id) {
		Department departmentIdObject = null;
		String getDepartmentQuery = "SELECT department_id, name FROM department WHERE department_id = ?";
		SqlRowSet results = jdbcTemplate.queryForRowSet(getDepartmentQuery, id);
		
	    if (results.next()) {
	        departmentIdObject = mapRowToDepartment(results);
	    }
	    return departmentIdObject;
	}

	private Department mapRowToDepartment(SqlRowSet results) {
		Department theDepartment;
		theDepartment = new Department();
		theDepartment.setId(results.getLong("department_id"));
		theDepartment.setName(results.getString("name"));
		return theDepartment;
	}

}
