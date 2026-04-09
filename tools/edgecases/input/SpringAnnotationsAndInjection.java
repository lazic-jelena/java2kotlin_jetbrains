package edgecases;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SpringAnnotationsAndInjection {
    private final Helper helper;

    @Autowired
    public SpringAnnotationsAndInjection(Helper helper) {
        this.helper = helper;
    }

    public String greet(String name) {
        return helper.prefix() + name;
    }

    public interface Helper {
        String prefix();
    }
}
