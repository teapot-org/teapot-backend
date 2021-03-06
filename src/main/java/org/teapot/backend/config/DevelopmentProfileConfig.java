package org.teapot.backend.config;

import lombok.RequiredArgsConstructor;
import org.h2.server.web.WebServlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.teapot.backend.model.kanban.*;
import org.teapot.backend.model.meta.TeapotAction;
import org.teapot.backend.model.meta.TeapotProperty;
import org.teapot.backend.model.meta.TeapotResource;
import org.teapot.backend.model.organization.Member;
import org.teapot.backend.model.organization.MemberStatus;
import org.teapot.backend.model.organization.Organization;
import org.teapot.backend.model.user.User;
import org.teapot.backend.model.user.UserAuthority;
import org.teapot.backend.repository.kanban.KanbanRepository;
import org.teapot.backend.repository.kanban.ProjectRepository;
import org.teapot.backend.repository.kanban.TicketListRepository;
import org.teapot.backend.repository.meta.TeapotActionRepository;
import org.teapot.backend.repository.meta.TeapotPropertyRepository;
import org.teapot.backend.repository.meta.TeapotResourceRepository;
import org.teapot.backend.repository.organization.MemberRepository;
import org.teapot.backend.repository.organization.OrganizationRepository;
import org.teapot.backend.repository.user.UserRepository;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

@Configuration
@Profile("development")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DevelopmentProfileConfig {

    private final UserRepository userRepository;
    private final TeapotPropertyRepository propertyRepository;
    private final TeapotActionRepository actionRepository;
    private final TeapotResourceRepository resourceRepository;
    private final OrganizationRepository organizationRepository;
    private final MemberRepository memberRepository;
    private final ProjectRepository projectRepository;
    private final KanbanRepository kanbanRepository;
    private final TicketListRepository ticketListRepository;

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
            addProjects();
            addKanbans();
            addTicketLists();
            addTickets();
        };
    }

    private void registerAdmin() {
        User admin = new User();

        admin.setName("admin");
        admin.setEmail("admin@teapot.org");
        admin.setPassword("1234");
        admin.setActivated(true);
        admin.setFirstName("Cake");
        admin.setLastName("Lover");
        admin.setBirthday(LocalDate.now());
        admin.setDescription("i manage everything");
        admin.setAuthority(UserAuthority.ADMIN);

        userRepository.save(admin);
    }

    private void registerDaleCooper() {
        User user = new User();

        user.setName("dale_cooper");
        user.setEmail("dale_cooper@twin.peaks");
        user.setPassword("1234");
        user.setActivated(true);
        user.setFirstName("Dale");
        user.setLastName("Cooper");
        user.setBirthday(LocalDate.now());
        user.setDescription("a special FBI agent");
        user.setAuthority(UserAuthority.USER);

        userRepository.save(user);
    }

    private void registerLoraPalmer() {
        User user = new User();

        user.setName("lora_palmer");
        user.setEmail("lora_palmer@twin.peaks");
        user.setPassword("1234");
        user.setActivated(true);
        user.setFirstName("Lora");
        user.setLastName("Palmer");
        user.setBirthday(LocalDate.now());
        user.setDescription("a dead girl");
        user.setAuthority(UserAuthority.USER);

        userRepository.save(user);
    }

    private void registerSherlockHolmes() {
        User user = new User();

        user.setName("sherlock_holmes");
        user.setEmail("sherlock_holmes@baker.st");
        user.setPassword("1234");
        user.setActivated(true);
        user.setFirstName("Sherlock");
        user.setLastName("Holmes");
        user.setBirthday(LocalDate.now());
        user.setDescription("private detective");
        user.setAuthority(UserAuthority.USER);

        userRepository.save(user);
    }

    private void registerDoctorWatson() {
        User user = new User();

        user.setName("dr_watson");
        user.setEmail("dr_watson@baker.st");
        user.setPassword("1234");
        user.setActivated(true);
        user.setFirstName("John");
        user.setLastName("Watson");
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
        action2.setUsage("/actions/activate?user={username|id}&token={token}");

        actionRepository.save(Arrays.asList(action1, action2));
    }

    private void addOrganizations() {
        Organization teapot = new Organization();
        teapot.setName("teapot");
        organizationRepository.save(teapot);

        Organization someOrganization = new Organization();
        someOrganization.setName("someOrganization");
        organizationRepository.save(someOrganization);
    }

    private void addMembers() {
        Member member1 = new Member();
        member1.setOrganization(organizationRepository.findByName("teapot"));
        member1.setUser(userRepository.findByName("dr_watson"));
        member1.setStatus(MemberStatus.CREATOR);
        memberRepository.save(member1);

        Member member2 = new Member();
        member2.setOrganization(organizationRepository.findByName("teapot"));
        member2.setUser(userRepository.findByName("lora_palmer"));
        member2.setStatus(MemberStatus.WORKER);
        memberRepository.save(member2);

        Member member3 = new Member();
        member3.setOrganization(organizationRepository.findByName("someOrganization"));
        member3.setUser(userRepository.findByName("dale_cooper"));
        member3.setStatus(MemberStatus.CREATOR);
        memberRepository.save(member3);

        Member member4 = new Member();
        member4.setOrganization(organizationRepository.findByName("someOrganization"));
        member4.setUser(userRepository.findByName("sherlock_holmes"));
        member4.setStatus(MemberStatus.OWNER);
        memberRepository.save(member4);
    }

    private void addProjects() {
        projectRepository.save(new Project());
        projectRepository.save(new Project());
    }

    private void addKanbans() {
        Kanban kanban = new Kanban("kanban123");
        kanban.setOwner(userRepository.findByName("admin"));
        kanban.setProject(projectRepository.findOne(1L));
        kanban.getContributors().add(userRepository.findByName("dr_watson"));
        kanbanRepository.save(kanban);

        kanbanRepository.save(new Kanban("kanban", userRepository.findByName("admin")));
        kanbanRepository.save(new Kanban("kanban1", organizationRepository.findByName("teapot")));
        kanbanRepository.save(new Kanban("kanban2", organizationRepository.findByName("teapot"), KanbanAccess.PRIVATE));
        kanbanRepository.save(new Kanban("kanban3", organizationRepository.findByName("teapot")));
        kanbanRepository.save(new Kanban("kanban2", organizationRepository.findByName("teapot")));
    }

    private void addTicketLists() {
        Kanban kanban1 = kanbanRepository.findOne(3L);
        kanban1.addTicketList(new TicketList("ticketList1"));
        kanbanRepository.save(kanban1);

        Kanban kanban2 = kanbanRepository.findOne(4L);
        kanban2.addTicketList(new TicketList("ticketList2"));
        kanbanRepository.save(kanban2);

        Kanban kanban3 = kanbanRepository.findOne(3L);
        kanban3.addTicketList(new TicketList("ticketList3"));
        kanbanRepository.save(kanban3);

        Kanban kanban4 = kanbanRepository.findOne(3L);
        kanban4.addTicketList(new TicketList("ticketList4"));
        kanbanRepository.save(kanban4);

        Kanban kanban5 = kanbanRepository.findOne(4L);
        kanban5.addTicketList(new TicketList("ticketList4"));
        kanbanRepository.save(kanban5);

        Kanban kanban6 = kanbanRepository.findOne(5L);
        kanban6.addTicketList(new TicketList("ticketList4"));
        kanbanRepository.save(kanban6);

        Kanban kanban7 = kanbanRepository.findOne(5L);
        kanban7.addTicketList(new TicketList("ticketList2"));
        kanbanRepository.save(kanban7);

        Kanban kanban8 = kanbanRepository.findOne(6L);
        kanban8.addTicketList(new TicketList("ticketList5"));
        kanbanRepository.save(kanban8);

        Kanban kanban9 = kanbanRepository.findOne(7L);
        kanban9.addTicketList(new TicketList("ticketList6"));
        kanbanRepository.save(kanban9);
    }

    private void addTickets() {
        TicketList ticketList1 = ticketListRepository.findOne(1L);
        Arrays.asList(
                new Ticket("ticket1", "lalalalal"),
                new Ticket("ticket2", "sd fsdfsd fsdf2"),
                new Ticket("ticket3", "sd fsdfsd fsdf2"),
                new Ticket("ticket4", "sd fsdfsd fsdf2"),
                new Ticket("ticket5", "sd fsdfsd fsdf2"),
                new Ticket("ticket6", "sd fsdfsd fsdf2"),
                new Ticket("ticket7", "sd fsdfsd fsdf2"),
                new Ticket("ticket8", "sd fsdfsd fsdf2"),
                new Ticket("ticket9", "sd fsdfsd fsdf2"),
                new Ticket("ticket10", "sd fsdfsd fsdf2"),
                new Ticket("ticket11", "sd fsdfsd fsdf2"),
                new Ticket("ticket12", "sd fsdfsd fsdf2"),
                new Ticket("ticket13", "sd fsdfsd fsdf2"),
                new Ticket("ticket14", "sd fsdfsd fsdf2"),
                new Ticket("ticket15", "sd fsdfsd fsdf2"),
                new Ticket("ticket16", "sd fsdfsd fsdf2"),
                new Ticket("ticket17", "sd fsdfsd fsdf2"),
                new Ticket("ticket18", "sd fsdfsd fsdf2"),
                new Ticket("ticket19", "sdfsdff sdf1")
        ).forEach(ticket -> {
            ticket.getSubscribers().add(userRepository.findByName("lora_palmer"));
            ticketList1.addTicket(ticket);
        });
        ticketListRepository.save(ticketList1);

        TicketList ticketList4 = ticketListRepository.findOne(4L);
        Arrays.asList(
                new Ticket("ticketsd1", "lalalsdgf sdfalal"),
                new Ticket("ticasket2", "sdfsdsd  sff sdf1")
        ).forEach(ticket -> {
            ticket.getSubscribers().add(userRepository.findByName("lora_palmer"));
            ticketList4.addTicket(ticket);
        });
        ticketListRepository.save(ticketList4);
    }
}
