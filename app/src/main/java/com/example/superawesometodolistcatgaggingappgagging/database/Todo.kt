package com.example.superawesometodolistcatgaggingappgagging.database

import android.app.Application
import android.content.Context
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.work.impl.model.Preference
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "preferences", primaryKeys = ["key"])
data class PreferenceTable(
    @ColumnInfo(name = "key") val key: String,
    @ColumnInfo(name = "value") val value: String
)

@Entity(tableName = "todos", primaryKeys = ["id"])
data class TodoTable(
    @ColumnInfo(name = "id") val todoID: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "desc") val desc: String,
    @ColumnInfo(name = "time") val time: Long,
)

@Dao
interface TodosDao {
    @Query("SELECT * FROM todos")
    fun getAll(): Flow<List<TodoTable>>

    @Query("DELETE FROM todos")
    suspend fun deleteAll()

    @Insert
    suspend fun insertAll(vararg todos: TodoTable)

    @Query("SELECT value FROM preferences WHERE `key` = :key")
    suspend fun getPreference(key: String): String?

    @Insert
    suspend fun setPreference(preference: PreferenceTable)
}

interface TodosRepository {
    fun getAll(): Flow<List<TodoTable>>
    suspend fun deleteAll()
    suspend fun insertAll(vararg todos: TodoTable)

    suspend fun getPreference(key: String): String?
    suspend fun setPreference(preference: PreferenceTable)
}

class OfflineTodosRepository(private val todosDao: TodosDao) : TodosRepository{
    override fun getAll(): Flow<List<TodoTable>> = todosDao.getAll()
    override suspend fun deleteAll() = todosDao.deleteAll()
    override suspend fun insertAll(vararg todos: TodoTable) = todosDao.insertAll(*todos)

    override suspend fun getPreference(key: String): String? = todosDao.getPreference(key)
    override suspend fun setPreference(preference: PreferenceTable) = todosDao.setPreference(preference)
}

@Database(entities = [TodoTable::class, PreferenceTable::class], version = 2)
abstract class TodosDatabase: RoomDatabase() {
    abstract fun todoDao(): TodosDao

    companion object {
        @Volatile
        private var Instance: TodosDatabase? = null

        fun getDatabase(context: Context): TodosDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, TodosDatabase::class.java, "app_db")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}

interface TodosContainer {
    val todosRepository: TodosRepository
}

class TodosDataContainer(private val context: Context) : TodosContainer {
    override val todosRepository: TodosRepository by lazy {
        val db = TodosDatabase.getDatabase(context)
        OfflineTodosRepository(
            db.todoDao(),
        )
    }
}

class TodosApplication: Application() {
    lateinit var container: TodosContainer

    override fun onCreate() {
        super.onCreate()
        container = TodosDataContainer(this)
    }
}
