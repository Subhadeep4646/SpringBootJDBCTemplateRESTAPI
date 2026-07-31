package com.neueda.restapi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.containsString;

@WebMvcTest(EmployeeController.class)
class EmployeeControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    EmployeeService service;

    @Test
    void shouldReturnEmployeesWhenEmployeesExist() throws Exception {

        List<Employee> list = List.of(

                new Employee("John","IT",50000),
                new Employee("David","HR",60000)

        );

        when(service.getAllEmployees())
                .thenReturn(list);

        mockMvc.perform(get("/api/v1/employee/"))

                .andExpect(status().isOk())

                .andExpect(jsonPath("$.message").value("Employees fetched successfully"))

                .andExpect(jsonPath("$.data.length()").value(2))

                .andExpect(jsonPath("$.data[0].name").value("John"));

    }

    @Test
    void shouldReturnEmptyEmployeeListWhenNoEmployeesExist() throws Exception {

        when(service.getAllEmployees())
                .thenReturn(List.of());

        mockMvc.perform(get("/api/v1/employee/"))

                .andExpect(status().isOk())

                .andExpect(jsonPath("$.message").value("Employees fetched successfully"))

                .andExpect(jsonPath("$.data.length()").value(0));

    }

    @Test
    void shouldReturnEmployeeByIdWhenEmployeeExists() throws Exception {

        Employee employee = new Employee("John", "IT", 50000);
        employee.setId(1);

        when(service.getEmployeeById(1))
                .thenReturn(employee);

        mockMvc.perform(get("/api/v1/employee/1"))

                .andExpect(status().isOk())

                .andExpect(jsonPath("$.message").value("Employee fetched successfully"))

                .andExpect(jsonPath("$.data.id").value(1))

                .andExpect(jsonPath("$.data.name").value("John"));

    }

    @Test
    void shouldReturnNotFoundWhenEmployeeDoesNotExist() throws Exception {

        when(service.getEmployeeById(99))
                .thenReturn(null);

        mockMvc.perform(get("/api/v1/employee/99"))

                .andExpect(status().isNotFound())

                .andExpect(content().string("Employee with ID 99 not found"));

    }

    @Test
    void shouldCreateEmployeeWhenServiceReturnsPositiveResult() throws Exception {

        when(service.saveEmployee(org.mockito.ArgumentMatchers.any(Employee.class)))
                .thenReturn(1);

        mockMvc.perform(post("/api/v1/employee/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"John\",\"department\":\"IT\",\"salary\":50000}"))

                .andExpect(status().isCreated())

                .andExpect(jsonPath("$.message").value("Employee added successfully"))

                .andExpect(jsonPath("$.data.name").value("John"));

    }

    @Test
    void shouldReturnBadRequestWhenCreateEmployeeFails() throws Exception {

        when(service.saveEmployee(org.mockito.ArgumentMatchers.any(Employee.class)))
                .thenReturn(0);

        mockMvc.perform(post("/api/v1/employee/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"John\",\"department\":\"IT\",\"salary\":50000}"))

                .andExpect(status().isBadRequest())

                .andExpect(jsonPath("$.message").value("Failed to add employee"))

                .andExpect(jsonPath("$.data.name").value("John"));

    }

    @Test
    void shouldUpdateEmployeeWhenServiceReturnsPositiveResult() throws Exception {

        when(service.updateEmployee(org.mockito.ArgumentMatchers.eq(1), org.mockito.ArgumentMatchers.any(Employee.class)))
                .thenReturn(1);

        mockMvc.perform(put("/api/v1/employee/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"John\",\"department\":\"IT\",\"salary\":55000}"))

                .andExpect(status().isOk())

                .andExpect(jsonPath("$.message").value("Employee updated successfully"))

                .andExpect(jsonPath("$.data.id").value(1))

                .andExpect(jsonPath("$.data.salary").value(55000.0));

    }

    @Test
    void shouldReturnBadRequestWhenUpdateEmployeeFails() throws Exception {

        when(service.updateEmployee(org.mockito.ArgumentMatchers.eq(1), org.mockito.ArgumentMatchers.any(Employee.class)))
                .thenReturn(0);

        mockMvc.perform(put("/api/v1/employee/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"John\",\"department\":\"IT\",\"salary\":55000}"))

                .andExpect(status().isBadRequest())

                .andExpect(jsonPath("$.message").value("Failed to update employee"))

                .andExpect(jsonPath("$.data.id").value(1));

    }

    @Test
    void shouldDeleteEmployeeWhenServiceReturnsPositiveResult() throws Exception {

        when(service.deleteEmployee(1))
                .thenReturn(1);

        mockMvc.perform(delete("/api/v1/employee/1"))

                .andExpect(status().isOk())

                .andExpect(jsonPath("$.message").value("Employee deleted successfully"))

                .andExpect(jsonPath("$.data").value(1));

    }

    @Test
    void shouldReturnBadRequestWhenDeleteEmployeeFails() throws Exception {

        when(service.deleteEmployee(1))
                .thenReturn(0);

        mockMvc.perform(delete("/api/v1/employee/1"))

                .andExpect(status().isBadRequest())

                .andExpect(jsonPath("$.message").value("Failed to delete employee"))

                .andExpect(jsonPath("$.data").value(1));

    }

    @Test
    void shouldReturnInternalServerErrorWhenRequestBodyIsMalformedJson() throws Exception {

        mockMvc.perform(post("/api/v1/employee/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{invalid-json}"))

                .andExpect(status().isInternalServerError())

                .andExpect(content().string(containsString("An unexcepted error occured!")))

                .andExpect(content().string(containsString("JSON parse error")));

    }

    @Test
    void shouldReturnInternalServerErrorWhenUnhandledExceptionOccurs() throws Exception {

        when(service.getAllEmployees())
                .thenThrow(new RuntimeException("database unavailable"));

        mockMvc.perform(get("/api/v1/employee/"))

                .andExpect(status().isInternalServerError())

                .andExpect(content().string(containsString("An unexcepted error occured!")))

                .andExpect(content().string(containsString("database unavailable")));

    }

}