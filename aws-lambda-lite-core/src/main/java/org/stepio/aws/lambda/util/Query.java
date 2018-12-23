package org.stepio.aws.lambda.util;

import com.amazonaws.serverless.proxy.model.AwsProxyRequest;

import java.util.List;
import java.util.Optional;

import static org.stepio.aws.lambda.util.Assert.hasLength;
import static org.stepio.aws.lambda.util.Assert.notNull;
import static java.lang.String.format;
import static java.util.Collections.emptyList;

public class Query {

    private Query() {
    }

    public static List<String> list(AwsProxyRequest request, String name) {
        validate(request);
        return request.getMultiValueQueryStringParameters().getOrDefault(name, emptyList());
    }

    public static Optional<String> optional(AwsProxyRequest request, String name) {
        return Optional.ofNullable(query(request, name));
    }

    public static String required(AwsProxyRequest request, String name) {
        String value = query(request, name);
        hasLength(value, format("Query parameter '%s' is mandatory", name));
        return value;
    }

    static String query(AwsProxyRequest request, String name) {
        validate(request);
        return request.getMultiValueQueryStringParameters().getFirst(name);
    }

    static void validate(AwsProxyRequest request) {
        notNull(request, "Request cannot be null");
    }
}
