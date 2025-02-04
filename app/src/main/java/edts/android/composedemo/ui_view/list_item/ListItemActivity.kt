package edts.android.composedemo.ui_view.list_item

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import edts.android.composedemo.constants.getListData
import edts.android.composedemo.databinding.ListItemActivityBinding

class ListItemActivity: AppCompatActivity() {
    private lateinit var binding: ListItemActivityBinding

    companion object{
        fun open(activity: Activity){
            val intent = Intent(activity, ListItemActivity::class.java)
            activity.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ListItemActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val myAdapter = ListItemAdapter(getListData())
        myAdapter.delegate = object : ListItemDelegate{
            override fun onClick(value: String) {
                Toast.makeText(this@ListItemActivity, value, Toast.LENGTH_SHORT).show()
            }
        }
        binding.rvList.apply {
            layoutManager = LinearLayoutManager(this@ListItemActivity)
            adapter = myAdapter
        }
    }
}