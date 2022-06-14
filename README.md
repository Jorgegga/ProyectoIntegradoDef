# ProyectoIntegradoDef
Proyecto integrado DAM

## Como montar el proyecto
Para montar el proyecto seguiremos los siguientes pasos:

-   Para empezar, será necesario tener creado un proyecto en firebase.
-   Agrega un proyecto con el nombre que quieras. Una vez creado entra en autentication 
-> Sign-in method y habilita el login por Google.
-   Tras esto entra en Realtime Database y crea este árbol de nodos: *albums, autors, generos, music, perfil, playlists*.
-   En reglas de realtime firebase pon estas de momento. 
*{
  "rules": {
    ".read": "true",
    ".write": "true",
  }
}*
-   En reglas de firebase storage pon estas de momento.
*rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    match /{allPaths=**} {
      allow read, write;
    }
  }
}*

- Todas estas reglas son públicas para poner utilizarla sin preocupaciones, en caso de querer sacarla a producción deberán ser cambiadas.

- Borra el archivo app -> Google-services.json

- Después, en nuestro proyecto ya creado deberemos ir a Tools -> Firebase y sincronizamos con Authentication, Realtime Database y Cloud storage for Firebase.

- Una vez hecho ejecutamos en el gradle el signingreport.

- Y copiamos la clave SHA1.

- De vuelta en firebase vamos a configuración del proyecto.

- Y aquí agregamos nuestra SHA1.

- Ya lo único que quedaría seria ir por el código y cambiar aquellas rutas del storage y del realtime database por tus propias rutas y ya estaría listo para buildear el proyecto.
