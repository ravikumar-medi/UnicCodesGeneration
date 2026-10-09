// dto/ResponseDTO.java
package com.java.CodesGeneration.DTO;

import lombok.Data;

@Data
public class ResponseDTO {
    private String statusCode;
    private String message;
    private Object response;
}