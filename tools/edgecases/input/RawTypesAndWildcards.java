package edgecases;

import java.util.ArrayList;
import java.util.List;

public class RawTypesAndWildcards {
    public List copy(List<?> source) {
        List raw = new ArrayList();
        raw.addAll(source);
        return raw;
    }
}
