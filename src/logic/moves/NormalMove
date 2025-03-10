package chess.logic.moves;

import chess.logic.Board;
import chess.logic.Player;
import chess.logic.Position;

public abstract class Move {
    public abstract MoveType getType();
    public abstract Position getFromPos();
    public abstract Position getToPos();
    
    public boolean execute(Board board) {
        return false;
    }
    
    public boolean isLegal(Board board) {
        Player player = board.getPiece(getFromPos()).getColor();
        Board boardCopy = board.copy();
        execute(boardCopy);
        return !boardCopy.isInCheck(player);
    }
}