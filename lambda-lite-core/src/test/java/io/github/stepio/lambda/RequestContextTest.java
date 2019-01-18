package io.github.stepio.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class RequestContextTest {

    private Context context;
    private APIGatewayProxyRequestEvent request;
    private RequestContext requestContext;

    @Before
    public void setUp() {
        this.context = mock(Context.class);
        this.request = new APIGatewayProxyRequestEvent();
        this.requestContext = new RequestContext(this.request, this.context);
    }

    @Test
    public void getRequest_withDummy() {
        assertThat(this.requestContext.getRequest()).isSameAs(this.request);
    }

    @Test
    public void getContext_withDummy() {
        assertThat(this.requestContext.getContext()).isSameAs(this.context);
    }
}
