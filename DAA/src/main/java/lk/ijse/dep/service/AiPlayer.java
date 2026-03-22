package lk.ijse.dep.service;

import java.util.*;

public class AiPlayer extends Player {

    public AiPlayer(Board board) {
        super(board);
    }

    /* =========================
       DP MEMOIZATION CACHE
    ========================= */
    private Map<String, Integer> memo = new HashMap<>();

    @Override
    public void movePiece(int col) {

        // -----------> DYNAMIC PROGRAMMING (MINIMAX) <----------- //
        col = getBestMoveMinimax();
        // ------------------------------------------------------- //

        if (board.isLegalMove(col)) {
            int row = board.findNextAvailableSpot(col);

            board.updateMove(col, row, Piece.GREEN);
            board.getBoardUI().update(col, false);

            if (board.findWinner().getWinningPiece() != Piece.EMPTY || !board.existLegalMoves()) {
                board.getBoardUI().notifyWinner(board.findWinner());
            }
        }
    }

    /* =========================
       FIND BEST MOVE (ENTRY)
    ========================= */

    public int getBestMoveMinimax() {

        int bestScore = Integer.MIN_VALUE;
        int bestCol = -1;

        BoardImpl boardImpl = (BoardImpl) board;

        for (int col = 0; col < Board.NUM_OF_COLS; col++) {

            if (board.isLegalMove(col)) {

                int row = board.findNextAvailableSpot(col);
                board.updateMove(col, row, Piece.GREEN);

                int score = minimax(boardImpl, 4, false);

                board.updateMove(col, row, Piece.EMPTY); // BACKTRACK

                if (score > bestScore) {
                    bestScore = score;
                    bestCol = col;
                }
            }
        }

        return bestCol;
    }

    /* =========================
       MINIMAX (DP + BACKTRACKING)
    ========================= */

    private int minimax(BoardImpl boardImpl, int depth, boolean maximizing) {

        Winner winner = boardImpl.findWinner();

        if (winner.getWinningPiece() == Piece.GREEN) return 100;
        if (winner.getWinningPiece() == Piece.BLUE) return -100;

        if (depth == 0 || !boardImpl.existLegalMoves()) {
            return evaluateBoard(boardImpl);
        }

        String key = boardState(boardImpl) + depth + maximizing;

        if (memo.containsKey(key)) return memo.get(key);

        int best;

        if (maximizing) {

            best = Integer.MIN_VALUE;

            for (int col = 0; col < Board.NUM_OF_COLS; col++) {

                if (boardImpl.isLegalMove(col)) {

                    int row = boardImpl.findNextAvailableSpot(col);
                    boardImpl.updateMove(col, row, Piece.GREEN);

                    int score = minimax(boardImpl, depth - 1, false);

                    boardImpl.updateMove(col, row, Piece.EMPTY); // BACKTRACK

                    best = Math.max(best, score);
                }
            }

        } else {

            best = Integer.MAX_VALUE;

            for (int col = 0; col < Board.NUM_OF_COLS; col++) {

                if (boardImpl.isLegalMove(col)) {

                    int row = boardImpl.findNextAvailableSpot(col);
                    boardImpl.updateMove(col, row, Piece.BLUE);

                    int score = minimax(boardImpl, depth - 1, true);

                    boardImpl.updateMove(col, row, Piece.EMPTY); // BACKTRACK

                    best = Math.min(best, score);
                }
            }
        }

        memo.put(key, best);
        return best;
    }

    /* =========================
       BOARD EVALUATION
    ========================= */

    private int evaluateBoard(BoardImpl boardImpl) {

        int score = 0;

        for (int c = 0; c < Board.NUM_OF_COLS; c++) {
            for (int r = 0; r < Board.NUM_OF_ROWS; r++) {

                Piece p = boardImpl.getPiece(c, r);

                if (p == Piece.GREEN) score += 2;
                if (p == Piece.BLUE) score -= 2;
            }
        }

        return score;
    }

    /* =========================
       BOARD STATE FOR DP
    ========================= */

    private String boardState(BoardImpl boardImpl) {

        StringBuilder sb = new StringBuilder();

        for (int c = 0; c < Board.NUM_OF_COLS; c++) {
            for (int r = 0; r < Board.NUM_OF_ROWS; r++) {
                sb.append(boardImpl.getPiece(c, r).ordinal());
            }
        }

        return sb.toString();
    }

    /* =========================
       HINT MOVE (Optional)
    ========================= */

    public int getBestHintMove() {

        BoardImpl boardImpl = (BoardImpl) board;

        // 1️⃣ Check if AI can win immediately
        for (int col = 0; col < Board.NUM_OF_COLS; col++) {

            if (board.isLegalMove(col)) {

                int row = board.findNextAvailableSpot(col);
                board.updateMove(col, row, Piece.BLUE);

                if (board.findWinner().getWinningPiece() == Piece.BLUE) {
                    board.updateMove(col, row, Piece.EMPTY);
                    return col;
                }

                board.updateMove(col, row, Piece.EMPTY);
            }
        }

        // 2️⃣ Block player win
        for (int col = 0; col < Board.NUM_OF_COLS; col++) {

            if (board.isLegalMove(col)) {

                int row = board.findNextAvailableSpot(col);
                board.updateMove(col, row, Piece.GREEN);

                if (board.findWinner().getWinningPiece() == Piece.GREEN) {
                    board.updateMove(col, row, Piece.EMPTY);
                    return col;
                }

                board.updateMove(col, row, Piece.EMPTY);
            }
        }

        // 3️⃣ Prefer center
        if (board.isLegalMove(3)) return 3;

        // 4️⃣ fallback
        for (int col = 0; col < Board.NUM_OF_COLS; col++) {
            if (board.isLegalMove(col)) return col;
        }

        return 0;
    }
}