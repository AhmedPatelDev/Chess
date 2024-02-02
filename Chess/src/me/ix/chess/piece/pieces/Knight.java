package me.ix.chess.piece.pieces;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;

import me.ix.chess.board.Board;
import me.ix.chess.piece.Piece;
import me.ix.chess.piece.PieceType;

public class Knight extends Piece {

	public Knight(int x, int y, Color color) {
		super(PieceType.KNIGHT, x, y, color, "images/knight");
	}

	@Override
	public ArrayList<Point> getLegalMoves(Board b) {
	    ArrayList<Point> points = new ArrayList<Point>();

	    int[][] moves = {{1, 2}, {2, 1}, {2, -1}, {1, -2}, {-1, -2}, {-2, -1}, {-2, 1}, {-1, 2}};

	    for (int[] move : moves) {
	        int x = getPos().x + move[0];
	        int y = getPos().y + move[1];
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
