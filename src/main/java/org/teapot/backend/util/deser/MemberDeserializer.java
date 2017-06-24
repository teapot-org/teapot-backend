package org.teapot.backend.util.deser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.NumericNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.teapot.backend.model.organization.Member;
import org.teapot.backend.model.organization.MemberStatus;
import org.teapot.backend.model.organization.Organization;
import org.teapot.backend.model.user.User;
import org.teapot.backend.repository.organization.OrganizationRepository;
import org.teapot.backend.repository.user.UserRepository;

import java.io.IOException;

@Component
public class MemberDeserializer extends StdDeserializer<Member> {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    public MemberDeserializer() {
        this(null);
    }

    private MemberDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Member deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        TreeNode node = p.getCodec().readTree(p);

        NumericNode idNode = (NumericNode) node.get("id");
        Long id = idNode != null ? idNode.asLong() : null;

        NumericNode userIdNode = (NumericNode) node.get("userId");
        User user = userIdNode != null ? userRepository.findOne(userIdNode.asLong()) : null;

        TextNode statusNode = (TextNode) node.get("status");
        MemberStatus status = statusNode != null ? MemberStatus.valueOf(statusNode.asText()) : null;

        NumericNode organizationIdNode = (NumericNode) node.get("organizationId");
        Organization organization = organizationIdNode != null
                ? organizationRepository.findOne(organizationIdNode.asLong())
                : null;

        return new Member(
                id,
                user,
                status,
                organization,
                null
        );
    }
}
