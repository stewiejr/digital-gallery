package com.digitalgallery.security;

import com.digitalgallery.entity.User;
import com.digitalgallery.repository.UserRepository;
import java.util.UUID;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
  private final UserRepository userRepository;

  public CustomUserDetailsService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    UUID userId;
    try {
      userId = UUID.fromString(username);
    } catch (IllegalArgumentException ex) {
      throw new UsernameNotFoundException("Invalid user id");
    }
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    return new UserPrincipal(user);
  }
}
