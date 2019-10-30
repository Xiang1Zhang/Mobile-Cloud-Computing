package com.example.jsonparsingurlkotlin

class PlayersModel {

    var name: String? = null
    var imgURL: String? = null

    fun getNames(): String {
        return name.toString()
    }

    fun setNames(name: String) {
        this.name = name
    }

    fun getURL(): String {
        return imgURL.toString()
    }

    fun setURL(name: String) {
        this.imgURL = name
    }



}