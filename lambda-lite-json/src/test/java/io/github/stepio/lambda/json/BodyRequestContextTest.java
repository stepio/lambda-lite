package io.github.stepio.lambda.json;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import io.github.stepio.lambda.json.test.ABody;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class BodyRequestContextTest {

    private Context context;
    private APIGatewayProxyRequestEvent request;
    private ABody body;
    private BodyRequestContext<ABody> requestContext;

    @Before
    public void setUp() {
        this.context = mock(Context.class);
        this.request = new APIGatewayProxyRequestEvent();
        this.body = new ABody();
        this.requestContext = new BodyRequestContext<>(this.body, this.request, this.context);
    }

    @Test
    public void getBodyWithDummy() {
        assertThat(this.requestContext.getBody()).isSameAs(this.body);
    }

    @Test
    public void getRequestWithDummy() {
        assertThat(this.requestContext.getRequest()).isSameAs(this.request);
    }

    @Test
    public void getContextWithDummy() {
        assertThat(this.requestContext.getContext()).isSameAs(this.context);
    }
}
