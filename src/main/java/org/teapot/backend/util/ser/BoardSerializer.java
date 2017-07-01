package org.teapot.backend.util.ser;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.teapot.backend.model.Board;
import org.teapot.backend.model.Owner;
import org.teapot.backend.model.organization.Organization;
import org.teapot.backend.model.user.User;
import org.teapot.backend.util.LinkBuilder;

import java.io.IOException;

@Component
public class BoardSerializer extends StdSerializer<Board> {

    @Autowired
    private LinkBuilder linkBuilder;

    public BoardSerializer() {
        this(null);
    }

    private BoardSerializer(Class<Board> t) {
        super(t);
    }

    @Override
    public void serialize(Board board, JsonGenerator gen, SerializerProvider provider)
            throws IOException {
        gen.writeStartObject();
        gen.writeObjectField("id", board.getId());
        gen.writeObjectField("title", board.getTitle());
        gen.writeObjectField("owner", getOwnerLink(board.getOwner()));
        gen.writeEndObject();
    }

    private String getOwnerLink(Owner owner) {
        String ownerType = null;

        if (owner instanceof User) {
            ownerType = "users";
        } else if (owner instanceof Organization) {
            ownerType = "users";
        }

        return (ownerType != null)
                ? linkBuilder.format("/%s/%d", ownerType, owner.getId())
                : null;
    }
}
