package com.ps108.dentify.ui.settings.profile

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.ps108.dentify.data.Profile
import com.ps108.dentify.databinding.ActivityProfileBinding
import com.ps108.dentify.utils.reduceFileImage
import com.ps108.dentify.utils.uriToFile
import kotlinx.coroutines.launch
import java.io.File


class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private var currentImageUri: Uri? = null
    private var profile: Profile? = null

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        profile = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("PROFILE", Profile::class.java)
        } else {
            intent.getParcelableExtra<Profile>("PROFILE")
        }

        binding.edName.setText(profile?.name)
        binding.btnChangeImg.setOnClickListener {
            startGallery()
        }
        binding.btnChangeProfile.setOnClickListener {
            updateProfile()
        }

    }

    private fun updateProfile() {
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this).reduceFileImage()
            lifecycleScope.launch {
                uploadToStorage(imageFile)
            }
        }
    }

    private fun uploadToStorage(imageFile : File){
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference

        val imageName = "${profile?.id}_profile_image"

        val imageRef = storageRef.child("images_profile/$imageName")

        val imageUri = Uri.fromFile(File(imageFile.toURI()))

        val uploadTask = imageRef.putFile(imageUri)

        uploadTask.addOnProgressListener { taskSnapshot: UploadTask.TaskSnapshot ->
            val progress =
                100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount
        }.addOnSuccessListener { taskSnapshot: UploadTask.TaskSnapshot? ->
            imageRef.downloadUrl
                .addOnSuccessListener { uri: Uri ->
                    val imageUrl = uri.toString()
                    updateDb(imageUrl)
                }
        }.addOnFailureListener { exception: Exception? ->
            Toast.makeText(this, "edit gagal", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateDb(imageUrl : String){
        val db = FirebaseFirestore.getInstance()
        val updateProfile: MutableMap<String, Any> = HashMap()
        updateProfile["strName"] = binding.edName.text.toString()
        updateProfile["strImageUrl"] = imageUrl
        db.collection("profile").document(profile!!.id).update(updateProfile)
            .addOnSuccessListener { documentReference ->
                Toast.makeText(this, "update berhasil", Toast.LENGTH_SHORT).show()
                Log.d("DB", "DocumentSnapshot updated with ID: ${profile!!.id}")
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "update gagal", Toast.LENGTH_SHORT).show()
                Log.w("DB", "Error adding document : ${exception.message}")
            }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.btnChangeImg.setImageURI(it)
        }
    }

    companion object {
        const val PROFILE = "profile"
    }
}