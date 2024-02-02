package me.ix.chess.piece;

import java.awt.Point;
import java.util.ArrayList;

import me.ix.chess.board.Board;

public interface PieceInterface {

	public ArrayList<Point> getLegalMoves(Board b);
	public ArrayList<Point> getCheckedLegalMoves(Board b);
	
}
