package com.example.esjoguet;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.TextView;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.esjoguet.databinding.Activity2048GameBinding;

import java.util.ArrayList;
import java.util.Random;

public class Activity_2048Game extends AppCompatActivity implements GestureDetector.OnGestureListener {

    private static final String TAG = "MainActivity";
    private GestureDetector gestureDetector;
    private float startX, startY, endX, endY;


    private final int GRID_SIZE = 4; // Tamaño del tablero (4x4)
    private Integer[][] board; // Representa los valores del tablero
    private TextView[][] tiles; // Almacena las referencias a los ImageViews
    private Random random = new Random(); // Para elegir casillas y valores aleatorios

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: Activity has been created");
        super.onCreate(savedInstanceState);
        startStuff(savedInstanceState);
        gestureDetector = new GestureDetector(this,this);

        board = new Integer[GRID_SIZE][GRID_SIZE]; //Matriz vacia
        tiles = new TextView[GRID_SIZE][GRID_SIZE]; //almacena los valores de las casillas

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

        spawnRandomTile();
        spawnRandomTile();
    }

    private Boolean isEmptyTile(Integer tileValue) {
        return tileValue == null;
    }

    private void spawnRandomTile() {
        ArrayList<Integer[]> emptyTiles = new ArrayList<>();

        // Busca las casillas vacías en el tablero
        for (Integer i = 0; i < GRID_SIZE; i++) {
            for (Integer j = 0; j < GRID_SIZE; j++) {
                if (isEmptyTile(board[i][j])) {
                    emptyTiles.add(new Integer[]{i, j});
                }
            }
        }

        if (emptyTiles.isEmpty()) {
            return; // No hay casillas vacías, el juego ha terminado
        }

        Integer[] position = emptyTiles.get(random.nextInt(emptyTiles.size()));
        Integer row = position[0];
        Integer col = position[1];

        Integer value = random.nextFloat() < 0.9 ? 2 : 4; // 90% de probabilidad de 2, 10% de probabilidad de 4

        board[row][col] = value;

        updateTileUI(row, col, value);

        // Encuentra una posición vacía aleatoria y asigna un 2 o 4
    }

    private void updateTileUI(Integer row, Integer col, Integer value) {
        TextView tile = tiles[row][col];
        if (value > 0) {
            tile.setText(String.valueOf(value)); // Muestra el número
        } else {
            tile.setText(""); // Deja vacío si el valor es 0
        }
        switch (value) {
            case 2:
                tile.setBackgroundResource(R.color.tile_2);
                break;
            case 4:
                tile.setBackgroundResource(R.color.tile_4);
                break;
            case 8:
                tile.setBackgroundResource(R.color.tile_8);
                break;
            case 16:
                tile.setBackgroundResource(R.color.tile_16);
                break;
            default:
                tile.setBackgroundResource(R.color.tile_default);
                break;
        }
    }

    private void moveTiles(int direction) {
        // Realiza el movimiento de las piezas en la dirección especificada
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
        } else if (movementX > movementY && movementX > -movementY) {
            Log.d(TAG, "onFling: Fling gesture detected left");
        } else if (movementY > movementX && movementY < -movementX) {
            Log.d(TAG, "onFling: Fling gesture detected right");
        } else if (movementX > movementY && movementX < -movementY) {
            Log.d(TAG, "onFling: Fling gesture detected down");
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