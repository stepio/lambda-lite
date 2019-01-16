package org.stepio.aws.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import org.junit.Before;
import org.junit.Test;
import org.stepio.aws.lambda.enums.Method;
import org.stepio.aws.lambda.util.Responses;

import java.util.Objects;

import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.stepio.aws.lambda.util.Assert.state;
import static org.stepio.aws.lambda.util.Query.required;

public class LambdaHandlerTest {

    private Context context;
    private LambdaHandler handler;
    private LambdaHandler customHandler;

    private APIGatewayProxyResponseEvent response;
    private APIGatewayProxyRequestEvent request;

    @Before
    public void setUp() {
        this.context = mock(Context.class);
        this.handler = new LambdaHandler();
        this.customHandler = new LambdaHandler();

        this.customHandler.setDefaultHandler(requestContext -> Responses.notFound());
        this.customHandler.setDefaultResponse(() -> Responses.ok(null));

        this.handler.register(Method.GET, request -> {
            String value = required(request.getRequest(), "dummyParam");
            state(Objects.equals("dummyValue", value),
                    "Unexpected request parameter %s", value);
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(200)
                    .withBody("DummyGetText");
        });

        this.handler.register(Method.POST, request -> {
            state(Objects.equals("DummyRequestBody", request.getRequest().getBody()),
                    "Unexpected request %s", request.getRequest().getBody());
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(200)
                    .withBody("DummyPostText");
        });

        this.handler.register(Method.DELETE, request -> null);
        this.customHandler.register(Method.DELETE, request -> null);
    }

    @Test
    public void handleRequest_badRequest() {
        request = new APIGatewayProxyRequestEvent()
                .withHttpMethod("GET");
        response = handler.handleRequest(request, this.context);
        assertThat(response)
                .hasFieldOrPropertyWithValue("statusCode", 400);
    }

    @Test
    public void handleRequest_internalServerError() {
        request = new APIGatewayProxyRequestEvent()
                .withHttpMethod("GET")
                .withMultiValueQueryStringParameters(singletonMap("dummyParam", singletonList("13")));
        response = handler.handleRequest(request, this.context);
        assertThat(response)
                .hasFieldOrPropertyWithValue("statusCode", 500);

        request = new APIGatewayProxyRequestEvent()
                .withHttpMethod("POST")
                .withBody("fail");
        response = this.handler.handleRequest(request, this.context);
        assertThat(response)
                .hasFieldOrPropertyWithValue("statusCode", 500);
    }

    @Test
    public void handleRequest_methodNotAllowed() {
        request = new APIGatewayProxyRequestEvent()
                .withHttpMethod("PUT");
        response = this.handler.handleRequest(request, this.context);
        assertThat(response)
                .hasFieldOrPropertyWithValue("statusCode", 405);

        response = this.customHandler.handleRequest(request, this.context);
        assertThat(response)
                .hasFieldOrPropertyWithValue("statusCode", 404);


        request = new APIGatewayProxyRequestEvent()
                .withHttpMethod("PATCH");
        response = this.handler.handleRequest(request, this.context);
        assertThat(response)
                .hasFieldOrPropertyWithValue("statusCode", 405);

        response = this.customHandler.handleRequest(request, this.context);
        assertThat(response)
                .hasFieldOrPropertyWithValue("statusCode", 404);
    }

    @Test
    public void handleRequest_noContent() {
        request = new APIGatewayProxyRequestEvent()
                .withHttpMethod("DELETE");
        response = this.handler.handleRequest(request, this.context);
        assertThat(response)
                .hasFieldOrPropertyWithValue("statusCode", 204);

        response = this.customHandler.handleRequest(request, this.context);
        assertThat(response)
                .hasFieldOrPropertyWithValue("statusCode", 200);
    }

    @Test
    public void handleRequest_ok() {
        request = new APIGatewayProxyRequestEvent()
                .withHttpMethod("GET")
                .withQueryStringParameters(singletonMap("dummyParam", "dummyValue"));
        response = handler.handleRequest(request, this.context);
        assertThat(response)
                .hasFieldOrPropertyWithValue("statusCode", 200)
                .hasFieldOrPropertyWithValue("body", "DummyGetText");

        request = new APIGatewayProxyRequestEvent()
                .withHttpMethod("POST")
                .withBody("DummyRequestBody");
        response = this.handler.handleRequest(request, this.context);
        assertThat(response)
                .hasFieldOrPropertyWithValue("statusCode", 200)
                .hasFieldOrPropertyWithValue("body", "DummyPostText");
    }
}
