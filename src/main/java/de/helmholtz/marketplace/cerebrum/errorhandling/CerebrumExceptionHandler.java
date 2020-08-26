package de.helmholtz.marketplace.cerebrum.errorhandling;

import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import de.helmholtz.marketplace.cerebrum.errorhandling.exception.CerebrumEntityNotFoundException;
import de.helmholtz.marketplace.cerebrum.errorhandling.exception.CerebrumInvalidUuidException;

@ControllerAdvice
public class CerebrumExceptionHandler extends ResponseEntityExceptionHandler
{
    private static final String REGEX_VALUE = "^.|.$";

    // modified version of: https://www.baeldung.com/global-error-handler-in-a-spring-rest-api
    /**
     * code: 400
     * BindException: This exception is thrown when fatal binding errors occur.
     *              AND
     * MethodArgumentNotValidException: This exception is thrown when argument
     * annotated with @Valid failed validation:
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request)
    {
        logger.info(ex.getClass().getName());
        List<String> errors = new ArrayList<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(error.getField() + ": " + error.getDefaultMessage());
        }
        for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            errors.add(error.getObjectName() + ": " + error.getDefaultMessage());
        }

        CerebrumApiError cerebrumApiError =
                new CerebrumApiError(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), errors);
        return handleExceptionInternal(
                ex, cerebrumApiError, headers, cerebrumApiError.getStatus(), request);
    }

    /**
     * code: 400
     * TypeMismatchException: This exception is thrown when
     * try to set bean property with wrong type.
     */
    @Override
    protected ResponseEntity<Object> handleTypeMismatch(
            final TypeMismatchException ex,
            final HttpHeaders headers,
            final HttpStatus status,
            final WebRequest request)
    {
        logger.info(ex.getClass().getName());
        final String error = ex.getValue() + " value for " + ex.getPropertyName() +
                " should be of type " + ex.getRequiredType();

        final CerebrumApiError cerebrumApiError =
                new CerebrumApiError(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), error);
        return new ResponseEntity<>(
                cerebrumApiError, new HttpHeaders(), cerebrumApiError.getStatus());
    }

    /**
     * code: 400
     * MissingServletRequestParameterException: This exception is
     * thrown when request missing parameter:
     */
    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            final MissingServletRequestParameterException ex,
            final HttpHeaders headers,
            final HttpStatus status,
            final WebRequest request)
    {
        logger.info(ex.getClass().getName());
        final String error = ex.getParameterName() + " parameter is missing";
        final CerebrumApiError cerebrumApiError =
                new CerebrumApiError(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), error);
        return new ResponseEntity<>(
                cerebrumApiError, new HttpHeaders(), cerebrumApiError.getStatus());
    }

    /**
     * code: 400
     * MissingServletRequestPartException: This exception is
     * thrown when when the part of a multipart request not found
     */
    @Override
    protected ResponseEntity<Object> handleMissingServletRequestPart(
            final MissingServletRequestPartException ex,
            final HttpHeaders headers,
            final HttpStatus status,
            final WebRequest request)
    {
        logger.info(ex.getClass().getName());
        final String error = ex.getRequestPartName() + " part is missing";
        final CerebrumApiError cerebrumApiError =
                new CerebrumApiError(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), error);
        return new ResponseEntity<>(
                cerebrumApiError, new HttpHeaders(), cerebrumApiError.getStatus());
    }

    /**
     * code: 400
     * ConstrainViolationException: This exception reports the result of
     * constraint violations:
     */
    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<Object> handleConstraintViolation(
            final ConstraintViolationException ex,
            final WebRequest request)
    {
        logger.info(ex.getClass().getName());
        final List<String> errors = new ArrayList<>();
        for (final ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            errors.add(violation.getRootBeanClass().getName() + " "
                    + violation.getPropertyPath() + ": " + violation.getMessage());
        }

        final CerebrumApiError cerebrumApiError =
                new CerebrumApiError(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), errors);
        return new ResponseEntity<>(
                cerebrumApiError, new HttpHeaders(), cerebrumApiError.getStatus());
    }

    /**
     * code: 400
     * MethodArgumentTypeMismatchException:
     * This exception is thrown when method argument is
     * not the expected type
     */
    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    public ResponseEntity<Object> handleMethodArgumentTypeMismatch(
            final MethodArgumentTypeMismatchException ex,
            final WebRequest request)
    {
        logger.info(ex.getClass().getName());
        final String error = ex.getName() +
                " should be of type " +
                Objects.requireNonNull(ex.getRequiredType()).getName();
        final CerebrumApiError cerebrumApiError =
                new CerebrumApiError(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), error);
        return new ResponseEntity<>(
                cerebrumApiError, new HttpHeaders(), cerebrumApiError.getStatus());
    }

    // 400
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            final HttpMessageNotReadableException ex,
            final HttpHeaders headers,
            final HttpStatus status,
            final WebRequest request)
    {
        logger.info(ex.getClass().getName());
        final String error = "Malformed JSON or JSON+PATCH request";
        final CerebrumApiError cerebrumApiError =
                new CerebrumApiError(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), error);
        return new ResponseEntity<>(
                cerebrumApiError, new HttpHeaders(), cerebrumApiError.getStatus());
    }

    // 400
    @ExceptionHandler({CerebrumInvalidUuidException.class})
    private ResponseEntity<Object> handleInvalidUuid(
            final CerebrumInvalidUuidException ex,
            WebRequest request)
    {
        final String error = "Invalid uuid";

        final CerebrumApiError cerebrumApiError =
                new CerebrumApiError(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), error);
        return new ResponseEntity<>(
                cerebrumApiError, new HttpHeaders(), cerebrumApiError.getStatus());
    }

    //403
    @ExceptionHandler({AccessDeniedException.class})
    public ResponseEntity<Object> handleAccessDeniedException(
            Exception ex, WebRequest request) {
        return new ResponseEntity<>(new HttpHeaders(), HttpStatus.FORBIDDEN);
    }

    // 404
    @ExceptionHandler({CerebrumEntityNotFoundException.class})
    private ResponseEntity<Object> handleEntityNotFound(
            final CerebrumEntityNotFoundException ex,
            WebRequest request){
        final String error = "Entity not found";

        final CerebrumApiError cerebrumApiError =
                new CerebrumApiError(HttpStatus.NOT_FOUND, ex.getLocalizedMessage(), error);
        return new ResponseEntity<>(
                cerebrumApiError, new HttpHeaders(), cerebrumApiError.getStatus());
    }

    /**
     * code: 404
     * Handle NoHandlerFoundException - we can customize our servlet
     * to throw this exception instead of send 404 response
     */
    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(
            final NoHandlerFoundException ex,
            final HttpHeaders headers,
            final HttpStatus status,
            final WebRequest request)
    {
        logger.info(ex.getClass().getName());
        final String error = "No handler found for " +
                ex.getHttpMethod() + " " + ex.getRequestURL();
        final CerebrumApiError cerebrumApiError =
                new CerebrumApiError(HttpStatus.NOT_FOUND, ex.getLocalizedMessage(), error);
        return new ResponseEntity<>(
                cerebrumApiError, new HttpHeaders(), cerebrumApiError.getStatus());
    }

    /**
     * code: 405
     * HttpRequestMethodNotSupportedException – which occurs when you send
     * a requested with an unsupported HTTP method
     */
    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
            final HttpRequestMethodNotSupportedException ex,
            final HttpHeaders headers,
            final HttpStatus status,
            final WebRequest request)
    {
        logger.info(ex.getClass().getName());
        final StringBuilder builder = new StringBuilder();
        builder.append(ex.getMethod());
        builder.append(" method is not supported for this request. ");

        if (!Objects.requireNonNull(ex.getSupportedHttpMethods()).isEmpty()) {
            int size = ex.getSupportedHttpMethods().size();
            if (size == 1) {
                builder.append("Supported method is ")
                        .append(ex.getSupportedHttpMethods().iterator().next());
            } else if (size > 1) {
                Set<HttpMethod> methods = ex.getSupportedHttpMethods();
                builder.append("Supported media methods are ")
                        .append(methods.toString().replaceAll(REGEX_VALUE, ""));

                builder.replace(
                        builder.lastIndexOf(", "),
                        builder.lastIndexOf(",") + 1,
                        " and"
                );
            }
        }
        final CerebrumApiError cerebrumApiError =
                new CerebrumApiError(
                        HttpStatus.METHOD_NOT_ALLOWED,
                        ex.getLocalizedMessage(), builder.toString());
        return new ResponseEntity<>(
                cerebrumApiError, new HttpHeaders(), cerebrumApiError.getStatus());
    }

    /**
     * code: 406
     * HttpMediaTypeNotAcceptableException:
     * This exception is thrown when the server can not response
     * with the Accept header request from the client
     */
    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotAcceptable(
            final HttpMediaTypeNotAcceptableException ex,
            final HttpHeaders headers,
            final HttpStatus status,
            final WebRequest request)
    {
        logger.info(ex.getClass().getName());
        StringBuilder builder = new StringBuilder();
        builder.append(request.getHeader("Accept"));
        builder.append(" MIME type is not acceptable. ");
        int size = ex.getSupportedMediaTypes().size();
        if (size == 1) {
            builder.append("Acceptable MIME type is ")
                    .append(ex.getSupportedMediaTypes().iterator().next());
        } else if (size > 1) {
            List<MediaType> mediaTypes = ex.getSupportedMediaTypes();
            builder.append("Acceptable MIME types are ")
                    .append(mediaTypes.toString().replaceAll(REGEX_VALUE, ""));

            builder.replace(
                    builder.lastIndexOf(", "),
                    builder.lastIndexOf(",") + 1,
                    " and"
            );
        }
        final CerebrumApiError cerebrumApiError =
                new CerebrumApiError(
                        HttpStatus.NOT_ACCEPTABLE,
                        ex.getLocalizedMessage(),
                        builder.toString());
        return new ResponseEntity<>(
                cerebrumApiError, new HttpHeaders(), cerebrumApiError.getStatus());
    }

    /**
     * code: 415
     * HttpMediaTypeNotSupportedException – which occurs when the client send
     * a request with unsupported media type
     */
    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
            final HttpMediaTypeNotSupportedException ex,
            final HttpHeaders headers,
            final HttpStatus status,
            final WebRequest request)
    {
        logger.info(ex.getClass().getName());
        StringBuilder builder = new StringBuilder();
        builder.append(ex.getContentType());
        builder.append(" media type is not supported. ");
        int size = ex.getSupportedMediaTypes().size();
        if (size == 1) {
            builder.append("Supported media type is ")
                    .append(ex.getSupportedMediaTypes().iterator().next());
        } else if (size > 1) {
            List<MediaType> mediaTypes = ex.getSupportedMediaTypes();
            builder.append("Supported media types are ")
                    .append(mediaTypes.toString().replaceAll(REGEX_VALUE, ""));

            builder.replace(
                    builder.lastIndexOf(", "),
                    builder.lastIndexOf(",") + 1,
                    " and"
            );
        }
        final CerebrumApiError cerebrumApiError =
                new CerebrumApiError(
                        HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                        ex.getLocalizedMessage(),
                        builder.toString());
        return new ResponseEntity<>(
                cerebrumApiError, new HttpHeaders(), cerebrumApiError.getStatus());
    }

    /**
     * code: 500
     * Finally, a fall-back handler – a catch-all type
     * of logic that deals with all other exceptions that don't have
     * specific handlers
     */
    @ExceptionHandler({ Exception.class })
    public ResponseEntity<Object> handleAll(
            final Exception ex,
            final WebRequest request)
    {
        logger.info(ex.getClass().getName());
        logger.error("error", ex);
        final CerebrumApiError cerebrumApiError =
                new CerebrumApiError(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        ex.getLocalizedMessage(), "error occurred");
        return new ResponseEntity<>(
                cerebrumApiError, new HttpHeaders(), cerebrumApiError.getStatus());
    }
}
