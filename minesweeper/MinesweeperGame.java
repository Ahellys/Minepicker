package com.codegym.games.minesweeper;

import com.codegym.engine.cell.Color;
import com.codegym.engine.cell.Game;

import java.util.ArrayList;
import java.util.List;

public class MinesweeperGame extends Game {
    private static final int SIDE = 9;
    private GameObject[][] gameField = new GameObject[SIDE][SIDE];
    private int countMinesOnField;
    private int countFlags;
    private int countClosedTiles=SIDE*SIDE;
    private int score=0;
    private boolean isGameStopped;
    private static final String FLAG="\uD83D\uDEA9";
    private static final String MINE="\uD83D\uDCA3";

    @Override
    public void initialize() {
        setScreenSize(SIDE, SIDE);
        createGame();
    }

    private void createGame() {
        for (int y = 0; y < SIDE; y++) {
            for (int x = 0; x < SIDE; x++) {
                boolean isMine = getRandomNumber(10) < 1;
                if (isMine) {
                    countMinesOnField++;
                }
                gameField[y][x] = new GameObject(x, y, isMine);
                setCellColor(x, y, Color.GREY);
                setCellValue(x, y,"");
            }
        }
        countMineNeighbors();
        countFlags=countMinesOnField;
    }

    private void restart(){
        isGameStopped=false;
        score=0;
        setScore(score);
        countMinesOnField=0;
        countClosedTiles=SIDE*SIDE;
        createGame();

    }

    private void gameOver(){
        isGameStopped=true;
        showMessageDialog(Color.WHITE,"Game OVER",Color.BLACK,20);
    }

    private void win(){
        isGameStopped=true;
        showMessageDialog(Color.WHITE,"You win, yaaaaaaaaay!!!",Color.BLACK,20);
               /*for(GameObject[] g:gameField){
            for (GameObject f:g){
                openTile(f.x,f.y);
            }
        }*/
    }

    private List<GameObject> getNeighbors(GameObject gameObject) {
        List<GameObject> result = new ArrayList<>();
        for (int y = gameObject.y - 1; y <= gameObject.y + 1; y++) {
            for (int x = gameObject.x - 1; x <= gameObject.x + 1; x++) {
                if (y < 0 || y >= SIDE) {
                    continue;
                }
                if (x < 0 || x >= SIDE) {
                    continue;
                }
                if (gameField[y][x] == gameObject) {
                    continue;
                }
                result.add(gameField[y][x]);
            }
        }
        return result;
    }
    private void countMineNeighbors(){
        for (int y = 0; y < SIDE; y++) {
            for (int x = 0; x < SIDE; x++) {
                if (!gameField[y][x].isMine) {
                    int minedAdjCell=0;
                    List<GameObject> minedNeighbors = getNeighbors(gameField[y][x]);
                    for (GameObject g:minedNeighbors){
                        if (g.isMine) minedAdjCell++;
                    }
                    gameField[y][x].countMineNeighbors=minedAdjCell;
                }
            }
        }
    }

    private void openTile(int x,int y){

        if (!gameField[y][x].isOpen && !gameField[y][x].isFlag){
            gameField[y][x].isOpen=true;
            setCellColor(x,y,Color.GREEN);
            countClosedTiles--;
            if (gameField[y][x].isMine){
                setCellValueEx(x, y,Color.RED, MINE);
                gameOver();
                return;
            }
            else if (gameField[y][x].countMineNeighbors == 0) {
                setCellValue(x, y,"");
                List<GameObject> neighbors = getNeighbors(gameField[y][x]);
                for (GameObject g : neighbors) {
                    openTile(g.x, g.y);
                }
            } else setCellNumber(x, y, gameField[y][x].countMineNeighbors);
                score+=5;
                setScore(score);
                if (countClosedTiles == countMinesOnField) {
                    setCellValueEx(x, y, Color.GREEN, String.valueOf(gameField[y][x].countMineNeighbors));
                    win();
                }
        }

    }



    private void markTile(int x,int y){
        if (!gameField[y][x].isOpen && !isGameStopped){
            if (!gameField[y][x].isFlag  && countFlags>0){
                gameField[y][x].isFlag=true;
                countFlags--;
                setCellValue(x,y,FLAG);
                setCellColor(x,y,Color.BLUE);
            }
            else if(gameField[y][x].isFlag){
                gameField[y][x].isFlag=false;
                countFlags++;
                setCellValue(x,y,"");
                setCellColor(x,y,Color.GREY);
            }
        }
    }

    @Override
    public void onMouseLeftClick(int x, int y) {
        super.onMouseLeftClick(x, y);
        if (isGameStopped){
            restart();
            return;
        }
        openTile(x,y);
    }

    @Override
    public void onMouseRightClick(int x, int y) {
        super.onMouseRightClick(x, y);
        markTile(x,y);

    }
}