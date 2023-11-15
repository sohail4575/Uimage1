package com.example.uimage1

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.widget.Adapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.ByteArrayOutputStream

class MainActivity : AppCompatActivity(),OnClick {

    lateinit var image:ImageView
    lateinit var title:EditText
    lateinit var description:EditText
    lateinit var submit:Button
    lateinit var dbHelper:DatabaseHelper
    lateinit var recyclerView: RecyclerView
    lateinit var imageUp: ImageView
    var bitmap:Bitmap?=null
    var bitmap1:Bitmap?=null
    lateinit var array:ArrayList<DataModel>
    lateinit var adapter:com.example.uimage1.Adapter

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        dbHelper = DatabaseHelper(this)

        image = findViewById(R.id.image1)
        title = findViewById(R.id.title)
        description = findViewById(R.id.description)
        submit = findViewById(R.id.submitBt)
        recyclerView = findViewById(R.id.recyclerView)
        array = arrayListOf()
        adapter = Adapter(array,this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        val db = dbHelper.readableDatabase

        val cursor = db.rawQuery("SELECT * FROM Student",null)
        if(cursor != null && cursor.moveToFirst()){
            do{
                val idGet = cursor.getString(0)
                val titleGet = cursor.getString(1)
                val description = cursor.getString(2)
                val imageGet = cursor.getBlob(3)
                val imageBitmap = BitmapFactory.decodeByteArray(imageGet,0,imageGet.size)

                array.add(DataModel(idGet,imageBitmap,titleGet,description))

            }while (cursor.moveToNext())
        }


        image.setOnClickListener {
            val intentForC = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intentForC, 124)
        }

        submit.setOnClickListener {

            bitmap?.let { dbHelper.insert(title.text.toString(),description.text.toString(), it.toByteArray()) }
            val intent = Intent(this,MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
            startActivity(intent)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == RESULT_OK){
            if(requestCode == 124){
                bitmap = data?.extras?.get("data") as Bitmap
                image.setImageBitmap(bitmap)
            }
            if(requestCode == 125){
                bitmap1 = data?.extras?.get("data") as Bitmap
                imageUp.setImageBitmap(bitmap1)
            }
        }
    }
    private fun Bitmap.toByteArray(): ByteArray {
        val stream = ByteArrayOutputStream()
        this.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }

    @SuppressLint("MissingInflatedId")
    override fun update(id: String?, p0: Int) {
        val layout = LayoutInflater.from(this).inflate(R.layout.dialog,null,false)
        imageUp = layout.findViewById<ImageView>(R.id.imageUp)
        val titleUp = layout.findViewById<EditText>(R.id.titleUp)
        val descriptionUp = layout.findViewById<EditText>(R.id.descriptionUp)
        val update = layout.findViewById<Button>(R.id.update)
        val builder = AlertDialog.Builder(this)
        val dialog = builder.create()
        dialog.setView(layout)
        dialog.show()

        imageUp.setImageBitmap(array[p0].image)
        titleUp.setText(array[p0].title)
        descriptionUp.setText(array[p0].descriptionGet)

        imageUp.setOnClickListener {
            val intentForC = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intentForC, 125)
        }

        update.setOnClickListener {
            val db = dbHelper.writableDatabase
            val value = ContentValues()
            value.put("Title",titleUp.text.toString())
            value.put("Description",descriptionUp.text.toString())
            if(bitmap1 != null){
                value.put("Image",bitmap1?.toByteArray())}
            else{
                value.put("Image",array[p0].image?.toByteArray())
            }

            db.update("Student",value,"Id=${array[p0].id}",null)
            db.close()
            dialog.dismiss()
            val intent = Intent(this,MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
            startActivity(intent)
        }
    }

    override fun delete(id: String?, p0: Int){
        AlertDialog.Builder(this)
            .setTitle("Delete Item")
            .setMessage("Are you sure you want to delete this item?")
            .setPositiveButton("Delete") { _, _ ->
                // Logic to delete the item from the list and database
                val idToDelete = id.toString()
                val positionToDelete = array.indexOfFirst { it.id ==idToDelete }
//                    val positionToDelete = 1/* specify the position of the item to delete */
                if (positionToDelete != -1 && positionToDelete < array.size) {
                    val idToDelete = array[positionToDelete].id
                    val db = dbHelper.writableDatabase
                    val deletedRows = db.delete("Student", "Id=?", arrayOf(idToDelete))
                    db.close()
                    if (deletedRows > 0) {
                        array.removeAt(positionToDelete)
                        adapter.notifyDataSetChanged()
                        Toast.makeText(this, "Item deleted successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Failed to delete the item", Toast.LENGTH_SHORT).show()
                    }
                }

                val intent = Intent(this,MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
                startActivity(intent)
            }

            .setNegativeButton("Cancel", null).show()
    }
}