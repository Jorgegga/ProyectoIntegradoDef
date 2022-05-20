package com.example.proyectointegradodef


import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.style.TextAppearanceSpan
import android.util.DisplayMetrics
import android.view.Display
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.proyectointegradodef.camara.CamaraFragment
import com.example.proyectointegradodef.musica.music.CrearFragment
import com.example.proyectointegradodef.musica.music.MusicaFragment
import com.example.proyectointegradodef.databinding.ActivityInicioBinding
import com.example.proyectointegradodef.glide.GlideApp
import com.example.proyectointegradodef.musica.MusicaActivity
import com.example.proyectointegradodef.preferences.Prefs
import com.example.proyectointegradodef.room.CrearRoomFragment
import com.example.proyectointegradodef.webview.WebFragment
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage


class InicioActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener  {
    lateinit var binding : ActivityInicioBinding
    lateinit var transaction : FragmentTransaction
    lateinit var reference: DatabaseReference
    lateinit var db: FirebaseDatabase
    var storageFire = FirebaseStorage.getInstance()

    lateinit var fragmentPortada : Fragment
    lateinit var fragmentWeb : Fragment
    lateinit var fragmentCrear : Fragment
    lateinit var fragmentRead : Fragment
    lateinit var fragmentCrearLocal : Fragment
    lateinit var fragmentCamara : Fragment

    val user = Firebase.auth.currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInicioBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setToolbar()
        initDb()
        setHeader()
        title="Scarlet Perception"
        fragmentPortada = PortadaFragment()
        fragmentWeb = WebFragment()
        fragmentCrear = CrearFragment()
        fragmentRead = MusicaFragment()
        fragmentCrearLocal = CrearRoomFragment()
        fragmentCamara = CamaraFragment()
        supportFragmentManager.beginTransaction().add(R.id.fragmentContainerView, fragmentPortada).commit()

    }


    fun setToolbar(){
        val toolbar: Toolbar = binding.mainToolbar
        setSupportActionBar(toolbar)

        var drawerLayout = binding.drawerLayout
        var navigationView = binding.navView

        var actionBarDrawerToggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.openNavDrawer,
            R.string.closeNavDrawer
        )
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
        navigationView.setNavigationItemSelectedListener(this)
    }

    fun setHeader(){
        var prefs = Prefs(this)
        var navView = findViewById<NavigationView>(R.id.nav_view)
        var header = navView.getHeaderView(0)
        var tvCorreo = header.findViewById<TextView>(R.id.tvCorreo)
        tvCorreo.text = prefs.leerEmail()

        var referencia2 = ""
        reference.child(user!!.uid).child("ruta").get().addOnSuccessListener {
            if (it.value == null) {
                header.findViewById<ImageView>(R.id.ivPerfil).setImageDrawable(
                    AppCompatResources.getDrawable(
                        this,
                        R.drawable.keystoneback
                    )
                )
            } else {
                referencia2 = it.value as String
                val gsReference2 = storageFire.getReferenceFromUrl(referencia2 + ".png")
                val option = RequestOptions().error(R.drawable.keystoneback)
                GlideApp.with(this).load(gsReference2).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).apply(option).into(header.findViewById<ImageView>(R.id.ivPerfil))
            }
        }

        val display: Display = windowManager.defaultDisplay
        val outMetrics = DisplayMetrics()
        display.getMetrics(outMetrics)

        val density = resources.displayMetrics.density
        val dpHeight = outMetrics.heightPixels / density
        val dpWidth = outMetrics.widthPixels / density

        if(dpWidth >= 720){
            cambiarSubtitulos()
        }

    }

    fun cambiarSubtitulos(){
        var navView = findViewById<NavigationView>(R.id.nav_view)
        var menu = navView.menu
        var music = menu.findItem(R.id.group1)
        var user = menu.findItem(R.id.group2)
        var others = menu.findItem(R.id.group3)
        var s = SpannableString(music.title)
        s.setSpan(TextAppearanceSpan(this, R.style.TextAppearance44), 0, s.length, 0)
        music.setTitle(s)
        s = SpannableString(user.title)
        s.setSpan(TextAppearanceSpan(this, R.style.TextAppearance44), 0, s.length, 0)
        user.setTitle(s)
        s = SpannableString(others.title)
        s.setSpan(TextAppearanceSpan(this, R.style.TextAppearance44), 0, s.length, 0)
        others.setTitle(s)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        transaction = supportFragmentManager.beginTransaction()
        when(item.itemId){
            R.id.btnInicio -> {
                transaction.replace(R.id.fragmentContainerView, fragmentPortada).commit()
                transaction.addToBackStack(null)
                item.isChecked = true
                binding.drawerLayout.close()
                return true
            }

            R.id.btnCanciones ->{
                val i = Intent(this, MusicaActivity::class.java)
                startActivity(i)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                binding.drawerLayout.close()
                return true
            }

            R.id.btnSubir ->{
                transaction.replace(R.id.fragmentContainerView, fragmentCrear).commit()
                transaction.addToBackStack(null)
                item.isChecked = true
                binding.drawerLayout.close()
                return true
            }

            R.id.btnSubirLocal ->{
                transaction.replace(R.id.fragmentContainerView, fragmentCrearLocal).commit()
                transaction.addToBackStack(null)
                item.isChecked = true
                binding.drawerLayout.close()
                return true
            }

            R.id.btnWeb ->{
                transaction.replace(R.id.fragmentContainerView, fragmentWeb).commit()
                transaction.addToBackStack(null)
                item.isChecked = true
                binding.drawerLayout.close()
                return true
            }

            R.id.btnSesion ->{
                val pref= Prefs(this)
                FirebaseAuth.getInstance().signOut()
                pref.borrarTodo()
                finish()
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                return true
            }

            R.id.btnPerfil ->{
                transaction.replace(R.id.fragmentContainerView, fragmentCamara).commit()
                transaction.addToBackStack(null)
                item.isChecked = true
                binding.drawerLayout.close()
                return true
            }

            else ->{
                return false
            }
        }
    }


    override fun onBackPressed() {
        val count = supportFragmentManager.backStackEntryCount

        if (count == 0) {
            super.onBackPressed()
            //additional code
        } else {
            supportFragmentManager.popBackStack()
        }
    }

    private fun initDb(){
        db = FirebaseDatabase.getInstance("https://proyectointegradodam-eef79-default-rtdb.europe-west1.firebasedatabase.app/")
        reference = db.getReference("perfil")
    }

}