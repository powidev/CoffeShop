package com.powidev.coffeshop.Activity

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.powidev.coffeshop.Adapter.ItemsListCategoryAdapter
import com.powidev.coffeshop.ViewModel.MainViewModel
import com.powidev.coffeshop.databinding.ActivityItemListBinding

class ItemListActivity : AppCompatActivity() {
    lateinit var binding:ActivityItemListBinding
    private val viewModel=MainViewModel()
    private var id: String=""
    private var title: String=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityItemListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getBundle()
        initList()

    }

    private fun initList() {
        binding.apply {
            progressBar.visibility=View.VISIBLE
            viewModel.loadItems(id).observe(this@ItemListActivity, Observer{
                listView.layoutManager=
                    LinearLayoutManager(this@ItemListActivity
                        , LinearLayoutManager.VERTICAL, false)
                listView.adapter= ItemsListCategoryAdapter(it)
                progressBar.visibility=View.GONE
            })
            backBtn.setOnClickListener { finish() }
        }
    }

    private fun getBundle() {
        id=intent.getStringExtra("id")!!
        title=intent.getStringExtra("title")!!

        binding.categoryTxt.text=title
    }
}