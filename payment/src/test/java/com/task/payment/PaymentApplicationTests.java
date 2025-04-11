package com.task.payment;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.task.payment.dto.PaymentProcessingRequest;
import com.task.payment.model.Parent;
import com.task.payment.model.Student;
import com.task.payment.repository.ParentRepository;
import com.task.payment.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
class PaymentApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private ParentRepository parentRepository;

	@Autowired
	private StudentRepository studentRepository;
	private Long testParentId;
	private Long testStudentId;
	@BeforeEach
	void setUp() {
		parentRepository.deleteAll();
		studentRepository.deleteAll();

		// Create and save the student first
		Student student = new Student();
		student.setBalance(0.0);
		student = studentRepository.save(student);
		testStudentId = student.getStudentId(); // Capture ID

		// Create two parents
		Parent parent1 = new Parent();
		parent1.setBalance(1000.0);
		parent1.setStudents(List.of(student));

		Parent parent2 = new Parent();
		parent2.setBalance(1000.0);
		parent2.setStudents(List.of(student));

		// Save parents
		parent1 = parentRepository.save(parent1);
		parent2 = parentRepository.save(parent2);
		testParentId = parent1.getParentId();

		student.setAssignedParents(List.of(parent1, parent2));
		studentRepository.save(student);
	}
	@Test
	void testGetParentList() throws Exception {
		mockMvc.perform(get("/api/parents"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON));
	}

	@Test
	void testGetParentById_NotFound() throws Exception {
		mockMvc.perform(get("/api/parent/9999"))
				.andExpect(status().isNotFound());
	}

	@Test
	void testGetPaymentList() throws Exception {
		mockMvc.perform(get("/api/payments/all")
						.with(httpBasic("admin", "admin123")))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON));
	}

	@Test
	void testProcessPayment_Successful() throws Exception {
		PaymentProcessingRequest request = new PaymentProcessingRequest();
		request.setParentId(testParentId);
		request.setStudentId(testStudentId);
		request.setPaymentAmount(200.0);

		mockMvc.perform(post("/api/payments")
						.with(httpBasic("admin", "admin123"))
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk())
				.andExpect(content().string("Payment processed successfully"));
	}

	@Test
	void testProcessPayment_InsufficientFunds() throws Exception {
		PaymentProcessingRequest request = new PaymentProcessingRequest();
		request.setParentId(testParentId);
		request.setStudentId(testStudentId);
		request.setPaymentAmount(999999.0);

		mockMvc.perform(post("/api/payments")
						.with(httpBasic("admin","admin123"))
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isBadRequest())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Error processing payment")));
	}

}
