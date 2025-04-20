package com.cherrymooncake.englishstudy

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class Word (
    val original: String,
    val translate: String,
    var learned: Boolean = false,
)

data class Question (
    val variants: List<Word>,
    val correctAnswer: Word,
)

class LearnWordsTrainer(context: Context) {

    //private val dictionary: List<Word> = loadWordsFromAssets(context).shuffled()
    private var currentQuestion: Question? = null

    private val dictionary: List<Word>
    init {
        dictionary = loadWordsFromAssets(context).shuffled()
    }

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

    private fun loadWordsFromAssets(context: Context): List<Word> {
        val json = context.assets.open("words.json").bufferedReader().use { it.readText() }
        val type = object : TypeToken<List<Word>>() {}.type
        return Gson().fromJson(json, type)
    }
}

const val NUMBER_OF_ANSWERS: Int = 4