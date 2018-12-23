package org.stepio.aws.lambda;

import com.amazonaws.serverless.proxy.internal.testutils.AwsProxyRequestBuilder;
import com.amazonaws.serverless.proxy.internal.testutils.MockLambdaContext;
import com.amazonaws.serverless.proxy.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.model.AwsProxyResponse;
import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.junit.Before;
import org.junit.Test;
import org.stepio.aws.lambda.test.ABody;
import org.stepio.aws.lambda.test.APart;
import org.stepio.aws.lambda.test.AResult;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.Response;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.stepio.aws.lambda.util.Query.required;

public class AbstractLambdaHandlerTest {

    private AbstractLambdaHandler handler;
    private DefaultLambdaHandler<ABody, AResult> defaultHandler;
    private Context lambdaContext;
    private ObjectMapper objectMapper;

    @Before
    public void setUp() {
        this.handler = new DefaultLambdaHandler();
        this.defaultHandler = new DefaultLambdaHandler<ABody, AResult>(ABody.class, AResult.class) {
            @Override
            protected AResult doGetImpl(AwsProxyRequest request, Context context) {
                AResult result = new AResult();
                result.setValue(
                        format("Get with %s received",
                                required(request, "element")
                        )
                );
                return result;
            }
        };
        this.lambdaContext = new MockLambdaContext();
        this.objectMapper = new ObjectMapper();
    }

    @Test
    public void reader_caches() {
        ObjectReader reader1 = this.handler.reader(ABody.class);
        ABody body = dummy();
        ObjectReader reader2 = this.handler.reader(body.getClass());
        assertThat(reader2).isSameAs(reader1);
    }

    @Test
    public void writer_caches() {
        ObjectWriter writer1 = this.handler.writer(ABody.class);
        ABody body = dummy();
        ObjectWriter writer2 = this.handler.writer(body.getClass());
        assertThat(writer2).isSameAs(writer1);
    }

    @Test
    public void writeDummy_thenRead() throws IOException {
        ABody body = dummy();
        ObjectWriter writer = this.handler.writer(body.getClass());
        String text = writer.writeValueAsString(body);
        ObjectReader reader = this.handler.reader(ABody.class);
        ABody other = reader.readValue(text);
        assertThat(other).isEqualToComparingFieldByFieldRecursively(body);
    }

    @Test
    public void writeEmpty_thenRead() throws IOException {
        ABody empty = new ABody();
        ObjectReader reader = this.handler.reader(ABody.class);
        ABody other = reader.readValue("{}");
        assertThat(other).isEqualToComparingFieldByFieldRecursively(empty);
    }

    @Test
    public void handleRequest_unsupportedMethod() {
        InputStream requestStream = new AwsProxyRequestBuilder("/any/path", HttpMethod.POST)
                .buildStream();
        AwsProxyResponse response = handle(requestStream);
        assertThat(response).isNotNull()
                .hasFieldOrPropertyWithValue("statusCode", Response.Status.METHOD_NOT_ALLOWED.getStatusCode());

        requestStream = new AwsProxyRequestBuilder("/any/path", HttpMethod.OPTIONS)
                .buildStream();
        response = handle(requestStream);
        assertThat(response).isNotNull()
                .hasFieldOrPropertyWithValue("statusCode", Response.Status.METHOD_NOT_ALLOWED.getStatusCode());
    }

    @Test
    public void handleRequest_missingParameter() {
        InputStream requestStream = new AwsProxyRequestBuilder("/any/path", HttpMethod.GET)
                .buildStream();
        AwsProxyResponse response = handle(requestStream);
        assertThat(response).isNotNull()
                .hasFieldOrPropertyWithValue("statusCode", Response.Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    public void handleRequest_success() {
        InputStream requestStream = new AwsProxyRequestBuilder("/any/path", HttpMethod.GET)
                .queryString("element", "Ra")
                .buildStream();
        AwsProxyResponse response = handle(requestStream);
        assertThat(response).isNotNull()
                .hasFieldOrPropertyWithValue("statusCode", Response.Status.OK.getStatusCode())
                .hasFieldOrPropertyWithValue("body", "{\"value\":\"Get with Ra received\"}");
    }

    private AwsProxyResponse handle(InputStream requestStream) {
        ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
        try {
            this.defaultHandler.handleRequest(requestStream, responseStream, this.lambdaContext);
            return this.objectMapper.readValue(responseStream.toByteArray(), AwsProxyResponse.class);
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    private ABody dummy() {
        ABody body = new ABody();
        body.setName("dummy");
        body.setQuantity(42);
        List<APart> elements = new ArrayList<>();
        APart element = new APart();
        element.setValue("hello");
        elements.add(element);
        element = new APart();
        element.setValue("world");
        elements.add(element);
        body.setElements(elements);
        return body;
    }
}
