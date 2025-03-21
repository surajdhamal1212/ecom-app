package com.example.services.jwt.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.dto.SignupRequest;
import com.dto.UserDto;
import com.example.entity.User;
import com.example.enums.UserRole;
import com.example.repository.UserRepository;

import jakarta.annotation.PostConstruct;

@Service
public class AuthServiceImpl implements AuthService{
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	private User user;
	
	@Autowired
	UserDto userDto;
	
	public UserDto createUser(SignupRequest signupRequest) {
		 user.setEmail(signupRequest.getEmail());
		 user.setName(signupRequest.getName());
		 user.setPassword(new BCryptPasswordEncoder().encode(signupRequest.getPassword()));
		 user.setRole(UserRole.CUSTOMER);
		 User createdUser = userRepository.save(user);
		 
		 userDto.setId(createdUser.getId());
		 
		 return userDto;
	}
	
	public Boolean hasUserWithEmail(String email) {
		return userRepository.findFirstByEmail(email).isPresent();
		
	}
	
	@PostConstruct
	public void createAdminAccount() {
		User adminAccount = userRepository.findByRole(UserRole.ADMIN);
		if(null==adminAccount) {
			User user = new User();
			user.setEmail("admin@test.com");
			user.setName("admin");
			user.setRole(UserRole.ADMIN);
			user.setPassword(new BCryptPasswordEncoder().encode("admin"));
			userRepository.save(user);
		}
	}

}
