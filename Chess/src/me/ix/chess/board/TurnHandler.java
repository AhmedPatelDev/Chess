package me.ix.chess.board;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;

import me.ix.chess.piece.Piece;

public class TurnHandler {

	private Board board;
	
	private HashMap<String, ArrayList<Point>> pieceMoves = new HashMap<String, ArrayList<Point>>();
	
	private Color turn = Color.WHITE;
	
	public TurnHandler(Board board) {
		this.board = board;
	}
	
	public void generateMoves() {
		// Threading works to eliminate visual lag
		// But there is a delay between a move being made and available moves being shown.
		
		String fen = board.genFen();
		
		HashMap<String, ArrayList<Point>> tempPieceMoves = new HashMap<String, ArrayList<Point>>();
		
		for(Piece piece : board.pieces) {
			ArrayList<Point> moves = piece.getCheckedLegalMoves(board);
			tempPieceMoves.put(genKey(piece), moves);
		}
		
		pieceMoves.putAll(tempPieceMoves);
		
		Runnable myThread = () -> { };
        Thread run = new Thread(myThread);
        run.start();
	}
	
	public ArrayList<Point> getMoves(Piece piece) {
		return pieceMoves.getOrDefault(genKey(piece), new ArrayList<Point>());
	}
	
	public void endTurn() {
		this.turn = this.turn == Color.WHITE ? Color.BLACK : Color.WHITE;
		generateMoves();
	}
	
	public String genKey(Piece p) {
		return board.genFen() + p.getType().name() + p.getColor() + p.getPos().x + p.getPos().y;
	}
	
	public Color getTurn() {
		return turn;
	}
}
