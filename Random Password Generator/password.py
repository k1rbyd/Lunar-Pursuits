import tkinter as tk
from tkinter import messagebox
import random
import string
import pyperclip

def generate_password():
    letter_limit = int(entry_letter_limit.get())
    lowercase_enabled = chk_var_lower.get()
    uppercase_enabled = chk_var_upper.get()
    special_characters_enabled = chk_var_special.get()

    character_set = ""
    if lowercase_enabled:
        character_set += string.ascii_lowercase
    if uppercase_enabled:
        character_set += string.ascii_uppercase
    if special_characters_enabled:
        character_set += "!@#$%^&*()-_=+{[]}"

    if not character_set:
        messagebox.showerror("Error", "You must select at least one type of character.")
        return

    password = ''.join(random.choice(character_set) for _ in range(letter_limit))
    lbl_generated_password.config(text="The password is: " + password)

    btn_copy_password.config(state=tk.NORMAL)

def copy_password():
    password = lbl_generated_password.cget("text")[16:]  # Extract password from label text
    pyperclip.copy(password)
    messagebox.showinfo("Password Copied", "Password copied to clipboard!")

# Create the tkinter window
window = tk.Tk()
window.title("Password Generator")

# Letter Limit Entry
lbl_letter_limit = tk.Label(window, text="Enter the letter limit: ")
lbl_letter_limit.pack()
entry_letter_limit = tk.Entry(window)
entry_letter_limit.pack()

# Checkboxes for character types
chk_var_lower = tk.IntVar()
chk_var_upper = tk.IntVar()
chk_var_special = tk.IntVar()

chk_lower = tk.Checkbutton(window, text="Lowercase letters", variable=chk_var_lower)
chk_upper = tk.Checkbutton(window, text="Uppercase letters", variable=chk_var_upper)
chk_special = tk.Checkbutton(window, text="Special characters", variable=chk_var_special)

chk_lower.pack()
chk_upper.pack()
chk_special.pack()

# Generate Password Button
btn_generate = tk.Button(window, text="Generate Password", command=generate_password)
btn_generate.pack()

# Generated Password Label
lbl_generated_password = tk.Label(window, text="")
lbl_generated_password.pack()

# Copy Password Button
btn_copy_password = tk.Button(window, text="Copy Password", state=tk.DISABLED, command=copy_password)
btn_copy_password.pack()

window.mainloop()
