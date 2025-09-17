package ru.hogwarts.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.model.StudentRQ;

import java.util.Collection;
import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Collection<Student> findByAgeBetween (Integer startAge, Integer endAge);
    @Query(value = "SELECT COUNT(*) AS total_students FROM students", nativeQuery = true)
    List<StudentRQ>getTotal_Students();
}
