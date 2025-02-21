package edts.android.composedemo.data.remote

import edts.android.composedemo.di.RestNetworkModule
import okio.BufferedSource
import retrofit2.Response
import java.nio.charset.Charset

/**
 * Abstract Base Data source class with error handling
 */
abstract class BaseDataSource {

    protected suspend fun <T> getResult(call: suspend () -> Response<T>): Result<T> {
        return try {
            val response = call()
            val code = response.code()
            if (response.isSuccessful) {
                val body = response.body()
                return if (body != null) {
                    Result.success(body)
                } else {
                    Result.error("BODYNULL", "Connection Error", null)
                }
            } else {
                if (code == 401) {
                    if (response.errorBody() != null) {
                        val bufferedSource: BufferedSource = response.errorBody()!!.source()
                        bufferedSource.request(Long.MAX_VALUE) // Buffer the entire body.

                        val json =
                            bufferedSource.buffer.clone().readString(Charset.forName("UTF8"))

                        Result.unauthorized(json)
                    } else {
                        Result.unauthorized(null)
                    }
                } else if (code == 400 || code == 500) {
                    if (response.errorBody() != null) {
                        val bufferedSource: BufferedSource = response.errorBody()!!.source()
                        bufferedSource.request(Long.MAX_VALUE) // Buffer the entire body.

                        val json = bufferedSource.buffer.clone().readString(Charset.forName("UTF8"))

                        Result.error("SystemError", json, null)
                    } else {
                        Result.error(code.toString(), response.message(), null)
                    }
                } else {
                    Result.error(code.toString(), response.message(), null)
                }
            }
        } catch (e: RestNetworkModule.NoConnectivityException) {
            Result.error("NoConnectivity", e.message ?: "No internet connection", null)
        } catch (e: Exception) {
            Result.error("Exception", e.message ?: "An unknown error occurred", null)
        }
    }


}