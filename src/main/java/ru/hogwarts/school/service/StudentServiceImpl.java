package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.exception.EntityNotFoundException;
import ru.hogwarts.school.model.Student;

import java.util.Collection;
import java.util.HashMap;
import java.util.stream.Collectors;

@Service
public class StudentServiceImpl implements StudentService {
    private final HashMap<Long, Student> students = new HashMap<>();

    private static Long idCounter = 1L;

    @Override
    public Student add(Student student) {
        students.put(idCounter++, student);
        return student;
    }

    @Override
    public Student get(Long id) {
        if (students.containsKey(id)) {

            return students.get(id);
        }
        throw new EntityNotFoundException();
    }

    @Override
    public Student update(Student student) {
        if (students.containsKey(student.getId())) {
            return students.put(student.getId(), student);
        }
        throw new EntityNotFoundException();
    }

    @Override
    public Student remove(Long id) {
        if (students.containsKey(id)) {
            return students.remove(id);
        }
        throw new EntityNotFoundException();
    }

    @Override
    public Collection<Student> getByAge(Integer age) {
        return students.values().stream()
                .filter(s-> s.getAge().equals(age))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Student> getAll() {
        return students.values();
    }
}
