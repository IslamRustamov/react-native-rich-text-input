package com.richtextinput

import android.R.attr.htmlDescription
import android.graphics.Typeface
import android.text.Spannable
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.widget.EditText
import androidx.core.text.HtmlCompat
import androidx.core.text.toHtml
import androidx.core.widget.doOnTextChanged
import com.facebook.infer.annotation.Assertions
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.uimanager.SimpleViewManager
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.annotations.ReactProp
import com.facebook.react.uimanager.events.RCTEventEmitter


class RichTextInputViewManager : SimpleViewManager<EditText>() {
  var editText: EditText? = null

  override fun getName() = "RichTextInputView"

  override fun getExportedCustomBubblingEventTypeConstants(): Map<String, Any> {
    return mapOf(
      "topChange" to mapOf(
        "phasedRegistrationNames" to mapOf(
          "bubbled" to "onChange"
        )
      )
    )
  }

  override fun createViewInstance(reactContext: ThemedReactContext): EditText {
    editText = EditText(reactContext)

    editText!!.doOnTextChanged { text, start, before, count ->
      val event = Arguments.createMap().apply {
        putString("text", text.toString())
      }
      reactContext
        .getJSModule(RCTEventEmitter::class.java)
        .receiveEvent(editText!!.id, "topChange", event)
    }

    return editText as EditText
  }

  @ReactProp(name = "placeholder")
  fun setPlaceholder(view: EditText, placeholder: String) {
    view.hint = placeholder
  }

  @ReactMethod(isBlockingSynchronousMethod = true)
  fun getHTML(): String {
    if (editText != null) {
      return editText!!.text.toHtml()
    }

    return "ERROR: richTextView IS NOT INITIALIZED"
  }

  override fun receiveCommand(root: EditText, commandId: String?, args: ReadableArray?) {
    Assertions.assertNotNull(root)

    when (commandId) {
      "toggleBold" -> {
        toggleBold(root)
      }
      "toggleItalic" -> {
        toggleItalic(root)
      }
      "toggleStrike" -> {
        toggleStrike(root)
      }
      "toggleUnderline" -> {
        toggleUnderline(root)
      }
      "insertText" -> {
        if (args !== null) {
          val text = args.getString(0)

          insertText(root, text)
        }
      }
    }

    super.receiveCommand(root, commandId, args)
  }

  fun insertText(editText: EditText, text: String) {
    val spannedText = HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY)
    val trimmedText = spannedText.toString().trim()

    editText.text.insert(0, spannedText.subSequence(0, trimmedText.length))
  }

  fun toggleBold(editText: EditText) {
    val span = StyleSpan(Typeface.BOLD)
    toggleStyle(editText, span)
  }

  fun toggleItalic(editText: EditText) {
    val span = StyleSpan(Typeface.ITALIC)
    toggleStyle(editText, span)
  }

  fun toggleStrike(editText: EditText) {
    val span = StrikethroughSpan()
    toggleStyle(editText, span)
  }

  fun toggleUnderline(editText: EditText) {
    val span = UnderlineSpan()
    toggleStyle(editText, span)
  }

  private fun addSpan(editText: EditText, start: Int, end: Int, newSpan: StyleSpan) {
    editText.text.setSpan(newSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
  }

  private fun addSpan(editText: EditText, start: Int, end: Int, newSpan: StrikethroughSpan) {
    editText.text.setSpan(newSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
  }

  private fun addSpan(editText: EditText, start: Int, end: Int, newSpan: UnderlineSpan) {
    editText.text.setSpan(newSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
  }

  // TODO: below are 3 toggleStyle functions which are 99% repetitive
  // this is bad, what are the ways to refactor this?
  fun toggleStyle(editText: EditText, newSpan: StyleSpan) {
    val existingSpans = editText.text.getSpans(editText.selectionStart, editText.selectionEnd, StyleSpan::class.java)

    var didFindExistingSpans = false;

    if (existingSpans.isNotEmpty()) {
      for (span in existingSpans) {
        if (span.style == newSpan.style) {
          didFindExistingSpans = true

          val spanStart = editText.text.getSpanStart(span)
          val spanEnd = editText.text.getSpanEnd(span)

          editText.text.removeSpan(span)

          val isSpanInsideSelection = spanStart >= editText.selectionStart && spanEnd <= editText.selectionEnd

          if (isSpanInsideSelection) {
            continue
          }

          if (spanStart < editText.selectionStart) {
            addSpan(editText, spanStart, editText.selectionStart, newSpan)
          }

          if (spanEnd > editText.selectionEnd) {
            addSpan(editText, editText.selectionEnd, spanEnd, newSpan)
          }
        }
      }
    }

    // NOTE: it means we toggled existing spans (turned them off)
    if (didFindExistingSpans) {
      return;
    }

    addSpan(editText, editText.selectionStart, editText.selectionEnd, newSpan)
  }

  fun toggleStyle(editText: EditText, newSpan: StrikethroughSpan) {
    val existingSpans = editText.text.getSpans(editText.selectionStart, editText.selectionEnd, StrikethroughSpan::class.java)

    var didFindExistingSpans = false;

    if (existingSpans.isNotEmpty()) {
      for (span in existingSpans) {
        if (span.spanTypeId == newSpan.spanTypeId) {
          didFindExistingSpans = true

          val spanStart = editText.text.getSpanStart(span)
          val spanEnd = editText.text.getSpanEnd(span)

          editText.text.removeSpan(span)

          val isSpanInsideSelection = spanStart >= editText.selectionStart && spanEnd <= editText.selectionEnd

          if (isSpanInsideSelection) {
            continue
          }

          if (spanStart < editText.selectionStart) {
            addSpan(editText, spanStart, editText.selectionStart, newSpan)
          }

          if (spanEnd > editText.selectionEnd) {
            addSpan(editText, editText.selectionEnd, spanEnd, newSpan)
          }
        }
      }
    }

    // NOTE: it means we toggled existing spans (turned them off)
    if (didFindExistingSpans) {
      return;
    }

    addSpan(editText, editText.selectionStart, editText.selectionEnd, newSpan)
  }

  fun toggleStyle(editText: EditText, newSpan: UnderlineSpan) {
    val existingSpans = editText.text.getSpans(editText.selectionStart, editText.selectionEnd, UnderlineSpan::class.java)

    var didFindExistingSpans = false;

    if (existingSpans.isNotEmpty()) {
      for (span in existingSpans) {
        if (span.spanTypeId == newSpan.spanTypeId) {
          didFindExistingSpans = true

          val spanStart = editText.text.getSpanStart(span)
          val spanEnd = editText.text.getSpanEnd(span)

          editText.text.removeSpan(span)

          val isSpanInsideSelection = spanStart >= editText.selectionStart && spanEnd <= editText.selectionEnd

          if (isSpanInsideSelection) {
            continue
          }

          if (spanStart < editText.selectionStart) {
            addSpan(editText, spanStart, editText.selectionStart, newSpan)
          }

          if (spanEnd > editText.selectionEnd) {
            addSpan(editText, editText.selectionEnd, spanEnd, newSpan)
          }
        }
      }
    }

    // NOTE: it means we toggled existing spans (turned them off)
    if (didFindExistingSpans) {
      return;
    }

    addSpan(editText, editText.selectionStart, editText.selectionEnd, newSpan)
  }
}
