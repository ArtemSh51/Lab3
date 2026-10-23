package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.entity.User;
import org.example.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;

    public List<User> findAll() {
        return repository.findAll();
    }

    public User findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    public User create(User user) {
        return repository.save(user);
    }

    public User update(Long id, User details) {
        User user = findById(id);
        user.setName(details.getName());
        user.setEmail(details.getEmail());
        return repository.save(user);
    }

    public void delete(Long id) {
        findById(id);
        repository.deleteById(id);
    }
}