package io.github.stepio.lambda.util;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import io.github.stepio.lambda.enums.MediaType;
import io.github.stepio.lambda.enums.Status;

import java.util.Map;

import static java.util.Collections.singletonMap;
import static java.util.Objects.requireNonNull;

public class Responses {

    private static final String HEADER_CONTENT_TYPE = "Content-Type";

    private Responses() {
    }

    public static Map<String, String> contentTypeHeader(String mediaType) {
        return singletonMap(HEADER_CONTENT_TYPE, mediaType);
    }

    public static APIGatewayProxyResponseEvent ok(MediaType mediaType, String body) {
        return ok(mediaType.getValue(), body);
    }

    public static APIGatewayProxyResponseEvent ok(String mediaType, String body) {
        return ok(contentTypeHeader(mediaType), body);
    }

    public static APIGatewayProxyResponseEvent ok(Map<String, String> headers, String body) {
        return response(Status.OK.getStatusCode(), headers, body);
    }

    public static APIGatewayProxyResponseEvent noContent() {
        return status(Status.NO_CONTENT);
    }

    public static APIGatewayProxyResponseEvent badRequest() {
        return status(Status.BAD_REQUEST);
    }

    public static APIGatewayProxyResponseEvent notFound() {
        return status(Status.NOT_FOUND);
    }

    public static APIGatewayProxyResponseEvent methodNotAllowed() {
        return status(Status.METHOD_NOT_ALLOWED);
    }

    public static APIGatewayProxyResponseEvent internalServerError() {
        return status(Status.INTERNAL_SERVER_ERROR);
    }

    public static APIGatewayProxyResponseEvent status(Status status) {
        requireNonNull(status, "HTTP response status cannot be null");
        return status(status.getStatusCode());
    }

    public static APIGatewayProxyResponseEvent status(int status) {
        return new APIGatewayProxyResponseEvent()
                .withStatusCode(status);
    }

    public static APIGatewayProxyResponseEvent response(int status, Map<String, String> headers, String body) {
        return status(status)
                .withHeaders(headers)
                .withBody(body);
    }
}
