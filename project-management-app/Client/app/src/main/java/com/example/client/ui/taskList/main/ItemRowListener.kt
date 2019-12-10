package com.example.client.ui.taskList.main



interface ItemRowListener {

    fun modifyItemState(itemObjectId: String, isDone: Boolean)
    fun onItemDelete(itemObjectId: String)

}

