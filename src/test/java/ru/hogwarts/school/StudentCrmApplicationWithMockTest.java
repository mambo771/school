package ru.hogwarts.school;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.hogwarts.school.controller.StudentController;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;
import ru.hogwarts.school.service.Impl.StudentServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StudentController.class)
public class StudentCrmApplicationWithMockTest {
    @Autowired
    private MockMvc mockMvc;
    @SpyBean
    private StudentServiceImpl studentService;

    @MockBean
    private StudentRepository studentRepository;
    @InjectMocks
    private StudentController studentController;


    ObjectMapper objectMapper = new ObjectMapper();
    Student testStudent;

    @BeforeEach
    void init() {
        Student testStudent1 = new Student(1L, "Harry", 15);
        Student testStudent2 = new Student(2L, "Hermione", 18);
        Student testStudent3 = new Student(3L, "Ron", 20);
        testStudent = testStudent1;
        Faculty testFaculty = new Faculty(1L, "Gryffindor", "Red");

        testStudent1.setFaculty(testFaculty);
        testStudent2.setFaculty(testFaculty);
        testStudent3.setFaculty(testFaculty);

        when(studentRepository.save(ArgumentMatchers.any(Student.class))).thenReturn(testStudent1);
        when(studentRepository.findById(ArgumentMatchers.any(Long.class))).thenReturn(Optional.of(testStudent1));
        ArrayList<Student> studentArrayList1 = new ArrayList<>(List.of(testStudent2, testStudent3));
        when(studentRepository.findAll()).thenReturn(studentArrayList1);
        when(studentRepository.findByAgeBetween(ArgumentMatchers.eq(16), ArgumentMatchers.eq(21)))
                .thenReturn(studentArrayList1);

    }

    @Test
    void postStudent() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testStudent))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("id").value(1L))
                .andExpect(jsonPath("name").value("Harry"))
                .andExpect(jsonPath("age").value(15));
        Mockito.verify(studentRepository).save(ArgumentMatchers.any(Student.class));
    }



    @Test
    void getingStudent() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("id").value(1L))
                .andExpect(jsonPath("name").value("Harry"))
                .andExpect(jsonPath("age").value(15));
        Mockito.verify(studentRepository).findById(ArgumentMatchers.anyLong());

    }

    @Test
    void putStudent() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/student/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testStudent))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("id").value(1L))
                .andExpect(jsonPath("name").value("Harry"))
                .andExpect(jsonPath("age").value(15));
        Mockito.verify(studentRepository).save(ArgumentMatchers.any(Student.class));

    }

    @Test
    void deleteStudent() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/student/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("id").value(1L))
                .andExpect(jsonPath("name").value("Harry"))
                .andExpect(jsonPath("age").value(15));
        Mockito.verify(studentRepository).deleteById(ArgumentMatchers.anyLong());

    }

    @Test
    void getFacultyByStudentId() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/faculty/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("id").value(1L))
                .andExpect(jsonPath("name").value("Gryffindor"))
                .andExpect(jsonPath("color").value("Red"));
        Mockito.verify(studentRepository).findById(ArgumentMatchers.anyLong());
    }


    @Test
    void getAllStudents() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/all?")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$").isArray())

                .andExpect(jsonPath("$[0]id").value(2L))
                .andExpect(jsonPath("$[0]name").value("Hermione"))
                .andExpect(jsonPath("$[0]age").value(18))

                .andExpect(jsonPath("$[1]id").value(3L))
                .andExpect(jsonPath("$[1]name").value("Ron"))
                .andExpect(jsonPath("$[1]age").value(20));
        Mockito.verify(studentRepository).findAll();

    }

    @Test
    void getStudentsByAge() throws Exception {


        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/by-age?startAge=16&endAge=21")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$").isArray())

                .andExpect(jsonPath("$[0]id").value(2L))
                .andExpect(jsonPath("$[0]name").value("Hermione"))
                .andExpect(jsonPath("$[0]age").value(18))

                .andExpect(jsonPath("$[1]id").value(3L))
                .andExpect(jsonPath("$[1]name").value("Ron"))
                .andExpect(jsonPath("$[1]age").value(20));
    }


}
