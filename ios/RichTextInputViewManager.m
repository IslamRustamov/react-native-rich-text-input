//
//  RichTextInputViewmanager.m
//  AwesomeProject
//
//  Created by Islam on 12.09.2024.
//

#import <React/RCTViewManager.h>

@interface RCT_EXTERN_MODULE(RichTextInputViewManager, RCTViewManager)

RCT_EXTERN_METHOD(toggleUnderline:(nonnull NSNumber *)node)
RCT_EXTERN_METHOD(toggleBold:(nonnull NSNumber *)node)
RCT_EXTERN_METHOD(toggleStrike:(nonnull NSNumber *)node)
RCT_EXTERN_METHOD(toggleItalic:(nonnull NSNumber *)node)
RCT_EXTERN_METHOD(setPlaceholder:(nonnull NSNumber *)node placeholder:(nonnull NSString *)placeholder)
RCT_EXTERN_METHOD(focus:(nonnull NSNumber *)node)
RCT_EXTERN_METHOD(blur:(nonnull NSNumber *)node)

RCT_EXPORT_VIEW_PROPERTY(onChange, RCTBubblingEventBlock)

RCT_EXTERN__BLOCKING_SYNCHRONOUS_METHOD(getHTML);

@end
