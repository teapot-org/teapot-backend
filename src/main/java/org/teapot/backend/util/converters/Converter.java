package org.teapot.backend.util.converters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.teapot.backend.dto.MemberDto;
import org.teapot.backend.dto.OrganizationDto;
import org.teapot.backend.model.organization.Member;
import org.teapot.backend.model.organization.Organization;
import org.teapot.backend.model.user.User;

import java.util.Set;

@Component
public class Converter {

    @Configuration
    class Config {
        @Bean
        @Autowired
        public RestTemplate restTemplate(RestTemplateBuilder builder) {
            return builder.build();
        }
    }

    @Autowired
    private RestTemplate template;

    public Organization convert(OrganizationDto source) {
        Set<Member> members = (Set<Member>)template.getForObject(source.getMembers().toString(), Set.class);
        return new Organization(
                source.getId(),
                source.getName(),
                source.getFullName(),
                source.getCreationDate(),
                members
        );
    }

    public OrganizationDto convert(Organization source) {
        return new OrganizationDto(
                source.getId(),
                source.getName(),
                source.getFullName(),
                source.getCreationDate(),
                String.format("/organizations/%s/members", source.getId())
        );
    }

    public Member convert(MemberDto source) {
        User user = template.getForObject(source.getUser().toString(), User.class);
        Organization organization = template.getForObject(source.getOrganization().toString(), Organization.class);
        return new Member(
                source.getId(),
                user,
                source.getStatus(),
                organization,
                source.getAdmissionDate()
        );
    }

    public MemberDto convert(Member source) {
        return new MemberDto(
                source.getId(),
                String.format("/users/%d", source.getId()),
                source.getStatus(),
                String.format("/organizations/%d", source.getOrganization().getId()),
                source.getAdmissionDate()
        );
    }
}
