package me.ix.chess;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

import me.ix.chess.audio.AudioHandler;
import me.ix.chess.board.Board;
import me.ix.chess.board.TurnHandler;
import me.ix.chess.piece.Piece;
import me.ix.chess.piece.PieceType;
import me.ix.chess.piece.pieces.King;

public class Game extends Canvas implements Runnable, MouseListener {

	private static final long serialVersionUID = 1185828835232854456L;

	public static final int WIDTH = 1000;
	public static final int HEIGHT = 1000;
	
	public static final double SCALE = 0.8;
	public static final int SCALED_WIDTH = (int) (WIDTH * SCALE);
	public static final int SCALED_HEIGHT = (int) (HEIGHT * SCALE);
	
	private Thread thread;
	private boolean running = false;
	private int fps = 0;
	
	private Board board;
	private Piece focusedPiece;
	private TurnHandler turnHandler;
	private AudioHandler audioHandler;

	public Game() {
		new Window(SCALED_WIDTH, SCALED_HEIGHT, "Chess", this);
		
		audioHandler = new AudioHandler("audio");
		audioHandler.playAudio("start");
		
		board = new Board(SCALED_WIDTH, SCALED_HEIGHT, "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1", audioHandler);
		turnHandler = new TurnHandler(board);
		turnHandler.generateMoves();
		
		this.addMouseListener(this);
	}
	
	public synchronized void start() {
		thread = new Thread(this);
		thread.start();
		running = true;
	}
	
	public synchronized void stop() {
		try {
			thread.join();
			running = false;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		long lastTime = System.nanoTime();
		double amountOfTicks = 128.0;
		double ns = 1000000000 / amountOfTicks;
		double delta = 0;
		long timer = System.currentTimeMillis();
		int frames = 0;
		
		while(running) {
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			while(delta >= 1) {
				tick();
				delta--;
			}
			if(running) {
				render();
			}
			frames++;
			
			if((System.currentTimeMillis() - timer) > 1000) {
				timer += 1000;
				this.fps = frames;
				frames = 0;
			}
		}
		stop();
	}
	
	private void handleAI(Color controlColor) {
		if(turnHandler.getTurn() == controlColor) {
			Random r = new Random();
			
			ArrayList<Point> moves = new ArrayList<Point>();
			
			Piece p = null;
			
			while(moves.size() == 0) {
				ArrayList<Piece> controlPieces = new ArrayList<Piece>();
				
				for(Piece tempPiece : board.pieces) {
					if(tempPiece.getColor() == controlColor) {
						controlPieces.add(tempPiece);
					}
				}
				
				p = controlPieces.get(r.nextInt(controlPieces.size()));
				
				moves = p.getCheckedLegalMoves(board);
			}
			
			Point newPos = moves.get(r.nextInt(moves.size()));
			
			board.handlePieceMove(p, newPos);
			
			turnHandler.endTurn();
		}
	}
	
	private void isKingInCheck() {
		for(Piece piece : board.pieces) {
			if(piece.getType() == PieceType.KING) {
				King king = (King) piece;
				
				if(king.isInCheck(board)) {
					System.out.println((piece.getColor() == Color.WHITE ? "White" : "Black") + " King is in check!");
				}
			}
		}
	}
	
	private boolean isStaleMate(Color col, Board b) {
		for(Piece p : b.pieces) {
			if(p.getColor() == col && p.getCheckedLegalMoves(b).size() >= 1) {
				return false;
			}
		}
		
		return true;
	}
	
	public boolean isMate(Board b, Color color) {
	    // Check if the player is in check
	    King king = b.getKing(color);
	    if (!king.isInCheck(b)) {
	        return false;
	    }

	    // Check if the player has any legal moves that would get them out of check
	    for (Piece p : b.getPieces(color)) {
	        ArrayList<Point> legalMoves = p.getLegalMoves(b);
	        for (Point move : legalMoves) {
	            Point oldPos = p.getPos();
	            p.setPos(move);
	            if (!king.isInCheck(b)) {
	                p.setPos(oldPos);
	                return false;
	            }
	            p.setPos(oldPos);
	        }
	    }

	    // If no legal moves would get the player out of check, then the game is a mate
	    return true;
	}
	
	private void tick() {
		if(board != null) {
			handleAI(Color.BLACK);
		}
	}
	
	private void render() {
		BufferStrategy bs = this.getBufferStrategy();
		if(bs == null) {
			this.createBufferStrategy(3);
			return;
		}
		
		Graphics g = bs.getDrawGraphics();
		
		// Remove Flashing
		g.setColor(Color.black);
		g.fillRect(0, 0, SCALED_WIDTH, SCALED_HEIGHT);
		
		// Render Board
		if(board != null) {
			board.render(g);
		}
		
		// Render Drag Piece
		if(this.getMousePosition() != null && focusedPiece != null) {
			BufferedImage img = focusedPiece.getImage();
			int mousePosX = this.getMousePosition().x;
			int mousePosY = this.getMousePosition().y;
			
			g.drawImage(img, mousePosX - (img.getWidth()/2), mousePosY - (img.getHeight()/2), null);
		}
		
		// Render Piece Moves
		if(focusedPiece != null) {
			for(Point pos : turnHandler.getMoves(focusedPiece)) {
				g.setColor(Color.green);
				
				int squareWidth = SCALED_WIDTH / 8;
				int squareHeight = SCALED_HEIGHT / 8;
				
				g.fillOval((pos.x * squareWidth) + (squareWidth / 2) - 13, (pos.y * squareHeight) + (squareHeight / 2) - 13, 26, 26);
			}
		}
		
		// Render FPS
		g.setColor(Color.red);
		g.drawString("FPS: " + this.fps, 10, 20);
		
		g.dispose();
		bs.show();
	}
	
	public static void main(String[] args) {
		new Game();
	}
	
	public void mousePressed(MouseEvent e) {
		int squareWidth = SCALED_WIDTH / 8;
		int squareHeight = SCALED_HEIGHT / 8;
		
		int x = (e.getX() / squareWidth);
		int y = (e.getY() / squareHeight);
		
		// Focus clicked piece
		for(Piece piece : this.board.pieces) {
			if(piece.getPos().x == x && piece.getPos().y == y && piece.getColor() == turnHandler.getTurn()) {
				this.focusedPiece = piece;
				focusedPiece.setVisible(false);
			}
		}
	}

	public void mouseReleased(MouseEvent e) {
		if(focusedPiece == null) {
			return;
		}
		
		int squareWidth = SCALED_WIDTH / 8;
		int squareHeight = SCALED_HEIGHT / 8;
		
		int newPosX = (e.getX() / squareWidth);
		int newPosY = (e.getY() / squareHeight);
		
		Point newPos = new Point(newPosX, newPosY);
		
		ArrayList<Point> moves = turnHandler.getMoves(focusedPiece);
		
		// Check if no moves are available, if so, return back to normal.
		if(moves == null) {
			focusedPiece.setVisible(true);
			focusedPiece = null;
			return;
		}
		
		// Check if the move wasnt a move, if so, return back to normal.
		if(newPos.x == focusedPiece.getPos().x && newPos.y == focusedPiece.getPos().y) {
			focusedPiece.setVisible(true);
			focusedPiece = null;
			return;
		}
		
		// Check if move exists, if so, execute the move.
		for(Point point : moves) {
			if(point.x == newPos.x && point.y == newPos.y) {
				board.handlePieceMove(focusedPiece, newPos);
				focusedPiece = null;
				turnHandler.endTurn();
			}
		}
	}

	public void mouseClicked(MouseEvent e) {
		
	}
	
	public void mouseEntered(MouseEvent e) {
		
	}

	public void mouseExited(MouseEvent e) {
		
	}
}
