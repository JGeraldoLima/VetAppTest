package com.example.petmedtracker.presentation.common

/**
 * Species options for Add Pet, with display label and icon (emoji) for list and cards.
 */
enum class Species(val displayName: String, val icon: String) {
    Dog("Dog", "🐕"),
    Cat("Cat", "🐈"),
    Bird("Bird", "🐦"),
    Turtle("Turtle", "🐢"),
    Other("Other", "🐾");

    companion object {
        fun fromDisplayName(name: String): Species? = entries.find { it.displayName == name }
        fun fromProtoValue(value: String): Species? = entries.find { it.displayName == value }
    }
}
