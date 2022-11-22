package net.azarquiel.cazatopos

import android.app.AlertDialog
import android.graphics.drawable.AnimationDrawable
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.SoundPool
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.random.Random

class MainActivity : AppCompatActivity(), View.OnClickListener {
    companion object{
        const val NTOPOS = 8
    }


    private lateinit var mp: MediaPlayer
    private lateinit var BgMusic: Any
    private var isOver: Boolean = false
    private lateinit var tvscore: TextView
    private var puntos: Int = 0
    private lateinit var cl: ConstraintLayout
    private val iv = arrayOfNulls<ImageView>(NTOPOS)
    private lateinit var random: Random
    private var time: Long = 0L


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        time = System.currentTimeMillis()
        cl = findViewById<ConstraintLayout>(R.id.cl)
        random = Random(System.currentTimeMillis())
        tvscore = findViewById(R.id.tvscore)

        loadResources()
        showScore()
        pintartopos()
        sacatopos()

    }
    private fun loadResources(){
        mp = MediaPlayer.create(this, R.raw.sonido)
    }

    private fun sacatopos() {
        val ntopos = (1..NTOPOS/2).random(random)
        var vector = IntArray(6){i->i}
        vector.shuffle(random)
        val vtopos = vector.copyOfRange(0,ntopos)
        showTopos(vtopos)
        tapaTopos(vtopos)
    }

    private fun tapaTopos(vtopos: IntArray) {


        GlobalScope.launch() {
            val time = (1..3).random(random)*500
            SystemClock.sleep(time.toLong())
            launch(Main) {
                for (n in vtopos){
                    iv[n]!!.setBackgroundResource(android.R.color.transparent)
                    iv[n]!!.isEnabled = false
                }
               if (!isOver) {
                   sacatopos()
               }
            }
        }

    }

    private fun showTopos(vtopos: IntArray) {
        for (n in vtopos){
        val imageShow = iv[n]
            val mor = (1..10).random(random)
            if (mor == 1){
                showTopo(imageShow, 'r')
            }else{
                showTopo(imageShow, 'm')
            }

        }
    }

    private fun showTopo(imageShow: ImageView?, tipo: Char) {
        val id = resources.getIdentifier("ani$tipo", "drawable", packageName)
        imageShow!!.setBackgroundResource(id)
        val imageShowAD = imageShow.background as AnimationDrawable
        imageShowAD.start()
        imageShow.isEnabled = true
        imageShow.tag = tipo
    }


    private fun pintartopos() {
        for (i in 0 until cl.childCount) {
            iv[i] = cl.getChildAt(i) as ImageView
            iv[i]!!.setOnClickListener(this)
            iv[i]!!.isEnabled = false

        }
    }

        override fun onClick(v: View?) {
        val iv = v as ImageView
            //ponemos cuanto valen los puntos
            val tag = v.tag
            if (tag == 'm'){
                puntos++

            }else if (tag == 'r'){
                puntos+=10

            }

            iv.isEnabled = false
            showScore()
            //poner la imagen de los muertos
            val dead  = if (tag == 'm') "tdead" else "tedead"
            val id = resources.getIdentifier(dead, "drawable", packageName)
            iv.setBackgroundResource(id)

            //poner la musica
            mp.start()
          //  musicaStart()


            if (puntos >= 10){
                gameOver()
            }

        }


  //  private fun musicaStart() {
  //      var sp: SoundPool? = null
  //      var sonido: Int = 0
  //      var sonidoStream: Int = 0

  //      sp = SoundPool(5, AudioManager.STREAM_MUSIC, 0)
  //      sonido = sp!!.load(this, R.raw.sonido, 1)

  //      sonidoStream = sp!!.play(sonido, 1f, 1f, 1, -1, 1f)
  //      sp!!.stop(sonidoStream)
  //  }

    private fun gameOver(){
        isOver = true
        val segundos = (System.currentTimeMillis()-time)/100
        val builder = AlertDialog.Builder(this)
        builder.setTitle("GameOver.")
        builder.setMessage("Lo conseguiste en $segundos segundos")
        builder.setPositiveButton("Ok") { dialog, which ->
          finish()
        }
        builder.setNegativeButton("New Game") { dialog, which ->
            newGame()
        }

        builder.show()

    }

    private fun newGame() {
        puntos = 0
        isOver = false
        showScore()
        pintartopos()
        sacatopos()

    }

    private fun showScore() {
        tvscore.text = "$puntos puntos"
    }







}
