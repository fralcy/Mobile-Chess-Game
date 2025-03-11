package chess.logic;

public class StateString {
    private final StringBuilder sb = new StringBuilder();
    
    public StateString(Player currentPlayer, Board board) {
        addPiecePlacement(board);
        sb.append(' ');
        addCurrentPlayer(currentPlayer);
        sb.append(' ');
        addCastlingRights(board);
        sb.append(' ');
        addEnPassant(board, currentPlayer);
    }
    
    @Override
    public String toString() {
        return sb.toString();
    }
    
    private static char getPieceChar(Piece piece) {
        char c;
        switch (piece.getType()) {
            case PAWN: c = 'p'; break;
            case KNIGHT: c = 'n'; break;
            case BISHOP: c = 'b'; break;
            case ROOK: c = 'r'; break;
            case QUEEN: c = 'q'; break;
            case KING: c = 'k'; break;
            default: c = ' '; break;
        }
        
        if (piece.getColor() == Player.WHITE) {
            return Character.toUpperCase(c);
        }
        
        return c;
    }
    
    private void addRowData(Board board, int row) {
        int empty = 0;
        
        for (int c = 0; c < 8; c++) {
            if (board.getPiece(row, c) == null) {
                empty++;
                continue;
            }
            
            if (empty > 0) {
                sb.append(empty);
                empty = 0;
            }
            
            sb.append(getPieceChar(board.getPiece(row, c)));
        }
        
        if (empty > 0) {
            sb.append(empty);
        }
    }
    
    private void addPiecePlacement(Board board) {
        for (int r = 0; r < 8; r++) {
            if (r != 0) {
                sb.append('/');
            }
            addRowData(board, r);
        }
    }
    
    private void addCurrentPlayer(Player currentPlayer) {
        if (currentPlayer == Player.WHITE) {
            sb.append('w');
        } else {
            sb.append('b');
        }
    }
    
    private void addCastlingRights(Board board) {
        boolean castleWKS = board.canCastleKingSide(Player.WHITE);
        boolean castleWQS = board.canCastleQueenSide(Player.WHITE);
        boolean castleBKS = board.canCastleKingSide(Player.BLACK);
        boolean castleBQS = board.canCastleQueenSide(Player.BLACK);
        
        if (!(castleWKS || castleWQS || castleBKS || castleBQS)) {
            sb.append('-');
            return;
        }
        
        if (castleWKS) {
            sb.append('K');
        }
        if (castleWQS) {
            sb.append('Q');
        }
        if (castleBKS) {
            sb.append('k');
        }
        if (castleBQS) {
            sb.append('q');
        }
    }
    
    private void addEnPassant(Board board, Player currentPlayer) {
        if (!board.canCaptureEnPassant(currentPlayer)) {
            sb.append('-');
            return;
        }
        
        Position pos = board.getPawnSkipPosition(currentPlayer.getOpponent());
        char file = (char)('a' + pos.getColumn());
        int rank = 8 - pos.getRow();
        sb.append(file);
        sb.append(rank);
    }
}