package nl.bldn.project.stublime.predicate;

import static java.util.Collections.emptyList;
import static nl.bldn.project.stublime.model.ResponseKey.DEFAULT_DASH_STRING_JOINER;
import static nl.bldn.project.stublime.model.ResponseKey.DEFAULT_EMPTY_STRING_JOINER;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.junit.Test;

public class XmlBodySignaturePredicateTest {
    private static final XPathFactory xPathFactory = XPathFactory.newInstance();

    @Test(expected = NullPointerException.class)
    public void given_a_null_joining_expression_throws_exception() {
        new XmlBodySignaturePredicate(null, null, emptyList());
    }

    @Test(expected = NullPointerException.class)
    public void given_a_null_expression_list_throws_exception() {
        new XmlBodySignaturePredicate(null, DEFAULT_EMPTY_STRING_JOINER, null);
    }

    @Test
    public void given_a_null_expected_signature_the_predicate_always_returns_true() {
        XmlBodySignaturePredicate sut = new XmlBodySignaturePredicate(null, DEFAULT_EMPTY_STRING_JOINER, emptyList());

        assertThat(sut.test(null)).isTrue();
        assertThat(sut.test("")).isTrue();
        assertThat(sut.test("hello world")).isTrue();
    }

    @Test
    public void given_a_non_null_expected_signature_and_a_null_request_body_the_predicate_always_returns_false() {
        XmlBodySignaturePredicate sut = new XmlBodySignaturePredicate("", DEFAULT_EMPTY_STRING_JOINER, emptyList());

        assertThat(sut.test(null)).isFalse();
    }

    @Test
    public void given_a_non_null_request_body_and_an_empty_string_expected_signature_and_empty_expression_list_the_signature_must_match_the_evaluated_expressions() {
        XmlBodySignaturePredicate sut = new XmlBodySignaturePredicate("", DEFAULT_EMPTY_STRING_JOINER, emptyList());
        assertThat(sut.test("")).isTrue();
        assertThat(sut.test("hello world")).isTrue();
    }

    @Test
    public void given_a_non_null_request_body_and_a_non_empty_expected_signature_and_empty_expression_list_the_signature_must_match_the_evaluated_expressions() {
        XmlBodySignaturePredicate sut = new XmlBodySignaturePredicate("a", DEFAULT_EMPTY_STRING_JOINER, emptyList());
        assertThat(sut.test("")).isFalse();
        assertThat(sut.test("hello world")).isFalse();
    }

    @Test
    public void given_a_non_null_request_body_and_a_non_empty_expected_signature_and_non_empty_expression_list_the_signature_must_match_the_evaluated_expressions() {
        List<XPathExpression> bodyExpressions = new ArrayList<>();
        bodyExpressions.add(toXPath("/parent/child[1]"));

        XmlBodySignaturePredicate sut = new XmlBodySignaturePredicate("a", DEFAULT_EMPTY_STRING_JOINER, bodyExpressions);
        assertThat(sut.test("<parent><child>a</child><child>b</child><child>c</child></parent>")).isTrue();
        assertThat(sut.test("hello world")).isFalse();
    }

    @Test
    public void given_a_non_null_request_body_and_a_non_empty_expected_signature_and_non_empty_expression_list_the_signature_must_match_the_evaluated_joined_expressions() {
        List<XPathExpression> bodyExpressions = new ArrayList<>();
        bodyExpressions.add(toXPath("/parent/child[1]"));
        bodyExpressions.add(toXPath("/parent/child[3]"));

        XmlBodySignaturePredicate sut = new XmlBodySignaturePredicate("a-c", DEFAULT_DASH_STRING_JOINER, bodyExpressions);
        assertThat(sut.test("<parent><child>a</child><child>b</child><child>c</child></parent>")).isTrue();
        assertThat(sut.test("hello world")).isFalse();
    }

    @Test
    public void given_a_non_null_request_body_and_a_non_empty_expected_signature_and_non_empty_expression_list_the_json_body_must_be_valid_json() {
        List<XPathExpression> bodyExpressions = new ArrayList<>();
        bodyExpressions.add(toXPath("/parent/child[id=123]/name"));

        XmlBodySignaturePredicate sut = new XmlBodySignaturePredicate("a", DEFAULT_DASH_STRING_JOINER, bodyExpressions);
        assertThat(sut.test("<parent><<child><id>123</id><name>a</name></child></parent>")).isFalse();
    }

    @Test
    public void given_a_non_null_request_body_and_a_non_empty_expected_signature_and_non_empty_expression_list_the_body_and_expression_can_be_complex() {
        List<XPathExpression> bodyExpressions = new ArrayList<>();
        bodyExpressions.add(toXPath("/parent/child[id=123]/name"));

        XmlBodySignaturePredicate sut = new XmlBodySignaturePredicate("a", DEFAULT_DASH_STRING_JOINER, bodyExpressions);
        assertThat(sut.test("<parent><child><id>123</id><name>a</name></child></parent>")).isTrue();
    }

    @Test
    public void given_a_non_null_request_body_and_a_non_empty_expected_signature_and_non_empty_expression_list_the_elements_must_exist_in_the_body() {
        List<XPathExpression> bodyExpressions = new ArrayList<>();
        bodyExpressions.add(toXPath("/john/doe"));

        XmlBodySignaturePredicate sut = new XmlBodySignaturePredicate("a", DEFAULT_DASH_STRING_JOINER, bodyExpressions);
        assertThat(sut.test("<parent><child><id>123</id><name>a</name></child></parent>")).isFalse();
    }

    private static XPathExpression toXPath(String input) {
        try {
            return xPathFactory.newXPath().compile(input);
        } catch (XPathExpressionException e) {
            throw new RuntimeException(e);
        }
    }
}