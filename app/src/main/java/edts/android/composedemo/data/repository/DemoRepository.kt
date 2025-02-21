package edts.android.composedemo.data.repository

import edts.android.composedemo.data.local.DemoLocalDataSource
import edts.android.composedemo.data.mapper.toModel
import edts.android.composedemo.data.remote.NetworkBoundResource
import edts.android.composedemo.data.remote.data_source.DemoRemoteDataSource
import edts.android.composedemo.data.remote.response.ArticlesResponse
import edts.android.composedemo.domain.irepository.IDemoRepository
import edts.android.composedemo.domain.model.ArticlesData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import edts.android.composedemo.data.remote.Result

class DemoRepository @Inject constructor(
    private val remoteDataSource: DemoRemoteDataSource,
    private val demoLocalDataSource: DemoLocalDataSource
) : IDemoRepository {
    override fun getArticles(reload: Boolean) =
        object : NetworkBoundResource<ArticlesData?, ArticlesResponse>() {
            override fun getCached(): Flow<ArticlesData?> {
                return demoLocalDataSource.getData()
            }

            override suspend fun createCall(): Result<ArticlesResponse> {
                return remoteDataSource.getArticles()
            }

            override suspend fun saveCallResult(data: ArticlesResponse?) {
                data?.toModel()?.let { demoLocalDataSource.saveData(it) }
            }

            override fun shouldFetch(data: ArticlesData?) = data == null || reload
        }.asFlow()
}