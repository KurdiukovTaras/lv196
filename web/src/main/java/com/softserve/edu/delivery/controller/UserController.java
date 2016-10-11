package com.softserve.edu.delivery.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.softserve.edu.delivery.dto.UserProfileDto;
import com.softserve.edu.delivery.dto.UserProfileFilterDto;
import com.softserve.edu.delivery.service.UserService;

@RestController
@RequestMapping(path = "users")
public class UserController {
	
	   @Autowired
	   UserService userService;

		Logger logger = LoggerFactory.getLogger(UserController.class.getName());

	    @RequestMapping(path = "all", method = RequestMethod.GET)
	    List<UserProfileDto> allUsers() {
			logger.info("Method UserController.users()");
	        return userService.getAllUsers();
	    }
	    
	    @RequestMapping(path = "email", method = RequestMethod.GET)
	    UserProfileDto getUser(@RequestParam("email") String email) {
			logger.info("Method UserController.getUser()");
	        return userService.getUser(email);
	    }
	    
	    @RequestMapping(path = "change-status", method = RequestMethod.PUT)
	    UserProfileDto changeUserStatus(@RequestParam("email") String email, @RequestParam("status") String value) {
			logger.info("Method UserController.changeUserStatus()");
			Boolean status = null;
			try{
				status = Boolean.parseBoolean(value);
			} catch(IllegalArgumentException e) {
	            e.printStackTrace();
			}
	        return userService.changeUserStatus(email, status);
	    }
	    
	    @RequestMapping(path = "filter", method = RequestMethod.GET)
	    List<UserProfileDto> filterAllUsers(@RequestBody UserProfileFilterDto filter ) {
			logger.info("Method UserController.filterAllUsers()");
			return userService.filterAllUsers(filter);
	    }
}