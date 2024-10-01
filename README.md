# react-native-rich-text-input

Native component that allows you to add different rich text abilities

No webviews, no turbo-module-like tricks, just straight up native _UITextView_ for iOS and _EditText_ for Android

### NOTE This library is work in progress, check what was implemented below

![](https://github.com/IslamRustamov/react-native-rich-text-input/blob/main/public/iPhone.gif)

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

No specific installation needed, just install the lib and launch the project

## Usage

```js
import RichTextInput,  { type RichTextRef, type RichTextChangeEvent } from 'react-native-rich-text-input';

// ...

const ref = useRef<RichTextRef>(null);

const handleFocus = () => {
    ref.current?.focus();
};

const handleBlur = () => {
    ref.current?.blur();
};

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

const handleGetHTML = async () => {
    console.log(ref.current?.getHTML());
};

const handleChange = (event: RichTextChangeEvent) => {
    console.log(event.nativeEvent.text);
};

// NOTE: will automatically convert html to rich text
const handleInsertText = () => {
    ref.current?.insertText('<p dir="ltr"><u>some</u> <b>text</b></p>');
};

<RichTextInput
    ref={ref}
    placeholder="I am the angry placeholder"
    onChange={handleChange}
    style={{height: 200, width: "100%"}}
/>
```

# What was done

1. Setting placeholder (iOS, Android)
2. Select a portion of text and add different styles (bold, italic, underline, strikethrough) (iOS, Android)
3. Add native context menu for formatting (iOS)
4. Add onChange prop (iOS, Android)
5. Return text without markdown (iOS, Android)
6. Add method that returns rich text in HTML (iOS, Android)
7. Add method to set default text (Android)
8. Add focus and blur methods (iOS, Android)

# TODO

1. Ability to enable certain format and apply it without selecting a portion of text
2. Returning active formats for a selection
3. Add convertation to markdown (not sure about this for now)

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

Note from author: if you have requests regarding new props, bugs and stuff - please open up an issue and I will take a look at it

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
