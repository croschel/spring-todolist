package com.caique.todolist.user;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import at.favre.lib.crypto.bcrypt.BCrypt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;


@RestController
@RequestMapping("/user")
public class UserController {

  @Autowired
  private IUserRepository userRepository;
  @PostMapping("/create")
  public ResponseEntity create(@RequestBody UserModel userPayload) {
    var user = this.userRepository.findByUsername(userPayload.getUsername());
    if (user != null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User already exists!");
    }
    var encryptedPass = BCrypt.withDefaults().hashToString(12, userPayload.getPassword().toCharArray());
    userPayload.setPassword(encryptedPass);
    var userCreated = this.userRepository.save(userPayload);
    return ResponseEntity.status(HttpStatus.CREATED).body(userCreated);
  }
}
