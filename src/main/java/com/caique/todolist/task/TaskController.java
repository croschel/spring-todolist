package com.caique.todolist.task;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;



@RestController
@RequestMapping("/task")
public class TaskController {
  
  @Autowired
  private ITaskRepository taskRepository;
  
  @PostMapping("/create")
  public ResponseEntity create (@RequestBody TaskModel taskPayload, HttpServletRequest request) {
    taskPayload.setUserId((UUID) request.getAttribute("userId"));
    var currentDate = LocalDateTime.now();
    if (currentDate.isAfter(taskPayload.getStartAt()) || currentDate.isAfter(taskPayload.getEndAt())) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Start date and end date must be in the future");
    }
    if (taskPayload.getStartAt().isAfter(taskPayload.getEndAt())) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Start date must be before end date");
    }

    var task = this.taskRepository.save(taskPayload);
    return ResponseEntity.status(HttpStatus.CREATED).body(task);
  }

  @GetMapping("/list")
  public ResponseEntity list (HttpServletRequest request) {
    var userId = (UUID) request.getAttribute("userId");
    var tasks = this.taskRepository.findByUserId((UUID) userId);
    return ResponseEntity.status(HttpStatus.OK).body(tasks);
  }

  @PutMapping("/update/{id}")
  public ResponseEntity update (@PathVariable UUID id, @RequestBody TaskModel taskPayload , HttpServletRequest request) {
    var task = this.taskRepository.findById(id);
    if (task.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task not found");
    }

    TaskModel existingTask = task.get();

    // Set user-related fields from the existing task and request
    UUID userId = (UUID) request.getAttribute("userId");
    if (userId != null) {
      existingTask.setUserId(userId);
    }

    existingTask.setId(id);

    // Use BeanUtils.copyProperties to update fields selectively
    BeanUtils.copyProperties(taskPayload, existingTask, getNullPropertyNames(taskPayload));

    var response = this.taskRepository.save(existingTask);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  private String[] getNullPropertyNames(Object source) {
    return Arrays.stream(org.springframework.beans.BeanUtils.getPropertyDescriptors(source.getClass()))
        .map(propertyDescriptor -> propertyDescriptor.getName())
        .filter(propertyName -> {
          try {
            var readMethod = org.springframework.beans.BeanUtils.getPropertyDescriptor(source.getClass(), propertyName).getReadMethod();
            if (readMethod == null) {
              return false;
            }
            Object value = readMethod.invoke(source);
            return value == null;
          } catch (Exception e) {
            return false;
          }
        })
        .toArray(String[]::new);
  }
}
