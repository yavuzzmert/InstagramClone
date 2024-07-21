package com.yavuzmert.a20kotlininstagram.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.yavuzmert.a20kotlininstagram.databinding.ActivityUploadBinding
import java.util.UUID

class UploadActivity : AppCompatActivity() {

    private lateinit var binding : ActivityUploadBinding
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    var selectedPicture : Uri? = null
    private lateinit var auth : FirebaseAuth
    private lateinit var firestore : FirebaseFirestore
    private lateinit var storage : FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        registerLauncher()

        auth = Firebase.auth
        firestore = Firebase.firestore
        storage = Firebase.storage
    }

    fun upload(view: View){

        //universal unique id
        val uuid = UUID.randomUUID()
        val imageName = "$uuid.jpg"

        val  reference = storage.reference
        val imageReference = reference.child("images").child(imageName)

        if(selectedPicture != null){
            imageReference.putFile(selectedPicture!!).addOnSuccessListener{
                    //download url -> firestore
                val uploadPictureReference = storage.reference.child("images").child(imageName)
                uploadPictureReference.downloadUrl.addOnSuccessListener {
                    val downloadUrl = it.toString()

                    //değerleri ekleyelim
                    if(auth.currentUser != null){
                    val postMap = hashMapOf<String, Any>()
                    postMap.put("downloadUrl", downloadUrl)
                    postMap.put("userEmail", auth.currentUser!!.email!!)
                    postMap.put("comment", binding.commentText.text.toString())
                        postMap.put("date", com.google.firebase.Timestamp.now())

                        firestore.collection("Posts").add(postMap).addOnSuccessListener {
                            finish()
                        }.addOnFailureListener {
                            Toast.makeText(this@UploadActivity, it.localizedMessage,Toast.LENGTH_LONG).show()
                        }
                    }
                }

            }.addOnFailureListener {
                Toast.makeText(this, it.localizedMessage,Toast.LENGTH_LONG).show()
            }
        }

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

        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){ result ->
            val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            activityResultLauncher.launch(intentToGallery)
            /*if(result == true){
                //permission granted
                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            } else {
                //permissin denied
                Toast.makeText(this@UploadActivity, "Permission needed!", Toast.LENGTH_LONG).show()
            }*/
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
            -uygulamadan firebase'e depolamak için;
                1.auth
                2.firestore
                3.storage taınmlayacağız.
                daha sonra onCreate altında auth tanımlıyoruz,
            -upload fun altında, firebase'deki reference konumu alıyoruz
            -güncel kullanıcı auth'dan alıyoruz.
            note; kaydettiğimiz görselin url'sini de alıp string olarak kaydetmemiz lazım, yükledikten sonra çağıracağız
            -Timestamp.now(), güncel zamanı veriyor.
         4. verileri çekmek, iki yolu var getDataOnce bir defa çekme veya listenForRealTimeUpdate, canlı veri çekme durumu
            note; firebase documentation'dan her şeyi inceleyebiliyoruz ve feedActivity'den devam ediyoruz.

 */