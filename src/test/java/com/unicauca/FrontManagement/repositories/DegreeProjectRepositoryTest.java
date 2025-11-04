package com.unicauca.FrontManagement.repositories;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unicauca.FrontManagement.entity.*;
import com.unicauca.FrontManagement.infra.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class DegreeProjectRepositoryTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ObjectMapper objectMapper;



    @InjectMocks
    private DegreeProjectRepository repository;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void findProjectsByTypeFile_ShouldReturnEmptyList_WhenNoContent() throws Exception {
        ResponseEntity<String> response = new ResponseEntity<>(HttpStatus.NO_CONTENT);
        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(response);

        List<DegreeProject> result = repository.findProjectsByTypeFile("FormatoA");

        assertTrue(result.isEmpty());
    }

    @Test
    void savePreliminaryProject_ShouldSendPostRequest_WhenValid() throws Exception {
        File file = new File();
        file.setName("Anteproyecto.pdf");
        file.setVersion(1);
        file.setDocument("data".getBytes());
        file.setDate(LocalDate.now());

        ResponseEntity<String> response = new ResponseEntity<>("OK", HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                .thenReturn(response);

        repository.savePreliminaryProject(1L, file);

        verify(restTemplate).exchange(contains("/uploadPreliminaryProject"), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class));
    }

    @Test
    void findLastFileVersionByStudentEmail_ShouldReturnLatestVersion() throws Exception {
        File file1 = new File();
        file1.setVersion(1);
        file1.setType(EnumFileType.Anteproyecto);

        File file2 = new File();
        file2.setVersion(3);
        file2.setType(EnumFileType.Anteproyecto);

        DegreeProject project = new DegreeProject();
        project.setFiles(List.of(file1, file2));

        String jsonResponse = "{}";
        ResponseEntity<String> response = new ResponseEntity<>(jsonResponse, HttpStatus.OK);

        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(response);
        when(objectMapper.readValue(anyString(), eq(DegreeProject.class))).thenReturn(project);

        int result = repository.findLastFileVersionByStudentEmail("ana@uni.edu", "Anteproyecto");

        assertEquals(3, result);
        verify(restTemplate).getForEntity(contains("/projectByEmail/ana@uni.edu"), eq(String.class));
    }

    @Test
    void findProjectByEmail_ShouldReturnProject_WhenOk() throws Exception {
        String jsonResponse = "{\"title\":\"Proyecto Final\"}";
        DegreeProject project = new DegreeProject();
        project.setTitle("Proyecto Final");

        ResponseEntity<String> response = new ResponseEntity<>(jsonResponse, HttpStatus.OK);
        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(response);
        when(objectMapper.readValue(anyString(), eq(DegreeProject.class))).thenReturn(project);

        DegreeProject result = repository.findProjectByEmail("test@uni.edu");

        assertNotNull(result);
        assertEquals("Proyecto Final", result.getTitle());
    }

    @Test
    void saveProject_ShouldReturnTrue_WhenCreated() throws Exception {
        DegreeProject project = new DegreeProject();
        project.setTitle("Nuevo Proyecto");
        project.setGeneralObjective("Objetivo");
        project.setSpecificObjectives(List.of("Objetivos especificos"));
        project.setModality(EnumModality.ProyectoInvestigacion);

        State state= new State();
        state.setName("FirstReviewFormatA");
        project.setState(state);

        List<Student> students = new ArrayList<>();
        User user = new User();
        user.setId(999L);
        user.setEmail("juanyangela@unicauca.edu.co");
        user.setPassword("passsss");
        Role role = new Role();
        role.setRoleType(EnumRole.Estudiante);
        List<Role> roles = new ArrayList<>();
        roles.add(role);
        user.setRoles(roles);


        Student student = new Student();
        student.setName("Ana");
        student.setId(999L);
        student.setStudentCode("345678");
        student.setProgram(EnumProgram.Ingenieria_Automatica_Industrial);
        student.setLastName("Mendez");
        student.setUser(user);
        student.setPhoneNumber("098765");

        project.setStudents(students);

        File file = new File();
        file.setName("FormatoA.pdf");
        file.setType(EnumFileType.FormatoA);
        file.setVersion(1);
        file.setDate(LocalDate.now());
        file.setDocument("pdf".getBytes());
        project.setFiles(List.of(file));

        ResponseEntity<String> response = new ResponseEntity<>("CREATED", HttpStatus.CREATED);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenReturn(response);

        boolean result = repository.saveProject(project);

        assertTrue(result);
        verify(restTemplate).postForEntity(contains("/SaveProject"), any(HttpEntity.class), eq(String.class));
    }

    @Test
    void reuploadProject_ShouldReturnTrue_WhenCreated() throws Exception {
        DegreeProject project = new DegreeProject();
        project.setId(1L);
        project.setTitle("Proyecto Reenviado");
        project.setGeneralObjective("Objetivo");
        project.setSpecificObjectives(List.of("oBJETIVO"));
        project.setModality(EnumModality.ProyectoInvestigacion);
        State state= new State();
        state.setName("ApprovedFormatA");
        project.setState(state);
        List<Student> students = new ArrayList<>();
        User user = new User();
        user.setId(999L);
        user.setEmail("juanyangela@unicauca.edu.co");
        user.setPassword("passsss");
        Role role = new Role();
        role.setRoleType(EnumRole.Estudiante);
        List<Role> roles = new ArrayList<>();
        roles.add(role);
        user.setRoles(roles);


        Student student = new Student();
        student.setName("Ana");
        student.setId(999L);
        student.setStudentCode("345678");
        student.setProgram(EnumProgram.Ingenieria_Automatica_Industrial);
        student.setLastName("Mendez");
        student.setUser(user);
        student.setPhoneNumber("098765");

        students.add(student);


        project.setStudents(students);

        File file = new File();
        file.setId(10L);
        file.setName("FormatoA_v2.pdf");
        file.setType(EnumFileType.FormatoA);
        file.setVersion(2);
        file.setDate(LocalDate.now());
        file.setDocument("data".getBytes());
        project.setFiles(List.of(file));

        ResponseEntity<String> response = new ResponseEntity<>("CREATED", HttpStatus.CREATED);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenReturn(response);

        boolean result = repository.reuploadProject(project);

        assertTrue(result);
        verify(restTemplate).postForEntity(contains("/reuploadProject"), any(HttpEntity.class), eq(String.class));
    }
    @Test
    void updateProjectAndChanceState_ShouldCallPost_WhenOk() throws Exception {
        DegreeProject project = new DegreeProject();
        project.setId(1L);
        project.setTitle("Proyecto Estado Nuevo");
        project.setModality(EnumModality.ProyectoInvestigacion);
        project.setFiles(List.of());
        project.setStudents(List.of());

        ResponseEntity<String> response = new ResponseEntity<>("OK", HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                .thenReturn(response);

        assertDoesNotThrow(() -> repository.updateProjectAndChanceState(project, "Approved"));
        verify(restTemplate).exchange(contains("/updateProject?action=Approved"), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class));
    }
}
