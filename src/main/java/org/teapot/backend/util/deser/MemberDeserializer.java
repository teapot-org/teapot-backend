package org.teapot.backend.util.deser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ValueNode;
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

        ValueNode idNode = (ValueNode) node.get("id");
        Long id = (idNode instanceof NullNode) || (idNode == null)
                ? null
                : idNode.asLong();

        ValueNode userIdNode = (ValueNode) node.get("userId");
        User user = (userIdNode instanceof NullNode) || (userIdNode == null)
                ? null
                : userRepository.findOne(userIdNode.asLong());

        ValueNode statusNode = (ValueNode) node.get("status");
        MemberStatus status = (statusNode instanceof NullNode) || (statusNode == null)
                ? null
                : MemberStatus.valueOf(statusNode.asText());

        ValueNode organizationIdNode = (ValueNode) node.get("organizationId");
        Organization organization = (organizationIdNode instanceof NullNode) || (organizationIdNode == null)
                ? null
                : organizationRepository.findOne(organizationIdNode.asLong());

        return new Member(
                id,
                user,
                status,
                organization,
                null
        );
    }
}
