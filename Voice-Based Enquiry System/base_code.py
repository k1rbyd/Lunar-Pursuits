import speech_recognition as sr
from gtts import gTTS
import os

# Initialize the speech recognition engine
recognizer = sr.Recognizer()

# Function to recognize speech
def recognize_speech():
    with sr.Microphone() as source:
        print("Listening for your question...")
        recognizer.adjust_for_ambient_noise(source)  # Adjust for noise
        audio = recognizer.listen(source)

    try:
        # Recognize speech using Google Speech Recognition
        query = recognizer.recognize_google(audio)
        return query
    except sr.UnknownValueError:
        return "Sorry, I didn't catch that."
    except sr.RequestError as e:
        return f"Sorry, there was an error with the speech recognition service: {e}"

# Function to respond using text-to-speech
def respond_with_tts(response_text):
    tts = gTTS(text=response_text, lang='en')
    tts.save("response.mp3")
    os.system("mpg321 response.mp3")  # Play the response using mpg321

# Main loop
while True:
    query = recognize_speech()
    print("You asked:", query)

    # Simple logic to respond to specific queries
    if "bus schedule" in query:
        response = "The next bus to downtown leaves at 9:30 AM."
    elif "train timetable" in query:
        response = "The train to the airport departs every hour."
    elif "exit" in query:
        break
    else:
        response = "I'm sorry, I don't have information about that."

    print("Response:", response)

    # Replace this with a more sophisticated logic to respond to a wider range of queries
    respond_with_tts(response)

