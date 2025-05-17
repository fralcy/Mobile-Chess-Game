package com.example.chess_mobile.view.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chess_mobile.R;
import com.example.chess_mobile.model.logic.game_states.Board;
import com.example.chess_mobile.model.logic.game_states.EEndReason;
import com.example.chess_mobile.model.logic.game_states.EPlayer;
import com.example.chess_mobile.model.logic.game_states.GameState;
import com.example.chess_mobile.model.logic.game_states.Position;
import com.example.chess_mobile.model.logic.game_states.Result;
import com.example.chess_mobile.model.logic.moves.EMoveType;
import com.example.chess_mobile.model.logic.moves.Move;
import com.example.chess_mobile.model.logic.moves.PawnPromotion;
import com.example.chess_mobile.model.logic.pieces.EPieceType;
import com.example.chess_mobile.model.logic.pieces.Piece;
import com.example.chess_mobile.model.player.Player;
import com.example.chess_mobile.settings.board_color.BoardColorInstance;
import com.example.chess_mobile.utils.implementations.ChessTimer;
import com.example.chess_mobile.settings.piece_images.PieceImagesInstance;
import com.example.chess_mobile.utils.interfaces.IChessTimer;
import com.example.chess_mobile.settings.piece_images.IPieceImagesTheme;
import com.example.chess_mobile.view.activities.MainActivity;
import com.example.chess_mobile.view_model.ChessBoardViewModel;

import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

public class ChessBoardFragment extends Fragment {
    private static final String BOARD_SIZE = "boardSize";
    private static final String MAIN_PLAYER = "mainPlayer";

    // Board ui
    private boolean reversed = true;
    private int _size;
    private Player _mainPlayer;

    private ChessBoardViewModel _chessboardViewModel;
    private GridLayout _gridLayout;

    // Convert Logic & Handle Event
    IChessTimer _timer;
    private ImageView[][] _squares;
    private Position _selectedPos;
    private Move _lastMove;
    private final HashMap<Position, Move> _moveCache = new HashMap<>();

    public ChessBoardFragment() {
        // Required empty public constructor
    }

    @NonNull
    public static ChessBoardFragment newInstance(int size, Player mainPlayer) {
        ChessBoardFragment fragment = new ChessBoardFragment();
        Bundle args = new Bundle();
        args.putInt(BOARD_SIZE, size);
        args.putSerializable(MAIN_PLAYER, mainPlayer);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this._size = getArguments().getInt(BOARD_SIZE, 8);
            this._mainPlayer = (Player) getArguments().getSerializable(MAIN_PLAYER);
        }

        assert this._mainPlayer != null;
        this.reversed = this._mainPlayer.getColor() == EPlayer.BLACK;

        if (_size <= 0) {
            throw new IllegalStateException("Invalid board size: " + _size);
        }
        this._chessboardViewModel =
                new ViewModelProvider(requireActivity()).get(ChessBoardViewModel.class);
        this._squares = new ImageView[this._size][this._size];
        this._timer = new ChessTimer(1000, () -> {
            _chessboardViewModel.gameStateOnTick();
            if (_chessboardViewModel.isGameOver()) {
                onGameOver();
                _timer.stopTimer();
            }
        });
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chess_board, container, false);
        this._gridLayout = view.findViewById(R.id.chessboardGridLayoutContainer);

        if (this._chessboardViewModel.getBoard() == null) {
            throw new IllegalStateException("Game board was not initialized correctly.");
        }

        gridLayoutSetUp();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        gridLayoutSetUp();
        new Handler(Looper.getMainLooper()).postDelayed(() -> _timer.startTimer(), 800);
        showResignationDialog();
    }

    private void gridLayoutSetUp() {
        this._gridLayout.post(() -> {
            int gridWidth = this._gridLayout.getWidth();
            int gridHeight = this._gridLayout.getHeight();

            if (gridWidth == 0 || gridHeight == 0) {
                Log.e("ChessBoardFragment",
                        "GridLayout vẫn chưa được đo xong. Grid Size: Width: " + gridWidth + ", Height: " + gridHeight );
                return;
            }

            initializeBoard(this._gridLayout.getWidth(), this._gridLayout.getHeight());
            drawBoard(this._chessboardViewModel.getBoard());
        });
    }
    private void initializeBoard(int gridWidth, int gridHeight) {

        if (gridWidth == 0 || gridHeight == 0) {
            Log.e("initializeBoard", "GridLayout chưa được đo xong, hủy khởi tạo.");
            return;
        }

        int cellSize = Math.min(gridWidth, gridHeight) / this._size;

        if (this._squares == null || this._squares.length != this._size) {
            this._squares = new ImageView[this._size][this._size];
        }

        this._gridLayout.removeAllViews();
        this._gridLayout.setColumnCount(this._size);
        this._gridLayout.setRowCount(this._size);


        for (int row = 0; row < this._size; row++) {
            for (int col = 0; col < this._size; col++) {
                ImageView square = new ImageView(getContext());
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = cellSize;
                params.height = cellSize;
                params.setMargins(1, 1, 1, 1);

                square.setLayoutParams(params);
                square.setAdjustViewBounds(true);
                square.setScaleType(ImageView.ScaleType.FIT_CENTER);

                int cellRow = this.reversed ? _size - 1 - row: row;
                int cellCol = this.reversed ? _size - 1 - col: col;

                this._squares[cellRow][cellCol] = square;
                this._gridLayout.addView(square);

                boolean isWhite = (cellRow + cellCol) % 2 == 1;
                square.setBackgroundColor(BoardColorInstance.getInstance().getCellBackgroundColor(isWhite));

                square.setOnClickListener(v -> handleSquareClick(cellRow, cellCol));
            }
        }
    }

    private void drawBoard(Board board) {
        if (board == null) {
            throw new IllegalStateException("Board is null when trying to draw.");
        }

        if (this._squares == null) {
            throw new IllegalStateException("_squares array is not initialized.");
        }

        IPieceImagesTheme pImageInstance = PieceImagesInstance.getInstance();
        if (pImageInstance == null) {
            Log.d("Chess_Fragment_drawBoard", "PieceImage is null");
            throw new IllegalStateException("PieceImage is null");
        }

        for (int row = 0; row < this._size; row++) {
            for (int col = 0; col < this._size; col++) {

                if (this._squares[row][col] == null) {
                    throw new IllegalStateException("_squares[" + row + "][" + col + "] is not initialized");
                }

                Piece piece = board.getPiece(row, col);
                if (piece == null) {
                    Log.e("BOARD_FRAGMENT_INITIALIZE_BOARD", "Piece is null");
                }
                int imageId = pImageInstance.getImage(piece);

                this._squares[row][col].setScaleType(ImageView.ScaleType.FIT_CENTER);
                this._squares[row][col].setImageResource(imageId);
            }
        }
    }

    private void handleSquareClick(int row, int col) {
        showLastMoveColor();
        if (this._selectedPos == null) {
            showLastMoveColor();
            onFromPositionSelected(new Position(row, col));
        } else {
            onToPositionSelected(new Position(row, col));
            showLastMoveColor();
        }
    }

    private void cacheMoves(@NonNull List<Move> moves)
    {
        this._moveCache.clear();
        moves.forEach(move -> this._moveCache.put(move.getToPos(), move));
    }

    private void onFromPositionSelected(Position pos) {
        // Get possible moves of a piece
        List<Move> moves = this._chessboardViewModel.getLegalMovesForPiece(pos);
        if (!moves.isEmpty()) {
            this._selectedPos = pos;
            this.cacheMoves(moves);
            showHighlights();
        }
    }

    private void onToPositionSelected(Position pos) {
        this._selectedPos = null;
        hideHighlights();

        Move move = this._moveCache.get(pos);
        if (move == null) return;
        if (move.getType() == EMoveType.PAWN_PROMOTION) {
            showPromotionMenu(type -> {
                handlePromotion(move.getFromPos(), move.getToPos(), type);
            });
        } else {
            handleMove(move);
        }
    }

    private void subHandleMove(Move move) {
        this._chessboardViewModel.gameStateMakeMove(move);
        drawBoard(this._chessboardViewModel.getBoard());
    }

    private void handleMove(Move move) {
        this.subHandleMove(move);
        this._timer.startTimer();
        this._lastMove = move;
        showLastMoveColor();
        if (this._chessboardViewModel.isGameOver()) {
            this.onGameOver();
        }
    }

    private void handlePromotion(@NonNull Position fromPos, @NonNull Position toPos, EPieceType type) {
        IPieceImagesTheme pImageInstance = PieceImagesInstance.getInstance();

        this._squares[toPos.row()][toPos.column()].setImageResource(
                pImageInstance.getImage(this._chessboardViewModel.getCurrentPlayer(), EPieceType.PAWN)); // Thay ảnh
        this._squares[fromPos.row()][fromPos.column()].setImageResource(0); // Xóa ảnh
        Move promMove = new PawnPromotion(fromPos, toPos, type);
        handleMove(promMove);
    }

    private void showPromotionMenu(Consumer<EPieceType> onPieceSelected) {
        Dialog promotionDialog = new Dialog(requireContext(), R.style.Dialog_Full_Width);
        promotionDialog.setContentView(R.layout.layout_promotion_dialog);

        IPieceImagesTheme pImageInstance = PieceImagesInstance.getInstance();
        ((AppCompatImageView)promotionDialog.findViewById(R.id.promotionDialogQueen))
                .setImageResource(pImageInstance.getImage(this._chessboardViewModel.getCurrentPlayer(), EPieceType.QUEEN));
        ((AppCompatImageView)promotionDialog.findViewById(R.id.promotionDialogBishop))
                .setImageResource(pImageInstance.getImage(this._chessboardViewModel.getCurrentPlayer(), EPieceType.BISHOP));
        ((AppCompatImageView)promotionDialog.findViewById(R.id.promotionDialogRook))
                .setImageResource(pImageInstance.getImage(this._chessboardViewModel.getCurrentPlayer(), EPieceType.ROOK));
        ((AppCompatImageView)promotionDialog.findViewById(R.id.promotionDialogKnight))
                .setImageResource(pImageInstance.getImage(this._chessboardViewModel.getCurrentPlayer(), EPieceType.KNIGHT));

        promotionDialog.findViewById(R.id.promotionDialogQueen).setOnClickListener(v -> {
            onPieceSelected.accept(EPieceType.QUEEN);
            promotionDialog.dismiss();
        });

        promotionDialog.findViewById(R.id.promotionDialogBishop).setOnClickListener(v -> {
            onPieceSelected.accept(EPieceType.BISHOP);
            promotionDialog.dismiss();
        });

        promotionDialog.findViewById(R.id.promotionDialogRook).setOnClickListener(v -> {
            onPieceSelected.accept(EPieceType.ROOK);
            promotionDialog.dismiss();
        });

        promotionDialog.findViewById(R.id.promotionDialogKnight).setOnClickListener(v -> {
            onPieceSelected.accept(EPieceType.KNIGHT);
            promotionDialog.dismiss();
        });

        promotionDialog.setCanceledOnTouchOutside(true);
        promotionDialog.show();
    }

    private void showLastMoveColor() {
        if (this._lastMove == null) return;

        Position from = this._lastMove.getFromPos();
        Position to = this._lastMove.getToPos();

        this._squares[from.row()][from.column()].setBackgroundColor(BoardColorInstance.getInstance().getLastMoveCellColor());
        this._squares[to.row()][to.column()].setBackgroundColor(BoardColorInstance.getInstance().getLastMoveCellColor());
    }

    private void showHighlights() {
        hideHighlights();
        this._squares[this._selectedPos.row()][this._selectedPos.column()]
                .setBackgroundColor(BoardColorInstance.getInstance().getSelectedCellHighlightColor());
        this._moveCache.forEach((position, move)
                -> this._squares[position.row()][position.column()]
                .setBackgroundColor(BoardColorInstance.getInstance().getCellHighlightColor(move))
        );
    }

    private void hideHighlights() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                boolean isWhite = (row + col) % 2 == 1;
                this._squares[row][col].setBackgroundColor(BoardColorInstance.getInstance().getCellBackgroundColor(isWhite));
            }
        }
    }

    private void onGameOver() {
        this._timer.finishTimer();
        GameState currentGame = this._chessboardViewModel.getGameState().getValue();
        String endGameMessage = "";
        if (currentGame != null)
            endGameMessage += currentGame.getResult();
        Toast.makeText(requireContext(), endGameMessage, Toast.LENGTH_LONG).show();
        backToMenu();
    }

    private void backToMenu() {
        startActivity(new Intent(requireContext(), MainActivity.class));
        requireActivity().finish();
    }

    public void showDrawOfferDialog() {
        Dialog dialog = new Dialog(requireContext(), R.style.Dialog_Full_Width);
        dialog.setContentView(R.layout.layout_confirmation_dialog);
        ((TextView)dialog.findViewById(R.id.dialogMessage)).setText(R.string.draw_offer_message);
        dialog.findViewById(R.id.buttonYes).setOnClickListener(l -> this._chessboardViewModel.setResult(Result.draw(EEndReason.STALEMATE)));
        dialog.findViewById(R.id.buttonNo).setOnClickListener(l -> dialog.dismiss());
        dialog.show();

    }

    public void showResignationDialog() {
        Dialog dialog = new Dialog(requireContext(), R.style.Dialog_Full_Width);
        dialog.setContentView(R.layout.layout_confirmation_dialog);
        ((TextView)dialog.findViewById(R.id.dialogMessage)).setText(R.string.resignation_message);
        dialog.findViewById(R.id.buttonYes).setOnClickListener(l -> this._chessboardViewModel
                .setResult(Result.win(this._chessboardViewModel.getOpponentPlayer().getColor(), EEndReason.RESIGNATION)));
        dialog.findViewById(R.id.buttonNo).setOnClickListener(l -> dialog.dismiss());
        dialog.show();
    }
}