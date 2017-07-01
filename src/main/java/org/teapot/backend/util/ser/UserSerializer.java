package org.teapot.backend.util.ser;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.teapot.backend.model.OwnerType;
import org.teapot.backend.model.user.User;
import org.teapot.backend.util.LinkBuilder;

import java.io.IOException;

@Component
public class UserSerializer extends StdSerializer<User> {

    @Autowired
    private LinkBuilder linkBuilder;

    public UserSerializer() {
        this(null);
    }

    private UserSerializer(Class<User> t) {
        super(t);
    }

    @Override
    public void serialize(User user, JsonGenerator gen, SerializerProvider provider)
            throws IOException {
        gen.writeStartObject();
        gen.writeObjectField("type", OwnerType.USER);
        gen.writeObjectField("id", user.getId());
        gen.writeObjectField("name", user.getName());
        gen.writeObjectField("email", user.getEmail());
        gen.writeObjectField("isAvailable", user.isAvailable());
        gen.writeObjectField("isActivated", user.isActivated());
        gen.writeObjectField("firstName", user.getFirstName());
        gen.writeObjectField("lastName", user.getLastName());
        gen.writeObjectField("authority", user.getAuthority());
        gen.writeObjectField("registrationDateTime", user.getRegistrationDateTime());
        gen.writeObjectField("birthday", user.getBirthday());
        gen.writeObjectField("description", user.getDescription());
        gen.writeObjectField("organizations",
                linkBuilder.format("/organizations?user=%d", user.getId()));
        gen.writeObjectField("boards",
                linkBuilder.format("/boards?owner=%d", user.getId()));
        gen.writeEndObject();
    }
}
