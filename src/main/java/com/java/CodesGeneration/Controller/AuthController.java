package com.java.CodesGeneration.Controller;

import com.java.CodesGeneration.DTO.LoginRequestDTO;
import com.java.CodesGeneration.DTO.ResponseDTO;
import com.java.CodesGeneration.Service.UserAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserAuthService userAuthService;

    @PostMapping("/login")
    public ResponseEntity<ResponseDTO> login(@RequestBody LoginRequestDTO request) {
        ResponseDTO response = new ResponseDTO();

        if (request == null || request.getUserName() == null || request.getPassword() == null) {
            response.setStatusCode("400");
            response.setMessage("userName and password are required");
            response.setResponse("INVALID_REQUEST");
            return ResponseEntity.badRequest().body(response);
        }

        boolean isValid = userAuthService.validateLogin(request.getUserName(), request.getPassword());

        if (!isValid) {
            response.setStatusCode("401");
            response.setMessage("Invalid username or password");
            response.setResponse("AUTH_FAILED");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("userName", request.getUserName());
        result.put("status", "SUCCESS");
        result.put("message", "Login successful");

        response.setStatusCode("200");
        response.setMessage("Login successful");
        response.setResponse(result);
        return ResponseEntity.ok(response);
    }
}
