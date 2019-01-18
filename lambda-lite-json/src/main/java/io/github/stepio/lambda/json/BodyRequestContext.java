package io.github.stepio.lambda.json;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import io.github.stepio.lambda.RequestContext;

public class BodyRequestContext<T> extends RequestContext {

    protected T body;

    protected BodyRequestContext(T body, APIGatewayProxyRequestEvent request, Context context) {
        super(request, context);
        this.body = body;
    }

    public T getBody() {
        return body;
    }
}
