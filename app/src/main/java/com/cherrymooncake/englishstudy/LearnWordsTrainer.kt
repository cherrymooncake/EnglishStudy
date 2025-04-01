package com.cherrymooncake.englishstudy

data class Word (
    val original: String,
    val translate: String,
    var learned: Boolean = false,
)

data class Question (
    val variants: List<Word>,
    val correctAnswer: Word,
)

class LearnWordsTrainer {

    private val dictionary: List<Word> = listOf(
        Word("Galaxy", "Галактика"),
        Word("Table", "Стол"),
        Word("Moon", "Луна"),
        Word("Sun", "Солнце"),
        Word("Star", "Звезда"),
        Word("Sky", "Небо"),
        Word("Blue", "Синий"),
        Word("Tomato", "Томат"),
        Word("Pink", "Розовый"),
        Word("Dog", "Собака"),
        Word("Cat", "Кот"),
        Word("Black", "Черный"),
        Word("Gold", "Золото"),
        Word("Cherry", "Вишня"),
        Word("Pie", "Пирог"),
    )

    private var currentQuestion: Question? = null

    fun getNextQuestion(): Question? {
        val notLearnedList = dictionary.filter { !it.learned}
        if (notLearnedList.isEmpty()) return null

        val questionWords =
            if (notLearnedList.size < NUMBER_OF_ANSWERS) {
                val learnedList = dictionary.filter { it.learned }.shuffled()
                notLearnedList.shuffled()
                    .take(NUMBER_OF_ANSWERS) + learnedList
                    .take(NUMBER_OF_ANSWERS - notLearnedList.size)
            } else {
                notLearnedList.shuffled().take(NUMBER_OF_ANSWERS)
            }.shuffled()

        val correctAnswer = questionWords.random()

        currentQuestion = Question(
            variants = questionWords,
            correctAnswer = correctAnswer,
        )
        return currentQuestion
    }

    fun checkCorrectAnswer(userAnswerIndex: Int?): Boolean {

        return currentQuestion?.let {
            val correctAnswerId = it.variants.indexOf(it.correctAnswer)
            if (correctAnswerId == userAnswerIndex) {
                it.correctAnswer.learned = true
                true
            } else {
                false
            }
        } ?: false
    }

}

const val NUMBER_OF_ANSWERS: Int = 4