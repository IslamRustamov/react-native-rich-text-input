/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 */

import React, { useRef } from 'react';
import { Button, SafeAreaView, StyleSheet } from 'react-native';
import RichTextInput, {
  type RichTextChangeEvent,
  type RichTextRef,
} from 'react-native-rich-text-input';

function App(): React.JSX.Element {
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

  const handleGetHTML = async () => {
    console.log(ref.current?.getHTML());
  };

  const handleChange = (event: RichTextChangeEvent) => {
    console.log(event.nativeEvent.text);
  };

  const handleInsertText = () => {
    ref.current?.insertText('<p dir="ltr"><u>some</u> <b>text</b></p>');
  };

  return (
    <SafeAreaView style={styles.container}>
      <RichTextInput
        ref={ref}
        style={styles.input}
        placeholder="I HATE ASDASDSA"
        onChange={handleChange}
      />
      <Button title="Underline" onPress={handleUnderlinePress} />
      <Button title="Bold" onPress={handleBoldPress} />
      <Button title="Strike" onPress={handleStrikePress} />
      <Button title="Italic" onPress={handleItalicPress} />
      <Button title="Get HTML" onPress={handleGetHTML} />
      <Button title="Insert text" onPress={handleInsertText} />
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  input: {
    height: 200,
    width: '100%',
    fontSize: 15,
  },
});

export default App;
