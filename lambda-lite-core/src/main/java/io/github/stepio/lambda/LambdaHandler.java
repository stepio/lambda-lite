package io.github.stepio.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import io.github.stepio.lambda.enums.Method;
import io.github.stepio.lambda.util.Responses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.function.Supplier;

import static io.github.stepio.lambda.util.Responses.badRequest;
import static io.github.stepio.lambda.util.Responses.internalServerError;
import static io.github.stepio.lambda.util.Responses.methodNotAllowed;

public class LambdaHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    protected ConcurrentMap<Method, Function<RequestContext, APIGatewayProxyResponseEvent>> handlers;
    protected Function<RequestContext, APIGatewayProxyResponseEvent> defaultHandler;
    protected Supplier<APIGatewayProxyResponseEvent> defaultResponse;

    public LambdaHandler() {
        this.handlers = new ConcurrentHashMap<>();
        this.defaultHandler = requestContext -> {
            log.warn("Unexpected HTTP method {}", requestContext.getRequest().getHttpMethod());
            return methodNotAllowed();
        };
        this.defaultResponse = Responses::noContent;
    }

    public void setDefaultHandler(Function<RequestContext, APIGatewayProxyResponseEvent> defaultHandler) {
        this.defaultHandler = defaultHandler;
    }

    public void setDefaultResponse(Supplier<APIGatewayProxyResponseEvent> defaultResponse) {
        this.defaultResponse = defaultResponse;
    }

    public void register(Method method, Function<RequestContext, APIGatewayProxyResponseEvent> handler) {
        this.handlers.put(method, handler);
    }

    protected RequestContext toRequestContext(APIGatewayProxyRequestEvent request, Context context) {
        return new RequestContext(request, context);
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
        try {
            return Method.of(request.getHttpMethod())
                    .map(method -> this.handlers.getOrDefault(method, this.defaultHandler))
                    .map(handler -> handler.apply(toRequestContext(request, context)))
                    .orElseGet(this.defaultResponse);
        } catch (IllegalArgumentException ex) {
            log.error("Failed to process an AWS request as it's incorrect", ex);
            return badRequest();
        } catch (RuntimeException ex) {
            log.error("Failed to process an AWS request due to unexpected error", ex);
            return internalServerError();
        }
    }
}
