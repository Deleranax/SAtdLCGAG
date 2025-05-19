package com.example.superawesometodolistcatgaggingappgagging.database

import android.app.Application
import android.content.Context
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.superawesometodolistcatgaggingappgagging.api.TodoListItem
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "todos", primaryKeys = ["id"])
data class TodoTable(
    @ColumnInfo(name = "id") val todoID: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "desc") val desc: String,
    @ColumnInfo(name = "time") val time: String,
)

@Dao
interface TodosDao {
    @Query("SELECT * FROM todos")
    fun getAll(): Flow<List<TodoTable>>

    @Query("DELETE FROM todos")
    suspend fun deleteAll()

    @Insert
    suspend fun insertAll(vararg todos: TodoTable)
}

interface TodosRepository {
    fun getAll(): Flow<List<TodoTable>>
    suspend fun deleteAll()
    suspend fun insertAll(vararg todos: TodoTable)
}

class OfflineTodosRepository(private val todosDao: TodosDao) : TodosRepository{
    override fun getAll(): Flow<List<TodoTable>> = todosDao.getAll()
    override suspend fun deleteAll() = todosDao.deleteAll()
    override suspend fun insertAll(vararg todos: TodoTable) = todosDao.insertAll(*todos)
}

@Database(entities = [TodoTable::class], version = 2)
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
