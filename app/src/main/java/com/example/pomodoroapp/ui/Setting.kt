package com.example.pomodoroapp.ui

open class Setting (
    val name: String,
    val description: String = ""
){}

class BoolSetting(name:String, description: String, default: Boolean) :
    Setting(name = name, description = description)
{

}