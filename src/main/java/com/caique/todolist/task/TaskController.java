package com.caique.todolist.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/task")
public class TaskController {
  
  @Autowired
  private ITaskRepository taskRepository;
  
  @PostMapping("/create")
  public TaskModel create (@RequestBody TaskModel taskPayload) {
    var task = this.taskRepository.save(taskPayload);
    return task;
  }
}
