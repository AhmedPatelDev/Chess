package me.ix.chess.piece.pieces;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;

import me.ix.chess.board.Board;
import me.ix.chess.piece.Piece;
import me.ix.chess.piece.PieceType;

public class King extends Piece {

	public King(int x, int y, Color color) {
		super(PieceType.KING, x, y, color, "images/king");
	}

	public boolean isInCheck(Board b) {
	    for (Piece p : b.pieces) {
	        if (p.getColor() != getColor()) {
	            ArrayList<Point> legalMoves = p.getLegalMoves(b);
	            if (legalMoves.contains(getPos())) {
	                return true;
	            }
	        }
	    }
	    return false;
	}
	
	@Override
	public ArrayList<Point> getLegalMoves(Board b) {
	    ArrayList<Point> points = new ArrayList<Point>();

	    int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {-1, -1}, {1, -1}, {-1, 1}};

	    for (int[] direction : directions) {
	        int x = getPos().x + direction[0];
	        int y = getPos().y + direction[1];
	        if (b.isOnBoard(x, y)) {
	            Piece newPiece = b.getPiece(x, y);
	            if (newPiece == null || newPiece.getColor() != getColor()) {
	                points.add(new Point(x, y));
	            }
	        }
	    }
	    
	    return points;
	}
}
