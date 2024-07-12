package ru.hogwarts.school;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.datafaker.Faker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

import javax.validation.constraints.Null;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

public class StudentControllerTestRestTemplateTest {
    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private FacultyRepository facultyRepository;

    private final Faker faker = new Faker();
    private List<Student> students = new ArrayList<>(10);
    private List<Faculty> faculties = new ArrayList<>(4);


    @AfterEach
    public void afterEach() {
        studentRepository.deleteAll();
        facultyRepository.deleteAll();
    }

    @BeforeEach
    public void beforeEach() {
        Faculty faculty1 = createFaculty();
        Faculty faculty2 = createFaculty();

        createStudents(faculty1);
        createStudents(faculty2);

    }

    private Faculty createFaculty() {
        Faculty faculty = new Faculty();
        faculty.setName(faker.harryPotter().house());
        faculty.setColor(faker.color().name());
        return facultyRepository.save(faculty);
    }


    private void createStudents(Faculty faculty) {
        Student student;
        List<Student> students = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            student = new Student();
            student.setFaculty(faculty);
            student.setName(faker.harryPotter().character());
            student.setAge(faker.random().nextInt(11, 18));
            students.add(student);
        }
        this.students.addAll(students);
        studentRepository.saveAll(students);
    }


    public String buildUrl(String uriStartsWithSlash) { // "http://localhost:8080/students"
        return "http://localhost:" + port + uriStartsWithSlash;
    }

    @Test
    public void createStudent() {
        Student student = new Student();
        student.setName(faker.harryPotter().character());
        student.setAge(faker.random().nextInt(11, 18));

        ResponseEntity<Student> responseEntity = testRestTemplate.postForEntity(buildUrl(
                        "/student"),
                student,
                Student.class
        );

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody()).usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(student);
        assertThat(responseEntity.getBody().getId()).isNotNull();

        Optional<Student> fromDb = studentRepository.findById(responseEntity.getBody().getId());
        assertThat(fromDb).isPresent();
        assertThat(fromDb.get())
                .usingRecursiveComparison()
                .isEqualTo(responseEntity.getBody());
    }


    @Test
    public void updateStudent() {
        Student student = new Student();
        student.setName(faker.harryPotter().character());
        student.setAge(faker.random().nextInt(11, 18));
        studentRepository.save(student);
        student.setName(faker.harryPotter().character());
        student.setAge(faker.random().nextInt(11, 18));

        ResponseEntity<Student> responseEntity = testRestTemplate.postForEntity(buildUrl(
                        "/student"),
                student,
                Student.class
        );
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().getName()).isEqualTo(student.getName());
        assertThat(responseEntity.getBody().getAge()).isEqualTo(student.getAge());

    }

    private void assertWatResponseEntityIsCorrect(ResponseEntity<Student> responseEntity, Student student) {
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().getName()).isEqualTo(student.getName());
        assertThat(responseEntity.getBody().getAge()).isEqualTo(student.getAge());
    }

    @Test
    public void deleteStudent() {
        Student student = new Student();
        student.setName(faker.harryPotter().character());
        student.setAge(faker.random().nextInt(11, 18));
        Student save = studentRepository.save(student);


        testRestTemplate.delete(buildUrl(
                "/student/" + save.getId()));

        assertThat(studentRepository.getById(save.getId())).isNotNull();


    }

    @Test
    public void getStudentById() {
        Student student = new Student();
        student.setName(faker.harryPotter().character());
        student.setAge(faker.random().nextInt(11, 18));
        Student save = studentRepository.save(student);

        ResponseEntity<Student> responseEntity = testRestTemplate.getForEntity(buildUrl(
                        "/student" + save.getId()),
                Student.class
        );

        assertThat(studentRepository.getById(save.getId())).isNotNull();


    }


    @Test
    public void getStudentByAge() {
        int startAge = faker.random().nextInt(11, 18);
        int endAge = faker.random().nextInt(startAge, 20);

        Collection<Student> expected = students.stream()
                .filter(student -> student.getAge() >= startAge && student.getAge() <= endAge)
                .toList();


        ResponseEntity<Collection<Student>> responseEntity = testRestTemplate.exchange(buildUrl(
                        "/student/by-age?startAge={startAge}&endAge={endAge}"),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Collection<Student>>() {
                },
                Map.of("startAge", startAge, "endAge", endAge)
        );
        Collection<Student> actual = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(actual).isNotNull();

        assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);

    }

    @Test
    public void getAllStudent() {
        Student student = new Student();
        student.setName(faker.harryPotter().character());
        student.setAge(faker.random().nextInt(11, 18));
        Student save = studentRepository.save(student);


        ResponseEntity<Collection<Student>> responseEntity = testRestTemplate.exchange(buildUrl(
                        "/student/all?"),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Collection<Student>>() {
                }
        );
        Collection<Student> actual = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(actual).isNotNull();


    }

    @Test
    public void getFacultyByIdFinal() {
        Student student = new Student();
        student.setName(faker.harryPotter().character());
        student.setAge(faker.random().nextInt(11, 18));
        Student save = studentRepository.save(student);

        ResponseEntity<Faculty> responseEntity = testRestTemplate.getForEntity(buildUrl(
                        "/faculty" + save.getId()),
                Faculty.class
        );
        assertThat(save.getFaculty()).isEqualTo(responseEntity.getBody());


    }

    @Test
    public void createAddedFaculty() {
        Faculty faculty = new Faculty();
        faculty.setName(faker.harryPotter().house());
        faculty.setColor(faker.color().name());
        Faculty saveFaculty = facultyRepository.save(faculty);

        Student student = new Student();
        student.setName(faker.harryPotter().character());
        student.setAge(faker.random().nextInt(11, 18));
        student.setFaculty(saveFaculty);
        Student saveStudents = studentRepository.save(student);

        ResponseEntity<Faculty> responseEntity = testRestTemplate.postForEntity(buildUrl(
                        "/faculty"),
                faculty,
                Faculty.class
        );

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody()).usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(faculty);
        assertThat(responseEntity.getBody().getId()).isNotNull();

        Optional<Faculty> fromDb = facultyRepository.findById(responseEntity.getBody().getId());
        assertThat(fromDb).isPresent();
        assertThat(fromDb.get())
                .usingRecursiveComparison()
                .isEqualTo(responseEntity.getBody());
    }

    @Test
    public void updateFaculty() {
        Faculty faculty = new Faculty();
        faculty.setName(faker.harryPotter().house());
        faculty.setColor(faker.color().name());
        facultyRepository.save(faculty);
        faculty.setName(faker.harryPotter().house());
        faculty.setColor(faker.color().name());

        ResponseEntity<Faculty> responseEntity = testRestTemplate.postForEntity(buildUrl(
                        "/faculty"),
                faculty,
                Faculty.class
        );

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().getName()).isEqualTo(faculty.getName());
        assertThat(responseEntity.getBody().getColor()).isEqualTo(faculty.getColor());

    }


    @Test
    public void deleteFaculty() {
        Faculty faculty = new Faculty();
        faculty.setName(faker.harryPotter().house());
        faculty.setColor(faker.color().name());
        Faculty save = facultyRepository.save(faculty);


        testRestTemplate.delete(buildUrl(
                "/faculty/" + save.getId()));

        assertThat(facultyRepository.getById(save.getId())).isNotNull();

    }

    @Test
    public void getFacultyById() {
        Faculty faculty = new Faculty();
        faculty.setName(faker.harryPotter().house());
        faculty.setColor(faker.color().name());
        Faculty save = facultyRepository.save(faculty);


        ResponseEntity<Faculty> responseEntity = testRestTemplate.getForEntity(buildUrl(
                        "/faculty" + save.getId()),
                Faculty.class
        );

        assertThat(facultyRepository.getById(save.getId())).isNotNull();

    }


    @Test
    public void getFacultyByColor() {

        String name = faker.random().toString();
        String color = faker.random().toString();

        Collection<Faculty> expect = new ArrayList<>(faculties);

        ResponseEntity<Collection<Faculty>> responseEntity = testRestTemplate.exchange(buildUrl(
                        "/faculty/by-color?name={name}&color={color}"),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Collection<Faculty>>() {
                },
                Map.of("name", name, "color", color)
        );
        Collection<Faculty> actual = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(actual).isNotNull();

        assertThat(actual).containsExactlyInAnyOrderElementsOf(expect);


    }

    @Test
    public void getAllFaculty() {
        Faculty faculty = new Faculty();
        faculty.setName(faker.harryPotter().house());
        faculty.setColor(faker.color().name());
        Faculty save = facultyRepository.save(faculty);

        ResponseEntity<Collection<Faculty>> responseEntity = testRestTemplate.exchange(buildUrl(
                        "/faculty/all?"),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Collection<Faculty>>() {
                }
        );
        Collection<Faculty> actual = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(actual).isNotNull();
    }

    @Test
    public void getStudentsByFaculty() {
        Faculty faculty = new Faculty();
        faculty.setName(faker.harryPotter().house());
        faculty.setColor(faker.color().name());
        Faculty saveFaculty = facultyRepository.save(faculty);

        Student student = new Student();
        student.setName(faker.harryPotter().character());
        student.setAge(faker.random().nextInt(11, 18));
        student.setFaculty(saveFaculty);
        Student saveStudents = studentRepository.save(student);

        System.out.println(saveStudents);
        System.out.println(saveFaculty);

        studentRepository.findById(saveStudents.getId());
        facultyRepository.findById(saveFaculty.getId());
        ResponseEntity<Student> responseEntity = testRestTemplate.getForEntity(buildUrl(
                        "/students" + saveStudents.getId()),
                Student.class
        );
        assertThat(saveStudents.getFaculty()).isEqualTo(responseEntity.getBody());


    }

}