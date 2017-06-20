package org.teapot.backend.controller;

import com.google.common.primitives.Longs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.teapot.backend.controller.exception.BadRequestException;
import org.teapot.backend.controller.exception.ForbiddenException;
import org.teapot.backend.controller.exception.ResourceNotFoundException;
import org.teapot.backend.model.User;
import org.teapot.backend.model.UserAuthority;
import org.teapot.backend.repository.UserRepository;
import org.teapot.backend.util.VerificationMailSender;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private VerificationMailSender verificationMailSender;

    /**
     * Метод доступен всем пользователям, в том числе и неавторизованным.
     * Возвращает список всех пользователей, либо список не всех пользователей,
     * если в параметрах запроса указаны номер страницы, количество элементов
     * на странице и порядок сортировки.
     * Устаналивает код состояния 200 OK
     *
     * @param pageable объект, содержащий параметры запроса - номер страницы,
     *                 кол-во элементов на странице, порядок сортировки
     * @return сформированный список пользователей
     */
    @GetMapping
    public List<User> getUsers(Pageable pageable) {
        return userRepository.findAll(pageable).getContent();
    }

    /**
     * Метод доступен всем пользователям, в том числе и неавторизованным.
     * Ищет в базе данных пользователя с указанным id или username. Если
     * пользователь найден, возвращает этого пользователя и устаналивает код
     * состояния 200 OK; если пользователь не найден - устаналивает код
     * состояния 404 Not Found, выбрасывая исключение
     * {@link ResourceNotFoundException}
     *
     * @param idOrUsername id или username пользователя
     * @return найденный пользователь
     */
    @GetMapping("/{idOrUsername:.+}")
    public User getUser(@PathVariable String idOrUsername) {
        User user;

        Long id = Longs.tryParse(idOrUsername);
        if (id != null) {
            user = userRepository.findOne(id);
        } else {
            user = userRepository.findByUsername(idOrUsername);
        }

        if (user == null) {
            throw new ResourceNotFoundException();
        }
        return user;
    }

    /**
     * Метод доступен только пользователям с ролью ADMIN.
     * Изменяет данные пользователя с указанным id. Принимает на вход в теле
     * запроса объект пользователя с новыми данными. Если пользователь с
     * указанным id не найден в базе данных - устанавливает код состояния
     * 404 Not Found, выбрасывая исключение {@link ResourceNotFoundException}.
     * Если пользователь с таким id найден, то его данные изменяются на новые
     * данные (кроме даты регистрации и значения isActivated). Если никаких
     * ошибок не произошло - устаналивается код состояния 204 No Content.
     *
     * @param id   идентификатор изменяемого пользователя
     * @param user объект, содержащий новые данные пользователя
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateUser(@PathVariable Long id,
                           @RequestBody User user) {
        User existingUser = userRepository.findOne(id);
        if (existingUser == null) {
            throw new ResourceNotFoundException();
        }

        user.setId(id);
        user.setRegistrationDate(existingUser.getRegistrationDate());
        user.setActivated(existingUser.isActivated());
        // если пароль изменился
        if (!passwordEncoder.matches(user.getPassword(), existingUser.getPassword())) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        userRepository.save(user);
    }

    /**
     * Метод доступен только пользователям с ролью ADMIN.
     * Удаляет пользователя с указанным id, если такой пользователь существует.
     * После удаления устаналивает код состояния 204 No Content. Если
     * пользователь с указанным id не найден, устаналивает код состояния
     * 404 Not Found, выбрасывая исключение {@link ResourceNotFoundException}.
     *
     * @param id идентификатор удаляемого пользователя
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        if (!userRepository.exists(id)) {
            throw new ResourceNotFoundException();
        }
        userRepository.delete(id);
    }

    /**
     * Метод доступен неавторизованным пользователям и пользователям,
     * авторизованным как ADMIN.
     * Принимает на вход объект, содержащий данные о новом пользователе.
     * Общее назначение метода - проверка на наличие в базе пользователя с
     * указанным username (в таком случае устаналивает код состояния
     * 400 Bad Request, выбрасывая исключение {@link BadRequestException}) и,
     * в случае отсутствия такового, добавление нового пользователя в базу
     * (регистрация). Точное действие метода после проверки зависит от типа
     * авторизации пользователя, который вызывает метод. Если пользователь
     * авторизован как ADMIN, то новому пользователю устаналивается текущая
     * дата в качестве даты регистрации, и пользователь добавляется в базу.
     * Если пользователь не авторизован, то происходит регистрация: новому
     * пользователю также устаналивается текущая дата регистрации,
     * устаналиваются права USER, isAvailable устаналивается в false,
     * пользователь добавляется в базу, генерируется VerificationToken,
     * который передается почтовому сервису, который отправляет его на e-mail,
     * указанный при регистрации, чтобы пользователь подтвердил его (это
     * установит isAvailable в true). После добавления нового пользователя в
     * базу в обоих случая будет установлен код состояния 201 Created и
     * заголовок Location будет содержать адрес нового ресурса, например
     * 'Location: /users/123', где 123 - id добавленного пользователя. Также
     * метод возвращает данные только что добавленного пользователя.
     *
     * @param user     данные нового пользователя
     * @param response объект, содержащий заголовки ответа
     * @return добавленный пользователь
     */
    @PreAuthorize("isAnonymous() || hasRole('ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User registerUser(@RequestBody User user,
                             HttpServletResponse response,
                             WebRequest request,
                             Authentication auth) {
        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new BadRequestException();
        }

        user.setRegistrationDate(LocalDateTime.now());
        if (auth == null) {
            user = userRepository.save(user);
            verificationMailSender.createTokenAndSend(user, request.getLocale());
        } else if (auth.getAuthorities().contains(UserAuthority.ADMIN)) {
            user.setActivated(true);
            userRepository.save(user);
        }

        response.setHeader(HttpHeaders.LOCATION, "/users/" + user.getId());
        return user;
    }

    /**
     * Метод доступен пользователям с ролью ADMIN и пользователям, чей
     * идентификатор равен идентификатору в маппинге '/{id}'.
     * Метод выполняет изменение данных пользователя с идентификатором
     * id на новые данные, указанные в параметрах, если они указаны,
     * неуказанные в параметрах данные не изменяются. Пользователь с
     * ролью ADMIN может изменить любыые данные, кроме даты регистрации.
     * Пользователь, идентификатор которого равен идентификатору в
     * маппинге '/{id}', может изменить только username, firstName, lastName
     * и isAvailable (отолько на false). В случае успеха устаналивает код
     * состояния 204 No Content. В случае, если ресурс с указанным id не
     * найден - код состояния 404 Not Found. Если доступ пользователю к
     * ресурсу запрещен - код состояния 403 Forbidden.
     *
     * @param id          идентификатор пользователя, данные которого нужно
     *                    изменить
     * @param username    новое имя пользователя
     * @param email       новый e-mail
     * @param password    новый пароль
     * @param available   новое состояние available
     * @param firstName   новое имя
     * @param lastName    новая фамилия
     * @param authority   новая роль
     * @param birthday    новая дата рождения
     * @param description новое описание
     * @param auth        объект, содержащий данные об аутентификации
     */
    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void patchUser(@PathVariable Long id,
                          @RequestParam(required = false) String username,
                          @RequestParam(required = false) String email,
                          @RequestParam(required = false) String password,
                          @RequestParam(required = false) Boolean available,
                          @RequestParam(required = false) String firstName,
                          @RequestParam(required = false) String lastName,
                          @RequestParam(required = false) UserAuthority authority,
                          @RequestParam(required = false) LocalDate birthday,
                          @RequestParam(required = false) String description,
                          Authentication auth) {
        User user = userRepository.findOne(id);
        if (user == null) {
            throw new ResourceNotFoundException();
        }

        if (auth.getAuthorities().contains(UserAuthority.ADMIN)) {

            if (username != null) user.setUsername(username);
            if (email != null) user.setEmail(email);
            if (password != null) user.setPassword(passwordEncoder.encode(password));
            if (available != null) user.setAvailable(available);
            if (firstName != null) user.setFirstName(firstName);
            if (lastName != null) user.setLastName(lastName);
            if (authority != null) user.setAuthority(authority);
            if (birthday != null) user.setBirthday(birthday);
            if (description != null) user.setDescription(description);

        } else if (auth.getName().equals(user.getEmail())) {

            if (username != null) user.setUsername(username);
            if ((available != null) && (!available)) user.setAvailable(false);
            if (firstName != null) user.setFirstName(firstName);
            if (lastName != null) user.setLastName(lastName);
            if (birthday != null) user.setBirthday(birthday);
        } else {
            throw new ForbiddenException();
        }

        userRepository.save(user);
    }
}
