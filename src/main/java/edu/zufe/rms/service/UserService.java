package edu.zufe.rms.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.zufe.rms.enums.Position;
import edu.zufe.rms.model.User;
import edu.zufe.rms.repository.UserRepository;

@Service
public class UserService {
	@Autowired
	UserRepository userRepository;
	
	public User addUser(String name, String password, String phone, String position) {
		User user = new User(name, password, phone, Position.valueOf(position));
		return userRepository.save(user);
	}
	
	public Iterable<User> findAll() {
		Iterable<User> users;
		users =  userRepository.findAll();
		return users;
	}

	public User findByAccount(String account) {
		for (User user: userRepository.findAll()) {
			if (user.getPhone().equals(account)) {
				return user;
			}
		}
		return null;
	}
	
	// Delete a user by id
	public void deleteById(Long id) {
		userRepository.deleteById(id);
	}
	
	public void delete (User user) {
		userRepository.delete(user);
	}
	
	public void deleteByPhone(String phone) {
		for (User user: userRepository.findAll()) {
			if (phone.equals(user.getPhone()))
				userRepository.delete(user);
		}
	}

	public User save(User user) {
		return userRepository.save(user);
		
	}
}
