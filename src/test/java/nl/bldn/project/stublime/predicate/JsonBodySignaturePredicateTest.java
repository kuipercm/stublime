package nl.bldn.project.stublime.predicate;

import static java.util.Collections.emptyList;
import static nl.bldn.project.stublime.model.ResponseKey.DEFAULT_DASH_STRING_JOINER;
import static nl.bldn.project.stublime.model.ResponseKey.DEFAULT_EMPTY_STRING_JOINER;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.jayway.jsonpath.JsonPath;

public class JsonBodySignaturePredicateTest {
    @Test(expected = NullPointerException.class)
    public void given_a_null_joining_expression_throws_exception() {
        new JsonBodySignaturePredicate(null, null, emptyList());
    }

    @Test(expected = NullPointerException.class)
    public void given_a_null_expression_list_throws_exception() {
        new JsonBodySignaturePredicate(null, DEFAULT_EMPTY_STRING_JOINER, null);
    }

    @Test
    public void given_a_null_expected_signature_the_predicate_always_returns_true() {
        JsonBodySignaturePredicate sut = new JsonBodySignaturePredicate(null, DEFAULT_EMPTY_STRING_JOINER, emptyList());

        assertThat(sut.test(null)).isTrue();
        assertThat(sut.test("")).isTrue();
        assertThat(sut.test("hello world")).isTrue();
    }

    @Test
    public void given_a_non_null_expected_signature_and_a_null_request_body_the_predicate_always_returns_false() {
        JsonBodySignaturePredicate sut = new JsonBodySignaturePredicate("", DEFAULT_EMPTY_STRING_JOINER, emptyList());

        assertThat(sut.test(null)).isFalse();
    }

    @Test
    public void given_a_non_null_request_body_and_an_empty_string_expected_signature_and_empty_expression_list_the_signature_must_match_the_evaluated_expressions() {
        JsonBodySignaturePredicate sut = new JsonBodySignaturePredicate("", DEFAULT_EMPTY_STRING_JOINER, emptyList());
        assertThat(sut.test("")).isTrue();
        assertThat(sut.test("hello world")).isTrue();
    }

    @Test
    public void given_a_non_null_request_body_and_a_non_empty_expected_signature_and_empty_expression_list_the_signature_must_match_the_evaluated_expressions() {
        JsonBodySignaturePredicate sut = new JsonBodySignaturePredicate("a", DEFAULT_EMPTY_STRING_JOINER, emptyList());
        assertThat(sut.test("")).isFalse();
        assertThat(sut.test("hello world")).isFalse();
    }

    @Test
    public void given_a_non_null_request_body_and_a_non_empty_expected_signature_and_non_empty_expression_list_the_signature_must_match_the_evaluated_expressions() {
        List<JsonPath> bodyExpressions = new ArrayList<>();
        bodyExpressions.add(JsonPath.compile("$[0]"));

        JsonBodySignaturePredicate sut = new JsonBodySignaturePredicate("a", DEFAULT_EMPTY_STRING_JOINER, bodyExpressions);
        assertThat(sut.test("[a,b,c]")).isTrue();
        assertThat(sut.test("hello world")).isFalse();
    }

    @Test
    public void given_a_non_null_request_body_and_a_non_empty_expected_signature_and_non_empty_expression_list_the_signature_must_match_the_evaluated_joined_expressions() {
        List<JsonPath> bodyExpressions = new ArrayList<>();
        bodyExpressions.add(JsonPath.compile("$[0]"));
        bodyExpressions.add(JsonPath.compile("$[2]"));

        JsonBodySignaturePredicate sut = new JsonBodySignaturePredicate("a-c", DEFAULT_DASH_STRING_JOINER, bodyExpressions);
        assertThat(sut.test("[a,b,c]")).isTrue();
        assertThat(sut.test("hello world")).isFalse();
    }

    @Test
    public void given_a_non_null_request_body_and_a_non_empty_expected_signature_and_non_empty_expression_list_the_json_body_must_be_valid_json() {
        List<JsonPath> bodyExpressions = new ArrayList<>();
        bodyExpressions.add(JsonPath.compile("$['abc']"));

        JsonBodySignaturePredicate sut = new JsonBodySignaturePredicate("5", DEFAULT_DASH_STRING_JOINER, bodyExpressions);
        assertThat(sut.test("{'abc:5}")).isFalse();
    }

}