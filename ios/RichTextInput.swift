//
//  RichTextInput.swift
//  AwesomeProject
//
//  Created by Islam on 13.09.2024.
//

import Foundation

// NOTE: This class is needed to avoid props assigning issue
// Define all props here
// UPDATE: okay, now this class is also needed for helping functions
class RichTextInput: UITextView, UIEditMenuInteractionDelegate {
  @objc var placeholder: String = "Placeholder"
  
  override func editMenu(for textRange: UITextRange, suggestedActions: [UIMenuElement]) -> UIMenu? {
    var option: UIMenu.Options? = nil
    
    if #available(iOS 15.0, *) {
      option = .singleSelection
    } else {
      option = .displayInline
    }

    let customMenu = UIMenu(title: "Format", options: option ?? .displayInline, children: [
      UIAction(title: "Bold") { _ in
        self.toggleFont(trait: .traitBold)
      },
      UIAction(title: "Italic") { _ in
        self.toggleFont(trait: .traitItalic)
      },
      UIAction(title: "Strikethrough") { _ in
        self.toggleStyle(style: NSAttributedString.Key.strikethroughStyle)
      },
      UIAction(title: "Underline") { _ in
        self.toggleStyle(style: NSAttributedString.Key.underlineStyle)
      }
    ])

    var actions = suggestedActions
    actions.insert(customMenu, at: 0)
    return UIMenu(children: actions)
  }
  
  // TODO: styles are getting lost if you enable/reenable them, bug, needs fixing
  func toggleStyle(style: NSAttributedString.Key) {
    if selectedRange.length == 0 {
      return
    }
    
    let attribute = textStorage.attribute(
      style,
      at: selectedRange.location,
      longestEffectiveRange: nil,
      in: selectedRange
    )
    
    let isStyleEnabled = attribute != nil
    
    if (isStyleEnabled) {
      textStorage.removeAttribute(
        style,
        range: selectedRange
      )
    } else {
      textStorage.addAttribute(style, value: NSUnderlineStyle.single.rawValue, range: selectedRange)
    }
  }
  
  func toggleFont(trait: UIFontDescriptor.SymbolicTraits.Element) {
    if selectedRange.length == 0 {
      return
    }

    let fontAttribute = textStorage.attribute(
      NSAttributedString.Key.font,
      at: selectedRange.location,
      longestEffectiveRange: nil,
      in: selectedRange
    )
    
    guard let font = fontAttribute as? UIFont else {
      textStorage.addAttribute(NSAttributedString.Key.font, value: UIFont.systemFont(ofSize: 16, symbolicTraits: trait), range: selectedRange)
      return
    }
        
    let traits = font.fontDescriptor.symbolicTraits
    let isFontAdded = traits.contains(trait)
        
    textStorage.enumerateAttribute(.font, in: selectedRange, options: .longestEffectiveRangeNotRequired) { (value, range, stop) in
        if let font = value as? UIFont {
          textStorage.removeAttribute(.font, range: range)
          
          var traits: UIFontDescriptor.SymbolicTraits = UIFontDescriptor.SymbolicTraits()
          
          traits.insert(font.fontDescriptor.symbolicTraits)
          
          if isFontAdded {
              traits.remove(trait)
          } else {
              traits.insert(trait)
          }
          
          textStorage.addAttribute(
            .font,
            value: UIFont(descriptor: font.fontDescriptor.withSymbolicTraits(traits)!, size: font.pointSize),
            range: range
          )
        }
    }
  }
}
