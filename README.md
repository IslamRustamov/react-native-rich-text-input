# react-native-rich-text-input

Native component that allows you to add different rich text abilities

No webviews, no turbo-module-like tricks, just straight up native *UITextView* for iOS and *EditText* for Android

### NOTE This library is work in progress, check what was implemented below

## Installation

```sh
npm install react-native-rich-text-input
```
or 
```sh
yarn add react-native-rich-text-input
```

## Installation for iOS
```sh
cd ios && pod install
```

## Installation for Android
Android is not implemented yet

## Usage

```js
import RichTextInput,  { type RichTextRef } from 'react-native-rich-text-input';

// ...

const ref = useRef<RichTextRef>(null);

const handleUnderlinePress = () => {
ref.current?.toggleUnderline();
};

const handleBoldPress = () => {
ref.current?.toggleBold();
};

const handleStrikePress = () => {
ref.current?.toggleStrike();
};

const handleItalicPress = () => {
ref.current?.toggleItalic();
};

<RichTextInput ref={ref} placeholder="I am the angry placeholder" />;
```

# What was done
1. Setting placeholder (iOS)
2. Selecting a portion of text and adding different styles (bold, italic, underline, strikethrough) (iOS)

# TODO
1. Ability to enable certain format and apply it without selecting a portion of text
2. Returning active formats for a selection
3. Adding onChange prop
4. Returning text with markdown
5. Returning text without markdown

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
