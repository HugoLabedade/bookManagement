package com.jicay.bookmanagement.infrastructure.driven.adapter

import com.jicay.bookmanagement.domain.model.Book
import com.jicay.bookmanagement.domain.port.BookPort
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Service

@Service
class BookDAO(private val namedParameterJdbcTemplate: NamedParameterJdbcTemplate): BookPort {
    override fun getAllBooks(): List<Book> {
        return namedParameterJdbcTemplate
            .query("SELECT * FROM BOOK", MapSqlParameterSource()) { rs, _ ->
                Book(
                    name = rs.getString("title"),
                    author = rs.getString("author"),
                    reservation = rs.getBoolean("reservation")
                )
            }
    }

    override fun createBook(book: Book) {
        namedParameterJdbcTemplate
            .update("INSERT INTO BOOK (title, author) values (:title, :author)", mapOf(
                "title" to book.name,
                "author" to book.author
            ))
    }

    override fun getBookByName(name: String): Book? {
        val params = MapSqlParameterSource().addValue("title", name)
        val query = "SELECT * FROM BOOK WHERE title = :title"

        return try {
            namedParameterJdbcTemplate.queryForObject(query, params) { rs, _ ->
                Book(
                        name = rs.getString("title"),
                        author = rs.getString("author"),
                        reservation = rs.getBoolean("reservation")
                )
            }
        } catch (ex: EmptyResultDataAccessException) {
            null // Retourne null si aucun livre trouvé avec ce nom
        }
    }

    override fun updateBook(book: Book) {
        val query = """
            UPDATE BOOK 
            SET reservation = :reservation 
            WHERE title = :title
        """.trimIndent()

        val params = mapOf("reservation" to book.reservation, "title" to book.name)

        namedParameterJdbcTemplate.update(query, params)
    }
}