package leite.sampaio.lucas.creditapplicationsystem.service.impl

import leite.sampaio.lucas.creditapplicationsystem.entity.Credit
import leite.sampaio.lucas.creditapplicationsystem.exception.BusinessException
import leite.sampaio.lucas.creditapplicationsystem.repository.CreditRepository
import leite.sampaio.lucas.creditapplicationsystem.service.ICreditService
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.util.UUID

@Service
class CreditService(
    private val creditRepository: CreditRepository,
    private val customerService: CustomerService
) : ICreditService {
    override fun save(credit: Credit): Credit {
        this.validDayFirstInstallment(credit.dayFirstInstallment)
        credit.apply {
            customer = customerService.findById(credit.customer?.id!!)
        }
        return this.creditRepository.save(credit)
    }

    override fun findAllByCustomer(customerId: Long): List<Credit>{
        customerService.findById(customerId)
        return this.creditRepository.findAllByCustomerId(customerId)
    }


    override fun findByCreditCode(customerId: Long, creditCode: UUID): Credit {
        val credit: Credit = (this.creditRepository.findByCreditCode(creditCode)
            ?: throw BusinessException("Credit code $creditCode not found"))
        return if (credit.customer?.id == customerId) credit
        else throw IllegalArgumentException("Contact admin")
    }

    private fun validDayFirstInstallment(dayFirstInstallment: LocalDate): Boolean {
        return if (dayFirstInstallment.isBefore(LocalDate.now().plusMonths(3))) true
        else throw BusinessException("Invalid Date")
    }
}