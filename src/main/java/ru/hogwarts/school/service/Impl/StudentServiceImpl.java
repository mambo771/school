package ru.hogwarts.school.service.Impl;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.exception.EntityNotFoundException;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;
import ru.hogwarts.school.service.StudentService;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;

    public StudentServiceImpl(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    public Student add(Student student) {
        return studentRepository.save(student);
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
