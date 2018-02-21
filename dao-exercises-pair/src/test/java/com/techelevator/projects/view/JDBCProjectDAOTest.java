package com.techelevator.projects.view;

import static org.junit.Assert.fail;

import java.time.LocalDate;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import com.techelevator.projects.model.Project;
import com.techelevator.projects.model.jdbc.JDBCEmployeeDAO;
import com.techelevator.projects.model.jdbc.JDBCProjectDAO;

public class JDBCProjectDAOTest {

	private static SingleConnectionDataSource dataSource;
	private JDBCProjectDAO testThing;
	private Long newProjectId;

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
		testThing = new JDBCProjectDAO(dataSource);
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

		//making project 
		String newProject = "INSERT INTO project (name,from_date) VALUES (?,?) RETURNING project_id";
		newProjectId = jdbcTemplate.queryForObject(newProject, Long.class, "TESTPROJECT",  LocalDate.parse("2016-10-15"));
		

		
	}

	@After
	public void tearDown() throws Exception {
		dataSource.getConnection().rollback();

	}
	
	
	
	

	
	@Test
	public void testGetAllActiveProjects() {
		List<Project> activeProjects = testThing.getAllActiveProjects();
		
		for (Project proj: activeProjects) {
			if(proj.getId().equals(newProjectId)) {
				return;
			}
		}
		fail("Mission Failed");
	}

	@Test
	public void testRemoveEmployeeFromProject() {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

		String deleteProject = "DELETE FROM project WHERE project_id = ?";
		jdbcTemplate.update(deleteProject, newProjectId);
	
		
		List<Project> activeProjects = testThing.getAllActiveProjects();
		
		for (Project proj: activeProjects) {
			
			
			if(proj.getId().equals(newProjectId)) {
				fail("Mission Failed");
			}
		} return;
	}

	@Test
	public void testAddEmployeeToProject() {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

		String addEmployee = "INSERT INTO project (name,from_date) VALUES (?,?) RETURNING project_id";
		Long createdProjectId = jdbcTemplate.queryForObject(addEmployee, Long.class, "TESTPROJECT2", LocalDate.parse("2016-10-15") );
	
		
		List<Project> activeProjects = testThing.getAllActiveProjects();
		
		for (Project proj: activeProjects) {
			
			
			if(proj.getId().equals(createdProjectId)) {
				return;
			}
		} 	fail("Mission Failed");
	}

}
