package com.peng

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

class PriceInputMax(val input : EditText) {
    fun listen() {
        input.addTextChangedListener(mDateEntryWatcher)
    }
    private val mDateEntryWatcher = object : TextWatcher {
        var edited = false
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            if (edited) {
                edited = false
                return
            }
            val formattedString = getEditText().replace(Regex("\\D"), "")
                .let { Utils().formatCurrency(it) }.dropLast(3)
            edited = true
            input.setText(formattedString)
            input.setSelection(input.text.length)
        }
        private fun getEditText() : String {
            return if (input.text.toString().isEmpty()) {
                "0"
            } else {
                input.text.toString()
            }
        }

        override fun afterTextChanged(s: Editable) {}
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
    }
}