package com.sangwon.authflowwithsecurity.service;

import com.sangwon.authflowwithsecurity.dto.UserCreationRequestDto;
import com.sangwon.authflowwithsecurity.entity.User;
import com.sangwon.authflowwithsecurity.exception.AccountAlreadyExistsException;
import com.sangwon.authflowwithsecurity.repository.UserRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.authentication.password.CompromisedPasswordException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CompromisedPasswordChecker compromisedPasswordChecker;

    public void createUser(@NonNull final UserCreationRequestDto userCreationRequestDto) {
        final var emailId = userCreationRequestDto.getEmailId();
        final var userAccountExistsWithEmailId = userRepository.existsByEmailId(emailId);

        if (Boolean.TRUE.equals(userAccountExistsWithEmailId)) {
            throw new AccountAlreadyExistsException("Account with provided email-id already exists");
        }

        final var plainTextPassword = userCreationRequestDto.getPassword();
        final var isPasswordCompromised = compromisedPasswordChecker.check(plainTextPassword).isCompromised();
        if (Boolean.TRUE.equals(isPasswordCompromised)) {
            throw new CompromisedPasswordException("The provided password is compromised and cannot be used for account creation");

        }
        final var user = new User();
        final var encodedPassword = passwordEncoder.encode(plainTextPassword);
        user.setFirstName(userCreationRequestDto.getFirstName());
        user.setLastName(userCreationRequestDto.getLastName());
        user.setEmailId(userCreationRequestDto.getEmailId());
        user.setPassword(encodedPassword);

        userRepository.save(user);
    }
}
