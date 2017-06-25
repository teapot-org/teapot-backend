package org.teapot.backend.util.ser;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.springframework.stereotype.Component;
import org.teapot.backend.model.user.User;

import java.io.IOException;

@Component
public class UserSerializer extends StdSerializer<User> {

    public UserSerializer()  {
        this(null);
    }

    private UserSerializer(Class<User> t) {
        super(t);
    }

    @Override
    public void serialize(User user, JsonGenerator gen, SerializerProvider provider)
            throws IOException {
        gen.writeStartObject();
        gen.writeObjectField("id", user.getId());
        gen.writeObjectField("username", user.getUsername());
        gen.writeObjectField("email", user.getEmail());
        gen.writeObjectField("isAvailable", user.isAvailable());
        gen.writeObjectField("isActivated", user.isActivated());
        gen.writeObjectField("firstName", user.getFirstName());
        gen.writeObjectField("lastName", user.getLastName());
        gen.writeObjectField("authority", user.getAuthority());
        gen.writeObjectField("registrationDate", user.getRegistrationDate());
        gen.writeObjectField("birthday", user.getBirthday());
        gen.writeObjectField("description", user.getDescription());
        gen.writeEndObject();
    }
}
