package org.teapot.backend.config.data;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.hateoas.core.EvoInflectorRelProvider;
import org.springframework.stereotype.Component;
import org.teapot.backend.model.Owner;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class OwnerRelProvider extends EvoInflectorRelProvider {

    @Override
    public String getCollectionResourceRelFor(Class<?> type) {
        return super.getCollectionResourceRelFor(Owner.class);
    }

    @Override
    public String getItemResourceRelFor(Class<?> type) {
        return super.getItemResourceRelFor(Owner.class);
    }

    @Override
    public boolean supports(Class<?> delimiter) {
        return Owner.class.isAssignableFrom(delimiter);
    }
}
