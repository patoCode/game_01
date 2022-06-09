package com.training.game_01

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.training.game_01.R.drawable.*
import com.training.game_01.databinding.ActivityMainBinding
import com.training.game_01.databinding.DialogGameEndBinding
import java.util.*
import kotlin.collections.ArrayList
import java.util.Timer
import kotlin.concurrent.schedule

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    /* VARIABLES AUXILIARES*/
    private var _scoreGoals:Int = 0
    private var _scoreFails:Int = 0
    private var _lives:Int = 3
    private var _backgroundPanel : Int = 0 // fondo
    private val _panelImages = arrayOfNulls<ImageButton>(12)

    /* AUXILIARES - GAMEPLAY */
    private var _baseArrayShuffle = arrayListOf<Int>()
    val _cardsImgs = arrayListOf<Int>()
    private var _baseCard:ImageButton? = null
    private var _baseImg :Int = 0
    private var _matchImg :Int = 0
    var _lockPanel:Boolean = false
    /* DELAY */
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        actionButtons()
    }
    fun actionButtons() {
        binding.btnPlay.setOnClickListener(View.OnClickListener { init() })
        binding.btnReset.setOnClickListener(View.OnClickListener { init() })
        binding.btnPower.setOnClickListener(View.OnClickListener { finish() })
    }
    private fun _loadPanel(){
        _panelImages[0] = binding.ib11
        _panelImages[1] = binding.ib12
        _panelImages[2] = binding.ib13
        _panelImages[3] = binding.ib14
        _panelImages[4] = binding.ib21
        _panelImages[5] = binding.ib22
        _panelImages[6] = binding.ib23
        _panelImages[7] = binding.ib24
        _panelImages[8] = binding.ib31
        _panelImages[9] = binding.ib32
        _panelImages[10] = binding.ib33
        _panelImages[11] = binding.ib34
    }
    private fun _reset(){
        _scoreGoals = 0
        _lives = 3
        binding.tvScore.text = _scoreGoals.toString()
        binding.tvLive.text = _lives.toString()
    }
    private fun _loadCardsImg(){
        _cardsImgs.addAll(
            listOf(f01,f02,f03,f04,f05,f06)
        )
        _backgroundPanel  = fondo
    }
    private fun _shuffleCards(_size: Int): ArrayList<Int> {
        val res: ArrayList<Int> = ArrayList<Int>()
        for (i in 0 until _size)
            res.add(i % _size / 2)
        res.shuffle()
        Log.d("TEST","SUFFLE: "+ Arrays.toString(res.toArray()))
        return res
    }
    private fun _evaluate(i:Int, _btnTap:ImageButton){
        if(_baseCard == null){
            _baseCard = _btnTap
            _baseCard!!.scaleType = ImageView.ScaleType.CENTER_CROP
            _baseCard!!.setImageResource(_cardsImgs[_baseArrayShuffle[i]])
            _baseCard!!.isEnabled = false
            _baseImg = _baseArrayShuffle[i]
        }else{
            _lockPanel = true
            _btnTap.scaleType = ImageView.ScaleType.CENTER_CROP
            _btnTap.setImageResource(_cardsImgs[_baseArrayShuffle[i]])
            _btnTap.isEnabled = false
            _matchImg = _baseArrayShuffle[i]
            if(_baseImg === _matchImg){
                _baseCard = null
                _lockPanel = false
                _scoreGoals++
                binding.tvScore.text = _scoreGoals.toString()
                if(_scoreGoals === _cardsImgs.size){
                    Toast.makeText(applicationContext, "Has ganado!!", Toast.LENGTH_LONG).show()
                }
            }else{
                handler.postDelayed({
                    _lives--
                    if(_lives == 0){
                        showEndDialog()
                    }else{
                        _lockPanel = false
                        _baseCard!!.scaleType = ImageView.ScaleType.CENTER_CROP
                        _baseCard!!.setImageResource(R.drawable.fondo)
                        _baseCard!!.isEnabled = true
                        _baseCard = null
                        _btnTap.scaleType = ImageView.ScaleType.CENTER_CROP
                        _btnTap.setImageResource(R.drawable.fondo)
                        _btnTap.isEnabled = true
                        binding.tvLive.text = _lives.toString()
                        _scoreFails++
                    }

                },1000)
            }
        }
    }
    private fun showEndDialog() {
        val view = View.inflate(this@MainActivity, R.layout.dialog_game_end, null)
        val builder = AlertDialog.Builder(this@MainActivity)
        builder.setView(view)

        val dialog = builder.create()
        dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        var btn : Button = view.findViewById<Button>(R.id.btnConfirm)
        btn.setOnClickListener {
            dialog.dismiss()
            dialog.cancel()
            binding.rlMainContainer.removeView(view)
            init()
        }
    }
    private fun init(){
            _loadPanel()
            _loadCardsImg()
            _baseArrayShuffle = _shuffleCards(_panelImages.size)

            for (i in 0 until _panelImages.size){
                _panelImages[i]!!.scaleType = ImageView.ScaleType.CENTER_CROP
                _panelImages[i]!!.setImageResource(_cardsImgs[_baseArrayShuffle[i]])
            }

            handler.postDelayed({
                for (i in 0 until _panelImages.size){
                    _panelImages[i]!!.scaleType = ImageView.ScaleType.CENTER_CROP
                    _panelImages[i]!!.setImageResource(_backgroundPanel)
                }
            },1000)
            for (i in 0 until _panelImages.size){
                _panelImages[i]!!.isEnabled = true
                _panelImages[i]!!.setOnClickListener {
                    if(!_lockPanel){
                        _evaluate(i, _panelImages[i]!!)
                    }
                }
            }
            _reset()
        }
}