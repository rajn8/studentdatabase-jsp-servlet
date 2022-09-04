package com.luv2code.web.jdbc;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

/**
 * Servlet implementation class StudentControllerServlet
 */
@WebServlet("/StudentControllerServlet")
public class StudentControllerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private StudentDbUtil studentDbUtil;
	
	@Resource(name="jdbc/web_student_tracker")
	private DataSource dataSource;
	
	

	@Override
	public void init() throws ServletException {
		// TODO Auto-generated method stub
		super.init();
		
		//create our student db util ... and pass in conn pool/datasource
		
		try {
			
			studentDbUtil = new StudentDbUtil(dataSource);
		} catch (Exception exc) {
			throw new ServletException(exc);
		}
	}

	
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		PrintWriter out = resp.getWriter();
		String firstName = req.getParameter("firstName");
		out.println("First Name + firstName");
		String lasttName = req.getParameter("lastName");
		out.println("Last Name + lastName");
		String email = req.getParameter("email");
		out.println("Email + email");
	}


	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		//list the students ... in MVC fashion
		try {
			// read the "command" parameter
			String theCommand = request.getParameter("command");
			// if the command is missing, then default to listing students
			if (theCommand == null) {
				theCommand = "LIST";
			}
			// route to the appropriate method in the above the value = ADD so we create a ADD
			switch (theCommand) {
			
			case "LIST":
				listStudents(request, response);
				break;
			
			case "ADD":
				addStudents(request, response);
				break;
			
			case "LOAD":
				loadStudent(request, response);
				break;	
				
			case "UPDATE":
				updateStudent(request, response);
				break;
				
			default:
				listStudents(request, response);
				break;
			}
			
			// method ..like we created listStudent() to create list of students

			
			// list the students ... in MVC fashion		
			listStudents(request, response);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new ServletException(e);
			
		}
			
	}



	private void loadStudent(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		// read student id from form data
		String theStudentId = request.getParameter("studentId");
		// get student from database (db util)
		Student theStudent = studentDbUtil.getStudent(theStudentId);
		// place student in the request attribute
		request.setAttribute("THE_STUDENT", theStudent);
		// send to jsp page: update-student-form.jsp
		RequestDispatcher dispatcher = request.getRequestDispatcher("/update-student-form.jsp");
		dispatcher.forward(request, response);
		
		

	}


	private void updateStudent(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		// read student info from form data
		int id = Integer.parseInt(request.getParameter("studentId"));
		String firstName = request.getParameter("firstName");
		String lastName = request.getParameter("lastName");
		String email = request.getParameter("email");
		
		// create a new student object
		Student theStudent = new Student(id, firstName, lastName, email);
		// perform update on database
		studentDbUtil.updateStudent(theStudent);
		// send them back to the "list students" page
		listStudents(request, response);
	}


	private void addStudents(HttpServletRequest request, HttpServletResponse response) throws Exception {
		//read student info from form data
		String firstName = request.getParameter("firstName");
		String lastName = request.getParameter("lastName");
		String email = request.getParameter("email");
		// create a new student object
		Student theStudent = new Student(firstName, lastName, email);
		//add the student to the database
		studentDbUtil.addStudent(theStudent);
		
		//send back to main page (the student list)
		listStudents(request, response);
	}


	private void listStudents(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// get students from db util
		List<Student> students = studentDbUtil.getStudents();
		
		//add students to the request
		request.setAttribute("STUDENT_LIST", students);
		//send to JSP page(view)
		RequestDispatcher dispatcher = request.getRequestDispatcher("/list-students.jsp");
		dispatcher.forward(request, response);
	}

}
