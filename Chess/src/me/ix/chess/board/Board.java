package me.ix.chess.board;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;

import me.ix.chess.audio.AudioHandler;
import me.ix.chess.piece.Piece;
import me.ix.chess.piece.PieceType;
import me.ix.chess.piece.pieces.Bishop;
import me.ix.chess.piece.pieces.King;
import me.ix.chess.piece.pieces.Knight;
import me.ix.chess.piece.pieces.Pawn;
import me.ix.chess.piece.pieces.Queen;
import me.ix.chess.piece.pieces.Rook;

public class Board {

	public int width;
	public int height;

	public ArrayList<Piece> pieces = new ArrayList<Piece>();

	private AudioHandler audioHandler;
	
	public Board(int width, int height, String fen, AudioHandler audioHandler) {
		this.width = width;
		this.height = height;
		this.audioHandler = audioHandler;
		
		setupFen(fen);
	}

	public void setupFen(String fen) {
		HashMap<Character, TriConsumer<Integer, Integer, Color>> pieceSymbol = new HashMap<>();
		pieceSymbol.put('k', (x, y, color) -> pieces.add(new King(x, y, color)));
		pieceSymbol.put('p', (x, y, color) -> pieces.add(new Pawn(x, y, color)));
		pieceSymbol.put('n', (x, y, color) -> pieces.add(new Knight(x, y, color)));
		pieceSymbol.put('b', (x, y, color) -> pieces.add(new Bishop(x, y, color)));
		pieceSymbol.put('r', (x, y, color) -> pieces.add(new Rook(x, y, color)));
		pieceSymbol.put('q', (x, y, color) -> pieces.add(new Queen(x, y, color)));

		String setup = fen.split(" ")[0];
		
		int x = 0;
		int y = 0;

		for (char c : setup.toCharArray()) {
		    if (c == '/') {
		        y++;
		        x = 0;
		    } else if (Character.isDigit(c)) {
		        x += Character.getNumericValue(c);
		    } else {
		        Color color = Character.isLowerCase(c) ? Color.BLACK : Color.WHITE;
		        pieceSymbol.get(Character.toLowerCase(c)).accept(x, y, color);
		        x++;
		    }
		}
	}
	
	public String genFen() {
	    StringBuilder sb = new StringBuilder();
	    for (int y = 0; y < 8; y++) {
	        int emptySquares = 0;
	        for (int x = 0; x < 8; x++) {
	            Piece p = this.getPiece(x, y);
	            if (p == null) {
	                emptySquares++;
	            } else {
	                if (emptySquares > 0) {
	                    sb.append(emptySquares);
	                    emptySquares = 0;
	                }
	                sb.append(getPieceChar(p.getType(), p.getColor()));
	            }
	        }
	        if (emptySquares > 0) {
	            sb.append(emptySquares);
	        }
	        if (y < 7) {
	            sb.append("/");
	        }
	    }
	    return sb.toString();
	}

	public Piece getPiece(int x, int y) {
		for(Piece piece : this.pieces) {
			if(piece.getPos().x == x && piece.getPos().y == y) {
				return piece;
			}
		}
		
		return null;
	}
	
	public ArrayList<Piece> getPieces(Color color){
		ArrayList<Piece> colorPieces = new ArrayList<Piece>();
		
		for(Piece piece : this.pieces) {
			if(piece.getColor() == color) {
				colorPieces.add(piece);
			}
		}
		
		return colorPieces;
	}

	public King getKing(Color col) {
		for(Piece piece : pieces) {
			if(piece.getType() == PieceType.KING && piece.getColor() == col) {
				return (King) piece;
			}
		}
		
		return null;
	}
	
	private char getPieceChar(PieceType type, Color color) {
		HashMap<PieceType, Character> pieceChars = new HashMap<PieceType, Character>();
		pieceChars.put(PieceType.PAWN, 'p');
		pieceChars.put(PieceType.KING, 'k');
		pieceChars.put(PieceType.QUEEN, 'q');
		pieceChars.put(PieceType.ROOK, 'r');
		pieceChars.put(PieceType.BISHOP, 'b');
		pieceChars.put(PieceType.KNIGHT, 'n');
		
	    char pieceChar = pieceChars.get(type);
	    
	    return color == Color.WHITE ? Character.toUpperCase(pieceChar) : pieceChar;
	}

	public void render(Graphics g) {
		int squareWidth = width / 8;
		int squareHeight = height / 8;

		Color light = new Color(240, 218, 178, 255);
		Color dark = new Color(183, 137, 96, 255);

		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				boolean isLightSquare = (x + y) % 2 != 0;
				g.setColor(isLightSquare ? light : dark);
				g.fillRect(x * squareWidth, y * squareWidth, squareWidth, squareHeight);
			}
		}

		for (Piece piece : pieces) {
			if (piece.isVisible()) {
				g.drawImage(piece.getImage(), piece.getPos().x * squareWidth, piece.getPos().y * squareHeight, null);
			}
		}
	}
	
	public void handlePieceMove(Piece piece, Point pos) {
		Piece enemyPiece = this.getPiece(pos.x, pos.y);
		
		if(enemyPiece != null) {
			enemyPiece.setPos(new Point(99, 99));
			this.pieces.remove(enemyPiece);
		}
		
		if(piece instanceof Pawn) {
			Pawn pawn = (Pawn) piece;
			pawn.hasDoneFirst = true;
		}
		
		if(audioHandler != null) {
			audioHandler.playAudio("move");
		}
		
		piece.setPos(pos);
		piece.setVisible(true);
	}
	
	public boolean isOnBoard(int x, int y) {
	    return x >= 0 && x < 8 && y >= 0 && y < 8;
	}
	
}
