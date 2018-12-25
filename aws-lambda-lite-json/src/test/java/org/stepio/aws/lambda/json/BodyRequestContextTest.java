package org.stepio.aws.lambda.json;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import org.junit.Before;
import org.junit.Test;
import org.stepio.aws.lambda.RequestContext;
import org.stepio.aws.lambda.json.test.ABody;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
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
    public void getBody_withDummy() {
        assertThat(this.requestContext.getBody()).isSameAs(this.body);
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
