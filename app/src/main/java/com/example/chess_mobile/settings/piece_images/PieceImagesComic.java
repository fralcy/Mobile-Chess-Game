package com.example.chess_mobile.settings.piece_images;

import com.example.chess_mobile.R;
import com.example.chess_mobile.model.logic.pieces.EPieceType;

public class PieceImagesComic extends PieceImagesAbstract{
    public PieceImagesComic() {
        super();
        this.addBlackPiece(EPieceType.ROOK, R.drawable.asset_piece_comic_b_rook);
        this.addBlackPiece(EPieceType.KNIGHT, R.drawable.asset_piece_comic_b_knight);
        this.addBlackPiece(EPieceType.BISHOP, R.drawable.asset_piece_comic_b_bishop);
        this.addBlackPiece(EPieceType.QUEEN, R.drawable.asset_piece_comic_b_queen);
        this.addBlackPiece(EPieceType.KING, R.drawable.asset_piece_comic_b_king);
        this.addBlackPiece(EPieceType.PAWN, R.drawable.asset_piece_comic_b_pawn);

        this.addWhitePiece(EPieceType.ROOK, R.drawable.asset_piece_comic_w_rook);
        this.addWhitePiece(EPieceType.KNIGHT, R.drawable.asset_piece_comic_w_knight);
        this.addWhitePiece(EPieceType.BISHOP, R.drawable.asset_piece_comic_w_bishop);
        this.addWhitePiece(EPieceType.QUEEN, R.drawable.asset_piece_comic_w_queen);
        this.addWhitePiece(EPieceType.KING, R.drawable.asset_piece_comic_w_king);
        this.addWhitePiece(EPieceType.PAWN, R.drawable.asset_piece_comic_w_pawn);
    }
}
