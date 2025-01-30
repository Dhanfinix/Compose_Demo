package dhandev.android.composedemo.ui.component.segment_button

data class SegmentedData<T>(
    val title: String,
    val icon: Int,
    val checked: Boolean = false,
    val metadata: T? = null,
)
