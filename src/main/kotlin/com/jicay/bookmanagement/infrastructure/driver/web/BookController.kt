package com.jicay.bookmanagement.infrastructure.driver.web

import com.jicay.bookmanagement.domain.model.Book
import com.jicay.bookmanagement.domain.port.BookPort
import com.jicay.bookmanagement.domain.usecase.BookUseCase
import com.jicay.bookmanagement.infrastructure.driver.web.dto.BookDTO
import com.jicay.bookmanagement.infrastructure.driver.web.dto.toDto
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/books")
class BookController(
    private val bookUseCase: BookUseCase,
    private val bookPort: BookPort
) {
    @CrossOrigin
    @GetMapping
    fun getAllBooks(): List<BookDTO> {
        return bookUseCase.getAllBooks()
            .map { it.toDto() }
    }

    @CrossOrigin
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun addBook(@RequestBody bookDTO: BookDTO) {
        bookUseCase.addBook(bookDTO.toDomain())
    }

    @CrossOrigin
    @PostMapping("/{name}/reserve")
    @ResponseStatus(HttpStatus.OK)
    fun reserveBookByName(@PathVariable name: String) {
        val book = bookPort.getBookByName(name)
        if (book != null && !book.reservation) {
            book.reservation = true
            bookPort.updateBook(book)
        } else {
            throw ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Book '$name' is not available or does not exist"
            )
        }
    }

    @CrossOrigin
    @PostMapping("/use/{name}/reserve")
    @ResponseStatus(HttpStatus.OK)
    fun reserveBookByNameUseCase(@PathVariable name: String): String {
        val reservationResult = bookUseCase.reserveBookByNameUseCase(name)
        return if (reservationResult) {
            "Book '$name' has been successfully reserved."
        } else {
            "Book '$name' is not available or does not exist."
        }
    }

}