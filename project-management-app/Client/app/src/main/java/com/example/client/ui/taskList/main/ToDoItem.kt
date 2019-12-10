package com.example.client.ui.taskList.main

class ToDoItem {

    companion object Factory {
        fun create(): ToDoItem = ToDoItem()
    }

    var objectId: String? = null
    var itemText: String? = null
    var done: Boolean = false
    var status: String? = "ongoing"
    var deadline: String? = null
}