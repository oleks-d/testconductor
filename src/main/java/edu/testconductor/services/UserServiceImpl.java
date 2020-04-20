package edu.testconductor.services;

import edu.testconductor.domain.Role;
import edu.testconductor.domain.User;
//import edu.testconductor.repos.RoleRepo;
import edu.testconductor.repos.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;

//@Service
public class UserServiceImpl implements IUserService {
    private UserRepo userRepository;
    //private RoleRepo roleRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

//    @Autowired
//    public UserServiceImpl(UserRepo userRepository, RoleRepo roleRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
//        this.userRepository = userRepository;
        //this.roleRepository = roleRepository;
//        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
//    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User saveUser(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setActive(true);
        //Role userRole = roleRepository.findByRole("ADMIN");
        //user.setRoles(new HashSet<Role>(Arrays.asList(userRole)));
        return userRepository.save(user);
    }
}