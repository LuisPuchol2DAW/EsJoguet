package com.example.esjoguet;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Looper;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.TextView;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import android.os.Handler;
import android.os.Looper;
import java.util.logging.LogRecord;

public class Activity_2048Game extends AppCompatActivity implements GestureDetector.OnGestureListener {

    private static final String TAG = "MainActivity";
    private GestureDetector gestureDetector;
    private float startX, startY, endX, endY;

    private final int GRID_SIZE = 4;
    private final int MOVE_UP = -1;
    private final int MOVE_DOWN = 1;
    private final int MOVE_LEFT = -1;
    private final int MOVE_RIGHT = 1;
    private final int noMove = 0;

    private final Map<Integer, Integer> tileColors = new HashMap<>();
    private Integer[][] board; // valores
    private TextView[][] tiles; // Referencias
    private TextView tvScoreCounter, tvBestScoreCounter, tvMovesCounter, tvTimeCounter;
    private Integer scoreCounter = 0, bestScoreCounter, movesCounter = 0, timeCounter = 0;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable runnable;
    private Random random = new Random();
    private Boolean hasGameStarted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: Activity has been created");
        super.onCreate(savedInstanceState);
        startStuff(savedInstanceState);
        gestureDetector = new GestureDetector(this, this);

        startGameLayout();
        initializeTileColors();
        startGame();
    }

    private void startGame() {
        spawnRandomTile();
        spawnRandomTile();
    }

    private void startTimer() {
        runnable = new Runnable() {
            @Override
            public void run() {
                timeCounter++;

                int minutes = timeCounter / 60;
                int seconds = timeCounter % 60;
                String timeString = String.format("%02d:%02d", minutes, seconds);
                tvTimeCounter.setText(timeString);

                handler.postDelayed(this, 1000);
            }
        };
        handler.post(runnable); // Start the timer
    }

    private void stopTimer() {
        handler.removeCallbacks(runnable);
    }

    private void startGameLayout() {
        tvScoreCounter = findViewById(R.id.scoreCounter);
        tvBestScoreCounter = findViewById(R.id.bestScoreCounter);
        tvMovesCounter = findViewById(R.id.movesCounter);
        tvTimeCounter = findViewById(R.id.timeCounter);

        board = new Integer[GRID_SIZE][GRID_SIZE];
        tiles = new TextView[GRID_SIZE][GRID_SIZE];

        tiles[0][0] = findViewById(R.id.tv1x1);
        tiles[0][1] = findViewById(R.id.tv1x2);
        tiles[0][2] = findViewById(R.id.tv1x3);
        tiles[0][3] = findViewById(R.id.tv1x4);

        tiles[1][0] = findViewById(R.id.tv2x1);
        tiles[1][1] = findViewById(R.id.tv2x2);
        tiles[1][2] = findViewById(R.id.tv2x3);
        tiles[1][3] = findViewById(R.id.tv2x4);

        tiles[2][0] = findViewById(R.id.tv3x1);
        tiles[2][1] = findViewById(R.id.tv3x2);
        tiles[2][2] = findViewById(R.id.tv3x3);
        tiles[2][3] = findViewById(R.id.tv3x4);

        tiles[3][0] = findViewById(R.id.tv4x1);
        tiles[3][1] = findViewById(R.id.tv4x2);
        tiles[3][2] = findViewById(R.id.tv4x3);
        tiles[3][3] = findViewById(R.id.tv4x4);
    }

    private Boolean isEmptyTile(Integer tileValue) {
        return tileValue == null;
    }

    private void spawnRandomTile() {
        ArrayList<Integer[]> emptyTiles = new ArrayList<>();

        // Busca las casillas vacías en el tablero
        searchEmptyTiles(emptyTiles);

        if (emptyTiles.isEmpty()) {
            return;
        }

        Integer[] position = emptyTiles.get(random.nextInt(emptyTiles.size()));
        Integer row = position[0];
        Integer col = position[1];

        Integer value = random.nextFloat() < 0.9 ? 2 : 4; // 90% de probabilidad de 2, 10% de probabilidad de 4

        board[row][col] = value;


        updateTileUI(row, col, value);

    }

    private void searchEmptyTiles(ArrayList<Integer[]> emptyTiles) {
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                if (isEmptyTile(board[i][j])) {
                    emptyTiles.add(new Integer[]{i, j});
                }
            }
        }
    }

    private void initializeTileColors() {
        tileColors.put(2, R.color.tile_2);
        tileColors.put(4, R.color.tile_4);
        tileColors.put(8, R.color.tile_8);
        tileColors.put(16, R.color.tile_16);
        tileColors.put(32, R.color.tile_32);
        tileColors.put(64, R.color.tile_64);
        tileColors.put(128, R.color.tile_128);
        tileColors.put(256, R.color.tile_256);
        tileColors.put(512, R.color.tile_512);
        tileColors.put(1024, R.color.tile_1024);
        tileColors.put(2048, R.color.tile_2048);
        tileColors.put(4096, R.color.tile_4096);
        tileColors.put(8192, R.color.tile_8192);
    }

    private void updateTileUI(Integer row, Integer col, Integer value) {
        TextView tile = tiles[row][col];
        if (value > 0) {
            tile.setText(String.valueOf(value));
        } else {
            tile.setText("");
        }
        Integer colorResource = tileColors.getOrDefault(value, R.color.tile_default);
        tile.setBackgroundResource(colorResource);
    }

    private void moveTiles(int direction) {

        //Crear metodo para verificar si se puede si quiera mover, para poder detectar el fin de la partida.
        updateMovesCounter();

        if (!hasGameStarted) {
            hasGameStarted = true;
            startTimer();
        }

        switch (direction) {
            case 0:
                moveUp();
                break;
            case 1:
                moveLeft();
                break;
            case 2:
                moveRight();
                break;
            case 3:
                moveDown();
                break;
            default:
                break;
        }
        spawnRandomTile();
    }

    private void moveDown() {
        for (int i = GRID_SIZE - 1; i >= 0; i--) {
            for (int j = GRID_SIZE - 1; j >= 0; j--) {
                if (!isEmptyTile(board[i][j])) {
                    checkMoveAndAddNearbyTile(i, j, MOVE_DOWN, noMove, 3, 5);
                }
            }
        }
    }

    private void moveRight() {
        for (int i = GRID_SIZE - 1; i >= 0; i--) {
            for (int j = GRID_SIZE - 1; j >= 0; j--) {
                if (!isEmptyTile(board[i][j])) {
                    checkMoveAndAddNearbyTile(i, j, noMove, MOVE_RIGHT, 5, 3);
                }
            }
        }
    }

    private void moveLeft() {
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                if (!isEmptyTile(board[i][j])) {
                    checkMoveAndAddNearbyTile(i, j, noMove, MOVE_LEFT, 5, 0);
                }
            }
        }
    }

    private void moveUp() {
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                if (!isEmptyTile(board[i][j])) {
                    checkMoveAndAddNearbyTile(i, j, MOVE_UP, noMove, 0, 5);
                }
            }
        }
    }

    private void checkMoveAndAddNearbyTile(int i, int j, int moveRow, int moveColumn, int breakConditionRow, Integer breakConditionColumn) {
        Integer newRow = i + moveRow;
        Integer newColumn = j + moveColumn;

        while (i != breakConditionRow && j != breakConditionColumn) {
            if (!isEmptyTile(board[newRow][newColumn]) && board[newRow][newColumn].equals(board[i][j])) {
                //move and add
                updateTile(i, j, newRow, newColumn, board[i][j] * 2);
                updateScore(newRow, newColumn);
                break;
            } else if (!isEmptyTile(board[newRow][newColumn])) {
                //cant add
                break;
            }

            //move
            updateTile(i, j, newRow, newColumn, board[i][j]);
            //adjust Counters
            if (moveRow != 0) {
                i += moveRow;
                newRow += moveRow;
            } else if (moveColumn != 0) {
                j += moveColumn;
                newColumn += moveColumn;
            }
        }
    }

    private void updateMovesCounter() {
        movesCounter++;
        tvMovesCounter.setText(movesCounter.toString());
    }

    private void updateScore(Integer newRow, Integer newColumn) {
        scoreCounter += board[newRow][newColumn];
        tvScoreCounter.setText(scoreCounter.toString());
    }

    private void updateTile(int row, int col, int newRow, int newCol, int value) {
        updateTileUI(newRow, newCol, value);
        updateTileUI(row, col, 0);
        board[newRow][newCol] = value;
        board[row][col] = null;
    }


    public void startStuff(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity2048_game);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        //parte de abajo de la pantalla mayor valor Y
        //parte derecha de la pantalla mayor valor X
        Log.d(TAG, "onFling: Fling gesture detected with velocityX = " + velocityX + " and velocityY = " + velocityY);

        float movementX = startX - endX;
        float movementY = startY - endY;

        if (movementY > movementX && movementY > -movementX) {
            Log.d(TAG, "onFling: Fling gesture detected up");
            moveTiles(0);
        } else if (movementX > movementY && movementX > -movementY) {
            Log.d(TAG, "onFling: Fling gesture detected left");
            moveTiles(1);
        } else if (movementY > movementX && movementY < -movementX) {
            Log.d(TAG, "onFling: Fling gesture detected right");
            moveTiles(2);
        } else if (movementX > movementY && movementX < -movementY) {
            Log.d(TAG, "onFling: Fling gesture detected down");
            moveTiles(3);
        }

        return true;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        startX = e.getX();
        startY = e.getY();
        Log.d(TAG, "onDown: User touched the screen");
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        Log.d(TAG, "onShowPress: User is pressing on the screen");
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        Log.d(TAG, "onSingleTapUp: Single tap detected");
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        endX = e2.getX();
        endY = e2.getY();
        Log.d(TAG, "onScroll: Scroll gesture detected with distanceX = " + distanceX + " and distanceY = " + distanceY);
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        Log.d(TAG, "onLongPress: Long press detected");
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event) || super.onTouchEvent(event);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: Activity is starting");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: Activity has resumed");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: Activity is pausing");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: Activity has stopped");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart: Activity is restarting");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: Activity is being destroyed");
    }
}