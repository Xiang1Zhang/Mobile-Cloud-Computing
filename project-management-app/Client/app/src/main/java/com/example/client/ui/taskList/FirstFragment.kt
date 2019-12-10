package com.example.client.ui.taskList

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import com.example.client.R
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.example.client.ui.taskList.main.CreateTaskActivity
import com.example.client.ui.taskList.main.ToDoItem
import com.example.client.ui.taskList.main.ToDoItemAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*

/**
 * A simple [Fragment] subclass.
 */
class FirstFragment(private val projectId : String) : Fragment() {

    var toDoItemList: MutableList<ToDoItem>? = null
    lateinit var adapter: ToDoItemAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_first, container, false)

        val fab = view.findViewById<FloatingActionButton>(R.id.fab1)
        val listViewItems = view.findViewById<ListView>(R.id.items_list1)

        mDatabase = FirebaseDatabase.getInstance().reference
        toDoItemList = mutableListOf<ToDoItem>()
        adapter = ToDoItemAdapter(requireContext(), toDoItemList!!)
        listViewItems?.setAdapter(adapter)
        getDataFromFiretore()
        mDatabase.orderByKey().addListenerForSingleValueEvent(itemListener)

        //Adding click listener for FAB
        fab?.setOnClickListener { view ->
            //Show create task here to add new Item
            val intent = Intent(activity, CreateTaskActivity::class.java)
            intent.putExtra(EXTRA_PROJECT_ID, projectId)
            activity?.startActivityForResult(intent, CREATE_TASK_CODE)
        }

        return view
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun getDataFromFiretore() {

        db.collection("tasks")
            .whereEqualTo("projectID", projectId)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d(TAG, "${document.id} => ${document.data}")
                    val todoItem = ToDoItem.create()

                    //key will return Firebase ID
                    todoItem.objectId = document.getString("id")
                    todoItem.status = document.getString("status")
                    todoItem.itemText = document.getString("description")
                    todoItem.deadline = document.getString("deadline")
                    toDoItemList!!.add(todoItem)
                    adapter.notifyDataSetChanged()
                    Log.d(TAG, "DocumentSnapshot data: $toDoItemList")
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }

    }

   private var itemListener: ValueEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            // Get Post object and use the values to update the UI
            //addDataToList(dataSnapshot)
        }
        override fun onCancelled(databaseError: DatabaseError) {
            // Getting Item failed, log a message
            Log.w("MainActivity", "loadItem:onCancelled", databaseError.toException())
        }
    }


    /*
    private fun addDataToList(dataSnapshot: DataSnapshot) {
        val items = dataSnapshot.children.iterator()
        //Check if current database contains any collection
        if (items.hasNext()) {
            val toDoListindex = items.next()
            val itemsIterator = toDoListindex.children.iterator()

            //check if the collection has any to do items or not
            while (itemsIterator.hasNext()) {
                //get current item
                val currentItem = itemsIterator.next()
                val todoItem = ToDoItem.create()
                //get current data in a map
                val map = currentItem.getValue() as HashMap<*, *>
                //key will return Firebase ID
                todoItem.objectId = currentItem.key
                todoItem.done = map.get("done") as Boolean?
                todoItem.itemText = map.get("itemText") as String?
                todoItem.deadline = map.get("deadline") as String?
                toDoItemList!!.add(todoItem)
                Log.d(TAG, "DocumentSnapshot data: $toDoItemList")
            }
        }
        //alert adapter that has changed
        adapter.notifyDataSetChanged()
    }*/

}