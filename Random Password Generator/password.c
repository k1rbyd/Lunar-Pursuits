#include <stdio.h>
#include <stdlib.h>
#include <time.h>

int main() 
{
    int letter_limit;
    printf("Enter the letter limit: ");
    scanf("%d", &letter_limit);

    char password[letter_limit + 1];

    srand(time(NULL));

    char lowercase_characters[] = "abcdefghijklmnopqrstuvwxyz";
    char uppercase_characters[] = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    char special_characters[] = "!@#$%^&*()-_=+{[]}";

    int lowercase_enabled = 0;
    int uppercase_enabled = 0;
    int special_characters_enabled = 0;

    printf("Do you want lowercase letters in the password? (y/n): ");
    char input;
    scanf(" %c", &input);

    if (input == 'y' || input == 'Y') 
    {
        lowercase_enabled = 1;
    }

    printf("Do you want uppercase letters in the password? (y/n): ");
    scanf(" %c", &input);

    if (input == 'y' || input == 'Y') 
    {
        uppercase_enabled = 1;
    }

    printf("Do you want special characters in the password? (y/n): ");
    scanf(" %c", &input);

    if (input == 'y' || input == 'Y') 
    {
        special_characters_enabled = 1;
    }

    int character_set_size = 0;
    if (lowercase_enabled) character_set_size += sizeof(lowercase_characters) - 1;
    if (uppercase_enabled) character_set_size += sizeof(uppercase_characters) - 1;
    if (special_characters_enabled) character_set_size += sizeof(special_characters) - 1;

    if (character_set_size == 0) 
    {
        printf("You must select at least one type of character.\n");
        return 1;
    }

    for (int i = 0; i < letter_limit; i++) 
    {
        int random_character;
        int set_choice = rand() % character_set_size;

        if (lowercase_enabled && set_choice < sizeof(lowercase_characters) - 1) 
        {
            random_character = lowercase_characters[rand() % (sizeof(lowercase_characters) - 1)];
        } 
        else if (uppercase_enabled && set_choice < (sizeof(lowercase_characters) - 1) + (sizeof(uppercase_characters) - 1)) {
            random_character = uppercase_characters[rand() % (sizeof(uppercase_characters) - 1)];
        } 
        else if (special_characters_enabled) 
        {
            random_character = special_characters[rand() % (sizeof(special_characters) - 1)];
        } 
        else 
        {
            random_character = lowercase_characters[rand() % (sizeof(lowercase_characters) - 1)];
        }

        password[i] = random_character;
    }

    password[letter_limit] = '\0';

    printf("The password is: %s\n", password);

    printf("------------Press Ok To Exit------------\n");
    fflush(stdout);
    getchar();    

    char n12;
    scanf("%c",&n12);

    return 0;
}