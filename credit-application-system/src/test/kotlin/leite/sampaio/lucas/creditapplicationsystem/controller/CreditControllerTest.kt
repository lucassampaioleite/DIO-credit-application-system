package leite.sampaio.lucas.creditapplicationsystem.controller

import com.fasterxml.jackson.databind.ObjectMapper
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import leite.sampaio.lucas.creditapplicationsystem.dto.request.CreditDto
import leite.sampaio.lucas.creditapplicationsystem.dto.request.CustomerDto
import leite.sampaio.lucas.creditapplicationsystem.dto.response.CustomerView
import leite.sampaio.lucas.creditapplicationsystem.entity.Customer
import leite.sampaio.lucas.creditapplicationsystem.repository.CreditRepository
import leite.sampaio.lucas.creditapplicationsystem.repository.CustomerRepository
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.web.bind.annotation.PostMapping
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@ContextConfiguration
class CreditControllerTest {


    @Autowired
    private lateinit var creditRepository: CreditRepository

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    companion object {
        const val URL: String = "/api/credits"
    }

    @BeforeEach
    fun setup() {
        createCustomer() //creates a consumer with id=1 to test credits
        creditRepository.deleteAll()
    }

    @AfterEach
    fun tearDown() {
        creditRepository.deleteAll()
    }

    @Test
    fun `should save a credit and return 201 status`() {
        // Given
        val creditDto: CreditDto = builderCreditDto()
        val valueAsString: String = objectMapper.writeValueAsString(creditDto)
        // When
        // Then
        val result = mockMvc.perform(
            MockMvcRequestBuilders.post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(valueAsString)
        )
        //    .andExpect(MockMvcResultMatchers.status().isCreated) //remove the comment if it is run individually
            .andReturn()
        val responseContent = result.response.contentAsString
        val uuidReturned = extractUUIDFromString(responseContent)
        assertThat(responseContent).isEqualTo("Credit $uuidReturned - Customer joao.silva@example.com saved!")
    }

    @Test
    fun `should not save a credit because customer not exist and return 404 status`() {
        //given
        val creditDto: CreditDto = builderCreditDto(customerId = 88)
        val valueAsString: String = objectMapper.writeValueAsString(creditDto)
        //when
        //then
        mockMvc.perform(
            MockMvcRequestBuilders.post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(valueAsString)
        )
            .andExpect(MockMvcResultMatchers.status().isNotFound)
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Not Found!"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(404))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.exception")
                    .value("class leite.sampaio.lucas.creditapplicationsystem.exception.BusinessException")
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should not save a credit because the date is invalid and return status 400`() {
        //given
        val creditDto: CreditDto = builderCreditDto(dayFirstOfInstallment = LocalDate.now().minusDays(7))
        val valueAsString: String = objectMapper.writeValueAsString(creditDto)
        //when
        //then
        mockMvc.perform(
            MockMvcRequestBuilders.post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(valueAsString)
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Bad Request! Consult the documentation"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.exception")
                    .value("class org.springframework.web.bind.MethodArgumentNotValidException")
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should not save a credit because the number od installments is invalid and return status 400`() {
        //given
        val creditDto: CreditDto = builderCreditDto(numberOfInstallments = 49)
        val valueAsString: String = objectMapper.writeValueAsString(creditDto)
        //when
        //then
        mockMvc.perform(
            MockMvcRequestBuilders.post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(valueAsString)
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Bad Request! Consult the documentation"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.exception")
                    .value("class org.springframework.web.bind.MethodArgumentNotValidException")
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
            .andDo(MockMvcResultHandlers.print())
    }

   @Test
    fun `should find all by customer and return 200 status`() {
        //given
        creditRepository.save(builderCreditDto().toEntity())
        creditRepository.save(builderCreditDto(creditValue = BigDecimal(5000.00),).toEntity())
        //when
        //then
        mockMvc.perform(
            MockMvcRequestBuilders.get("$URL?customerId=1")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].creditValue").value(1000.0))
            .andExpect(MockMvcResultMatchers.jsonPath("$[1].creditValue").value(5000.0))

    }

    @Test
    fun `should not find all by customer and return 404 status`() {
        //given
        creditRepository.save(builderCreditDto().toEntity())
        creditRepository.save(builderCreditDto(creditValue = BigDecimal(5000.00),).toEntity())
        //when
        //then
        mockMvc.perform(
            MockMvcRequestBuilders.get("$URL?customerId=50")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isNotFound)
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Not Found!"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(404))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.exception")
                    .value("class leite.sampaio.lucas.creditapplicationsystem.exception.BusinessException")
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
            .andDo(MockMvcResultHandlers.print())
    }

    fun extractUUIDFromString(input: String): String? {
        val startIndex = input.indexOf("Credit ") + "Credit ".length
        val endIndex = input.indexOf(" - Customer")
        return if (startIndex != -1 && endIndex != -1) input.substring(startIndex, endIndex) else null
    }

    fun createCustomer(){
        val customerDto: CustomerDto = builderCustomerDto()
        val valueAsString: String = objectMapper.writeValueAsString(customerDto)
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(valueAsString)
        )
    }

    private fun builderCreditDto(
        creditValue: BigDecimal = BigDecimal(1000.00),
        dayFirstOfInstallment: LocalDate = LocalDate.now().plusDays(7),
        numberOfInstallments: Int = 12,
        customerId: Long = 1,
    ) = CreditDto(
        creditValue = creditValue,
        dayFirstOfInstallment = dayFirstOfInstallment,
        numberOfInstallments = numberOfInstallments,
        customerId = customerId
    )

    private fun builderCustomerDto(
        firstName: String = "João",
        lastName: String = "Silva",
        cpf: String = "27571884020",
        email: String = "joao.silva@example.com",
        income: BigDecimal = BigDecimal.valueOf(50000.0),
        password: String = "1234",
        zipCode: String = "12345-678",
        street: String = "Rua das Flores, 123",
    ) = CustomerDto(
        firstName = firstName,
        lastName = lastName,
        cpf = cpf,
        email = email,
        income = income,
        password = password,
        zipCode = zipCode,
        street = street
    )

}