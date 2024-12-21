package com.lazzaro

import kotlinx.coroutines.*
import kotlinx.serialization.Serializable
import java.sql.Connection
import java.sql.Statement

@Serializable
data class User(val name: String, val password: String)
class AuthService(private val connection: Connection) {
    companion object {
        private const val USER_TABLE_NAME = "app_user"
        private const val CREATE_TABLE_USER =
            "CREATE TABLE IF NOT EXISTS $USER_TABLE_NAME (ID SERIAL PRIMARY KEY, NAME VARCHAR(255), PASSWORD VARCHAR(255));"
        private const val SELECT_CITY_BY_ID = "SELECT name, population FROM $USER_TABLE_NAME WHERE id = ?"
        private const val INSERT_CITY = "INSERT INTO $USER_TABLE_NAME (name, password) VALUES (?, ?)"
        private const val UPDATE_CITY = "UPDATE $USER_TABLE_NAME SET name = ?, password = ? WHERE id = ?"
        private const val DELETE_CITY = "DELETE FROM $USER_TABLE_NAME WHERE id = ?"

    }

    init {
        val statement = connection.createStatement()
        statement.executeUpdate(CREATE_TABLE_USER)
    }

    suspend fun create(city: User): Int = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(INSERT_CITY, Statement.RETURN_GENERATED_KEYS)
        statement.setString(1, city.name)
        statement.setString(2, city.password)
        statement.executeUpdate()

        val generatedKeys = statement.generatedKeys
        if (generatedKeys.next()) {
            return@withContext generatedKeys.getInt(1)
        } else {
            throw Exception("Unable to retrieve the id of the newly inserted city")
        }
    }

    suspend fun read(id: Int): User = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(SELECT_CITY_BY_ID)
        statement.setInt(1, id)
        val resultSet = statement.executeQuery()

        if (resultSet.next()) {
            val name = resultSet.getString("name")
            val population = resultSet.getString("population")
            return@withContext User(name, population)
        } else {
            throw Exception("Record not found")
        }
    }

    suspend fun update(id: Int, city: User) = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(UPDATE_CITY)
        statement.setString(1, city.name)
        statement.setString(2, city.password)
        statement.setInt(3, id)
        statement.executeUpdate()
    }

    suspend fun delete(id: Int) = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(DELETE_CITY)
        statement.setInt(1, id)
        statement.executeUpdate()
    }
}

