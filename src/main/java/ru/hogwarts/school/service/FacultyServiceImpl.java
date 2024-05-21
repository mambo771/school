package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.exception.EntityNotFoundException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;

import java.util.Collection;
import java.util.HashMap;
import java.util.stream.Collectors;

@Service
public class FacultyServiceImpl implements FacultyService{
    private final HashMap<Long, Faculty> faculties = new HashMap<>();
    private static Long idCounter = 1L;
    @Override
    public Faculty add(Faculty faculty) {
        faculties.put(idCounter++, faculty);
        return faculty;
    }

    @Override
    public Faculty get(Long id) {
        if (faculties.containsKey(id)) {

            return faculties.get(id);
        }
        throw new EntityNotFoundException();
    }

    @Override
    public Faculty update(Faculty faculty) {
        if (faculties.containsKey(faculty.getId())) {
            return faculties.put(faculty.getId(), faculty);
        }
        throw new EntityNotFoundException();
    }

    @Override
    public Faculty remove(Long id) {
        if (faculties.containsKey(id)) {
            return faculties.remove(id);
        }
        throw new EntityNotFoundException();
    }

    @Override
    public Collection<Faculty> getByColor(String color) {
        return faculties.values().stream()
                .filter(s-> s.getColor().equals(color))
                .collect(Collectors.toList());
    }


    @Override
    public Collection<Faculty> getAll() {
        return faculties.values();
    }
}
