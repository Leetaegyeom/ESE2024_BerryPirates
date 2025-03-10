package com.example.practice_1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecordAdapter(
    private var records: MutableList<Record>,
    private val onRecordClick: (Record) -> Unit
) : RecyclerView.Adapter<RecordAdapter.RecordViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.record_recyclerview_item, parent, false)
        return RecordViewHolder(view, onRecordClick)
    }

    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {
        val record = records[position]
        holder.bind(record)
    }

    override fun getItemCount(): Int {
        return records.size
    }

    fun updateRecords(newRecords: List<Record>) {
        records = newRecords.toMutableList()
        notifyDataSetChanged()
    }

    fun removeRecord(record: Record) {
        val position = records.indexOf(record)
        if (position >= 0) {
            records.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun addRecord(record: Record) {
        records.add(record)
        notifyItemInserted(records.size - 1)
    }

    inner class RecordViewHolder(itemView: View, private val onRecordClick: (Record) -> Unit) : RecyclerView.ViewHolder(itemView) {
        private val documentNameTextView: TextView = itemView.findViewById(R.id.documentNameTextView)
        private val leftHeightTextView: TextView = itemView.findViewById(R.id.leftHeightTextView)
        private val leftAngleTextView: TextView = itemView.findViewById(R.id.leftAngleTextView)
        private val rightHeightTextView: TextView = itemView.findViewById(R.id.rightHeightTextView)
        private val rightAngleTextView: TextView = itemView.findViewById(R.id.rightAngleTextView)

        fun bind(record: Record) {
            val leftHeight = if (record.leftHeight < 0) 0 else if (record.leftHeight > 5) 5 else record.leftHeight
            val leftAngle = if (record.leftAngle < -5) -5 else if (record.leftAngle > 5) 5 else record.leftAngle
            val rightHeight = if (record.rightHeight < 0) 0 else if (record.rightHeight > 5) 5 else record.rightHeight
            val rightAngle = if (record.rightAngle < -5) -5 else if (record.rightAngle > 5) 5 else record.rightAngle

            documentNameTextView.text = record.documentName
            leftHeightTextView.text = "${leftHeight}단계"
            leftAngleTextView.text = "${leftAngle}단계"
            rightHeightTextView.text = "${rightHeight}단계"
            rightAngleTextView.text = "${rightAngle}단계"

            itemView.setOnClickListener {
                onRecordClick(record)
            }
        }

    }
}
