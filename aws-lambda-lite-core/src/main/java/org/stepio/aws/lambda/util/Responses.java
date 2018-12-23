package org.stepio.aws.lambda.util;

import com.amazonaws.serverless.proxy.model.AwsProxyResponse;
import com.amazonaws.serverless.proxy.model.MultiValuedTreeMap;

import javax.ws.rs.core.Response;

import static java.util.Objects.requireNonNull;

public class Responses {

    private Responses() {
    }

    public static AwsProxyResponse ok(String body) {
        return response(Response.Status.OK.getStatusCode(), null, body);
    }

    public static AwsProxyResponse noContent() {
        return status(Response.Status.NO_CONTENT);
    }

    public static AwsProxyResponse badRequest() {
        return status(Response.Status.BAD_REQUEST);
    }

    public static AwsProxyResponse notFound() {
        return status(Response.Status.NOT_FOUND);
    }

    public static AwsProxyResponse methodNotAllowed() {
        return status(Response.Status.METHOD_NOT_ALLOWED);
    }

    public static AwsProxyResponse internalServerError() {
        return status(Response.Status.INTERNAL_SERVER_ERROR);
    }

    public static AwsProxyResponse status(Response.Status status) {
        requireNonNull(status, "HTTP response status cannot be null");
        return status(status.getStatusCode());
    }

    public static AwsProxyResponse status(int status) {
        return response(status, null, null);
    }

    public static AwsProxyResponse response(int status, MultiValuedTreeMap<String, String> headers, String body) {
        return new AwsProxyResponse(status, headers, body);
    }
}
