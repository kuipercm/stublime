package nl.bldn.project.stublime.predicate;

import static java.util.Collections.emptyList;
import static nl.bldn.project.stublime.model.ResponseKey.DEFAULT_EMPTY_STRING_JOINER;
import static nl.bldn.project.stublime.predicate.BodySignaturePredicateFactory.BODY_TYPE_JSON;
import static nl.bldn.project.stublime.predicate.BodySignaturePredicateFactory.BODY_TYPE_XML;
import static nl.bldn.project.stublime.predicate.BodySignaturePredicateFactory.createBodySignaturePredicate;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class BodySignaturePredicateFactoryTest {

    @SuppressWarnings("unchecked")
    @Test
    public void when_setting_response_then_jsonpath_expressions_are_compiled_and_set_as_predicate_when_bodytype_is_json() {
        List<String> bodySignatureExpressions = new ArrayList<>();
        bodySignatureExpressions.add("$[0].id");
        bodySignatureExpressions.add("$[2].name");

        BodySignaturePredicate<?> bodySignaturePredicate = createBodySignaturePredicate(BODY_TYPE_JSON, "", DEFAULT_EMPTY_STRING_JOINER, bodySignatureExpressions);

        assertThat(bodySignaturePredicate).isNotNull();

        List<?> signatureExpressions = bodySignaturePredicate.getBodySignatureExpressions();
        assertThat(signatureExpressions).isNotNull();
        assertThat(signatureExpressions).isNotEmpty();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void when_setting_response_then_xpath_expressions_are_compiled_and_set_as_predicate_when_bodytype_is_xml() {
        List<String> bodySignatureExpressions = new ArrayList<>();
        bodySignatureExpressions.add("/body/id");
        bodySignatureExpressions.add("/body/name");

        BodySignaturePredicate<?> bodySignaturePredicate = createBodySignaturePredicate(BODY_TYPE_XML, "", DEFAULT_EMPTY_STRING_JOINER, bodySignatureExpressions);

        assertThat(bodySignaturePredicate).isNotNull();

        List<?> signatureExpressions = bodySignaturePredicate.getBodySignatureExpressions();
        assertThat(signatureExpressions).isNotNull();
        assertThat(signatureExpressions).isNotEmpty();
    }

    @Test(expected = IllegalArgumentException.class)
    public void unknown_body_type_throws_exception() {
        createBodySignaturePredicate("unknown", "", DEFAULT_EMPTY_STRING_JOINER, emptyList());
    }
}