package com.techelevator.projects.view;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import com.techelevator.projects.model.Department;
import com.techelevator.projects.model.jdbc.JDBCDepartmentDAO;

public class jdbcDepartmentDAOTest {

	private static SingleConnectionDataSource dataSource;
	private JDBCDepartmentDAO testThing;
	private Long newDepartmentId;
	
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
		testThing = new JDBCDepartmentDAO(dataSource);
		
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		String newDep = "INSERT INTO department (name) VALUES (?) RETURNING department_id";
		newDepartmentId = jdbcTemplate.queryForObject(newDep, Long.class, "TEST");
	}

	@After
	public void tearDown() throws Exception {
		dataSource.getConnection().rollback();
	}

	@Test
	public void testGetAllDepartments() {
		List<Department> allDepartments = testThing.getAllDepartments();
		
		for(Department dep: allDepartments) {
		if (dep.getName().equals("TEST") && dep.getId().equals(newDepartmentId)) {
		assertEquals(newDepartmentId, dep.getId());
		assertEquals("TEST", dep.getName());
		return;
		}
		}
		
		fail("Test Department not found");
	}

	@Test
	public void testSearchDepartmentsByName() {
		List<Department> deps = testThing.searchDepartmentsByName("TEST");
		
		Department dep = deps.get(0);
		assertNotNull(dep);
		assertEquals(newDepartmentId, dep.getId());
		assertEquals("TEST", dep.getName());
}

	@Test
	public void testUpdateDepartmentName() {
		//Action
		testThing.updateDepartmentName(newDepartmentId,  "NEW TEST");
		
		//Assert
		Department dep = testThing.getDepartmentById(newDepartmentId);
		assertEquals("NEW TEST", dep.getName());
	}

	@Test
	public void testCreateDepartment() {
	//Act
		Department newDep = testThing.createDepartment("ANOTHER TEST");
		
	//Assert
	Department assertDep = testThing.getDepartmentById(newDep.getId());
	assertEquals("ANOTHER TEST", assertDep.getName());
	}

	@Test
	public void testGetDepartmentById() {
		Department dep = testThing.getDepartmentById(newDepartmentId);
		assertEquals("TEST", dep.getName());
		assertEquals(newDepartmentId, dep.getId());
	}

}