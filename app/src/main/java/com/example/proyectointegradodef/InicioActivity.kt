package com.example.proyectointegradodef



import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.style.TextAppearanceSpan
import android.util.DisplayMetrics
import android.util.Log
import android.view.Display
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.proyectointegradodef.aboutus.AboutusFragment
import com.example.proyectointegradodef.databinding.ActivityInicioBinding
import com.example.proyectointegradodef.glide.GlideApp
import com.example.proyectointegradodef.models.CrearPerfil
import com.example.proyectointegradodef.musica.MusicaActivity
import com.example.proyectointegradodef.musica.crud.CrudActivity
import com.example.proyectointegradodef.musica.music.CrearFragment
import com.example.proyectointegradodef.musica.music.MusicaFragment
import com.example.proyectointegradodef.musica.playlist.PlaylistActivity
import com.example.proyectointegradodef.perfil.PerfilFragment
import com.example.proyectointegradodef.preferences.AppUse
import com.example.proyectointegradodef.preferences.Prefs
import com.example.proyectointegradodef.room.CrearRoomFragment
import com.example.proyectointegradodef.webview.WebFragment
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.*


/**
 * Inicio activity
 *
 * @constructor Create empty Inicio activity
 */
class InicioActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener  {
    lateinit var binding : ActivityInicioBinding
    lateinit var transaction : FragmentTransaction
    lateinit var reference: DatabaseReference
    lateinit var db: FirebaseDatabase

    lateinit var fragmentPortada : Fragment
    lateinit var fragmentWeb : Fragment
    lateinit var fragmentRead : Fragment
    lateinit var fragmentCrearLocal : Fragment
    lateinit var fragmentCamara : Fragment
    lateinit var fragmentAboutus : Fragment

    var storageFire = FirebaseStorage.getInstance()
    val user = Firebase.auth.currentUser
    var introUser: MutableList<CrearPerfil> = ArrayList()
    var crearId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInicioBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setToolbar()
        hideItem()
        initDb()
        annadirUsuario()
        setHeader()
        title="Scarlet Perception"
        fragmentPortada = PortadaFragment()
        fragmentWeb = WebFragment()
        fragmentRead = MusicaFragment()
        fragmentCrearLocal = CrearRoomFragment()
        fragmentCamara = PerfilFragment()
        fragmentAboutus = AboutusFragment()
        supportFragmentManager.beginTransaction().add(R.id.fragmentContainerView, fragmentPortada).commit()

    }

    override fun onResume() {
        super.onResume()
        if(AppUse.user_id == 0){
            setHeader()
            annadirUsuario()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return super.onCreateOptionsMenu(menu)

    }

    /**
     * Set toolbar
     *
     */
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

    /**
     * Set header
     *
     */
    fun setHeader(){
        var prefs = Prefs(this)
        var navView = findViewById<NavigationView>(R.id.nav_view)
        var header = navView.getHeaderView(0)
        var tvCorreo = header.findViewById<TextView>(R.id.tvCorreo)
        tvCorreo.text = prefs.leerEmail()

        reference.child(user!!.uid).child("permisos").get().addOnSuccessListener {
            if(it.value != null) {
                var x: Int = it.value.toString().toInt()
                if (x == 1) {
                    val nav_Menu: Menu = binding.navView.menu
                    nav_Menu.findItem(R.id.btnCrud).isVisible = true
                }
            }
        }.addOnFailureListener {

        }

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
                val gsReference2 = storageFire.getReferenceFromUrl("$referencia2.png")
                val option = RequestOptions().error(R.drawable.keystoneback)
                GlideApp.with(this).load(gsReference2).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).apply(option).into(header.findViewById<ImageView>(R.id.ivPerfil))
            }
        }.addOnFailureListener {
            Toast.makeText(this, R.string.reintentar, Toast.LENGTH_LONG).show()
            CoroutineScope(Dispatchers.Main).launch{
                    delay(10000)
                    setHeader()
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

    private fun hideItem() {
        val nav_Menu: Menu = binding.navView.menu
        nav_Menu.findItem(R.id.btnCrud).isVisible = false
    }

    private fun annadirUsuario(){
        reference.child(user!!.uid).child("id").get().addOnSuccessListener {
            if(it.value == null){
                buscarId()
            }else{
                AppUse.user_id = it.value.toString().toInt()
            }
        }.addOnFailureListener {
            Toast.makeText(this, R.string.reintentar, Toast.LENGTH_LONG).show()
            runBlocking {
                CoroutineScope(Dispatchers.IO).launch{
                    delay(10000)
                    annadirUsuario()
                }

            }
        }

    }

    private fun buscarId(){
        reference.get()
        reference.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(crearId == 0) {
                    for (messageSnapshot in snapshot.children) {
                        var user = messageSnapshot.getValue<CrearPerfil>(CrearPerfil::class.java)
                        if (user != null) {
                            introUser.add(user)
                        }
                    }
                    filtrarDatos()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun filtrarDatos(){
        if(crearId == 0) {
            var tempUser = introUser.maxByOrNull { it.id }
            crearId = tempUser!!.id + 1
            reference.child(user!!.uid).setValue(CrearPerfil(crearId, 0))
            AppUse.user_id = crearId
        }
    }

    /**
     * Cambiar subtitulos
     *
     */
    fun cambiarSubtitulos(){
        var navView = findViewById<NavigationView>(R.id.nav_view)
        var menu = navView.menu
        var music = menu.findItem(R.id.group1)
        var user = menu.findItem(R.id.group2)
        var others = menu.findItem(R.id.group3)
        var s = SpannableString(music.title)
        s.setSpan(TextAppearanceSpan(this, R.style.TextAppearance44), 0, s.length, 0)
        music.title = s
        s = SpannableString(user.title)
        s.setSpan(TextAppearanceSpan(this, R.style.TextAppearance44), 0, s.length, 0)
        user.title = s
        s = SpannableString(others.title)
        s.setSpan(TextAppearanceSpan(this, R.style.TextAppearance44), 0, s.length, 0)
        others.title = s
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

            R.id.btnCrud ->{
                val i = Intent(this, CrudActivity::class.java)
                startActivity(i)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                binding.drawerLayout.close()
                return true
            }

            R.id.btnPlaylist->{
                val i = Intent(this, PlaylistActivity::class.java)
                startActivity(i)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
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
                /*transaction.replace(R.id.fragmentContainerView, fragmentWeb).commit()
                transaction.addToBackStack(null)
                item.isChecked = true
                binding.drawerLayout.close()
                return true*/
                Toast.makeText(this, "Proximamente", Toast.LENGTH_LONG).show()
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

            R.id.btnAboutus ->{
                transaction.replace(R.id.fragmentContainerView, fragmentAboutus).commit()
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