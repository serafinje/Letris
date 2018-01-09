package com.sera.android.letris;

class LetrisStatus {
    // Constantes que identifican el nivel de dificultad
    public static final int DIFFICULTY_EASY = 0;
    public static final int DIFFICULTY_HARD = 1;
    public static final int DIFFICULTY_MEDIUM = 2;

    // Constantes para el estado de juego
    public static final int STATE_LOSE = 1;
    public static final int STATE_PAUSE = 2;
    public static final int STATE_RUNNING = 3;
    public static final int STATE_WIN = 4; 
    public static final int STATE_EXIT = 5;
    
    // Dimensiones del tablero
	int ALTO_CUADRICULA=11;
	int ANCHO_CUADRICULA=8;

    /** The state of the game. One of RUNNING, PAUSE, LOSE, or WIN */
    public int mMode;
    public long points;

    private LetrisThread currentGame;
    
    public LetrisStatus(LetrisThread currentGame)
    {
    	this.currentGame=currentGame;
    }
    
    public boolean running() {
    	return mMode==LetrisStatus.STATE_RUNNING;
    }
    public boolean paused() {
    	return mMode==LetrisStatus.STATE_PAUSE; 
    }
    
    public void switchPause() {
    	switch (mMode) {
    		case LetrisStatus.STATE_PAUSE: currentGame.unpause(); break;
    		case LetrisStatus.STATE_RUNNING: currentGame.pause(); break;
    	}
    }        
}
