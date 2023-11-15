package com.example.uimage1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView

class Adapter(private val dataArrayList: ArrayList<DataModel>,val onClick: OnClick):
    RecyclerView.Adapter<Adapter.MyViewHolder>() {
    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val titleShow: TextView = itemView.findViewById(R.id.titleShow)
        val descriptionShow: TextView = itemView.findViewById(R.id.descriptionShow)
        val showImage: ImageView = itemView.findViewById(R.id.imageShow)
        val updatData: ImageView = itemView.findViewById(R.id.updateData)
        val delete: ImageView = itemView.findViewById(R.id.delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val row = LayoutInflater.from(parent.context).inflate(R.layout.data_item,null,false)
        return MyViewHolder(row)
    }

    override fun getItemCount(): Int {
        return dataArrayList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.titleShow.text = dataArrayList[position].title.toString()
        holder.descriptionShow.text = dataArrayList[position].descriptionGet.toString()
        holder.showImage.setImageBitmap(dataArrayList[position].image)

        holder.updatData.setOnClickListener {
            onClick.update(dataArrayList[position].id,position)
        }
        holder.delete.setOnClickListener {
           onClick.delete(dataArrayList[position].id,position)
        }
    }
}