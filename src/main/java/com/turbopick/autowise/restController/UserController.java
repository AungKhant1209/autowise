//package com.turbopick.autowise.restController;
//
//import com.turbopick.autowise.model.User;
//import com.turbopick.autowise.service.UserService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//import java.util.Optional;
//
//@RestController
//@RequestMapping("/users")
//public class UserController {
//
//    @Autowired
//    private UserService userService;
//
//    @PostMapping
//    public User saveUser(@RequestBody User user) {
//        return userService.saveUser(user);
//    }
//
//    @GetMapping
//    public List<User> getAllUsers() {
//        return userService.getAllUsers();
//    }
//
//    @GetMapping("/{id}")
//    public Optional<User> getUserById(@PathVariable int id) {
//        return userService.findUserById(id);
//    }
//
//    @DeleteMapping("/{id}")
//    public void deleteUser(@PathVariable int id) {
//        userService.deleteUserById(id);
//    }
//}
