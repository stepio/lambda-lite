package org.stepio.aws.lambda.util;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.stepio.aws.lambda.util.Assert.hasLength;
import static org.stepio.aws.lambda.util.Assert.notNull;
import static java.lang.String.format;
import static java.util.Collections.emptyList;

public class Query {

    private static final int ELEMENTS_FIRST = 0;

    private Query() {
    }

    public static List<String> list(APIGatewayProxyRequestEvent request, String name) {
        validate(request);
        return request.getMultiValueQueryStringParameters().getOrDefault(name, emptyList());
    }

    public static Optional<String> optional(APIGatewayProxyRequestEvent request, String name) {
        return Optional.ofNullable(query(request, name));
    }

    public static String required(APIGatewayProxyRequestEvent request, String name) {
        String value = query(request, name);
        hasLength(value, format("Query parameter '%s' is mandatory", name));
        return value;
    }

    static String query(APIGatewayProxyRequestEvent request, String name) {
        validate(request);
        Map<String, String> map = request.getQueryStringParameters();
        if (map != null && !map.isEmpty()) {
            String value = map.get(name);
            if (value != null) {
                return value;
            }
        }
        Map<String, List<String>> multiMap = request.getMultiValueQueryStringParameters();
        if (multiMap != null && !multiMap.isEmpty()) {
            List<String> list = multiMap.get(name);
            if (list != null && !list.isEmpty()) {
                return list.get(ELEMENTS_FIRST);
            }
        }
        return null;
    }

    static void validate(APIGatewayProxyRequestEvent request) {
        notNull(request, "Request cannot be null");
    }
}
