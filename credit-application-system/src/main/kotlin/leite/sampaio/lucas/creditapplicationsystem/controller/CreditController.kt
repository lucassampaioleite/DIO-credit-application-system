package leite.sampaio.lucas.creditapplicationsystem.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import leite.sampaio.lucas.creditapplicationsystem.dto.request.CreditDto
import leite.sampaio.lucas.creditapplicationsystem.dto.response.CreditView
import leite.sampaio.lucas.creditapplicationsystem.dto.response.CreditViewList
import leite.sampaio.lucas.creditapplicationsystem.dto.response.CustomerView
import leite.sampaio.lucas.creditapplicationsystem.entity.Credit
import leite.sampaio.lucas.creditapplicationsystem.service.impl.CreditService
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.PathVariable
import java.util.UUID
import java.util.stream.Collectors

@RestController
@RequestMapping("/api/credits")
@Tag(name = "Credits", description = "Endpoints for managing credits")
class CreditController(
    private val creditService: CreditService
) {

    @PostMapping(produces = [MediaType.APPLICATION_JSON_VALUE], consumes = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(summary = "Adds a new credit", description = "Adds a new credit",
        responses = [
            ApiResponse(description = "Created", responseCode = "201",
                content = [Content(schema = Schema(implementation = CustomerView::class))]
            ),
            ApiResponse(description = "Not Found", responseCode = "404",
                content = [Content(schema = Schema(implementation = Unit::class))]
            ),
            ApiResponse(description = "Bad Request", responseCode = "400",
                content = [Content(schema = Schema(implementation = Unit::class))]
            ),
        ]
    )
    fun saveCredit(@RequestBody @Valid creditDto: CreditDto): ResponseEntity<String> {
        val credit: Credit = this.creditService.save(creditDto.toEntity())
        return ResponseEntity.status(HttpStatus.CREATED)
            .body("Credit ${credit.creditCode} - Customer ${credit.customer?.email} saved!")
    }

    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(summary = "Find credits by customerId", description = "Find credits by customerId",
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
    fun findAllByCustomerId(@RequestParam(value = "customerId") customerId: Long):
            ResponseEntity<List<CreditViewList>> {
        val creditViewList: List<CreditViewList> = this.creditService.findAllByCustomer(customerId)
            .stream()
            .map { credit: Credit -> CreditViewList(credit) }
            .collect(Collectors.toList())
        return ResponseEntity.status(HttpStatus.OK).body(creditViewList)
    }

    @GetMapping("/{creditCode}", produces = [org.springframework.http.MediaType.APPLICATION_JSON_VALUE])
    @Operation(summary = "Find a credit by creditID and customerId", description = "Find a credit by creditID and customerId",
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
    fun findByCreditCode(
        @RequestParam(value = "customerId") customerId: Long,
        @PathVariable creditCode: UUID
    ): ResponseEntity<CreditView> {
        val credit: Credit = this.creditService.findByCreditCode(customerId, creditCode)
        return ResponseEntity.status(HttpStatus.OK).body(CreditView(credit))
    }
}