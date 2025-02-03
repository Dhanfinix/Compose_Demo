package dhandev.android.composedemo.ui_view.list_item

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dhandev.android.composedemo.databinding.NumberedValueItemBinding

class ListItemAdapter(
    private val myList: List<String>
): RecyclerView.Adapter<ListItemHolder>() {
    lateinit var delegate : ListItemDelegate
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListItemHolder {
        val binding = NumberedValueItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ListItemHolder(binding, delegate)
    }

    override fun getItemCount() = myList.size

    override fun onBindViewHolder(holder: ListItemHolder, position: Int) {
        holder.show(position+1, myList[position])
    }
}