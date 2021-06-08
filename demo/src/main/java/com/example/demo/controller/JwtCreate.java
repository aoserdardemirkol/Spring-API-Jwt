package com.example.demo.controller;

import com.example.demo.payload.response.JwtResponse;
import com.example.demo.payload.resquest.JwtRequest;
import com.example.demo.security.jwt.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import static org.springframework.http.HttpStatus.OK;

@RestController
@CrossOrigin
@Api(value = "Jwt Create API documentation")
public class JwtCreate {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserDetailsService jwtInMemoryUserDetailsService;

    @GetMapping("/ctoken")
    @ApiOperation(value = "Token Oluşturma", authorizations = { @Authorization(value="apiKey") })
    public ResponseEntity<?> cToken() throws Exception{
        authenticate("username", "password");

        final String token = jwtUtil.generateToken("username");
        return new ResponseEntity(new JwtResponse(token), OK);
    }

    private void authenticate(String username, String password) throws Exception {
        Objects.requireNonNull(username);
        Objects.requireNonNull(password);

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }

    @PostMapping("/hello")
    @ApiOperation(value = "Deneme", authorizations = { @Authorization(value="apiKey") })
    public ResponseEntity<?> deneme(){
        return new ResponseEntity("Hello world!!!", OK);
    }
}
