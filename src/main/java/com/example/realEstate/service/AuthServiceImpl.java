package com.example.realEstate.service;

import com.example.realEstate.entity.Admin;
import com.example.realEstate.entity.Customer;
import com.example.realEstate.entity.Role;
import com.example.realEstate.entity.User;
import com.example.realEstate.entity.dto.request.LoginRequest;
import com.example.realEstate.entity.dto.request.RefreshTokenRequest;
import com.example.realEstate.entity.dto.request.SignupRequest;
import com.example.realEstate.entity.dto.response.LoginResponse;
import com.example.realEstate.entity.enums.RoleType;
import com.example.realEstate.entity.enums.UserStatus;
import com.example.realEstate.repository.UserRepository;
import com.example.realEstate.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        Authentication result = null;
        try {
            result = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException(e.getMessage());
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(result.getName());
        final String accessToken = jwtUtil.generateToken(userDetails);
        final String refreshToken = jwtUtil.generateRefreshToken(loginRequest.getEmail());
        User user = userRepository.findByEmail(loginRequest.getEmail());
        var loginResponse = new LoginResponse(accessToken, refreshToken, user.getFirstName(), user.getId());
        return loginResponse;
    }

    @Override
    public LoginResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        boolean isRefreshTokenValid = jwtUtil.validateToken(refreshTokenRequest.getRefreshToken());
        if (isRefreshTokenValid) {
            // (check the expiration of the accessToken when request sent, if the is recent according to
            //  issue Date, then accept the renewal)
            var isAccessTokenExpired = jwtUtil.isTokenExpired(refreshTokenRequest.getAccessToken());
            if(isAccessTokenExpired)
                System.out.println("ACCESS TOKEN IS EXPIRED"); //  Renew is this case
            else
                System.out.println("ACCESS TOKEN IS NOT EXPIRED");
            final String accessToken = jwtUtil.doGenerateToken(  jwtUtil.getSubject(refreshTokenRequest.getRefreshToken()));
            var loginResponse = new LoginResponse(accessToken, refreshTokenRequest.getRefreshToken());
            // (OPTIONAL) When to renew the refresh token?
            return loginResponse;
        }
        return new LoginResponse();
    }

//    @Override
//    public User signup(SignupRequest signupRequest) {
//        String firstName=  signupRequest.getFirstName();
//        String lastName = signupRequest.getLastName();
//        String email = signupRequest.getEmail();
//        String password = passwordEncoder.encode(signupRequest.getPassword());
//        RoleType roleType = signupRequest.getRole();
//        User user;
//
//
//        Role role = new Role(roleType);
//        if(roleType.name().equals("CUSTOMER")){
//            System.out.println(roleType.name());
//            user = new Customer(firstName, lastName, email, role, UserStatus.PENDING);
//
//        }
//        User user =
//        return userRepository.save();
//    }
}
