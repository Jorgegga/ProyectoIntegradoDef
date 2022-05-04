package com.example.proyectointegradodef.musica

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.viewpager2.widget.ViewPager2
import com.example.proyectointegradodef.R
import com.example.proyectointegradodef.databinding.ActivityMusicaBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MusicaActivity : AppCompatActivity() {

    lateinit var binding: ActivityMusicaBinding
    var tabTitle = arrayOf("Albums", "Autores", "Música")
    lateinit var tabLayout: TabLayout
    lateinit var viewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMusicaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setTitle("Scarlet Perception")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        tabLayout = binding.tabLayout
        viewPager = binding.viewPager
        viewPager.adapter = AdapterMusic(supportFragmentManager, lifecycle)

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