package org.teapot.backend.util.ser;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.teapot.backend.model.OwnerType;
import org.teapot.backend.model.organization.Organization;
import org.teapot.backend.util.LinkBuilder;

import java.io.IOException;

@Component
public class OrganizationSerializer extends StdSerializer<Organization> {

    @Autowired
    private LinkBuilder linkBuilder;

    public OrganizationSerializer() {
        this(null);
    }

    private OrganizationSerializer(Class<Organization> t) {
        super(t);
    }

    @Override
    public void serialize(Organization org, JsonGenerator gen, SerializerProvider provider)
            throws IOException {
        gen.writeStartObject();
        gen.writeObjectField("type", OwnerType.ORGANIZATION);
        gen.writeObjectField("id", org.getId());
        gen.writeObjectField("name", org.getName());
        gen.writeObjectField("fullName", org.getFullName());
        gen.writeObjectField("registrationDateTime", org.getRegistrationDateTime());
        gen.writeStringField("members",
                linkBuilder.format("/organizations/%d/members", org.getId()));
        gen.writeStringField("boards",
                linkBuilder.format("/boards?owner=%d", org.getId()));
        gen.writeEndObject();
    }
}
