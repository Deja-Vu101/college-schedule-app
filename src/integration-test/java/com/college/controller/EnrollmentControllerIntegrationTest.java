package com.college.controller;

import com.college.*;
import com.college.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(
    classes = MainApp.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application.properties")
@Transactional
public class EnrollmentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StudentService studentService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private DepartmentService departmentService;

    private Student student;
    private Course course;

    @BeforeEach
    void setUp() {
        Department department = new Department();
        department.setName("Computer Science");
        department.setLocation("Building A");
        departmentService.save(department);

        student = new Student();
        student.setFirstName("Alice");
        student.setLastName("Smith");
        student.setDepartment(department);
        studentService.save(student);

        course = new Course();
        course.setCourseName("Java Programming");
        course.setCredits(3);
        course.setDepartment(department);
        courseService.save(course);
    }

    @Test
    void showEnrollmentForm_ShouldDisplayForm() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/enrollment/enroll"))
                .andExpect(status().isOk())
                .andExpect(view().name("enrollment/enroll-form"))
                .andExpect(model().attributeExists("enrollment"))
                .andExpect(model().attributeExists("students"))
                .andExpect(model().attributeExists("courses"));
    }

    @Test
    void enroll_ShouldCreateEnrollmentAndRedirect() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/enrollment/enroll")
                .param("student", student.getStudentId().toString())
                .param("course", course.getCourseId().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/schedule/list"));
    }

    @Test
    void enroll_WithInvalidData_ShouldReturnToForm() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/enrollment/enroll")
                .param("student", "")
                .param("course", ""))
                .andExpect(status().is3xxRedirection());
    }
}