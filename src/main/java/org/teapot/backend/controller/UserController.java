package org.teapot.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.teapot.backend.controller.exception.BadRequestException;
import org.teapot.backend.controller.exception.ResourceNotFoundException;
import org.teapot.backend.model.User;
import org.teapot.backend.repository.UserRepository;

import javax.servlet.http.HttpServletResponse;
import java.util.List;


@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

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
     * Ищет в базе данных пользователя с указанным id. Если пользователь
     * найден, возвращает этого пользователя и устаналивает код состояния
     * 200 OK; если пользователь не найден - устаналивает код состояния
     * 404 Not Found, выбрасывая исключение {@link ResourceNotFoundException}
     *
     * @param id идентификатор возвращаемого пользователя
     * @return найденный пользователь
     */
    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        User user = userRepository.findOne(id);
        if (user == null) {
            throw new ResourceNotFoundException();
        }
        return user;
    }

    /**
     * Метод доступен только авторизованным пользователям.
     * Изменяет данные пользователя с указанным id. Принимает на вход в теле
     * запроса объект пользователя с новыми данными. Если пользователь с
     * указанным id не найден в базе данных - устанавливает код состояния
     * 404 Not Found, выбрасывая исключение {@link ResourceNotFoundException}.
     * Если пользователь с таким id найден, то его данные изменяются на новые
     * данные, но с некоторыми ограничениями: пользователь с ролью USER может
     * только свои данные и только определенные поля (имя, фамилия, дата
     * рождения). Пользователь с ролью ADMIN может изменить любые данные
     * любого пользователя (кроме даты регистрации). Если никаких ошибок не
     * произошло - устаналивается код состояния 204 No Content.
     *
     * @param id   идентификатор изменяемого пользователя
     * @param user объект, содержащий новые данные пользователя
     */
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateUser(@PathVariable Long id,
                           @RequestBody User user) {
        if (!userRepository.exists(id)) {
            throw new ResourceNotFoundException();
        }
        user.setId(id);
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
     * Если пользователь неавторизован, то происходит регистрация: новому
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
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User registerUser(@RequestBody User user,
                             HttpServletResponse response) {
        if (userRepository.findByUsername(user.getUsername()) != null) {
            throw new BadRequestException();
        }

        user = userRepository.save(user);
        response.setHeader("Location", "/users/" + user.getId());
        return user;
    }
}
