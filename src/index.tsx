import {
  requireNativeComponent,
  UIManager,
  Platform,
  type ViewStyle,
} from 'react-native';

const LINKING_ERROR =
  `The package 'react-native-rich-text-input' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

type RichTextInputProps = {
  color: string;
  style: ViewStyle;
};

const ComponentName = 'RichTextInputView';

export const RichTextInputView =
  UIManager.getViewManagerConfig(ComponentName) != null
    ? requireNativeComponent<RichTextInputProps>(ComponentName)
    : () => {
        throw new Error(LINKING_ERROR);
      };
