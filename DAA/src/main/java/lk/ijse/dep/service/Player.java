package lk.ijse.dep.service;

public abstract class Player {
    protected Board board;

    public Player(Board board) {
        this.board = board;
    }

    public abstract void movePiece(int col);

	public BoardImpl getBoard() {
		// TODO Auto-generated method stub
		return null;
	}
}



/*
 * Player is considered to be an abstract class
 * Because class's behavoiurs are implemented on another classes
 * (On AiPLayer and HumanPlayer sub-Classes)
 */