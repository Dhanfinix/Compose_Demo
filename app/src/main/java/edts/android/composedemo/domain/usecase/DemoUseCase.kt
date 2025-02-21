package edts.android.composedemo.domain.usecase

import edts.android.composedemo.data.remote.Resource
import edts.android.composedemo.domain.model.ArticlesData
import kotlinx.coroutines.flow.Flow

interface DemoUseCase {
    fun getArticles(reload: Boolean = false): Flow<Resource<ArticlesData?>>
}