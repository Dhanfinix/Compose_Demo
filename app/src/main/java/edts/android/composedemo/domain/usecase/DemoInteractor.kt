package edts.android.composedemo.domain.usecase

import edts.android.composedemo.data.remote.Resource
import edts.android.composedemo.domain.irepository.IDemoRepository
import edts.android.composedemo.domain.model.ArticlesData
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class DemoInteractor @Inject constructor(
    private val demoRepository: IDemoRepository
) : DemoUseCase {
    override fun getArticles(reload: Boolean) =
        demoRepository.getArticles(reload)
}

/** dummy interactor for compose preview */
class DummyDemoInteractor(): DemoUseCase {
    override fun getArticles(reload: Boolean) = flowOf(Resource.Loading<ArticlesData?>())
}