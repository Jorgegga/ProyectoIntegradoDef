package com.example.proyectointegradodef.musica

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.viewpager2.widget.ViewPager2
import com.example.proyectointegradodef.R
import com.example.proyectointegradodef.databinding.ActivityMusicaBinding
import com.example.proyectointegradodef.musica.playlist.PlaylistActivity
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MusicaActivity : AppCompatActivity() {

    lateinit var binding: ActivityMusicaBinding
    lateinit var tabLayout: TabLayout
    lateinit var viewPager: ViewPager2

    var tabTitle = arrayOf("Albums", "Autores", "Música")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMusicaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Scarlet Perception"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        tabLayout = binding.tabLayout
        viewPager = binding.viewPager
        viewPager.adapter = MusicaAdapter(supportFragmentManager, lifecycle)

        TabLayoutMediator(tabLayout, viewPager){
            tab, position ->
                tab.text = tabTitle[position]

        }.attach()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_musica, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.playlistButton->{
                val i = Intent(this, PlaylistActivity::class.java)
                startActivity(i)
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
    
    override fun onSupportNavigateUp() : Boolean{
        finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        return true
    }
}