package org.stepio.aws.lambda;

import com.amazonaws.serverless.proxy.internal.testutils.AwsProxyRequestBuilder;
import com.amazonaws.serverless.proxy.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.model.AwsProxyResponse;
import org.junit.Before;
import org.junit.Test;
import org.stepio.aws.lambda.test.ABody;
import org.stepio.aws.lambda.test.AResult;
import org.stepio.aws.lambda.test.StringLambdaHandler;
import org.stepio.aws.lambda.test.StringLambdaHandlerExplicit;
import org.stepio.aws.lambda.test.TestLambdaHandler;

import javax.ws.rs.core.Response;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class DefaultLambdaHandlerTest {

    private StringLambdaHandler stringHandler;
    private TestLambdaHandler testHandler;
    private DefaultLambdaHandler<ABody, AResult> defaultHandler;

    @Before
    public void setUp() {
        this.stringHandler = new StringLambdaHandler();
        this.testHandler = new TestLambdaHandler();
        this.defaultHandler = new DefaultLambdaHandler<>(ABody.class, AResult.class);
    }

    @Test
    public void doGetImpl_entity() {

        AwsProxyRequest request = new AwsProxyRequestBuilder()
                .method("GET")
                .body("{\"name\":\"DUMMY#1\"}")
                .build();
        AwsProxyResponse response = this.testHandler.handleRequest(request, null);
        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo("{\"value\":\"DUMMY#1\"}");

        response = this.defaultHandler.handleRequest(request, null);
        assertThat(response.getStatusCode()).isEqualTo(405);
    }

    @Test
    public void doPostImpl_entity() {
        AwsProxyRequest request = new AwsProxyRequestBuilder()
                .method("POST")
                .body("{\"name\":\"fail\"}")
                .build();
        AwsProxyResponse response = this.testHandler.handleRequest(request, null);
        assertThat(response.getStatusCode()).isEqualTo(500);

        request = new AwsProxyRequestBuilder()
                .method("POST")
                .body("{\"name\":\"DUMMY#2\"}")
                .build();
        response = this.testHandler.handleRequest(request, null);
        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo("{\"value\":\"DUMMY#2\"}");

        response = this.defaultHandler.handleRequest(request, null);
        assertThat(response.getStatusCode()).isEqualTo(405);
    }

    @Test
    public void doPutImpl_entity() {
        AwsProxyRequest request = new AwsProxyRequestBuilder()
                .method("PUT")
                .body("{\"name\":\"fail\"}")
                .build();
        AwsProxyResponse response = this.testHandler.handleRequest(request, null);
        assertThat(response.getStatusCode()).isEqualTo(500);

        request = new AwsProxyRequestBuilder()
                .method("PUT")
                .body("{\"name\":\"DUMMY#3\"}")
                .build();
        response = this.testHandler.handleRequest(request, null);
        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo("{\"value\":\"DUMMY#3\"}");

        response = this.defaultHandler.handleRequest(request, null);
        assertThat(response.getStatusCode()).isEqualTo(405);
    }

    @Test
    public void doDeleteImpl_entity() {
        AwsProxyRequest request = new AwsProxyRequestBuilder()
                .method("DELETE")
                .body("{\"name\":\"fail\"}")
                .build();
        AwsProxyResponse response = this.testHandler.handleRequest(request, null);
        assertThat(response.getStatusCode()).isEqualTo(500);

        request = new AwsProxyRequestBuilder()
                .method("DELETE")
                .body("{\"name\":\"DUMMY#4\"}")
                .build();
        response = this.testHandler.handleRequest(request, null);
        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo("{\"value\":\"DUMMY#4\"}");

        response = this.defaultHandler.handleRequest(request, null);
        assertThat(response.getStatusCode()).isEqualTo(405);
    }

    @Test
    public void constructor_generics() {
        StringLambdaHandlerExplicit stringHandlerExplicit = new StringLambdaHandlerExplicit();
        assertThat(this.stringHandler.bodyClass).isSameAs(stringHandlerExplicit.bodyClass);
        assertThat(this.stringHandler.responseClass).isSameAs(stringHandlerExplicit.responseClass);
        assertThat(this.stringHandler.responseClass).isSameAs(stringHandlerExplicit.responseClass);
    }

    @Test
    public void body_empty() {
        AwsProxyRequest request = new AwsProxyRequestBuilder().build();
        assertThat(this.stringHandler.body(request)).isNull();
    }

    @Test
    public void body_entity() {
        assertThatThrownBy(() -> this.testHandler.body(
                new AwsProxyRequestBuilder().body("{name:dummy}").build()
        )).isInstanceOf(IllegalArgumentException.class).hasMessageStartingWith("Failed to extract class org.stepio.aws.lambda.test.ABody from request body");
        AwsProxyRequest request = new AwsProxyRequestBuilder()
                .body("{\"name\":\"Bond007\"}")
                .build();
        ABody body = this.testHandler.body(request);
        assertThat(body.getName()).isEqualTo("Bond007");
        assertThat(body.getQuantity()).isNull();
        assertThat(body.getElements()).isNull();
    }

    @Test
    public void body_text() {
        AwsProxyRequest request = new AwsProxyRequestBuilder()
                .body("Request, world!")
                .build();
        assertThat(this.stringHandler.body(request)).isEqualTo("Request, world!");
    }

    @Test
    public void wrap_empty() {
        AwsProxyResponse response = this.stringHandler.wrap(null);
        assertThat(response.getStatusCode()).isEqualTo(Response.Status.NO_CONTENT.getStatusCode());
        response = this.stringHandler.wrap("");
        assertThat(response.getStatusCode()).isEqualTo(Response.Status.NO_CONTENT.getStatusCode());
    }

    @Test
    public void wrap_entity() {
        AResult result = new AResult();
        result.setValue("Joy42");
        AwsProxyResponse response = this.testHandler.wrap(result);
        assertThat(response.getStatusCode()).isEqualTo(Response.Status.OK.getStatusCode());
        assertThat(response.getBody()).isEqualTo("{\"value\":\"Joy42\"}");
    }

    @Test
    public void wrap_text() {
        AwsProxyResponse response = this.stringHandler.wrap("Response, world!");
        assertThat(response.getStatusCode()).isEqualTo(Response.Status.OK.getStatusCode());
        assertThat(response.getBody()).isEqualTo("Response, world!");
    }
}
