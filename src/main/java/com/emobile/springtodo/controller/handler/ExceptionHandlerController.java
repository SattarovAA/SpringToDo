package com.emobile.springtodo.controller.handler;

import com.emobile.springtodo.exception.AlreadyExitsException;
import com.emobile.springtodo.exception.DeleteEntityWithReferenceException;
import com.emobile.springtodo.exception.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.List;

/**
 * Exception Handler Controller for handle exceptions.
 * toAdd HttpMessageNotReadableException json parse ex
 */
@RestControllerAdvice
@Slf4j
public class ExceptionHandlerController {
    /**
     * ExceptionHandler for {@link MethodArgumentNotValidException}.
     * <br>
     * Used for aggregate validation exceptions.
     *
     * @param ex exception type of {@link MethodArgumentNotValidException}.
     * @return {@link ResponseEntity} with {@link ErrorResponse}.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> badRequest(
            MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        List<String> errorMessages = bindingResult.getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();
        String errorMessage = String.join("; ", errorMessages);
        log.error("Not valid arguments: " + errorMessage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(errorMessage));
    }

    /**
     * ExceptionHandler for {@link DeleteEntityWithReferenceException}.
     *
     * @param ex         exception type of
     *                   {@link DeleteEntityWithReferenceException}.
     * @param webRequest web request for exception description.
     * @return {@link ResponseEntity} with {@link ErrorResponseBody}.
     * @see #buildResponse(HttpStatus, Exception, WebRequest)
     */
    @ExceptionHandler(DeleteEntityWithReferenceException.class)
    public ResponseEntity<ErrorResponseBody> badRequest(
            DeleteEntityWithReferenceException ex,
            WebRequest webRequest
    ) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex, webRequest);
    }

    /**
     * ExceptionHandler for {@link AlreadyExitsException}.
     *
     * @param ex         exception type of {@link AlreadyExitsException}.
     * @param webRequest web request for exception description.
     * @return {@link ResponseEntity} with {@link ErrorResponseBody}.
     * @see #buildResponse(HttpStatus, Exception, WebRequest)
     */
    @ExceptionHandler(AlreadyExitsException.class)
    public ResponseEntity<ErrorResponseBody> badRequest(
            AlreadyExitsException ex,
            WebRequest webRequest
    ) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex, webRequest);
    }

    /**
     * ExceptionHandler for {@link EntityNotFoundException}.
     *
     * @param ex         exception type of {@link EntityNotFoundException}.
     * @param webRequest web request for exception description.
     * @return {@link ResponseEntity} with {@link ErrorResponseBody}.
     * @see #buildResponse(HttpStatus, Exception, WebRequest)
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponseBody> notFound(
            EntityNotFoundException ex,
            WebRequest webRequest
    ) {
        return buildResponse(HttpStatus.NOT_FOUND, ex, webRequest);
    }

    /**
     * ExceptionHandler for {@link AccessDeniedException}.
     *
     * @param ex         exception type of {@link AccessDeniedException}.
     * @param webRequest web request for exception description.
     * @return {@link ResponseEntity} with {@link ErrorResponseBody}.
     * @see #buildResponse(HttpStatus, Exception, WebRequest)
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponseBody> forbidden(
            AccessDeniedException ex,
            WebRequest webRequest
    ) {
        return buildResponse(HttpStatus.FORBIDDEN, ex, webRequest);
    }

    /**
     * Exception response builder.
     *
     * @param status     exception {@link HttpStatus}.
     * @param ex         exception.
     * @param webRequest web request for exception description.
     * @return {@link ResponseEntity} with {@link ErrorResponseBody}.
     */
    private ResponseEntity<ErrorResponseBody> buildResponse(
            HttpStatus status,
            Exception ex,
            WebRequest webRequest
    ) {
        ErrorResponseBody responseBody = ErrorResponseBody.builder()
                .message(ex.getMessage())
                .description(webRequest.getDescription(false))
                .build();
        log.error(responseBody.toString(), ex);
        return ResponseEntity.status(status)
                .body(responseBody);
    }
}
