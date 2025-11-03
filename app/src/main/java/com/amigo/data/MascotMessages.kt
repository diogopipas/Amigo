package com.amigo.data

import kotlin.random.Random

object MascotMessages {
    private val motivations = listOf(
        "Small steps, big gains!",
        "Fuel up and feel great!",
        "You got this, Amigo!",
        "Progress over perfection.",
        "Strong habits, strong you!",
        "One smart bite at a time."
    )

    private val tips = listOf(
        "Aim for 25–35g protein per meal.",
        "Half your plate veggies is a great rule of thumb.",
        "Hydrate: 6–8 glasses of water a day.",
        "Healthy fats keep you satisfied longer.",
        "Add fiber for better fullness and gut health.",
        "Colorful plates often equal more micronutrients."
    )

    fun randomMotivation(): String = motivations.random()

    fun randomTip(): String = tips.random()

    fun randomAny(): String = if (Random.nextBoolean()) randomMotivation() else randomTip()

    fun analyzingMessage(): String = "Analyzing your meal… " + randomTip()

    fun afterSaveMessage(): String = "Nice log! " + randomAny()
}


