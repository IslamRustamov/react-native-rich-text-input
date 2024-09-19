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
  func setPlaceholder(_ node: NSNumber, placeholder: String) {
      // NOTE: this is a bad approach
      executeBlock({ (richTextView) in
        self.richTextView?.placeholder = placeholder
        self.richTextView?.text = placeholder
      }, onNode: node)
  }
  
  @objc
  func toggleUnderline(_ node: NSNumber) {
      executeBlock({ (richTextView) in
        richTextView.toggleStyle(style: NSAttributedString.Key.underlineStyle)
      }, onNode: node)
  }
  
  @objc
  func toggleStrike(_ node: NSNumber) {
      executeBlock({ (richTextView) in
        richTextView.toggleStyle(style: NSAttributedString.Key.strikethroughStyle)
      }, onNode: node)
  }
  
  @objc
  func toggleBold(_ node: NSNumber) {
      executeBlock({ (richTextView) in
        richTextView.toggleFont(trait: .traitBold)
      }, onNode: node)
  }
  
  @objc
  func toggleItalic(_ node: NSNumber) {
      executeBlock({ (richTextView) in
        richTextView.toggleFont(trait: .traitItalic)
      }, onNode: node)
  }
  
  // Helpers
  func executeBlock(_ block: @escaping (RichTextInput) -> Void, onNode node: NSNumber) {
      self.bridge.uiManager.addUIBlock { (manager, viewRegistry) in
        guard let richText = self.richTextView else {
            return
        }
        block(richText)
      }
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
