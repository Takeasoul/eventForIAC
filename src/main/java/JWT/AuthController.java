package JWT;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kafedra.spring.Securityjwt.DTO.*;
import ru.kafedra.spring.Securityjwt.services.TeacherServices;

@RequestMapping("api/auth")
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;


    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody JwtRequest authRequest) {
        final JwtResponse token = authService.login(authRequest);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/token")
    public ResponseEntity<JwtResponse> getNewAccessToken(@RequestBody RefreshJwtRequest request) {
        final JwtResponse token = authService.getAccessToken(request.getRefreshToken());
        return ResponseEntity.ok(token);
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtResponse> getNewRefreshToken(@RequestBody RefreshJwtRequest request) {
        final JwtResponse token = authService.refresh(request.getRefreshToken());
        return ResponseEntity.ok(token);
    }

    @PostMapping("/adduser")
    public ResponseEntity<?> createNewUser(@RequestBody RegistrationUserDto registrationUserDto) {
    return authService.createNewUser(registrationUserDto);


    }

    @PostMapping("/addTeacher")
    public ResponseEntity<?> createNewTeacher(@RequestBody RegistrationTeacherDto registrationTeacherDto){
        return authService.createNewTeacher(registrationTeacherDto);
    }

    @GetMapping("/teacher/info/{id}")
    public ResponseEntity<?> TeacherInfo(@PathVariable Long id){
        return authService.TeacherInfo(id);
    }

    @PostMapping("/teacher/edit/{id}")
    public ResponseEntity<?> editTeacher(@RequestBody TeacherDto teacherDto, @PathVariable Long id){
        return authService.editTeacher(teacherDto, id);
    }


    @PostMapping("/addStudent")
    public ResponseEntity<?> createNewStudent(@RequestBody RegistrationStudentDto registrationStudentDto){
        return authService.createNewStudent(registrationStudentDto);
    }

    @GetMapping("/student/info/{id}")
    public ResponseEntity<?> StudentInfo(@PathVariable Long id){
        return authService.StudentInfo(id);
    }

    @PostMapping("/student/edit/{id}")
    public ResponseEntity<?> createNewStudent(@RequestBody RegistrationStudentDto registrationStudentDto, @PathVariable Long id){
        return authService.editStudent(registrationStudentDto, id);
    }

    @GetMapping("/student/findbygroup/{group}")
    public ResponseEntity<?> findStudentByGroup(@PathVariable String group){
        return authService.FindStudentByGroup(group);
    }

    @GetMapping("/student/findbycourse/{course}")
    public ResponseEntity<?> findStudentByCourse(@PathVariable Integer course){
        return authService.FindStudentByCourse(course);
    }


}
