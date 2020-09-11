package com.example.android.guesstheword.screens.game

import android.os.CountDownTimer
import android.text.format.DateUtils
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

class GameViewModel: ViewModel() {

    companion object {
        private const val DONE = 0L
        private const val ONE_SECOND = 1000L
        private const val COUNTDOWN_TIME = 60 * 1000L

        private val CORRECT_BUZZ_PATTERN = longArrayOf(100, 100, 100, 100, 100, 100)
        private val PANIC_BUZZ_PATTERN = longArrayOf(0, 200)
        private val GAME_OVER_BUZZ_PATTERN = longArrayOf(0, 2000)
        private val NO_BUZZ_PATTERN = longArrayOf(0)
    }

    enum class BuzzType(val pattern: LongArray) {
        CORRECT(CORRECT_BUZZ_PATTERN),
        GAME_OVER(GAME_OVER_BUZZ_PATTERN),
        COUNTDOWN_PANIC(PANIC_BUZZ_PATTERN),
        NO_BUZZ(NO_BUZZ_PATTERN)
    }

    private lateinit var timer: CountDownTimer

    // live data encapsulation
    // The current word
    private var _word = MutableLiveData<String>()
    val word : LiveData<String>
        get() = _word
    // The current score
    private val _score = MutableLiveData<Int> ()
    val score : LiveData<Int>
        get() = _score
    private val _eventGameFinish = MutableLiveData<Boolean>()
    val eventGameFinish : LiveData<Boolean>
        get() = _eventGameFinish
    private val _time = MutableLiveData<Long>()
    val time : LiveData<Long>
        get() = _time
    private val _buzzType = MutableLiveData<BuzzType>()
    val buzzType : LiveData<BuzzType>
        get() = _buzzType

    val timeString = Transformations.map(time) { nowTime ->
        DateUtils.formatElapsedTime(nowTime / 1000L)
    }

    // The list of words - the front of the list is the next word to guess
    private lateinit var wordList: MutableList<String>

    init {
        Log.i("GameViewModel", "GameViewModel Create!")
        _score.value = 0
        _word.value = ""
        _time.value = COUNTDOWN_TIME
        _eventGameFinish.value = false
        _buzzType.value = BuzzType.NO_BUZZ

        resetList()
        nextWord()

        // init time
        timer = object : CountDownTimer (COUNTDOWN_TIME, ONE_SECOND) {
            override fun onTick(p0: Long) {
                _time.value = p0
                _buzzType.value = BuzzType.COUNTDOWN_PANIC
            }

            override fun onFinish() {
                _eventGameFinish.value = true
                _buzzType.value = BuzzType.GAME_OVER
            }
        }
        timer.start()
    }
    override fun onCleared() {
        super.onCleared()
        Log.i("GameViewModel", "GameViewModel Cleared!")
        timer.cancel()
    }

    /** Methods for buttons presses **/
    fun onSkip() {
        _score.value = score.value ?.minus(1)
        nextWord()
    }

    fun onCorrect() {
        _score.value = score.value ?.plus(1)
        _buzzType.value = BuzzType.CORRECT
        nextWord()
    }

    /**
     * Moves to the next word in the list
     */
    fun nextWord() {
        //Select and remove a word from the list
        if (wordList.isEmpty()) {
            resetList()
        }
        _word.value = wordList.removeAt(0)
    }

    /** game complete */
    fun gameComplete() {
        _eventGameFinish.value = false
    }

    /**
     * Resets the list of words and randomizes the order
     */
    private fun resetList() {
        wordList = mutableListOf(
                "queen",
                "hospital",
                "basketball",
                "cat",
                "change",
                "snail",
                "soup",
                "calendar",
                "sad",
                "desk",
                "guitar",
                "home",
                "railway",
                "zebra",
                "jelly",
                "car",
                "crow",
                "trade",
                "bag",
                "roll",
                "bubble"
        )
        wordList.shuffle()
    }
}