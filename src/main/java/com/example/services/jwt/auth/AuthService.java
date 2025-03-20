package com.example.services.jwt.auth;

import com.dto.SignupRequest;
import com.dto.UserDto;

public interface AuthService {
	
	UserDto createUser(SignupRequest signupRequest);
	
	 Boolean hasUserWithEmail(String email);

}
