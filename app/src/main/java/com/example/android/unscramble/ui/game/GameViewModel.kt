package com.example.android.unscramble.ui.game

import android.text.Spannable
import android.text.SpannableString
import android.text.style.TtsSpan
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

class GameViewModel : ViewModel(){


    // var -> default getter and setter functions automatically generated (makes value mutable)
    // val -> getter function is generated by default (makes value immutable, something similar with final)

    // without LiveData
//    private val _score = 0
//    val score: Int
//        get() = _score

    // with LiveData
    private val _score = MutableLiveData(0)
    val score: LiveData<Int>
        get() = _score

    private val _currentWordCount = MutableLiveData(0)
    val currentWordCount: LiveData<Int>
        get() = _currentWordCount

    private val _currentScrambledWord = MutableLiveData<String>()
    val currentScrambledWord: LiveData<Spannable> = Transformations.map(_currentScrambledWord) {
        if (it == null) {
            SpannableString("")
        } else {
            val scrambledWord = it.toString()
            val spannable: Spannable = SpannableString(scrambledWord)
            spannable.setSpan(
                TtsSpan.VerbatimBuilder(scrambledWord).build(),
                0,
                scrambledWord.length,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
            )
            spannable
        }
    }

    /**
     In view model, data should be private and var (editable).
     In public, data should be public and val (readable, but not editable).
     To achieve , use backing property (return something from a getter other than the exact object)
     */

    // list of words in the game, avoid repetition
    private var wordsList: MutableList<String> = mutableListOf()
    private lateinit var currentWord: String

    init {
        getNextWord()
    }

    private fun getNextWord(){
        // get random word
        currentWord = allWordsList.random();
        // convert currentWord string to an array of characters and assign to new val called tempWord
        val tempWord = currentWord.toCharArray()
        tempWord.shuffle()

        // avoid same word after shuffle
        while(String(tempWord).toString().equals(currentWord,false)){
            tempWord.shuffle()
        }

        // avoid word repetition while playing
        if(wordsList.contains(currentWord)){
            getNextWord()
        }else{
//          Previous(Without live data)
//          _currentScrambledWord = String(tempWord)
//          With LiveData (more .value)
            _currentScrambledWord.value = String(tempWord)
            //  Increment the value by one with null-safety.
            _currentWordCount.value = (_currentWordCount.value)?.inc()
            wordsList.add(currentWord)
        }
    }

    fun reinitializeData(){
        _score.value = 0
        _currentWordCount.value = 0
        wordsList.clear()
        getNextWord()
    }

    private fun increaseScore(){
        _score.value = (_score.value)?.plus(SCORE_INCREASE)
    }


    fun isUserWordCorrect(playerWord: String): Boolean {
        if(playerWord.equals(currentWord, true)){
            increaseScore()
            return true
        }
        return false
    }
    fun nextWord(): Boolean{
        return if (_currentWordCount.value!! < MAX_NO_OF_WORDS){
            getNextWord()
            true
        }
        else false
    }


}