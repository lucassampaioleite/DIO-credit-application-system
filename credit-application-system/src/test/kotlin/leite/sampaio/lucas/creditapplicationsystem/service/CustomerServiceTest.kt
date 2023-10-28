package leite.sampaio.lucas.creditapplicationsystem.service

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import leite.sampaio.lucas.creditapplicationsystem.entity.Address
import leite.sampaio.lucas.creditapplicationsystem.entity.Customer
import leite.sampaio.lucas.creditapplicationsystem.exception.BusinessException
import leite.sampaio.lucas.creditapplicationsystem.repository.CustomerRepository
import leite.sampaio.lucas.creditapplicationsystem.service.impl.CustomerService
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

import java.math.BigDecimal
import java.util.*

@ExtendWith(MockKExtension::class)
class CustomerServiceTest {
    @MockK
    lateinit var customerRepository: CustomerRepository

    @InjectMockKs
    lateinit var customerService: CustomerService

    @Test
    fun `should create customer successfully`() {
        // Given
        val fakeCustomer = buildCustomer()
        customerRepository.apply {
            every { save(any()) } returns fakeCustomer
        }

        // When
        val actual = customerService.save(fakeCustomer)

        // Then
        Assertions.assertThat(actual).isNotNull
        Assertions.assertThat(actual).isSameAs(fakeCustomer)
        verify(exactly = 1) { customerRepository.save(fakeCustomer) }
    }

    @Test
    fun `should find customer by id`() {
        // Given
        val fakeId = 1L
        val fakeCustomer = buildCustomer(id = fakeId)
        customerRepository.apply {
            every { findById(fakeId) } returns Optional.of(fakeCustomer)
        }

        // When
        val actual = customerService.findById(fakeId)

        // Then
        Assertions.assertThat(actual).isNotNull
        Assertions.assertThat(actual).isExactlyInstanceOf(Customer::class.java)
        Assertions.assertThat(actual).isSameAs(fakeCustomer)
        verify(exactly = 1) { customerRepository.findById(fakeId) }
    }

    @Test
    fun `should not find customer by invalid id and throw BusinessException`() {
        // Given
        val fakeId = 999L
        customerRepository.apply {
            every { findById(fakeId) } returns Optional.empty()
        }

        // When/Then
        Assertions.assertThatExceptionOfType(BusinessException::class.java)
            .isThrownBy { customerService.findById(fakeId) }
            .withMessage("Id $fakeId not found")
        verify(exactly = 1) { customerRepository.findById(fakeId) }
    }

    @Test
    fun `should delete customer by id`() {
        // Given
        val fakeId = 1L
        val fakeCustomer = buildCustomer(id = fakeId)
        customerRepository.apply {
            every { findById(fakeId) } returns Optional.of(fakeCustomer)
            every { delete(fakeCustomer) } just runs
        }

        // When
        customerService.delete(fakeId)

        // Then
        verify(exactly = 1) { customerRepository.findById(fakeId) }
        verify(exactly = 1) { customerRepository.delete(fakeCustomer) }
    }

    companion object {
        fun buildCustomer(
            firstName: String = "João",
            lastName: String = "Silva",
            cpf: String = "27571884020",
            email: String = "joao.silva@example.com",
            password: String = "50000.0",
            zipCode: String = "12345-678",
            street: String = "Rua das Flores, 123",
            income: BigDecimal = BigDecimal.valueOf(50000.0),
            id: Long = 1L
        ) = Customer(
            firstName = firstName,
            lastName = lastName,
            cpf = cpf,
            email = email,
            password = password,
            address = Address(
                zipCode = zipCode,
                street = street,
            ),
            income = income,
            id = id
        )
    }
}