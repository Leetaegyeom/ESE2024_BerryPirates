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
            documentNameTextView.text = record.documentName
            leftHeightTextView.text = "${record.leftHeight}"
            leftAngleTextView.text = "${record.leftAngle}"
            rightHeightTextView.text = "${record.rightHeight}"
            rightAngleTextView.text = "${record.rightAngle}"

            itemView.setOnClickListener {
                onRecordClick(record)
            }
        }
    }
}
