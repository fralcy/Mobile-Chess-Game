package com.example.chess_mobile.view.fragments;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;

import com.example.chess_mobile.R;
import com.example.chess_mobile.model.game_states.Board;
import com.example.chess_mobile.model.game_states.EEndReason;
import com.example.chess_mobile.model.game_states.EPlayer;
import com.example.chess_mobile.model.game_states.GameState;
import com.example.chess_mobile.model.game_states.Position;
import com.example.chess_mobile.model.game_states.Result;
import com.example.chess_mobile.model.moves.EMoveType;
import com.example.chess_mobile.model.moves.Move;
import com.example.chess_mobile.model.moves.PawnPromotion;
import com.example.chess_mobile.model.pieces.EPieceType;
import com.example.chess_mobile.model.pieces.Piece;
import com.example.chess_mobile.utils.implementations.ChessTimer;
import com.example.chess_mobile.utils.implementations.PieceImages;
import com.example.chess_mobile.utils.interfaces.IChessTimer;
import com.example.chess_mobile.utils.interfaces.ITimerCallback;

import java.util.HashMap;
import java.util.List;

public class ChessBoardFragment extends Fragment {
    private static final String BOARD_SIZE = "boardSize";
    private GridLayout _gridLayout;
    private int _size;

    // Convert
    IChessTimer _timer = new ChessTimer(1000, new ITimerCallback() {
        @Override
        public void onTick() {
             if(_gameState == null) return;

             if(_gameState.getWhiteTimer().isZero()) {
                 _gameState.setResult(Result.win(EPlayer.BLACK, EEndReason.TIMEOUT));
                 onGameOver();
             } else if (_gameState.getBlackTimer().isZero()) {
                 _gameState.setResult(Result.win(EPlayer.WHITE, EEndReason.TIMEOUT));
                 onGameOver();
             } else {
                 _gameState.timerTick();
             }
        }

        @Override
        public void onFinish() {

        }
    });

    private ImageView[][] _squares;
    private GameState _gameState;
    private Board _board;
    private Position _selectedPos;
    private HashMap<Position, Move> _moveCache;

    public ChessBoardFragment() {
        // Required empty public constructor
    }

    public static ChessBoardFragment newInstance(int size) {
        ChessBoardFragment fragment = new ChessBoardFragment();
        Bundle args = new Bundle();
        args.putInt(BOARD_SIZE, size);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            _size = getArguments().getInt(BOARD_SIZE, 8);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chess_board, container, false);
        _gridLayout = view.findViewById(R.id.chessboardGridLayoutContainer);


        if (_size <= 0) {
            throw new IllegalStateException("Invalid board size: " + _size);
        }

        // Khởi tạo mảng ô cờ trước khi sử dụng
        this._squares = new ImageView[this._size][this._size];

        // Khởi tạo trạng thái trò chơi
        this._board = new Board();
        this._gameState = new GameState(EPlayer.WHITE, this._board.initial());

        if (this._gameState.getBoard() == null) {
            throw new IllegalStateException("Game board was not initialized correctly.");
        }

        // Khởi tạo bàn cờ và vẽ sau khi đảm bảo GridLayout đã đo xong
        _gridLayout.post(() -> {
            initializeBoard();
            drawBoard(this._gameState.getBoard());
        });

        this._timer.startTimer();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (_gridLayout.getChildCount() == 0) { // Kiểm tra xem có ô cờ nào chưa
            initializeBoard();
            drawBoard(this._gameState.getBoard());
        }
    }

    private void initializeBoard() {
        if (_gridLayout == null) {
            throw new IllegalStateException("GridLayout is not initialized.");
        }

        _gridLayout.removeAllViews();
        _gridLayout.setColumnCount(this._size);
        _gridLayout.setRowCount(this._size);

        Log.d("BOARD_FRAGMENT", "GRID COLS:" + this._gridLayout.getColumnCount() + "GRID ROWS:" + this._gridLayout.getRowCount());

        int gridWidth = _gridLayout.getWidth();
        int gridHeight = _gridLayout.getHeight();
        int cellSize = Math.min(gridWidth, gridHeight) / this._size;

        for (int row = 0; row < this._size; row++) {
            for (int col = 0; col < this._size; col++) {
                ImageView square = new ImageView(getContext());
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = cellSize;
                params.height = cellSize;
                params.setMargins(1, 1, 1, 1);

                square.setLayoutParams(params);
                square.setAdjustViewBounds(true);
                square.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                square.setBackgroundColor((row + col) % 2 == 0 ? Color.WHITE : Color.BLACK);

                this._squares[row][col] = square;
                this._gridLayout.addView(square);

                final int finalRow = row, finalCol = col;
                square.setOnClickListener(v -> handleSquareClick(finalRow, finalCol));
            }
        }
    }

    private void drawBoard(Board board) {
        if (board == null) {
            throw new IllegalStateException("Board is null when trying to draw.");
        }

        PieceImages pImageInstance = PieceImages.getInstance();
        if (pImageInstance == null) {
            throw new IllegalStateException("PieceImage is null");
        }

        for (int row = 0; row < this._size; row++) {
            for (int col = 0; col < this._size; col++) {
                if (this._squares[row][col] == null) {
                    throw new IllegalStateException("_squares[" + row + "][" + col + "] is not initialized");
                }

                Piece piece = board.getPiece(row, col);
                int imageId = pImageInstance.getImage(piece);
                this._squares[row][col].setImageResource(imageId);
            }
        }
    }

    private void handleSquareClick(int row, int col) {
        Position pos = new Position(row, col);
        if (this._selectedPos == null)
        {
            onFromPositionSelected(pos);
        }
        else
        {
            onToPositionSelected(pos);
        }

    }

    private void onFromPositionSelected(Position pos) {
        // Get possible moves of a piece
        List<Move> moves = this._gameState.getLegalMovesForPiece(pos);
        if (!moves.isEmpty()) {
            // Chọn quân cờ đã chỉ định
            this._selectedPos = pos;
            // Lưu trữ các nước đi có thể
            this.cacheMoves(moves);
            // Hiển thị các nước đi có thể
            showHighlights();
        }
    }

    private void cacheMoves(List<Move> moves)
    {
        this._moveCache.clear();
        moves.forEach(move -> _moveCache.put(move.getToPos(), move));
    }

    private void onToPositionSelected(Position pos) {
        this._selectedPos = null;
        hideHighlights();

        Move move = this._moveCache.get(pos);
        if (move != null)
        {
            if (move.getType() == EMoveType.PAWN_PROMOTION)
            {
                handlePromotion(move.getFromPos(), move.getToPos());
            }
            else
            {
                handleMove(move);
            }
        }
    }

    private void subHandleMove(Move move) {
        this._gameState.makeMove(move);
        drawBoard(this._gameState.getBoard());
    }

    private void handleMove(Move move) {
        this.subHandleMove(move);
        this._timer.startTimer();


        if (this._gameState.isGameOver())
        {
            this.onGameOver();
        }
    }

    private void handlePromotion(Position fromPos, Position toPos) {
        this._squares[toPos.row()][toPos.column()].setImageResource(
                PieceImages.getInstance().getImage(this._gameState.getCurrentPlayer(), EPieceType.PAWN)); // Thay ảnh
        this._squares[fromPos.row()][fromPos.column()].setImageResource(0); // Xóa ảnh

        EPieceType type = this.showPromotionMenu();
        Move promMove = new PawnPromotion(fromPos, toPos, type);
        handleMove(promMove);
    }

    private EPieceType showPromotionMenu() {
        return EPieceType.QUEEN;
    }

    private void showHighlights() {
        hideHighlights();
        this._moveCache.forEach((position, move) -> {
            this._squares[position.row()][position.column()].setBackgroundColor(Color.GREEN);
        });
    }

    private void hideHighlights() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                boolean isWhite = (row + col) % 2 == 0;
                this._squares[row][col].setBackgroundColor(isWhite ? Color.WHITE : Color.BLACK);
            }
        }
    }

    private void onGameOver() {
        this._timer.finishTimer();
    }
}