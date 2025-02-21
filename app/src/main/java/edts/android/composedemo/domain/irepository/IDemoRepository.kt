package edts.android.composedemo.domain.irepository

import edts.android.composedemo.data.remote.Resource
import edts.android.composedemo.domain.model.ArticlesData
import kotlinx.coroutines.flow.Flow

interface IDemoRepository {
    fun getArticles(reload: Boolean): Flow<Resource<ArticlesData?>>
}