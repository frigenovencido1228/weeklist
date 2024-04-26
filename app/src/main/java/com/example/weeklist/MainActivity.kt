package com.example.weeklist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.example.weeklist.classes.Item
import com.example.weeklist.classes.ItemsAdapter
import com.example.weeklist.classes.OnItemClick
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.w3c.dom.Text
import java.sql.Timestamp

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

//    private fun addItem() {
//        val name = etName?.text.toString()
//        val price = etPrice?.text.toString()
//
//        if (name.isEmpty() || price.isEmpty()) {
//            Toast.makeText(applicationContext, "Enter name and price", Toast.LENGTH_LONG).show()
//            return
//        }
//
//        val currentTimeMillis = System.currentTimeMillis()
//        val timeStamp = Timestamp(currentTimeMillis)
//        val id = myDb.push().key!!
//        val items = Item(name, id, timeStamp.toString(), price)
//
//        myDb.child(id).setValue(items).addOnCompleteListener {
//            Toast.makeText(applicationContext, "Item added", Toast.LENGTH_SHORT).show()
//        }.addOnFailureListener {
//            Toast.makeText(applicationContext, "Item failed", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    private fun getData() {
//        myDb.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                itemsList.clear()
//                if (snapshot.exists()) {
//                    for (itemSnap in snapshot.children) {
//                        val items = itemSnap.getValue(Item::class.java)
//                        itemsList.add(items!!)
//                    }
//                    rvItems.layoutManager = LinearLayoutManager(this@MainActivity)
//                    rvItems.setHasFixedSize(true)
//                    rvItems.adapter = ItemsAdapter(itemsList,this@MainActivity)
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                Toast.makeText(applicationContext, "Error $error", Toast.LENGTH_SHORT).show()
//            }
//
//        })
//    }
//
//    override fun onClick(item: Item) {
//        TODO("Not yet implemented")
//    }
}