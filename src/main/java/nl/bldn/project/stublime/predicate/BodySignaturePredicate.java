package nl.bldn.project.stublime.predicate;

import java.util.List;
import java.util.function.Predicate;

public interface BodySignaturePredicate<T> extends Predicate<String> {

    List<T> getBodySignatureExpressions();
    Class<T> getSignatureExpressionKlazz();

}
