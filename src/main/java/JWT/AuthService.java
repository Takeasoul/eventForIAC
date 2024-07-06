package JWT;

import io.jsonwebtoken.Claims;
import jakarta.security.auth.message.AuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import ru.kafedra.spring.Securityjwt.DTO.*;
import ru.kafedra.spring.Securityjwt.entity.Student;
import ru.kafedra.spring.Securityjwt.entity.Teacher;
import ru.kafedra.spring.Securityjwt.exceptions.AppError;
import ru.kafedra.spring.Securityjwt.services.StudentService;
import ru.kafedra.spring.Securityjwt.services.TeacherServices;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class AuthService<JwtAuthentication> {
    private final UserService userService;

    private final TeacherServices teacherServices;

    private final StudentService studentService;




    private final Map<String, String> refreshStorage = new HashMap<>();
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    public JwtResponse login(@NonNull JwtRequest authRequest) {
        final User user;
        try {
            user = userService.findByLogin(authRequest.getLogin())
                    .orElseThrow(() -> new AuthException("Пользователь не найден"));
        } catch (AuthException e) {
            throw new RuntimeException(e);
        }
        user.getPassword();

        if (passwordEncoder.matches(authRequest.getPassword(), user.getPassword())) {
            final String accessToken = jwtProvider.generateAccessToken(user);
            final String refreshToken = jwtProvider.generateRefreshToken(user);
            refreshStorage.put(user.getLogin(), refreshToken);
            return new JwtResponse(accessToken, refreshToken);
        } else {
            try {
                throw new AuthException("Неправильный пароль");
            } catch (AuthException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public JwtResponse getAccessToken(@NonNull String refreshToken) {
        if (jwtProvider.validateRefreshToken(refreshToken)) {
            final Claims claims = jwtProvider.getRefreshClaims(refreshToken);
            final String login = claims.getSubject();
            final String saveRefreshToken = refreshStorage.get(login);
            if (saveRefreshToken != null && saveRefreshToken.equals(refreshToken)) {
                final User user;
                try {
                    user = userService.findByLogin(login)
                            .orElseThrow(() -> new AuthException("Пользователь не найден"));
                } catch (AuthException e) {
                    throw new RuntimeException(e);
                }
                final String accessToken = jwtProvider.generateAccessToken(user);
                System.out.println("Отдаем новый акцес токен = " + accessToken);
                return new JwtResponse(accessToken, null);
            }
        }
        return new JwtResponse(null, null);
    }

    public JwtResponse refresh(@NonNull String refreshToken) {
        if (jwtProvider.validateRefreshToken(refreshToken)) {
            final Claims claims = jwtProvider.getRefreshClaims(refreshToken);
            final String login = claims.getSubject();
            final String saveRefreshToken = refreshStorage.get(login);
            if (saveRefreshToken != null && saveRefreshToken.equals(refreshToken)) {
                final User user;
                try {
                    user = userService.findByLogin(login)
                            .orElseThrow(() -> new AuthException("Пользователь не найден"));
                } catch (AuthException e) {
                    throw new RuntimeException(e);
                }
                final String accessToken = jwtProvider.generateAccessToken(user);
                final String newRefreshToken = jwtProvider.generateRefreshToken(user);
                refreshStorage.put(user.getLogin(), newRefreshToken);
                return new JwtResponse(accessToken, newRefreshToken);
            }
        }
        try {
            throw new AuthException("Невалидный JWT токен");
        } catch (AuthException e) {
            throw new RuntimeException(e);
        }
    }

    public JwtAuthentication getAuthInfo() {
        return (JwtAuthentication) SecurityContextHolder.getContext().getAuthentication();
    }


    public ResponseEntity<?> createNewUser(@RequestBody RegistrationUserDto registrationUserDto) {
        if(userService.findByLogin(registrationUserDto.getLogin()).isPresent()) {
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "Такой пользователь уже существует"), HttpStatus.BAD_REQUEST);
        }
        User user = userService.createNewUser(registrationUserDto);

        return ResponseEntity.ok(new UserDto(user.getId(), user.getLogin(), user.getPassword(), user.getRoles()));
    }

    public ResponseEntity<?> createNewTeacher(@RequestBody RegistrationTeacherDto registrationTeacherDto) {
        if(userService.findByLogin(registrationTeacherDto.getUsername()).isPresent()) {
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "Такой пользователь уже существует"), HttpStatus.BAD_REQUEST);
        }
        Teacher teacher = teacherServices.createNewTeacher(registrationTeacherDto);

        return ResponseEntity.ok(200);
    }

    public  ResponseEntity<?> TeacherInfo(Long id)
    {
        return  ResponseEntity.ok(teacherServices.teacherInfo(id));
    }

    public ResponseEntity<?> editTeacher(@RequestBody TeacherDto teacherDto, Long id) {
        if(teacherServices.findById(id).isEmpty()) {
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "Такого преподавателя не существует"), HttpStatus.BAD_REQUEST);
        }
       teacherServices.editTeacher(teacherDto, id);

        return ResponseEntity.ok(200);
    }


    public ResponseEntity<?> createNewStudent(@RequestBody RegistrationStudentDto registrationStudentDto) {
        if(userService.findByLogin(registrationStudentDto.getLogin()).isPresent()) {
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "Такой пользователь уже существует"), HttpStatus.BAD_REQUEST);
        }
        Student student = studentService.createNewStudent(registrationStudentDto);

        return ResponseEntity.ok(200);
    }

    public  ResponseEntity<?> StudentInfo(Long id)
    {
        return  ResponseEntity.ok(studentService.studentInfo(id));
    }

    public ResponseEntity<?> editStudent(@RequestBody RegistrationStudentDto registrationStudentDto, Long id) {
        if(studentService.findById(id).isEmpty()) {
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "Такого студента не существует"), HttpStatus.BAD_REQUEST);
        }
        studentService.editStudent(registrationStudentDto, id);

        return ResponseEntity.ok(200);
    }

    public  ResponseEntity<?> FindStudentByGroup(String group)
    {
        List<Student> studentList = studentService.findByGroup(group);
        studentList.forEach(student -> student.setUser(null));
        return  ResponseEntity.ok(studentList);
    }

    public  ResponseEntity<?> FindStudentByCourse(Integer course)
    {
        List<Student> studentList = studentService.findByCourse(course);
        studentList.forEach(student -> student.setUser(null));
        return  ResponseEntity.ok(studentList);
    }


}
