package com.example.superawesometodolistcatgaggingappgagging.api

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.superawesometodolistcatgaggingappgagging.database.TodoTable
import com.example.superawesometodolistcatgaggingappgagging.database.TodosDatabase
import net.gotev.cookiestore.SharedPreferencesCookieStore
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import java.net.CookieManager
import java.net.CookiePolicy


private const val BASE_URL = "https://satdlcgag.saiger.dev/"
object TodoApi {
    var cookieStore: SharedPreferencesCookieStore? = null
    var retrofitService: TodoApiService? = null;

    fun init(context: Context) {
        if (retrofitService == null) {
            cookieStore = SharedPreferencesCookieStore(context.applicationContext, "loginCookie")

            val interceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

            // Used to persist the session
            val cookieHandler = CookieManager(
                cookieStore,
                CookiePolicy.ACCEPT_ORIGINAL_SERVER
            )
            val client = OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .cookieJar(JavaNetCookieJar(cookieHandler))
                .build()

            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .client(client)
                .build()

            retrofitService = retrofit.create(TodoApiService::class.java)
        }
    }
}

interface TodoApiService {
    @POST("auth/login")
    suspend fun login(@Body body: RequestBody): GenericResponse

    @POST("auth/register")
    suspend fun register(@Body body: RequestBody): GenericResponse

    @GET("auth/info")
    suspend fun info(): AuthInfoResponse

    @GET("todos/list")
    suspend fun fetchTodoList(): TodoListResponse

    @POST("todos/remove")
    suspend fun removeTodo(@Body body: RequestBody): GenericResponse

    @POST("todos/add")
    suspend fun addTodo(@Body body: RequestBody): TodoListResponse
}

class TodoFetchWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
        val db = TodosDatabase.getDatabase(applicationContext)
        val dao = db.todoDao()
        return try {
            if (TodoApi.retrofitService != null) {
                val response = TodoApi.retrofitService!!.fetchTodoList()
                if (response.success == 1) {
                    Log.d("Worker", "Fetched ${response.data.todos.size} todos")
                    dao.deleteAll()
                    dao.insertAll(*response.data.todos.map {
                        TodoTable(it.id, it.name, it.desc, it.time)
                    }.toTypedArray())
                    Result.success()
                } else {
                    Log.e("Worker", "API error")
                    Result.retry()
                }
            } else {
                Result.retry()
            }
        } catch (e: Exception) {
            Log.e("Worker", "Network error: ${e.message}")
            Result.retry() // You could also use Result.failure()
        }
    }
}