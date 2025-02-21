package edts.android.composedemo.data.remote

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

abstract class NetworkBoundResource<ResultType, RequestType> {
    private var result: Flow<Resource<ResultType>> = flow {
        emit(Resource.Loading())
        val getCached = getCached().first()
        if (shouldFetch(getCached)) {
            emit(Resource.Loading())
            val apiResponse = createCall()
            when (apiResponse.status) {
                Result.Status.SUCCESS -> {
                    saveCallResult(apiResponse.data)
                    emitAll(
                        getCached().map {
                            Resource.Success(it)
                        }
                    )
                }
                Result.Status.UNAUTHORIZED -> {
                    onFetchFailed()
                    emit(Resource.Error(apiResponse.message ?: "Unauthorized"))
                }
                else -> {
                    onFetchFailed()
                    getCached().map {
                        Result.error(
                            apiResponse.code,
                            apiResponse.message,
                            it
                        )
                    }
                }
            }
        } else {
            emitAll(
                getCached().map {
                    Resource.Success(it)
                }
            )
        }
    }

    protected open fun onFetchFailed() {}

    protected abstract fun getCached(): Flow<ResultType>

    protected abstract fun shouldFetch(data: ResultType?): Boolean

    protected abstract suspend fun createCall(): Result<RequestType>

    protected abstract suspend fun saveCallResult(data: RequestType?)

    fun asFlow(): Flow<Resource<ResultType>> = result
}