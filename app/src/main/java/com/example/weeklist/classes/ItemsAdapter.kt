package com.example.weeklist.classes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weeklist.R
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Locale

class ItemsAdapter(private val itemsList: ArrayList<Item>, private val onItemClick: OnItemClick) :
    RecyclerView.Adapter<ItemsAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.tvName)
        val price: TextView = itemView.findViewById(R.id.tvPrice)
        val date: TextView = itemView.findViewById(R.id.tvDate)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemsAdapter.ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.layout_item, parent, false)

        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ItemsAdapter.ViewHolder, position: Int) {

        val currentItem = itemsList[position]
        holder.name.text = currentItem.name?.uppercase()
        holder.price.text = currentItem.price
        holder.date.text = currentItem.createdAt

        holder.itemView.setOnClickListener(View.OnClickListener {
            onItemClick.onClick(currentItem)
        })

        holder.itemView.setOnLongClickListener(View.OnLongClickListener {
            onItemClick.onItemLongClick(currentItem)

            return@OnLongClickListener true
        })
    }


    override fun getItemCount(): Int {
        return itemsList.size
    }
}