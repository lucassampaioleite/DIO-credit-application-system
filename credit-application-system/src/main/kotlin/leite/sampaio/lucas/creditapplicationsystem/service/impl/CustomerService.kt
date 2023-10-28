package leite.sampaio.lucas.creditapplicationsystem.service.impl

import leite.sampaio.lucas.creditapplicationsystem.entity.Customer
import leite.sampaio.lucas.creditapplicationsystem.exception.BusinessException
import leite.sampaio.lucas.creditapplicationsystem.repository.CustomerRepository
import leite.sampaio.lucas.creditapplicationsystem.service.ICustomerService
import org.springframework.stereotype.Service

@Service
class CustomerService(
    private val customerRepository: CustomerRepository
): ICustomerService {
    override fun save(customer: Customer): Customer = this.customerRepository.save(customer)

    override fun findById(id: Long): Customer = this.customerRepository.findById(id)
        .orElseThrow{throw BusinessException("Id $id not found") }

    override fun delete(id: Long) {
        val customer: Customer = this.findById(id)
        this.customerRepository.delete(customer)
    }
}