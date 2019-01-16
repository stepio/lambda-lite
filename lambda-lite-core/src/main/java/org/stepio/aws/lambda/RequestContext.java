package org.stepio.aws.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;

public class RequestContext {

    protected APIGatewayProxyRequestEvent request;
    protected Context context;

    public RequestContext(APIGatewayProxyRequestEvent request, Context context) {
        this.request = request;
        this.context = context;
    }

    public APIGatewayProxyRequestEvent getRequest() {
        return this.request;
    }

    public Context getContext() {
        return this.context;
    }
}
