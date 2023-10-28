package leite.sampaio.lucas.creditapplicationsystem.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import leite.sampaio.lucas.creditapplicationsystem.dto.request.CustomerDto
import leite.sampaio.lucas.creditapplicationsystem.dto.request.CustomerUpdateDto
import leite.sampaio.lucas.creditapplicationsystem.dto.response.CustomerView
import leite.sampaio.lucas.creditapplicationsystem.entity.Customer
import leite.sampaio.lucas.creditapplicationsystem.service.impl.CustomerService
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/customers")
@Tag(name = "Customers", description = "Endpoints for managing customers")
class CustomerController(
    private val customerService: CustomerService
) {

    @PostMapping(produces = [MediaType.APPLICATION_JSON_VALUE], consumes = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(summary = "Adds a new customer", description = "Adds a new customer",
        responses = [
            ApiResponse(description = "Created", responseCode = "201",
                content = [Content(schema = Schema(implementation = CustomerView::class))]
            ),
            ApiResponse(description = "Conflict", responseCode = "409",
                content = [Content(schema = Schema(implementation = Unit::class))]
            ),
            ApiResponse(description = "Bad Request", responseCode = "400",
                content = [Content(schema = Schema(implementation = Unit::class))]
            ),
        ]
    )
    fun saveCustomer(@RequestBody @Valid customerDto: CustomerDto): ResponseEntity<CustomerView> {
        val savedCustomer: Customer = this.customerService.save(customerDto.toEntity())
        return ResponseEntity.status(HttpStatus.CREATED).body(CustomerView(savedCustomer))
    }

    @GetMapping("/{id}", produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(summary = "Finds a Customer", description = "Finds a Customer",
        responses = [
            ApiResponse( description = "Success", responseCode = "200",
                content = [Content(schema = Schema(implementation = CustomerView::class))]
            ),
            ApiResponse(description = "Bad Request", responseCode = "400",
                content = [Content(schema = Schema(implementation = Unit::class))]
            ),
            ApiResponse(description = "Not Found", responseCode = "404",
                content = [Content(schema = Schema(implementation = Unit::class))]
            )
        ]
    )
    fun findById(@PathVariable id: Long): ResponseEntity<CustomerView> {
        val customer: Customer = this.customerService.findById(id)
        return ResponseEntity.status(HttpStatus.OK).body(CustomerView(customer))
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletes a customer", description = "Deletes a customer",
        responses = [
            ApiResponse(description = "No Content", responseCode = "204",
                content = [Content(schema = Schema(implementation = Unit::class))]
            ),
            ApiResponse(description = "Bad Request", responseCode = "400",
                content = [Content(schema = Schema(implementation = Unit::class))]
            ),
            ApiResponse(description = "Not Found", responseCode = "404",
                content = [Content(schema = Schema(implementation = Unit::class))]
            )
        ]
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteCustomer(@PathVariable id: Long) = this.customerService.delete(id)

    @PatchMapping
    @Operation(summary = "Updates a Customer", description = "Updates a Customer",
        responses = [
            ApiResponse(
                description = "Success", responseCode = "200",
                content = [Content(schema = Schema(implementation = CustomerView::class))]
            ),
            ApiResponse(description = "Bad Request", responseCode = "400",
                content = [Content(schema = Schema(implementation = Unit::class))]
            ),
            ApiResponse(description = "Not Found", responseCode = "404",
                content = [Content(schema = Schema(implementation = Unit::class))]
            )
        ]
    )
    fun updateCustomer(
        @RequestParam(value = "customerId") id: Long,
        @RequestBody @Valid customerUpdateDto: CustomerUpdateDto
    ): ResponseEntity<CustomerView> {
        val customer: Customer = this.customerService.findById(id)
        val customerToUpdate: Customer = customerUpdateDto.toEntity(customer)
        val customerUpdated: Customer = this.customerService.save(customerToUpdate)
        return ResponseEntity.status(HttpStatus.OK).body(CustomerView(customerUpdated))
    }
}