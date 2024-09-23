package com.richtextinput

import android.widget.EditText
import com.facebook.react.uimanager.SimpleViewManager
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.annotations.ReactProp

class RichTextInputViewManager : SimpleViewManager<EditText>() {
  override fun getName() = "RichTextInputView"

  override fun createViewInstance(reactContext: ThemedReactContext): EditText {
    return EditText(reactContext)
  }

  @ReactProp(name = "placeholder")
  fun setPlaceholder(view: EditText, placeholder: String) {
    view.hint = placeholder
  }
}
