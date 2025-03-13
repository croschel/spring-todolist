package com.caique.todolist.task;

import java.time.LocalDateTime;
import java.util.UUID;

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
}
