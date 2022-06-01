package com.example.roomdatabase

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.roomdatabase.databinding.ItemRowBinding

class ItemAdapter(private var items:ArrayList<EmployeeEntity>,
                    private var updateListener:(id:Int)->Unit,
                    private var deleteListener:(id:Int)->Unit
):RecyclerView.Adapter<ItemAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemRowBinding):RecyclerView.ViewHolder(binding.root){
        val constrainLayout=binding.constraintLayout
        val tvName=binding.tvName
        val tvEmail=binding.tvEmail
        val imgEdit=binding.imgEdit
        val imgDelete=binding.imgDelete
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemRowBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
      val context = holder.itemView.context
      val item=items[position]
      holder.tvName.text=item.name
      holder.tvEmail.text=item.email

        if (position % 2 == 0){
            holder.constrainLayout.setBackgroundColor(ContextCompat.getColor(holder.itemView.context,
            R.color.light_gray))
        }else{
            holder.constrainLayout.setBackgroundColor(ContextCompat.getColor(holder.itemView.context,
                R.color.teal_200))
        }
        holder.imgEdit.setOnClickListener {
            updateListener(item.id)
        }
        holder.imgDelete .setOnClickListener {
            deleteListener(item.id)
        }

    }

    override fun getItemCount(): Int {
        return items.size
    }

}