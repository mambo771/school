package ru.hogwarts.school.service;

import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.Collection;

public interface StudentService {
    Student add(Student student);

    Student get(Long id);

    Student update(Student student);

    Student remove(Long id);

    Collection<Student> getByAge(Integer startAge, Integer endAge);

    Collection<Student> getAll();


    Faculty getFacultyOfStudent(Long studentId);
}
