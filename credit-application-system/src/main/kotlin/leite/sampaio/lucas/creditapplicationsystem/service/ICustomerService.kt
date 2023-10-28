package leite.sampaio.lucas.creditapplicationsystem.service

import leite.sampaio.lucas.creditapplicationsystem.entity.Customer

interface ICustomerService {
    fun save(customer: Customer): Customer
    fun findById(id: Long): Customer
    fun delete(id: Long)
}