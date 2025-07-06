package edts.android.composedemo.overlay

import androidx.lifecycle.*
import androidx.savedstate.*

class OverlayOwner : SavedStateRegistryOwner,
    ViewModelStoreOwner,
    LifecycleOwner {

    private val lifecycleRegistry = LifecycleRegistry(this)
    private val viewModel = ViewModelStore()
    private val savedStateRegistryController: SavedStateRegistryController

    init {
        // 1) Kick off in INITIALIZED
        lifecycleRegistry.currentState = Lifecycle.State.INITIALIZED

        // 2) Create & attach the SavedStateRegistryController right now
        savedStateRegistryController = SavedStateRegistryController.create(this)
        savedStateRegistryController.performAttach()
        savedStateRegistryController.performRestore(null)

        // 3) Now that the registry is ready, advance the lifecycle
        lifecycleRegistry.currentState = Lifecycle.State.CREATED
        lifecycleRegistry.currentState = Lifecycle.State.STARTED
    }

    override val lifecycle: Lifecycle
        get() = lifecycleRegistry

    override val savedStateRegistry: SavedStateRegistry
        get() = savedStateRegistryController.savedStateRegistry

    override val viewModelStore: ViewModelStore
        get() = viewModel

    fun destroy() {
        lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
    }
}
