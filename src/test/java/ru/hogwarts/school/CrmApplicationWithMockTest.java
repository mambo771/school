package ru.hogwarts.school;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.hogwarts.school.controller.FacultyController;
import ru.hogwarts.school.controller.StudentController;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;
import ru.hogwarts.school.service.Impl.FacultyServiceImpl;
import ru.hogwarts.school.service.Impl.StudentServiceImpl;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
public class CrmApplicationWithMockTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentRepository studentRepository;

    @MockBean
    private FacultyRepository facultyRepository;

    @SpyBean
    private StudentServiceImpl studentService;

    @SpyBean
    private FacultyServiceImpl facultyService;


    @InjectMocks
    private StudentController studentController;

    @InjectMocks
    private FacultyController facultyController;

    @Test
    public void createSaveStudent() throws Exception {
        final String name = "Garry";
        final Integer age = 18;
        final Long id = 1L;


        JSONObject studentObject = new JSONObject();
        studentObject.put("name", name);
        studentObject.put("age", age);
        studentObject.put("id", id);


        Student createStudents = new Student();
        createStudents.setName(name);
        createStudents.setAge(age);
        createStudents.setId(id);


        when(studentRepository.save(any(Student.class))).thenReturn(createStudents);
        when(studentRepository.findById(any(Long.class))).thenReturn(Optional.of(createStudents));

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/student"+ id)
                        .content(studentObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.age").value(age));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.age").value(age));
    }


}
