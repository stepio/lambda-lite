package io.github.stepio.lambda.util;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import io.github.stepio.lambda.enums.MediaType;
import io.github.stepio.lambda.enums.Status;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

public class ResponsesTest {

    @Test
    public void ok_checkStatus() {
        APIGatewayProxyResponseEvent responseEvent = Responses.ok(MediaType.TEXT_PLAIN, "dummy7");
        assertThat(responseEvent.getStatusCode()).isEqualTo(200);
        assertThat(responseEvent.getBody()).isEqualTo("dummy7");
        assertThat(responseEvent.getHeaders()).contains(entry("Content-Type", "text/plain"));
    }

    @Test
    public void noContent_checkStatus() {
        assertThat(Responses.noContent().getStatusCode()).isEqualTo(204);
        assertThat(Responses.noContent().getBody()).isNull();
    }

    @Test
    public void badRequest_checkStatus() {
        assertThat(Responses.badRequest().getStatusCode()).isEqualTo(400);
        assertThat(Responses.badRequest().getBody()).isNull();
    }

    @Test
    public void notFound_checkStatus() {
        assertThat(Responses.notFound().getStatusCode()).isEqualTo(404);
        assertThat(Responses.notFound().getBody()).isNull();
    }

    @Test
    public void methodNotAllowed_checkStatus() {
        assertThat(Responses.methodNotAllowed().getStatusCode()).isEqualTo(405);
        assertThat(Responses.methodNotAllowed().getBody()).isNull();
    }

    @Test
    public void internalServerError_checkStatus() {
        assertThat(Responses.internalServerError().getStatusCode()).isEqualTo(500);
        assertThat(Responses.internalServerError().getBody()).isNull();
    }

    @Test
    public void status_withStatus() {
        APIGatewayProxyResponseEvent response = Responses.status(Status.CREATED);
        assertThat(response.getStatusCode()).isEqualTo(201);
        assertThat(response.getBody()).isNull();
    }

    @Test
    public void status_withStatusCode() {
        APIGatewayProxyResponseEvent response = Responses.status(201);
        assertThat(response.getStatusCode()).isEqualTo(201);
        assertThat(response.getBody()).isNull();
    }

    @Test
    public void response_custom() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "text/html;charset=UTF-8");
        headers.put("Content-Encoding", "gzip");
        String body = "dummy response body";
        APIGatewayProxyResponseEvent response = Responses.response(409, headers, body);
        assertThat(response.getStatusCode()).isEqualTo(409);
        assertThat(response.getHeaders())
                .contains(
                        entry("Content-Type", "text/html;charset=UTF-8"),
                        entry("Content-Encoding", "gzip")
                );
        assertThat(response.getBody()).isEqualTo(body);
    }
}
