package org.teapot.backend.util.ser;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.teapot.backend.model.organization.Member;
import org.teapot.backend.util.LinkBuilder;

import java.io.IOException;

@Component
public class MemberSerializer extends StdSerializer<Member> {

    @Autowired
    private LinkBuilder linkBuilder;

    public MemberSerializer() {
        this(null);
    }

    private MemberSerializer(Class<Member> t) {
        super(t);
    }

    @Override
    public void serialize(Member member, JsonGenerator gen, SerializerProvider provider)
            throws IOException {
        gen.writeStartObject();
        gen.writeObjectField("id", member.getId());
        gen.writeStringField("user",
                linkBuilder.format("/users/%d", member.getUser().getId()));
        gen.writeObjectField("status", member.getStatus());
        gen.writeStringField("organization",
                linkBuilder.format("/organizations/%d", member.getOrganization().getId()));
        gen.writeObjectField("admissionDate", member.getAdmissionDate());
        gen.writeEndObject();
    }
}
