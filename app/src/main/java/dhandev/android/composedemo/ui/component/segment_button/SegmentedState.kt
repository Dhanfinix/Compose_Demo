package dhandev.android.composedemo.ui.component.segment_button

data class SegmentedState<T>(
    val isMulti: Boolean = true,
    val options: List<SegmentedData<T>>,
    /** selectedIndex is for Single Choice */
    val selectedIndex: Int = -1,
)
