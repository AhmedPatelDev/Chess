package me.ix.chess.piece;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import me.ix.chess.Game;
import me.ix.chess.board.Board;
import me.ix.chess.piece.pieces.King;

public class Piece implements PieceInterface {

	private PieceType type;
	private Point pos;
	private Color color;
	private String imageLocation;
	private BufferedImage image;
	
	private boolean visible = true;
	
	public Piece(PieceType type, int x, int y, Color color, String imageLocation) {
		this.type = type;
		this.pos = new Point(x, y);
		this.color = color;
		this.imageLocation = imageLocation;
		
		try {
			BufferedImage tempImage = ImageIO.read(new File(this.getImageLocation() + (this.getColor() == Color.WHITE ? "white" : "black") + ".png"));
			this.image = resize(tempImage, (int) ((tempImage.getWidth()*2) * Game.SCALE), (int) ((tempImage.getHeight()*2) * Game.SCALE));	
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static BufferedImage resize(BufferedImage img, int newW, int newH) { 
	    Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
	    BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

	    Graphics2D g2d = dimg.createGraphics();
	    g2d.drawImage(tmp, 0, 0, null);
	    g2d.dispose();

	    return dimg;
	}  
	
	@Override
	public ArrayList<Point> getLegalMoves(Board b) {
		ArrayList<Point> points = new ArrayList<Point>();
		return points;
	}
	
	@Override
	public ArrayList<Point> getCheckedLegalMoves(Board b) {
		ArrayList<Point> points = this.getLegalMoves(b);
		ArrayList<Point> checkedPoints = new ArrayList<Point>();
			
		for(Point point : points) {
			Board tempBoard = new Board(b.width, b.height, b.genFen(), null);
			Piece tempPiece = tempBoard.getPiece(this.pos.x, this.pos.y);
			
			if(tempPiece != null) {
				tempBoard.handlePieceMove(tempPiece, point);
				
				King king = tempBoard.getKing(tempPiece.color);
				
				if(king != null) {
					if(!king.isInCheck(tempBoard)) {
						checkedPoints.add(point);
					}
				}
			}
		}
		
		return checkedPoints;
	}
	
	public ArrayList<Point> getCheckedLegalMovesOld(Board b) {
		ArrayList<Point> points = this.getLegalMoves(b);
		
		ArrayList<Point> checkedPoints = new ArrayList<Point>();
		
		for(Point point : points) {
			Point oldPos = this.getPos();
			
			this.setPos(point);
			
			if(!b.getKing(this.getColor()).isInCheck(b)) {
				checkedPoints.add(point);
			} else {
				System.out.println("Removed " + point.x + "," + point.y + " from " + this.getType().name() + " as it would result in check");
			}
			
			this.setPos(oldPos);
		}
		
		return checkedPoints;
	}
	
	
	public BufferedImage getImage() {
		return image;
	}
	
	public void setImage(BufferedImage image) {
		this.image = image;
	}
	
	public PieceType getType() {
		return type;
	}

	public void setType(PieceType type) {
		this.type = type;
	}

	public Point getPos() {
		return pos;
	}

	public void setPos(Point pos) {
		this.pos = pos;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public String getImageLocation() {
		return imageLocation;
	}

	public void setImageLocation(String imageLocation) {
		this.imageLocation = imageLocation;
	}
	
	public boolean isVisible() {
		return visible;
	}
	
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
}
