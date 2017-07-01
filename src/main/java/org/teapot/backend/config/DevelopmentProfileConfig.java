package org.teapot.backend.config;

import org.h2.server.web.WebServlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.teapot.backend.model.Board;
import org.teapot.backend.model.meta.TeapotAction;
import org.teapot.backend.model.meta.TeapotProperty;
import org.teapot.backend.model.meta.TeapotResource;
import org.teapot.backend.model.organization.Member;
import org.teapot.backend.model.organization.MemberStatus;
import org.teapot.backend.model.organization.Organization;
import org.teapot.backend.model.user.User;
import org.teapot.backend.model.user.UserAuthority;
import org.teapot.backend.repository.BoardRepository;
import org.teapot.backend.repository.meta.TeapotActionRepository;
import org.teapot.backend.repository.meta.TeapotPropertyRepository;
import org.teapot.backend.repository.meta.TeapotResourceRepository;
import org.teapot.backend.repository.organization.MemberRepository;
import org.teapot.backend.repository.organization.OrganizationRepository;
import org.teapot.backend.repository.user.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;


@Configuration
@Profile("development")
public class DevelopmentProfileConfig {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TeapotPropertyRepository propertyRepository;

    @Autowired
    private TeapotActionRepository actionRepository;

    @Autowired
    private TeapotResourceRepository resourceRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BoardRepository boardRepository;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    ServletRegistrationBean h2ServletRegistrationBean() {
        ServletRegistrationBean registrationBean =
                new ServletRegistrationBean(new WebServlet());

        registrationBean.addUrlMappings("/h2/*");

        return registrationBean;
    }

    @Bean
    public CommandLineRunner loadData() {
        return args -> {
            registerAdmin();
            registerDaleCooper();
            registerLoraPalmer();
            registerSherlockHolmes();
            registerDoctorWatson();

            addProperties();
            addResources();
            addActions();
            addOrganizations();
            addMembers();
            addBoards();
        };
    }

    private void registerAdmin() {
        User admin = new User();

        admin.setName("admin");
        admin.setEmail("admin@teapot.org");
        admin.setPassword(passwordEncoder.encode("1234"));
        admin.setActivated(true);
        admin.setFirstName("Cake");
        admin.setLastName("Lover");
        admin.setRegistrationDateTime(LocalDateTime.now());
        admin.setBirthday(LocalDate.now());
        admin.setDescription("i manage everything");
        admin.setAuthority(UserAuthority.ADMIN);

        userRepository.save(admin);
    }

    private void registerDaleCooper() {
        User user = new User();

        user.setName("dale_cooper");
        user.setEmail("dale_cooper@twin.peaks");
        user.setPassword(passwordEncoder.encode("1234"));
        user.setActivated(true);
        user.setFirstName("Dale");
        user.setLastName("Cooper");
        user.setRegistrationDateTime(LocalDateTime.now());
        user.setBirthday(LocalDate.now());
        user.setDescription("a special FBI agent");
        user.setAuthority(UserAuthority.USER);

        userRepository.save(user);
    }

    private void registerLoraPalmer() {
        User user = new User();

        user.setName("lora_palmer");
        user.setEmail("lora_palmer@twin.peaks");
        user.setPassword(passwordEncoder.encode("1234"));
        user.setActivated(true);
        user.setFirstName("Lora");
        user.setLastName("Palmer");
        user.setRegistrationDateTime(LocalDateTime.now());
        user.setBirthday(LocalDate.now());
        user.setDescription("a dead girl");
        user.setAuthority(UserAuthority.USER);

        userRepository.save(user);
    }

    private void registerSherlockHolmes() {
        User user = new User();

        user.setName("sherlock_holmes");
        user.setEmail("sherlock_holmes@baker.st");
        user.setPassword(passwordEncoder.encode("1234"));
        user.setActivated(true);
        user.setFirstName("Sherlock");
        user.setLastName("Holmes");
        user.setRegistrationDateTime(LocalDateTime.now());
        user.setBirthday(LocalDate.now());
        user.setDescription("private detective");
        user.setAuthority(UserAuthority.USER);

        userRepository.save(user);
    }

    private void registerDoctorWatson() {
        User user = new User();

        user.setName("dr_watson");
        user.setEmail("dr_watson@baker.st");
        user.setPassword(passwordEncoder.encode("1234"));
        user.setActivated(true);
        user.setFirstName("John");
        user.setLastName("Watson");
        user.setRegistrationDateTime(LocalDateTime.now());
        user.setBirthday(LocalDate.now());
        user.setDescription("Sherlock Holmes' mate");
        user.setAuthority(UserAuthority.USER);

        userRepository.save(user);
    }

    private void addProperties() {
        TeapotProperty property1 = new TeapotProperty();
        TeapotProperty property2 = new TeapotProperty();

        property1.setName("verification-token-expire-days");
        property1.setValue("1");

        property2.setName("site-uri");
        property2.setValue("http://localhost:8080");

        propertyRepository.save(property1);
        propertyRepository.save(property2);
    }

    private void addResources() {
        TeapotResource resource1 = new TeapotResource();

        resource1.setName("user");
        resource1.setUri("/users");
        resource1.setDescription(
                "Available methods:\n\nGET /users/{id|username}\n" +
                "POST /users\nPUT /users\nDELETE /users/{id}\n\n");

        resourceRepository.save(resource1);
    }

    private void addActions() {
        TeapotAction action1 = new TeapotAction();
        TeapotAction action2 = new TeapotAction();

        action1.setName("help");
        action1.setUsage("/actions/help?resource={name|id}|action={name|id}");

        action2.setName("activate");
        action2.setUsage("/actions/activate?token={token}");

        actionRepository.save(Arrays.asList(action1, action2));
    }

    private void addOrganizations() {
        Organization teapot = new Organization();
        teapot.setName("teapot");
        teapot.setRegistrationDateTime(LocalDateTime.now());
        organizationRepository.save(teapot);

        Organization someOrganization = new Organization();
        someOrganization.setName("someOrganization");
        someOrganization.setRegistrationDateTime(LocalDateTime.now());
        organizationRepository.save(someOrganization);
    }

    private void addMembers() {
        Member member1 = new Member();
        member1.setAdmissionDate(LocalDate.now());
        member1.setOrganization(organizationRepository.findByName("teapot"));
        member1.setUser(userRepository.findByName("dr_watson"));
        member1.setStatus(MemberStatus.CREATOR);
        memberRepository.save(member1);

        Member member2 = new Member();
        member2.setAdmissionDate(LocalDate.now());
        member2.setOrganization(organizationRepository.findByName("teapot"));
        member2.setUser(userRepository.findByName("lora_palmer"));
        member2.setStatus(MemberStatus.WORKER);
        memberRepository.save(member2);

        Member member3 = new Member();
        member3.setAdmissionDate(LocalDate.now());
        member3.setOrganization(organizationRepository.findByName("someOrganization"));
        member3.setUser(userRepository.findByName("dale_cooper"));
        member3.setStatus(MemberStatus.CREATOR);
        memberRepository.save(member3);

        Member member4 = new Member();
        member4.setAdmissionDate(LocalDate.now());
        member4.setOrganization(organizationRepository.findByName("someOrganization"));
        member4.setUser(userRepository.findByName("sherlock_holmes"));
        member4.setStatus(MemberStatus.OWNER);
        memberRepository.save(member4);
    }

    private void addBoards() {
        boardRepository.save(new Board("board", userRepository.findByName("dr_watson")));
        boardRepository.save(new Board("board", userRepository.findByName("admin")));
        boardRepository.save(new Board("board", userRepository.findByName("admin")));
        boardRepository.save(new Board("board1", organizationRepository.findByName("teapot")));
        boardRepository.save(new Board("board2", organizationRepository.findByName("teapot")));
        boardRepository.save(new Board("board3", organizationRepository.findByName("teapot")));
        boardRepository.save(new Board("board2", organizationRepository.findByName("teapot")));
    }
}
