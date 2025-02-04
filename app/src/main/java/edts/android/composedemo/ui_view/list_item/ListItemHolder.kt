package edts.android.composedemo.ui_view.list_item

import androidx.recyclerview.widget.RecyclerView
import edts.android.composedemo.databinding.NumberedValueItemBinding

class ListItemHolder(
    private val viewBinding: NumberedValueItemBinding,
    private val delegate: ListItemDelegate
) : RecyclerView.ViewHolder(viewBinding.root) {
    fun show(number: Int, value: String) {
        viewBinding.apply {
            tvTitle.text = "$number. $value"
            root.setOnClickListener {
                delegate.onClick(value)
            }
        }
    }
}