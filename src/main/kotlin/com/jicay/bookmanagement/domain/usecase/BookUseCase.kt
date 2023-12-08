package com.jicay.bookmanagement.domain.usecase

import com.jicay.bookmanagement.domain.model.Book
import com.jicay.bookmanagement.domain.port.BookPort

class BookUseCase(
    private val bookPort: BookPort
) {
    fun getAllBooks(): List<Book> {
        return bookPort.getAllBooks().sortedBy {
            it.name.lowercase()
        }
    }

    fun addBook(book: Book) {
        bookPort.createBook(book)
    }

    fun reserveBookByNameUseCase(name: String): Boolean {
        val book = bookPort.getBookByName(name)
        if (book != null && !book.reservation) {
            book.reservation = true
            bookPort.updateBook(book) // Mettre à jour la réservation dans le port
            return true // Réservation réussie
        }
        return false // Le livre n'est pas disponible ou n'existe pas
    }
}