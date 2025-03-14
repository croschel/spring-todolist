package com.caique.todolist.task;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.caique.todolist.utils.Utils;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
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
    // Set user-related fields from the existing task and request
    UUID userIdFromReq = (UUID) request.getAttribute("userId");
    if (!userIdFromReq.equals(task.get().getUserId())) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("This task does not belongs to you!");
    }
    TaskModel existingTask = task.get();
    existingTask.setUserId(userIdFromReq);
    existingTask.setId(id);

    // Use BeanUtils.copyProperties to update fields selectively
    BeanUtils.copyProperties(taskPayload, existingTask, Utils.getNullPropertyNames(taskPayload));

    var response = this.taskRepository.save(existingTask);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }
}
