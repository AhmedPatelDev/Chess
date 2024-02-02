package me.ix.chess.piece.pieces;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;

import me.ix.chess.board.Board;
import me.ix.chess.piece.Piece;
import me.ix.chess.piece.PieceType;

public class Pawn extends Piece {

	public boolean hasDoneFirst = false;
	
	public Pawn(int x, int y, Color color) {
		super(PieceType.PAWN, x, y, color, "images/pawn");
	}
	
	@Override
	public ArrayList<Point> getLegalMoves(Board b) {
	    ArrayList<Point> points = new ArrayList<Point>();

	    int x = getPos().x;
	    int y = getPos().y;
	    int direction = getColor() == Color.WHITE ? -1 : 1;

	    // move forward one square
	    if (b.isOnBoard(x, y + direction) && b.getPiece(x, y + direction) == null) {
	        points.add(new Point(x, y + direction));

	        // move forward two squares on first move
	        if (!this.hasDoneFirst && b.getPiece(x, y + 2 * direction) == null) {
	            points.add(new Point(x, y + 2 * direction));
	        }
	    }

	    // capture diagonally
	    if (b.isOnBoard(x + 1, y + direction) && b.getPiece(x + 1, y + direction) != null) {
	    	if(b.getPiece(x + 1, y + direction).getColor() != this.getColor()) {
	    		points.add(new Point(x + 1, y + direction));
	    	}
	    }
	    if (b.isOnBoard(x - 1, y + direction) && b.getPiece(x - 1, y + direction) != null) {
	    	if(b.getPiece(x - 1, y + direction).getColor() != this.getColor()) {
	    		points.add(new Point(x - 1, y + direction));
	    	}
	    }

	    
	    return points;
	}
}
