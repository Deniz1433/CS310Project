package com.sabanci.cs310project.controller;

import aj.org.objectweb.asm.TypeReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sabanci.cs310project.model.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api")
public class AuthenticationController {

    @Autowired
    private UserRepository userRepository;

    private String jsonValueExtractor(String json) {
        // Remove leading and trailing whitespace and quotes
        json = json.trim().replace("\"", "");

        // Split the JSON string by the colon (:) to separate key and value
        String[] keyValue = json.split(":");

        // Extract the value
        String value = keyValue[1].trim();

        // Check if the value contains a closing curly brace
        int closingBracketIndex = value.indexOf("}");
        if (closingBracketIndex != -1) {
            // If the closing brace exists, remove it
            value = value.substring(0, closingBracketIndex);
        }

        return value;
    }

    static class loginDTO {
        private String email;
        private String password;

        public String getEmail() {
            return email;
        }

        public String getPassword() {
            return password;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    static class loginCallbackDTO {
        private String token;
        private Date expiry_date;
        private String username;
        private String email;

        loginCallbackDTO(String token, Date expiry_date, String username, String email) {
            this.token = token;
            this.expiry_date = expiry_date;
            this.username = username;
            this.email = email;
        }

        public Date getExpiry_date() {
            return expiry_date;
        }

        public String getEmail() {
            return email;
        }

        public String getToken() {
            return token;
        }

        public String getUsername() {
            return username;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public void setExpiry_date(Date expiry_date) {
            this.expiry_date = expiry_date;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public void setUsername(String username) {
            this.username = username;
        }
    }

    static class CheckUser {
        @JsonProperty("token")
        private String token;

        @JsonProperty("email")
        private String email;

        String getMail() {
            return email;
        }

        String getToken() {
            return token;
        }
    }

    @Autowired
    private jwtUtil jwtUtil;  // Ensure JwtUtil is a Spring bean and injected

    @PostMapping(path="/loginUser")
    public ResponseEntity<loginCallbackDTO> loginUser(@RequestBody loginDTO loginDTO) {
        try {
            if (userRepository.findPasswordByEmail(loginDTO.getEmail()) != null)
            {
                String pass = jsonValueExtractor(userRepository.findPasswordByEmail(loginDTO.getEmail()));
                String sent_pass = loginDTO.getPassword();

                if (pass.equals(sent_pass))
                {
                    String name = jsonValueExtractor(userRepository.findNameByEmail(loginDTO.getEmail()));
                    final String jwt = jwtUtil.generateToken(loginDTO.getEmail());
                    final Date expiry = jwtUtil.extractExpiration(jwt);

                    final String email = loginDTO.getEmail();

                    loginCallbackDTO message = new loginCallbackDTO(jwt, expiry, name, email);
                    return new ResponseEntity<>(message, HttpStatus.OK);
                }

                else throw new Exception("1");
            }

            else throw new Exception("No such user exists.");

        } catch (Exception e) {
            // Handling wrong email/password
            if (e.getMessage().equals("1"))
            {
                loginCallbackDTO error_message = new loginCallbackDTO(null,null,null,null);
                return new ResponseEntity<>(error_message, HttpStatus.UNAUTHORIZED);
            }

            else
            {
                loginCallbackDTO error_message = new loginCallbackDTO(null,null,null,null);
                return new ResponseEntity<>(error_message, HttpStatus.UNAUTHORIZED);
            }
        }
    }

    // Method to check if the user is already logged in and has a valid token
    // Renew the token and send the fresh token back.
    @PostMapping(path="/refreshToken")
    public ResponseEntity<loginCallbackDTO> checkAuthentication(@RequestBody CheckUser jwt)
    {
        try {
            if (jwtUtil.validateToken(jwt.getToken(), jwt.getMail()))
            {
                String mail = jwt.getMail();
                final String newJwt = jwtUtil.generateToken(mail);  // Corrected the static access
                final Date expiry = jwtUtil.extractExpiration(newJwt);
                final String username = jsonValueExtractor(userRepository.findNameByEmail(mail));

                loginCallbackDTO message = new loginCallbackDTO(newJwt, expiry, username, mail);
                return new ResponseEntity<>(message, HttpStatus.OK);
            }

            else throw new Exception("Invalid email or password");

        } catch (Exception e) {
            // Handling wrong email/password
            loginCallbackDTO error_message = new loginCallbackDTO(null, null, null, null);
            return new ResponseEntity<>(error_message, HttpStatus.UNAUTHORIZED);
        }
    }
}

