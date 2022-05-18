package com.example.simplerates

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.OnBackPressedCallback


class FavouritesActivity : AppCompatActivity() {
    private var fav_list: ListView? = null
    private var fav_array: ArrayList<String> = ArrayList()
    private var adapter: MyAdapter? = null

    private var source_field: EditText? = null
    private var target_field: EditText? = null
    private var add_btn: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favourites)

        initFavList()
        initAddingFields()
        initBackBtn()
    }

    fun initFavList() {
        fav_list = findViewById(R.id.rates_list)
        fav_array.add("test1")
        fav_array.add("test2")
        fav_array.add("test3")
        adapter = MyAdapter(this, fav_array)
        fav_list?.adapter = adapter
    }

    fun initAddingFields() {
        source_field = findViewById(R.id.source_field)
        target_field = findViewById(R.id.target_field)

        add_btn = findViewById(R.id.fav_add_btn)
        add_btn?.setOnClickListener {
            if (arrayOf(source_field, target_field).any {
                    it?.text?.toString()?.trim()?.equals("")!!
                }) {
                Toast.makeText(this, getString(R.string.empty_field_error), Toast.LENGTH_LONG)
                    .show();

            } else {
                var sourceCurrency = source_field?.text?.toString()?.trim()?.uppercase()
                var targetCurrency = target_field?.text?.toString()?.trim()?.uppercase()
                var res =
                    "${1} ${sourceCurrency} = ${1} ${targetCurrency}"
                fav_array.add(res)
                adapter?.notifyDataSetChanged()
            }
        }
    }

    fun initBackBtn() {
        var callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                var intent = Intent(this@FavouritesActivity, MainActivity::class.java)
                startActivity(intent)
            }
        }
        this.onBackPressedDispatcher.addCallback(this, callback)
    }
}


class MyAdapter(private val context: Context, private val arrayList: ArrayList<String>) : BaseAdapter() {

    override fun getCount(): Int {
        return arrayList.size
    }

    override fun getItem(position: Int): Any {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, view: View?, parent: ViewGroup): View? {
        var res_view = LayoutInflater.from(context).inflate(R.layout.fav_item, parent, false)
        var item_text: TextView? = res_view?.findViewById(R.id.fav_item_text)
        item_text?.text = arrayList[position]

        return res_view
    }
}