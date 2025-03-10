package chess.logic;

import java.io.Serializable;
import java.util.Objects;

public class Position implements Serializable {
    private final int row;
    private final int column;
    
    public Position(int row, int column) {
        this.row = row;
        this.column = column;
    }
    
    public int getRow() {
        return row;
    }
    
    public int getColumn() {
        return column;
    }
    
    public Player getSquareColor() {
        if ((row + column) % 2 == 0) {
            return Player.WHITE;
        }
        return Player.BLACK;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Position position = (Position) obj;
        return row == position.row && column == position.column;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(row, column);
    }
    
    public Position plus(Direction dir) {
        return new Position(row + dir.getRowDelta(), column + dir.getColumnDelta());
    }
}