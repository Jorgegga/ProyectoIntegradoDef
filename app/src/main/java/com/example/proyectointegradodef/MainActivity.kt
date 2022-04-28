package com.example.proyectointegradodef

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.proyectointegradodef.databinding.ActivityMainBinding
import com.example.proyectointegradodef.preferences.Prefs
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class MainActivity : AppCompatActivity() {
    lateinit var binding : ActivityMainBinding
    private val GOOGLE_SIGN_IN = 150
    lateinit var prefs : Prefs
    var default_web_client_id = "575086626083-8fa3o0v47cu7c1du6nnj8mdh4lqn4nk7.apps.googleusercontent.com"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        btnListeners()
        comprobarSesion()

    }
    private fun btnListeners(){
        binding.btnLogin.setOnClickListener {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(default_web_client_id)
                .requestEmail()
                .build()
            val googleClient = GoogleSignIn.getClient(this, gso)
            googleClient.signOut()
            startActivityForResult(googleClient.signInIntent, GOOGLE_SIGN_IN)
        }
    }

    private fun comprobarSesion(){
        prefs = Prefs(this)
        val email = prefs.leerEmail()
        if(!email.isNullOrEmpty()){
            irMenu()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == GOOGLE_SIGN_IN){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.getResult(ApiException::class.java)
            if(account !=null){
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener {
                    if (it.isSuccessful) {
                        guardarCorreo(it.result?.user?.email)
                        irMenu()
                    }else{
                        Log.d("Error: ", it.exception.toString())
                    }
                }
            }
        }
    }

    private fun irMenu(){
        val i = Intent(this, InicioActivity::class.java)
        startActivity(i)
    }

    private fun guardarCorreo(correo: String?){
        prefs = Prefs(this)
        prefs.guardarEmail(correo!!)
    }
}