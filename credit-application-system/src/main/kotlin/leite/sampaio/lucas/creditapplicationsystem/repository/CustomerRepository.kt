package leite.sampaio.lucas.creditapplicationsystem.repository

import leite.sampaio.lucas.creditapplicationsystem.entity.Customer
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CustomerRepository: JpaRepository<Customer, Long>