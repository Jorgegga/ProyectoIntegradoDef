package com.example.proyectointegradodef.musica.crud

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.viewpager2.widget.ViewPager2
import com.example.proyectointegradodef.R
import com.example.proyectointegradodef.databinding.ActivityCrudBinding
import com.example.proyectointegradodef.musica.MusicaAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

/**
 * Crud activity
 *
 * @constructor Create empty Crud activity
 */
class CrudActivity : AppCompatActivity() {

    lateinit var binding: ActivityCrudBinding
    lateinit var tabLayout: TabLayout
    lateinit var viewPager: ViewPager2

    var tabTitle = arrayOf("Autores", "Generos", "Albumes", "Musica")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCrudBinding.inflate(layoutInflater)

        setContentView(binding.root)
        var toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Scarlet Perception"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        tabLayout = binding.tabLayout
        viewPager = binding.viewPager
        viewPager.adapter = CrudAdapter(supportFragmentManager, lifecycle)

        TabLayoutMediator(tabLayout, viewPager){
                tab, position ->
            tab.text = tabTitle[position]

        }.attach()
    }

    override fun onSupportNavigateUp() : Boolean{
        finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        return true
    }
}