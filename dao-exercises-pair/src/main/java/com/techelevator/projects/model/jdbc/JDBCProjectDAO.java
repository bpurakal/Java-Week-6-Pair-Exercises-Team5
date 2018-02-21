package com.techelevator.projects.model.jdbc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.techelevator.projects.model.Department;
import com.techelevator.projects.model.Employee;
import com.techelevator.projects.model.Project;
import com.techelevator.projects.model.ProjectDAO;

public class JDBCProjectDAO implements ProjectDAO {

	private JdbcTemplate jdbcTemplate;

	public JDBCProjectDAO(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Override
	public List<Project> getAllActiveProjects(){
			
			ArrayList<Project> projects = new ArrayList<>();
			String sqlFindAllProjects = "SELECT project_id, name, from_date, to_date" + " FROM project WHERE "
			+ "(now() BETWEEN from_date AND to_date ) OR "
			+ "(now() >  from_date AND to_date IS NULL ) OR "
			+ "(now() < to_date AND to_date IS NULL)";
			
			SqlRowSet results = jdbcTemplate.queryForRowSet(sqlFindAllProjects);
			while(results.next()) {
				Project projectResult = mapRowToProject(results);
				projects.add(projectResult);
			}

			return projects;
	}
		

	private Project mapRowToProject(SqlRowSet results) {
		//connecting tables
		Project theProject;
		theProject = new Project();
		theProject.setId(results.getLong("project_id"));
		theProject.setName(results.getString("name"));
		
		if (results.getDate("to_date") != null) {
		theProject.setEndDate(results.getDate("to_date").toLocalDate());
		}
		if (results.getDate("to_date") != null) {
		theProject.setStartDate(results.getDate("from_date").toLocalDate());
		}
		
		return theProject;
		
	
//		to_date
//		project_id
//		name
//		from_date
//			
//		private Long id;
//		private String name;
//		private LocalDate startDate;
//		private LocalDate endDate;
		}

	@Override
	public void removeEmployeeFromProject(Long projectId, Long employeeId) {
		String removeEmpProject= "DELETE FROM project_employee where employee_id= ? and project_Id = ? ";
		jdbcTemplate.update(removeEmpProject, projectId, employeeId);
	}

	@Override
	public void addEmployeeToProject(Long projectId, Long employeeId) {
		String sqlAddEmployeeToProject = "INSERT INTO project_employee (project_id, employee_id) VALUES (?, ?)";
		jdbcTemplate.update(sqlAddEmployeeToProject, projectId, employeeId);
	}

}
