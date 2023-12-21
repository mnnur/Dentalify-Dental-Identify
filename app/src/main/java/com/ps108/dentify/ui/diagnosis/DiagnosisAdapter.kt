package com.ps108.dentify.ui.diagnosis

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ps108.dentify.R
import com.ps108.dentify.data.Diagnosis
import com.ps108.dentify.ui.detail.DetailActivity
import com.ps108.dentify.ui.detail.DetailActivity.Companion.DIAGNOSIS_RESULT
import com.ps108.dentify.utils.descriptionSelector
import com.ps108.dentify.utils.titleSelector

class DiagnosisAdapter : ListAdapter<Diagnosis, DiagnosisAdapter.DiagnosisViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiagnosisViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.diagnosis_history_list, parent, false)
        return DiagnosisViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DiagnosisViewHolder, position: Int) {
        val diagnosis = getItem(position) as Diagnosis
        holder.bind(diagnosis)
    }

    inner class DiagnosisViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val rvTitle: TextView = itemView.findViewById(R.id.rvTitleDiagnosis)
        private val rvDate: TextView = itemView.findViewById(R.id.rvDate)
        private val rvImage: ImageView = itemView.findViewById(R.id.rv_image)
        private val rvDescription: TextView = itemView.findViewById(R.id.tv_item_description)

        lateinit var getDiagnosis: Diagnosis

        fun bind(diagnosis: Diagnosis) {
            getDiagnosis = diagnosis
            rvTitle.text = titleSelector(diagnosis.diagnosis)
            rvDate.text = diagnosis.date
            rvDescription.text = descriptionSelector(diagnosis.diagnosis)
            Glide.with(itemView).load(diagnosis.imageUrl).into(rvImage)
            itemView.setOnClickListener {
                val detailIntent = Intent(itemView.context, DetailActivity::class.java)
                detailIntent.putExtra("DIAGNOSIS_RESULT", diagnosis)
                itemView.context.startActivity(detailIntent)
            }
        }

    }

    companion object {

        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Diagnosis>() {
            override fun areItemsTheSame(oldItem: Diagnosis, newItem: Diagnosis): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Diagnosis, newItem: Diagnosis): Boolean {
                return oldItem == newItem
            }
        }

    }

}