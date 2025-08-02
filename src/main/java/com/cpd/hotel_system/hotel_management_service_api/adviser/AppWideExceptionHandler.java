package com.cpd.hotel_system.hotel_management_service_api.adviser;

import com.cpd.hotel_system.hotel_management_service_api.exceptions.EntryNotFoundException;
import com.cpd.hotel_system.hotel_management_service_api.util.StandardResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AppWideExceptionHandler {
    @ExceptionHandler(EntryNotFoundException.class)
    public ResponseEntity<StandardResponseDto> handleEntryNotFoundException(EntryNotFoundException ex) {
        return new ResponseEntity<StandardResponseDto>(
                new StandardResponseDto(404,ex.getMessage(),ex),
                HttpStatus.NOT_FOUND
        );
    }
}
