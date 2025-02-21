package edts.android.composedemo.data.remote.network

import edts.android.composedemo.data.remote.response.ArticlesResponse
import retrofit2.Response
import retrofit2.http.GET


interface ApiDemoService {
    @GET("/v1/articles/extended")
    suspend fun getArticles(): Response<ArticlesResponse>
}