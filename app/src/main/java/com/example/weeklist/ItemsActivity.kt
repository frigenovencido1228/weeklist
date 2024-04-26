package com.example.weeklist

import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weeklist.classes.Item
import com.example.weeklist.classes.ItemsAdapter
import com.example.weeklist.classes.OnItemClick
import com.google.android.material.button.MaterialButton
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ItemsActivity : AppCompatActivity(), OnItemClick {
    lateinit var fabAdd: FloatingActionButton
    lateinit var rvItems: RecyclerView
    lateinit var database: FirebaseDatabase
    lateinit var itemsDb: DatabaseReference
    lateinit var itemsList: ArrayList<Item>
    lateinit var fabFilter: FloatingActionButton
    lateinit var tvDate: TextView
    lateinit var tvTotal: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_items)


        rvItems = findViewById(R.id.rvItems)
        tvDate = findViewById(R.id.tvDate)
        tvTotal = findViewById(R.id.tvTotal)

        fabFilter = findViewById(R.id.fabFilter)
        fabAdd = findViewById(R.id.fabAdd)
        database = Firebase.database
        itemsDb = database.getReference("items")
        itemsList = arrayListOf()

        fabAdd.setOnClickListener(View.OnClickListener {
            showDialog()
        })
        fabFilter.setOnClickListener(View.OnClickListener {
            showFilterDialog()
        })

        //get items from current week
        getAllItems(getStartDate(0), getEndDate(6))
    }

    private fun showFilterDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_filter)
        dialog.show()
        setDialogAttributes(dialog)

        var strStartDate: String = ""
        var strEndDate: String = ""

        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.TAIWAN)
        val etPriceStart = dialog.findViewById<EditText>(R.id.etPriceStart)
        val etPriceEnd = dialog.findViewById<EditText>(R.id.etPriceEnd)

        val btnClearAll = dialog.findViewById<MaterialButton>(R.id.btnClear)
        btnClearAll.setOnClickListener(View.OnClickListener {
            getAllItems(getStartDate(0), getEndDate(6))
            Toast.makeText(applicationContext, "Filters cleared.", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        })

        val etDateStart = dialog.findViewById<EditText>(R.id.etDateStart)
        etDateStart.setOnClickListener(View.OnClickListener {

            val datePicker =
                MaterialDatePicker.Builder.datePicker().setTitleText("Select a date").build()

            datePicker.addOnPositiveButtonClickListener {

                val startDate = datePicker.selection
                strStartDate = simpleDateFormat.format(startDate)

                etDateStart.setText(strStartDate)
            }

            datePicker.show(supportFragmentManager, "DATE_PICKER_START")
        })

        val etDateEnd = dialog.findViewById<EditText>(R.id.etDateEnd)
        etDateEnd.setOnClickListener(View.OnClickListener {
            val datePicker =
                MaterialDatePicker.Builder.datePicker().setTitleText("Select a date").build()

            datePicker.addOnPositiveButtonClickListener {
                val endDate = datePicker.selection

                strEndDate = simpleDateFormat.format(endDate)

                etDateEnd.setText(strEndDate)
            }

            datePicker.show(supportFragmentManager, "DATE_PICKER_END")
        })

        val btnFilter = dialog.findViewById<MaterialButton>(R.id.btnFilter)
        btnFilter.setOnClickListener(View.OnClickListener {

            val doubleStart = etPriceStart.text.toString()
            val doubleEnd = etPriceEnd.text.toString()

            if (doubleStart.isEmpty() && doubleEnd.isEmpty()) {
                getAllItems(strStartDate, strEndDate)
            } else {
                getAllItems(
                    getStartDate(0),
                    getEndDate(6),
                    doubleStart.toDouble(),
                    doubleEnd.toDouble()
                )
            }
            Toast.makeText(applicationContext, "Filters applied.", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        })
    }

    private fun setDialogAttributes(dialog: Dialog) {
        //set dialog attributes
        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT
        )

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        dialog.window?.setGravity(Gravity.BOTTOM)
    }

    private fun showDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_item)
        dialog.show()

        setDialogAttributes(dialog)

        val etName = dialog.findViewById<EditText>(R.id.etName)
        val etPrice = dialog.findViewById<EditText>(R.id.etPrice)

        etName.requestFocus()

        val btnCancel = dialog.findViewById<MaterialButton>(R.id.btnCancel)
        btnCancel.setOnClickListener(View.OnClickListener { dialog.dismiss() })

        val btnAdd = dialog.findViewById<MaterialButton>(R.id.btnConfirm)
        btnAdd.text = "Add"
        btnAdd.setOnClickListener(View.OnClickListener {
            val name = etName.text.toString().trim()
            val price = etPrice.text.toString().trim()

            if (name.isEmpty() && price.isEmpty()) {
                Toast.makeText(applicationContext, "Please fill fields", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }

            addToDb(name, price, dialog)

        })

    }

    private fun getStartDate(day: Int): String {

        val c = Calendar.getInstance()

        c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
        c.add(Calendar.DAY_OF_MONTH, day)
        c.set(Calendar.HOUR_OF_DAY, 0)
        c.set(Calendar.MINUTE, 0)
        c.set(Calendar.SECOND, 0)
        c.set(Calendar.MILLISECOND, 0)

        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.TAIWAN)

        return simpleDateFormat.format(c.time)
    }

    private fun getEndDate(day: Int): String {
        val c = Calendar.getInstance(Locale.TAIWAN)
        c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
        c.add(Calendar.DAY_OF_MONTH, day)
        c.set(Calendar.HOUR_OF_DAY, 23)
        c.set(Calendar.MINUTE, 59)
        c.set(Calendar.SECOND, 59)
        c.set(Calendar.MILLISECOND, 999)

        //adding 1 day for filtering

        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.TAIWAN)

        return simpleDateFormat.format(c.time)
    }

    private fun addToDb(name: String, price: String, dialog: Dialog) {

        //set date
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.TAIWAN)
        val time = Calendar.getInstance().time
        val timeStamp = simpleDateFormat.format(time)

        //create id for item
        val id = itemsDb.push().key!!
        val items = Item(name, id, timeStamp.toString(), price)

        itemsDb.child(id).setValue(items).addOnCompleteListener {
            Toast.makeText(applicationContext, "Item added", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }.addOnFailureListener {
            Toast.makeText(
                applicationContext,
                "Adding failed. Error: ${it.message}",
                Toast.LENGTH_SHORT
            ).show()
            dialog.dismiss()
        }
    }

    private fun getAllItems(
        start: String,
        end: String,
        priceStart: Double? = 0.0,
        priceEnd: Double? = 0.0
    ) {


        val query = itemsDb.orderByChild("createdAt").startAt(start).endAt(end)
        //get items and order by date
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                itemsList.clear()
                var total: Double = 0.00
                if (snapshot.exists()) {
                    for (itemSnap in snapshot.children) {
                        val items = itemSnap.getValue(Item::class.java)

                        //multiply to 100 then divide to avoid problems with double
                        total += items?.price.toString().toDouble() * 100
                        itemsList.add(items!!)
                    }
                    //sort by descending to show latest items first
                    itemsList.sortByDescending { it.createdAt }

                    if (priceStart != 0.0 || priceEnd != 0.0) {

                        itemsList =
                            itemsList.filter {
                                it.price?.toDouble()!! > priceStart!! && it.price?.toDouble()!! <= priceEnd!!
                            } as ArrayList<Item>
                    }

                    rvItems.layoutManager = LinearLayoutManager(this@ItemsActivity)
                    rvItems.setHasFixedSize(true)

                    rvItems.adapter = ItemsAdapter(itemsList, this@ItemsActivity)
                    tvDate.text = "$start to $end"
                    tvTotal.text = "Total: ${total / 100}"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, "Error $error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onClick(item: Item) {
        showItemDetails(item)
    }

    private fun showItemDetails(item: Item) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_item)
        dialog.show()
        setDialogAttributes(dialog)

        val tvLabel = dialog.findViewById<TextView>(R.id.tvLabel)
        tvLabel.text = "Item Details"
        val etName = dialog.findViewById<EditText>(R.id.etName)
        etName.setText(item.name?.uppercase())
        val etPrice = dialog.findViewById<EditText>(R.id.etPrice)
        etPrice.setText(item.price)

        val btnUpdate = dialog.findViewById<MaterialButton>(R.id.btnConfirm)
        btnUpdate.text = "Update"

        btnUpdate.setOnClickListener {
            val name = etName.text.toString().trim()
            val price = etPrice.text.toString().trim()

            if (name.isEmpty() || price.isEmpty()) {
                Toast.makeText(applicationContext, "Fill up all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val item = Item(name, item.id, item.createdAt, price)
            itemsDb.child(item.id.toString()).setValue(item).addOnCompleteListener {
                Toast.makeText(applicationContext, "Item Updated.", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }.addOnFailureListener {
                Toast.makeText(
                    applicationContext,
                    "Updating failed. Error: ${it.message}",
                    Toast.LENGTH_SHORT
                ).show()
                dialog.dismiss()
            }

        }

        val btnCancel = dialog.findViewById<MaterialButton>(R.id.btnCancel)
        btnCancel.setOnClickListener {
            dialog.dismiss()
        }
    }

    override fun onItemLongClick(item: Item) {
        deleteItem(item)
    }

    private fun deleteItem(item: Item) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_item)
        dialog.show()
        setDialogAttributes(dialog)

        val tvLabel = dialog.findViewById<TextView>(R.id.tvLabel)
        tvLabel.text = "Delete item?"

        val etName = dialog.findViewById<EditText>(R.id.etName)
        etName.setText(item.name?.uppercase())

        val etPrice = dialog.findViewById<EditText>(R.id.etPrice)
        etPrice.setText(item.price)

        val btnDelete = dialog.findViewById<MaterialButton>(R.id.btnConfirm)
        btnDelete.text = "Delete"

        btnDelete.setBackgroundColor(resources.getColor(R.color.red))

        btnDelete.setOnClickListener(View.OnClickListener {
            itemsDb.child(item.id.toString()).removeValue().addOnCompleteListener {
                Toast.makeText(applicationContext, "Item deleted.", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }.addOnFailureListener {
                Toast.makeText(
                    applicationContext,
                    "Deleting failed. Error: ${it.message}",
                    Toast.LENGTH_SHORT
                ).show()
                dialog.dismiss()
            }
        })
        val btnCancel = dialog.findViewById<MaterialButton>(R.id.btnCancel)
        btnCancel.setOnClickListener(View.OnClickListener {
            dialog.dismiss()
        })
    }
}