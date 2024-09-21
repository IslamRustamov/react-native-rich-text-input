# react-native-rich-text-input

Native component that allows you to add different rich text abilities

No webviews, no turbo-module-like tricks, just straight up native _UITextView_ for iOS and _EditText_ for Android

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
import RichTextInput,  { type RichTextRef, type RichTextChangeEvent } from 'react-native-rich-text-input';

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

const handleChange = (event: RichTextChangeEvent) => {
    console.log(event.nativeEvent.text);
};

<RichTextInput
    ref={ref}
    placeholder="I am the angry placeholder"
    onChange={handleChange}
    style={{height: 200, width: "100%"}}
/>
```

# What was done

1. Setting placeholder (iOS)
2. Select a portion of text and add different styles (bold, italic, underline, strikethrough) (iOS)
3. Add native context menu for formatting (iOS)
4. Add onChange prop (iOS)
5. Return text without markdown

# TODO

1. Ability to enable certain format and apply it without selecting a portion of text
2. Returning active formats for a selection
3. Returning text with markdown

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
