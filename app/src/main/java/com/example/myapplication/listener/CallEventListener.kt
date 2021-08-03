package com.example.myapplication.listener

interface CallEventListener {
   public fun onGoingCallEvent(event: String)
   public fun onGoingCallInfo(time: String, number:String)
}