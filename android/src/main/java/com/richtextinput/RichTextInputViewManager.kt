package com.richtextinput

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Typeface
import android.text.Spannable
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import android.text.style.URLSpan
import android.text.style.UnderlineSpan
import android.view.ActionMode
import android.view.ActionMode.Callback
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.text.HtmlCompat
import androidx.core.text.toHtml
import androidx.core.widget.doOnTextChanged
import com.facebook.infer.annotation.Assertions
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.WritableArray
import com.facebook.react.uimanager.SimpleViewManager
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.annotations.ReactProp
import com.facebook.react.uimanager.events.RCTEventEmitter

class RichTextInputViewManager : SimpleViewManager<EditText>() {
  private var editText: EditText? = null

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

    editText!!.customSelectionActionModeCallback = object : Callback {
      override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
        return true
      }

      override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
        menu.clear()

        menu.add(0, 1, 0, "Copy")
        menu.add(0, 2, 0, "Bold")
        menu.add(0, 3, 0, "Italic")
        menu.add(0, 4, 0, "Strikethrough")
        menu.add(0, 4, 0, "Underline")

        return true
      }

      override fun onActionItemClicked(mode: ActionMode, menu: MenuItem): Boolean {
        // TODO: you can avoid code duplication probably
        when (menu.title) {
          "Bold" -> {
            toggleBold(editText!!)
          }
          "Italic" -> {
            toggleItalic(editText!!)
          }
          "Underline" -> {
            toggleUnderline(editText!!)
          }
          "Strikethrough" -> {
            toggleStrike(editText!!)
          }
          "Copy" -> {
            copy(editText!!)
          }
        }

        mode.finish()

        return true
      }

      override fun onDestroyActionMode(mode: ActionMode) {}
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

    return INPUT_NOT_INITIALIZED_ERROR
  }

  @ReactMethod(isBlockingSynchronousMethod = true)
  fun getSelection(): WritableArray {
    val array = Arguments.createArray()

    if (editText != null) {
      array.pushInt(editText!!.selectionStart)
      array.pushInt(editText!!.selectionEnd)

      return array
    }

    return array
  }

  @ReactMethod(isBlockingSynchronousMethod = true)
  fun getLink(position: Int): String {
    if (editText != null) {
      val spans = editText!!.text.getSpans(position, position, URLSpan::class.java)

      if (spans.size > 0) {
        return spans[0].url
      }

      return ""
    }

    return INPUT_NOT_INITIALIZED_ERROR
  }

  override fun receiveCommand(root: EditText, commandId: String?, args: ReadableArray?) {
    Assertions.assertNotNull(root)

    when (commandId) {
      "focus" -> {
        focus(root)
      }
      "blur" -> {
        blur(root)
      }
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
      "embedLink" -> {
        if (args !== null) {
          val start = args.getInt(0)
          val end = args.getInt(1)
          val href = args.getString(2)

          if (start == end) {
            return
          }

          embedLink(root, start, end, href)
        }
      }
      "removeLink" -> {
        if (args !== null) {
          val start = args.getInt(0)

          removeLink(root, start)
        }
      }
    }

    super.receiveCommand(root, commandId, args)
  }

  fun copy(editText: EditText) {
    val clipboard = editText.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    val clip = ClipData.newPlainText("Copied text", editText.text.subSequence(editText.selectionStart, editText.selectionEnd))

    clipboard.setPrimaryClip(clip)
  }

  fun embedLink(editText: EditText, start: Int, end: Int, href: String) {
    if (start == end) {
      return
    }

    editText.text.setSpan(URLSpan(href), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
  }

  fun removeLink(editText: EditText, start: Int) {
    val spans = editText.text.getSpans(start, start, URLSpan::class.java)

    if (spans.isNotEmpty()) {
      for (span in spans) {
        editText.text.removeSpan(span)
      }
    }
  }

  fun focus(editText: EditText) {
    editText.requestFocus()

    val inputMethodManager = editText.context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.showSoftInput(editText, 0)
  }

  fun blur(editText: EditText) {
    editText.clearFocus()

    val inputMethodManager = editText.context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(editText.windowToken, 0)
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

  companion object {
    const val INPUT_NOT_INITIALIZED_ERROR = "ERROR: richTextView IS NOT INITIALIZED"
    // NOTE: maybe I can rename this so that in iOS and Android we had same module names?
    const val NAME = "RichTextInputView"
  }
}
