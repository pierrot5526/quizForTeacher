package com.cnam.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.cnam.quiz.common.dto.UserDto;
import com.cnam.quiz.server.domain.user.User;
import com.cnam.quiz.server.domain.user.UserDao;
import com.cnam.quiz.server.service.user.UserService;

@Component
public class RestAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
 
 @Autowired
 private UserDao userDao;
 
 
 @Override
 public void onAuthenticationSuccess(HttpServletRequest request, 
  HttpServletResponse response, Authentication authentication)
		  throws ServletException, IOException {
	 		User user =  userDao.findUserByMail(authentication.getName());
	 		SecurityUtils.sendResponse(response, HttpServletResponse.SC_OK, user);
 }
}