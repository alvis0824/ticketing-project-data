package com.cydeo.service.impl;

import com.cydeo.dto.UserDTO;
import com.cydeo.entity.User;
import com.cydeo.mapper.UserMapper;
import com.cydeo.repository.UserRepository;
import com.cydeo.service.UserService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public List<UserDTO> listAllUsers() {

        List<User> userList = userRepository.findAll(Sort.by("firstName"));

        return userList.stream().map(userMapper::convertToDto).collect(Collectors.toList());
    }

    @Override
    public UserDTO findByUserName(String username) {

        return userMapper.convertToDto(userRepository.findByUserName(username));

    }

    @Override
    public void save(UserDTO user) {

        userRepository.save(userMapper.convertToEntity(user));

    }

    @Override
    public void deleteByUserName(String username) {

        userRepository.deleteByUserName(username);

    }

    @Override
    public UserDTO update(UserDTO user) {
        // Find current user
        User user1 = userRepository.findByUserName(user.getUserName());
        // Map update user dto to entity object
        User convertedUser = userMapper.convertToEntity(user);
        // Set id to the converted user
        convertedUser.setId(user1.getId());
        // save the updated user in the DB
        userRepository.save(convertedUser);

        return findByUserName(user.getUserName());
    }

    @Override
    public void delete(String username) {
        // go to DB and get that user with username
        User user = userRepository.findByUserName(username);

        // change the is_deleted field to true
        user.setIsDeleted(true);
        userRepository.save(user);

        // save the object in the DB
    }

    @Override
    public List<UserDTO> listAllByRole(String role) {

        List<User> users = userRepository.

        return null;
    }
}
