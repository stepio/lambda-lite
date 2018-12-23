package org.stepio.aws.lambda.util;

import com.amazonaws.serverless.proxy.model.AwsProxyResponse;
import com.amazonaws.serverless.proxy.model.MultiValuedTreeMap;
import org.junit.Test;

import java.util.Collections;

import static javax.ws.rs.core.Response.Status.CREATED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

public class ResponsesTest {

    @Test
    public void ok_checkStatus() {
        assertThat(Responses.ok("dummy").getStatusCode()).isEqualTo(200);
        assertThat(Responses.ok("dummy7").getBody()).isEqualTo("dummy7");
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
        AwsProxyResponse response = Responses.status(CREATED);
        assertThat(response.getStatusCode()).isEqualTo(201);
        assertThat(response.getBody()).isNull();
    }

    @Test
    public void status_withStatusCode() {
        AwsProxyResponse response = Responses.status(201);
        assertThat(response.getStatusCode()).isEqualTo(201);
        assertThat(response.getBody()).isNull();
    }

    @Test
    public void response_custom() {
        MultiValuedTreeMap<String, String> headers = new MultiValuedTreeMap<>();
        headers.add("Content-Type", "text/html;charset=UTF-8");
        headers.add("Content-Encoding", "gzip");
        String body = "dummy response body";
        AwsProxyResponse response = Responses.response(409, headers, body);
        assertThat(response.getStatusCode()).isEqualTo(409);
        assertThat(response.getMultiValueHeaders())
                .contains(
                        entry("Content-Type", Collections.singletonList("text/html;charset=UTF-8")),
                        entry("Content-Encoding", Collections.singletonList("gzip"))
                );
        assertThat(response.getBody()).isEqualTo(body);
    }
}
