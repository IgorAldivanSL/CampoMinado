package com.igor.p2

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class GameActivity : AppCompatActivity() {

    // Board configuration
    private val ROWS = 8
    private val COLS = 8
    private val MINES = 10

    private lateinit var playerName: String
    private lateinit var tvPlayerName: TextView
    private lateinit var tvMinesLeft: TextView
    private lateinit var gridLayout: GridLayout

    // Board state
    private val isMine = Array(ROWS) { BooleanArray(COLS) }
    private val isRevealed = Array(ROWS) { BooleanArray(COLS) }
    private val isFlagged = Array(ROWS) { BooleanArray(COLS) }
    private val adjacentMines = Array(ROWS) { IntArray(COLS) }
    private val buttons = Array(ROWS) { arrayOfNulls<Button>(COLS) }

    private var revealedCount = 0
    private val safeCells get() = ROWS * COLS - MINES
    private var flagCount = 0
    private var gameOver = false
    private var startTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        playerName = intent.getStringExtra("PLAYER_NAME") ?: "Jogador"

        tvPlayerName = findViewById(R.id.tvPlayerName)
        tvMinesLeft = findViewById(R.id.tvMinesLeft)
        gridLayout = findViewById(R.id.gridLayout)

        tvPlayerName.text = "Jogador: $playerName"

        val btnReset = findViewById<Button>(R.id.btnReset)
        btnReset.setOnClickListener { resetGame() }

        setupGrid()
        placeMines()
        calculateAdjacent()
        updateMinesLeft()
        startTime = System.currentTimeMillis()
    }

    private fun setupGrid() {
        gridLayout.removeAllViews()
        gridLayout.rowCount = ROWS
        gridLayout.columnCount = COLS

        val screenWidth = resources.displayMetrics.widthPixels
        val btnSize = (screenWidth - 32) / COLS  // 32dp margins

        for (r in 0 until ROWS) {
            for (c in 0 until COLS) {
                val btn = Button(this)
                val params = GridLayout.LayoutParams()
                params.width = btnSize
                params.height = btnSize
                params.setMargins(1, 1, 1, 1)
                params.rowSpec = GridLayout.spec(r)
                params.columnSpec = GridLayout.spec(c)
                btn.layoutParams = params
                btn.textSize = 12f
                btn.setPadding(0, 0, 0, 0)
                btn.setBackgroundColor(Color.LTGRAY)
                btn.text = ""

                val row = r
                val col = c

                // Click: reveal cell
                btn.setOnClickListener {
                    if (!gameOver) revealCell(row, col)
                }

                // Long click: flag/unflag
                btn.setOnLongClickListener {
                    if (!gameOver) toggleFlag(row, col)
                    true
                }

                buttons[r][c] = btn
                gridLayout.addView(btn)
            }
        }
    }

    private fun placeMines() {
        var placed = 0
        while (placed < MINES) {
            val r = (0 until ROWS).random()
            val c = (0 until COLS).random()
            if (!isMine[r][c]) {
                isMine[r][c] = true
                placed++
            }
        }
    }

    private fun calculateAdjacent() {
        for (r in 0 until ROWS) {
            for (c in 0 until COLS) {
                if (!isMine[r][c]) {
                    adjacentMines[r][c] = countAdjacentMines(r, c)
                }
            }
        }
    }

    private fun countAdjacentMines(row: Int, col: Int): Int {
        var count = 0
        for (dr in -1..1) {
            for (dc in -1..1) {
                val nr = row + dr
                val nc = col + dc
                if (nr in 0 until ROWS && nc in 0 until COLS && isMine[nr][nc]) {
                    count++
                }
            }
        }
        return count
    }

    private fun revealCell(row: Int, col: Int) {
        if (isRevealed[row][col] || isFlagged[row][col]) return

        isRevealed[row][col] = true
        revealedCount++

        val btn = buttons[row][col]!!

        if (isMine[row][col]) {
            // Hit a mine
            btn.text = "💣"
            btn.setBackgroundColor(Color.RED)
            gameOver = true
            revealAllMines()
            Toast.makeText(this, "Você pisou em uma mina!", Toast.LENGTH_SHORT).show()
            val elapsed = ((System.currentTimeMillis() - startTime) / 1000).toInt()
            // Even if lost, give points for the cells they managed to reveal (subtracting 1 because this mine cell was just marked as revealed)
            val score = maxOf(0, (revealedCount - 1) * 10)
            goToResult(score, elapsed, false)
            return
        }

        val adj = adjacentMines[row][col]
        btn.setBackgroundColor(Color.WHITE)
        if (adj > 0) {
            btn.text = adj.toString()
            btn.setTextColor(getNumberColor(adj))
        } else {
            btn.text = ""
            // Flood fill for empty cells
            for (dr in -1..1) {
                for (dc in -1..1) {
                    val nr = row + dr
                    val nc = col + dc
                    if (nr in 0 until ROWS && nc in 0 until COLS && !isRevealed[nr][nc]) {
                        revealCell(nr, nc)
                    }
                }
            }
        }

        if (revealedCount == safeCells && !gameOver) {
            gameOver = true
            Toast.makeText(this, "Você ganhou!", Toast.LENGTH_SHORT).show()
            val elapsed = ((System.currentTimeMillis() - startTime) / 1000).toInt()
            val score = calculateScore(elapsed)
            goToResult(score, elapsed, true)
        }
    }

    private fun toggleFlag(row: Int, col: Int) {
        if (isRevealed[row][col]) return
        val btn = buttons[row][col]!!
        if (isFlagged[row][col]) {
            isFlagged[row][col] = false
            flagCount--
            btn.text = ""
            btn.setBackgroundColor(Color.LTGRAY)
        } else {
            isFlagged[row][col] = true
            flagCount++
            btn.text = "🚩"
            btn.setBackgroundColor(Color.YELLOW)
        }
        updateMinesLeft()
    }

    private fun revealAllMines() {
        for (r in 0 until ROWS) {
            for (c in 0 until COLS) {
                if (isMine[r][c]) {
                    buttons[r][c]!!.text = "💣"
                    buttons[r][c]!!.setBackgroundColor(Color.RED)
                }
            }
        }
    }

    private fun calculateScore(elapsedSeconds: Int): Int {
        // More score for faster time, base = revealedCount * 10
        val base = revealedCount * 10
        //val timeBonus = maxOf(0, 300 - elapsedSeconds) // Bonus for finishing under 5 min
        return base 
    }

    private fun goToResult(score: Int, elapsed: Int, won: Boolean) {
        val intent = Intent(this, ResultActivity::class.java)
        intent.putExtra("PLAYER_NAME", playerName)
        intent.putExtra("SCORE", score)
        intent.putExtra("ELAPSED", elapsed)
        intent.putExtra("WON", won)
        startActivity(intent)
        finish()
    }

    private fun updateMinesLeft() {
        tvMinesLeft.text = "Minas: ${MINES - flagCount}"
    }

    private fun getNumberColor(n: Int): Int = when (n) {
        1 -> Color.BLUE
        2 -> Color.parseColor("#006400") // dark green
        3 -> Color.RED
        4 -> Color.parseColor("#00008B") // dark blue
        5 -> Color.parseColor("#8B0000") // dark red
        6 -> Color.CYAN
        7 -> Color.BLACK
        else -> Color.GRAY
    }

    private fun resetGame() {
        // Reset state
        revealedCount = 0
        flagCount = 0
        gameOver = false
        for (r in 0 until ROWS) {
            for (c in 0 until COLS) {
                isMine[r][c] = false
                isRevealed[r][c] = false
                isFlagged[r][c] = false
                adjacentMines[r][c] = 0
            }
        }
        setupGrid()
        placeMines()
        calculateAdjacent()
        updateMinesLeft()
        startTime = System.currentTimeMillis()
    }
}
