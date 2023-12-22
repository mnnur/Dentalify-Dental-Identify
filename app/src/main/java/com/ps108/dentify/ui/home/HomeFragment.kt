package com.ps108.dentify.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ps108.dentify.data.Diagnosis
import com.ps108.dentify.databinding.FragmentHomeBinding
import com.ps108.dentify.ui.diagnosis.DiagnosisAdapter

class HomeFragment : Fragment() {
    private lateinit var binding : FragmentHomeBinding
    private val diagnosisList: MutableLiveData<List<Diagnosis>> = MutableLiveData()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater,container, false)

        val user = FirebaseAuth.getInstance().currentUser!!
        val db = FirebaseFirestore.getInstance()
        val userEmail = user.email

        val recyleView = binding.rvDiagnosisList
        recyleView.layoutManager = LinearLayoutManager(requireActivity())
        val adapter = DiagnosisAdapter()
        recyleView.adapter = adapter

        diagnosisList.observe(requireActivity()) { diagnoses ->
            // Update the adapter with the new data
            adapter.submitList(diagnoses)
        }

        val query = db.collection("diagnosis").whereEqualTo("strEmail", userEmail)

        query.get()
            .addOnSuccessListener { queryDocumentSnapshots ->
                val diagnoses = mutableListOf<Diagnosis>()
                // Iterate through the documents
                for (documentSnapshot in queryDocumentSnapshots) {
                    // Access the data in each document
                    val email = documentSnapshot.getString("strEmail")
                    val diagnosis = documentSnapshot.getString("strDiagnosis")
                    val imageUrl = documentSnapshot.getString("strImageUrl")
                    val date = documentSnapshot.getString("strDate")
                    val confidence = documentSnapshot.getDouble("floatConfidence")
                    val dataDiagnosis = Diagnosis(
                        documentSnapshot.id,
                        email ?: "",
                        diagnosis ?: "",
                        imageUrl ?: "",
                        date ?: "",
                        confidence?.toFloat() ?: 0f
                    )

                    diagnoses.add(dataDiagnosis)
                    Log.d("DB", "Diagnosis: $diagnosis, Email: $email")
                }
                diagnosisList.value = diagnoses
            }
            .addOnFailureListener { e ->
                // Handle any errors
                Log.e("DB", "Error getting documents: ", e)
            }
        return binding.root

    }

}