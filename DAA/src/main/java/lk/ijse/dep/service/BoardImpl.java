package lk.ijse.dep.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

public class BoardImpl implements Board {

    private final Piece[][] pieces;
    private final BoardUI boardUI;

    // Variables used by MCTS
    public Piece piece;
    public int cols;

    private Stack<int[]> moveHistory = new Stack<>();

    public BoardImpl(BoardUI boardUI) {
        this.boardUI = boardUI;
        pieces = new Piece[NUM_OF_COLS][NUM_OF_ROWS];

        for (int i = 0; i < NUM_OF_COLS; i++) {
            for (int j = 0; j < NUM_OF_ROWS; j++) {
                pieces[i][j] = Piece.EMPTY;
            }
        }
    }

    @Override
    public BoardUI getBoardUI() {
        return this.boardUI;
    }

    @Override
    public int findNextAvailableSpot(int col) {
        for (int i = 0; i < NUM_OF_ROWS; i++) {
            if (pieces[col][i] == Piece.EMPTY) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public boolean isLegalMove(int col) {
        return findNextAvailableSpot(col) > -1;
    }

    @Override
    public boolean existLegalMoves() {
        for (int i = 0; i < NUM_OF_COLS; i++) {
            if (isLegalMove(i)) return true;
        }
        return false;
    }

    @Override
    public void updateMove(int col, Piece move) {

        int row = findNextAvailableSpot(col);
        if (row == -1) return;

        this.cols = col;
        this.piece = move;

        pieces[col][row] = move;

        // store move for undo
        moveHistory.push(new int[]{col, row});
    }

    @Override
    public void updateMove(int col, int row, Piece move) {
        pieces[col][row] = move;

        if (move != Piece.EMPTY) {
            moveHistory.push(new int[]{col, row});
        }
    }

    /* =======================
       DIVIDE & CONQUER START
       ======================= */

    @Override
    public Winner findWinner() {

        Winner winner;

        if ((winner = checkVertical()) != null) return winner;
        if ((winner = checkHorizontal()) != null) return winner;
        if ((winner = checkDiagonal()) != null) return winner;

        return new Winner(Piece.EMPTY);
    }

    private Winner checkVertical() {
        for (int col = 0; col < NUM_OF_COLS; col++) {
            for (int row = 0; row < NUM_OF_ROWS - 3; row++) {
                Piece p = pieces[col][row];
                if (p != Piece.EMPTY &&
                        p == pieces[col][row + 1] &&
                        p == pieces[col][row + 2] &&
                        p == pieces[col][row + 3]) {
                    return new Winner(p, col, row, col, row + 3);
                }
            }
        }
        return null;
    }

    private Winner checkHorizontal() {
        for (int col = 0; col < NUM_OF_COLS - 3; col++) {
            for (int row = 0; row < NUM_OF_ROWS; row++) {
                Piece p = pieces[col][row];
                if (p != Piece.EMPTY &&
                        p == pieces[col + 1][row] &&
                        p == pieces[col + 2][row] &&
                        p == pieces[col + 3][row]) {
                    return new Winner(p, col, row, col + 3, row);
                }
            }
        }
        return null;
    }

    private Winner checkDiagonal() {

        // \ diagonal
        for (int col = 0; col < NUM_OF_COLS - 3; col++) {
            for (int row = 0; row < NUM_OF_ROWS - 3; row++) {
                Piece p = pieces[col][row];
                if (p != Piece.EMPTY &&
                        p == pieces[col + 1][row + 1] &&
                        p == pieces[col + 2][row + 2] &&
                        p == pieces[col + 3][row + 3]) {
                    return new Winner(p, col, row, col + 3, row + 3);
                }
            }
        }

        // / diagonal
        for (int col = 0; col < NUM_OF_COLS - 3; col++) {
            for (int row = 3; row < NUM_OF_ROWS; row++) {
                Piece p = pieces[col][row];
                if (p != Piece.EMPTY &&
                        p == pieces[col + 1][row - 1] &&
                        p == pieces[col + 2][row - 2] &&
                        p == pieces[col + 3][row - 3]) {
                    return new Winner(p, col, row, col + 3, row - 3);
                }
            }
        }

        return null;
    }

    /* =======================
       DIVIDE & CONQUER END
       ======================= */


    // ======== MCTS SUPPORT ========

    public BoardImpl(Piece[][] pieces, BoardUI boardUI) {
        this.pieces = new Piece[NUM_OF_COLS][NUM_OF_ROWS];
        for (int i = 0; i < NUM_OF_COLS; i++) {
            for (int j = 0; j < NUM_OF_ROWS; j++) {
                this.pieces[i][j] = pieces[i][j];
            }
        }
        this.boardUI = boardUI;
    }

    public List<BoardImpl> getAllLegalNextMoves() {

        Piece nextPiece = piece == Piece.BLUE ? Piece.GREEN : Piece.BLUE;
        List<BoardImpl> nextMoves = new ArrayList<>();

        for (int col = 0; col < NUM_OF_COLS; col++) {
            if (findNextAvailableSpot(col) > -1) {
                BoardImpl move = new BoardImpl(this.pieces, this.boardUI);
                move.updateMove(col, nextPiece);
                nextMoves.add(move);
            }
        }
        return nextMoves;
    }

    public BoardImpl getRandomLegalNextMove() {

        List<BoardImpl> legalMoves = getAllLegalNextMoves();

        if (legalMoves.isEmpty()) return null;

        return legalMoves.get(new Random().nextInt(legalMoves.size()));
    }

    public boolean getStatus() {

        if (!existLegalMoves()) return false;

        Winner winner = findWinner();

        return winner.getWinningPiece() == Piece.EMPTY;
    }

    public Piece getPiece1(int c, int r) {
        return pieces[c][r];
    }

    public void undoLastMove() {

        if (moveHistory.isEmpty()) return;

        int[] move = moveHistory.pop();

        pieces[move[0]][move[1]] = Piece.EMPTY;
    }

    public Piece getPiece(int col, int row) {
        return pieces[col][row];
    }
}