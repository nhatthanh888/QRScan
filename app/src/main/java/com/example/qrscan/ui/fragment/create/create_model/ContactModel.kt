package com.example.qrscan.ui.fragment.create.create_model

import android.provider.ContactsContract.CommonDataKinds.Nickname

 class ContactModel(
  var name: String,
  var phone: String,
  var email: String,
  var url: String,
  var note: String,
  var  birthday: String,
  var nickname: String,
  var address: String
) {
}