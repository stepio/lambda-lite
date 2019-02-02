package io.github.stepio.lambda.json;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import io.github.stepio.lambda.LambdaHandler;
import io.github.stepio.lambda.RequestContext;
import io.github.stepio.lambda.enums.MediaType;
import io.github.stepio.lambda.enums.Method;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

import static io.github.stepio.lambda.util.Assert.message;
import static io.github.stepio.lambda.util.Assert.notNull;
import static io.github.stepio.lambda.util.Responses.noContent;
import static io.github.stepio.lambda.util.Responses.ok;
import static io.github.stepio.lambda.util.StringUtils.isEmpty;

public class DefaultLambdaHandler<B, R> extends LambdaHandler {

    protected Class<B> bodyClass;
    protected Class<R> responseClass;

    protected ObjectMapper objectMapper;
    protected ConcurrentMap<Class<?>, ObjectReader> readers;
    protected ConcurrentMap<Class<?>, ObjectWriter> writers;

    public DefaultLambdaHandler() {
        Type[] array = genericTypes();
        if (array.length >= 2) {
            this.bodyClass = (Class<B>) array[0];
            this.responseClass = (Class<R>) array[1];
        }
        setObjectMapper(new ObjectMapper());
    }

    public DefaultLambdaHandler(Class<B> bodyClass, Class<R> responseClass) {
        this.bodyClass = bodyClass;
        this.responseClass = responseClass;
        setObjectMapper(new ObjectMapper());
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.readers = new ConcurrentHashMap<>();
        this.writers = new ConcurrentHashMap<>();
    }

    protected ObjectReader reader(Class<?> type) {
        return this.readers.computeIfAbsent(type, this.objectMapper::readerFor);
    }

    protected ObjectWriter writer(Class<?> type) {
        return this.writers.computeIfAbsent(type, this.objectMapper::writerFor);
    }

    public void registerCustom(Method method, Function<BodyRequestContext<B>, R> customHandler) {
        Function<RequestContext, APIGatewayProxyResponseEvent> handler = requestContext -> {
            BodyRequestContext<B> bodyRequestContext = new BodyRequestContext<>(
                    body(requestContext.getRequest()),
                    requestContext.getRequest(),
                    requestContext.getContext()
            );
            R response = customHandler.apply(bodyRequestContext);
            return wrap(response);
        };
        register(method, handler);
    }

    protected B body(APIGatewayProxyRequestEvent request) {
        String bodyText = request.getBody();
        if (isEmpty(bodyText)) {
            return null;
        }
        notNull(this.bodyClass, "Request class is mandatory");
        if (String.class == this.bodyClass) {
            return (B) bodyText;
        }
        ObjectReader reader = reader(this.bodyClass);
        try {
            return reader.readValue(bodyText);
        } catch (IOException ex) {
            throw new IllegalArgumentException(message("Failed to extract %s from request body %s", this.bodyClass, bodyText), ex);
        }
    }

    protected APIGatewayProxyResponseEvent wrap(R response) {
        if (isEmpty(response)) {
            return noContent();
        }
        notNull(this.responseClass, "Response class is mandatory");
        if (String.class == this.responseClass) {
            return ok(MediaType.TEXT_PLAIN, (String) response);
        }
        ObjectWriter writer = writer(this.responseClass);
        try {
            String responseText = writer.writeValueAsString(response);
            return ok(MediaType.APPLICATION_JSON_UTF8, responseText);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException(message("Failed to serialize %s", response), ex);
        }
    }

    protected Type[] genericTypes() {
        Type type = getClass().getGenericSuperclass();
        if (!(type instanceof ParameterizedType)) {
            return new Type[0];
        }
        Type[] array = ((ParameterizedType) type).getActualTypeArguments();
        if (array == null || array.length < 2) {
            return new Type[0];
        }
        return array;
    }
}
