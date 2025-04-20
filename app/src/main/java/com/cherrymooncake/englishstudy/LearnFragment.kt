package com.cherrymooncake.englishstudy

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.cherrymooncake.englishstudy.databinding.FragmentLearnBinding

class LearnFragment : Fragment() {

    private var _binding: FragmentLearnBinding? = null
    private val binding get() = _binding!!

    private lateinit var trainer: LearnWordsTrainer


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLearnBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        trainer = LearnWordsTrainer(requireContext())

        showNextQuestion()

        with(binding) {
            btnContinue.setOnClickListener {
                layoutResult.isVisible = false
                resetAnswers()
                showNextQuestion()
            }
            btnSkip.setOnClickListener {
                showNextQuestion()
            }
        }
    }

    private fun showNextQuestion() {
        val question: Question? = trainer.getNextQuestion()

        if (question == null || question.variants.size < NUMBER_OF_ANSWERS) {
            Log.d("QuestionFragment", "No more questions, navigating to ResultFragment")
            parentFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, ResultFragment())
                .commit()
            return
        }

        with(binding) {
            if (question == null || question.variants.size < NUMBER_OF_ANSWERS) {
                tvQuestionWord.isVisible = false
                layoutVariants.isVisible = false
                btnSkip.text = "Complete"
                btnSkip.isVisible = true
            } else {
                btnSkip.isVisible = true
                tvQuestionWord.isVisible = true
                tvQuestionWord.text = question.correctAnswer.original

                val variantViews = listOf(
                    Pair(layoutAnswer1, tvVariantValue1),
                    Pair(layoutAnswer2, tvVariantValue2),
                    Pair(layoutAnswer3, tvVariantValue3),
                    Pair(layoutAnswer4, tvVariantValue4)
                )

                question.variants.forEachIndexed { index, variant ->
                    val (layout, textView) = variantViews[index]
                    textView.text = variant.translate

                    layout.setOnClickListener {
                        if (trainer.checkCorrectAnswer(index)) {
                            markAnswerCorrect(layout, textView)
                            showResultMessage(true)
                        } else {
                            markAnswerWrong(layout, textView)
                            showResultMessage(false)
                        }
                    }
                }
            }
        }
    }

    private fun resetAnswers() {
        with(binding) {
            markAnswerNeutral(layoutAnswer1, tvVariantValue1)
            markAnswerNeutral(layoutAnswer2, tvVariantValue2)
            markAnswerNeutral(layoutAnswer3, tvVariantValue3)
            markAnswerNeutral(layoutAnswer4, tvVariantValue4)
        }
    }

    private fun markAnswerNeutral(layoutAnswer: LinearLayout, tvVariantValue: TextView) {
        layoutAnswer.background = ContextCompat.getDrawable(requireContext(), R.drawable.shape_rounded_containers)
        tvVariantValue.setTextColor(ContextCompat.getColor(requireContext(), R.color.textVariantsColor))
    }

    private fun markAnswerWrong(layoutAnswer: LinearLayout, tvVariantValue: TextView) {
        layoutAnswer.background = ContextCompat.getDrawable(requireContext(), R.drawable.shape_rounded_containers_wrong)
        tvVariantValue.setTextColor(ContextCompat.getColor(requireContext(), R.color.wrongAnswerColor))
    }

    private fun markAnswerCorrect(layoutAnswer: LinearLayout, tvVariantValue: TextView) {
        layoutAnswer.background = ContextCompat.getDrawable(requireContext(), R.drawable.shape_rounded_containers_correct)
        tvVariantValue.setTextColor(ContextCompat.getColor(requireContext(), R.color.correctAnswerColor))
    }

    private fun showResultMessage(isCorrect: Boolean) {
        val color = if (isCorrect) {
            ContextCompat.getColor(requireContext(), R.color.correctAnswerColor)
        } else {
            ContextCompat.getColor(requireContext(), R.color.wrongAnswerColor)
        }

        val messageText = getString(if (isCorrect) R.string.title_correct else R.string.title_wrong)
        val resultIconResource = if (isCorrect) R.drawable.ic_correct else R.drawable.ic_wrong

        with(binding) {
            btnSkip.isVisible = false
            layoutResult.isVisible = true
            btnContinue.setTextColor(color)
            layoutResult.setBackgroundColor(color)
            tvResultMessage.text = messageText
            ivResultIcon.setImageResource(resultIconResource)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
