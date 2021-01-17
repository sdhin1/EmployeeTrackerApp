package com.saudhing.web.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

public class StudentDBUtil {
	
	private DataSource dataSource;
	
	public StudentDBUtil(DataSource theDataSource) {		
		dataSource = theDataSource;		
	}
	
	public List<Student> getStudents() throws Exception {
		
		List<Student> students = new ArrayList<Student>();
		
		Connection myConn = null;
		Statement myStmt = null;
		ResultSet myRs = null;
		
		try {
			//get a connection 
			myConn = dataSource.getConnection();
			
			//create sql statement
			String sql = "select * from student order by last_name";			
			myStmt = myConn.createStatement();
			
			//process result set
			myRs = myStmt.executeQuery(sql);
			
			//close JDBC objects
			while(myRs.next()) {
				
				//retrieve data from result set row
				int id = myRs.getInt("id");				
				String firstName = myRs.getString("first_name");
				String lastName = myRs.getString("last_name");
				String email = myRs.getString("email");
				
				//create new student object
				Student tempStudent = new Student(id, firstName, lastName, email);
				
				//add it to the list of students
				students.add(tempStudent);
				
			}
			
			return students;
			
		} finally {
			
			//close db objects
			close(myConn, myStmt, myRs);
			
		}
		
	}

	private void close(Connection myConn, Statement myStmt, ResultSet myRs) {
		
		try {
			
			if(myRs != null) {
				myRs.close();
			}
			
			if(myStmt != null) {
				myStmt.close();
			}
			
			if(myConn != null) {
				myConn.close();     //does not close the connection. puts it back to the connection pool
			}
			
		} catch (Exception e) {
			
		}
		
	}

	public void addStudent(Student theStudent) throws SQLException {
		
		Connection myConn = null;
		PreparedStatement myStmt = null;
		
		try {
			//get db connection
			myConn = dataSource.getConnection();
			
			//create sql for insert
			String sql = "insert into student (first_name, last_name, email) values (? ,?, ?)";
			
			myStmt = myConn.prepareStatement(sql);
			
			//set the parameter values for the student
			myStmt.setString(1, theStudent.getFirstName());
			myStmt.setString(2, theStudent.getLastName());
			myStmt.setString(3, theStudent.getEmail());
			
			//execute sql insert
			myStmt.execute();
			
		} finally {			
			//clean up JDBC objects
			close(myConn, myStmt, null);			
		}
		
	}

	public Student getStudent(String theStudentId) throws Exception {
		
		Student theStudent = null;
		
		Connection myConn = null;
		PreparedStatement myStmt = null;
		ResultSet myRs = null;
		int studentId;
		
		try {
			//convert student id to int
			studentId = Integer.parseInt(theStudentId);
			
			//get connection to database
			myConn = dataSource.getConnection();
			
			//create sql to selected student
			String sql = "select * from student where id=?";
			
			//create prepared statement
			myStmt = myConn.prepareStatement(sql);
			
			//set parameters
			myStmt.setInt(1, studentId);
			
			//execute statement
			myRs = myStmt.executeQuery();
			
			//retrieve data from result set row
			if(myRs.next()) {
				String firstName = myRs.getString("first_name");
				String lastName = myRs.getString("last_name");
				String email = myRs.getString("email");
				
				//use the student id during construction
				theStudent = new Student(studentId, firstName, lastName, email);
			} else {
				throw new Exception("Could not find Student with Student Id: "+studentId);
			}		
			
			return theStudent;
			
		} finally {
			//clean up of jdbc objects
			close(myConn, myStmt, myRs);
		}
		
	}

	public void updateStudent(Student theStudent) throws Exception {
		
		Connection myConn = null;
		PreparedStatement myStmt = null;

		try {
			//get db connection
			myConn = dataSource.getConnection();
			
			//create sql update statement
			String sql = "update student Set first_name=?, last_name=?, email=? where id=?";
			
			//prepare statement
			myStmt = myConn.prepareStatement(sql);
			
			//set params
			myStmt.setString(1, theStudent.getFirstName());
			myStmt.setString(2, theStudent.getLastName());
			myStmt.setString(3, theStudent.getEmail());
			myStmt.setInt(4, theStudent.getId());
			
			//execute sql statement
			myStmt.execute();
			
		} finally {
			//close db connection
			close(myConn, myStmt, null);
		}
		
	}

	public void deleteStudent(String theStudentId) throws Exception {

		Connection myConn = null;
		PreparedStatement myStmt = null;
		
		try {
			
			//convert student id to integer
			int studentId = Integer.parseInt(theStudentId);
			
			//get connection to db
			myConn = dataSource.getConnection();
			
			//create sql to delete student
			String sql = "delete from student where id=?";
			
			//prepare statement
			myStmt = myConn.prepareStatement(sql);
			
			//set params
			myStmt.setInt(1, studentId);
			
			//execute sql statement
			myStmt.execute();
			
		} finally {
			//close jdbc
			close(myConn, myStmt, null);
		}
		
	}

}
