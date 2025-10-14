package com.example.gesttrack.paciente

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gesttrack.R
class NotasPaciente(private val notes: MutableList<String>) :
    RecyclerView.Adapter<NotasPaciente.NoteViewHolder>() {

    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNote: TextView = itemView.findViewById(R.id.tvNote)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_nota, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.tvNote.text = notes[position]
    }

    override fun getItemCount(): Int = notes.size

    fun addNote(note: String) {
        notes.add(note)
        notifyItemInserted(notes.size - 1)
    }
}
