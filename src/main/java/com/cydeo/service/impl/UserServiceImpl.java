package com.cydeo.service.impl;

import com.cydeo.dto.ProjectDTO;
import com.cydeo.dto.TaskDTO;
import com.cydeo.dto.UserDTO;
import com.cydeo.entity.User;
import com.cydeo.mapper.UserMapper;
import com.cydeo.repository.UserRepository;
import com.cydeo.service.ProjectService;
import com.cydeo.service.TaskService;
import com.cydeo.service.UserService;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final ProjectService projectService;
    private final TaskService taskService;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, @Lazy ProjectService projectService, @Lazy TaskService taskService) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.projectService = projectService;
        this.taskService = taskService;
    }

    @Override
    public List<UserDTO> listAllUsers() {
        List<User> userList = userRepository.findAllByIsDeletedOrderByFirstNameDesc(false);
        return userList.stream().map(userMapper::convertToDto).collect(Collectors.toList());
    }

    @Override
    public UserDTO findByUserName(String username) {

        return userMapper.convertToDto(userRepository.findByUserNameAndIsDeleted(username, false));

    }

    @Override
    public void save(UserDTO user) {

        userRepository.save(userMapper.convertToEntity(user));

    }

//    @Override
//    public void deleteByUserName(String username) {
//
//        userRepository.deleteByUserName(username);
//
//    }

    @Override
    public UserDTO update(UserDTO user) {
        // Find current user
        User user1 = userRepository.findByUserNameAndIsDeleted(user.getUserName(),false);
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
        User user = userRepository.findByUserNameAndIsDeleted(username,false);

        if(checkIfUserCanBeDeleted(user)) {
            // change the is_deleted field to true
            user.setIsDeleted(true);
            user.setUserName(user.getUserName() + "-" + user.getId());
            // save the object in the DB
            userRepository.save(user);
        }
    }

    @Override
    public List<UserDTO> listAllByRole(String role) {
        List<User> users = userRepository.findByRoleDescriptionIgnoreCaseAndIsDeleted(role,false);
        return users.stream().map(userMapper::convertToDto).collect(Collectors.toList());
    }

    private boolean checkIfUserCanBeDeleted(User user){

        switch (user.getRole().getDescription()){

            case "manager":
                List<ProjectDTO> projectDTOList = projectService.listAllNonCompletedByAssignedManager(userMapper.convertToDto(user));
                return projectDTOList.size() == 0;
            case "employee":
                List<TaskDTO> taskDTOList = taskService.listAllNonCompletedByAssignedEmployee(userMapper.convertToDto(user));
                return taskDTOList.size() == 0;
            default:
                return true;

        }
    }
}
