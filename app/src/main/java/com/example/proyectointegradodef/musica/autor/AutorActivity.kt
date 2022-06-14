package com.example.proyectointegradodef.musica.autor

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.viewpager2.widget.ViewPager2
import com.example.proyectointegradodef.R
import com.example.proyectointegradodef.databinding.ActivityAutorBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

/**
 * Autor activity
 *
 * @constructor Create empty Autor activity
 */
class AutorActivity : AppCompatActivity() {

    lateinit var binding: ActivityAutorBinding
    var tabTitle = arrayOf("Albums", "Musica")
    lateinit var tabLayout: TabLayout
    lateinit var viewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAutorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Scarlet Perception"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        tabLayout = binding.tabLayout
        viewPager = binding.viewPager

        val bundle = intent.extras
        viewPager.adapter = AutorAdapterActivity(supportFragmentManager, lifecycle, bundle!!)

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