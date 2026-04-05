package com.learninglogs.dao;

import com.learninglogs.entity.User;

/**
 * User DAO Interface — defines database operations for users.
 * New for Week 5: supports registration and login.
 */
public interface UserDao {

    boolean insertUser(User user);
    User findByUsername(String username);
    User findByEmail(String email);
}
