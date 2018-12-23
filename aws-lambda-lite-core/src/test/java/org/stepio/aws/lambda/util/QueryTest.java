package org.stepio.aws.lambda.util;

import com.amazonaws.serverless.proxy.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.model.MultiValuedTreeMap;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class QueryTest {

    @Test
    public void list_withDummy() {
        assertThatThrownBy(() -> Query.query(null, "dummy"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Request cannot be null");

        assertThat(Query.list(request(), "dummy")).isEqualTo(Collections.singletonList("value42"));

        List<String> list = new ArrayList<>();
        list.add("first");
        list.add("second");
        assertThat(Query.list(request(), "param")).isEqualTo(list);
    }

    @Test
    public void optional_withDummy() {
        assertThatThrownBy(() -> Query.query(null, "dummy"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Request cannot be null");

        Optional<String> result = Query.optional(request(), "dummy");
        assertThat(result.isPresent()).isTrue();
        assertThat(result.get()).isEqualTo("value42");

        assertThat(Query.optional(request(), "invalid").isPresent()).isFalse();
    }

    @Test
    public void required_withDummy() {
        assertThatThrownBy(() -> Query.query(null, "dummy"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Request cannot be null");

        assertThat(Query.required(request(), "dummy")).isEqualTo("value42");
        assertThatThrownBy(() -> Query.required(request(), "invalid"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Query parameter 'invalid' is mandatory");
    }

    @Test
    public void query_withDummy() {
        assertThatThrownBy(() -> Query.query(null, "dummy"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Request cannot be null");


        assertThat(Query.query(request(), "dummy")).isEqualTo("value42");
        assertThat(Query.query(request(), "invalid")).isNull();
    }

    @Test
    public void validate_withDummy() {
        Query.validate(new AwsProxyRequest());
        assertThatThrownBy(() -> Query.validate(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Request cannot be null");
    }

    private AwsProxyRequest request() {
        AwsProxyRequest request = new AwsProxyRequest();
        MultiValuedTreeMap<String, String> query = new MultiValuedTreeMap<>();
        query.add("dummy", "value42");
        query.addAll("param", "first", "second");
        request.setMultiValueQueryStringParameters(query);
        return request;
    }
}
