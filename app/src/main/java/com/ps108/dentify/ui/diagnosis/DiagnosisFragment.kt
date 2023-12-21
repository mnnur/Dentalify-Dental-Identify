package com.ps108.dentify.ui.diagnosis

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.ps108.dentify.MainActivity
import com.ps108.dentify.R
import com.ps108.dentify.data.Diagnosis
import com.ps108.dentify.databinding.FragmentDiagnosisBinding
import com.ps108.dentify.ml.ModelDentalify13kelas
import com.ps108.dentify.ui.camera.CameraActivity
import com.ps108.dentify.ui.camera.CameraActivity.Companion.CAMERAX_RESULT
import com.ps108.dentify.ui.detail.DetailActivity
import com.ps108.dentify.ui.detail.DetailActivity.Companion.DIAGNOSIS_RESULT
import com.ps108.dentify.ui.home.HomeFragment
import com.ps108.dentify.utils.getCurrentDateTime
import com.ps108.dentify.utils.reduceFileImage
import com.ps108.dentify.utils.uriToFile
import kotlinx.coroutines.launch
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.File
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder


class DiagnosisFragment : Fragment() {
    private var currentImageUri: Uri? = null
    private lateinit var binding : FragmentDiagnosisBinding
    private val imageSize = 244
    private lateinit var user : FirebaseUser
    private var resultMl = ""

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
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDiagnosisBinding.inflate(inflater,container, false)


        user = FirebaseAuth.getInstance().currentUser!!
        binding.btnGallery.setOnClickListener { startGallery() }
        binding.btnCamera.setOnClickListener { showAlertDialog() }
        binding.btnUpload.setOnClickListener { uploadImage() }

        return binding.root
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.ivPreview.setImageURI(it)
        }
    }

    private fun startCameraX() {
        val intent = Intent(requireActivity(), CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERAX_RESULT) {
            currentImageUri = it.data?.getStringExtra(CameraActivity.EXTRA_CAMERAX_IMAGE)?.toUri()
            showImage()
        }
    }

    private fun showAlertDialog() {
        val alertDialogBuilder = AlertDialog.Builder(requireActivity())

        alertDialogBuilder.setTitle(getString(R.string.alert_camera_title))
        alertDialogBuilder.setMessage(getString(R.string.alert_camera))

        alertDialogBuilder.setPositiveButton(getString(R.string.alert_camera_confirm)) { dialog, _ ->
            dialog.dismiss()
            startCameraX()
        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

     private fun classifyImage(image : Bitmap)
    {
        try {
            val model: ModelDentalify13kelas = ModelDentalify13kelas.newInstance(requireActivity())

            // Creates inputs for reference.
            val inputFeature0 =
                TensorBuffer.createFixedSize(intArrayOf(1, imageSize, imageSize, 3), DataType.FLOAT32)
            val byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3)
            byteBuffer.order(ByteOrder.nativeOrder())
            val intValues = IntArray(imageSize * imageSize)
            image.getPixels(intValues, 0, image.width, 0, 0, image.width, image.height)
            var pixel = 0
            //iterate over each pixel and extract R, G, and B values. Add those values individually to the byte buffer.
            for (i in 0 until imageSize) {
                for (j in 0 until imageSize) {
                    val `val` = intValues[pixel++] // RGB
                    byteBuffer.putFloat((`val` shr 16 and 0xFF) * (1f / 1))
                    byteBuffer.putFloat((`val` shr 8 and 0xFF) * (1f / 1))
                    byteBuffer.putFloat((`val` and 0xFF) * (1f / 1))
                }
            }
            inputFeature0.loadBuffer(byteBuffer)

            // Runs model inference and gets result.
            val outputs: ModelDentalify13kelas.Outputs = model.process(inputFeature0)
            val outputFeature0: TensorBuffer = outputs.outputFeature0AsTensorBuffer
            val confidences = outputFeature0.floatArray
            // find the index of the class with the biggest confidence.
            var maxPos = 0
            var maxConfidence = 0f
            for (i in confidences.indices) {
                if (confidences[i] > maxConfidence) {
                    maxConfidence = confidences[i]
                    maxPos = i
                }
            }
            val classes = arrayOf(
                "CaS",
                "Cos",
                "Gum",
                "MC",
                "OC",
                "OLP",
                "OT",
                "caries",
                "gingivitis",
                "toothDiscoloration",
                "ulcers",
                "hypodontia",
                "calculus")

            resultMl = classes[maxPos]

            model.close()

        } catch (e: IOException) {
            // TODO Handle the exception
        }
    }

    private fun storeToDb(imageUrl : String){
        val db = FirebaseFirestore.getInstance()
        val diagnosis = hashMapOf(
            "strEmail" to "${user.email}",
            "strDiagnosis" to resultMl,
            "strImageUrl" to imageUrl,
            "strDate" to getCurrentDateTime("dd/MM/yyyy")
        )
        db.collection("diagnosis").add(diagnosis)
            .addOnSuccessListener { documentReference ->
                Toast.makeText(requireActivity(), "Upload berhasil", Toast.LENGTH_SHORT).show()
                Log.d("DB", "DocumentSnapshot added with ID: ${documentReference.id}")
                binding.barDiagnosis.visibility = View.GONE

                val intent = Intent(requireActivity(), MainActivity::class.java)
                startActivity(intent)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireActivity(), "Upload gagal", Toast.LENGTH_SHORT).show()
                Log.w("DB", "Error adding document : ${exception.message}")
                binding.barDiagnosis.visibility = View.GONE
            }
    }

    private fun uploadImage() {
        binding.barDiagnosis.visibility = View.VISIBLE
        if (currentImageUri != null) {
            val imageFile = uriToFile(currentImageUri!!, requireActivity()).reduceFileImage()
            var image: Bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
            image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false)

            viewLifecycleOwner.lifecycleScope.launch {
                classifyImage(image)
                upload(imageFile)
            }
        } else {
            Toast.makeText(requireActivity(), "Pilih foto terlebih dahulu", Toast.LENGTH_SHORT).show()
            binding.barDiagnosis.visibility = View.GONE
        }
    }

    private fun upload(imageFile : File){
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference

        val imageName = "${user.displayName}_${getCurrentDateTime("yyyy_MM_dd_HH_mm_ss")}"

        val imageRef = storageRef.child("images/$imageName")

        val imageUri = Uri.fromFile(File(imageFile.toURI()))

        val uploadTask = imageRef.putFile(imageUri)

        uploadTask.addOnProgressListener { taskSnapshot: UploadTask.TaskSnapshot ->
            val progress =
                100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount
        }.addOnSuccessListener { taskSnapshot: UploadTask.TaskSnapshot? ->
            imageRef.downloadUrl
                .addOnSuccessListener { uri: Uri ->
                    val imageUrl = uri.toString()
                    storeToDb(imageUrl)

                }
        }.addOnFailureListener { exception: Exception? ->
            Toast.makeText(requireActivity(), "Upload gagal", Toast.LENGTH_SHORT).show()
            binding.barDiagnosis.visibility = View.GONE
        }
    }

}