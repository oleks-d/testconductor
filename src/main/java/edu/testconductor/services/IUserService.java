package edu.testconductor.services;

import edu.testconductor.domain.User;

public interface IUserService {
    public User findUserByEmail(String email) ;
    public User saveUser(User user);
}
