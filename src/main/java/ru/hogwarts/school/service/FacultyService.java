package ru.hogwarts.school.service;

import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;

import java.util.Collection;

public interface FacultyService {
    Faculty add(Faculty faculty);

    Faculty get(Long id);

    Faculty update(Faculty faculty);

    Faculty remove(Long id);

    Collection<Faculty> getByColorOrName(String name, String color);

    Collection<Faculty> getAll();


    Collection<Student> getStudents(Long facultyId);
}
