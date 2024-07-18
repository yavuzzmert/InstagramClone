package com.yavuzmert.a20kotlininstagram

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.yavuzmert.a20kotlininstagram.databinding.ActivityUploadBinding

class UploadActivity : AppCompatActivity() {

    private lateinit var binding : ActivityUploadBinding
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    var selectedPicture : Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        registerLauncher()
    }

    fun upload(view: View){

    }

    fun selectImage(view : View){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
             if(ActivityCompat.shouldShowRequestPermissionRationale(this@UploadActivity,Manifest.permission.READ_EXTERNAL_STORAGE)){
                    Snackbar.make(view, "Permission needed for gallery", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission"){
                        //request permission
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }.show()
             } else {
                 //request permission
                 permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
             }
        } else {
            val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            //start activity for result
            activityResultLauncher.launch(intentToGallery)
        }
    }

    private fun registerLauncher(){

        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if(result.resultCode == RESULT_OK){
                val intentFromResult = result.data
                if(intentFromResult != null){
                    selectedPicture = intentFromResult.data
                    selectedPicture?.let {
                        binding.imageView.setImageURI(it)
                    }
                }
           }
        }

        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){result ->
            if(result){
                //permission granted
                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            } else{
                //permissin denied
                Toast.makeText(this@UploadActivity, "Permission needed!", Toast.LENGTH_LONG).show()
            }
            }
        }
}

/*
    - upload işlemi için görsel yüklememiz lazım onun adımları
        1. manifest'e giderek readExternalStorage iznini eklememiz gerekiyor.
            note; bu dangerous bir permission çünkü kullanıcıya açık açık sormamız gerekiyor
        2. selectImage fun altında bu izni daha önce aldık mı diye kontrol etmemiz gerekiyor,
            -iznin mantığını gösterme durumunu android'e soruyoruz 2. if kısmında
            -İznin rational kısmına gerek yoksa request permission
            -izni aldıysak gallery kısmına intent yapıyoruz direkt
            -Hem requestPermission hem de startActivityForResult için ActivityResultLauncher sınıfından faydalanmamız lazım
            -activityResultLauncher ve permissionLauncher onCreate altında register etmemiz gerekiyor o yüzden fun tanımladık registerLauncher adında olacak şekilde.
            -görselin Uri'ını registerForActivityResult(ActivityResultContracts.StartActivityForResult(),) kısmında alıyoruz ve virgülden sonra bir callback dönüyor bu da görselin uri alındıktan sonra kullanıcı ne yapacak ve ona göre de resultCode kontrol ediyoruz ok, cancelled gibi çünkü kullanıcı iptal de edebilir image seçmeyi.
            -Uri intentFromResult ile dönüş yapıyor bize onu da bir global değişkende tutabiliriz ve imageView'e direkt koyabiliriz.
         3. upload işlemini yapacağız o yüzden firebase'deki veri depolama ve firestore kısmını çözmeliyiz.
            -firebase'de iki tane alan var FirestoreDatabase ve RealtimeDatabase ve yeni teknoloji olan FirestoreDatabase
            note; bu ikisi de NoSQL dediğimiz bir veri tabanı ve firebase database oluşturacağız.
            -bu tip veri tabanlarında collections and documents var.
            -documents kısmını storage'e koyacağız ve oradan alacağımız url'yi firestore kısmına imageurl olarak koyacağız.
 */