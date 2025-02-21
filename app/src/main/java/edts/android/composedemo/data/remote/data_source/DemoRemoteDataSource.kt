package edts.android.composedemo.data.remote.data_source

import edts.android.composedemo.data.remote.network.ApiDemoService
import edts.android.composedemo.data.remote.BaseDataSource
import javax.inject.Inject

class DemoRemoteDataSource @Inject constructor(
    private val demoService: ApiDemoService
): BaseDataSource() {
    suspend fun getArticles() =
        getResult {
            demoService.getArticles()
        }
}