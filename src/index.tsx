import {
  forwardRef,
  useRef,
  useEffect,
  useImperativeHandle,
  Component,
} from 'react';
import {
  requireNativeComponent,
  UIManager,
  Platform,
  type ViewProps,
  findNodeHandle,
  NativeModules,
} from 'react-native';

const LINKING_ERROR =
  `The package 'react-native-rich-text-input' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

export interface RichTextChangeEvent {
  nativeEvent: { text: string };
}

export interface RichTextProps extends Pick<ViewProps, 'style'> {
  ref: any;
  placeholder?: string;
  onChange?: (event: RichTextChangeEvent) => void;
}

export interface RichTextRef {
  toggleBold: Function;
  toggleItalic: Function;
  toggleStrike: Function;
  toggleUnderline: Function;
  getHTML: () => Promise<string>;
}

const ComponentName = 'RichTextInputView';

const RichTextInputView =
  UIManager.getViewManagerConfig(ComponentName) != null
    ? requireNativeComponent<RichTextProps>(ComponentName)
    : () => {
        throw new Error(LINKING_ERROR);
      };

const RichTextInput = forwardRef<RichTextRef, RichTextProps>(
  ({ placeholder, ...rest }, ref) => {
    const inputRef = useRef<RichTextRef>();

    useEffect(() => {
      // Temporary hack for iOS
      if (Platform.OS === 'ios') {
        UIManager.dispatchViewManagerCommand(
          findNodeHandle(inputRef.current as unknown as Component),
          'setPlaceholder',
          [placeholder]
        );
      }
    }, [placeholder]);

    useImperativeHandle(ref, () => {
      return {
        toggleBold: () => {
          UIManager.dispatchViewManagerCommand(
            findNodeHandle(inputRef.current as unknown as Component),
            'toggleBold',
            []
          );
        },
        toggleItalic: () => {
          UIManager.dispatchViewManagerCommand(
            findNodeHandle(inputRef.current as unknown as Component),
            'toggleItalic',
            []
          );
        },
        toggleStrike: () => {
          UIManager.dispatchViewManagerCommand(
            findNodeHandle(inputRef.current as unknown as Component),
            'toggleStrike',
            []
          );
        },
        toggleUnderline: () => {
          UIManager.dispatchViewManagerCommand(
            findNodeHandle(inputRef.current as unknown as Component),
            'toggleUnderline',
            []
          );
        },
        getHTML: () => {
          // NOTE: maybe I should address this issue... just maybe...
          if (Platform.OS === 'android') {
            return NativeModules.RichTextInputView.getHTML();
          }
          return NativeModules.RichTextInputViewManager.getHTML();
        },
      };
    }, []);

    return (
      <RichTextInputView ref={inputRef} placeholder={placeholder} {...rest} />
    );
  }
);

export default RichTextInput;
