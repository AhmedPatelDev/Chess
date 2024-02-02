package me.ix.chess.piece.pieces;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;

import me.ix.chess.board.Board;
import me.ix.chess.piece.Piece;
import me.ix.chess.piece.PieceType;

public class Rook extends Piece {

	public Rook(int x, int y, Color color) {
		super(PieceType.ROOK, x, y, color, "images/rook");
	}
	
	@Override
	public ArrayList<Point> getLegalMoves(Board b) {
	    ArrayList<Point> points = new ArrayList<Point>();

	    int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

	    for (int[] direction : directions) {
	        int x = getPos().x + direction[0];
	        int y = getPos().y + direction[1];
	        while (b.isOnBoard(x, y)) {
	            Piece newPiece = b.getPiece(x, y);
	            if (newPiece == null) {
	                points.add(new Point(x, y));
	            } else {
	                if (newPiece.getColor() != getColor()) {
	                    points.add(new Point(x, y));
	                }
	                break;
	            }
	            x += direction[0];
	            y += direction[1];
	        }
	    }
	    
	    return points;
	}
	
}
