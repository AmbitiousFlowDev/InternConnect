package uca.github.org.handlers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Global exception handler to catch IllegalArgumentExceptions thrown by the PDFValidator and return a consistent error message.    
 * This ensures that any validation errors related to PDF file uploads will result in a clear and user-friendly response, indicating that only PDF files are allowed.
 * By centralizing the exception handling logic, we can maintain cleaner controller code and provide a unified error response format across the application.
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body("Only PDF files are allowed");
    }
}
