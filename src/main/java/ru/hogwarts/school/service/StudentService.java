package ru.hogwarts.school.service;

import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.model.StudentRQ;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.Collection;
import java.util.List;

public interface StudentService {
    Student add(Student student);

    Student get(Long id);

    Student update(Student student);

    Student remove(Long id);

    Collection<Student> getByAge(Integer startAge, Integer endAge);

    Collection<Student> getAll();

    List<StudentRQ> getTotal_Students();

    Faculty getFacultyOfStudent(Long studentId);
}
