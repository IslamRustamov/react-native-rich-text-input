//
//  RichTextInputViewManager.swift
//  AwesomeProject
//
//  Created by Islam on 12.09.2024.
//

import Foundation
import UIKit
import React

@objc (RichTextInputViewManager)
public class RichTextInputViewManager: RCTViewManager, UITextViewDelegate  {
  var richTextView: RichTextInput? = nil

  public override static func requiresMainQueueSetup() -> Bool {
      return true
  }

  @objc
  public override func view() -> UITextView {
      let view = RichTextInput()

      view.font = .systemFont(ofSize: 16)

      view.text = view.placeholder
      view.textColor = UIColor.lightGray
    
      view.delegate = self
        
      richTextView = view
    
      return view
  }
    
  @objc
  func toggleUnderline(_ node: NSNumber) {
      executeBlock({ (richTextView) in
        self.toggleStyle(style: NSAttributedString.Key.underlineStyle)
      }, onNode: node)
  }
  
  @objc
  func setPlaceholder(_ node: NSNumber, placeholder: String) {
      // NOTE: this is a bad approach
      executeBlock({ (richTextView) in
        self.richTextView?.placeholder = placeholder
        self.richTextView?.text = placeholder
      }, onNode: node)
  }
  
  @objc
  func toggleStrike(_ node: NSNumber) {
      executeBlock({ (richTextView) in
        self.toggleStyle(style: NSAttributedString.Key.strikethroughStyle)
      }, onNode: node)
  }
  
  @objc
  func toggleBold(_ node: NSNumber) {
      executeBlock({ (richTextView) in
        self.toggleFont(trait: .traitBold)
      }, onNode: node)
  }
  
  @objc
  func toggleItalic(_ node: NSNumber) {
      executeBlock({ (richTextView) in
        self.toggleFont(trait: .traitItalic)
      }, onNode: node)
  }

  
  // Helpers
  func executeBlock(_ block: @escaping (UITextView) -> Void, onNode node: NSNumber) {
      self.bridge.uiManager.addUIBlock { (manager, viewRegistry) in
        guard let richText = self.richTextView else {
            return
        }
        block(richText)
      }
  }
  
  func toggleStyle(style: NSAttributedString.Key) {
    guard let richText = richTextView else {
        return
    }
    
    if richText.selectedRange.length == 0 {
      return
    }
    
    let isStyleEnabled = richText.textStorage.attribute(
      style,
      at: richText.selectedRange.location,
      longestEffectiveRange: nil,
      in: richText.selectedRange
    ) != nil
    
    if (isStyleEnabled) {
      richText.textStorage.removeAttribute(
        style,
        range: richText.selectedRange
      )
    } else {
      var attrs = richText.textStorage.attributes(
        at: richText.selectedRange.location,
        longestEffectiveRange: nil,
        in: richText.selectedRange
      )
      
      attrs[style] = NSUnderlineStyle.single.rawValue

      richText.textStorage.setAttributes(
        attrs,
        range: richText.selectedRange
      )
    }
  }
  
  
  func toggleFont(trait: UIFontDescriptor.SymbolicTraits.Element) {
    guard let richText = richTextView else {
        return
    }
    
    if richText.selectedRange.length == 0 {
      return
    }

    let fontAttribute = richText.textStorage.attribute(
      NSAttributedString.Key.font,
      at: richText.selectedRange.location,
      longestEffectiveRange: nil,
      in: richText.selectedRange
    )
    
    guard let font = fontAttribute as? UIFont else {
      addFont(newTrait: trait, existingTraits: [])
      return
    }
        
    let isFontAdded = font.fontDescriptor.symbolicTraits.contains(trait)

    if (isFontAdded) {
      addFont(newTrait: trait, existingTraits: font.fontDescriptor.symbolicTraits, deletion: true)
    } else {
      addFont(newTrait: trait, existingTraits: font.fontDescriptor.symbolicTraits)
    }
  }
  
  func addFont(newTrait: UIFontDescriptor.SymbolicTraits.Element, existingTraits: UIFontDescriptor.SymbolicTraits = [], deletion: Bool = false) {
    guard let richText = richTextView else {
        return
    }
    
    var attrs = richText.textStorage.attributes(
      at: richText.selectedRange.location,
      longestEffectiveRange: nil,
      in: richText.selectedRange
    )

    var traits: UIFontDescriptor.SymbolicTraits = existingTraits
    
    attrs[NSAttributedString.Key.font] = UIFont.systemFont(ofSize: 16)
  
    if deletion {
      traits.remove(newTrait)
    } else {
      traits.insert(newTrait)
    }

    attrs[NSAttributedString.Key.font] = UIFont.systemFont(ofSize: 16, symbolicTraits: traits)
    
    richText.textStorage.setAttributes(
      attrs,
      range: richText.selectedRange
    )
  }
    
  public func textViewDidBeginEditing(_ textView: UITextView) {
      if textView.textColor == UIColor.lightGray {
          textView.text = nil
          textView.textColor = UIColor.black
      }
  }
  
  public func textViewDidEndEditing(_ textView: UITextView) {
      if textView.text.isEmpty {
          textView.text = richTextView?.placeholder ?? "Placeholder"
          textView.textColor = UIColor.lightGray
      }
  }
}

extension UIFont {
  class func systemFont(ofSize fontSize: CGFloat, symbolicTraits: UIFontDescriptor.SymbolicTraits) -> UIFont? {
          return UIFont.systemFont(ofSize: fontSize).including(symbolicTraits: symbolicTraits)
      }

  func including(symbolicTraits: UIFontDescriptor.SymbolicTraits) -> UIFont? {
      var _symbolicTraits = self.fontDescriptor.symbolicTraits
      _symbolicTraits.update(with: symbolicTraits)
      return withOnly(symbolicTraits: _symbolicTraits)
  }

  func excluding(symbolicTraits: UIFontDescriptor.SymbolicTraits) -> UIFont? {
      var _symbolicTraits = self.fontDescriptor.symbolicTraits
      _symbolicTraits.remove(symbolicTraits)
      return withOnly(symbolicTraits: _symbolicTraits)
  }

  func withOnly(symbolicTraits: UIFontDescriptor.SymbolicTraits) -> UIFont? {
      guard let fontDescriptor = fontDescriptor.withSymbolicTraits(symbolicTraits) else { return nil }
      return .init(descriptor: fontDescriptor, size: pointSize)
  }
}
