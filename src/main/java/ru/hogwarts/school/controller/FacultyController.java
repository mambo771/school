package ru.hogwarts.school.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.FacultyService;
import ru.hogwarts.school.service.StudentService;

import java.util.Collection;

@RestController
@RequestMapping("faculty")
@Tag(name= "API для работы с факультетами")
public class FacultyController {
    private final FacultyService service;

    public FacultyController(FacultyService service) {
        this.service = service;
    }

    @PostMapping
    @Operation (summary = "Создание факультетов")
    public ResponseEntity<Faculty> create(@RequestBody Faculty faculty) {
        Faculty addedFaculty = service.add(faculty);
        return ResponseEntity.ok(addedFaculty);
    }
    @PutMapping
    @Operation (summary = "Обновление факультетов")
    public ResponseEntity<Faculty> update (@RequestBody Faculty faculty) {
        Faculty updatedFaculty = service.update(faculty);
        return ResponseEntity.ok(updatedFaculty);
    }
    @DeleteMapping("{id}")
    @Operation (summary = "Удаление факультетов")
    public ResponseEntity <Faculty> remove (@PathVariable Long id){
        Faculty deletedFaculty = service.remove(id);
        return ResponseEntity.ok(deletedFaculty);
    }
    @GetMapping("{id}")
    @Operation (summary = "Получение факультетов по id")
    public ResponseEntity <Faculty> get (@PathVariable Long id){
        Faculty faculty = service.get(id);
        return ResponseEntity.ok(faculty);
    }
    @GetMapping("by-color")
    @Operation (summary = "Получение факультетов по цвету ")
    public ResponseEntity <Collection<Faculty>> getByColor (@RequestParam String color){
        Collection<Faculty> faculties= service.getByColor(color);
        return ResponseEntity.ok(faculties);
    }
    @GetMapping("all")
    @Operation (summary = "Получение всех факультетов")
    public ResponseEntity <Collection<Faculty>> getAll(){
        Collection<Faculty> faculties= service.getAll();
        return ResponseEntity.ok(faculties);
    }
}

